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
