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
package org.apache.jetspeed.services.perisistence.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.fulcrum.InitializationException;
import org.apache.ojb.broker.PersistenceBroker;

/**
 * This plugin uses OJB with straight peristence broker to 
 * provide persistence operations.  transaction support 
 * is provided straight through RDBMS. 
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a> 
 */

/**
 * 
 * OJBPBPersistencePlugin
 * <br/>
 * This plugin uses OJB with straight peristence broker to 
 * provide persistence operations.  transaction support 
 * is provided straight through RDBMS. 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */

public class OJBPBPersistencePlugin extends AbstractOJBPersistencePlugin
{

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#abortTransaction(java.lang.Object)
     */
    public void abortTransaction(Object transaction)
    {
        PersistenceBroker pb = getBroker();
        try
        {
            transaction = null;
            pb.abortTransaction();
        }
        finally
        {
            releaseBroker(pb);
        }

    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#addObjectToTransaction(java.lang.Object, java.lang.Object, int)
     */
    public void addObjectToTransaction(Object object, Object transaction, int lockLevel)
    {
        HashMap transactions = (HashMap) transaction;
        ArrayList list = (ArrayList) transactions.get("add");
        list.add(object);
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#commitTransaction(java.lang.Object)
     */
    public void commitTransaction(Object transaction)
    {

        HashMap transactions = (HashMap) transaction;
        PersistenceBroker pb = (PersistenceBroker) transactions.get("pb");
        Iterator deletes = ((ArrayList) transactions.get("delete")).iterator();
        Iterator adds = ((ArrayList) transactions.get("add")).iterator();
        try
        {
            // process update/add operations
            while (adds.hasNext())
            {
                pb.store(adds.next());
            }

            // process delete operations
            while (deletes.hasNext())
            {
                pb.delete(deletes.next());
            }

            pb.commitTransaction();
        }
        finally
        {
            releaseBroker(pb);
        }

    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#setObjectForDeletion(java.lang.Object, java.lang.Object)
     */
    public void setObjectForDeletion(Object object, Object transaction)
    {
        HashMap transactions = (HashMap) transaction;
        ArrayList list = (ArrayList) transactions.get("delete");
        list.add(object);
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#startTransaction()
     */
    public Object startTransaction()
    {
        try
        {
            HashMap transactions = new HashMap(2);
            transactions.put("add", new ArrayList());
            transactions.put("delete", new ArrayList());
            PersistenceBroker pb = getBroker();
            pb.beginTransaction();
            transactions.put("pb", pb);
            return transactions;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JetspeedOJBRuntimeException(e);
        }
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.impl.AbstractOJBPersistencePlugin#postInit()
     */
    protected void postInit() throws InitializationException
    {
        // nothing needs to be done
    }

}