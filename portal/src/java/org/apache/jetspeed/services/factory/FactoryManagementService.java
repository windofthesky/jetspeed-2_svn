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
package org.apache.jetspeed.services.factory;

import org.apache.jetspeed.cps.CommonService;
import org.apache.pluto.factory.Factory;

/**
 * <p>
 * Manages the life-time of portal-to-container shared factories as defined by Pluto's factory interfaces.
 * A factory must derive from <a href='http://jakarta.apache.org/pluto/apidocs/org/apache/pluto/factory/Factory.html'>org.apache.pluto.factory.Factory</a> and implement the
 * <CODE>init()</CODE> and <CODE>destroy()</CODE> methods to meet Pluto's factory contract.
 * Factories create the shared classes between the portal and Pluto container. 
 * Implementations are created by portal provided factories. Most of the shared
 * classes are implementations of the Java Portlet API interfaces. 
 * <p>
 * Factory Managed Interfaces per Pluto requirements:
 * <p> 
 * <a href='http://www.bluesunrise.com/portlet-api/javax/portlet/ActionRequest.html'>javax.portlet.ActionRequest</a><br>
 * <a href='http://www.bluesunrise.com/portlet-api/javax/portlet/ActionResponse.html'>javax.portlet.ActionResponse</a><br>
 * <a href='http://www.bluesunrise.com/portlet-api/javax/portlet/RenderRequest.html'>javax.portlet.RenderRequest</a><br>
 * <a href='http://www.bluesunrise.com/portlet-api/javax/portlet/RenderResponse.html'>javax.portlet.RenderResponse</a><br>
 * <a href='http://www.bluesunrise.com/portlet-api/javax/portlet/PortletSession.html'>javax.portlet.PortletSession</a><br>
 * <a href='http://www.bluesunrise.com/portlet-api/javax/portlet/PortletConfig.html'>javax.portlet.PortletConfig</a><br>
 * <a href='http://www.bluesunrise.com/portlet-api/javax/portlet/PortletContext.html'>javax.portlet.PortletContext</a><br>
 * <a href='http://www.bluesunrise.com/portlet-api/javax/portlet/PortletPreferences.html'>javax.portlet.PortletPreferences</a><br>
 * <a href='http://www.bluesunrise.com/portlet-api/javax/portlet/PortalContext.html'>javax.portlet.PortalContext</a><br>
 * <a href='http://www.bluesunrise.com/portlet-api/javax/portlet/PortletURL.html'>javax.portlet.PortletURL</a><br>
 * <a href='http://www.bluesunrise.com/portlet-api/javax/portlet/PortletPreferences.html'>javax.portlet.PortletPreferences</a><br>
 * <a href='http://www.bluesunrise.com/portlet-api/javax/portlet/PreferencesValidator.html'>javax.portlet.PreferencesValidator</a><br>
 * <a href='http://java.sun.com/products/servlet/2.3/javadoc/javax/servlet/http/HttpServletRequest.html'>javax.servlet.http.HttpServletRequest</a><br>
 * <a href='http://java.sun.com/products/servlet/2.3/javadoc/javax/servlet/http/HttpServletResponse.html'>javax.servlet.http.HttpServletResponse</a><br>
 * <a href='http://jakarta.apache.org/pluto/apidocs/org/apache/pluto/invoker/PortletInvoker.html'>org.apache.pluto.invoker.PortletInvoker</a><br>
 * <a href='http://jakarta.apache.org/pluto/apidocs/org/apache/pluto/util/NamespaceMapper.html'>org.apache.pluto.util.NamespaceMapper</a><br>
 * <a href='http://jakarta.apache.org/pluto/apidocs/org/apache/pluto/om/ControllerFactory.html'>org.apache.pluto.om.ControllerFactory</a><br>
 * <p>
 * Pluto Service Providers
 * <p>
 * <a href='http://jakarta.apache.org/pluto/apidocs/org/apache/pluto/services/information/StaticInformationProvider.html'>org.apache.pluto.services.information.InformationProviderService</a><br>
 * <a href='http://jakarta.apache.org/pluto/apidocs/org/apache/pluto/services/information/DynamicInformationProvider.html'>org.apache.pluto.services.information.DynamicInformationProvider</a><br>
 * <a href='http://jakarta.apache.org/pluto/apidocs/org/apache/pluto/services/information/PortletActionProvider.html'>org.apache.pluto.services.information.PortletActionProvider</a><br>
 * <a href='http://jakarta.apache.org/pluto/apidocs/org/apache/pluto/services/information/PortalContextProvider.html'>org.apache.pluto.services.information.PortalContextProvider</a><br>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface FactoryManagementService 
    extends CommonService
{
    public String SERVICE_NAME = "factory";
    
    /**
     * Returns the factory implementation for the given Pluto managed interface, or
     * <CODE>null</CODE> if no such factory is registered for the given Pluto interface.
     * Pluto uses factories for creating portal implementations of shared classes (services) 
     * between Pluto (the container) and the portal. For example, for the Portlet standard's 
     * RenderRequest and RenderResponse interfaces, its up to the portal to create the implementations 
     * of these interfaces and provide the request or response to the container. With Pluto,
     * the implementations are created by portal provided factories. For each factory-managed interface,
     * its up to the portal to provide a factory for that interface. 
     *
     * @param    managedInterface The known interface who's factory we are requesting.
     * @return   The portal's portal factory for this interface, used to create a portal/container service.
     */
    public Factory getFactory (Class managedInterface);
        
}
