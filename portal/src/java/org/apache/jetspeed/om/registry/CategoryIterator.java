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
package org.apache.jetspeed.om.registry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedMap;


/**
 * CategoryIterator - iterators over category treemap/hashmap allowing dups
 * 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class CategoryIterator implements Iterator
{
    protected SortedMap map = null;
    protected String key;
    protected Iterator mapIterator = null;
    protected Iterator bucketIterator = null;
    protected boolean iteratingMaps = true;
    protected HashMap bucket = null;
    protected PortletEntry portlet = null;
    protected boolean findall = false;
    protected String category = "";
    protected String group = "";

    public String getCategory()
    {
        return category;
    }

    public String getGroup()
    {
        return group;
    }

    public CategoryIterator(SortedMap map, String key)
    {
        this.map = map;
        this.key = key;
        findall = (this.key == null || this.key.equals(""));
        if (findall)
            this.map = map;
        else
            this.map = map.tailMap(key);
        this.mapIterator = this.map.entrySet().iterator();
        this.bucketIterator = null;
        this.bucket = null;
    }

    private CategoryIterator() 
    {}

    public boolean hasNext()
    {
        if (iteratingMaps) 
        {      
            if (mapIterator.hasNext() == false)
                return false; 
            return filter();
        }

        if (bucketIterator.hasNext())
            return getPortletEntry();

        // reached end of bucket, try next map
        if (mapIterator.hasNext())
        {
            return filter();
        }
        return false; //reached end of maps
    }

    protected boolean filter()
    {
        java.util.Map.Entry entry = (java.util.Map.Entry)mapIterator.next();
        String entryKey = (String)entry.getKey();
        int pos = entryKey.indexOf('.');
        this.category = "";
        if (-1 == pos)
        {
            this.group = entryKey;
        }
        else
        {
            this.group = entryKey.substring(0, pos);
            int length = entryKey.length();
            if (length > pos + 1)
                this.category = entryKey.substring(pos + 1, length);
        }

        if (!findall && !entryKey.startsWith(this.key))
            return false; // end of criteria

        bucket = (HashMap)entry.getValue();

        bucketIterator = bucket.entrySet().iterator();
        iteratingMaps = false;
        if (bucketIterator.hasNext() == false)
            return false;
        return getPortletEntry();
    }


    protected boolean getPortletEntry()
    {
        java.util.Map.Entry entry = (java.util.Map.Entry)bucketIterator.next();
        if (null == entry)
            return false;

        this.portlet = (PortletEntry)entry.getValue();

        return true;
    }

    public void remove() throws IllegalStateException, UnsupportedOperationException
    {
        throw new UnsupportedOperationException("The remove() method is not supported");
    }


    public Object next() throws NoSuchElementException
    {       
        return portlet;
    }

}
