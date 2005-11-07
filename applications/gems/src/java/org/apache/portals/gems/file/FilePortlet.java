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
package org.apache.portals.gems.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletPreferences;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.portals.bridges.common.GenericServletPortlet;


/**
 * FilePortlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class FilePortlet extends GenericServletPortlet
{
    
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        // NOTE: this is Jetspeed specific
        String path = (String)request.getAttribute(PortalReservedParameters.PATH_ATTRIBUTE);
        if (null == path)
        {
            PortletPreferences prefs = request.getPreferences();
            path = prefs.getValue("file", null);            
        }
        else
        {
            // default to 'content' area
            File temp = new File(path);             
            path = "/WEB-INF/" + temp.getPath();            
        }
        if (null == path)
        {
            response.setContentType("text/html");
            response.getWriter().println("Could not find source document.");            
        }
        else
        {
            setContentType(path, response);        
            renderFile(response, path);
        }        
    }

    protected void setContentType(String path, RenderResponse response)
    {
        if (path.endsWith(".html"))
        {
            response.setContentType("text/html");
        }
        else if (path.endsWith(".pdf"))
        {
            response.setContentType("application/pdf");
        }
        else if (path.endsWith(".zip"))
        {
            response.setContentType("application/zip");
        }        
        else
        {
            response.setContentType("text/html");
        }
    }
    
    protected void renderFile(RenderResponse response, String fileName)
    throws PortletException, IOException
    {
        InputStream is = this.getPortletContext().getResourceAsStream(fileName);
        drain(is, response.getPortletOutputStream());
        response.getPortletOutputStream().flush();
        is.close();        
    }
    
    
    static final int BLOCK_SIZE=4096;

    public static void drain(InputStream r,OutputStream w) throws IOException
    {
        byte[] bytes=new byte[BLOCK_SIZE];
        try
        {
          int length=r.read(bytes);
          while(length!=-1)
          {
              if(length!=0)
                  {
                      w.write(bytes,0,length);
                  }
              length=r.read(bytes);
          }
      }
      finally
      {
        bytes=null;
      }

    }
   
    
}
