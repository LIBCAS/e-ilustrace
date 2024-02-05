package cz.inqool.eas.common.db;

import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.LocalDateType;
import org.hibernate.type.LongType;

import java.sql.Types;

/**
 * Custom Postgres dialect for version 9 and newer.
 * <p>
 * Provides additional configuration for hibernate to properly create SQL queries and helper methods.
 */
public class EasPostgreSQL95Dialect extends PostgreSQL95Dialect {
    public EasPostgreSQL95Dialect() {
        super();

        registerColumnType(Types.NCHAR,    "char(1)");
        registerColumnType(Types.NVARCHAR, "varchar($l)");
        registerColumnType(Types.NCLOB,    "text");
        registerColumnType(Types.CLOB,    "text");

        registerFunction("add_years",   new SQLFunctionTemplate(LocalDateType.INSTANCE, "?1 + interval '1' year * ?2"));
        registerFunction("add_months",  new SQLFunctionTemplate(LocalDateType.INSTANCE, "?1 + interval '1' month * ?2"));
        registerFunction("add_weeks",   new SQLFunctionTemplate(LocalDateType.INSTANCE, "?1 + interval '1' week * ?2"));
        registerFunction("add_days",    new SQLFunctionTemplate(LocalDateType.INSTANCE, "?1 + interval '1' day * ?2"));
        registerFunction("add_hours",   new SQLFunctionTemplate(LocalDateType.INSTANCE, "?1 + interval '1' hour * ?2"));
        registerFunction("add_minutes", new SQLFunctionTemplate(LocalDateType.INSTANCE, "?1 + interval '1' minute * ?2"));
        registerFunction("add_seconds", new SQLFunctionTemplate(LocalDateType.INSTANCE, "?1 + interval '1' second * ?2"));

        registerFunction("diff_years", new SQLFunctionTemplate(LongType.INSTANCE, "EXTRACT(YEAR FROM ?1) - EXTRACT(YEAR FROM ?2)"));

        registerKeyword("int");
    }
}
