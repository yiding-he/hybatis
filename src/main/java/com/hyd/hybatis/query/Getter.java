package com.hyd.hybatis.query;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface Getter<C, P> extends Function<C, P>, Serializable {

}
