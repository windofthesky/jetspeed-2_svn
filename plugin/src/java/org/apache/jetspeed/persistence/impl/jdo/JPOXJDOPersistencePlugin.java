/**
 * Created on Jan 26, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.persistence.impl.jdo;

import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

//import javax.jdo.JDOHelper;
//import javax.jdo.PersistenceManager;
//import javax.jdo.PersistenceManagerFactory;
//import javax.jdo.Query;
//import javax.jdo.Transaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.TransactionStateException;
import org.apache.jetspeed.services.plugin.PluginConfiguration;
import org.apache.jetspeed.services.plugin.PluginInitializationException;

/**
 * <p>
 * JPOXJDOPersistencePlugin
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
// public class JPOXJDOPersistencePlugin implements PersistencePlugin
public class JPOXJDOPersistencePlugin
{

//    public static final String VALIDATE_CONSTRAINTS = "org.jpox.validateConstraints";
//    public static final String VALIDATE_TABLES = "org.jpox.validateTables";
//    public static final String AUTOCREATE_TABLES = "org.jpox.autoCreateTables";
//    public static final String CONNECTION_PASSWORD = "javax.jdo.option.ConnectionPassword";
//    public static final String CONNECTION_USER_NAME = "javax.jdo.option.ConnectionUserName";
//    public static final String CONNECTION_URL = "javax.jdo.option.ConnectionURL";
//    public static final String CONNECTION_DRIVER_NAME = "javax.jdo.option.ConnectionDriverName";
//    public static final String PERSISTENCE_MANAGER_FACTORY_CLASS = "javax.jdo.PersistenceManagerFactoryClass";
//    protected static final String RESOLVE_DB_ALIAS = "resolveDbAlias";
//
//    private static final Log log = LogFactory.getLog(JPOXJDOPersistencePlugin.class);
//
//    private PersistenceManagerFactory pmf;
//
//    private ThreadLocal TLpm = new ThreadLocal();
//    
//    private PluginConfiguration conf;
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#beginTransaction()
//     */
//    public void beginTransaction() throws TransactionStateException
//    {
//        Transaction tx = getManager().currentTransaction();
//        tx.begin();
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#commitTransaction()
//     */
//    public void commitTransaction() throws TransactionStateException
//    {
//        Transaction tx = getManager().currentTransaction();
//        tx.commit();
//        getManager().close();
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#prepareForDelete(java.lang.Object)
//     */
//    public void prepareForDelete(Object obj) throws TransactionStateException
//    {
//        getManager().deletePersistent(obj);
//
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#prepareForUpdate(java.lang.Object)
//     */
//    public void prepareForUpdate(Object obj) throws TransactionStateException
//    {
//        getManager().makeTransactional(obj);
//
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#makePersistent(java.lang.Object)
//     */
//    public void makePersistent(Object obj) throws TransactionStateException
//    {
//        getManager().makePersistent(obj);
//
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#rollbackTransaction()
//     */
//    public void rollbackTransaction() throws TransactionStateException
//    {
//        Transaction tx = getManager().currentTransaction();
//        try
//        {
//            if (tx.isActive())
//            {
//                tx.rollback();
//            }
//        }
//        finally
//        {
//            getManager().close();
//        }
//
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#markDirty(java.lang.Object)
//     */
//    public Object markDirty(Object obj) throws TransactionStateException
//    {
//        getManager().makeTransactional(obj);
//        return obj;
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#deleteByQuery(java.lang.Object)
//     */
//    public void deleteByQuery(Object query)
//    {
//        // TODO Auto-generated method stub
//
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#getCollectionByQuery(java.lang.Class, java.lang.Object)
//     */
//    public Collection getCollectionByQuery(Class clazz, Object query)
//    {
//        Query jdoQuery = (Query) query;
//        return (Collection) jdoQuery.execute();
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#getExtent(java.lang.Class)
//     */
//    public Collection getExtent(Class clazz)
//    {
//        Query jdoQuery = getManager().newQuery(clazz);
//        return getCollectionByQuery(clazz, jdoQuery);
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#getObjectByQuery(java.lang.Class, java.lang.Object)
//     */
//    public Object getObjectByQuery(Class clazz, Object query)
//    {
//        Collection c = getCollectionByQuery(clazz, query);
//        Iterator itr = c.iterator();
//        if (itr.hasNext())
//        {
//            return itr.next();
//        }
//        else
//        {
//            return null;
//        }
//
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#generateQuery(java.lang.Class, org.apache.jetspeed.persistence.LookupCriteria)
//     */
//    public Object generateQuery(Class clazz, LookupCriteria criteria)
//    {
//        Query jdoQuery = getManager().newQuery(clazz);
//        JDOLookUpCriteria jdoCrit = (JDOLookUpCriteria) criteria;
//        jdoQuery.setFilter(jdoCrit.toString());
//        jdoQuery.setOrdering(jdoCrit.getOrderingString());
//        return jdoQuery;
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#newLookupCriteria()
//     */
//    public LookupCriteria newLookupCriteria()
//    {        
//        return new JDOLookUpCriteria( );
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#getConfiguration()
//     */
//    public PluginConfiguration getConfiguration()
//    {        
//        return conf;
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#setDbAlias(java.lang.String)
//     */
//    public void setDbAlias(String dbAlias) throws UnsupportedOperationException
//    {
//        // TODO Auto-generated method stub
//
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#getDbAlias()
//     */
//    public String getDbAlias() throws UnsupportedOperationException
//    {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#getSqlConnection()
//     */
//    public Connection getSqlConnection()
//    {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#releaseSqlConnection(java.sql.Connection)
//     */
//    public void releaseSqlConnection(Connection sqlConnection)
//    {
//        // TODO Auto-generated method stub
//
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#invalidateObject(java.lang.Object)
//     */
//    public void invalidateObject(Object object)
//    {
//        getManager().evict(object);
//
//    }
//
//    /**
//     * @see org.apache.jetspeed.persistence.PersistencePlugin#clearCache()
//     */
//    public void clearCache()
//    {
//        getManager().evictAll();
//
//    }
//
//    /**
//     * @see org.apache.jetspeed.services.plugin.Plugin#init(org.apache.jetspeed.services.plugin.PluginConfiguration)
//     */
//    public void init(PluginConfiguration configuration) throws PluginInitializationException
//    {
//    	this.conf = configuration;
//        Properties properties = new Properties();
//
//        properties.setProperty(
//            PERSISTENCE_MANAGER_FACTORY_CLASS,
//            configuration.getProperty(PERSISTENCE_MANAGER_FACTORY_CLASS, "org.jpox.PersistenceManagerFactoryImpl"));
//        properties.setProperty(CONNECTION_DRIVER_NAME, configuration.getProperty(CONNECTION_DRIVER_NAME));
//
//        //		This helps us support RDBMSes that have file system based aliases
//        // like HSQL in stand-alone mode
////      boolean resolveAlias = new Boolean(configuration.getProperty(RESOLVE_DB_ALIAS, "false")).booleanValue();
//        String url = configuration.getProperty(CONNECTION_URL);
////        if (resolveAlias)
////        {
////            log.info("Resolving DB alias to absolute path.");
////            int startAlias = url.indexOf("://");
////            if (startAlias != -1)
////            {
////                startAlias = startAlias + 3;
////            }
////            String relativePath = url.substring(startAlias, url.length());
////            String protocol = url.substring(0, startAlias);
////            log.info("DB protocol: " + protocol);
////            log.info("DB relative path: " + relativePath);
////
////            String truePath = configuration.getPathResolver().getRealPath(relativePath);
////            url = protocol + truePath;
////        }
//
//        log.info("Database URL: " + url);
//
//        properties.setProperty(CONNECTION_URL, url);
//        properties.setProperty(CONNECTION_USER_NAME, configuration.getProperty(CONNECTION_USER_NAME));
//        properties.setProperty(CONNECTION_PASSWORD, configuration.getProperty(CONNECTION_PASSWORD, ""));
//        properties.setProperty(AUTOCREATE_TABLES, configuration.getProperty(AUTOCREATE_TABLES, "false"));
//        properties.setProperty(VALIDATE_TABLES, configuration.getProperty(VALIDATE_TABLES, "false"));
//        properties.setProperty(VALIDATE_CONSTRAINTS, configuration.getProperty(VALIDATE_CONSTRAINTS, "false"));
//
//        this.pmf = JDOHelper.getPersistenceManagerFactory(properties);
//    }
//
//    public PersistenceManager getManager()
//    {
//        PersistenceManager pm = (PersistenceManager) TLpm.get();
//        if (pm == null)
//        {
//            pm = this.pmf.getPersistenceManager();
//            TLpm.set(pm);
//        }
//        return pm;
//    }

}
