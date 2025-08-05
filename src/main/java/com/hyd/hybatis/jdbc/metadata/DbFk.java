package com.hyd.hybatis.jdbc.metadata;

import lombok.Data;

@Data
public class DbFk {

    private DbTable table;

    private DbColumn column;
}
