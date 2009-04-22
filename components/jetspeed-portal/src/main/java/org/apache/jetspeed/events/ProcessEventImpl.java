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

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.container.PortletWindow;

/**
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ProcessEventImpl implements ProcessEvent
{
    private PortletWindow window;
    private QName qname;
    private java.io.Serializable value;
    private String className;
    private JetspeedEventCoordinationService eventService;
    private boolean processed = false;
    private static Logger log = LoggerFactory.getLogger(ProcessEventImpl.class);
    

    public ProcessEventImpl(PortletWindow window, QName qname, String className, java.io.Serializable value, JetspeedEventCoordinationService eventService)
    {
        this.window = window;
        this.qname = qname;
        this.value = value;
        this.className = className;
        this.eventService = eventService;
    }

    public QName getQName()
    {
        return qname;
    }

    public java.io.Serializable getRawValue()
    {
        return value;
    }
    
    public java.io.Serializable getValue()
    {        
        if (value != null && value instanceof String)
        {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try
            {
                Serializable object = eventService.deserialize(this);
                return object;
            }
            catch (Exception e)
            {
                log.error(e.getMessage(),e);
            }
            finally
            {
                Thread.currentThread().setContextClassLoader(cl);
            }
        }
        return value;
    }

    public String getName()
    {
        return qname.getLocalPart();
    }

    public void setProcessed(boolean processed)
    {
        this.processed = processed;
    }
    
    public boolean isProcessed()
    {
        return processed;
    }
    
    public PortletWindow getPortletWindow()
    {
        return this.window;
    }
    
    public String getClassName()
    {
        return this.className;
    }
}
