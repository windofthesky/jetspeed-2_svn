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

package org.apache.jetspeed.om.page.psml;

import java.security.AccessController;
import java.security.Permission;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.page.BaseElement;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintImpl;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.psml.NodeSetImpl;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.PermissionFactory;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.User;


/**
 *
 * @version $Id$
 */
public abstract class AbstractBaseElement implements java.io.Serializable, SecuredResource
{
    private final static Logger log = LoggerFactory.getLogger(AbstractBaseElement.class);

    private String id = null;

    private String title = null;

    private String shortTitle = null;

    private boolean constraintsEnabled;

    private SecurityConstraints constraints = null;

    private boolean permissionsEnabled;
    
    private DocumentHandlerFactory handlerFactory = null;

    protected static PermissionFactory pf;
    
    public static void setPermissionsFactory(PermissionFactory pf)
    {
        AbstractBaseElement.pf = pf;
    }
    
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
     * @return short title
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
     * isStale
     * </p>
     *
     * @see org.apache.jetspeed.om.page.BaseElement#isStale()
     */
    public boolean isStale()
    {
        // file based PSML instances never considered stale
        return false;
    }

    /**
     * <p>
     * getConstraintsEnabled
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecuredResource#getConstraintsEnabled()
     * @return whether security relies on PSML constraints
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
     * @see org.apache.jetspeed.om.common.SecuredResource#getSecurityConstraints()
     * @return the PSML security constraints
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
     * @see org.apache.jetspeed.om.common.SecuredResource#newSecurityConstraints()
     * @return  a new security constraints object
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
     * @see org.apache.jetspeed.om.common.SecuredResource#newSecurityConstraint()
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
     * @see org.apache.jetspeed.om.common.SecuredResource#setSecurityConstraints(org.apache.jetspeed.om.common.SecurityConstraints)
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
     * @see org.apache.jetspeed.om.common.SecuredResource#checkConstraints(java.lang.String)
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
                viewActionList = new ArrayList<String>(1);
                viewActionList.add(JetspeedActions.VIEW);
            }
        }

        // get current request context subject
        Subject subject = JSSubject.getSubject(AccessController.getContext());
        if (subject == null)
        {
            throw new SecurityException("AbstractBaseElement.checkConstraints(): Missing JSSubject");
        }

        // get user/group/role principal names
        List<String> userPrincipals = null;
        List<String> rolePrincipals = null;
        List<String> groupPrincipals = null;
        for (Principal principal: subject.getPrincipals())
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
    public void checkConstraints(List<String> actions, List<String> userPrincipals, List<String> rolePrincipals, List<String> groupPrincipals, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // check node constraints if available
        if ((constraints != null) && !constraints.isEmpty())
        {
            ((SecurityConstraintsImpl)constraints).checkConstraints(actions, userPrincipals, rolePrincipals, groupPrincipals, getEffectivePageSecurity());
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
     * @see org.apache.jetspeed.om.common.SecuredResource#checkPermissions(int)
     * @param mask Mask of actions requested
     * @throws SecurityException
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
    /**
     * <p>
     * checkPermissions
     * </p>
     *
     * @param mask of actions
     * @param checkNodeOnly
     * @param checkParentsOnly
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
     * <p>
     * checkPermissions
     * </p>
     *
     * @param path
     * @param mask Mask of actions requested
     * @param checkNodeOnly
     * @param checkParentsOnly
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
            int mask = pf.parseActions(actions);
            checkPermissions(mask);
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
     * @return whether the supplied object equals this one
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
     * @return the hashcode for this object
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
     * @return the id as a string representation of this object
     */
    public String toString()
    {      
        return getId();
    }

    /**
     * <p>
     * checkAccess returns a set of nodes we can access.  It may be the passed in node set or a partial copy.
     * </p>
     *
     * @param nodes
     * @param actions
     * @return a NodeSet containing the nodes allowing access
     */
    public static NodeSet checkAccess(NodeSet nodes, String actions)
    {
        if ((nodes != null) && !nodes.isEmpty())
        {
            // check permissions and constraints, filter nodes as required
            NodeSetImpl filteredNodes = null;
            for (Node node : nodes)
            {
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
                        for (Node copyNode : nodes)
                        {
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
     * @param generator id generator
     * @return dirty flag
     */
    public boolean unmarshalled(IdGenerator generator)
    {
        // by default, no action required
        return false;
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
