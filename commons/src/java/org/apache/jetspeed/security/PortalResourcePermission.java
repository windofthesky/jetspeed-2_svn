/* Copyright 2004 Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.security;

import java.security.Permission;
import java.util.StringTokenizer;

import javax.security.auth.Subject;

import org.apache.jetspeed.JetspeedActions;


/**
 * <p>Generalized Portlet Resoure permission.</p>
 * <p>This code was partially inspired from articles from:</p>
 * <ul>
 *    <li><a href="http://www-106.ibm.com/developerworks/library/j-jaas/">
 *    Extend JAAS for class instance-level authorization.</a></li>
 * </ul>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 */
public abstract class PortalResourcePermission extends Permission
{
    /** <p>Mask used for determining what action to perform.</p> */
    protected int mask;

    /** <p>The subject the permission is being performed against.</p> */
    protected Subject subject;
    
    /**
     * <p>Constructor for PortletPermission.</p>
     * @param name The portlet name.
     * @param actions The actions on the portlet.
     */
    public PortalResourcePermission(String name, String actions, Subject subject)
    {
        super(name);
        parseActions(actions);
        this.subject = subject;
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
    
    /**
     * @see java.security.Permission#getActions()
     */
    public String getActions()
    {
        StringBuffer buf = new StringBuffer();

        if ((mask & JetspeedActions.MASK_VIEW) == JetspeedActions.MASK_VIEW)
        {
            buf.append(JetspeedActions.VIEW);
        }
        if ((mask & JetspeedActions.MASK_EDIT) == JetspeedActions.MASK_EDIT)
        {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(JetspeedActions.EDIT);
        }
        if ((mask & JetspeedActions.MASK_RESTORE) == JetspeedActions.MASK_RESTORE)
        {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(JetspeedActions.RESTORE);
        }
        if ((mask & JetspeedActions.MASK_MINIMIZE) == JetspeedActions.MASK_MINIMIZE)
        {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(JetspeedActions.MINIMIZE);
        }
        if ((mask & JetspeedActions.MASK_MAXIMIZE) == JetspeedActions.MASK_MAXIMIZE)
        {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(JetspeedActions.MAXIMIZE);
        }
        if ((mask & JetspeedActions.MASK_HELP) == JetspeedActions.MASK_HELP)
        {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(JetspeedActions.HELP);
        }
        if ((mask & JetspeedActions.MASK_SECURE) == JetspeedActions.MASK_SECURE)
        {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(JetspeedActions.SECURE);
        }

        return buf.toString();
    }

    /* (non-Javadoc)
     * @see java.security.Permission#implies(java.security.Permission)
     */
    public boolean implies(Permission permission)
    {
        // TODO Auto-generated method stub
        return false;
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
                if (token.equals(JetspeedActions.VIEW))
                    mask |= JetspeedActions.MASK_VIEW;
                else if (token.equals(JetspeedActions.VIEW) || token.equals(JetspeedActions.RESTORE))
                    mask |= JetspeedActions.MASK_VIEW;
                else if (token.equals(JetspeedActions.EDIT))
                    mask |= JetspeedActions.MASK_EDIT;
                else if (token.equals(JetspeedActions.MINIMIZE))
                    mask |= JetspeedActions.MASK_MINIMIZE;
                else if (token.equals(JetspeedActions.MAXIMIZE))
                    mask |= JetspeedActions.MASK_MAXIMIZE;
                else if (token.equals(JetspeedActions.HELP))
                    mask |= JetspeedActions.MASK_HELP;
                else if (token.equals(JetspeedActions.SECURE))
                    mask |= JetspeedActions.MASK_SECURE;                
                else
                    throw new IllegalArgumentException("Unknown action: " + token);
            }
        }
    }
    
    /**
     * <p>Gets the subject.</p>
     * @return Returns a Subject
     */
    public Subject getSubject()
    {
        return subject;
    }
    
}
