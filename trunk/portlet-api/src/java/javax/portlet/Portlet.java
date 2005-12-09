/*
 * Copyright 2003,2004 The Apache Software Foundation.
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
 * 
 * ====================================================================
 *
 * This source code implements specifications defined by the Java
 * Community Process. In order to remain compliant with the specification
 * DO NOT add / change / or delete method signatures!
 */

package javax.portlet;


/**
 * The <CODE>Portlet</CODE> interface is used by the portlet container to
 * invoke the portlets. Every portlet has to implement this interface,
 * either by directly implementing it, or by using an existing class 
 * implementing the Portlet interface.
 * <P>
 * A portlet is a Java technology-based web component. It is managed by the portlet container and
 * processes requests and generates dynamic content as response. Portlets are used by portals as
 * pluggable user interface components.
 * <p>
 * The content generated by a portlet is called a fragment. A fragment is a piece of
 * markup (e.g. HTML, XHTML, WML) adhering to certain rules and can be aggregated
 * with other fragments into a complete document. The content of a portlet is normally
 * aggregated with the content of other portlets into the portal page. 
 * <P>
 * The portlet container instanciates portlets, manages their lifecycle 
 * and invoking them to process requests. The lifecycle consists of:
 * <ul>
 * <li>initializing the portlet using using the <code>init</code> method
 * <li>request processsing
 * <li>taking the portlet out of service using the <code>destroy</code> method
 * </ul>
 * <p>
 * Request processing is divided into two types:
 * <ul>
 * <li>action requests handled through the <code>processAction</code> method, 
 *     to perform actions targeted to the portlet
 * <li>render requests handled through the <code>render</code> method, 
 *     to perform the render operation
 * </ul>
 */
public interface Portlet
{



  /**
   * Called by the portlet container to indicate to a portlet that the 
   * portlet is being placed into service.
   *
   * <p>The portlet container calls the <code>init</code>
   * method exactly once after instantiating the portlet.
   * The <code>init</code> method must complete successfully
   * before the portlet can receive any requests.
   *
   * <p>The portlet container cannot place the portlet into service
   * if the <code>init</code> method
   * <ol>
   * <li>Throws a <code>PortletException</code>
   * <li>Does not return within a time period defined by the portlet container.
   * </ol>
   *
   *
   * @param config            a <code>PortletConfig</code> object 
   *                    containing the portlet's
   *                     configuration and initialization parameters
   *
   * @exception PortletException     if an exception has occurred that
   *                    interferes with the portlet's normal
   *                    operation.
   * @exception UnavailableException     if the portlet cannot perform the initialization at this time.
   *
   *
   */

  public void init(PortletConfig config) throws PortletException;




  /**
   * Called by the portlet container to allow the portlet to process
   * an action request. This method is called if the client request was
   * originated by a URL created (by the portlet) with the 
   * <code>RenderResponse.createActionURL()</code> method.
   * <p>
   * Typically, in response to an action request, a portlet updates state 
   * based on the information sent in the action request parameters.
   * In an action the portlet may:
   * <ul>
   * <li>issue a redirect
   * <li>change its window state
   * <li>change its portlet mode
   * <li>modify its persistent state
   * <li>set render parameters
   * </ul>
   * <p>
   * A client request triggered by an action URL translates into one 
   * action request and many render requests, one per portlet in the portal page.
   * The action processing must be finished before the render requests
   * can be issued.
   *
   * @param request
   *                 the action request
   * @param response
   *                 the action response
   * @exception  PortletException
   *                   if the portlet has problems fulfilling the
   *                   request
   * @exception  UnavailableException     
   *                   if the portlet is unavailable to process the action at this time
   * @exception  PortletSecurityException  
   *                   if the portlet cannot fullfill this request because of security reasons
   * @exception  IOException
   *                   if the streaming causes an I/O problem
   */
  public void processAction (ActionRequest request, ActionResponse response) 
    throws PortletException, java.io.IOException;



  /**
   * Called by the portlet container to allow the portlet to generate
   * the content of the response based on its current state.
   *
   * @param   request
   *          the render request
   * @param   response
   *          the render response
   *
   * @exception   PortletException
   *              if the portlet has problems fulfilling the
   *              rendering request
   * @exception  UnavailableException     
   *                   if the portlet is unavailable to perform render at this time
   * @exception  PortletSecurityException  
   *                   if the portlet cannot fullfill this request because of security reasons
   * @exception  java.io.IOException
   *              if the streaming causes an I/O problem
   */

  public void render (RenderRequest request, RenderResponse response) 
    throws PortletException, java.io.IOException;


  /**
   *
   * Called by the portlet container to indicate to a portlet that the
   * portlet is being taken out of service.  
   * <p>
   * Before the portlet container calls the destroy method, it should 
   * allow any threads that are currently processing requests within 
   * the portlet object to complete execution. To avoid
   * waiting forever, the portlet container can optionally wait for 
   * a predefined time before destroying the portlet object.
   *
   * <p>This method enables the portlet to do the following:
   * <ul>
   * <li>clean up any resources that it holds (for example, memory,
   * file handles, threads) 
   * <li>make sure that any persistent state is
   * synchronized with the portlet current state in memory.
   * </ul>
   */
  
  public void destroy();
}
