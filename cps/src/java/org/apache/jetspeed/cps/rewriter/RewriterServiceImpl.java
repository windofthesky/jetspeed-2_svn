/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.cps.rewriter;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.cps.rewriter.rules.Ruleset;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;
import org.w3c.dom.Document;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * RewriterServiceImpl
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class RewriterServiceImpl
    extends BaseCommonService
    implements RewriterService
{
    protected final static Log log = LogFactory.getLog(RewriterServiceImpl.class);
    final static String CONFIG_MAPPING_FILE = "mapping";
    final static String CONFIG_BASIC_REWRITER = "basic.class";
    final static String CONFIG_RULESET_REWRITER = "ruleset.class";
    final static String CONFIG_ADAPTOR_HTML = "adaptor.html";
    final static String CONFIG_ADAPTOR_XML = "adaptor.xml";
        
    // configuration parameters
    private String mappingFile = null; 
    
    /** the Castor mapping file name */
    private Mapping mapper = null;
    
    /** Collection of rulesets in the system */
    private Map rulesets = new HashMap();
            
    /** configured basic rewriter class */
    private Class basicRewriterClass = null;
    private String basicRewriter;

    /** configured ruleset rewriter class */
    private Class rulesetRewriterClass = null;
    private String rulesetRewriter;

    /** Adaptors */
    private Class adaptorHtmlClass = null;
    private Class adaptorXmlClass = null;        
    private String adaptorHtml;    
    private String adaptorXml;
                    
    /**
     * This is the early initialization method called by the service framework
     * 
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public void init() throws CPSInitializationException
    {
        log.info("Initializing Rewriter service");
        if (isInitialized())
        {
            return;
        }
        
        basicRewriter = getConfiguration().getString(CONFIG_BASIC_REWRITER, null);
        rulesetRewriter = getConfiguration().getString(CONFIG_RULESET_REWRITER, null);
        
        if (basicRewriter == null || rulesetRewriter == null)
        {
            throw new CPSInitializationException("Rewriter class factories not configured");                                 
        }

        adaptorHtml = getConfiguration().getString(CONFIG_ADAPTOR_HTML, null);
        adaptorXml = getConfiguration().getString(CONFIG_ADAPTOR_XML, null);        

        if (adaptorXml == null || adaptorHtml == null)
        {
            throw new CPSInitializationException("Parser Adaptor class factories not configured");                                 
        }
        
        mappingFile = getConfiguration().getString(CONFIG_MAPPING_FILE, null);
        if (null == mappingFile)
        {
            throw new CPSInitializationException("Rewriter rules not configured");                     
        }        
        mappingFile = CommonPortletServices.getInstance().getRealPath(mappingFile);
               
        loadMapping();
        
        // initialization done
        setInit(true);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.RewriterService#createRewriter()
     */
    public Rewriter createRewriter()
        throws RewriterException    
    {
        try
        {
            if (null == basicRewriterClass)
            {                    
                basicRewriterClass = Class.forName(basicRewriter);
            }
            
            return (Rewriter)basicRewriterClass.newInstance();    
        }
        catch (Exception e)
        {
            log.error("Error creating rewriter class", e);
        }
        return null;
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.RewriterService#createRewriter(org.apache.jetspeed.cps.rewriter.rules.Ruleset)
     */
    public RulesetRewriter createRewriter(Ruleset ruleset)
        throws RewriterException    
    {
        try
        {
            if (null == rulesetRewriterClass)
            {                    
                rulesetRewriterClass = Class.forName(rulesetRewriter);
            }
            
            RulesetRewriter rewriter = (RulesetRewriter)rulesetRewriterClass.newInstance();
            rewriter.setRuleset(ruleset);
            return rewriter;    
        }
        catch (Exception e)
        {
            log.error("Error creating rewriter class", e);            
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.RewriterService#createParserAdaptor(java.lang.String)
     */
    public ParserAdaptor createParserAdaptor(String mimeType)
        throws RewriterException
    {
        try
        {
            if (mimeType.equals("text/html"))
            {
                if (null == adaptorHtmlClass)
                {                    
                    adaptorHtmlClass = Class.forName(adaptorHtml);
                }
                
                return (ParserAdaptor)adaptorHtmlClass.newInstance();
            }
            else if (mimeType.equals("text/xml"))
            {
                if (null == adaptorXmlClass)
                {                    
                    adaptorXmlClass = Class.forName(adaptorXml);
                }
                
                return (ParserAdaptor)adaptorXmlClass.newInstance();
            }
            else
            {
            }
        }
        catch (Exception e)
        {
            log.error("Error creating rewriter class", e);
        }
        return null;        
    }
              
    /**
     * Load the mapping file for ruleset configuration
     *
     */
    private void loadMapping()
        throws CPSInitializationException
    {
        File map = new File(this.mappingFile);
        if (map.exists() && map.isFile() && map.canRead())
        {
            try
            {
                this.mapper = new Mapping();
                InputSource is = new InputSource(new FileReader(map));
                is.setSystemId(this.mappingFile);
                this.mapper.loadMapping(is);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                String msg = "RewriterService: Error in castor mapping creation";
                log.error(msg, e);
                throw new CPSInitializationException(msg, e);
            }
        }
        else
        {
                        
            String msg = "RewriterService: Mapping not found or not a file or unreadable: " + this.mappingFile;
            System.out.println(msg);
            log.error(msg);
            throw new CPSInitializationException(msg);
        }

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.RewriterService#lookupRuleset(java.lang.String)
     */
    public Ruleset lookupRuleset(String id)
    {
        return (Ruleset)rulesets.get(id);
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.RewriterService#loadRuleset(java.io.Reader)
     */
    public Ruleset loadRuleset(Reader reader)
    {
        Ruleset ruleset = null;
        try
        {
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbfactory.newDocumentBuilder();

            InputSource source = new InputSource(reader);

            Document doc = builder.parse(source);

            Unmarshaller unmarshaller = new Unmarshaller(this.mapper);
             
            ruleset = (Ruleset) unmarshaller.unmarshal((Node) doc);
            ruleset.sync();
            rulesets.put(ruleset.getId(), ruleset);
            

        }
        catch (Throwable t)
        {
            log.error("ForwardService: Could not unmarshal: " + reader, t);
        }

        return ruleset;            
    }
    
}
