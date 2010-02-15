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

import java.lang.reflect.Method;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletMode;
import javax.portlet.RenderMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import junit.framework.TestCase;

public class TestGenericPortletUtils extends TestCase
{
    private GenericPortlet genericPortletWithProtectedDoEdit;
    private GenericPortlet genericPortletWithPublicDoEdit;
    private GenericPortlet genericPortletWithAnnotatedProtectedDoEdit;
    private GenericPortlet genericPortletWithAnnotatedPublicDoEdit;
    
    public void setUp()
    {
        genericPortletWithProtectedDoEdit = new GenericPortletWithProtectedDoEdit();
        genericPortletWithPublicDoEdit = new GenericPortletWithPublicDoEdit();
        genericPortletWithAnnotatedProtectedDoEdit = new GenericPortletWithAnnotatedProtectedDoEdit();
        genericPortletWithAnnotatedPublicDoEdit = new GenericPortletWithAnnotatedPublicDoEdit();
    }
    
    public void testRenderModeHelperMethods()
    {
        Method helperMethod = GenericPortletUtils.getRenderModeHelperMethod(genericPortletWithProtectedDoEdit, PortletMode.EDIT);
        assertNull("The helper method should not be found.", helperMethod);
        
        helperMethod = GenericPortletUtils.getRenderModeHelperMethod(genericPortletWithPublicDoEdit, PortletMode.EDIT);
        assertNotNull("The helper method should be found.", helperMethod);
        assertEquals("doEdit", helperMethod.getName());
        
        helperMethod = GenericPortletUtils.getRenderModeHelperMethod(genericPortletWithAnnotatedProtectedDoEdit, PortletMode.EDIT);
        assertNull("The helper method should not be found.", helperMethod);
        
        helperMethod = GenericPortletUtils.getRenderModeHelperMethod(genericPortletWithAnnotatedPublicDoEdit, PortletMode.EDIT);
        assertNotNull("The helper method should be found.", helperMethod);
        assertEquals("myEdit", helperMethod.getName());
    }
    
    public class GenericPortletWithProtectedDoEdit extends GenericPortlet
    {
    }
    
    public class GenericPortletWithPublicDoEdit extends GenericPortlet
    {
        @RenderMode(name="help")
        public void myHelp(RenderRequest request, RenderResponse response)
        {
        }
        
        @Override
        public void doEdit(RenderRequest request, RenderResponse response)
        {
        }
    }
    
    public class GenericPortletWithAnnotatedProtectedDoEdit extends GenericPortlet
    {
        @RenderMode(name="edit")
        protected void myEdit(RenderRequest request, RenderResponse response)
        {
        }
    }
    
    public class GenericPortletWithAnnotatedPublicDoEdit extends GenericPortlet
    {
        @RenderMode(name="edit")
        public void myEdit(RenderRequest request, RenderResponse response)
        {
        }
        
        @Override
        public void doEdit(RenderRequest request, RenderResponse response)
        {
        }
    }
    
}
