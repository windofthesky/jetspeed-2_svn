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
package org.apache.jetspeed.aggregator.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.aggregator.PortletAggregator;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Property;
import org.apache.jetspeed.om.page.ContentFragmentImpl;
import org.apache.jetspeed.request.RequestContext;

/**
 * PortletAggregator builds the content required to render a single portlet.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletAggregatorImpl implements PortletAggregator
{
    private final static Log log = LogFactory.getLog(PortletAggregatorImpl.class);    
    
    private PortletRenderer renderer;

    public PortletAggregatorImpl(PortletRenderer renderer) 
    {
        this.renderer = renderer;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment
     */
    public class PortletEntityFragmentImpl implements Fragment
    {
        private String id;
        private String name;
        private String type;
        private String decorator;
        private String state;

        public PortletEntityFragmentImpl(String id)
        {
            this.id = id;
        }

        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.common.SecuredResource#getConstraintsEnabled()
         */
        public boolean getConstraintsEnabled()
        {
            return false;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.common.SecuredResource#getSecurityConstraints()
         */
        public SecurityConstraints getSecurityConstraints()
        {
            return null;
        }
    
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.common.SecuredResource#setSecurityConstraints(org.apache.jetspeed.om.common.SecurityConstraints)
         */
        public void setSecurityConstraints(SecurityConstraints constraints)
        {
        }

        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.common.SecuredResource#checkConstraints(java.lang.String)
         */
        public void checkConstraints(String actions) throws SecurityException
        {
        }

        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.common.SecuredResource#getPermissionsEnabled()
         */
        public boolean getPermissionsEnabled()
        {
            return false;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.common.SecuredResource#checkPermissions(java.lang.String)
         */
        public void checkPermissions(String actions) throws SecurityException
        {
        }

        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.common.SecuredResource#checkAccess(java.lang.String)
         */
        public void checkAccess(String actions) throws SecurityException
        {
        }

        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.BaseElement#getId()
         */
        public String getId()
        {
            return id;
        }

        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.BaseElement#getTitle()
         */
        public String getTitle()
        {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.BaseElement#setTitle(java.lang.String)
         */
        public void setTitle(String title)
        {
        }

        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.BaseElement#getShortTitle()
         */
        public String getShortTitle()
        {
            return null;
        }

        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.BaseElement#setShortTitle(java.lang.String)
         */
        public void setShortTitle(String title)
        {
        }

        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#getName()
         */
        public String getName()
        {
            return name;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#setName(java.lang.String)
         */
        public void setName( String name )
        {
            this.name = name;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#getType()
         */
        public String getType()
        {
            return type;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#setType(java.lang.String)
         */
        public void setType(String type)
        {
            this.type = type;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#getSkin()
         */
        public String getSkin()
        {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#setSkin(java.lang.String)
         */
        public void setSkin(String skinName)
        {
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#getDecorator()
         */
        public String getDecorator()
        {
            return decorator;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#setDecorator(java.lang.String)
         */
        public void setDecorator(String decoratorName)
        {
            this.decorator = decoratorName;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#getState()
         */
        public String getState()
        {
            return state;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#setState(java.lang.String)
         */
        public void setState(String state)
        {
            this.state = state;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#getFragments()
         */
        public List getFragments()
        {
            return new ArrayList(0);
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#getLayoutProperties()
         */
        public List getLayoutProperties()
        {
            return new ArrayList(0);
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#getProperties(java.lang.String)
         */
        public List getProperties(String layoutName)
        {
            return new ArrayList(0);
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#getPropertyValue(java.lang.String,java.lang.String)
         */
        public String getPropertyValue(String layout, String propName)
        {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#getPropertyValue(java.lang.String,java.lang.String,java.lang.String)
         */
        public void setPropertyValue(String layout, String propName, String value)
        {
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#addProperty(org.apache.jetspeed.om.page.Property)
         */
        public void addProperty(Property p)
        {
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#removeProperty(org.apache.jetspeed.om.page.Property)
         */
        public void removeProperty(Property p)
        {
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#clearProperties(java.lang.String)
         */
        public void clearProperties(String layoutName)
        {
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#getLayoutWidths()
         */
        public int getLayoutRow()
        {
            return -1;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#getLayoutWidths()
         */
        public int getLayoutColumn()
        {
            return -1;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#getLayoutSizes()
         */
        public String getLayoutSizes()
        {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#isReference()
         */
        public boolean isReference()
        {
            return false;
        }
        
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.Fragment#clone()
         */
        public Object clone() throws CloneNotSupportedException
        {
            return null;
        }    
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.aggregator.Aggregator#build(org.apache.jetspeed.request.RequestContext)
     */
    public void build(RequestContext context) throws JetspeedException, IOException
    {
        // construct Fragment for rendering use with
        // appropriate id to match portlet entity
        String entity = context.getRequestParameter(PortalReservedParameters.PORTLET_ENTITY);
        if (entity == null)
        {
            entity = (String)context.getAttribute(PortalReservedParameters.PORTLET_ENTITY);
        }
        PortletEntityFragmentImpl fragment = new PortletEntityFragmentImpl(entity);
        fragment.setType(Fragment.PORTLET);
        fragment.setName(context.getRequestParameter(PortalReservedParameters.PORTLET));
        String decorator = fragment.getDecorator();

        // render and write portlet content to response
        if (decorator == null)
        {
            // decorator = context.getPage().getDefaultDecorator(fragment.getType());
            log.debug("No sepecific decorator portlet so using page default: "+decorator);
        }
        ContentDispatcher dispatcher = renderer.getDispatcher(context, false);
        ContentFragment contentFragment = new ContentFragmentImpl(fragment, new HashMap());
        renderer.renderNow(contentFragment, context);
//        dispatcher.include(fragment);
        context.getResponse().getWriter().write(contentFragment.getRenderedContent());
    }
    
    private void addStyle(RequestContext context, String decoratorName, String decoratorType) 
    {
        log.debug("addStyle: decoratorName=" + decoratorName + ", decoratorType=" + decoratorType );
        HeaderResourceFactory headerResourceFactory=(HeaderResourceFactory)Jetspeed.getComponentManager().getComponent(HeaderResourceFactory.class);
        HeaderResource headerResource=headerResourceFactory.getHeaderResouce(context);
        
        if(decoratorType.equals(Fragment.LAYOUT))
        {
            headerResource.addStyleSheet("content/css/styles.css");
        }
        else
        {
            headerResource.addStyleSheet("content/"+decoratorName+"/css/styles.css");
        }
    }
}
