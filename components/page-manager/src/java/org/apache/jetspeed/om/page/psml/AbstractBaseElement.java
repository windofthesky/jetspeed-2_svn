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

package org.apache.jetspeed.om.page.psml;

import java.security.AccessController;
import java.security.Principal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.page.BaseElement;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.security.FolderPermission;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.PagePermission;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.UserPrincipal;


/**
 *
 * @version $Id$
 */
public abstract class AbstractBaseElement implements java.io.Serializable, SecuredResource
{
    private final static Log log = LogFactory.getLog(AbstractBaseElement.class);

    private String id = null;

    private String title = null;

    private String shortTitle = null;

    private boolean constraintsEnabled;

    private SecurityConstraints constraints = null;

    private boolean permissionsEnabled;
    
    private DocumentHandlerFactory handlerFactory = null;

    public String getId()
    {
         return this.id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * <p>
     * getTitle
     * </p>
     *
     * @see org.apache.jetspeed.om.page.BaseElement#getTitle()
     * @return
     */
    public String getTitle()
    {
        return this.title;
    }
    /**
     * <p>
     * setTitle
     * </p>
     *
     * @see org.apache.jetspeed.om.page.BaseElement#setTitle(java.lang.String)
     * @param title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }
    /**
     * <p>
     * getShortTitle
     * </p>
     *
     * @see org.apache.jetspeed.om.page.BaseElement#getShortTitle()
     * @return
     */
    public String getShortTitle()
    {
        // default to title if not specified
        String title = this.shortTitle;
        if (title == null)
        {
            title = this.title;
        }
        return title;
    }
    /**
     * <p>
     * setShortTitle
     * </p>
     *
     * @see org.apache.jetspeed.om.page.BaseElement#setShortTitle(java.lang.String)
     * @param title
     */
    public void setShortTitle(String title)
    {
        this.shortTitle = title;
    }

    /**
     * <p>
     * getConstraintsEnabled
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecureResource#getConstraintsEnabled()
     * @return
     */
    public boolean getConstraintsEnabled()
    {
        return constraintsEnabled;
    }

    /**
     * <p>
     * setConstraintsEnabled
     * </p>
     *
     * @param enabled indicator
     */
    public void setConstraintsEnabled(boolean enabled)
    {
        constraintsEnabled = enabled;
    }

    /**
     * <p>
     * getSecurityConstraints
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecureResource#getSecurityConstraints()
     * @return
     */
    public SecurityConstraints getSecurityConstraints()
    {
        return constraints;
    }
    
    /**
     * <p>
     * setSecurityConstraints
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecureResource#setSecurityConstraints(org.apache.jetspeed.om.common.SecurityConstraints)
     * @param constraints
     */
    public void setSecurityConstraints(SecurityConstraints constraints)
    {
        this.constraints = constraints;
    }

    /**
     * <p>
     * checkConstraints
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecureResource#checkConstraints(java.lang.String)
     * @param actions
     * @throws SecurityException
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
            throw new SecurityException("AbstractBaseElement.checkConstraints(): No actions specified.");
        }

        // get action names list
        List actionsList = SecurityConstraintImpl.parseCSVList(actions);


        // get current request context subject
        Subject subject = Subject.getSubject(AccessController.getContext());
        if (subject == null)
        {
            throw new SecurityException("AbstractBaseElement.checkConstraints(): Missing Subject.");
        }

        // get user/group/role principal names
        List userPrincipals = null;
        List rolePrincipals = null;
        List groupPrincipals = null;
        Iterator principals = subject.getPrincipals().iterator();
        while (principals.hasNext())
        {
            Principal principal = (Principal) principals.next();
            if (principal instanceof UserPrincipal)
            {
                if (userPrincipals == null)
                {
                    userPrincipals = new LinkedList();
                }
                userPrincipals.add(principal.getName());
            }
            else if (principal instanceof RolePrincipal)
            {
                if (rolePrincipals == null)
                {
                    rolePrincipals = new LinkedList();
                }
                rolePrincipals.add(principal.getName());
            }
            else if (principal instanceof GroupPrincipal)
            {
                if (groupPrincipals == null)
                {
                    groupPrincipals = new LinkedList();
                }
                groupPrincipals.add(principal.getName());
            }
        }

        // check constraints using parsed and accessed lists
        checkConstraints(actionsList, userPrincipals, rolePrincipals, groupPrincipals);
    }

    /**
     * <p>
     * checkConstraints
     * </p>
     *
     * @param actions
     * @param userPrincipals
     * @param rolePrincipals
     * @param groupPrincipals
     * @throws SecurityException
     */
    public void checkConstraints(List actions, List userPrincipals, List rolePrincipals, List groupPrincipals) throws SecurityException
    {
        // check constraints if available
        SecurityConstraints constraints = getSecurityConstraints();
        if (constraints != null)
        {
            ((SecurityConstraintsImpl)constraints).checkConstraints(actions, userPrincipals, rolePrincipals, groupPrincipals, handlerFactory);
        }
    }

    /**
     * <p>
     * getPermissionsEnabled
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecureResource#getPermissionsEnabled()
     * @return
     */
    public boolean getPermissionsEnabled()
    {
        return permissionsEnabled;
    }

    /**
     * <p>
     * setPermissionsEnabled
     * </p>
     *
     * @param enabled indicator
     */
    public void setPermissionsEnabled(boolean enabled)
    {
        permissionsEnabled = enabled;
    }

    /**
     * <p>
     * checkPermissions
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecureResource#checkPermissions(java.lang.String)
     * @param actions
     * @throws SecurityException
     */
    public void checkPermissions(String actions) throws SecurityException
    {
        // skip checks if not enabled
        if (!getPermissionsEnabled())
        {
            return;
        }

        // check page permissions and fallback to folder permissions
        // if permission paths available for this element
        String physicalPermissionPath = getPhysicalPermissionPath();
        if (physicalPermissionPath != null)
        {
            // check permission using physical path
            String permissionPath = physicalPermissionPath;
            try
            {
                try
                {
                    PagePermission permission = new PagePermission(permissionPath, actions);
                    AccessController.checkPermission(permission);
                }
                catch (SecurityException se)
                {
                    FolderPermission permission = new FolderPermission(permissionPath, actions);
                    AccessController.checkPermission(permission);
                }
            }
            catch (SecurityException physicalSE)
            {
                // fallback check using logical path if available and different
                String logicalPermissionPath = getLogicalPermissionPath();
                if ((logicalPermissionPath != null) && !logicalPermissionPath.equals(physicalPermissionPath))
                {
                    permissionPath = logicalPermissionPath;
                    try
                    {
                        PagePermission permission = new PagePermission(permissionPath, actions);
                        AccessController.checkPermission(permission);
                    }
                    catch (SecurityException se)
                    {
                        FolderPermission permission = new FolderPermission(permissionPath, actions);
                        AccessController.checkPermission(permission);
                    }
                }
                else
                {
                    throw physicalSE;
                }
            }
        }
    }

    /**
     * <p>
     * getLogicalPermissionPath
     * </p>
     *
     * @return path used for permissions checks
     */
    public String getLogicalPermissionPath()
    {
        return getPhysicalPermissionPath();
    }

    /**
     * <p>
     * getPhysicalPermissionPath
     * </p>
     *
     * @return path used for permissions checks
     */
    public String getPhysicalPermissionPath()
    {
        // no permissions path available by default
        log.warn("getPhysicalPermissionPath(): no permission path available for " + this + " element.");
        return null;
    }

    /**
     * <p>
     * checkAccess
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecureResource#checkAccess(java.lang.String)
     * @param actions
     * @throws SecurityException
     */
    public void checkAccess(String actions) throws SecurityException
    {
        // check access permissions and constraints as enabled
        if (getPermissionsEnabled())
        {
            checkPermissions(actions);
        }
        if (getConstraintsEnabled())
        {
            checkConstraints(actions);
        }
    }

    /**
     * <p>
     * getHandlerFactory
     * </p>
     *
     * @return element handler factory
     */
    public DocumentHandlerFactory getHandlerFactory()
    {
        return handlerFactory;
    }

    /**
     * <p>
     * setHandlerFactory
     * </p>
     *
     * @param factory element handler factory
     */
    public void setHandlerFactory(DocumentHandlerFactory factory)
    {
        this.handlerFactory = factory;
    }

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        Object cloned = super.clone();
        return cloned;
    }

    /**
     * <p>
     * equals
     * </p>
     *
     * @see java.lang.Object#equals(java.lang.Object)
     * @param obj
     * @return
     */
    public boolean equals( Object obj )
    {
        if(obj instanceof BaseElement)
        {
            AbstractBaseElement element = (AbstractBaseElement) obj;
            return id != null && element.getId() != null && id.equals(element.getId());            
        }
        else
        {
            return false;
        }
    }
    
    /**
     * <p>
     * hashCode
     * </p>
     *
     * @see java.lang.Object#hashCode()
     * @return
     */
    public int hashCode()
    {
        return id.hashCode();
    }
    
    /**
     * <p>
     * toString
     * </p>
     *
     * @see java.lang.Object#toString()
     * @return
     */
    public String toString()
    {      
        return getId();
    }
}
