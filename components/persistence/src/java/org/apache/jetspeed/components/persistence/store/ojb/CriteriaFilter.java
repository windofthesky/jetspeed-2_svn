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
package org.apache.jetspeed.components.persistence.store.ojb;

import java.util.Collection;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.ojb.broker.query.Criteria;

/**
 *
 * Wraps an <code>org.apache.ojb.broker.query.Criteria</code> object
 * to provide the required functionallity for implementing <code>LookupCriteria</code>
 * interface. 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class CriteriaFilter implements Filter
{
    private Criteria ojbCriteria;

    public CriteriaFilter()
    {
        ojbCriteria = new Criteria();
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addBetween(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void addBetween(String arg0, Object arg1, Object arg2)
    {

        ojbCriteria.addBetween(arg0, arg1, arg2);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addEqualTo(java.lang.String, java.lang.Object)
     */
    public void addEqualTo(String arg0, Object arg1)
    {
        ojbCriteria.addEqualTo(arg0, arg1);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addGreaterOrEqualThan(java.lang.String, java.lang.Object)
     */
    public void addGreaterOrEqualThan(String arg0, Object arg1)
    {
        ojbCriteria.addGreaterOrEqualThan(arg0, arg1);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addGreaterThan(java.lang.String, java.lang.Object)
     */
    public void addGreaterThan(String arg0, Object arg1)
    {
        ojbCriteria.addGreaterThan(arg0, arg1);
    }
    
    /**
     * @see org.apache.ojb.broker.query.Criteria#addIn(java.lang.String, java.util.Collection)
     */      
    public void addIn(String arg0, Collection arg1)
    {
        ojbCriteria.addIn(arg0, arg1);
    }
    
    /**
     * @see org.apache.ojb.broker.query.Criteria#addLessOrEqualThan(java.lang.String, java.lang.Object)
     */
    public void addLessOrEqualThan(String arg0, Object arg1)
    {
        ojbCriteria.addLessOrEqualThan(arg0, arg1);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addLike(java.lang.Object, java.lang.Object)
     */
    public void addLike(Object arg0, Object arg1)
    {
        ojbCriteria.addLike((String)arg0, arg1);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addNotBetween(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void addNotBetween(String arg0, Object arg1, Object arg2)
    {
        ojbCriteria.addNotBetween(arg0, arg1, arg2);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addNotEqualTo(java.lang.String, java.lang.Object)
     */
    public void addNotEqualTo(String arg0, Object arg1)
    {
        ojbCriteria.addNotEqualTo(arg0, arg1);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addNotLike(java.lang.String, java.lang.Object)
     */
    public void addNotLike(String arg0, Object arg1)
    {
        ojbCriteria.addNotLike(arg0, arg1);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addNotNull(java.lang.String)
     */
    public void addNotNull(String arg0)
    {
        ojbCriteria.addNotNull(arg0);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addOrCriteria(org.apache.ojb.broker.query.Criteria)
     */
    public void addOrFilter(Filter arg0)
    {
        ojbCriteria.addOrCriteria(((CriteriaFilter)arg0).getOjbCriteria());
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addOrderByAscending(java.lang.String)
     */
    public void addOrderByAscending(String arg0)
    {
        ojbCriteria.addOrderByAscending(arg0);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addOrderByDescending(java.lang.String)
     */
    public void addOrderByDescending(String arg0)
    {
        ojbCriteria.addOrderByDescending(arg0);
    }

    public Criteria getOjbCriteria()
    {
        return ojbCriteria;
    }
}
