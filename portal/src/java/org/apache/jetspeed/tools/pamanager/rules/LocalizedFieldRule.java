/*
 * Created on Mar 9, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.jetspeed.tools.pamanager.rules;

import java.util.Locale;

import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.om.common.impl.LocalizedFieldImpl;
import org.xml.sax.Attributes;

/**
 * This class helps load internationalized fields
 *
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 */
public class LocalizedFieldRule extends Rule
{
    protected final static Log log = LogFactory.getLog(LocalizedFieldRule.class);
    /**
     * Handle the beginning of an XML element.
     *
     * @param attributes The attributes of this element
     *
     * @exception Exception if a processing error occurs
     */
    public void begin(String namespace, String name, Attributes attributes)
    throws Exception {

        if (digester.getLogger().isDebugEnabled())
            digester.getLogger().debug("Setting localized field " + name);
        
        LocalizedField child = new LocalizedFieldImpl();

        if(name.equals("metadata"))
        {
            String nameAttr = attributes.getValue("name");
            child.setName(nameAttr);
        }
        else
        {
            child.setName(name);
        }
        String language = attributes.getValue("xml:lang");
        Locale locale = null;
        if(language == null)
        {
            locale = new Locale("en");
        }
        else
        {
            locale = new Locale(language);
        }

        child.setLocale(locale);
        digester.push(child);
    }

    public void body(String namespace, String name, String text)
    throws Exception
    {
        LocalizedField child = (LocalizedField) digester.peek(0);
        child.setValue(text);
    }

    public void end(String namespace, String name)
    throws Exception
    {
        LocalizedField child = (LocalizedField) digester.pop();
        GenericMetadata metadata = (GenericMetadata)digester.peek();
        metadata.addField(child);
    }
}
