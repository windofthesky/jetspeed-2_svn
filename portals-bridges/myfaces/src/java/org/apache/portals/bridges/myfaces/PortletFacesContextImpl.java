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

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import net.sourceforge.myfaces.util.NullIterator;

/**
 * TODO There should be a base class shared with ServletFacesContextImpl.
 * 
 * @see Also {@link net.sourceforge.myfaces.context.servlet.ServletFacesContextImpl}
 * @author <a href="dlestrat@apache.org">David Le Strat</a>
 *
 */
public class PortletFacesContextImpl extends FacesContext
{
	//~ Static fields/initializers -----------------------------------------------------------------
	protected static final Object NULL_DUMMY = new Object();

	//~ Instance fields ----------------------------------------------------------------------------
    private List messageClientIds = null;
    private List messages = null;
    private Application application;
    private PortletExternalContextImpl externalContext;
    private ResponseStream responseStream = null;
    private ResponseWriter responseWriter = null;
    private FacesMessage.Severity maximumSeverity = FacesMessage.SEVERITY_INFO;
    private UIViewRoot viewRoot;
    private boolean renderResponse = false;
    private boolean responseComplete = false;
    private RenderKitFactory renderKitFactory;

    //~ Constructors -------------------------------------------------------------------------------
    public PortletFacesContextImpl(PortletContext portletContext,
                                   PortletRequest portletRequest,
                                   PortletResponse portletResponse)
    {
        this.application = ((ApplicationFactory)FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY))
                            .getApplication();
        this.renderKitFactory = (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        this.externalContext = new PortletExternalContextImpl(portletContext,
                                                          	  portletRequest,
                                                              portletResponse);
        FacesContext.setCurrentInstance(this);  //protected method, therefore must be called from here
    }

    public ExternalContext getExternalContext()
    {
        return this.externalContext;
    }

    public FacesMessage.Severity getMaximumSeverity()
    {
        return this.maximumSeverity;
    }

    public Iterator getMessages()
    {
        return (this.messages != null) ? this.messages.iterator() : Collections.EMPTY_LIST.iterator();
    }

    public Application getApplication()
    {
        return this.application;
    }

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
                if (savedClientId == NULL_DUMMY) lst.add(this.messages.get(i));
            }
            else
            {
                if (clientId.equals(savedClientId)) lst.add(this.messages.get(i));
            }
        }
        return lst.iterator();
    }

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

    public boolean getRenderResponse()
    {
        return this.renderResponse;
    }

    public boolean getResponseComplete()
    {
        return this.responseComplete;
    }

    public void setResponseStream(ResponseStream responseStream)
    {
        if (responseStream == null)
        {
            throw new NullPointerException("responseStream");
        }
        this.responseStream = responseStream;
    }

    public ResponseStream getResponseStream()
    {
        return this.responseStream;
    }

    public void setResponseWriter(ResponseWriter responseWriter)
    {
        if (responseWriter == null)
        {
            throw new NullPointerException("responseWriter");
        }
        this.responseWriter = responseWriter;
    }

    public ResponseWriter getResponseWriter()
    {
        return this.responseWriter;
    }

    public void setViewRoot(UIViewRoot viewRoot)
    {
        if (viewRoot == null)
        {
            throw new NullPointerException("viewRoot");
        }
        this.viewRoot = viewRoot;
    }

    public UIViewRoot getViewRoot()
    {
        return this.viewRoot;
    }

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
        FacesMessage.Severity serSeverity =  message.getSeverity();
        if (serSeverity != null && serSeverity.compareTo(this.maximumSeverity) > 0)
        {
            this.maximumSeverity = message.getSeverity();
        }
    }

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

    public void renderResponse()
    {
        this.renderResponse = true;
    }

    public void responseComplete()
    {
        this.responseComplete = true;
    }
}
