/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.portlets.googlemaps;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpException;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * YahooGeocodeProxyServlet
 * 
 * 
 * @author jonathan david phillips
 * @version $Id: YahooGeocodeProxyServlet.java 000001 2006-04-25 00:57:00Z jdp $
 */

public class YahooGeocodeProxyServlet extends HttpServlet
{
    /**
     * Configuration 
     */
     private static final String YAHOO_REQUEST = "http://api.local.yahoo.com/MapsService/V1/geocode?appid=YahooDemo&location=";

    /**
     * doGet() override doGet
     */
     protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, java.io.IOException {
		String location = req.getParameter("location");
		location = URLEncoder.encode(location,"UTF-8");
		String url = YAHOO_REQUEST + location;
		String content = "<error/>";
		
		// get content from yahoo, code from http://jakarta.apache.org/commons/httpclient/tutorial.html
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
    			new DefaultHttpMethodRetryHandler(3, false));
		try {
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}
			// set content
			content = method.getResponseBodyAsString();

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
					
		//  return content
		resp.setContentType("text/xml");
		PrintWriter out = resp.getWriter();
		out.print(content);
		out.close();
	}
}

