package javax.portlet;



/**
 * The <CODE>PortletPreferences</CODE> interface allows the portlet to store 
 * configuration data. It is not the
 * purpose of this interface to replace general purpose databases.
 * <p>
 * There are two different types of preferences:
 * <ul>
 * <li>modifiable preferences - these preferences can be changed by the
 *     portlet in any standard portlet mode (<code>EDIT, HELP, VIEW</code>). 
 *     Per default every preference is modifiable.
 * <li>non-modifiable preferences - these preferences cannot be changed by the
 *     portlet in any standard portlet mode, but may be changed by administrative modes.
 *     Preferences are non-modifiable, if the are defined in the 
 *     deployment descriptor with <code>modifiable</code> set to zero.
 * </ul>
 * <p>
 * Changes are persisted when the <code>store</code> method is called. 
 * Changes that are not persisted are discarded when the 
 * <code>processAction</code> or <code>render</code> method ends.
 */
public interface PortletPreferences
{



  /**
   * Returns true, if the value of this key can be modified by the user.
   * <p>
   * Modifiable preferences can be changed by the
   * portlet in any standard portlet mode (<code>EDIT, HELP, VIEW</code>). 
   * Per default every preference is modifiable.
   * <p>
   * Non-modifiable preferences cannot be changed by the
   * portlet in any standard portlet mode, but may be changed by administrative modes.
   * Preferences are non-modifiable, if they are defined in the 
   * deployment descriptor with <code>modifiable</code> set to zero.
   *
   * @return  true, if the value of this key can be changed, or
   *          if the key is not known
   *
   * @exception java.lang.IllegalArgumentException 
   *         if <code>key</code> is <code>null</code>.
   */
  public boolean isModifiable(String key);



  /**
   * Returns the first String value associated with the specified key in this preference.
   * Returns the specified default if there is no value associated
   * with the key, or if the backing store is inaccessible.
   *
   * <p>Some implementations may store default values in their backing
   * stores.  If there is no value associated with the specified key
   * but there is such a <i>stored default</i>, it is returned in
   * preference to the specified default.
   *
   * @param key key for which the associated value is to be returned
   * @param def the value to be returned in the event that there is no 
   *            value available associated with this <code>key</code>.
   *
   * @return the value associated with <code>key</code>, or <code>def</code>
   *         if no value is associated with <code>key</code>, or the backing
   *         store is inaccessible.
   *
   * @exception java.lang.IllegalArgumentException 
   *         if <code>key</code> is <code>null</code>.  (A 
   *         <code>null</code> value for <code>def</code> <i>is</i> permitted.)
   * 
   * @see #getValues(String, String[])
   */
  public String getValue(String key, String def);


  /**
   * Returns the String array value associated with the specified key in this preference.
   *
   * <p>Returns the specified default if there is no value
   * associated with the key, or if the backing store is inaccessible.
   *
   * <p>If the implementation supports <i>stored defaults</i> and such a
   * default exists and is accessible, it is used in favor of the
   * specified default.
   *
   *
   * @param key key for which associated value is to be returned.
   * @param def the value to be returned in the event that this
   *        preference node has no value associated with <code>key</code>
   *        or the associated value cannot be interpreted as a String array,
   *        or the backing store is inaccessible.
   *
   * @return the String array value associated with
   *         <code>key</code>, or <code>def</code> if the
   *         associated value does not exist.
   *
   * @exception java.lang.IllegalArgumentException if <code>key</code> is <code>null</code>.  (A 
   *         <code>null</code> value for <code>def</code> <i>is</i> permitted.)
   *
   * @see #getValue(String,String)
   */

  public String[] getValues(String key, String[] def);



  /**
   * Associates the specified String value with the specified key in this
   * preference.
   *
   * @param key key with which the specified value is to be associated.
   * @param value value to be associated with the specified key.
   *
   * @exception  UnmodifiableException
   *                 if this preference cannot be modified for this request
   * @exception java.lang.IllegalArgumentException if key is <code>null</code>,
   *                 or <code>key.length()</code> 
   *                 or <code>value.length</code> are to long. The maximum length 
   *                 for key and value are implementation specific.
   *
   * @see #setValues(String, String[])
   */
  public void setValue(String key, String value)  throws UnmodifiableException;




  /**
   * Associates the specified String array value with the specified key in this
   * preference.
   *
   * @param key key with which the  value is to be associated
   * @param values values to be associated with key
   *
   * @exception  java.lang.IllegalArgumentException if key is <code>null</code>, or
   *                 <code>key.length()</code> 
   *                 is to long or <code>value.size</code> is to large.  The maximum 
   *                 length for key and maximum size for value are implementation specific.
   * @exception  UnmodifiableException
   *                 if this preference cannot be modified for this request
   *
   * @see #setValue(String,String)
   */

  public void setValues(String key, String[] values) throws UnmodifiableException;


  /**
   * Returns all of the keys that have an associated value,
   * or an empty <code>Enumeration</code> if no keys are
   * available.
   *
   * @return an Enumeration of the keys that have an associated value,
   *         or an empty <code>Enumeration</code> if no keys are
   *         available.
   */
  public java.util.Enumeration getNames();


  /**
   * Resets or removes the value associated with the specified key.
   * <p>
   * If this implementation supports stored defaults, and there is such
   * a default for the specified preference, the given key will be 
   * reset to the stored default.
   * <p>
   * If there is no default available the key will be removed.
   *
   * @param  key to reset
   *
   * @exception  java.lang.IllegalArgumentException if key is <code>null</code>.
   * @exception  UnmodifiableException
   *                 if this preference cannot be modified for this request
   */

  public void reset(String key) throws UnmodifiableException;


  /**
   * Commits all changes made to the preferences via the 
   * <code>set</code> methods in the persistent store.
   * <P>
   * If this call returns succesfull, all changes are made
   * persistent. If this call fails, no changes are made
   * in the persistent store. This call is an atomic operation 
   * regardless of how many preference attributes have been modified.
   * <P>
   * All changes made to preferences not followed by a call 
   * to the <code>store</code> method are discarded when the 
   * portlet finishes the <code>processAction</code> or 
   * <code>render</code> methods.
   * <P>
   * If a validator is defined for this preferences in the
   * deployment descriptor, this validator is called before
   * the actual store is performed to check wether the given
   * preferences are vaild. If this check fails a 
   * <code>ValidatorException</code> is thrown.
   *
   * @exception  java.io.IOException    
   *                 if changes cannot be written into
   *                 the backend store
   * @exception  ValidatorException
   *                 if the validation performed by the
   *                 associated validator fails
   * @exception  java.lang.UnsupportedOperationException
   *                 if this method is called inside a render call
   *
   * @see  PreferencesValidator
   */

  public void store() throws java.io.IOException, ValidatorException;


}
