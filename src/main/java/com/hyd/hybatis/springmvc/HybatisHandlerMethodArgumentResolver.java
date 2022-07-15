package com.hyd.hybatis.springmvc;

import com.hyd.hybatis.Conditions;
import com.hyd.hybatis.HybatisConfiguration;
import com.hyd.hybatis.annotations.HbArgument;
import com.hyd.hybatis.utils.Str;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class HybatisHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final HybatisConfiguration hybatisConfiguration;

    public HybatisHandlerMethodArgumentResolver(HybatisConfiguration hybatisConfiguration) {
        this.hybatisConfiguration = hybatisConfiguration;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.getParameterType() != Conditions.class) {
            return false;
        }
        if (!parameter.hasParameterAnnotation(HbArgument.class)) {
            log.warn("No @HbArgument annotation found on parameter {}", parameter);
            return false;
        }
        return true;
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory
    ) {
        Conditions conditions = new Conditions();
        webRequest.getParameterMap().forEach((key, values) -> {
            if (key.contains("[")) {
                key = key.replace("[", ".").replace("]", "");
            }
            if (key.contains("$")) {
                key = key.replace("$", ".");
            }
            if (!key.contains(".")) {
                return;
            }
            var split = key.split("\\.");
            var column = Str.camel2Underline(split[0]);
            var condition = split[1];

            var valuesList = new ArrayList<String>();
            for (String v : values) {
                if (v.contains(",")) {
                    valuesList.addAll(Arrays.asList(v.split(",")));
                } else {
                    valuesList.add(v);
                }
            }
            valuesList.removeIf(String::isBlank);

            var c = conditions.with(column);
            if (condition.equals("in")) {
                List<Object> l = new ArrayList<>();
                l.addAll(valuesList);
                c.in(l);
            } else if (condition.equals("between") && valuesList.size() == 2) {
                c.between(valuesList.get(0), valuesList.get(1));
            } else if (condition.equals("startsWith")) {
                c.startsWith(values[0]);
            } else if (condition.equals("endsWith")) {
                c.endsWith(values[0]);
            } else if (condition.equals("contains")) {
                c.contains(values[0]);
            } else if (condition.equals("eq")) {
                c.eq(values[0]);
            } else if (condition.equals("ne")) {
                c.ne(values[0]);
            } else if (condition.equals("null")) {
                if (values[0].equals("true")) {
                    c.beNull();
                } else {
                    c.nonNull();
                }
            } else if (condition.equals("lt")) {
                c.lt(values[0]);
            } else if (condition.equals("lte")) {
                c.lte(values[0]);
            } else if (condition.equals("gt")) {
                c.gt(values[0]);
            } else if (condition.equals("gte")) {
                c.gte(values[0]);
            }
        });
        return conditions;
    }
}
