/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
            customerList = (List) portletSession.getAttribute("CustomerList");
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
