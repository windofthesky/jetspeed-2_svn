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

import java.util.List;

/**
 * BaseSecurityConstraintsRef
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class BaseSecurityConstraintsRef
{
    private int id;
    private int applyOrder;
    private String name;

    /**
     * getApplyOrder
     *
     * @return apply order for constraints
     */
    public int getApplyOrder()
    {
        return applyOrder;
    }

    /**
     * setApplyOrder
     *
     * @param order apply order for constraints
     */
    public void setApplyOrder(int order)
    {
        applyOrder = order;
    }

    /**
     * getName
     *
     * @return name of referenced constraint
     */
    public String getName()
    {
        return name;
    }

    /**
     * setName
     *
     * @param name name of referenced constraint
     */
    public void setName(String name)
    {
        this.name = name;
    }
}
