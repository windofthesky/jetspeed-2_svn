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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.api.factory.CreationException;
import org.apache.cornerstone.framework.api.factory.IFactory;
import org.apache.cornerstone.framework.api.implementation.ImplementationException;
import org.apache.cornerstone.framework.api.persistence.factory.IPersistenceFactory;
import org.apache.cornerstone.framework.api.persistence.factory.IPersistentObjectFactory;
import org.apache.cornerstone.framework.api.persistence.factory.PersistenceException;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.context.BaseContext;
import org.apache.cornerstone.framework.init.Cornerstone;
import org.apache.cornerstone.framework.util.Util;
import org.apache.log4j.Logger;

/**
 * Requirements on subclasses:
 * - config
 *   - define "query.default" if to call createInstance()
 *   - define "query.<queryName>" if to call createInstance(ctx{queryName:'<queryName>',timeZone:'PST'})
 * - single object factory
 *   - config
 *     - getPrimaryKeyPropertyName()
 */
public abstract class BasePersistentObjectCollectionFactory extends BasePersistenceFactory
{
    public static final String REVISION = "$Revision$";

    public static final String PARAMETERS = "parameters";
    public static final String DEFAULT = "default";
    public static final String COLLECTION = "collection";
    public static final String ELEMENT = "element";

    public static final String CONFIG_ELEMENT_PARENT_NAME = ELEMENT + Constant.DOT + Constant.PARENT_NAME;
    public static final String CONFIG_COLLECTION_INSTANCE_CLASS_NAME = COLLECTION + Constant.DOT + Constant.INSTANCE_CLASS_NAME;
    public static final String CONFIG_COLLECTION_FACTORY_CLASS_NAME = COLLECTION + Constant.DOT + Constant.FACTORY_CLASS_NAME;
    public static final String CONFIG_COLLECTION_PARENT_NAME = COLLECTION + Constant.DOT + Constant.PARENT_NAME;

    public Object createInstance() throws CreationException
    {
        String defaultQueryNameConfigName = QUERY + "." + DEFAULT;
        String defaultQueryName = getConfigProperty(defaultQueryNameConfigName);
        if (defaultQueryName == null)
            throw new CreationException("config property '" + defaultQueryNameConfigName + "' missing");

        IContext context = new BaseContext();
        context.setValue(IPersistenceFactory.CTX_QUERY_NAME, defaultQueryName);
        return createInstance(context);
    }

    // expected of queryContext:
    // name="all" (required)
    // timeZoneCode="PST" (for example)

    public Object createInstance(Object queryContext) throws CreationException
    {
        try
        {
            return retrieveAndPopulate((IContext) queryContext);
        }
        catch (PersistenceException pe)
        {
            _Logger.error("persistenceException; rootCause:", pe.getCause());
            throw new CreationException(pe.getCause());
        }
    }

    public void delete(IContext context) throws PersistenceException
    {
        // TODO: this method is very similar to retrieveAndPopulate; consolidate!

        Connection conn = getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try
        {
            String query = getQuery(context);
            statement = conn.prepareStatement(query);

            String queryName = (String) context.getValue(IPersistenceFactory.CTX_QUERY_NAME);
            String queryConfigName = QUERY + "." + queryName;
            String queryParametersConfigName = queryConfigName + "." + PARAMETERS;
            String queryParametersString = getConfigProperty(queryParametersConfigName);
            if (queryParametersString != null)
            {
                String[] queryParameters = Util.convertStringsToArray(queryParametersString);
                for (int i = 1; i <= queryParameters.length; i++)
                {
                    String propertyName = queryParameters[i - 1];
                    Object propertyValue = context.getValue(propertyName);
                    statement.setObject(i, propertyValue);
                    if (_Logger.isDebugEnabled()) _Logger.debug("query param: " + propertyName + ":=" + propertyValue);
                }
            }
            statement.executeUpdate();
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

    public String mapColumnNameToPropertyName(String columnName)
    {
        return _elementFactory.mapColumnNameToPropertyName(columnName);
    }

    protected Object retrieveAndPopulate(IContext context) throws PersistenceException
    {
        Connection conn = getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try
        {
            String query = getQuery(context);
            statement = conn.prepareStatement(query);

            List parameterNameList = (List) context.getValue(QUERY_PARAMETER_NAME_LIST);
            if (parameterNameList != null)
            {
                for (int i = 0; i < parameterNameList.size(); i++)
                {
                    String parameterName = (String ) parameterNameList.get(i);
                    Object parameterValue = context.getValue(parameterName);
                    statement.setObject(i + 1, parameterValue);
                    if (_Logger.isDebugEnabled()) _Logger.debug("query param: " + parameterName + ":=" + parameterValue);
                }
            }
            else
            {
                String queryName = (String) context.getValue(IPersistenceFactory.CTX_QUERY_NAME);
                String queryConfigName = QUERY + "." + queryName;
                String queryParametersConfigName = queryConfigName + "." + PARAMETERS;
                String queryParametersString = getConfigProperty(queryParametersConfigName);
                if (queryParametersString != null)
                {
                    String[] queryParameters = Util.convertStringsToArray(queryParametersString);
                    for (int i = 1; i <= queryParameters.length; i++)
                    {
                        String propertyName = queryParameters[i - 1];
                        Object propertyValue = context.getValue(propertyName);
                        statement.setObject(i, propertyValue);
                        if (_Logger.isDebugEnabled()) _Logger.debug("query param: " + propertyName + ":=" + propertyValue);
                    }
                }
            }
            rs = statement.executeQuery();
            Object collection = createCollection();
            IPersistentObjectFactory elementFactory = getElementFactory();
            String primaryKeyPropertyName = elementFactory.getPrimaryKeyPropertyName();
            while (rs.next())
            {
                Object object = elementFactory.createInstance();
                populateProperties(object, rs);
                addElement(collection, primaryKeyPropertyName, object);
            }
            return collection;
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

    protected IPersistentObjectFactory getElementFactory() throws PersistenceException
    {
        if (_elementFactory == null)
        {
            String elementParentName = getConfigProperty(CONFIG_ELEMENT_PARENT_NAME);
            if (elementParentName == null || elementParentName.trim().length() == 0)
            	throw new PersistenceException("config '" + CONFIG_ELEMENT_PARENT_NAME + "' missing");
            try
			{
				_elementFactory = (IPersistentObjectFactory) Cornerstone.getImplementationManager().createImplementation(
					IFactory.class,
					elementParentName
				);
			}
			catch (ImplementationException ie)
			{
				throw new PersistenceException("failed to create element factory", ie.getCause());
			}
        }
        return _elementFactory;
    }

    protected Object createCollection() throws CreationException
    {
        try
        {
        	String configCollectionInstanceClassName = getConfigProperty(CONFIG_COLLECTION_INSTANCE_CLASS_NAME);
            if (configCollectionInstanceClassName != null)
            {
            	return Class.forName(configCollectionInstanceClassName).newInstance();
            }

            String configCollectionFactoryClassName = getConfigProperty(CONFIG_COLLECTION_FACTORY_CLASS_NAME);
            if (configCollectionFactoryClassName != null)
            {
            	IFactory factory = (IFactory) Class.forName(configCollectionFactoryClassName).newInstance();
                return factory.createInstance();
            }

            String configCollectionParentName = getConfigProperty(CONFIG_COLLECTION_PARENT_NAME);
            if (configCollectionParentName != null)
            {
                int slash = configCollectionParentName.indexOf(Constant.SLASH);
                if (slash < 0)
                    throw new CreationException("expected format of '" + CONFIG_COLLECTION_PARENT_NAME + "' is <interfaceName>/<variantName>");
                String interfaceName = configCollectionParentName.substring(0, slash);
                String variantName = configCollectionParentName.substring(slash + 1);
            	return Cornerstone.getImplementationManager().createImplementation(interfaceName, variantName);
            }

            throw new CreationException(
            	"collection type definition missing; use '" + CONFIG_COLLECTION_INSTANCE_CLASS_NAME + "' or '" +
            	CONFIG_COLLECTION_FACTORY_CLASS_NAME + "' or '" + CONFIG_COLLECTION_PARENT_NAME + "'"
            );
        }
        catch (Exception e)
        {
        	throw new CreationException(e);
        }
    }

    protected abstract void addElement(Object collection, String primaryKeyPropertyName, Object element);

    private static Logger _Logger = Logger.getLogger(BasePersistentObjectCollectionFactory.class);
    protected IPersistentObjectFactory _elementFactory;
}