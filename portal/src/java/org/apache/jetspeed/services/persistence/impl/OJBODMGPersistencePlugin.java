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
package org.apache.jetspeed.services.persistence.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.services.persistence.LookupCriteria;
import org.apache.jetspeed.services.persistence.PersistencePlugin;
import org.apache.jetspeed.services.persistence.PersistenceService;
import org.apache.jetspeed.services.persistence.PluginConfiguration;

import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.odmg.OJB;
import org.apache.ojb.odmg.TransactionImpl;
import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.ODMGException;
import org.odmg.Transaction;

/**
 * 
 * OJBODMGPersistencePlugin
 * This is a implementation of  <code>PersistencePlugin</code> 
 * that is backed by <a href="http://db.apache.org/ojb">ObjectRelationalBridge (OJB)</a>
 * that uses ODMG to support its transactions.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class OJBODMGPersistencePlugin extends AbstractOJBPersistencePlugin implements PersistencePlugin
{

    private static final Log log = LogFactory.getLog(OJBODMGPersistencePlugin.class);

    private String overrideDefaultJcd;
    private PluginConfiguration configuration;
    private String persistenceApi;
    private Implementation odmg;
    private Database db;
    private PersistenceService ps;
    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#abortTransaction(java.lang.Object)
     */
    public void abortTransaction(Object transaction)
    {
        Transaction tx = (Transaction) transaction;
        tx.abort();
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#addObjectToTransaction(java.lang.Object, java.lang.Object)
     */
    public void addObjectToTransaction(Object object, Object transaction, int lockLevel)
    {
        int useLevel = -1;

        switch (lockLevel)
        {
            case LOCK_LEVEL_READ :
                useLevel = Transaction.READ;
                break;

            default :
                useLevel = Transaction.WRITE;
                break;
        }

        Transaction tx = (Transaction) transaction;
        tx.lock(object, useLevel);

    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#commitTransaction(java.lang.Object)
     */
    public void commitTransaction(Object transaction)
    {
        Transaction tx = (Transaction) transaction;
        tx.commit();
        System.out.print("");
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#setObjectForDeletion(java.lang.Object, java.lang.Object)
     */
    public void setObjectForDeletion(Object object, Object transaction)
    {
        Transaction tx = (Transaction) transaction;
        tx.lock(object, Transaction.WRITE);
        db.deletePersistent(object);
    }

    /**
     * This plug in uses ODMG to bacl its transactions.
     * 
     * @return  <code>org.odmg.Transaction</code> 
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#startTransaction()
     * 
     */
    public Object startTransaction()
    {
        Transaction tx = odmg.newTransaction();
        tx.begin();
        return tx;
    }

    protected void initODMG() throws InitializationException
    {
        PersistenceBroker broker = null;
        try
        {
            odmg = OJB.getInstance();
            db = odmg.newDatabase();
            broker = getBroker();

            String repoFileName = broker.serviceConnectionManager().getConnectionDescriptor().getJcdAlias();
            db.open(repoFileName, Database.OPEN_READ_WRITE);
            log.info("ODMG initialized.");
        }
        catch (ODMGException e)
        {
            String message = "Failed to initialize ODMG api";
            log.error(message, e);
            throw new InitializationException(message, e);
        }
        finally
        {
            releaseBroker(broker);
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
     * @see org.apache.jetspeed.services.perisistence.impl.AbstractOJBPersistencePlugin#postInit()
     */
    protected void postInit() throws InitializationException
    {
        initODMG();
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#update(java.lang.Object)
     */
    public void update(Object object)
    {

        try
        {

            // 1. Start the transaction
            TransactionImpl tx = (TransactionImpl) odmg.newTransaction();
            //  2. remove object from the OJB cache
            PersistenceBroker pb = tx.getBroker();
            //pb.removeFromCache(object);

            tx.begin();
            tx.markDirty(object);

            // 3. retreive a "stale" version of this object from the db
            Identity id = new Identity(object, pb);
            //Object staleObject = pb.getObjectByIdentity(id);
            // addObjectToTransaction(staleObject, tx, LOCK_LEVEL_WRITE);
            tx.lock(object, Transaction.WRITE);

            // 4. Map new values to the stale object
            //BeanUtils.copyProperties(object, object);

            // 5. Commit the transaction
            tx.commit();

        }
        catch (Throwable e)
        {
            log.error("Unexpected exception thrown while updating object instance", e);
        }

    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#delete(java.lang.Object)
     */
    public void delete(Object object)
    {
        //  PersistenceBroker pb1 = getBroker();
        PersistenceBroker pb = null;
        try
        {
            // 1. Start the transaction
            TransactionImpl tx = (TransactionImpl) odmg.newTransaction();

            // 2. remove object from the OJB cache

            tx.begin();
            pb = tx.getBroker();
            //pb.removeFromCache(object);
            //Identity id = new Identity(object, pb);
            // 3. retreive a "stale" version of this object from the db

            //Object staleObject = pb.getObjectByIdentity(id);
            tx.lock(object, Transaction.WRITE);
            db.deletePersistent(object);
            pb.removeFromCache(object);

            // 5. Commit the transaction
            tx.commit();

        }
        finally
        {
            releaseBroker(pb);
        }

    }

}
