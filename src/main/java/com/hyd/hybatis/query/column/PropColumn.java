package com.hyd.hybatis.query.column;

import com.hyd.hybatis.query.Getter;
import com.hyd.hybatis.utils.EntityUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PropColumn<T> extends AbstractColumn<PropColumn<T>> {

    public PropColumn(Getter<T, ?> getter) {
        this.alias = EntityUtil.resolveGetter(getter);
        setSqlCommand(this.alias);
    }
}
