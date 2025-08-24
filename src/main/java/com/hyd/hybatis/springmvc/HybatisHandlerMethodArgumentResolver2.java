package com.hyd.hybatis.springmvc;

import com.hyd.hybatis.Condition;
import com.hyd.hybatis.ConditionOperator;
import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.reflection.Reflections;
import com.hyd.hybatis.utils.Str;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 将 WebRequest 中的查询参数封装成 JavaBean 对象
 */
@Slf4j
public class HybatisHandlerMethodArgumentResolver2 implements HandlerMethodArgumentResolver {

    public HybatisHandlerMethodArgumentResolver2(HybatisConfiguration config) {
        this.config = config;
    }

    private static class Param {

        private String column;

        private String field;

        private String operator;

        private String[] values;

    }

    private final HybatisConfiguration config;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (Conditions.class.isAssignableFrom(parameter.getParameterType())) {
            return true;
        }
        Class<?> parameterType = parameter.getParameterType();
        return !parameterType.isPrimitive() && Reflections.isPojoClassQueryable(parameterType);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Class<?> parameterType = parameter.getParameterType();
        if (parameterType == Conditions.class) {
            return buildConditionsObject(webRequest);
        } else {
            return buildJavaBeanObject(parameterType, webRequest);
        }
    }

    /**
     * 将参数封装成 Conditions 对象
     */
    private Object buildConditionsObject(NativeWebRequest webRequest) {

        Conditions conditions = new Conditions();
        webRequest.getParameterMap().forEach((key, values) -> {
            Param param = parseParamValues(key, values);
            if (param == null) {
                return;
            }

            Condition c = new Condition();
            c.setColumn(param.column);
            c.setOperator(ConditionOperator.of(param.operator, param.values));
        });

        //-------------------------- limit, offset 和 projection 是不针对字段的 --------------------------

        var limit = webRequest.getParameter("limit");
        if (limit != null) {
            conditions.setLimit(Integer.parseInt(limit));
        }

        var offset = webRequest.getParameter("offset");
        if (offset != null) {
            conditions.setOffset(Integer.parseInt(offset));
        }

        var projection = webRequest.getParameter("projection");
        if (projection != null) {
            conditions.setProjection(parseProjection(projection));
        }

        return conditions;
    }

    /**
     * 将参数封装成 JavaBean 对象
     * 当查询参数被封装成 JavaBean 对象时，Hybatis 要求其属性类型必须是 Condition
     * 本方法将 webRequest 中的查询条件转换为 Condition 对象，然后设置到新创建的 Bean 对象中
     */
    private Object buildJavaBeanObject(Class<?> parameterType, NativeWebRequest webRequest) {

        var conditionFields = Reflections.getPojoFieldsOfType(
            parameterType, Condition.class, config.getHideBeanFieldsFrom()
        );
        var conditionFieldsMap = new HashMap<String, Field>();
        for (var field : conditionFields) {
            conditionFieldsMap.put(field.getName(), field);
        }

        // 创建 Bean 对象，然后解析 webRequest
        var t = BeanUtils.instantiateClass(parameterType);
        var camelToUnderline = config.isCamelToUnderline();
        webRequest.getParameterMap().forEach((key, values) -> {
            Param param = parseParamValues(key, values);
            if (param == null) {
                return;
            }
            if (conditionFieldsMap.containsKey(param.field)) {
                var field = conditionFieldsMap.get(param.field);
                param.column = Reflections.getColumnName(field, camelToUnderline);

                Condition condition = new Condition();
                condition.setOperator(ConditionOperator.of(param.operator, param.values));
                condition.setColumn(param.column);
                condition.setValues(List.of(param.values));
                Reflections.setFieldValue(t, field, condition);
            }
        });
        return t;
    }

    static class KeyPattern {

        private final Pattern pattern;

        // 将不同的 key 格式替换成统一的 "column.operator" 格式
        private final Function<String, String> normalizer;

        KeyPattern(String pattern, Function<String, String> normalizer) {
            this.pattern = Pattern.compile(pattern);
            this.normalizer = normalizer;
        }

        boolean matches(String key) {
            return pattern.matcher(key).matches();
        }

        String normalize(String key) {
            return normalizer.apply(key);
        }
    }

    // 这里允许三种表达方式："column.operator"，"column[operator]" 和 "column$operator"。
    // 之所以允许三种表达方式，是为了兼容不同框架下的前端在构造查询参数时可能存在的限制。
    private static final KeyPattern[] VALID_PARAM_PATTERNS = new KeyPattern[]{
        new KeyPattern("^([a-zA-Z0-9_]+)\\.([a-zA-Z0-9_]+)$", key -> key),
        new KeyPattern("^([a-zA-Z0-9_]+)\\[([a-zA-Z0-9_]+)]$", key -> key.replace("[", ".").replace("]", "")),
        new KeyPattern("^([a-zA-Z0-9_]+)\\$([a-zA-Z0-9_]+)$", key -> key.replace("$", "."))
    };

    private Param parseParamValues(String key, String[] values) {

        var keyPattern = Stream.of(VALID_PARAM_PATTERNS)
            .filter(p -> p.matches(key))
            .findFirst()
            .orElse(null);

        // 不符合三种表达方式，说明这个参数不是查询条件，因此没有 Param 对象可返回
        if (keyPattern == null) {
            return null;
        }

        // 这里用 "+" 是为了兼容可能出现多个连续 "." 的情况，提升健壮性
        var finalKey = keyPattern.normalize(key);
        var split = finalKey.split("\\.+");
        var field = split[0];
        var column = config.isCamelToUnderline() ? Str.camel2Underline(field) : field;
        var operator = split[1];

        var valuesList = new ArrayList<String>();
        for (String v : values) {
            if (v.contains(",")) {
                valuesList.addAll(Arrays.asList(v.split(",")));
            } else {
                valuesList.add(v);
            }
        }

        if (config.isIgnoreEmptyString()) {
            valuesList.removeIf(String::isBlank);
        }

        if (valuesList.isEmpty()) {
            return null;
        }

        Param param = new Param();
        param.column = column;
        param.field = field;
        param.operator = operator;
        param.values = values;
        return param;
    }

    private List<String> parseProjection(String projection) {
        var stream = Stream.of(projection.split(",")).filter(Str::isNotBlank);
        if (config.isCamelToUnderline()) {
            stream = stream.map(Str::camel2Underline);
        }
        return stream.collect(Collectors.toList());
    }
}
