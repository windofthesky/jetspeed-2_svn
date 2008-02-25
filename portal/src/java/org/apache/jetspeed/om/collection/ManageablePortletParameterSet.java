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
package org.apache.jetspeed.om.collection;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.BaseParameterSet;
import org.apache.jetspeed.om.common.extended.PortletParameterSet;
import org.apache.ojb.broker.ManageableCollection;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;

/**
 * @author Sweaver
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ManageablePortletParameterSet extends PortletParameterSet implements ManageableCollection
{

    /**
     * @param wrappedSet
     */
    public ManageablePortletParameterSet(Set wrappedSet)
    {
        super(wrappedSet);
    }

    /**
     * 
     */
    public ManageablePortletParameterSet()
    {
        super();
    }

    /**
      * @see org.apache.ojb.broker.ManageableCollection#afterStore(org.apache.ojb.broker.PersistenceBroker)
      */
    public void afterStore(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        // Nothin'

    }

    /**
     * @see org.apache.ojb.broker.ManageableCollection#ojbAdd(java.lang.Object)
     */
    public void ojbAdd(Object arg0)
    {
        add(arg0);

    }

    /**
     * @see org.apache.ojb.broker.ManageableCollection#ojbAddAll(org.apache.ojb.broker.ManageableCollection)
     */
    public void ojbAddAll(ManageableCollection arg0)
    {
        addAll((BaseParameterSet) arg0);
    }

    /**
     * @see org.apache.ojb.broker.ManageableCollection#ojbIterator()
     */
    public Iterator ojbIterator()
    {
        return iterator();
    }

    /**
     * @see org.apache.jetspeed.om.common.BaseParameterSet#getLog()
     */
    protected Log getLog()
    {

        return LogFactory.getLog(PortletParameterSet.class);
    }

}