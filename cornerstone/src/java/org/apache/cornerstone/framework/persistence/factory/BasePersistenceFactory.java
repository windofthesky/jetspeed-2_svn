/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
		_connectionManager = (IConnectionManager) Cornerstone.getSingletonManager().getSingleton(connectionManagerClassName);

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