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
package org.apache.jetspeed.portlets.layout;

import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;

/**
 * A LayoutEvent is used by ColumnLayout to notify its LayoutAeventListeners
 * that there have been a change in the position of a fragment within the layout.
 * <h3>Constant Values</h3>
 * <ul>
 *   <li>ADDED == 0</li>
 *   <li>MOVED_UP == 1</li>
 *   <li>MOVED_DOWN == 2</li>
 *   <li>MOVED_LEFT == 3</li>
 *   <li>MOVED_RIGHT == 4</li>
 * </ul>
 * 
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @see org.apache.jetspeed.om.page.Fragment
 * @see org.apache.jetspeed.portlets.layout.LayoutEventListener
 * @see org.apache.jetspeed.portlets.layout.ColumnLayout
 *
 */
public class LayoutEvent
{   
    /**Event type value that notifies that a fragment has been added */
    public static final int ADDED =0;
    /**Event type value that notifies that a fragment has been moved up */
    public static final int MOVED_UP = 1;
    /**Event type value that notifies that a fragment has been moved down */
    public static final int MOVED_DOWN = 2;
    /**Event type value that notifies that a fragment has been moved left */
    public static final int MOVED_LEFT = 3;
    /**Event type value that notifies that a fragment has been moved right */
    public static final int MOVED_RIGHT = 4;
    
    private final int eventType;
    private final ContentPage page;
    private final String portletType;
    private final String portletName;
    private final ContentFragment fragment;
    private final LayoutCoordinate originalCoordinate;
    private final LayoutCoordinate newCoordinate;    
   
    /**
     * 
     * @param eventType The type of event (see the event constants)
     * @param page Page that is the target of this event.
     * @param portletType The new portlet type.
     * @param portletName The new portlet name.
     * @see org.apache.jetspeed.om.page.ContentFragment
     */
    public LayoutEvent(int eventType, ContentPage page, String portletType, String portletName)
    {
        super();       
        this.eventType = eventType;
        this.page = page;
        this.portletType = portletType;
        this.portletName = portletName;
        this.fragment = null;
        this.originalCoordinate = null;
        this.newCoordinate = null;
    }
   
    /**
     * 
     * @param eventType The type of event (see the event constants)
     * @param fragment Fragment that is the target of this event.
     * @param originalCoordinate the previous LayoutCoordinate of this Fragment
     * @param newCoordinate the new and current coordinates of this fragment.
     * @see org.apache.jetspeed.om.page.ContentFragment
     */
    public LayoutEvent(int eventType, ContentFragment fragment, LayoutCoordinate originalCoordinate, LayoutCoordinate newCoordinate)
    {
        super();       
        this.eventType = eventType;
        this.page = null;
        this.portletType = null;
        this.portletName = null;
        this.fragment = fragment;
        this.originalCoordinate = originalCoordinate;
        this.newCoordinate = newCoordinate;
    }
   
   
    /** 
     * Returns the event type (see event constants)
     * @return the event type (see event constants)
     * @see ColumnLayout#layoutType
     */    
    public int getEventType()
    {
        return eventType;
    }
    
    /**
     * Returns the page that is the target of this event.
     * @return Page the fragment that is the target of this event.
     * @see org.apache.jetspeed.om.page.ContentPage
     */
    public ContentPage getPage()
    {
        return page;
    }
    
    /** 
     * Returns the portlet type.
     * @return the portlet type.
     */    
    public String getPortletType()
    {
        return portletType;
    }
    
    /** 
     * Returns the portlet name.
     * @return the portlet name.
     */    
    public String getPortletName()
    {
        return portletName;
    }
    
    /**
     * Returns the fragment that is the target of this event.
     * @return Fragment the fragment that is the target of this event.
     * @see org.apache.jetspeed.om.page.ContentFragment
     */
    public ContentFragment getFragment()
    {
        return fragment;
    }
    
    /**
     * Returns the new/current coordinate of the Fragment targeted by this event.
     * @return the new/current coordinate of the Fragment targeted by this event.
     * @see LayoutCoordinate
     */
    public LayoutCoordinate getNewCoordinate()
    {
        return newCoordinate;
    }
    
    /**
     * Returns the original (prior to the event) coordinate of the Fragment targeted by this event.
     * @return the original (prior to the event) coordinate of the Fragment targeted by this event.
     * @see LayoutCoordinate
     */
    public LayoutCoordinate getOriginalCoordinate()
    {
        return originalCoordinate;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof LayoutEvent)
        {
            LayoutEvent event = (LayoutEvent) obj;
            if (fragment != null)
            {
                return (event.fragment.equals(fragment) && 
                        event.eventType == eventType &&
                        event.originalCoordinate.equals(originalCoordinate) &&
                        event.newCoordinate.equals(newCoordinate));
            }
            else if (page != null)
            {
                return (event.page.equals(page) &&
                        event.eventType == eventType &&
                        ((event.portletName == null && portletName == null) ||
                         (event.portletName != null && event.portletName.equals(portletName))) &&
                        ((event.portletType == null && portletType == null) ||
                         (event.portletType != null && event.portletType.equals(portletType))));
            }
            else
            {
                return (event.eventType == eventType);
            }
        }
        else
        {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        if (fragment != null)
        {
            return "event_target="+fragment.getId()+",event_type_code="+ eventType + ",orginial_coordinate="+ originalCoordinate+",new_coordinate="+ newCoordinate;
        }
        else if (page != null)
        {
            return "event_target="+page.getId()+",event_type_code="+ eventType + ",portlet_type="+ portletType+",portlet_name="+ portletName;            
        }
        else
        {
            return "event_type_code="+ eventType;
        }
    }
}
