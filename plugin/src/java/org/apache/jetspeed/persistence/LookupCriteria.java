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
package org.apache.jetspeed.persistence;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;

/**
 * 
 * LookupCriteria
 * 
 * Provides a simple selection criteria based on the property values of a single object.
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface LookupCriteria
{
    /**
     * @see org.apache.ojb.broker.query.Criteria#addBetween(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public abstract void addBetween(String arg0, Object arg1, Object arg2);
    /**
     * @see org.apache.ojb.broker.query.Criteria#addEqualTo(java.lang.String, java.lang.Object)
     */
    public abstract void addEqualTo(String arg0, Object arg1);
    /**
     * @see org.apache.ojb.broker.query.Criteria#addGreaterOrEqualThan(java.lang.String, java.lang.Object)
     */
    public abstract void addGreaterOrEqualThan(String arg0, Object arg1);
    /**
     * @see org.apache.ojb.broker.query.Criteria#addGreaterThan(java.lang.String, java.lang.Object)
     */
    public abstract void addGreaterThan(String arg0, Object arg1);
    /**
     * @see org.apache.ojb.broker.query.Criteria#addIn(java.lang.String, java.util.Collection)
     */      
    public abstract void addIn(String attribute, Collection values);
    /**
     * @see org.apache.ojb.broker.query.Criteria#addLessOrEqualThan(java.lang.String, java.lang.Object)
     */
    public abstract void addLessOrEqualThan(String arg0, Object arg1);
    /**
     * @see org.apache.ojb.broker.query.Criteria#addLike(java.lang.String, java.lang.Object)
     */
    public abstract void addLike(String arg0, Object arg1);
    /**
     * @see org.apache.ojb.broker.query.Criteria#addNotBetween(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public abstract void addNotBetween(String arg0, Object arg1, Object arg2);
    /**
     * @see org.apache.ojb.broker.query.Criteria#addNotEqualTo(java.lang.String, java.lang.Object)
     */
    public abstract void addNotEqualTo(String arg0, Object arg1);
    /**
     * @see org.apache.ojb.broker.query.Criteria#addNotLike(java.lang.String, java.lang.Object)
     */
    public abstract void addNotLike(String arg0, Object arg1);
    /**
     * @see org.apache.ojb.broker.query.Criteria#addNotNull(java.lang.String)
     */
    public abstract void addNotNull(String arg0);
    /**
     * @see org.apache.ojb.broker.query.Criteria#addOrCriteria(org.apache.ojb.broker.query.Criteria)
     */
    public abstract void addOrCriteria(Criteria arg0);
    /**
     * @see org.apache.ojb.broker.query.Criteria#addOrderByAscending(java.lang.String)
     */
    public abstract void addOrderByAscending(String arg0);
    /**
     * @see org.apache.ojb.broker.query.Criteria#addOrderByDescending(java.lang.String)
     */
    public abstract void addOrderByDescending(String arg0);

}