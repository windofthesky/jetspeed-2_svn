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
package org.apache.jetspeed.persistence.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.persistence.TransactionStateException;
import org.apache.ojb.broker.PersistenceBroker;

/**
 * This plugin uses OJB with straight peristence broker to 
 * provide persistence operations.  transaction support 
 * is provided straight through RDBMS. 
 * 
 * @deprecated Currently unimplemented.  DO NOT USE!!!  
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a> 
 */
public class OJBPBPersistencePlugin extends AbstractOJBPersistencePlugin
{

    protected ThreadLocal TLdeletes = new ThreadLocal();
    protected ThreadLocal TLupdates = new ThreadLocal();
    protected ThreadLocal TLpb = new ThreadLocal();

    /**
     * @see org.apache.jetspeed.services.perisistence.impl.AbstractOJBPersistencePlugin#postInit()
     */
    protected void postInit() throws InitializationException
    {
        // nothing needs to be done
    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#beginTransaction()
     */
    public void beginTransaction() throws TransactionStateException
    {
        super.beginTransaction();
        getBroker().beginTransaction();
    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#commitTransaction()
     */
    public void commitTransaction() throws TransactionStateException
    {
        PersistenceBroker pb = getBroker();

		Collection updates = (Collection) TLupdates.get();
        if (updates != null)
        {
            Iterator itr = updates.iterator();
            while (itr.hasNext())
            {
                pb.store(itr.next());
            }
        }

		Collection deletes = (Collection) TLdeletes.get();
        if (deletes != null)
        {
            Iterator itr = deletes.iterator();
            while (itr.hasNext())
            {
                pb.delete(itr.next());
            }
        }

        pb.commitTransaction();
		super.commitTransaction();
        clearTx();

    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#markDirty(java.lang.Object)
     */
    public Object markDirty(Object obj) throws TransactionStateException
    {
        prepareForUpdate(obj);
        return obj;
    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#prepareForDelete(java.lang.Object)
     */
    public void prepareForDelete(Object obj) throws TransactionStateException
    {
        HashSet deletes = (HashSet) TLdeletes.get();
        if (deletes == null)
        {
            deletes = new HashSet();
            TLdeletes.set(deletes);
        }

        deletes.add(obj);
    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#prepareForUpdate(java.lang.Object)
     */
    public void prepareForUpdate(Object obj) throws TransactionStateException
    {
        List updates = (List) TLupdates.get();
        if (updates == null)
        {
            updates = new ArrayList();
            TLupdates.set(updates);
        }

        updates.add(obj);

    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#rollbackTransaction()
     */
    public void rollbackTransaction() throws TransactionStateException
    {
        PersistenceBroker pb = getBroker();
        try
        {
            pb.abortTransaction();
        }
        finally
        {
        	super.rollbackTransaction();
            clearTx();
        }

    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#makePersistent(java.lang.Object)
     */
    public void makePersistent(Object obj) throws TransactionStateException
    {
        prepareForUpdate(obj);
    }

    protected void clearTx()
    {
		Collection updates = (Collection) TLupdates.get();
        if (updates != null)
        {
            updates.clear();
        }
		Collection deletes = (Collection) TLdeletes.get();
        if (deletes != null)
        {
            deletes.clear();
        }

    }

}
