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
package org.apache.jetspeed.services.registry.impl;

import java.util.List;
import java.util.ArrayList;
 
// Castor JDO
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.JDO;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;

import org.apache.jetspeed.exception.RegistryException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helper functions to manipulate persistent objects using Castor
 * 
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class CastorHelper
{
    protected final static Log log = LogFactory.getLog(CastorHelper.class);
    
    private static final String DUPKEY_MSG = "Duplicate Key Entry Exception: choose another key value: ";
    private static final String EXCEPTION_CONNECTION = "Exception in getting database connection ";
    private static final String EXCEPTION_ADD = "Exception in adding object to database ";
    private static final String EXCEPTION_UPDATE = "Exception in updating object in database ";
    private static final String EXCEPTION_REMOVE = "Exception in removing object from database ";
    private static final String EXCEPTION_QUERY = "Exception in executing OQL query ";
    private static final String EXCEPTION_CLOSE = "Exception in closing database connection ";
    
    /**
     * Generic implementation of adding a new object to the persistent store.
     *
     * @param object The object to be added.
     * @param objectName The name of the object being added, used in exception.
     * @throws RegistryException
     */
    public static void addObject(JDO jdo, Database db, Object object, String objectName)
        throws RegistryException
    {
        boolean inTransaction = (db != null);
        boolean exception = false;
                
        if (null == db)
        {
            db = getDatabaseConnection(jdo);
        }
        
        try
        {
            if (!inTransaction)
            {
                db.begin();
            }
            
            db.create(object);
            
            if (!inTransaction)
            {
                db.commit();
            }
        }
        catch (DuplicateIdentityException die)
        {
            exception = true;            
            String msg = DUPKEY_MSG + objectName;
            log.info(msg, die);
            throw new RegistryException(msg, die);
        }
        catch (PersistenceException pe)
        {
            exception = true;
            String emsg = pe.getMessage();
            if (emsg != null && emsg.indexOf("Duplicate") > -1)
            {
                String msg = DUPKEY_MSG + objectName;
                log.info(msg, pe);
                throw new RegistryException(msg, pe);
            }
            String msg = EXCEPTION_ADD + object.getClass() + " : " + objectName;
            log.error(msg, pe);

            throw new RegistryException(msg, pe);
        }
        catch (Exception e)
        {
            exception = true;
            String msg = EXCEPTION_ADD + object.getClass() + " : " + objectName;
            log.error(msg, e);
            throw new RegistryException(msg, e);
        }
        finally
        {
            try
            {
                if (null != db)
                {                    
                    if (exception && db.isActive())
                    {
                        db.rollback();
                    }
                    
                    if (!inTransaction)
                    {
                        db.close();
                    }                    
                }
            }
            catch (Exception e)
            {
                log.error(EXCEPTION_CLOSE, e);
                throw new RegistryException(EXCEPTION_CLOSE, e);
            }
        }
    }

    /**
     * Connection management - Castor connections
     *
     */
    public static Database getDatabaseConnection(JDO jdo)
        throws RegistryException
    {
        Database db = null;

        try
        {
            db = jdo.getDatabase();
        }
        catch (Exception e)
        {
            log.error(EXCEPTION_CONNECTION, e);
            throw new RegistryException(EXCEPTION_CONNECTION, e);
        }
        return db;
    }

    /**
     * Generic implementation of updating objects in the persistent store.
     *
     * @param object The object to be updated.
     * @param objectName The name of the object being updating, used in exception.
     * @throws RegistryException
     */
    public static void updateObject(JDO jdo, Database db, Object object, String objectName)
        throws RegistryException
    {
        boolean inTransaction = (db != null);
        boolean exception = false;
        
        if (null == db)
        {
            db = getDatabaseConnection(jdo);
        }

        try
        {
            if (!inTransaction)
            {
                db.begin();
            }
            
            db.update(object);
            
            if (!inTransaction)
            {
                db.commit();
            }
        }
        catch (DuplicateIdentityException die)
        {
            exception = true;
            String msg = DUPKEY_MSG + objectName;
            log.info(msg, die);
            throw new RegistryException(msg, die);
        }
        catch (PersistenceException pe)
        {
            exception = true;            
            String emsg = pe.getMessage();
            if (emsg != null && emsg.indexOf("Duplicate") > -1)
            {
                String msg = DUPKEY_MSG + objectName;
                log.info(msg, pe);
                throw new RegistryException(msg, pe);
            }
            String msg = EXCEPTION_UPDATE + object.getClass() + " : " + objectName;
            log.error(msg, pe);

            throw new RegistryException(msg, pe);
        }
        catch (Exception e)
        {
            exception = true;            
            String msg = EXCEPTION_UPDATE + object.getClass() + " : " + objectName;
            log.error(msg, e);
            throw new RegistryException(msg, e);
        }
        finally
        {
            try
            {
                if (null != db)
                {
                    if (exception && db.isActive())
                    {
                        db.rollback();
                    }
                    if (!inTransaction)
                    {
                        db.close();
                    }                    
                }
            }
            catch (Exception e)
            {
                log.error(EXCEPTION_CLOSE, e);
                throw new RegistryException(EXCEPTION_CLOSE, e);
            }
        }
    }

    /**
     * Generic implementation of removing objects from the persistent store.
     *
     * @param className The name of the class that we are querying on
     * @param keyName The unique key field name to lookup the object by
     * @param keyValue The unique key field value to lookup the object by
     * @throws RegistryException
     */
    public static void removeObject(JDO jdo, Database db, String className, String keyName, Object keyValue)
        throws RegistryException
    {
        boolean inTransaction = (db != null);
        boolean exception = false;
        
        if (null == db)
        {
            db = getDatabaseConnection(jdo);
        }

        OQLQuery query = null;
        QueryResults results = null;

        try
        {
            if (!inTransaction)
            {
                db.begin();
            }
            
            String queryString = "select p from " + className + " p where " + keyName + " = $1";

            query = db.getOQLQuery(queryString);
            if (keyValue instanceof Long)
            {
                query.bind( ((Long)keyValue).longValue() );
            }
            else
            {
                query.bind(keyValue);
            }
            

            results = query.execute();
            if (results.hasMore())
            {
                Object object = results.next();
                db.remove(object);
            }
            
            if (!inTransaction)
            {            
                db.commit();
            }
        }
        catch (Exception e)
        {
            exception = true;
            String msg = EXCEPTION_REMOVE + className + " : " + keyName + " = " + keyValue;
            log.error(msg, e);
            throw new RegistryException(msg, e);
        }
        finally
        {
            try
            {
                if (null != results)
                {
                    results.close();
                }
                if (null != db)
                {
                    if (exception && db.isActive())
                    {
                        db.rollback();
                    }
                    if (!inTransaction)
                    {
                        db.close();
                    }                    
                }
            }
            catch (Exception e)
            {
                log.error(EXCEPTION_CLOSE, e);
                throw new RegistryException(EXCEPTION_CLOSE, e);
            }
        }
    }

    /**
     * Generic implementation of retrieving ALL objects from the persistent store.
     *
     * @param queryString The OQL query string to be executed.
     * @return List over the result set.
     * @throws RegistryException
     */
    public static List getAllObjects(JDO jdo, Database db, String queryString)
        throws RegistryException
    {
        boolean inTransaction = (db != null);
        boolean exception = false;
        
        if (null == db)
        {
            db = getDatabaseConnection(jdo);
        }

        OQLQuery query = null;
        QueryResults results = null;
        ArrayList resultSet = null;

        try
        {
            if (!inTransaction)
            {                        
                db.begin();
            }
            query = db.getOQLQuery(queryString);
            results = query.execute();
            // FIXME: how do I know if I can get the frickin size or not from Castor?            
            resultSet = new ArrayList(/*results.size()*/);
            while (results.hasMore())
            {
                resultSet.add(results.next());
            }
            if (!inTransaction)
            {                        
                db.commit();
            }
        }
        catch (Exception e)
        {
            exception = true;
            String msg = EXCEPTION_QUERY + queryString;
            log.error(msg, e);
            throw new RegistryException(msg, e);
        }
        finally
        {
            try
            {
                if (null != results)
                {
                    results.close();
                }
                if (null != db)
                {
                    if (exception && db.isActive())
                    {
                        db.rollback();
                    }
                    if (!inTransaction)
                    {
                        db.close();
                    }                    
                }
            }
            catch (Exception e)
            {
                log.error(EXCEPTION_CLOSE, e);
                throw new RegistryException(EXCEPTION_CLOSE, e);
            }
        }
        return resultSet;
    }

    /**
     * Generic implementation of retrieving ALL objects from the persistent store
     * for a master object (1..many)
     *
     * @param queryString The OQL query string to be executed.
     * @param key The key of the master object.
     * @return List over the result set.
     * @throws RegistryException
     */
    public static List getAllSubObjects(JDO jdo, Database db, String queryString, long key)
        throws RegistryException
    {
        boolean inTransaction = (db != null);
        boolean exception = false;
        
        if (null == db)
        {
            db = getDatabaseConnection(jdo);
        }

        OQLQuery query = null;
        QueryResults results = null;
        ArrayList resultSet = null;

        try
        {
            if (!inTransaction)
            {                                    
                db.begin();
            }
            query = db.getOQLQuery(queryString);
            query.bind(key);
            results = query.execute();
            // FIXME: how do I know if I can get the frickin size or not from Castor?
            resultSet = new ArrayList(/*results.size()*/);
            while (results.hasMore())
            {
                resultSet.add(results.next());
            }
            if (!inTransaction)
            {                                    
                db.commit();
            }
        }
        catch (Exception e)
        {
            exception = true;
            String msg = EXCEPTION_QUERY + queryString;
            log.error(msg, e);
            throw new RegistryException(msg, e);
        }
        finally
        {
            try
            {
                if (null != results)
                {
                    results.close();
                }
                if (null != db)
                {
                    if (exception && db.isActive())
                    {
                        db.rollback();
                    }
                    if (!inTransaction)
                    {
                        db.close();
                    }                    
                }
            }
            catch (Exception e)
            {
                log.error(EXCEPTION_CLOSE, e);
                throw new RegistryException(EXCEPTION_CLOSE, e);
            }
        }
        return resultSet;
    }

    /**
      * Generic implementation of retrieving ALL objects from the persistent store
      * which match the constructed where clause.
      *
      * @param className The name of the class that we are querying on
      * @param queryString The query string
      * @param values The values to be substituted in the where clause
      * @return Object The found object or null
      * @throws RegistryException
      */
    public static Object getObject(JDO jdo, Database db, String className, String queryString, Object[] values)
        throws RegistryException
    {
        boolean inTransaction = (db != null);
        boolean exception = false;
        
        if (null == db)
        {
            db = getDatabaseConnection(jdo);
        }
        
        OQLQuery query = null;
        QueryResults results = null;
        Object object = null;

        try
        {
            if (!inTransaction)
            {                                    
                db.begin();
            }

            query = db.getOQLQuery(queryString);
            for (int count = 0; count < values.length; count++)
            {
                Object keyValue = values[count];
                if (keyValue instanceof Long)
                {
                    long intValue = ((Long)keyValue).longValue();
                    query.bind(intValue);
                }
                else
                {
                    query.bind(keyValue);
                }
            }

            results = query.execute();
            if (results.hasMore())
            {
                object = results.next();

            }
            if (!inTransaction)
            {                                                
                db.commit();
            }
        }
        catch (Exception e)
        {
            exception = true;
            String msg = EXCEPTION_QUERY + className + " : " + queryString + " = " + values;
            log.error(msg, e);
            throw new RegistryException(msg, e);
        }
        finally
        {
            try
            {
                if (null != results)
                {
                    results.close();
                }
                if (null != db)
                {
                    if (exception && db.isActive())
                    {
                        db.rollback();
                    }
                    if (!inTransaction)
                    {
                        db.close();
                    }                    
                }
            }
            catch (Exception e)
            {
                log.error(EXCEPTION_CLOSE, e);
                throw new RegistryException(EXCEPTION_CLOSE, e);
            }
        }
        return object;
    }


   /**
     * Generic implementation of retrieving ALL objects from the persistent store
     * which match the constructed where clause.
     *
     * @param className The name of the class that we are querying on
     * @param keyName The unique key field name to lookup the object by
     * @param keyValue The unique key field value to lookup the object by
     * @return Object The found object or null
     * @throws RegistryException
     */
    public static Object getObject(JDO jdo, Database db, String className, String keyName, Object keyValue)
        throws RegistryException
    {
        String queryString = "select p from " + className + " p where " + keyName + " = $1";
        Object[] values = {keyValue};
        return getObject(jdo, db, className, queryString, values);
    }
    
}
