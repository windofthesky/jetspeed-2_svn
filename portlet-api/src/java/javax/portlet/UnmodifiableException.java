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
