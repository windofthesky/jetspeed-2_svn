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
 * BaseSecurityConstraint
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class BaseSecurityConstraint
{
    private int id;
    private int applyOrder;
    private List userPrincipals;
    private List rolePrincipals;
    private List groupPrincipals;
    private List permissions;

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
     * getUserPrincipals
     *
     * @return user principal names list
     */
    public List getUserPrincipals()
    {
        return userPrincipals;
    }

    /**
     * setUserPrincipals
     *
     * @param principals user principal names list
     */
    public void setUserPrincipals(List principals)
    {
        userPrincipals = principals;
    }

    /**
     * getRolePrincipals
     *
     * @return role principal names list
     */
    public List getRolePrincipals()
    {
        return rolePrincipals;
    }

    /**
     * setRolePrincipals
     *
     * @param principals role principal names list
     */
    public void setRolePrincipals(List principals)
    {
        rolePrincipals = principals;
    }

    /**
     * getGroupPrincipals
     *
     * @return group principal names list
     */
    public List getGroupPrincipals()
    {
        return groupPrincipals;
    }

    /**
     * setGroupPrincipals
     *
     * @param principals group principal names list
     */
    public void setGroupPrincipals(List principals)
    {
        groupPrincipals = principals;
    }

    /**
     * getPermissions
     *
     * @return permissions names list
     */
    public List getPermissions()
    {
        return permissions;
    }

    /**
     * setPermissions
     *
     * @param permissions permissions names list
     */
    public void setPermissions(List permissions)
    {
        this.permissions = permissions;
    }
}
