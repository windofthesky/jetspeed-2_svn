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
package org.apache.jetspeed.layout;

import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.ContentPageImpl;
import org.apache.jetspeed.om.page.psml.PageImpl;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.jetspeed.request.RequestContext;

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

        // Build some fragments and add them to the request context
        // Prepare some fragments
        Fragment a_oFrag1 = buildFragment("frag1", "1", "portlet", 0, 0);
        Fragment a_oFrag2 = buildFragment("frag2", "2", "portlet", 0, 1);
        Fragment a_oFrag3 = buildFragment("frag3", "3", "portlet", 1, 0);
        Fragment a_oFrag4 = buildFragment("frag4", "4", "portlet", 1, 1);
        Fragment a_oFrag5 = buildFragment("frag5", "5", "portlet", 1, 2);
        Fragment a_oLayout = buildFragment("layout", "6", "layout", 0, 0);

        LocalFragmentImpl a_oLocalLayout = (LocalFragmentImpl) a_oLayout;
        a_oLocalLayout.addFragment(a_oFrag1);
        a_oLocalLayout.addFragment(a_oFrag2);
        a_oLocalLayout.addFragment(a_oFrag3);
        a_oLocalLayout.addFragment(a_oFrag4);
        a_oLocalLayout.addFragment(a_oFrag5);

        Page a_oPage = new PageImpl();
        a_oPage.setRootFragment(a_oLayout);
        ContentPage a_oContentPage = new ContentPageImpl(a_oPage);
        a_oRC.setPage(a_oContentPage);

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

        RequestContext a_oRC = new JetspeedRequestContext(request, response,
                config, null);

        Page a_oPage = setupPage();
        ContentPage a_oContentPage = new ContentPageImpl(a_oPage);

        a_oRC.setPage(a_oContentPage);

        return a_oRC;
    }

    // Helper method
    public static Page setupPage()
    {
        // Prepare some fragments
        Fragment a_oFrag1 = buildFragment("frag1", "1", "portlet", 0, 0);
        Fragment a_oFrag2 = buildFragment("frag2", "2", "portlet", 0, 1);
        Fragment a_oFrag3 = buildFragment("frag3", "3", "portlet", 1, 0);
        Fragment a_oFrag4 = buildFragment("frag4", "4", "portlet", 1, 1);
        Fragment a_oFrag5 = buildFragment("frag5", "5", "portlet", 1, 2);
        Fragment a_oLayout = buildFragment("layout", "6", "layout", 0, 0);

        LocalFragmentImpl a_oLocalLayout = (LocalFragmentImpl) a_oLayout;
        a_oLocalLayout.addFragment(a_oFrag1);
        a_oLocalLayout.addFragment(a_oFrag2);
        a_oLocalLayout.addFragment(a_oFrag3);
        a_oLocalLayout.addFragment(a_oFrag4);
        a_oLocalLayout.addFragment(a_oFrag5);

        Page a_oPage = new PageImpl();
        a_oPage.setRootFragment(a_oLayout);

        return a_oPage;
    }

    public static Fragment buildFragment(String p_sName, String p_sId,
            String p_sType, int p_iCol, int p_iRow)
    {
        LocalFragmentImpl a_oFrag = new LocalFragmentImpl();
        a_oFrag.setName(p_sName);
        a_oFrag.setType(p_sType);
        a_oFrag.setLayoutColumn(p_iCol);
        a_oFrag.setLayoutRow(p_iRow);
        a_oFrag.setId(p_sId);
        return a_oFrag;
    }

}
