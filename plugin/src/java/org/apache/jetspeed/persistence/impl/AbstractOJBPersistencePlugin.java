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
package org.apache.jetspeed.persistence.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.InitializationException;

import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.services.plugin.PluginConfiguration;
import org.apache.jetspeed.services.plugin.PluginInitializationException;
import org.apache.jetspeed.services.plugin.util.CauseExtractor;
import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.metadata.JdbcConnectionDescriptor;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;

/**
 * This is a implementation of  <code>PersistencePlugin</code> 
 * that is backed by <a href="http://db.apache.org/ojb">ObjectRelationalBridge (OJB)</a>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public abstract class AbstractOJBPersistencePlugin implements PersistencePlugin
{
    protected static final String RESOLVE_DB_ALIAS = "resolveDbAlias";
    private static final Log log = LogFactory.getLog(AbstractOJBPersistencePlugin.class);

    protected static final JetspeedOJBRuntimeException failure(String message, Throwable e)
    {
        log.error(message, e);
        return new JetspeedOJBRuntimeException(message, e);
    }

    protected PluginConfiguration configuration;

    private HashMap connectionToPBMap;

    private String overrideDefaultJcd;

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#abortTransaction(java.lang.Object)
     */
    public abstract void abortTransaction(Object transaction);

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#add(java.lang.Object)
     */
    public void add(Object object)
    {
        Object tx = startTransaction();
        addObjectToTransaction(object, tx, LOCK_LEVEL_WRITE);
        commitTransaction(tx);

    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#addObjectToTransaction(java.lang.Object, java.lang.Object)
     */
    public abstract void addObjectToTransaction(Object object, Object transaction, int lockLevel);

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#commitTransaction(java.lang.Object)
     */
    public abstract void commitTransaction(Object transaction);

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#delete(java.lang.Object)
     */
    public void delete(Object object)
    {
        Object tx = startTransaction();
        setObjectForDeletion(object, tx);
        commitTransaction(tx);
    }

    /**
     * @return <code>true</code> if the <code>obj</code> is
     * of type AbstractOJBPersistencePlugin and the name defined
     * within the plugin configuration for <code>obj</code> is the
     * same as this ones.  Otherwise, returns <code>false</code>
     * 
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof AbstractOJBPersistencePlugin)
        {
            AbstractOJBPersistencePlugin plugin = (AbstractOJBPersistencePlugin) obj;
            return plugin.configuration.getName().equals(configuration.getName());
        }

        return false;
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#generateQuery(org.apache.jetspeed.services.perisistence.SimpleCriteria)
     */
    public Object generateQuery(Class clazz, LookupCriteria criteria)
    {
        OjbLookupCriteria luCrit = (OjbLookupCriteria) criteria;
        return QueryFactory.newQuery(clazz, luCrit.getOjbCriteria());
    }

    /**
     * @see org.apache.jetspeed.services.ojb.OJBService#getBroker()
     */
    public PersistenceBroker getBroker()
    {
        if (overrideDefaultJcd != null)
        {
            return getBroker(overrideDefaultJcd);
        }
        else
        {
            return PersistenceBrokerFactory.defaultPersistenceBroker();
        }

    }

    /**
     * @see org.apache.jetspeed.services.ojb.OJBService#getBroker(java.lang.String)
     */
    public PersistenceBroker getBroker(String aliasName)
    {
        return PersistenceBrokerFactory.createPersistenceBroker(new PBKey(aliasName));
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#getCollectionByQuery(java.lang.Class, java.lang.Object)
     */
    public Collection getCollectionByQuery(Class clazz, Object query)
    {
        PersistenceBroker pb = null;
        try
        {
            pb = getBroker();
            Query useQuery = null;
            if (query instanceof Criteria)
            {
                useQuery = QueryFactory.newQuery(clazz, (Criteria) query);
            }
            else
            {
                useQuery = (Query) query;
            }

            return pb.getCollectionByQuery(useQuery);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw failure("Failed to retreive Collection", e);
        }
        finally
        {
            // always release the broker
            releaseBroker(pb);
        }
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#getConfiguration()
     */
    public PluginConfiguration getConfiguration()
    {
        return configuration;
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#getDbAlias()
     */
    public String getDbAlias() throws UnsupportedOperationException
    {
        PersistenceBroker pb = getBroker();
        try
        {
            return pb.serviceConnectionManager().getConnectionDescriptor().getDbAlias();
        }
        finally
        {
            releaseBroker(pb);
        }
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#getObjectByQuery(java.lang.Class, java.lang.Object)
     */
    public Object getObjectByQuery(Class clazz, Object query)
    {
        PersistenceBroker pb = null;
        try
        {
            Query useQuery = null;
            pb = getBroker();
            if (query instanceof Criteria)
            {
                useQuery = QueryFactory.newQuery(clazz, (Criteria) query);
            }
            else
            {
                useQuery = (Query) query;
            }
            return pb.getObjectByQuery(useQuery);
        }
        catch (Throwable e)
        {
            throw failure("Failed to retreive Object.", e);
        }
        finally
        {
            // always release the broker
            releaseBroker(pb);
        }
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#getSqlConnection()
     */
    public Connection getSqlConnection()
    {
        PersistenceBroker pb = getBroker();
        try
        {
            // Retrieve the SQL Connection assoc. with this broker instance
            Connection connection = pb.serviceConnectionManager().getConnection();
            // record the connection to PB relationship so it can be released correctly
            connectionToPBMap.put(connection, pb);
            return connection;
        }
        catch (LookupException e)
        {
            releaseBroker(pb);
            throw failure("Failed to retreive a SQL connection object.", e);
        }

    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        HashCodeBuilder hasher = new HashCodeBuilder(3, 5);
        hasher.append(configuration.getName());

        return hasher.toHashCode();
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#init(org.apache.commons.configuration.Configuration)
     */
    public void init(PluginConfiguration configuration) throws PluginInitializationException
    {

        this.configuration = configuration;
        connectionToPBMap = new HashMap();

        String correctPath = configuration.getPathResolver().getRealPath(configuration.getProperty("OJB.path") + File.separator);

        File ojbPropsLocation = new File(correctPath);
        URL ojbUrl = null;
        if (ojbPropsLocation.exists())
        {
            try
            {
                ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

                ojbUrl = ojbPropsLocation.getAbsoluteFile().toURL();
                log.info("URL to OJB resources: " + ojbPropsLocation.toURL());
                URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[] { ojbUrl }, contextClassLoader);
                Thread.currentThread().setContextClassLoader(urlClassLoader);
                // URL ojbPropUrl = contextClassLoader.getSystemResource("OJB.properties");
                URL ojbPropUrl = urlClassLoader.getResource("OJB.properties");
                log.info("Loading OJB.properties from: " + ojbPropUrl.toString());
                System.out.println(ojbPropUrl);
            }
            catch (MalformedURLException e1)
            {
                log.error("Could not locate the OJB load directory.  Bad URL.", e1);
                throw new PluginInitializationException("Could not locate the OJB load directory.  Bad URL.", e1);
            }

        }
        else
        {
            throw new PluginInitializationException("Could not locate the OJB load directory. " + ojbUrl);
        }

        //System.setProperty("OJB.properties", ojbPropsLocation);

        overrideDefaultJcd = configuration.getProperty("override.default.jcd");

        //log.info("OJB.properties system property set to " + ojbPropsLocation);
        //log.info("OJB will use this value to locate the OJB.properties configuration file.");
        try
        {
            // run through and test whether or not we were able
            // to get a pb instance.
            PersistenceBroker pb = null;
            if (overrideDefaultJcd != null)
            {
                log.info("overriding default JDBC Connection Descriptor with " + overrideDefaultJcd);
                pb = getBroker(overrideDefaultJcd);
            }
            else
            {
                pb = getBroker();
            }

            // This helps us support RDBMSes that have file system based aliases
            // like HSQL in stand-alone mode
            boolean resolveAlias = new Boolean(configuration.getProperty(RESOLVE_DB_ALIAS, "false")).booleanValue();
            if (resolveAlias)
            {
                log.info("Resolving DB alias to absolute path.");
                JdbcConnectionDescriptor jcd = pb.serviceConnectionManager().getConnectionDescriptor();
                if (jcd.getDbAlias() != null)
                {
                    String truePath = configuration.getPathResolver().getRealPath(jcd.getDbAlias());
                    log.info("DB alias reolved to: " + truePath);
                    jcd.setDbAlias(truePath);
                }
            }

            pb.close();

            // Allow subclasses to init their own stuff
            postInit();

            log.info(this.getClass().getName() + " initalized!");
        }
        catch (Exception e)
        {
            String cause = CauseExtractor.getCompositeMessage(e);
            String message = "Unable create a ojb persistence plugin.  Cause: " + cause;
            log.fatal(message, e);
            throw new PluginInitializationException(message, e);
        }

    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#newLookupCriteria()
     */
    public LookupCriteria newLookupCriteria()
    {
        return new OjbLookupCriteria();
    }

    /**
     * Subclasses acn implement this method to provide any addtional
     * initialization requirements for its specific implementation.
     * 
     * @throws InitializationException
     */
    protected abstract void postInit() throws InitializationException;

    /**
     * @see org.apache.jetspeed.services.ojb.OJBService#releaseBroker(org.apache.ojb.broker.PersistenceBroker)
     */
    public void releaseBroker(PersistenceBroker broker)
    
    {
        if (broker != null)
        {
            broker.close();
        }
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#releaseSqlConnection(java.sql.Connection)
     */
    public void releaseSqlConnection(Connection sqlConnection)
    {
        PersistenceBroker pb = (PersistenceBroker) connectionToPBMap.get(sqlConnection);
        if (pb != null)
        {
            // release the assoc. SQL Connection through the broker
            pb.serviceConnectionManager().releaseConnection();
            // relase the broker back to the pool
            releaseBroker(pb);
            // remove mapping
            connectionToPBMap.remove(sqlConnection);
        }

    }

    /**
     * Removes an object from OJB's cache
     * @param obj
     */
    public void removeFromCache(Object obj)
    {

        PersistenceBroker pb = getBroker();
        try
        {
            pb.removeFromCache(obj);
        }
        finally
        {
            releaseBroker(pb);
        }

    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#setDbAlias(java.lang.String)
     */
    public void setDbAlias(String dbAlias) throws UnsupportedOperationException
    {
        PersistenceBroker pb = getBroker();

        try
        {
            JdbcConnectionDescriptor jcd = pb.serviceConnectionManager().getConnectionDescriptor();
            jcd.setDbAlias(dbAlias);
            log.info("DB Alias changed to " + dbAlias + " for the " + configuration.getName() + " plugin.");
        }
        finally
        {
            releaseBroker(pb);
        }

    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#setObjectForDeletion(java.lang.Object, java.lang.Object)
     */
    public abstract void setObjectForDeletion(Object object, Object transaction);

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#startTransaction()
     */
    public abstract Object startTransaction();

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#update(java.lang.Object)
     */
    public void update(Object object)
    {
        PersistenceBroker pb = null;
        try
        {
            pb = getBroker();
            // 1. remove object from the OJB cache
            pb.removeFromCache(object);

            // 2. Start the transaction
            Object tx = startTransaction();

            // 3. retreive a "stale" version of this object from the db
            Identity id = new Identity(object, pb);
            Object staleObject = pb.getObjectByIdentity(id);
            addObjectToTransaction(staleObject, tx, LOCK_LEVEL_WRITE);

            // 4. Map new values to the stale object
            BeanUtils.copyProperties(staleObject, object);

            // 5. Commit the transaction
            commitTransaction(tx);
            pb.removeFromCache(staleObject);

        }
        catch (Throwable e)
        {
            log.error("Unexpected exception thrown while updating object instance", e);
        }
        finally
        {
            releaseBroker(pb);
        }
    }

    public Collection getExtent(Class clazz)
    {
        return getCollectionByQuery(clazz, new Criteria());
    }

}
