/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.velocity;

import java.io.Serializable;
import java.security.AccessControlException;
import java.security.AccessController;
import java.util.HashMap;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.security.PortletPermission;

/**
 * PageActionAccess
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PageActionAccess implements Serializable
{
    protected static final Log log = LogFactory.getLog(PageActionAccess.class);

    private static final class ActionAccess implements Serializable
    {
        int checkedFlags;
        int actionFlags;
    }
    
    private boolean anonymous;
    private boolean editAllowed;
    private HashMap fragmentActionAccess;
    
    public PageActionAccess(boolean anonymous, Page page)
    {
        this.anonymous = anonymous;        
        this.editAllowed = checkEditPage(page);
        this.fragmentActionAccess = new HashMap();
    }
    
    public void checkReset(boolean anonymous, Page page)
    {
        if (this.anonymous != anonymous)
        {
            this.anonymous = anonymous;
            this.editAllowed = checkEditPage(page);
            this.fragmentActionAccess.clear();
        }
    }
    
    public boolean isAnonymous()
    {
        return anonymous;
    }
    
    public boolean isEditAllowed()
    {
        return editAllowed;
    }
    
    public boolean checkPortletMode(String fragmentId, String portletName, PortletMode mode)
    {
        return checkActionAccess(fragmentId, portletName, mode.toString());
    }

    public boolean checkWindowState(String fragmentId, String portletName, WindowState state)
    {
        return checkActionAccess(fragmentId, portletName, state.toString());
    }
    
    protected synchronized boolean checkActionAccess(String fragmentId, String portletName, String action)
    {
        try
        {
            int actionIndex = getActionMask(action);
            ActionAccess actionAccess = (ActionAccess)fragmentActionAccess.get(fragmentId);
            if ( actionAccess == null )
            {
                actionAccess = new ActionAccess();
                fragmentActionAccess.put(fragmentId, actionAccess);
            }
            if ( (actionAccess.checkedFlags & actionIndex) != actionIndex )
            {
                // TODO: not handling PortletPermission checks yet 
                // boolean access = checkPermission(portletName, action);
                boolean access = true;

                if ( access )
                {
                    actionAccess.actionFlags |= actionIndex;
                }
                actionAccess.checkedFlags |= actionIndex;            
            }
            return ((actionAccess.actionFlags & actionIndex) == actionIndex);
        }
        catch (IndexOutOfBoundsException e)
        {
            log.error("Unknown action: "+action, e);
            return false;
        }
    }
    
    /**
     * Determines whether the access request indicated by the specified
     * permission should be allowed or denied, based on the security policy
     * currently in effect.
     * 
     * @param resource
     *                  The fully qualified resource name of the portlet
     *                  (PA::portletName)
     * @param action
     *                  The action to perform on this resource (i.e. view, edit, help,
     *                  max, min...)
     * @return true if the action is allowed, false if it is not
     */
    protected boolean checkPermission( String resource, String action )
    {
        try
        {
            // TODO: it may be better to check the PagePermission for the outer
            // most
            // fragment (i.e. the PSML page)
            AccessController.checkPermission(new PortletPermission(resource, action));
        }
        catch (AccessControlException e)
        {
            return false;
        }
        return true;
    }
    
    protected boolean checkEditPage(Page page)
    {
        boolean allowed = false;
        try
        {
            page.checkAccess(JetspeedActions.EDIT);
            allowed = true;
        }       
        catch (SecurityException se) {}
        return allowed;
    }

    protected int getActionMask(String action)
    throws IndexOutOfBoundsException
    {
        int i = 0;
        // will throw IndexOutOfBoundsExceptions on unknown action
        while ( !JetspeedActions.ACTIONS[i++].equals(action) ) ;
        return 1<<i;
    }
}
