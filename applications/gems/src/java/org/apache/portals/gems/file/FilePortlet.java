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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletPreferences;

import org.apache.portals.bridges.common.GenericServletPortlet;


/**
 * FilePortlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class FilePortlet extends GenericServletPortlet
{
    
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");
        PortletPreferences prefs = request.getPreferences();
        String fileName = prefs.getValue("file", null);
        if (fileName != null)
        {
            InputStream is = this.getPortletContext().getResourceAsStream(fileName);
            drain(is, response.getPortletOutputStream());
            is.close();
        }
        else
        {
            response.getWriter().println("Could not find file preference ");
        }
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
