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
package org.apache.jetspeed.demo.customerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id$
 */
public class CustomerPortlet extends org.apache.jetspeed.portlet.ServletPortlet
{
    
    private List defaultCustomers = new ArrayList();
    
    /** Creates a new instance of CustomerPortlet */
    public CustomerPortlet()
    {
    }
    
    
    public void init() throws javax.portlet.PortletException
    {
        CustomerInfo newCustomer = null;
        Address newAddress = null;
        
        // Initialize the defaultCustomer
        newCustomer = new CustomerInfo();
        newCustomer.setName("Jane Doe");
        
        newCustomer.setLastOrdered(new GregorianCalendar(2002,05,15));
        newAddress = new Address();
        newAddress.setName(newCustomer.getName());
        newAddress.setStreet("124 Main Street");
        newAddress.setCity("AnyTown");
        newAddress.setState("ME");
        newAddress.setCountry("U.S.A.");
        newCustomer.setBillingAddress(newAddress);
        newAddress.setName(newCustomer.getName());
        newAddress.setStreet("1862 Elm Drive");
        newAddress.setCity("AnyTown");
        newAddress.setState("ME");
        newAddress.setCountry("U.S.A.");
        newCustomer.setShippingAddress(newAddress);
        this.defaultCustomers.add(newCustomer);
        
        newCustomer = new CustomerInfo();
        newCustomer.setName("Fred Smith");
        newCustomer.setLastOrdered(new GregorianCalendar(2002,9,15));
        newAddress = new Address();
        newAddress.setName(newCustomer.getName());
        newAddress.setStreet("1 Bearch Way");
        newAddress.setCity("AnyTown");
        newAddress.setState("ME");
        newAddress.setCountry("U.S.A.");
        newCustomer.setBillingAddress(newAddress);
        newAddress.setName(newCustomer.getName());
        newAddress.setStreet("1862 Elm Drive");
        newAddress.setCity("AnyTown");
        newAddress.setState("ME");
        newAddress.setCountry("U.S.A.");
        newCustomer.setShippingAddress(newAddress);
        this.defaultCustomers.add(newCustomer);

        newCustomer = new CustomerInfo();
        newCustomer.setName("Wallace George");
        newCustomer.setLastOrdered(new GregorianCalendar(2003,1,1));
        newAddress = new Address();
        newAddress.setName(newCustomer.getName());
        newAddress.setStreet("73 Wamack Drive");
        newAddress.setCity("AnyTown");
        newAddress.setState("ME");
        newAddress.setCountry("U.S.A.");
        newCustomer.setBillingAddress(newAddress);
        newAddress.setName(newCustomer.getName());
        newAddress.setStreet("73 Wamack Drive");
        newAddress.setCity("AnyTown");
        newAddress.setState("ME");
        newAddress.setCountry("U.S.A.");
        newCustomer.setShippingAddress(newAddress);
        this.defaultCustomers.add(newCustomer);
}
    
    /**
     * Execute the servlet as define by the init parameter or preference PARAM_ACTION_PAGE.  The value
     * if the parameter is a relative URL, i.e. /actionPage.jsp will execute the
     * JSP editPage.jsp in the portlet application's web app.  The action should
     * not generate any content.  The content will be generate by doCustom(),
     * doHelp() , doEdit(), or doView().
     *
     * See section PLT.16.2 of the JSR 168 Portlet Spec for more information
     * around executing a servlet or JSP in processAction()
     *
     * @see javax.portlet.GenericPortlet#processAction
     *
     * @task Need to be able to execute a servlet for the action
     * @task Need to set current customer and customer detail item
     *       in the session.
     *
     */
    public void processAction(RenderRequest request, ActionResponse actionResponse) throws PortletException, IOException
    {

    }
    
    
    /**
     * Execute the servlet as define by the init parameter or preference PARAM_VIEW_PAGE.
     * The value if the parameter is a relative URL, i.e. /viewPage.jsp will execute the
     * JSP viewPage.jsp in the portlet application's web app.
     *
     * @see javax.portlet.GenericPortlet#doView
     *
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        List customerList = null;
        // Get the current customer list from the session
        PortletSession portletSession = request.getPortletSession();
        if (portletSession != null)
        {
            customerList = (List) portletSession.getAttribute("CustomerList", PortletSession.APPLICATION_SCOPE);
            if (customerList == null)
            {
                customerList = this.defaultCustomers;
                portletSession.setAttribute("CustomerList", this.defaultCustomers, PortletSession.APPLICATION_SCOPE);    
            }
        }
        
        else
        {
            // TODO:  the portletSession == null?
            System.out.println("In org.apache.demo.customerInfo.CustomerPortlet.doView() - The portletSession == null !!!!");
        }
        
        // If no customer list exists, use the default.
        if (customerList == null)
        {
            customerList = this.defaultCustomers;
        }
        
        // Place the customer list in the request context.
        request.setAttribute("CustomerList", customerList);
        super.doView(request, response);
    }
    
}
