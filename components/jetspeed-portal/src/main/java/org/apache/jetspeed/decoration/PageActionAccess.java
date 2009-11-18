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
package org.apache.jetspeed.decoration;

import java.io.Serializable;
import java.util.HashMap;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.page.ContentPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PageActionAccess
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PageActionAccess implements PageEditAccess, Serializable
{
    protected static final Logger log = LoggerFactory.getLogger(PageActionAccess.class);

    private static final class ActionAccess implements Serializable
    {
        int checkedFlags;
        int actionFlags;
    }
    
    private boolean anonymous;
    private boolean editAllowed;
    private boolean editing;
    private HashMap fragmentActionAccess;
    
    public PageActionAccess(boolean anonymous, ContentPage page)
    {
        this.anonymous = anonymous;        
        this.editAllowed = checkEditPage(page);
        this.fragmentActionAccess = new HashMap();
    }
    
    public void checkReset(boolean anonymous, ContentPage page)
    {
        if (this.anonymous != anonymous)
        {
            this.anonymous = anonymous;
            this.editAllowed = checkEditPage(page);
            this.fragmentActionAccess.clear();
            this.editing = false;
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
    
    public boolean isEditing()
    {
        return editing;
    }
    
    public void setEditing(boolean editing)
    {
        if ( editing && ! editAllowed )
        {
            throw new SecurityException();
        }
        this.editing = editing;
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
            int actionIndex = JetspeedActions.getContainerActionMask(action);
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
        
    protected boolean checkEditPage(ContentPage page)
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
}
