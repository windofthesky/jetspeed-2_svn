package org.apache.cornerstone.framework.api.persistence.datasource;

public interface IDataSource
{
    public static final String REVISION = "$Revision$";
    
    public String getDriverClassName();
    public String getConnectionUrl();
    public String getConnectionUserName();
    public String getConnectionPassword();
}