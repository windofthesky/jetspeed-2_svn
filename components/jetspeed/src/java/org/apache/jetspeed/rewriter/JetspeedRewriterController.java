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
package org.apache.jetspeed.rewriter;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.rewriter.html.SwingParserAdaptor;
import org.apache.jetspeed.rewriter.rules.Ruleset;
import org.apache.jetspeed.rewriter.xml.SaxParserAdaptor;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;
import org.picocontainer.Startable;
import org.w3c.dom.Document;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * RewriterServiceImpl
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedRewriterController implements RewriterController, Startable
{
    protected final static Log log = LogFactory.getLog(JetspeedRewriterController.class);
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
    private Class basicRewriterClass = BasicRewriter.class;

    /** configured ruleset rewriter class */
    private Class rulesetRewriterClass = RulesetRewriterImpl.class;

    /** Adaptors */
    private Class adaptorHtmlClass = SwingParserAdaptor.class;
    private Class adaptorXmlClass = SaxParserAdaptor.class;        
                    
                    
    private JetspeedRewriterController()
    {                    
    }

    public JetspeedRewriterController(String mappingFile)
    {                    
        this.mappingFile = mappingFile;
    }

    public JetspeedRewriterController(String mappingFile, List rewriterClasses, List adaptorClasses)
    {                    
        this.mappingFile = mappingFile;
        if (rewriterClasses.size() > 0)
        {
            this.basicRewriterClass = (Class)rewriterClasses.get(0);
            if (rewriterClasses.size() > 1)
            {
                this.rulesetRewriterClass  = (Class)rewriterClasses.get(1);
            }
        }        
        if (adaptorClasses.size() > 0)
        {
            this.adaptorHtmlClass = (Class)adaptorClasses.get(0);
            if (adaptorClasses.size() > 1)
            {
                this.adaptorXmlClass  = (Class)adaptorClasses.get(1);
            }
        }        
        
    }
    
    public void start()
    {
        log.info("Starting Rewriter service");
                
        try
        {                                            
            loadMapping();
        }
        catch (Exception e)
        {
            log.error("Failed to load rewriter rules", e);
        }
        
    }
    
    public void stop()
    {
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.rewriter.RewriterService#createRewriter()
     */
    public Rewriter createRewriter()
        throws RewriterException    
    {
        try
        {            
            return (Rewriter)basicRewriterClass.newInstance();    
        }
        catch (Exception e)
        {
            log.error("Error creating rewriter class", e);
        }
        return null;
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.rewriter.RewriterService#createRewriter(org.apache.jetspeed.rewriter.rules.Ruleset)
     */
    public RulesetRewriter createRewriter(Ruleset ruleset)
        throws RewriterException    
    {
        try
        {            
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
     * @see org.apache.jetspeed.rewriter.RewriterService#createParserAdaptor(java.lang.String)
     */
    public ParserAdaptor createParserAdaptor(String mimeType)
        throws RewriterException
    {
        try
        {
            if (mimeType.equals("text/html"))
            {                
                return (ParserAdaptor)adaptorHtmlClass.newInstance();
            }
            else if (mimeType.equals("text/xml"))
            {
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
        throws RewriterException
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
                throw new RewriterException(msg, e);
            }
        }
        else
        {
                        
            String msg = "RewriterService: Mapping not found or not a file or unreadable: " + this.mappingFile;
            System.out.println(msg);
            log.error(msg);
            throw new RewriterException(msg);
        }

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.rewriter.RewriterService#lookupRuleset(java.lang.String)
     */
    public Ruleset lookupRuleset(String id)
    {
        return (Ruleset)rulesets.get(id);
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.rewriter.RewriterService#loadRuleset(java.io.Reader)
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
