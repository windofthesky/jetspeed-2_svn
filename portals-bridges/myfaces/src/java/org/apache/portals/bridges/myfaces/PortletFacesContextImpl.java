/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.portals.bridges.myfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import javax.portlet.ActionRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import net.sourceforge.myfaces.util.NullIterator;

/**
 * <p>
 * See MyFaces project for servlet implementation.
 * </p>
 * <p>
 * TODO There should be a base class shared with the MyFaces
 * ServletFacesContextImpl.
 * </p>
 * 
 * @author <a href="dlestrat@apache.org">David Le Strat </a>
 *  
 */
public class PortletFacesContextImpl extends FacesContext
{
    protected static final Object NULL_DUMMY = new Object();

    /** The message client ids. */
    private List messageClientIds = null;

    /** The mesages. */
    private List messages = null;

    /** The application. */
    private Application application;

    /** The portlet external context. */
    private PortletExternalContextImpl externalContext;

    /** The response stream. */
    private ResponseStream responseStream = null;

    /** The response writer. */
    private ResponseWriter responseWriter = null;

    /** The severity. */
    private FacesMessage.Severity maximumSeverity = FacesMessage.SEVERITY_INFO;

    /** The view root. */
    private UIViewRoot viewRoot;

    /** The render response. */
    private boolean renderResponse = false;

    /** Whether the response is complete. */
    private boolean responseComplete = false;

    /** The render kit factory. */
    private RenderKitFactory renderKitFactory;

    /** The JSF_VIEW_ID used to maintain the state of the view action. */
    public static final String JSF_VIEW_ID = "jsf_viewid";
    
    /**
     * @param portletContext The {@link PortletContext}.
     * @param portletRequest The {@link PortletRequest}.
     * @param portletResponse The {@link PortletResponse}.
     */
    public PortletFacesContextImpl(PortletContext portletContext,                                    
                                   PortletRequest portletRequest,
                                   PortletResponse portletResponse)
    {
        this.application = ((ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY))
                .getApplication();
        this.renderKitFactory = (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        this.externalContext = new PortletExternalContextImpl(portletContext, portletRequest, portletResponse);
        FacesContext.setCurrentInstance(this); //protected method, therefore
                                               // must be called from here
    }
    
    public UIViewRoot resolveViewRoot(String defaultViewName, PortletRequest portletRequest)
    {
System.out.println("-----------------------------------------");        
System.out.println("+++ Resolving view root: DEFAULT VID: " + defaultViewName);        
        // shoot: can't get the entity id and be portable
        PortletRequest request = (PortletRequest)externalContext.getRequest();
        String viewId = request.getParameter(JSF_VIEW_ID);
System.out.println("+++ Resolving: END VIEW ID: " + viewId);                
        if (viewId == null)
        {
            viewId = defaultViewName;
        }
System.out.println("+++ Resolving: END VIEW ID: " + viewId);        
        
    if (portletRequest instanceof ActionRequest)
    {
        System.out.println("+++ Resolving: ACTION: " + viewId);
        setViewRoot(viewRoot);        
        portletRequest.setAttribute(FacesPortlet.REQUEST_SERVLET_PATH, viewId.replaceAll(".jsp", ".jsf"));        
        return null;
    }


        UIViewRoot viewRoot = 
            (UIViewRoot)request.getPortletSession().getAttribute(viewId, PortletSession.PORTLET_SCOPE);
        if (null == viewRoot)
        {
System.out.println("+++ Resolving: CREATING NEW VIEW ROOT: " + viewId);                    
            viewRoot = application.getViewHandler().createView(this, viewId);
            //viewRoot = new UIViewRoot();
            viewRoot.setViewId(viewId);
            viewRoot.setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);
            request.getPortletSession().setAttribute(viewId, viewRoot, PortletSession.PORTLET_SCOPE);
        }
        else
        {
System.out.println("+++ Resolving: USING FROM SESSION VIEW ROOT: " + viewId);                                
        }
        setViewRoot(viewRoot);
        portletRequest.setAttribute(FacesPortlet.REQUEST_SERVLET_PATH, viewId.replaceAll(".jsp", ".jsf"));
        return viewRoot;
    }

    /**
     * @see javax.faces.context.FacesContext#getExternalContext()
     */
    public ExternalContext getExternalContext()
    {
        return this.externalContext;
    }

    /**
     * @see javax.faces.context.FacesContext#getMaximumSeverity()
     */
    public FacesMessage.Severity getMaximumSeverity()
    {
        return this.maximumSeverity;
    }

    /**
     * @see javax.faces.context.FacesContext#getMessages()
     */
    public Iterator getMessages()
    {
        return (this.messages != null) ? this.messages.iterator() : Collections.EMPTY_LIST.iterator();
    }

    /**
     * @see javax.faces.context.FacesContext#getApplication()
     */
    public Application getApplication()
    {
        return this.application;
    }

    /**
     * @see javax.faces.context.FacesContext#getClientIdsWithMessages()
     */
    public Iterator getClientIdsWithMessages()
    {
        if (this.messages == null || this.messages.isEmpty())
        {
            return NullIterator.instance();
        }

        return new Iterator()
        {
            private int next;

            boolean nextFound;

            public void remove()
            {
                throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
            }

            public boolean hasNext()
            {
                if (!nextFound)
                {
                    for (int len = messageClientIds.size(); next < len; next++)
                    {
                        if (messageClientIds.get(next) != NULL_DUMMY)
                        {
                            nextFound = true;
                            break;
                        }
                    }
                }
                return nextFound;
            }

            public Object next()
            {
                if (hasNext())
                {
                    nextFound = false;
                    return messageClientIds.get(next++);
                }
                throw new NoSuchElementException();
            }
        };
    }

    /**
     * @see javax.faces.context.FacesContext#getMessages(java.lang.String)
     */
    public Iterator getMessages(String clientId)
    {
        if (this.messages == null)
        {
            return NullIterator.instance();
        }

        List lst = new ArrayList();
        for (int i = 0; i < this.messages.size(); i++)
        {
            Object savedClientId = this.messageClientIds.get(i);
            if (clientId == null)
            {
                if (savedClientId == NULL_DUMMY)
                    lst.add(this.messages.get(i));
            }
            else
            {
                if (clientId.equals(savedClientId))
                    lst.add(this.messages.get(i));
            }
        }
        return lst.iterator();
    }

    /**
     * @see javax.faces.context.FacesContext#getRenderKit()
     */
    public RenderKit getRenderKit()
    {
        if (getViewRoot() == null)
        {
            return null;
        }

        String renderKitId = getViewRoot().getRenderKitId();

        if (renderKitId == null)
        {
            return null;
        }

        return this.renderKitFactory.getRenderKit(this, renderKitId);
    }

    /**
     * @see javax.faces.context.FacesContext#getRenderResponse()
     */
    public boolean getRenderResponse()
    {
        return this.renderResponse;
    }

    /**
     * @see javax.faces.context.FacesContext#getResponseComplete()
     */
    public boolean getResponseComplete()
    {
        return this.responseComplete;
    }

    /**
     * @see javax.faces.context.FacesContext#setResponseStream(javax.faces.context.ResponseStream)
     */
    public void setResponseStream(ResponseStream responseStream)
    {
        if (responseStream == null)
        {
            throw new NullPointerException("responseStream");
        }
        this.responseStream = responseStream;
    }

    /**
     * @see javax.faces.context.FacesContext#getResponseStream()
     */
    public ResponseStream getResponseStream()
    {
        return this.responseStream;
    }

    /**
     * @see javax.faces.context.FacesContext#setResponseWriter(javax.faces.context.ResponseWriter)
     */
    public void setResponseWriter(ResponseWriter responseWriter)
    {
        if (responseWriter == null)
        {
            throw new NullPointerException("responseWriter");
        }
        this.responseWriter = responseWriter;
    }

    /**
     * @see javax.faces.context.FacesContext#getResponseWriter()
     */
    public ResponseWriter getResponseWriter()
    {
        return this.responseWriter;
    }

    /**
     * @see javax.faces.context.FacesContext#setViewRoot(javax.faces.component.UIViewRoot)
     */
    public void setViewRoot(UIViewRoot viewRoot)
    {
        if (viewRoot == null)
        {
            throw new NullPointerException("viewRoot");
        }
        this.viewRoot = viewRoot;
    }

    /**
     * @see javax.faces.context.FacesContext#getViewRoot()
     */
    public UIViewRoot getViewRoot()
    {
        return this.viewRoot;
    }

    /**
     * @see javax.faces.context.FacesContext#addMessage(java.lang.String, javax.faces.application.FacesMessage)
     */
    public void addMessage(String clientId, FacesMessage message)
    {
        if (message == null)
        {
            throw new NullPointerException("message");
        }

        if (this.messages == null)
        {
            this.messages = new ArrayList();
            this.messageClientIds = new ArrayList();
        }
        this.messages.add(message);
        this.messageClientIds.add((clientId != null) ? clientId : NULL_DUMMY);
        FacesMessage.Severity serSeverity = message.getSeverity();
        if (serSeverity != null && serSeverity.compareTo(this.maximumSeverity) > 0)
        {
            this.maximumSeverity = message.getSeverity();
        }
    }

    /**
     * @see javax.faces.context.FacesContext#release()
     */
    public void release()
    {
        if (this.externalContext != null)
        {
            this.externalContext.release();
            this.externalContext = null;
        }

        this.messageClientIds = null;
        this.messages = null;
        this.application = null;
        this.responseStream = null;
        this.responseWriter = null;
        this.viewRoot = null;

        FacesContext.setCurrentInstance(null);
    }

    /**
     * @see javax.faces.context.FacesContext#renderResponse()
     */
    public void renderResponse()
    {
        this.renderResponse = true;
    }

    /**
     * @see javax.faces.context.FacesContext#responseComplete()
     */
    public void responseComplete()
    {
        this.responseComplete = true;
    }
}