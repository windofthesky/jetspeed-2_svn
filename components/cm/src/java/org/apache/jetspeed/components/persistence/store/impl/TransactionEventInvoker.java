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
package org.apache.jetspeed.components.persistence.store.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.TransactionEventListener;

/**
 * <p>
 * TransactionEventInvoker
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public class TransactionEventInvoker
{
	
	protected List listeners;
	protected PersistenceStore store;
	
	public TransactionEventInvoker(List eventListeners, PersistenceStore store)
	{
		this.listeners = eventListeners;
		this.store = store;
	}

    /** 
     * <p>
     * afterCommit
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#afterCommit(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterCommit()
    {
        Iterator itr = listeners.iterator();
        while(itr.hasNext())
        {
        	TransactionEventListener tel = (TransactionEventListener) itr.next();
        	tel.afterCommit(new PersistenceStoreEventImpl(store));
        }

    }

    /** 
     * <p>
     * beforeCommit
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#beforeCommit(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeCommit()
    {
		Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			TransactionEventListener tel = (TransactionEventListener) itr.next();
			tel.beforeCommit(new PersistenceStoreEventImpl(store));
		}

    }

    /** 
     * <p>
     * afterRollback
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#afterRollback(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterRollback()
    {
		Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			TransactionEventListener tel = (TransactionEventListener) itr.next();
			tel.afterRollback(new PersistenceStoreEventImpl(store));
		}

    }

    /** 
     * <p>
     * beforeRollback
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#beforeRollback(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeRollback()
    {
        Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			TransactionEventListener tel = (TransactionEventListener) itr.next();
			tel.beforeRollback(new PersistenceStoreEventImpl(store));
		}

    }

    /** 
     * <p>
     * afterBegin
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#afterBegin(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterBegin()
    {
		Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			TransactionEventListener tel = (TransactionEventListener) itr.next();
			tel.afterBegin(new PersistenceStoreEventImpl(store));
		}

    }

    /** 
     * <p>
     * beforeBegin
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#beforeBegin(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeBegin()
    {
		Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			TransactionEventListener tel = (TransactionEventListener) itr.next();
			tel.beforeBegin(new PersistenceStoreEventImpl(store));
		}

    }

}
