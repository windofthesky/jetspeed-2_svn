package org.apache.jetspeed.tools.deploy;

import org.jdom.Document;

/**
 * @author Nicolas Dutertry
 * @version $Id$
 */
public class JetspeedWebApplicationRewriterFactory {
    
    /** 
     * Returns an instance of JetspeedWebApplicationRewriter.
     * 
     * @param doc
     * @return JetspeedWebApplicationRewriter
     * @throws Exception
     */
    public JetspeedWebApplicationRewriter getInstance(Document doc) throws Exception
    {
        return getInstance(doc, null, null);
    }
    
    /** 
     * Returns an instance of JetspeedWebApplicationRewriter.
     * 
     * @param doc
     * @return JetspeedWebApplicationRewriter
     * @throws Exception
     */
    public JetspeedWebApplicationRewriter getInstance(Document doc, String portletApplication) throws Exception
    {
        return getInstance(doc, portletApplication, null);
    }
    
    /** 
     * Returns an instance of JetspeedWebApplicationRewriter.
     * 
     * @param doc
     * @param portletApplication
     * @param forcedVersion
     * @return JetspeedWebApplicationRewriter
     * @throws Exception
     */
    public JetspeedWebApplicationRewriter getInstance(Document doc, String portletApplication, String forcedVersion) throws Exception
    {
        String version = forcedVersion;
        if(version == null)
        {
            version = doc.getRootElement().getAttributeValue("version", "2.3");
        }
        
        try
        {
            // Check version is a valid number
            Double.parseDouble(version);
        }
        catch(NumberFormatException e)
        {
            throw new Exception("Unable to create JetspeedWebApplicationRewriter for version " + version, e);
        }
        
        if(version.equals("2.3"))
        {
            return new JetspeedWebApplicationRewriter2_3(doc, portletApplication);
        }
        else if(version.compareTo("2.4") >= 0)
        {
            return new JetspeedWebApplicationRewriter2_4(doc, portletApplication);
        }
        else
        {
            throw new Exception("Unable to create JetspeedWebApplicationRewriter for version " + version);
        }
    }
}
