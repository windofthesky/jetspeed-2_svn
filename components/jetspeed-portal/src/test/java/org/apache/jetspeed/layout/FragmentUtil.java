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
package org.apache.jetspeed.layout;

import javax.security.auth.Subject;

import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.impl.ContentPageImpl;
import org.apache.jetspeed.om.page.impl.ContentFragmentImpl;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;
import org.jmock.Mock;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletConfig;
import com.mockrunner.mock.web.MockServletContext;

/**
 * Test for Fragment placement
 * 
 * @author <a>David Gurney </a>
 * @version $Id: $
 */
public class FragmentUtil
{

    public static RequestContext buildFullRequestContext()
    {
        // Build a request object and populate it with fragments
        RequestContext a_oRC = setupRequestContext("remove", "1234", "0", "0");

        a_oRC.setPage(setupPage());

        return a_oRC;
    }

    // Helper method to find a string within the response
    public static boolean findValue(RequestContext p_oRequestContext,
            String p_sValue)
    {
        MockHttpServletResponse mr = (MockHttpServletResponse) p_oRequestContext
                .getResponse();
        String a_sContent = mr.getOutputStreamContent();
        boolean a_bResults = a_sContent.indexOf(p_sValue) >= 0;
        return a_bResults;
    }

    // Helper method
    public static RequestContext setupRequestContext(String p_sAction,
            String p_sPortletId, String p_sCol, String p_sRow)
    {
        RequestContextComponent rcc = (RequestContextComponent) new Mock(RequestContextComponent.class).proxy();
        
        MockServletConfig config = new MockServletConfig();
        MockServletContext context = new MockServletContext();
        MockHttpSession session = new MockHttpSession();
        session.setupServletContext(context);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setupAddParameter("action", p_sAction);
        request.setupAddParameter("id", p_sPortletId);
        if (p_sRow != null)
        {
            request.setupAddParameter("row", p_sRow);
        }
        if (p_sCol != null)
        {
            request.setupAddParameter("col", p_sCol);
        }

        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();

        RequestContext a_oRC = new JetspeedRequestContext(rcc, request, response,
                config, null);
        
        a_oRC.setSubject(new Subject());
        
        a_oRC.setPage(setupPage());

        return a_oRC;
    }

    // Helper method
    public static ContentPage setupPage()
    {
        // Prepare some fragments
        ContentFragmentImpl a_oLayout = buildFragment("layout", "6", "layout", 0, 0);
        ContentFragmentImpl a_oFrag1 = buildFragment("frag1", "1", "portlet", 0, 0);
        ContentFragmentImpl a_oFrag2 = buildFragment("frag2", "2", "portlet", 0, 1); 
        ContentFragmentImpl a_oFrag3 = buildFragment("frag3", "3", "portlet", 1, 0);
        ContentFragmentImpl a_oFrag4 = buildFragment("frag4", "4", "portlet", 1, 1);
        ContentFragmentImpl a_oFrag5 = buildFragment("frag5", "5", "portlet", 1, 2);
        
        a_oLayout.getFragments().add(a_oFrag1);
        a_oLayout.getFragments().add(a_oFrag2);
        a_oLayout.getFragments().add(a_oFrag3);
        a_oLayout.getFragments().add(a_oFrag4);
        a_oLayout.getFragments().add(a_oFrag5);

        ContentPageImpl a_oPage = new ContentPageImpl();
        a_oPage.setRootFragment(a_oLayout);

        return a_oPage;
    }

    public static ContentFragmentImpl buildFragment(String p_sName, String p_sId,
            String p_sType, int p_iCol, int p_iRow)
    {
        ContentFragmentImpl a_oFrag = new ContentFragmentImpl(p_sId);
        a_oFrag.setName(p_sName);
        a_oFrag.setType(p_sType);
        a_oFrag.setLayoutColumn(p_iCol);
        a_oFrag.setLayoutRow(p_iRow);
        return a_oFrag;
    }
    
    public static void debugContentOutput(RequestContext rc)
    {
        MockHttpServletResponse mr = (MockHttpServletResponse) rc.getResponse();        
        String content = mr.getOutputStreamContent();
        System.out.println("content = " + content);
    }
    
    public static String getContentOutput(RequestContext rc)
    {
        MockHttpServletResponse mr = (MockHttpServletResponse) rc.getResponse();        
        return mr.getOutputStreamContent();
    }

}
