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
package org.apache.jetspeed.portlets.layout;

import org.apache.jetspeed.om.page.Fragment;

public class LayoutEvent
{
    public static final int ADDED =0;
    public static final int MOVED_UP = 1;
    public static final int MOVED_DOWN = 2;
    public static final int MOVED_LEFT = 3;
    public static final int MOVED_RIGHT = 4;
    
    private final int eventType;
    private final Fragment fragment;
    private final LayoutCoordinate originalCoordinate;
    private final LayoutCoordinate newCoordinate;    

    public LayoutEvent(int eventType, Fragment fragment, LayoutCoordinate originalCoordinate, LayoutCoordinate newCoordinate)
    {
        super();       
        this.eventType = eventType;
        this.fragment = fragment;
        this.originalCoordinate = originalCoordinate;
        this.newCoordinate = newCoordinate;
    }
   
   
    public int getEventType()
    {
        return eventType;
    }
    

    public Fragment getFragment()
    {
        return fragment;
    }
    

    public LayoutCoordinate getNewCoordinate()
    {
        return newCoordinate;
    }
    

    public LayoutCoordinate getOriginalCoordinate()
    {
        return originalCoordinate;
    }


    public boolean equals(Object obj)
    {
        if(obj instanceof LayoutEvent)
        {
            LayoutEvent event = (LayoutEvent) obj;
            return event.fragment.equals(fragment) 
              && event.eventType == eventType
              && event.originalCoordinate.equals(originalCoordinate)
              && event.newCoordinate.equals(newCoordinate);
            
        }
        else
        {
            return false;
        }
    }


    public String toString()
    {
        
        return "event_target="+fragment.getId()+",event_type_code="+ eventType + ",orginial_coordinate="+ originalCoordinate+
               ",new_coordinate="+newCoordinate;
    }
    

}
