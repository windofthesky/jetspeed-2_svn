/*
 * Created on Jun 18, 2004
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
public class PortletApplicationRuleSet extends RuleSetBase
{
    protected String appName;
    
    public PortletApplicationRuleSet(String appName)
    {
        this.appName = appName;
    }

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
        digester.addRule("portlet-app", new PortletApplicationRule(appName));
        digester.addSetProperties("portlet-app", "id", "applicationIdentifier");
    }

}
