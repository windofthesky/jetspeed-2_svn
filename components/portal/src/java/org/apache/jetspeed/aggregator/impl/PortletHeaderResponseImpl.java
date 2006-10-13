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
package org.apache.jetspeed.aggregator.impl;

import java.io.BufferedReader;
import java.io.StringReader;

import javax.portlet.PortletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.portlet.PortletHeaderRequest;
import org.apache.jetspeed.portlet.PortletHeaderResponse;
import org.apache.jetspeed.request.RequestContext;


public class PortletHeaderResponseImpl implements PortletHeaderResponse
{
    private RequestContext requestContext;
    private HeaderResource hr;
    private String tempContent;
    
    public PortletHeaderResponseImpl(RequestContext requestContext, HeaderResource hr)
    {
        this.requestContext = requestContext;
        this.hr = hr;
    }

    public HeaderResource getHeaderResource()
    {
        return this.hr;
    }
    
    public String getContent()
    {
        return tempContent; 
    }
    
    public void include(PortletHeaderRequest request, PortletHeaderResponse response, String headerResource)
    throws PortletException
    {
        try
        {
            HttpServletRequest servletRequest = requestContext.getRequest();
            HttpServletResponse servletResponse = requestContext.getResponse();
            PortletContent content = new PortletContentImpl();
            HttpBufferedResponse bufferedResponse = 
                new HttpBufferedResponse(servletResponse, content.getWriter());
            ServletContext crossContext = requestContext.getConfig().getServletContext().getContext(request.getPortletApplicationContextPath());            
            RequestDispatcher dispatcher = crossContext.getRequestDispatcher(headerResource);
            if (dispatcher != null)
                dispatcher.include(servletRequest, bufferedResponse);            
            bufferedResponse.flushBuffer();
            BufferedReader reader = new BufferedReader(new StringReader(content.getContent()));
            String buffer;
            StringBuffer headerText = new StringBuffer();
            while ((buffer = reader.readLine()) != null)
            {
                headerText.append( buffer ).append( "\r\n" );
            }
            tempContent = headerText.toString();            
        }
        catch (Exception e)
        {
            throw new PortletException(e);
        }
    }
}
