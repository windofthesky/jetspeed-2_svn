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
package org.apache.portals.bridges.frameworks;

import java.io.IOException;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.portals.bridges.frameworks.model.PortletApplicationModel;


/**
 * ForwardTag
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ForwardTag extends TagSupport
{
    private String view = null;
    private String action = null;
    private String forward = null;
    
    public int doStartTag()
    {
        String content;
        try
        {
            RenderRequest request = (RenderRequest)
                pageContext.getRequest().getAttribute("javax.portlet.request");
            RenderResponse response = (RenderResponse) 
                pageContext.getRequest().getAttribute("javax.portlet.response");
            
            if (request == null || response == null)
            {
                JspWriter out = pageContext.getOut();
                out.print("request response not found");
                return SKIP_BODY;
            }
            PortletApplicationModel model = (PortletApplicationModel)request.getAttribute(FrameworkConstants.MODEL_TOOL);
            if (model == null)
            {
                JspWriter out = pageContext.getOut();
                out.print("model not found");
                return SKIP_BODY;
            }
                                                
            Forwarder forwarder = new Forwarder(model, request, response);
            if (view != null)
            {
                content = forwarder.getView(view).toString();
            }
            else if (forward != null)
            {
                if (action != null)
                {
                    content = forwarder.getLink(forward, action).toString();
                }
                else
                {
                    content = forwarder.getLink(forward).toString();                    
                }
            }
            else
            {
                content = forwarder.toString();
            }
            JspWriter out = pageContext.getOut();
            out.print(content);            
        }
        catch (IOException e)
        {
            System.err.println("Error printing tag: " + e);
        }
        return SKIP_BODY;
    }
    
    
    /**
     * @return Returns the action.
     */
    public String getAction()
    {
        return action;
    }
    /**
     * @param action The action to set.
     */
    public void setAction(String action)
    {
        this.action = action;
    }
    /**
     * @return Returns the view.
     */
    public String getView()
    {
        return view;
    }
    /**
     * @param view The view to set.
     */
    public void setView(String view)
    {
        this.view = view;
    }
    /**
     * @return Returns the forward.
     */
    public String getForward()
    {
        return forward;
    }
    /**
     * @param forward The forward to set.
     */
    public void setForward(String forward)
    {
        this.forward = forward;
    }
}
