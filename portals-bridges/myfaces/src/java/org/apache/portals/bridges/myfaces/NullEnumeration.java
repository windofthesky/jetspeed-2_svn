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

/**
 * <p>
 * Enumeration without elements
 * </p>
 * <p>
 * Code provided by MyFaces project.
 * </p>
 * 
 * @author <a href="dlestrat@apache.org">David Le Strat </a>
 */
public final class NullEnumeration implements Enumeration
{
    /** Null enumeration. */
    private static final NullEnumeration nullEnumeration = new NullEnumeration();

    /**
     * @return An instance of the enumeration.
     */
    public static final NullEnumeration instance()
    {
        return nullEnumeration;
    }

    /**
     * @see java.util.Enumeration#hasMoreElements()
     */
    public boolean hasMoreElements()
    {
        return false;
    }

    /**
     * @see java.util.Enumeration#nextElement()
     */
    public Object nextElement()
    {
        throw new UnsupportedOperationException("NullEnumeration has no elements");
    }
}