/*
 * Created on Jun 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.util.descriptor;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PortletPreferenceRuleSet extends RuleSetBase
{

  

    /**
     * <p>
     * addRuleInstances
     * </p>
     *
     * @see org.apache.commons.digester.RuleSet#addRuleInstances(org.apache.commons.digester.Digester)
     * @param arg0
     */
    public void addRuleInstances( Digester digester )
    {
       digester.addRule("portlet-app/portlet/portlet-preferences/preference", new PortletPreferenceRule());
       digester.addBeanPropertySetter("portlet-app/portlet/portlet-preferences/preference/name", "name");
       digester.addCallMethod("portlet-app/portlet/portlet-preferences/preference/value", "addValue", 0);
       digester.addCallMethod(
           "portlet-app/portlet/portlet-preferences/preference/read-only",
           "setReadOnly",
           0,
           new Class[] { Boolean.class });

    }

}
