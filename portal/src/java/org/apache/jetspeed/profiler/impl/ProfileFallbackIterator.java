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
package org.apache.jetspeed.profiler.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.rules.RuleCriterion;

/**
 * ProfileFallbackIterator
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ProfileFallbackIterator implements Iterator
{
    private ProfileLocatorControl locator;
    private int last = 0;
    private int state = RuleCriterion.FALLBACK_CONTINUE;     
    private ProfileFallbackIterator()
    {
    }
    
    public ProfileFallbackIterator(ProfileLocatorControl locator)
    {
        this.locator = locator;
        last = locator.getElements().size() - 1;
    }
    
    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove()
    {
        // TODO Auto-generated method stub
    }
    
    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        boolean hasNext = false;
        
        List elements = locator.getElements();
        
        if (last < 0 || last >= elements.size())
        {
            state = RuleCriterion.FALLBACK_STOP;
            return false;
        }
        
        if (state == RuleCriterion.FALLBACK_STOP)
        {
            hasNext = false;
        }        
        else if (state == RuleCriterion.FALLBACK_CONTINUE ||
                 state == RuleCriterion.FALLBACK_LOOP)
        {
            hasNext = true;
        }
        
        ProfileLocatorPropertyImpl element = (ProfileLocatorPropertyImpl)elements.get(last);
        state = element.getFallbackType();
                
        return hasNext;
    }
    
    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Object next()
    {
        List elements = locator.getElements();
                
        StringBuffer key = new StringBuffer();
        Iterator it = elements.listIterator();
        int count = 0;
        while (it.hasNext())
        {
            if (count > 0)
            {
                key.append(ProfileLocator.PATH_SEPARATOR);
            }
            ProfileLocatorPropertyImpl element = (ProfileLocatorPropertyImpl)it.next();
            key.append(element.getName());
            key.append(ProfileLocator.PATH_SEPARATOR);
            key.append(element.getValue());
            if (count >= last)
            {
                break;
            }
            count++;            
        }
        last--;
        return key.toString();
    }
}
