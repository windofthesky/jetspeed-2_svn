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
package org.apache.jetspeed.services.rest;

import java.util.Set;
import java.util.HashSet;

import javax.ws.rs.core.Application;

import org.apache.jetspeed.Jetspeed;

/**
 * JetspeedJaxrsApplication
 * 
 * @version $Id$
 */
public class JetspeedJaxrsApplication extends Application
{
    
    public static final String CLASSES_ID = JetspeedJaxrsApplication.class.getPackage().getName() + ".classes";
    
    public static final String SINGLETONS_ID = JetspeedJaxrsApplication.class.getPackage().getName() + ".singletons";
    
    
    private Set<Class<?>> classes = new HashSet<Class<?>>();
    
    private Set<Object> singletons = new HashSet<Object>();
    
    public JetspeedJaxrsApplication()
    {
        super();
        
        String classesId = System.getProperty(CLASSES_ID);
        
        if (classesId != null && Jetspeed.getComponentManager().containsComponent(classesId))
        {
            classes = (Set<Class<?>>) Jetspeed.getComponentManager().getComponent(classesId);
        }
        
        String singletonsId = System.getProperty(SINGLETONS_ID);
        
        if (singletonsId != null && Jetspeed.getComponentManager().containsComponent(singletonsId))
        {
            singletons = (Set<Object>) Jetspeed.getComponentManager().getComponent(singletonsId);
        }
    }
    
    @Override
    public Set<Class<?>> getClasses()
    {
        return classes;
    }
    
    public void setClasses(final Set<Class<?>> classes)
    {
        this.classes = classes;
    }
    
    @Override
    public Set<Object> getSingletons()
    {
        return singletons;
    }
    
    public void setSingletons(final Set<Object> singletons)
    {
        this.singletons = singletons;
    }
    
}