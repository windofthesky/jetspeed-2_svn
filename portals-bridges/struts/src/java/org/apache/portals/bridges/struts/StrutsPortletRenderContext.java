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
package org.apache.portals.bridges.struts;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMessages;

/**
 * StrutsPortletRenderContext
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class StrutsPortletRenderContext implements Serializable
{
    private String path;
    private boolean dispatchNamed;
    private ActionForm actionForm;
    private boolean requestCancelled;
    private ActionMessages messages;
    private ActionMessages errors;
    public String getPath()
    {
        return path;
    }
    public void setPath(String path)
    {
        this.path = path;
    }
    public boolean getDispatchNamed()
    {
        return dispatchNamed;
    }
    public void setDispatchNamed(boolean namedPath)
    {
        this.dispatchNamed = namedPath;
    }
    public ActionForm getActionForm()
    {
        return actionForm;
    }
    public void setActionForm(ActionForm actionForm)
    {
        this.actionForm = actionForm;
    }
    public boolean isRequestCancelled()
    {
        return requestCancelled;
    }
    public void setRequestCancelled(boolean requestCancelled)
    {
        this.requestCancelled = requestCancelled;
    }
    public ActionMessages getMessages()
    {
        return messages;
    }
    public void setMessages(ActionMessages messages)
    {
        this.messages = messages;
    }
    public ActionMessages getErrors()
    {
        return errors;
    }
    public void setErrors(ActionMessages errors)
    {
        this.errors = errors;
    }
}
