package com.hyd.hybatis.springmvc;

import com.hyd.hybatis.Condition2;
import com.hyd.hybatis.ConditionOperator;
import com.hyd.hybatis.Conditions2;
import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.reflection.Reflections;
import com.hyd.hybatis.utils.Str;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hyd.hybatis.reflection.Reflections.getGenericTypeArg;
import static com.hyd.hybatis.sql.SqlHelper.injectCondition;
import static com.hyd.hybatis.utils.Bean.convertValue;

public class HybatisHandlerMethodArgumentResolver2 implements HandlerMethodArgumentResolver {

    public HybatisHandlerMethodArgumentResolver2(HybatisConfiguration config) {
        this.config = config;
    }

    private static class Param {

        private String key;

        private String column;

        private String field;

        private String condition;

        private String[] values;

        private List<String> parsedValues;
    }

    private final HybatisConfiguration config;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (Conditions2.class.isAssignableFrom(parameter.getParameterType())) {
            return true;
        }
        Class<?> parameterType = parameter.getParameterType();
        return !parameterType.isPrimitive() && Reflections.isPojoClassQueryable(parameterType);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Class<?> parameterType = parameter.getParameterType();
        if (parameterType == Conditions2.class) {
            return buildConditionsObject(webRequest);
        } else {
            return buildJavaBeanObject(parameterType, webRequest);
        }
    }

    private Object buildConditionsObject(NativeWebRequest webRequest) {
        return null;
    }

    /**
     * 当查询参数被封装成 Bean 对象时，Hybatis 要求其属性类型必须是 Condition
     * 本方法将 webRequest 中的查询条件转换为 Condition 对象，然后设置到新创建的 Bean 对象中
     */
    private Object buildJavaBeanObject(Class<?> parameterType, NativeWebRequest webRequest) {

        var conditionFields = Reflections.getPojoFieldsOfType(
            parameterType, Condition2.class, config.getHideBeanFieldsFrom()
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

                Condition2 condition = new Condition2();
                Reflections.setFieldValue(t, field, condition);
                 injectCondition(param, condition, getGenericTypeArg(field.getGenericType()));
            }
        });
        return t;
    }


    private <T> void injectCondition(Param param, Condition2 c, Class<T> type) {

        c.setOperator(ConditionOperator.of(param.condition));

        if (param.condition.equals("in")) {
            List<Object> list = param.parsedValues.stream()
                .map(v -> convertValue(v, type))
                .collect(Collectors.toList());
            c.setValues(list);
        } else if (param.condition.equals("nin")) {
            List<Object> list = param.parsedValues.stream()
                .map(v -> convertValue(v, type))
                .collect(Collectors.toList());
            c.setValues(list);
        } else if (param.condition.equals("between") && param.parsedValues.size() == 2) {
            c.setValues(List.of(
                convertValue(param.parsedValues.get(0), type),
                convertValue(param.parsedValues.get(1), type)
            ));
        } else if (param.condition.equals("startsWith")) {
            c.setValue(param.values[0]);
        } else if (param.condition.equals("endsWith")) {
            c.setValue(param.values[0]);
        } else if (param.condition.equals("contains")) {
            c.setValue(param.values[0]);
        } else if (param.condition.equals("eq")) {
            c.setValue(convertValue(param.values[0], type));
        } else if (param.condition.equals("ne")) {
            c.setValue(convertValue(param.values[0], type));
        } else if (param.condition.equals("null")) {
            if (param.values[0].equals("true")) {
                c.beNull();
            } else {
                c.nonNull();
            }
        } else if (param.condition.equals("lt")) {
            c.lt(convertValue(param.values[0], type));
        } else if (param.condition.equals("lte")) {
            c.lte(convertValue(param.values[0], type));
        } else if (param.condition.equals("gt")) {
            c.gt(convertValue(param.values[0], type));
        } else if (param.condition.equals("gte")) {
            c.gte(convertValue(param.values[0], type));
        } else if (param.condition.equals("orderAsc")) {
            c.setOrderAsc(Integer.parseInt(param.values[0]));
        } else if (param.condition.equals("orderDesc")) {
            c.setOrderDesc(Integer.parseInt(param.values[0]));
        }
    }

    private Param parseParamValues(String key, String[] values) {
        if (key.contains("[")) {
            key = key.replace("[", ".").replace("]", "");
        }
        if (key.contains("$")) {
            key = key.replace("$", ".");
        }
        if (!key.contains(".")) {
            return null;
        }
        var split = key.split("\\.");
        var field = split[0];
        var column = config.isCamelToUnderline() ? Str.camel2Underline(field) : field;
        var condition = split[1];

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
        param.key = key;
        param.column = column;
        param.field = field;
        param.condition = condition;
        param.values = values;
        param.parsedValues = valuesList;
        return param;
    }

    private Set<String> parseProjection(String projection) {
        var stream = Stream.of(projection.split(",")).filter(Str::isNotBlank);
        if (config.isCamelToUnderline()) {
            stream = stream.map(Str::camel2Underline);
        }
        return stream.collect(Collectors.toSet());
    }
}
