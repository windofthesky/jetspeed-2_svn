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
package org.apache.jetspeed.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletMode;
import javax.portlet.RenderMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * GenericPortletUtils
 * 
 * @version $Id$
 */
public class GenericPortletUtils
{
    
    private GenericPortletUtils()
    {
    }
    
    /**
     * Finds the helper method from the portlet object extending <CODE>javax.portlet.GenericPortlet</CODE>.
     * <P>
     * <EM>Note: the helper method can be retrieved only when the method is public.</EM>
     * </P>
     * 
     * @param genericPortletClazz the portlet object extending <CODE>javax.portlet.GenericPortlet</CODE>
     * @param mode the portlet mode for the helper method
     * @return
     * @see javax.portlet.GenericPortlet
     */
    public static Method getRenderModeHelperMethod(GenericPortlet genericPortlet, PortletMode mode)
    {
        return getRenderModeHelperMethod(genericPortlet.getClass(), mode);
    }
    
    /**
     * Finds the helper method from the portlet class extending <CODE>javax.portlet.GenericPortlet</CODE>.
     * <P>
     * <EM>Note: the helper method can be retrieved only when the method is public.</EM>
     * </P>
     * 
     * @param genericPortletClazz the portlet class extending <CODE>javax.portlet.GenericPortlet</CODE>
     * @param mode the portlet mode for the helper method
     * @return
     * @see javax.portlet.GenericPortlet
     */
    public static Method getRenderModeHelperMethod(Class<? extends GenericPortlet> genericPortletClazz, PortletMode mode)
    {
        Method helperMethod = null;
        String modeName = mode.toString();
        
        for (Method method : genericPortletClazz.getMethods()) {
            Annotation[] annotations = method.getAnnotations();
            
            if (annotations != null) {
                for (Annotation annotation : annotations) {
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    
                    if (RenderMode.class.equals(annotationType)) 
                    {
                        String name = ((RenderMode) annotation).name();
                        
                        if (modeName.equals(name))
                        {
                            if (Modifier.isPublic(method.getModifiers()))
                            {
                                return method;
                            }
                            else
                            {
                                return null;
                            }
                        }
                    }
                }
            }
        }
        
        try
        {
            if (PortletMode.EDIT.equals(mode))
            {
                helperMethod = genericPortletClazz.getMethod("doEdit", new Class [] { RenderRequest.class, RenderResponse.class });
            }
            else if (PortletMode.HELP.equals(mode))
            {
                helperMethod = genericPortletClazz.getMethod("doHelp", new Class [] { RenderRequest.class, RenderResponse.class });
            }
            else if (PortletMode.VIEW.equals(mode))
            {
                helperMethod = genericPortletClazz.getMethod("doView", new Class [] { RenderRequest.class, RenderResponse.class });
            }
        }
        catch (NoSuchMethodException e)
        {
        }
        
        if (helperMethod != null && Modifier.isPublic(helperMethod.getModifiers()))
        {
            return helperMethod;
        }
        
        return null;
    }
    
}
