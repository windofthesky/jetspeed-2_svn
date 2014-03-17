/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.page.BaseElement;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintImpl;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.impl.DatabasePageManagerUtils;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.PermissionFactory;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.User;

import javax.security.auth.Subject;
import java.security.AccessController;
import java.security.Permission;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

/**
 * BaseElementImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class BaseElementImpl implements BaseElement
{
    private int id;
    private String name;
    private String title;
    private String shortTitle;
    private SecurityConstraintsImpl constraints;

    private boolean constraintsEnabled;
    private boolean permissionsEnabled;
    private PageManager pageManager;
    private volatile boolean stale;
    protected static PermissionFactory pf;
    
    public static void setPermissionsFactory(PermissionFactory pf)
    {
        BaseElementImpl.pf = pf;
    }

    protected BaseElementImpl(SecurityConstraintsImpl constraints)
    {
        this.constraints = constraints;
    }

    /**
     * getName
     *
     * @return element name
     */
    public String getName()
    {
        return name;
    }

    /**
     * setName
     *
     * @param name element name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * setConstraintsEnabled
     *
     * @param enabled enable/disable security constraints checks
     */
    public void setConstraintsEnabled(boolean enabled)
    {
        constraintsEnabled = enabled;
    }
    
    /**
     * setPermissionsEnabled
     *
     * @param enabled enable/disable security permissions checks
     */
    public void setPermissionsEnabled(boolean enabled)
    {
        permissionsEnabled = enabled;
    }
    
    /**
     * Infuses PageManager for use by this folder instance.
     *
     * @param pageManager page manager that manages this folder instance
     */
    public void setPageManager(PageManager pageManager)
    {
        this.pageManager = pageManager;
    }

    /**
     * Get infused or registered page manager instance managing this base element.
     * 
     * @return page manager instance
     */
    public PageManager getPageManager()
    {
        if (pageManager == null)
        {
            pageManager = Jetspeed.getComponentManager().lookupComponent("org.apache.jetspeed.page.PageManager");
        }        
        return pageManager;
    }     

    /**
     * Set stale status of this object.
     * 
     * @param stale stale flag status
     */
    public void setStale(boolean stale)
    {
        this.stale = stale;
    }
    
    /**
     * grantViewActionAccess
     *
     * @return granted access for view action
     */
    public boolean grantViewActionAccess()
    {
        // by default, access must be checked
        return false;
    }

    /**
     * getEffectivePageSecurity
     *
     * @return effective page security object
     */
    public PageSecurity getEffectivePageSecurity()
    {
        // no page security available by default
        return null;
    }

    /**
     * checkConstraints
     *
     * Check fully parameterized principal against specified security constraint scope.
     *
     * @param actions actions to check
     * @param userPrincipals principal users list
     * @param rolePrincipals principal roles list
     * @param groupPrincipals principal group list
     * @param checkNodeOnly check node scope only
     * @param checkParentsOnly check parent folder scope only
     * @throws SecurityException
     */
    public void checkConstraints(List<String> actions, List<String> userPrincipals, List<String> rolePrincipals, List<String> groupPrincipals, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // check node constraints if available
        if ((constraints != null) && !constraints.isEmpty())
        {
            constraints.checkConstraints(actions, userPrincipals, rolePrincipals, groupPrincipals, getEffectivePageSecurity());
        }
    }

    /**
     * getLogicalPermissionPath
     *
     * @return path used for permissions checks
     */
    public String getLogicalPermissionPath()
    {
        // same as physical path by default
        return getPhysicalPermissionPath();
    }

    /**
     * getPhysicalPermissionPath
     *
     * @return path used for permissions checks
     */
    public String getPhysicalPermissionPath()
    {
        // no permissions path available by default
        return null;
    }

    /**
     * checkPermissions
     *
     * @param mask mask of actions to check
     * @param checkNodeOnly check node scope only
     * @param checkParentsOnly check parent folder scope only
     * @throws SecurityException
     */
    public void checkPermissions(int mask, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // check page and folder permissions
        String physicalPermissionPath = getPhysicalPermissionPath();
        if (physicalPermissionPath != null)
        {
            // check permissions using physical path
            try
            {
                checkPermissions(physicalPermissionPath, mask, checkNodeOnly, checkParentsOnly);
            }
            catch (SecurityException physicalSE)
            {
                // fallback check using logical path if available and different
                String logicalPermissionPath = getLogicalPermissionPath();
                if ((logicalPermissionPath != null) && !logicalPermissionPath.equals(physicalPermissionPath))
                {
                    checkPermissions(logicalPermissionPath, mask, checkNodeOnly, checkParentsOnly);
                }
                else
                {
                    throw physicalSE;
                }
            }
        }
    }

    /**
     * checkPermissions
     *
     * @param path permissions path to check
     * @param mask mask of actions to check
     * @param checkNodeOnly check node scope only
     * @param checkParentsOnly check parent folder scope only
     * @throws SecurityException
     */
    public void checkPermissions(String path, int mask, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // check actions permissions
        try
        {
            // check for granted page permissions
            AccessController.checkPermission((Permission)pf.newPermission(pf.PAGE_PERMISSION, path, mask));
        }
        catch (SecurityException se)
        {
            // fallback check for granted folder permissions
            AccessController.checkPermission((Permission)pf.newPermission(pf.FOLDER_PERMISSION, path, mask));
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getConstraintsEnabled()
     */
    public boolean getConstraintsEnabled()
    {
        return constraintsEnabled;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        // compare element by id
        return ((o != null) && getClass().equals(o.getClass()) && (id != 0) && (id == ((BaseElementImpl)o).id));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        // use id to generate hashCode
        return id;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getSecurityConstraints()
     */
    public SecurityConstraints getSecurityConstraints()
    {
        return constraints;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#newSecurityConstraints()
     */
    public SecurityConstraints newSecurityConstraints()
    {
        // return universal security constraints instance
        // since object members are copied on assignment to
        // maintain persistent collection members
        return new SecurityConstraintsImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#newSecurityConstraint()
     */
    public SecurityConstraint newSecurityConstraint()
    {
        // return constraints specific security constraint instance
        if ((constraints != null) && (constraints.getSecurityConstraintClass() != null))
        {
            try
            {
                return (SecurityConstraintImpl)constraints.getSecurityConstraintClass().newInstance();
            }
            catch (InstantiationException ie)
            {
                throw new ClassCastException("Unable to create security constraint instance: " + constraints.getSecurityConstraintClass().getName() + ", (" + ie + ").");
            }
            catch (IllegalAccessException iae)
            {
                throw new ClassCastException("Unable to create security constraint instance: " + constraints.getSecurityConstraintClass().getName() + ", (" + iae + ").");
            }
        }
        // return universal security constraint instance
        return new SecurityConstraintImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#setSecurityConstraints(org.apache.jetspeed.om.common.SecurityConstraints)
     */
    public void setSecurityConstraints(SecurityConstraints constraints)
    {
        if (this.constraints != null)
        {
            // set constraints configuration in nested om implementation instance
            this.constraints.setOwner(constraints.getOwner());
            this.constraints.setSecurityConstraints(constraints.getSecurityConstraints());
            this.constraints.setSecurityConstraintsRefs(constraints.getSecurityConstraintsRefs());
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#checkConstraints(java.lang.String)
     */
    public void checkConstraints(String actions) throws SecurityException
    {
        // skip checks if not enabled
        if (!getConstraintsEnabled())
        {
            return;
        }

        // validate specified actions
        if (actions == null)
        {
            throw new SecurityException("BaseElementImpl.checkConstraints(): No actions specified.");
        }

        // get action names lists; separate view and other
        // actions to mimic file system permissions logic
        List<String> viewActionList = SecurityConstraintImpl.parseCSVList(actions);
        List<String> otherActionsList = null;
        if (viewActionList.size() == 1)
        {
            if (!viewActionList.contains(JetspeedActions.VIEW))
            {
                otherActionsList = viewActionList;
                viewActionList = null;
            }
        }
        else
        {
            otherActionsList = viewActionList;
            viewActionList = null;
            if (otherActionsList.remove(JetspeedActions.VIEW))
            {
                viewActionList = DatabasePageManagerUtils.createList();
                viewActionList.add(JetspeedActions.VIEW);
            }
        }

        // get current request context subject
        Subject subject = JSSubject.getSubject(AccessController.getContext());
        if (subject == null)
        {
            throw new SecurityException("BaseElementImpl.checkConstraints(): Missing JSSubject.");
        }

        // get user/group/role principal names
        List<String> userPrincipals = null;
        List<String> rolePrincipals = null;
        List<String> groupPrincipals = null;
        for (Principal principal : subject.getPrincipals())
        {
            if (principal instanceof User)
            {
                if (userPrincipals == null)
                {
                    userPrincipals = new LinkedList<String>();
                }
                userPrincipals.add(principal.getName());
            }
            else if (principal instanceof Role)
            {
                if (rolePrincipals == null)
                {
                    rolePrincipals = new LinkedList<String>();
                }
                rolePrincipals.add(principal.getName());
            }
            else if (principal instanceof Group)
            {
                if (groupPrincipals == null)
                {
                    groupPrincipals = new LinkedList<String>();
                }
                groupPrincipals.add(principal.getName());
            }
        }

        // check constraints using parsed action and access lists
        if (viewActionList != null)
        {
            checkConstraints(viewActionList, userPrincipals, rolePrincipals, groupPrincipals, false, grantViewActionAccess());
        }
        if (otherActionsList != null)
        {
            checkConstraints(otherActionsList, userPrincipals, rolePrincipals, groupPrincipals, true, false);
        }
    }

    /**
     * resetCachedSecurityConstraints
     */
    public void resetCachedSecurityConstraints()
    {
        // propagate to constraints
        if (constraints != null)
        {
            constraints.resetCachedSecurityConstraints();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getPermissionsEnabled()
     */
    public boolean getPermissionsEnabled()
    {
        return permissionsEnabled;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#checkPermissions(java.lang.String)
     */
    public void checkPermissions(int mask) throws SecurityException
    {
        // skip checks if not enabled
        if (!getPermissionsEnabled())
        {
            return;
        }

        // separate view and other actions to mimic file system permissions logic
        boolean viewAction = (mask & JetspeedActions.MASK_VIEW) == JetspeedActions.MASK_VIEW;
        int otherMask = mask & ~JetspeedActions.MASK_VIEW;

        // check permissions using parsed actions
        if (viewAction)
        {
            checkPermissions(JetspeedActions.MASK_VIEW, false, grantViewActionAccess());
        }
        if (otherMask != 0)
        {
            checkPermissions(otherMask, true, false);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#checkAccess(java.lang.String)
     */
    public void checkAccess(String actions) throws SecurityException
    {
        // check access permissions and constraints as enabled
        if (getPermissionsEnabled())
        {
            int mask = pf.parseActions(actions);
            checkPermissions(mask);
        }
        if (getConstraintsEnabled())
        {
            checkConstraints(actions);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#getId()
     */
    public String getId()
    {
        // by default, return identity
        return Integer.toString(getIdentity());
    }
    
    /**
     * Get implementation identity key.
     * 
     * @return identity key.
     */
    public int getIdentity()
    {
        return id;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#getTitle()
     */
    public String getTitle()
    {
        return title;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#setTitle(java.lang.String)
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#getShortTitle()
     */
    public String getShortTitle()
    {
        return shortTitle;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#setShortTitle(java.lang.String)
     */
    public void setShortTitle(String shortTitle)
    {
        this.shortTitle = shortTitle;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#isStale()
     */
    public boolean isStale()
    {
        return stale;
    }
}
