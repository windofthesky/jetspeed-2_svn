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
package org.apache.jetspeed.factory;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;

import org.apache.jetspeed.factory.PortletInstance;

/**
 * JetspeedPortletInstance
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 *
 */
public class JetspeedPortletInstance implements PortletInstance
{
  private Portlet portlet;
  private PortletConfig config;
  private boolean destroyed;
  private final String portletName;
  
  public JetspeedPortletInstance(String portletName, Portlet portlet)
  {
      this.portletName = portletName;
      this.portlet = portlet;
  }
  
  private void checkAvailable() throws UnavailableException
  {
      if ( destroyed )
      {
          throw new UnavailableException("Portlet "+portletName+" no longer available");
      }
  }
  
  public void destroy()
  {
      if (!destroyed)
      {
          destroyed = true;
          if ( config != null )
          {
              // Portlet really has been put into service, now destroy it.
              portlet.destroy();
          }
      }
  }
  
  public boolean equals(Object obj)
  {
    return portlet.equals(obj);
  }
  
  public int hashCode()
  {
    return portlet.hashCode();
  }
  
  public void init(PortletConfig config) throws PortletException
  {
    portlet.init(config);
    this.config = config;
  }
  
  public PortletConfig getConfig()
  {
      return config;
  }
  
  public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
  {
    checkAvailable();
    portlet.processAction(request, response);
  }
  
  public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException
  {
    checkAvailable();
    portlet.render(request, response);
  }
  
  public String toString()
  {
      return portlet.toString();
  }
}
