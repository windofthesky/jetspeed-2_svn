package javax.portlet;

/**
 * The <CODE>PortletSessionUtil</CODE> class helps with the decoding 
 * of portlet session attributes from the <code>HttpSession</code> that the
 * <code>PortletSession</code> uses.
 *
 */
public class PortletSessionUtil
{

  private static final String PORTLET_SCOPE_NAMESPACE = "javax.portlet.p.";
  
  /**
   * Returns the portlet attribute name from an encoded portlet
   * attribute.
   *
   * @param name		a string specifying the name of the
   *                            encoded portlet attribute
   *
   * @return			the decoded attribute name
   */

  public static java.lang.String decodeAttribute(java.lang.String name)
  {
    if (name.startsWith(PORTLET_SCOPE_NAMESPACE)) {
      int index = name.indexOf('?');
      if (index>-1) {
	name = name.substring(index+1);
      }
    }
    return name;
  }


  /**
   * Returns the portlet attribute scope from an encoded portlet
   * attribute.
   *
   * @param name		a string specifying the name of the
   *                            encoded portlet attribute
   *
   * @return			the decoded attribute scope
   * @see PortletSession
   */

  public static int decodeScope(java.lang.String name)
  {
    int scope = PortletSession.APPLICATION_SCOPE; // APP
    if (name.startsWith(PORTLET_SCOPE_NAMESPACE)) {
      int index = name.indexOf('?');
      if (index>-1) {
	scope = PortletSession.PORTLET_SCOPE; // PORTLET
      }
    }
    return scope;
  }
}


