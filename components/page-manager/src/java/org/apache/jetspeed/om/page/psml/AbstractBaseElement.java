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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.page.BaseElement;
import org.apache.jetspeed.om.page.SecurityConstraintImpl;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.psml.NodeSetImpl;
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
     * newSecurityConstraints
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecureResource#newSecurityConstraints()
     * @return security constraints
     */
    public SecurityConstraints newSecurityConstraints()
    {
        return new SecurityConstraintsImpl();
    }

    /**
     * <p>
     * newSecurityConstraint
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecureResource#newSecurityConstraint()
     * @return security constraint
     */
    public SecurityConstraint newSecurityConstraint()
    {
        return new SecurityConstraintImpl();
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

        // get action names lists; separate view and other
        // actions to mimic file system permissions logic
        List viewActionList = SecurityConstraintImpl.parseCSVList(actions);
        List otherActionsList = null;
        if (viewActionList.size() == 1)
        {
            if (!viewActionList.contains(SecuredResource.VIEW_ACTION))
            {
                otherActionsList = viewActionList;
                viewActionList = null;
            }
        }
        else
        {
            otherActionsList = viewActionList;
            viewActionList = null;
            if (otherActionsList.remove(SecuredResource.VIEW_ACTION))
            {
                viewActionList = new ArrayList(1);
                viewActionList.add(SecuredResource.VIEW_ACTION);
            }
        }

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
     * <p>
     * checkConstraints
     * </p>
     *
     * @param actions
     * @param userPrincipals
     * @param rolePrincipals
     * @param groupPrincipals
     * @param checkNodeOnly
     * @param checkParentsOnly
     * @throws SecurityException
     */
    public void checkConstraints(List actions, List userPrincipals, List rolePrincipals, List groupPrincipals, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // check node constraints if available
        if ((constraints != null) && !constraints.isEmpty())
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

        // separate view and other actions to mimic file system permissions logic
        boolean viewAction = false;
        String otherActions = actions.trim();
        int viewActionIndex = otherActions.indexOf(SecuredResource.VIEW_ACTION);
        if (viewActionIndex != -1)
        {
            viewAction = true;
            if (viewActionIndex == 0)
            {
                if (otherActions.length() > SecuredResource.VIEW_ACTION.length())
                {
                    // remove view action from other actions
                    int nextDelimIndex = otherActions.indexOf(',', viewActionIndex + SecuredResource.VIEW_ACTION.length());
                    otherActions = otherActions.substring(nextDelimIndex + 1);
                }
                else
                {
                    // no other actions
                    otherActions = null;
                }
            }
            else
            {
                // remove view action from other actions
                int prevDelimIndex = otherActions.lastIndexOf(',', viewActionIndex);
                otherActions = otherActions.substring(0, prevDelimIndex) + otherActions.substring(viewActionIndex + SecuredResource.VIEW_ACTION.length());
            }
        }

        // check permissions using parsed actions
        if (viewAction)
        {
            checkPermissions(SecuredResource.VIEW_ACTION, false, grantViewActionAccess());
        }
        if (otherActions != null)
        {
            checkPermissions(otherActions, true, false);
        }
    }
    /**
     * <p>
     * checkPermissions
     * </p>
     *
     * @param actions
     * @param checkNodeOnly
     * @param checkParentsOnly
     * @throws SecurityException
     */
    public void checkPermissions(String actions, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // check page and folder permissions
        String physicalPermissionPath = getPhysicalPermissionPath();
        if (physicalPermissionPath != null)
        {
            // check permissions using physical path
            try
            {
                checkPermissions(physicalPermissionPath, actions, checkNodeOnly, checkParentsOnly);
            }
            catch (SecurityException physicalSE)
            {
                // fallback check using logical path if available and different
                String logicalPermissionPath = getLogicalPermissionPath();
                if ((logicalPermissionPath != null) && !logicalPermissionPath.equals(physicalPermissionPath))
                {
                    checkPermissions(logicalPermissionPath, actions, checkNodeOnly, checkParentsOnly);
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
     * checkPermissions
     * </p>
     *
     * @param path
     * @param actions
     * @param checkNodeOnly
     * @param checkParentsOnly
     * @throws SecurityException
     */
    public void checkPermissions(String path, String actions, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // check actions permissions
        try
        {
            // check for granted page permissions
            PagePermission permission = new PagePermission(path, actions);
            AccessController.checkPermission(permission);
        }
        catch (SecurityException se)
        {
            // fallback check for granted folder permissions
            FolderPermission permission = new FolderPermission(path, actions);
            AccessController.checkPermission(permission);
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
     * grantViewActionAccess
     * </p>
     *
     * @return granted access for view action
     */
    public boolean grantViewActionAccess()
    {
        // by default, access must be checked
        return false;
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
        return ((null != id) ? id.hashCode() : -1);
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

    /**
     * <p>
     * checkAccess
     * </p>
     *
     * @param nodes
     * @param actions
     * @return
     */
    public static NodeSet checkAccess(NodeSet nodes, String actions)
    {
        if ((nodes != null) && !nodes.isEmpty())
        {
            // check permissions and constraints, filter nodes as required
            NodeSetImpl filteredNodes = null;
            Iterator checkAccessIter = nodes.iterator();
            while (checkAccessIter.hasNext())
            {
                AbstractBaseElement node = (AbstractBaseElement)checkAccessIter.next();
                try
                {
                    // check access
                    node.checkAccess(actions);

                    // add to filteredNodes nodes if copying
                    if (filteredNodes != null)
                    {
                        // permitted, add to filteredNodes nodes
                        filteredNodes.add((Node)node);
                    }
                }
                catch (SecurityException se)
                {
                    // create filteredNodes nodes if not already copying
                    if (filteredNodes == null)
                    {
                        // not permitted, copy previously permitted nodes
                        // to new filteredNodes node set with same comparator
                        filteredNodes = new NodeSetImpl(null, ((NodeSetImpl) nodes).getComparator());
                        Iterator copyIter = nodes.iterator();
                        while (copyIter.hasNext())
                        {
                            Node copyNode = (Node)copyIter.next();
                            if (copyNode != node)
                            {
                                filteredNodes.add(copyNode);
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }
            }

            // return filteredNodes nodes if generated
            if (filteredNodes != null)
            {
                return filteredNodes;
            }
        }
        return nodes;
    }

    /**
     * unmarshalled - notification that this instance has been
     *                loaded from the persistent store
     */
    public void unmarshalled()
    {
        // by default, no action required
    }

    /**
     * marshalling - notification that this instance is to
     *               be saved to the persistent store
     */
    public void marshalling()
    {
        // by default, no action required
    }
}
