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
package org.apache.jetspeed.security;

import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.SecurityPermission;
import java.util.StringTokenizer;

import javax.security.auth.Subject;

/**
 * <p>Portlet permission.</p>
 * <p>This code was partially inspired from articles from:</p>
 * <ul>
 *    <li><a href="http://www-106.ibm.com/developerworks/library/j-jaas/">
 *    Extend JAAS for class instance-level authorization.</a></li>
 * </ul>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PortletPermission extends Permission
{

    /** <p>Portlet view permission.</p> */
    static private int VIEW = 0x01;

    /** <p>Portlet edit permission.</p> */
    static private int EDIT = 0x02;

    /** <p>Portlet edit permission.</p> */
    static private int DELETE = 0x04;

    /** <p>Portlet minimize permission.</p> */
    static private int MINIMIZE = 0x08;

    /** <p>Portlet maximize permission.</p> */
    static private int MAXIMIZE = 0x10;

    /** <p>Portlet help permission.</p> */
    static private int HELP = 0x20;

    /** <p>Portlet view action.</p> */
    static final public String VIEW_ACTION = "view";

    /** <p>Portlet edit action.</p> */
    static final public String EDIT_ACTION = "edit";

    /** <p>Portlet edit action.</p> */
    static final public String DELETE_ACTION = "delete";

    /** <p>Portlet delete action.</p> */
    static final public String MINIMIZE_ACTION = "minimize";

    /** <p>Portlet maximize action.</p> */
    static final public String MAXIMIZE_ACTION = "maximize";

    /** <p>Portlet help action.</p> */
    static final public String HELP_ACTION = "help";

    /** <p>Mask used for determining what action to perform.</p> */
    int mask;

    /** <p>The subject the permission is being performed against.</p> */
    Subject subject;

    /**
     * <p>Constructor for PortletPermission.</p>
     * @param name The portlet name.
     * @param actions The actions on the portlet.
     */
    public PortletPermission(String name, String actions)
    {
        this(name, actions, null);
    }

    /**
     * <p>Constructor for PortletPermission.</p>
     * @param name The portlet name.
     * @param actions The actions on the portlet.
     */
    public PortletPermission(String name, String actions, Subject subject)
    {
        super(name);
        parseActions(actions);
        this.subject = subject;
    }

    /**
     * @see java.security.Permission#getActions()
     */
    public String getActions()
    {
        StringBuffer buf = new StringBuffer();

        if ((mask & VIEW) == VIEW)
        {
            buf.append(VIEW_ACTION);
        }
        if ((mask & EDIT) == EDIT)
        {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(EDIT_ACTION);
        }
        if ((mask & DELETE) == DELETE)
        {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(DELETE_ACTION);
        }
        if ((mask & MINIMIZE) == MINIMIZE)
        {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(MINIMIZE_ACTION);
        }
        if ((mask & MAXIMIZE) == MAXIMIZE)
        {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(MAXIMIZE_ACTION);
        }
        if ((mask & HELP) == HELP)
        {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(HELP_ACTION);
        }

        return buf.toString();
    }

    /**
     * @see java.security.Permission#hashCode()
     */
    public int hashCode()
    {
        StringBuffer value = new StringBuffer(getName());
        return value.toString().hashCode() ^ mask;
    }

    /**
     * @see java.security.Permission#equals(Object)
     */
    public boolean equals(Object object)
    {
        if (!(object instanceof PortletPermission))
            return false;

        PortletPermission p = (PortletPermission) object;
        boolean isEqual = ((p.getName().equals(getName())) && (p.mask == mask));
        return isEqual;
    }

    public boolean implies(Permission permission)
    {
        // The permission must be an instance 
        // of the PortletPermission.
        if (!(permission instanceof PortletPermission))
        {
            return false;
        }

        // The portlet name must be the same.
        if (!(permission.getName().equals(getName())))
        {
            return false;
        }

        PortletPermission portletPerm = (PortletPermission) permission;

        // Get the subject.
        // It was either provide in the constructor.
        Subject user = portletPerm.getSubject();
        // Or we get it from the AccessControlContext.
        if (null == user)
        {
            AccessControlContext context = AccessController.getContext();
            user = Subject.getSubject(context);
        }
        // No user was passed.  The permission must be denied.
        if (null == user)
        {
            return false;
        }

        // The action bits in portletPerm (permission) 
        // must be set in the current mask permission.
        if ((mask & portletPerm.mask) != portletPerm.mask)
        {
            return false;
        }

        return true;
    }

    /**
     * <p>Overrides <code>Permission.newPermissionCollection()</code>.</p>
     * @see java.security.Permission#newPermissionCollection()
     */
    public PermissionCollection newPermissionCollection()
    {
        return new PortletPermissionCollection();
    }

    /**
     * <p>Gets the subject.</p>
     * @return Returns a Subject
     */
    public Subject getSubject()
    {
        return subject;
    }

    /**
     * <p>Parses the actions string.</p>
     * <p>Actions are separated by commas or white space.</p>
     * @param actions The actions
     */
    private void parseActions(String actions)
    {
        mask = 0;
        if (actions != null)
        {
            StringTokenizer tokenizer = new StringTokenizer(actions, ",\t ");
            while (tokenizer.hasMoreTokens())
            {
                String token = tokenizer.nextToken();
                if (token.equals(VIEW_ACTION))
                    mask |= VIEW;
                else if (token.equals(EDIT_ACTION))
                    mask |= EDIT;
                else if (token.equals(DELETE_ACTION))
                    mask |= DELETE;
                else if (token.equals(MINIMIZE_ACTION))
                    mask |= MINIMIZE;
                else if (token.equals(MAXIMIZE_ACTION))
                    mask |= MAXIMIZE;
                else if (token.equals(HELP_ACTION))
                    mask |= HELP;
                else
                    throw new IllegalArgumentException("Unknown action: " + token);
            }
        }
    }

}
