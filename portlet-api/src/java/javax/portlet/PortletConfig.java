package javax.portlet;




/**
 * The <CODE>PortletConfig</CODE> interface provides the portlet with
 * its configuration. The configuration holds information about the
 * portlet that is valid for all users. The configuration is maintained by the
 * developer. The portlet can only read the configuration data.
 * 
 * <p>The configuration information contains 
 * <ul>
 * <il>the portlet name
 * <il>initialization parameters
 * <il>access to the <code>PortletContext</code> object
 * <il>access to the portlet <code>ResourceBundle</code>
 * </ul>
 *
 * @see Portlet
 */
public interface PortletConfig
{


  /**
   * Returns the name of the portlet. The portlet container needs the
   * portlet name for administration purposes.
   * <P>
   * The name may be provided via server administration, or is the name
   * assigned in the deployment descriptor with the <code>portlet-name</code>
   * tag.
   *
   * @return   the portlet name
   */

  public String getPortletName ();


  /**
   * Returns a reference to the <code>PortletContext</code> in which 
   * the caller is executing.
   *
   * @return   a <code>PortletContext</code> object, used by the 
   *           caller to interact with its portlet container
   *
   * @see PortletContext
   */

  public PortletContext getPortletContext ();


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
   * @param    locale    the locale for which to retrieve the resource bundle
   * 
   * @return   the resource bundle for the given locale
   *
   */

  public java.util.ResourceBundle getResourceBundle(java.util.Locale locale);


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

  public String getInitParameter(java.lang.String name);


  /**
   * Returns the names of the portlet initialization parameters as an 
   * <code>Enumeration</code> of String objects, or an empty <code>Enumeration</code> if the 
   * portlet has no initialization parameters.    
   *
   * @return		an <code>Enumeration</code> of <code>String</code> 
   *			objects containing the names of the portlet 
   *			initialization parameters, or an empty <code>Enumeration</code> if the 
   *                    portlet has no initialization parameters. 
   */

  public java.util.Enumeration getInitParameterNames();
}

