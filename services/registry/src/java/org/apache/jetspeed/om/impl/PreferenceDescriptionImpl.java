/**
 * Created on Jan 22, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.om.impl;

/**
 * <p>
 * PreferenceDescriptionImpl
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PreferenceDescriptionImpl extends DescriptionImpl
{
	/**
	* Tells OJB which class to use to materialize.  
	*/
	protected String ojbConcreteClass = PreferenceDescriptionImpl.class.getName();
}
