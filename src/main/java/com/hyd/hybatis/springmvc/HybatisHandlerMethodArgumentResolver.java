package com.hyd.hybatis.springmvc;

import com.hyd.hybatis.Condition;
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
import java.util.stream.Collectors;

import static com.hyd.hybatis.reflection.Reflections.getGenericTypeArg;
import static com.hyd.hybatis.utils.Bean.convertValue;

@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
public class HybatisHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static class Param {

        private String key;

        private String column;

        private String field;

        private String condition;

        private String[] values;

        private List<String> parsedValues;
    }

    private final HybatisConfiguration config;

    public HybatisHandlerMethodArgumentResolver(HybatisConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (Conditions.class.isAssignableFrom(parameter.getParameterType())) {
            return true;
        }
        Class<?> parameterType = parameter.getParameterType();
        return !parameterType.isPrimitive() && Reflections.isPojoClassQueryable(parameterType);
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory
    ) {
        Class<?> parameterType = parameter.getParameterType();
        if (parameterType == Conditions.class) {
            return buildConditionsObject(webRequest);
        } else {
            return buildJavaBeanObject(parameterType, webRequest);
        }
    }

    private <T> T buildJavaBeanObject(Class<T> parameterType, NativeWebRequest webRequest) {

        var conditionFields = Reflections.getPojoFieldsOfType(
            parameterType, Condition.class, config.getHideBeanFieldsFrom()
        );
        var conditionFieldsMap = new HashMap<String, Field>();
        for (var field : conditionFields) {
            conditionFieldsMap.put(field.getName(), field);
        }

        var t = BeanUtils.instantiateClass(parameterType);
        webRequest.getParameterMap().forEach((key, values) -> {
            Param param = parseParamValues(key, values);
            if (param == null) {
                return;
            }
            if (conditionFieldsMap.containsKey(param.field)) {
                var field = conditionFieldsMap.get(param.field);
                param.column = Reflections.getColumnName(field);

                Condition condition = Reflections.getFieldValue(t, field);
                if (condition == null) {
                    condition = new Condition();
                    Reflections.setFieldValue(t, field, condition);
                }
                injectCondition(param, condition, getGenericTypeArg(field.getGenericType()));
            }
        });
        return t;
    }

    private Conditions buildConditionsObject(NativeWebRequest webRequest) {
        Conditions conditions = new Conditions();
        webRequest.getParameterMap().forEach((key, values) -> {
            Param param = parseParamValues(key, values);
            if (param == null) {
                return;
            }

            Condition c = conditions.with(param.column);
            injectCondition(param, c, String.class);
        });

        var limit = webRequest.getParameter("limit");
        if (limit != null) {
            conditions.limit(Integer.parseInt(limit));
        }

        return conditions;
    }

    private <T> void injectCondition(Param param, Condition<T> c, Class<T> type) {
        if (param.condition.equals("in")) {
            List<T> list = param.parsedValues.stream()
                .map(v -> convertValue(v, type))
                .collect(Collectors.toList());
            c.in(list);
        } else if (param.condition.equals("between") && param.parsedValues.size() == 2) {
            c.between(
                convertValue(param.parsedValues.get(0), type),
                convertValue(param.parsedValues.get(1), type)
            );
        } else if (param.condition.equals("startsWith")) {
            c.startsWith(param.values[0]);
        } else if (param.condition.equals("endsWith")) {
            c.endsWith(param.values[0]);
        } else if (param.condition.equals("contains")) {
            c.contains(param.values[0]);
        } else if (param.condition.equals("eq")) {
            c.eq(convertValue(param.values[0], type));
        } else if (param.condition.equals("ne")) {
            c.ne(convertValue(param.values[0], type));
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
        var column = Str.camel2Underline(field);
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
}
