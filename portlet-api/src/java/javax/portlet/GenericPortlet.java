package javax.portlet;



/**
 * The <CODE>GenericPortlet</CODE> class provides a default implementation
 * for the <CODE>Portlet</CODE> interface. 
 * <p>
 * It is recommended not to extend the Portlet interface directly. 
 * Rather, a portlet should derive
 * from this or any other derived class and use the provided helper
 * methods for the different modes.
 */
public abstract class GenericPortlet implements Portlet, PortletConfig
{

  private transient PortletConfig config;

  /**
   * Does nothing.
   */

  public GenericPortlet()
  {
  }


  /**
   * Called by the portlet container to indicate to a portlet that the 
   * portlet is being placed into service.
   * <p>
   * The default implementation just stores the <code>PortletConfig</code>
   * object.
   * <p>The portlet container calls the <code>init</code>
   * method exactly once after instantiating the portlet.
   * The <code>init</code> method must complete successfully
   * before the portlet can receive any requests.
   *
   * <p>The portlet container cannot place the portlet into service
   * if the <code>init</code> method does one of the following:
   * <ol>
   * <li>it throws a <code>PortletException</code>
   * <li>it does not return within a time period defined by the Web server
   * </ol>
   *
   *
   * @param config			a <code>PortletConfig</code> object 
   *					containing the portlet
   * 					configuration and initialization parameters
   *
   * @exception PortletException 	if an exception has occurred that
   *					interferes with the portlet normal
   *					operation.
   * @exception UnavailableException 	if the portlet is unavailable to perform init
   */

  public void init (PortletConfig config) throws PortletException
  {
    this.config = config;
    config.getPortletContext().log("init");
    this.init();
  }

  
  /**
   *
   * A convenience method which can be overridden so that there's no need
   * to call <code>super.init(config)</code>.
   *
   * <p>Instead of overriding {@link #init(PortletConfig)}, simply override
   * this method and it will be called by
   * <code>GenericPortlet.init(PortletConfig config)</code>.
   * The <code>PortletConfig</code> object can still be retrieved via {@link
   * #getPortletConfig}. 
   *
   * @exception PortletException 	if an exception has occurred that
   *					interferes with the portlet normal
   *					operation.
   * @exception UnavailableException 	if the portlet is unavailable to perform init
   */
    
  public void init() throws PortletException
  {
  }


  /**
   * Notifies the portlet which is called by the portlet container
   * that an action request has been performed.
   * <p>
   * The default implementation throws an exception.
   *
   * @param request
   *                 the action request
   * @param response
   *                 the action response
   * @exception PortletException
   *                   if the portlet cannot fulfilling the request
   * @exception  UnavailableException 	
   *                   if the portlet is unavailable to perform processAction
   * @exception  PortletSecurityException  
   *                   if the portlet cannot fullfill this request because of security reasons
   * @exception  IOException
   *                   if the streaming causes an I/O problem
   */
  public void processAction (ActionRequest request, ActionResponse response) 
    throws PortletException, java.io.IOException {
    throw new PortletException("processAction method not implemented");
  }


  /**
   * The default implementation of this method sets the title 
   * using the <code>getTitle</code> method and performs
   * the rendering using the <code>doDispatch</code> method.
   * 
   * @param request
   *                 the render request
   * @param response
   *                 the render response
   *
   * @exception PortletException
   *                   if the portlet cannot fulfilling the request
   * @exception  UnavailableException 	
   *                   if the portlet is unavailable to perform render
   * @exception  PortletSecurityException  
   *                   if the portlet cannot fullfill this request because of security reasons
   * @exception IOException
   *                   if the streaming causes an I/O problem
   *
   */
  public void render (RenderRequest request,
		      RenderResponse response)
    throws PortletException, java.io.IOException
  {
    response.setTitle(getTitle(request));
    doDispatch(request, response);
  }
  


  /**
   * Used by the render method to get the title.
   * <p>
   * The default implementation gets the title
   * defined in the deployment descriptor.
   * <p>
   * Portlets can overwrite this method to provide dynamic
   * titles (e.g. based on locale, client,
   * and session information).
   * Examples are:
   * <UL>
   * <LI>language-dependant titles for multi-lingual portals
   * <LI>shorter titles for WAP phones
   * <LI>the number of messages in a mailbox portlet
   * </UL>
   * 
   * @return the portlet title for this window
   */

  protected java.lang.String getTitle(RenderRequest request) {
    return config.getResourceBundle(request.getLocale()).getString("javax.portlet.title");
  }


  /**
   * The default implementation of this method routes the render request
   * to a set of helper methods based on the portlet mode. 
   * These methods are:
   * <ul>
   * <il><code>doView</code> for handling <code>view</code> requests
   * <il><code>doEdit</code> for handling <code>edit</code> requests
   * <il><code>doHelp</code> for handling <code>help</code> requests
   * </ul>
   * <P>
   * If the window state of this portlet is <code>minimized</code>, this
   * method does not invoke any of the portlet mode rendering methods.
   * <p>
   * For handling custom portlet modes the portlet should override this
   * method.
   *
   * @param request
   *                 the render request
   * @param response
   *                 the render response
   *
   * @exception PortletException
   *                   if the portlet cannot fulfilling the request
   * @exception  UnavailableException 	
   *                   if the portlet is unavailable to perform render
   * @exception  PortletSecurityException  
   *                   if the portlet cannot fullfill this request because of security reasons
   * @exception IOException
   *                   if the streaming causes an I/O problem
   *
   * @see #doView(RenderRequest, RenderResponse)
   * @see #doEdit(RenderRequest, RenderResponse)
   * @see #doHelp(RenderRequest, RenderResponse)
   */
  protected void doDispatch (RenderRequest request,
			  RenderResponse response) throws PortletException,java.io.IOException
  {
    WindowState state = request.getWindowState();
    
    if ( ! state.equals(WindowState.MINIMIZED)) {
      PortletMode mode = request.getPortletMode();
      if (mode.equals(PortletMode.VIEW)) {
	doView (request, response);
      }
      else if (mode.equals(PortletMode.EDIT)) {
	doEdit (request, response);
      }
      else if (mode.equals(PortletMode.HELP)) {
	doHelp (request, response);
      }
      else {
	throw new PortletException("unknown portlet mode: " + mode);
      }
    }

  }


  /**
   * Helper method to serve up the mandatory <code>view</code> mode.
   * <p>
   * The default implementation throws an exception.
   *
   * @param    request
   *           the portlet request
   * @param    response
   *           the render response
   *
   * @exception PortletException
   *                   if the portlet cannot fulfilling the request
   * @exception  UnavailableException 	
   *                   if the portlet is unavailable to perform render
   * @exception  PortletSecurityException  
   *                   if the portlet cannot fullfill this request because of security reasons
   * @exception IOException
   *                   if the streaming causes an I/O problem
   *
   */

  protected void doView (RenderRequest request,
		      RenderResponse response)
    throws PortletException, java.io.IOException
  {
    throw new PortletException("doView method not implemented");
  }


  /**
   * Helper method to serve up the <code>edit</code> mode.
   * <p>
   * The default implementation throws an exception.
   *
   * @param    request
   *           the portlet request
   * @param    response
   *           the render response
   *
   * @exception PortletException
   *                   if the portlet cannot fulfilling the request
   * @exception  UnavailableException 	
   *                   if the portlet is unavailable to perform render
   * @exception  PortletSecurityException  
   *                   if the portlet cannot fullfill this request because of security reasons
   * @exception IOException
   *                   if the streaming causes an I/O problem
   *
   */

  protected void doEdit (RenderRequest request,
		      RenderResponse response)
    throws PortletException, java.io.IOException
  {
    throw new PortletException("doEdit method not implemented");
  }

  /**
   * Helper method to serve up the <code>help</code> mode.
   * <p>
   * The default implementation throws an exception.
   *
   * @param    request
   *           the portlet request
   * @param    response
   *           the render response
   *
   * @exception PortletException
   *                   if the portlet cannot fulfilling the request
   * @exception  UnavailableException 	
   *                   if the portlet is unavailable to perform render
   * @exception  PortletSecurityException  
   *                   if the portlet cannot fullfill this request because of security reasons
   * @exception IOException
   *                   if the streaming causes an I/O problem
   */

  protected void doHelp (RenderRequest request,
		      RenderResponse response)
    throws PortletException, java.io.IOException
  {
    throw new PortletException("doHelp method not implemented");

  }



  /**
   * Returns the PortletConfig object of this portlet.
   *
   * @return   the PortletConfig object of this portlet
   */

  public PortletConfig getPortletConfig ()
  {
    return config;
  }

  
  /**
   * Called by the portlet container to indicate to a portlet that the portlet 
   * is being taken out of service.
   * <p>
   * The default implementation does nothing.
   *
   */
  
  public void destroy ()
  {
    getPortletContext().log("destroy");
    // do nothing
  }

  //-------------------------------------------------------------------------
  // implement PortletConfig
  //-------------------------------------------------------------------------


  /**
   * Returns the name of the portlet. The portlet container needs the
   * portlet name for administration purposes.
   *
   * @return   the portlet name
   */

  public String getPortletName ()
  {
  	return config.getPortletName();
  }


  /**
   * Returns the portlet application context.
   *
   * @return   the portlet application context
   */

  public PortletContext getPortletContext ()
  {
  	return config.getPortletContext();
  }



  /**
   * Gets the resource bundle for the given locale based on the
   * resource bundle defined in the deployment descriptor
   * with <code>resource-bundle</code> tag or the inlined resources
   * defined in the deployment descriptor.
   * <p>
   * If the resources are included inline the deployment descriptor, 
   * no localization support is provided by the portlet container. 
   * The defined values will be used for all Locales.
   * 
   * @return   the resource bundle for the given locale
   */

  public java.util.ResourceBundle getResourceBundle(java.util.Locale locale)
  {
  	return config.getResourceBundle(locale);
  }

  
  /**
   * Returns a String containing the value of the named initialization parameter, 
   * or null if the parameter does not exist.
   *
   * @param name	a <code>String</code> specifying the name
   *			of the initialization parameter
   *
   * @return		a <code>String</code> containing the value 
   *			of the initialization parameter
   *
   * @exception	java.lang.IllegalArgumentException	
   *                      if name is <code>null</code>.
   */

  public String getInitParameter(java.lang.String name)
  {
  	return config.getInitParameter(name);
  }


  /**
   * Returns the names of the portlet initialization parameters as an 
   * Enumeration of String objects, or an empty Enumeration if the 
   * portlet has no initialization parameters.    
   * 
   * @return		an <code>Enumeration</code> of <code>String</code> 
   *			objects containing the names of the portlet 
   *			initialization parameters, or an empty Enumeration if the 
   *                    portlet has no initialization parameters. 
   */

  public java.util.Enumeration getInitParameterNames()
  {
  	return config.getInitParameterNames();
  }
}
