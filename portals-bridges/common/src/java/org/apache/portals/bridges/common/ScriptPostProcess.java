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

package org.apache.portals.bridges.common;

import javax.portlet.PortletURL;

/**
 * ScriptPostProcess
 * 
 * Utility class for post processing perl or php created pages.
 * 
 * @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
 * @version $Id$
 */


public class ScriptPostProcess {

	// Private members
	StringBuffer internalPage = null;
	
	/**
	 * 
	 */
	public ScriptPostProcess() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * getFinalizedPage
	 * @return String processed page
	 */
	public String getFinalizedPage()
	{
		if (internalPage != null)
		{
			return internalPage.toString();
		}
		else
		{
			return "";	
		}
	}
	
	/**
	 * setInitialPage() 
	 *  Sets the internal page that will be processed by invoking the different methods
	 * @param page
	 */
	public void setInitalPage(StringBuffer page)
	{
		this.internalPage = page;
	}
	
	/**
	 * postProcessPage()
	 * Applies default rules for processing HREFS and actions in an HTML page
	 * @param actionURL
	 * @param actionParameterName
	 */
	public void postProcessPage(PortletURL actionURL, String actionParameterName)
	{
		// Anchor tags
		processPage("<a", ">", "href=",  actionURL, actionParameterName);
		processPage("<A", ">", "HREF=",  actionURL, actionParameterName);
		processPage("<AREA", ">", "href=",  actionURL, actionParameterName);
		
		// Forms
		processPage("<FORM", ">", "action=",  actionURL, actionParameterName);
		processPage("<form", ">", "action=",  actionURL, actionParameterName);
	}
	
	/**
	 * processPage()
	 * Apply one rule to the page
	 * @param startTag
	 * @param endTag
	 * @param ref
	 * @param actionURL
	 * @param actionParameterName
	 */
	public void processPage(String startTag, String endTag, String ref,  PortletURL actionURL, String actionParameterName)
	{
		final String SINGLE_QUOTE = "\'";
    	final String DOUBLE_QUOTE = "\"";
    	
    	StringBuffer finalPage = new StringBuffer();
		String page = internalPage.toString();
		
		int ixTagOpen, ixTagEnd, ixRefStart, ixRefEnd;
		//ref = ref + quote;
		
		// Start search
		ixTagOpen = page.indexOf(startTag);
		
		try
		{
			while (ixTagOpen != -1 )
			{
				finalPage.append(page.substring(0, ixTagOpen));
				page = page.substring(ixTagOpen);
				
				ixTagEnd = page.indexOf(endTag);
				ixRefStart = page.indexOf(ref);
				
				//If reference start tag is after endTag it means that the Tag doesn't include any source links
				// just continue...
				if ( ixRefStart == -1 || ixRefStart > ixTagEnd )
				{
					finalPage.append(page.substring(0, ixTagEnd));
					page = page.substring(ixTagEnd);
				}
				else
				{
					String strQuote = "";
					String url = "";
					
					ixRefStart = ixRefStart + ref.length();
					finalPage.append(page.substring(0, ixRefStart));
					page = page.substring(ixRefStart);
					
					// Check if the argument starts with a single or double quote or no quote
					if ( page.startsWith(SINGLE_QUOTE))
						strQuote = SINGLE_QUOTE;
					else if (page.startsWith(DOUBLE_QUOTE))
							strQuote = DOUBLE_QUOTE;
					
					if ( strQuote.length() > 0)
					{
						finalPage.append(strQuote);
						page = page.substring(1);
						ixRefEnd = page.indexOf(strQuote);
						
						// Extract the URL
						url = page.substring(0, ixRefEnd);
					}
					else
					{
						// Make sure that we don't parse over the tag end
						ixTagEnd = page.indexOf(endTag);
						
						// No quote just the first space or tagEnd index
						ixRefEnd = 0;
						StringBuffer nqurl = new StringBuffer();
						boolean  bEnd = false;
						
						while ( bEnd == false)
						{
							char c = page.charAt(ixRefEnd);
							
							if ( (Character.isSpaceChar(c) == false) && (ixRefEnd < ixTagEnd) )
							{
								ixRefEnd++;
								nqurl.append(c);
							}
							else
							{
								bEnd = true;
								ixRefEnd--;
							}
						}
						// Get the string
						url = nqurl.toString();
						
					}
						
					// Prepend the Action URL
					actionURL.setParameter(actionParameterName, url);
					
					finalPage.append(actionURL.toString()).append(strQuote);
					
					//Remainder
					page = page.substring(ixRefEnd+1);
				}
				
				// Continue scan
				ixTagOpen = page.indexOf(startTag);
			}
			
			finalPage.append(page);
		}
		catch(Exception e)
		{
			System.out.println("ERROR: Exception in processHREFS " + e.getMessage() );
		}
	
		internalPage = finalPage;
	}
}
