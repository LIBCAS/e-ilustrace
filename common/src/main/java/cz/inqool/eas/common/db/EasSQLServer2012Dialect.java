package cz.inqool.eas.common.db;

import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.type.StandardBasicTypes;

/**
 * Custom MSSQL Server dialect for version 2012 and newer.
 * <p>
 * Provides additional configuration for hibernate to properly create SQL queries and
 * helper methods.
 */
public class EasSQLServer2012Dialect extends SQLServer2012Dialect {
    public EasSQLServer2012Dialect() {
        registerFunction( "random", new NoArgSQLFunction("NEWID", StandardBasicTypes.UUID_CHAR) );
    }
}
