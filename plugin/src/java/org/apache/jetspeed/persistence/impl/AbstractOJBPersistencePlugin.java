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
import java.sql.DriverManager;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.InitializationException;

import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.TransactionStateException;
import org.apache.jetspeed.services.plugin.PluginConfiguration;
import org.apache.jetspeed.services.plugin.PluginInitializationException;
import org.apache.jetspeed.services.plugin.util.CauseExtractor;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PBLifeCycleEvent;
import org.apache.ojb.broker.PBLifeCycleListener;
import org.apache.ojb.broker.PBStateEvent;
import org.apache.ojb.broker.PBStateListener;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.metadata.JdbcConnectionDescriptor;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;

/**
 * This is a implementation of  <code>PersistencePlugin</code> 
 * that is backed by <a href="http://db.apache.org/ojb">ObjectRelationalBridge (OJB)</a>
 * <p>
 *   This plugin is self monitoring in that there is no need for client applications to worry
 *   about explicitly doing a <code>PersistenceBroker.close()</code> the plug in itself
 *   will monitor and reclaim inactive PersistenceBrokers.  This also allows to have a single
 *   <code>PersistnceBroker</code> per thread, for that entire threads lifetime without having
 *   to worry about resource leakage.
 * </p>
 * <p>
 *   Configuring PersistenceBroker life times:<br\>
 *   <code>broker.ttl</code> The time the broker will remain open from the last operation defaults to 15000 millis.<br/>
 *   <code>broker.check.interval</code> How often we check all registered PersistenceBrokers.  Defaults to 10000 millis. 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public abstract class AbstractOJBPersistencePlugin implements PersistencePlugin, PBLifeCycleListener, PBStateListener
{
    protected static final String RESOLVE_DB_ALIAS = "resolveDbAlias";
    private static final Log log = LogFactory.getLog("org.apache.jetspeed.persistence");

    protected static final JetspeedOJBRuntimeException failure(String message, Throwable e)
    {
        log.error(message, e);
        return new JetspeedOJBRuntimeException(message, e);
    }

    protected PluginConfiguration configuration;

    protected String overrideDefaultJcd;

    protected Map brokerActivity = new HashMap();

    protected ThreadLocal TLpb = new ThreadLocal();

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#deleteByQuery(java.lang.Object)
     */
    public void deleteByQuery(Object query)
    {
        PersistenceBroker pb = null;
        try
        {
            Query useQuery = (Query) query;
            pb = getBroker();

            pb.deleteByQuery(useQuery);
        }
        catch (Throwable e)
        {
            throw failure("Failed to delete by query.", e);
        }

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

        PersistenceBroker pb = (PersistenceBroker) TLpb.get();
        if (pb == null || pb.isClosed())
        {
            if (overrideDefaultJcd != null)
            {
                log.info("overriding default JDBC Connection Descriptor with " + overrideDefaultJcd);
                pb = PersistenceBrokerFactory.createPersistenceBroker(new PBKey(overrideDefaultJcd));
            }
            else
            {
                pb = PersistenceBrokerFactory.defaultPersistenceBroker();
            }
            // Add the plugin as a listener, temporary is fine
            pb.addListener(this);
            // Set current thread's broker
            TLpb.set(pb);
            // Set up this broker with a last active time stamp
            brokerActivity.put(pb, new Date());
        }

        return pb;

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
            throw failure("Failed to retreive Collection", e);
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

        return pb.serviceConnectionManager().getConnectionDescriptor().getDbAlias();

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
            return connection;
        }
        catch (LookupException e)
        {
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

            PersistenceBroker pb = getBroker();

            // This helps us support RDBMSes that have file system based aliases
            // like HSQL in stand-alone mode
            boolean resolveAlias = new Boolean(configuration.getProperty(RESOLVE_DB_ALIAS, "false")).booleanValue();
            JdbcConnectionDescriptor jcd = pb.serviceConnectionManager().getConnectionDescriptor();

            if (resolveAlias)
            {
                log.info("Resolving DB alias to absolute path.");

                if (jcd.getDbAlias() != null)
                {
                    String truePath = configuration.getPathResolver().getRealPath(jcd.getDbAlias());
                    log.info("DB alias reolved to: " + truePath);
                    jcd.setDbAlias(truePath);
                }
            }

            String protocol = jcd.getProtocol();
            String subProtocol = jcd.getSubProtocol();
            String testalias = jcd.getDbAlias();
            String jdbcDriver = jcd.getDriver();
            String jdbcURL = protocol + ":" + subProtocol + ":" + testalias;

            // if we are using a datasource, this will not be available

            if (jdbcDriver != null)
            {

                Class.forName(jdbcDriver);

                Connection c = DriverManager.getConnection(jdbcURL, jcd.getUserName(), jcd.getPassWord());
                c.close();
            }
            else if (jcd.getDatasourceName() != null)
            {
//				InitialContext ctx = new InitialContext();
//                DataSource ds = (DataSource) ctx.lookup(jcd.getDatasourceName() );
//                if(ds == null)
//                {
//                	throw new IllegalStateException("Unable to retreive the DataSource: "+jcd.getDatasourceName() );
//                }
            }

            // We should keep the broker around until we are finished			
            // pb.close();

            // Allow subclasses to init their own stuff
            postInit();

            log.info(this.getClass().getName() + " initalized!");
        }
        catch (Exception e)
        {

            String message = "Unable create a ojb persistence plugin: " + e.toString();
            log.fatal(message, e);
            throw new PluginInitializationException(message, e);
        }

        // default to 15 seconds of inactivity, after which the broker is recalimed to the pool
        int ttl = Integer.parseInt(configuration.getProperty("broker.ttl", "15000"));
        log.info("PersistenceBroker Time To Live set to " + ttl);
        int checkInterval = Integer.parseInt(configuration.getProperty("broker.check.interval", "10000"));
        log.info("PersistenceBrokers will be checked for inactivity every " + (checkInterval / 1000) + " seconds.");

        InactivityMonitor monitor = new InactivityMonitor(ttl, checkInterval);
        monitor.setDaemon(true);
        monitor.setPriority(Thread.MIN_PRIORITY);
        monitor.setContextClassLoader(Thread.currentThread().getContextClassLoader());
        monitor.start();

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

    //    protected void releaseCurrentPB()    
    //    {
    //        PersistenceBroker pb = (PersistenceBroker) TLpb.get();
    //        if (pb != null)
    //        {
    //            TLpb.set(null);
    //            if (!pb.isClosed())
    //            {
    //                pb.close();
    //            }
    //        }
    //    }

    /**
     * Updates the last time the broker was accessed
     * @param pb
     */
    protected void touchBroker(PersistenceBroker pb)
    
    {
        Date lastAccessed = (Date) brokerActivity.get(pb);
        if (lastAccessed != null)
        {
            //update
            log.debug("Updating PersistenceBroker " + pb + " last access stamp to now");
            lastAccessed.setTime(System.currentTimeMillis());
        }
        else
        {
            // Set up this broker with a last active time stamp
            brokerActivity.put(pb, new Date());
        }
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#releaseSqlConnection(java.sql.Connection)
     */
    public void releaseSqlConnection(Connection sqlConnection)
    {
        PersistenceBroker pb = getBroker();
        if (pb != null)
        {
            // release the assoc. SQL Connection through the broker
            pb.serviceConnectionManager().releaseConnection();
        }

    }

    /**
     * Removes an object from OJB's cache
     * @param obj
     */
    public void removeFromCache(Object obj)
    {

        PersistenceBroker pb = getBroker();

        pb.removeFromCache(obj);

    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#setDbAlias(java.lang.String)
     */
    public void setDbAlias(String dbAlias) throws UnsupportedOperationException
    {
        PersistenceBroker pb = getBroker();

        JdbcConnectionDescriptor jcd = pb.serviceConnectionManager().getConnectionDescriptor();
        jcd.setDbAlias(dbAlias);
        log.info("DB Alias changed to " + dbAlias + " for the " + configuration.getName() + " plugin.");

    }

    public Collection getExtent(Class clazz)
    {
        return getCollectionByQuery(clazz, new Criteria());
    }

    public void invalidateObject(Object object)
    {
        PersistenceBroker broker = getBroker();
        broker.removeFromCache(object);
    }

    /** 
     * <p>
     * clearCache
     * </p>
     * 
     * @see org.apache.jetspeed.persistence.PersistencePlugin#clearCache()
     * 
     */
    public void clearCache()
    {
        getBroker().clearCache();
    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#beginTransaction()
     */
    public void beginTransaction() throws TransactionStateException
    {
        getBroker();

    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#commitTransaction()
     */
    public void commitTransaction() throws TransactionStateException
    {
        // nothin
    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#rollbackTransaction()
     */
    public void rollbackTransaction() throws TransactionStateException
    {
        // nothin
    }

    protected class InactivityMonitor extends Thread
    {
        int ttl;
        int checkInterval;
        boolean started = true;

        protected InactivityMonitor(int ttl, int checkInterval)
        {
            this.ttl = ttl;
            this.checkInterval = checkInterval;
            setName("Persistence plugin inactivity monitor [TTL:" + ttl + "] [interval:" + checkInterval + "]");
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            while (started)
            {
                Iterator keys = brokerActivity.keySet().iterator();
                while (keys.hasNext())
                {
                    PersistenceBroker pb = (PersistenceBroker) keys.next();
                    Date last = (Date) brokerActivity.get(pb);
                    Date now = new Date();
                    if ((now.getTime() - last.getTime()) > ttl)
                    {
                        log.debug("PersistenceBroker " + pb + " has exceeded its TTL, attemting to close.");
                        // broker should now be considered available
                        try
                        {
                            pb.close();
                            log.debug("PersistenceBroker successfully closed.");
                        }
                        catch (Throwable e1)
                        {
                            log.error("Unable to close PersistenceBroker " + pb, e1);
                        }
                    }
                }

                try
                {
                    sleep(checkInterval);
                }
                catch (InterruptedException e)
                {

                }
            }
        }

        public void safeStop()
        {
            started = false;
        }

    }

    /**
     * @see org.apache.ojb.broker.PBLifeCycleListener#afterDelete(org.apache.ojb.broker.PBLifeCycleEvent)
     */
    public void afterDelete(PBLifeCycleEvent arg0) throws PersistenceBrokerException
    {
        // all touches happen "before" the event

    }

    /**
     * @see org.apache.ojb.broker.PBLifeCycleListener#afterInsert(org.apache.ojb.broker.PBLifeCycleEvent)
     */
    public void afterInsert(PBLifeCycleEvent arg0) throws PersistenceBrokerException
    {
        // all touches happen "before" the event

    }

    /**
     * @see org.apache.ojb.broker.PBLifeCycleListener#afterLookup(org.apache.ojb.broker.PBLifeCycleEvent)
     */
    public void afterLookup(PBLifeCycleEvent arg0) throws PersistenceBrokerException
    {
        touchBroker(arg0.getTriggeringBroker());

    }

    /**
     * @see org.apache.ojb.broker.PBLifeCycleListener#afterUpdate(org.apache.ojb.broker.PBLifeCycleEvent)
     */
    public void afterUpdate(PBLifeCycleEvent arg0) throws PersistenceBrokerException
    {
        // all touches happen "before" the event

    }

    /**
     * @see org.apache.ojb.broker.PBLifeCycleListener#beforeDelete(org.apache.ojb.broker.PBLifeCycleEvent)
     */
    public void beforeDelete(PBLifeCycleEvent arg0) throws PersistenceBrokerException
    {
        touchBroker(arg0.getTriggeringBroker());

    }

    /**
     * @see org.apache.ojb.broker.PBLifeCycleListener#beforeInsert(org.apache.ojb.broker.PBLifeCycleEvent)
     */
    public void beforeInsert(PBLifeCycleEvent arg0) throws PersistenceBrokerException
    {
        touchBroker(arg0.getTriggeringBroker());

    }

    /**
     * @see org.apache.ojb.broker.PBLifeCycleListener#beforeUpdate(org.apache.ojb.broker.PBLifeCycleEvent)
     */
    public void beforeUpdate(PBLifeCycleEvent arg0) throws PersistenceBrokerException
    {
        touchBroker(arg0.getTriggeringBroker());

    }

    /**
     * @see org.apache.ojb.broker.PBStateListener#afterBegin(org.apache.ojb.broker.PBStateEvent)
     */
    public void afterBegin(PBStateEvent arg0)
    {
        // all touches happen "before" the event

    }

    /**
     * @see org.apache.ojb.broker.PBStateListener#afterCommit(org.apache.ojb.broker.PBStateEvent)
     */
    public void afterCommit(PBStateEvent arg0)
    {
        // all touches happen "before" the event

    }

    /**
     * @see org.apache.ojb.broker.PBStateListener#afterOpen(org.apache.ojb.broker.PBStateEvent)
     */
    public void afterOpen(PBStateEvent arg0)
    {
        // all touches happen "before" the event

    }

    /**
     * @see org.apache.ojb.broker.PBStateListener#afterRollback(org.apache.ojb.broker.PBStateEvent)
     */
    public void afterRollback(PBStateEvent arg0)
    {
        // all touches happen "before" the event

    }

    /**
     * @see org.apache.ojb.broker.PBStateListener#beforeBegin(org.apache.ojb.broker.PBStateEvent)
     */
    public void beforeBegin(PBStateEvent arg0)
    {
        touchBroker(arg0.getTriggeringBroker());

    }

    /**
     * @see org.apache.ojb.broker.PBStateListener#beforeClose(org.apache.ojb.broker.PBStateEvent)
     */
    public void beforeClose(PBStateEvent arg0)
    {
        // not needed at this point

    }

    /**
     * @see org.apache.ojb.broker.PBStateListener#beforeCommit(org.apache.ojb.broker.PBStateEvent)
     */
    public void beforeCommit(PBStateEvent arg0)
    {
        touchBroker(arg0.getTriggeringBroker());

    }

    /**
     * @see org.apache.ojb.broker.PBStateListener#beforeRollback(org.apache.ojb.broker.PBStateEvent)
     */
    public void beforeRollback(PBStateEvent arg0)
    {
        touchBroker(arg0.getTriggeringBroker());

    }

}
