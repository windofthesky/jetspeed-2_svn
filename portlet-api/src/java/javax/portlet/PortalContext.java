package javax.portlet;



/**
 * The <CODE>PortalContext</CODE> interface gives the portlet
 * the ability to retrieve information about the portal calling this portlet.
 * <p>
 * The portlet can only read the <CODE>PortalContext</CODE> data.
 */
public interface PortalContext
{

  
  /**
   * Returns the portal property with the given name, 
   * or a <code>null</code> if there is 
   * no property by that name.
   *
   * @param  name    property name
   *
   * @return  portal property with key <code>name</code>
   *
   * @exception	java.lang.IllegalArgumentException	
   *                      if name is <code>null</code>.
   */

  public java.lang.String getProperty(java.lang.String name);


  /**
   * Returns all portal property names as strings,
   * or an empty <code>Enumeration</code> if 
   * there are no property names.
   *
   * @return  portal property names
   */
  public java.util.Enumeration getPropertyNames();


  /**
   * Returns the portlet modes that the portal supports.
   * <p>
   * The portlet modes must at least include the
   * standard portlet modes <code>EDIT, HELP, VIEW</code>.
   *
   * @return  list of supported portlet modes
   */

  public java.util.Enumeration getSupportedPortletModes();


  /**
   * Returns the window states that the portal supports.
   * <p>
   * The window states must at least include the
   * standard window states <code> MINIMIZED, NORMAL, MAXIMIZED</code>.
   *
   * @return  list of supported window states
   */

  public java.util.Enumeration getSupportedWindowStates();


  /**
   * Returns information about the portal like vendor, version, etc.
   * <p>
   * The returned string should start with <br>
   * <code>vendorname.majorversion.minorversion.</code>
   */

  public java.lang.String getPortalInfo();
}
