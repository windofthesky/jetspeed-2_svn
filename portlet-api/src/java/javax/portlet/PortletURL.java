package javax.portlet;



/**
 * The <CODE>PortletURL</CODE> interface represents a URL 
 * that reference the portlet itself.
 * <p>
 * A URL is created through the <CODE>RenderResponse</CODE> and
 * additional parameters can be added to this URL. The complete URL can 
 * be converted to a String
 * which is ready for embedding into markup.
 * <P>
 * There are two types of PortletURLs: 
 * <ul>
 * <li>render URLs created with <code>RenderResponse.createRenderURL</code> as optimzed action, 
 *     only setting the render parameters
 * <li>action URLs created with <code>RenderResponse.createActionURL</code> for
 *     triggering an action request
 * </ul>
 * <p>
 * A portlet URL does not need to be a valid URL when converted to a String. It may contain
 * special tokens that will be converted to a URL by the portal.
 */
public interface PortletURL
{



  /**
   * Indicates the window state the portlet should be in, if this 
   * portlet URL triggers a request.
   * <p>
   * A URL can not have more than one window state attached to it.
   * If more than one window state is set only the last one set
   * is attached to the URL.
   * 
   * @param windowState
   *               the portlet window state
   *
   * @exception WindowStateException
   *                   if the portlet cannot switch to this state,
   *                   because the portal does not support this state, the portlet has not 
   *                   declared in its deployment descriptor that it supports this state, or the current
   *                   user is not allowed to switch to this state.
   *                   To avoid this exception the portlet can check the allowed
   *                   window states with <code>Request.isWindowStateAllowed()</code>.
   */
  public void setWindowState (WindowState windowState)
    throws WindowStateException;


  /**
   * Indicates the portlet mode the portlet must be in, if this 
   * portlet URL triggers a request.
   * <p>
   * A URL can not have more than one portlet mode attached to it.
   * If more than one portlet mode is set only the last one set
   * is attached to the URL.
   *
   * @param portletMode
   *               the portlet mode
   *
   * @exception PortletModeException
   *                   if the portlet cannot switch to this mode,
   *                   because the portal does not support this mode, the portlet has not 
   *                   declared in its deployment descriptor that it supports this mode for the current markup,
   *                   or the current user is not allowed to switch to this mode
   *                   To avoid this exception the portlet can check the allowed
   *                   portlet modes with <code>Request.isPortletModeAllowed()</code>.
   */
  public void setPortletMode (PortletMode portletMode)
    throws PortletModeException;


  /**
   * Adds the given String parameter to this URL. 
   * <p>
   * This method does not url encode parameters. Parameters that need
   * url encoding must be "x-www-form-urlencoded" before using the
   * <code>addParameter</code> method, e.g. by using the
   * <code>java.net.URLEncoder.encode</code> method.
   * <p>
   * A portlet container may prefix the attribute names internally 
   * in order to preserve a unique namespace for the portlet.
   *
   * @param   name
   *          the parameter name
   * @param   value
   *          the parameter value
   *
   * @exception  java.lang.IllegalArgumentException 
   *                            if name or value is <code>null</code>.
   */

  public void addParameter (String name, String value);


  /**
   * Adds the given String array parameter to this URL. 
   * <p>
   * This method does not url encode parameters. Parameters that need
   * url encoding must be "x-www-form-urlencoded" before using the
   * <code>addParameter</code> method, e.g. by using the
   * <code>java.net.URLEncoder.encode</code> method.
   * <p>
   * A portlet container may prefix the attribute names internally 
   * in order to preserve a unique namespace for the portlet.
   *
   * @param   name
   *          the parameter name
   * @param   values
   *          the parameter values
   *
   * @exception  java.lang.IllegalArgumentException 
   *                            if name or values is <code>null</code>.
   */

  public void addParameter (String name, String[] values);


  /**
   * Sets a parameter map for this URL.
   * <p>
   * All previously set parameters are cleared.
   * <p>
   * A portlet container
   * may prefix the attribute names internally, in order to preserve
   * a unique namespace for the portlet.
   *
   * @param  parameters   Map containing parameter names for 
   *                      the render phase as 
   *                      keys and parameter values as map 
   *                      values. The keys in the parameter
   *                      map must be of type String. The values 
   *                      in the parameter map must be of type
   *                      String array (<code>String[]</code>).
   *
   * @exception  java.lang.IllegalArgumentException 
   *                            if parameters is <code>null</code>.
   */

  public void setParameters(java.util.Map parameters);


  /**
   * Indicated the security setting for this URL. 
   * <p>
   * Secure set to <code>true</code> indicates that the portlet requests
   * a secure connection between the client and the portlet window for
   * this URL. Secure set to <code>false</code> indicates that the portlet 
   * does not need a secure connection for this URL. If the security is not
   * set for a URL, it will stay the same as the current request. 
   * <p>
   * NOTE: using this URL may result in loosing the current session,
   * depending on the session implementation.
   *
   * @param  secure  true, if portlet requests to have a secure connection
   *                 between its portlet window and the client; false, if
   *                 the portlet does not require a secure connection.
   *
   * @throws PortletSecurityException  if the run-time environment does
   *                                   not support the indicated setting
   */

  public void setSecure (boolean secure) throws PortletSecurityException;


  /**
   * Returns the complete URL as a string.
   * The string is ready to be embedded in markup.
   *
   * @return   the encoded URL as a string
   */

  public String toString ();
}
