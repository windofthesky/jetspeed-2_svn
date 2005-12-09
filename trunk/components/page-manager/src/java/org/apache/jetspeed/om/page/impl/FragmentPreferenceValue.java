/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.jetspeed.om.page.impl;

/**
 * FragmentPreferenceValue
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class FragmentPreferenceValue
{
    private int id;
    private int valueOrder;
    private String value;

    /**
     * getValueOrder
     *
     * @return value order
     */
    public int getValueOrder()
    {
        return valueOrder;
    }

    /**
     * setValueOrder
     *
     * @param order value order
     */
    public void setValueOrder(int order)
    {
        valueOrder = order;
    }

    /**
     * getValue
     *
     * @return preference value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * setValue
     *
     * @param value preference value
     */
    public void setValue(String value)
    {
        this.value = value;
    }
}
