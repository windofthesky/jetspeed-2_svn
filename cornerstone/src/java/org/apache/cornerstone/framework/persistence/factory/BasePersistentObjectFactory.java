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

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Properties;
import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.api.factory.CreationException;
import org.apache.cornerstone.framework.api.factory.IFactory;
import org.apache.cornerstone.framework.api.implementation.IImplementationManager;
import org.apache.cornerstone.framework.api.implementation.ImplementationException;
import org.apache.cornerstone.framework.api.persistence.factory.IPersistentObjectFactory;
import org.apache.cornerstone.framework.api.persistence.factory.PersistenceException;
import org.apache.cornerstone.framework.bean.helper.BeanHelper;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.context.BaseContext;
import org.apache.cornerstone.framework.init.Cornerstone;
import org.apache.cornerstone.framework.util.Util;
import org.apache.log4j.Logger;

public class BasePersistentObjectFactory extends BasePersistenceFactory implements IPersistentObjectFactory
{
    public static final String REVISION = "$Revision$";

    public static final String ASSOCIATION = "association";
    public static final String FACTORY_PARENT_NAME = Constant.FACTORY + Constant.DOT + Constant.PARENT_NAME;
    public static final String PARAMETER = "parameter";
    public static final String DOT_PARAMETER = Constant.DOT + PARAMETER;

    public static final String QUERY_BY_ID = "byId";
    public static final String QUERY_INSERT = "insert";
    public static final String QUERY_UPDATE = "update";
    public static final String QUERY_DELETE = "delete";

    public String getPrimaryKeyPropertyName()
    {
        return getConfigProperty(PRIMARY_KEY_PROPERTY_NAME);
    }

    public String getPrimaryKeyColumnName()
    {
        return getConfigProperty(PRIMARY_KEY_COLUMN_NAME);
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.common.persistence.IPersistentObjectFactory#store(java.lang.Object)
     */
    public void store(Object object) throws PersistenceException
    {
        // check whether primary key is already populated
        String primaryKeyPropertyName = getConfigProperty(PRIMARY_KEY_PROPERTY_NAME);
        if (primaryKeyPropertyName == null)
            throw new PersistenceException("config property '" + PRIMARY_KEY_PROPERTY_NAME + "' undefined");
        Object primaryKeyValue = BeanHelper.getSingleton().getProperty(object, primaryKeyPropertyName);
        if (primaryKeyValue == null)
            doInsertOrUpdate(true, object, QUERY_INSERT, primaryKeyPropertyName);
        else
            doInsertOrUpdate(false, object, QUERY_UPDATE, primaryKeyPropertyName);
        if (_Logger.isDebugEnabled()) _Logger.debug("object stored: " + object);
    }

    /* (non-Javadoc)
     * @see com.cisco.salesit.framework.common.persistence.IPersistentObjectFactory#delete(java.lang.Object)
     */
    public void delete(Object object) throws PersistenceException
    {
        if (object == null) return;

        String primaryKeyPropertyName = getConfigProperty(PRIMARY_KEY_PROPERTY_NAME);
        if (primaryKeyPropertyName == null)
            throw new PersistenceException("config property '" + PRIMARY_KEY_PROPERTY_NAME + "' undefined");
        Object primaryKey = BeanHelper.getSingleton().getProperty(object, primaryKeyPropertyName);
        if (primaryKey != null)
            doDelete(primaryKey);
    }

    public Object createInstance(Object id) throws CreationException
    {
        // then retrieve and populate new instance
        try
        {
            Object newInstance = retrieveAndPopulate(id);
            return newInstance;
        }
        catch (PersistenceException pe)
        {
            throw new CreationException(pe.getCause());
        }
    }

    protected void doInsertOrUpdate(boolean isInsert, Object object, String queryName, String primaryKeyPropertyName) throws PersistenceException
    {
        String queryConfigName = QUERY + "." + queryName;
        String query = getConfigProperty(queryConfigName);
        if (query == null)
            throw new PersistenceException("config property '" + queryConfigName + "' not found");

        Connection conn = getConnection();
        PreparedStatement statement = null;
        try
        {
            statement = conn.prepareStatement(query);
            List keyList = getPropertyNameToColumnNameMap().getKeyList();
            for (int i = 0, varIndex = 1; i < keyList.size(); i++)
            {
                String propertyName = (String) keyList.get(i);
                if (propertyName.equals(primaryKeyPropertyName)) continue;    // skip primary key, whose value should come from a sequence, when inserting

                // String columnName = _propertyNameToColumnNameMap.getProperty(propertyName);
                Object value = BeanHelper.getSingleton().getProperty(object, propertyName);
                if (value == null)
                {
                    PropertyDescriptor desc = BeanHelper.getSingleton().getPropertyDescriptor(object, propertyName);
                    Class propertyType = desc.getPropertyType();

                    int sqlType = Types.VARCHAR;
                    if (propertyType == Timestamp.class)
                        sqlType = Types.TIMESTAMP;
                    else if (propertyType == BigDecimal.class)
                        sqlType = Types.DECIMAL;

                    statement.setNull(varIndex++, sqlType);
                    _Logger.debug(propertyName + ":=null");
                }
                else
                {
                    statement.setObject(varIndex++, value);
                    _Logger.debug(propertyName + ":=" + value);
                }
            }

            // update
            if (!isInsert)
            {
                String keyName = getPrimaryKeyPropertyName();
                Object keyValue = BeanHelper.getSingleton().getProperty(object, keyName);
                statement.setObject(keyList.size(), keyValue);
                _Logger.debug("key:" + keyName + ":=" + keyValue);
            }

            int count = statement.executeUpdate();
        }
        catch(Exception e)
        {
            _Logger.error("failed to store", e);
            throw new PersistenceException("failed to store", e);
        }
        finally
        {
            try
            {
                if (conn != null) conn.close();
            }
            catch (SQLException se)
            {
                _Logger.error(se);
            }
        }
    }

    protected Object retrieveAndPopulate(Object id) throws PersistenceException
    {
        String queryConfigName = QUERY + "." + QUERY_BY_ID;
        String query = getConfigProperty(queryConfigName);
        if (query == null)
            throw new PersistenceException("config property '" + queryConfigName + "' not found");

        Connection conn = getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try
        {
            statement = conn.prepareStatement(query);
            statement.setObject(1, id);
            rs = statement.executeQuery();
            if (rs.next())
            {
                Object object = createInstance();
                populateProperties(object, rs);
                populateAssociations();
                return object;
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            throw new PersistenceException(e);
        }
        finally
        {
            try
            {
                if (rs != null) rs.close();
                if (statement != null) statement.close();
                if (conn != null) conn.close();
            }
            catch (SQLException se)
            {
                _Logger.error("failed closing connection", se);
            }
        }
    }

    protected void doDelete(Object primaryKey) throws PersistenceException
    {
        String queryConfigName = QUERY + "." + QUERY_DELETE;
        String query = getConfigProperty(queryConfigName);
        if (query == null)
            throw new PersistenceException("config property '" + queryConfigName + "' not found");

        Connection conn = getConnection();
        PreparedStatement statement = null;
        try
        {
            statement = conn.prepareStatement(query);
            statement.setObject(1, primaryKey);
            int count = statement.executeUpdate();
        }
        catch(Exception e)
        {
            _Logger.error("failed to delete", e);
            throw new PersistenceException("failed to delete", e);
        }
        finally
        {
            try
            {
                if (conn != null) conn.close();
            }
            catch (SQLException se)
            {
                _Logger.error(se);
            }
        }        
    }

    protected void populateAssociations()
    {
    	
    }

    protected Object createAssociation(
        Object product,
        String associationName,
        String instanceSpecName,
        String instanceSpecValue
    )
        throws CreationException
    {
        if (FACTORY_PARENT_NAME.equals(instanceSpecName))
        {
            String paramPrefix = CONFIG_PRODUCT_PROPERTY_DOT + associationName + Constant.DOT + Constant.FACTORY + DOT_PARAMETER + Constant.DOT;
            Properties parameterProperties = Util.getPropertiesOfPrefix(getConfig(), paramPrefix);
            Object propertyValue = createInstanceByFactoryParentName(instanceSpecValue, parameterProperties);
            return propertyValue;
        }
        else
        {
            throw new CreationException(
                "instanceSpecName '" + instanceSpecName + "' of association '" +
                associationName + "' not understood;" +
                "allowed: '" + FACTORY_PARENT_NAME +
                "'"
            );
        }
    }

    protected Object createInstanceByFactoryParentName(String factoryParentName, Properties parameterProperties) throws CreationException
    {
        try
        {
        	IImplementationManager implementationManager = (IImplementationManager) Cornerstone.getImplementation(IImplementationManager.class);
            IFactory factory = (IFactory) implementationManager.createImplementation(IFactory.class, factoryParentName);
            if (parameterProperties == null || parameterProperties.size() == 0)
            {    
                return factory.createInstance();
            }
            else
            {
                IContext context = new BaseContext();
                Util.addPropertiesToContext(parameterProperties, context);
                return factory.createInstance(context);
            }
        }
        catch (ImplementationException ie)
        {
            throw new CreationException(ie.getCause());
        }
    }

    private static Logger _Logger = Logger.getLogger(BasePersistentObjectFactory.class);
}