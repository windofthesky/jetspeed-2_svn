package javax.portlet;

/**
 * The <CODE>UnmodifiableException</CODE> is thrown when 
 * a portlet tries to change the value for a preference 
 * attribute, marked as unmodifiable in the deployment descriptor
 * with the <code>non-modifiable</code> tag, without
 * the needed permissions.
 */

public class UnmodifiableException extends PortletException
{

  private UnmodifiableException ()
  {
  }

  /**
   * Constructs a new unmodifiable exception with the given text. The
   * portlet container may use the text write it to a log.
   *
   * @param   text
   *          the exception text
   */

  public UnmodifiableException (String text)
  {
    super (text);
  }

  /**
   * Constructs a new unmodifiable exception when the portlet needs to do
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
  
  public UnmodifiableException (String text, Throwable cause)
  {
    super(text, cause);
  }

  /**
   * Constructs a new unmodifiable exception when the portlet needs to throw an
   * exception. The exception message is based on the localized message
   * of the underlying exception.
   *
   * @param   cause
   *          the root cause
   */

  public UnmodifiableException (Throwable cause)
  {
    super(cause);
  }


}
