/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.portals.bridges.myfaces;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * <p>
 * Code borrowed from myfaces.
 * </p>
 */
public class EnumerationIterator implements Iterator
{
    private Enumeration enumeration;

    public EnumerationIterator(Enumeration enumeration)
    {
        this.enumeration = enumeration;
    }

    public boolean hasNext()
    {
        return this.enumeration.hasMoreElements();
    }

    public Object next()
    {
        return this.enumeration.nextElement();
    }

    public void remove()
    {
        throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
    }
}