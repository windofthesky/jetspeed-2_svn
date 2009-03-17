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
package org.apache.jetspeed.events;

import java.io.Serializable;

import javax.portlet.Event;

import org.apache.pluto.container.PortletWindow;

/**
 * ProcessEvent extends the Portlet API Event to give Jetspeed internals access to processing status as well other state information
 * necessary to the internal implementation. 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$ 
 */
public interface ProcessEvent extends Event
{
    /**
     * Sets the current event status to processed. Should be called when the event is fully processed. 
     * @param processed Use this boolean to set the processed flag to either true or false.
     */
    void setProcessed(boolean processed);
    
    /**
     * Indicates whether an event has been processed by the portal, or is queued to be processed.
     * @return The processed status of this event
     */
    boolean isProcessed();
    
    /**
     * Retrieve the portlet window associated with this event.
     * 
     * @return The portlet window
     */
    PortletWindow getPortletWindow();
    
    /**
     * Retrieve the class name of the serializable class that represents the Portlet Event.
     * @return The class name of the event
     */
    public String getClassName();
    
    /**
     * Retrieve the raw value of the event class instance. This is necessary to provide an alternative
     * to kicking off the deserialziation process, if you just need the 'raw' value of the event
     * @return the raw value of the event without deserializing the event
     */
    public Serializable getRawValue();
    
}
