/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.cornerstone.framework.persistence.factory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.api.persistence.connection.ConnectionException;
import org.apache.cornerstone.framework.api.persistence.connection.IConnectionManager;
import org.apache.cornerstone.framework.api.persistence.factory.IPersistenceFactory;
import org.apache.cornerstone.framework.api.persistence.factory.PersistenceException;
import org.apache.cornerstone.framework.api.singleton.ISingletonManager;
import org.apache.cornerstone.framework.bean.helper.BeanHelper;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.factory.ioc.InversionOfControlFactory;
import org.apache.cornerstone.framework.init.Cornerstone;
import org.apache.cornerstone.framework.util.OrderedProperties;
import org.apache.log4j.Logger;

public abstract class BasePersistenceFactory extends InversionOfControlFactory implements IPersistenceFactory
{
    public static final String REVISION = "$Revision$";

    public static final String CONFIG_CONNECTION_MANAGER_INSTANCE_CLASS_NAME = "connectionManager." + Constant.INSTANCE_CLASS_NAME;
    public static final String CONFIG_DATA_SOURCE_NAME = "dataSource.name";
    public static final String CONFIG_DB_COLUMN_TO_PROPERTY_MAP = "db.columnToPropertyMap";

    public static final String QUERY = "query";
    public static final String SEGMENT = "segment";

    public static final String QUERY_SEGMENT_NAME_LIST = BasePersistenceFactory.class.getName() + ".querySegmentNameList";
    public static final String QUERY_PARAMETER_NAME_LIST = BasePersistenceFactory.class.getName() + ".queryParameterNameList";
    public static final String QUERY_SEGMENT_NAME_AND = "and";
    
	protected void postInit()
	{
		String connectionManagerClassName = getConfigProperty(CONFIG_CONNECTION_MANAGER_INSTANCE_CLASS_NAME);
		if (connectionManagerClassName == null)
		{
			Exception e = new PersistenceException("config property '" + CONFIG_CONNECTION_MANAGER_INSTANCE_CLASS_NAME + "' missing");
			_Logger.error("", e);
		}
		ISingletonManager singletonManager = (ISingletonManager) Cornerstone.getImplementation(ISingletonManager.class);
		_connectionManager = (IConnectionManager) singletonManager.getSingleton(connectionManagerClassName);

		_dataSourceName = getConfigProperty(CONFIG_DATA_SOURCE_NAME);
		if (_dataSourceName == null)
		{
			Exception e = new PersistenceException("config property '" + CONFIG_DATA_SOURCE_NAME + "' missing");
			_Logger.error("", e);
		}
	}

	public void overwriteConfig(Properties overwrites)
	{
		super.overwriteConfig(overwrites);
		postInit();
	}

    public String mapColumnNameToPropertyName(String columnName)
    {
        String columnNameConfigPropertyName = CONFIG_DB_COLUMN_TO_PROPERTY_MAP + "." + columnName.toLowerCase();
        return getConfigPropertyWithDefault(columnNameConfigPropertyName, columnName);
    }

    public String mapPropertyNameToColumnName(String propertyName)
    {
        return getPropertyNameToColumnNameMap().getProperty(propertyName);
    }
        
    public OrderedProperties getPropertyNameToColumnNameMap()
    {
        if (_propertyNameToColumnNameMap == null)
        {
            _propertyNameToColumnNameMap = new OrderedProperties();
            List keyList = _config.getKeyList();
            for (int i = 0; i < keyList.size(); i++)
            {
                String configPropertyName = (String) keyList.get(i);
                if (configPropertyName.startsWith(CONFIG_DB_COLUMN_TO_PROPERTY_MAP))
                {
                    String columnName = configPropertyName.substring(CONFIG_DB_COLUMN_TO_PROPERTY_MAP.length() + 1);
                    String beanPropertyName = _config.getProperty(configPropertyName);
                    _propertyNameToColumnNameMap.setProperty(beanPropertyName, columnName);
                }
            }
        }
        return _propertyNameToColumnNameMap;
    }

    protected BasePersistenceFactory()
    {
    	init();
    }

    protected Connection getConnection() throws PersistenceException
    {
        try
        {
            Connection connection = _connectionManager.getConnection(_dataSourceName);
            return connection;
        }
        catch (ConnectionException ce)
        {
            throw new PersistenceException(ce.getCause());
        }
    }
    
    protected void populateProperties(Object object, ResultSet rs) throws SQLException
    {
        ResultSetMetaData rsmd = rs.getMetaData();

        for (int i = 1; i <= rsmd.getColumnCount(); i++)
        {
            String columnName = rsmd.getColumnName(i);
            String propertyName = mapColumnNameToPropertyName(columnName);
            Object propertyValue = rs.getObject(i);
            BeanHelper.getSingleton().setProperty(object, propertyName, propertyValue);
        }
    }

    protected String getQuery(IContext queryContext) throws PersistenceException
    {
        String queryName = (String) queryContext.getValue(IPersistenceFactory.CTX_QUERY_NAME);
        if (queryName == null)
            throw new PersistenceException("'" + IPersistenceFactory.CTX_QUERY_NAME + "' missing from context");

        String queryConfigName = QUERY + "." + queryName;
        String query = getConfigProperty(queryConfigName);
        if (query == null)
            throw new PersistenceException("config property '" + queryConfigName + "' not found");

        List segmentList = (List) queryContext.getValue(QUERY_SEGMENT_NAME_LIST);
        if (segmentList != null)
        {
            // ASSUMPTION: all segments are after where
            StringBuffer queryBuffer = new StringBuffer(query);
            for (int i = 0; i < segmentList.size(); i++)
            {
                String segmentName = (String) segmentList.get(i);
                String segmentConfigName = queryConfigName + "." + SEGMENT + "." + segmentName;
                String segment = getConfigProperty(segmentConfigName);
                if (segment == null)
                    throw new PersistenceException("config property '" + segmentConfigName + "' not found");
                queryBuffer.append(' ');
                queryBuffer.append(segment);
            }
            query = queryBuffer.toString();
        }

        if (_Logger.isDebugEnabled()) _Logger.debug("query: " + query);
        return query;
    }

    private static Logger _Logger = Logger.getLogger(BasePersistenceFactory.class);
    protected IConnectionManager _connectionManager;
    protected String _dataSourceName;
    protected OrderedProperties _propertyNameToColumnNameMap;
}