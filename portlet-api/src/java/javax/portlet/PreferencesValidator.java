package javax.portlet;


/**
 * The <CODE>PreferencesValidator</CODE> allows the portlet to validate
 * the preference settings before they are stored in the persistent store.
 * <p>
 * The validator is called each time before the <code>store</code> method on the preferences
 * is performed.
 */
public interface PreferencesValidator
{




  /**
   * Throws a <code>ValidatorException</code> if the given preferences contains 
   * invalid settings.
   *
   * @param  preferences   preferences to validate
   *
   * @throws  ValidatorException  if the given preferences contains invalid
   *                              settings
   *
   */

  public void validate(PortletPreferences preferences)
    throws ValidatorException;  
}
