/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.om.page;

/**
 * Simple interface for storing properties tied to Fragments in a page
 * description
 *
 * @version $Id$
 */
public interface Property extends Cloneable
{
    /**
     * Return the name of the layout element concerned by this
     * property.
     * Other layouts will not be able to see this property.
     *
     * @return name the name of the layout
     */
    public String getLayout();

    /**
     * Sets the name of the layout concerned by this property
     *
     * @param layoutName the name of a layout as defined in the
     * layout component repository
     */
    public void setLayout(String layoutName);

    /**
     * Returns the name of the property
     *
     * @return the name of the property as String
     */
    public String getName();

    /**
     * Sets the name of this property
     *
     * @param name the property name
     */
    public void setName( String name );

    /**
     * Return the value of the property encoded as a String
     *
     * @return the string value of the property
     */
    public String getValue();
    
    /**
     * 
     * <p>
     * getIntValue
     * </p>
     *
     * @return the string value of the property as an <code>int</code>
     */
    public int getIntValue();

    /**
     * Sets the value of the property
     *
     * @param value the value of the property
     */
    public void setValue( String value );

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException;
}