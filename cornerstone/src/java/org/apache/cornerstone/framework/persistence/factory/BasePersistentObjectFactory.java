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

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import org.apache.cornerstone.framework.api.factory.CreationException;
import org.apache.cornerstone.framework.api.persistence.factory.IPersistentObjectFactory;
import org.apache.cornerstone.framework.api.persistence.factory.PersistenceException;
import org.apache.cornerstone.framework.bean.helper.BeanHelper;
import org.apache.log4j.Logger;

public abstract class BasePersistentObjectFactory extends BasePersistenceFactory implements IPersistentObjectFactory
{
    public static final String REVISION = "$Revision$";

    public static final String QUERY_RETRIEVE_BY_ID = "retrieveById";
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
     * @see com.cisco.salesit.framework.common.core.IFactory#createInstance()
     */
    public abstract Object createInstance() throws CreationException;

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
            throw new CreationException(pe);
        }
    }

    /**
     * @throws PersistenceException
     */
    protected BasePersistentObjectFactory() throws PersistenceException
    {
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
        String queryConfigName = QUERY + "." + QUERY_RETRIEVE_BY_ID;
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
                populate(object, rs);
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

    private static Logger _Logger = Logger.getLogger(BasePersistentObjectFactory.class);
}