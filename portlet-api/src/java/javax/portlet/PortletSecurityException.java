package javax.portlet;

/**
 * The <CODE>PortletSecurityException</CODE> is thrown when a 
 * call fails because of security reasons.
 */

public class PortletSecurityException extends PortletException
{

  private PortletSecurityException ()
  {
  }

  /**
   * Constructs a new security exception with the given text. The
   * portlet container may use the text write it to a log.
   *
   * @param   text
   *          the exception text
   */

  public PortletSecurityException (String text)
  {
    super (text);
  }

  /**
   * Constructs a new portlet security exception when the portlet needs to do
   * the following:
   * <ul>
   * <il>throw an exception 
   * <li>include a message about the "root cause" that interfered
   *     with its normal operation
   * <li>include a description message
   * </ul>
   *
   * @param   text
   *          the exception text
   * @param   cause
   *          the root cause
   */
  
  public PortletSecurityException (String text, Throwable cause)
  {
    super(text, cause);
  }

  /**
   * Constructs a new portlet security exception when the portlet needs to throw an
   * exception. The exception message is based on the localized message
   * of the underlying exception.
   *
   * @param   cause
   *          the root cause
   */

  public PortletSecurityException (Throwable cause)
  {
    super(cause);
  }


}
