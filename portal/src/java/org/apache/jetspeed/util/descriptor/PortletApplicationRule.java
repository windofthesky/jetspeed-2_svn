/*
 * Created on Jun 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.util.descriptor;

import org.apache.commons.digester.Rule;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.xml.sax.Attributes;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PortletApplicationRule extends Rule
{
    protected String appName;
    
    public PortletApplicationRule(String appName)
    {
        this.appName = appName;
    }


    /**
     * <p>
     * begin
     * </p>
     *
     * @see org.apache.commons.digester.Rule#begin(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws java.lang.Exception
     */
    public void begin( String arg0, String arg1, Attributes arg2 ) throws Exception
    {
        PortletApplicationDefinitionImpl app = new PortletApplicationDefinitionImpl();
        app.setName(appName);
        digester.push(app);        
    }
    
    
    /**
     * <p>
     * end
     * </p>
     *
     * @see org.apache.commons.digester.Rule#end(java.lang.String, java.lang.String)
     * @param arg0
     * @param arg1
     * @throws java.lang.Exception
     */
    public void end( String arg0, String arg1 ) throws Exception
    {
       // digester.pop();
    }
}
