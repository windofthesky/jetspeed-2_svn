/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * RewriterServiceImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: JetspeedRewriterController.java,v 1.2 2004/03/08 00:44:40 jford
 *          Exp $
 */
public class JetspeedRewriterController implements RewriterController
{
    protected final static Log log = LogFactory.getLog(JetspeedRewriterController.class);
    final static String CONFIG_MAPPING_FILE = "mapping";
    final static String CONFIG_BASIC_REWRITER = "basic.class";
    final static String CONFIG_RULESET_REWRITER = "ruleset.class";
    final static String CONFIG_ADAPTOR_HTML = "adaptor.html";
    final static String CONFIG_ADAPTOR_XML = "adaptor.xml";

    // configuration parameters
    protected String mappingFile = null;

    /** the Castor mapping file name */
    protected Mapping mapper = null;

    /** Collection of rulesets in the system */
    protected Map rulesets = new HashMap();

    /** configured basic rewriter class */
    protected Class basicRewriterClass = BasicRewriter.class;

    /** configured ruleset rewriter class */
    protected Class rulesetRewriterClass = RulesetRewriterImpl.class;

    /** Adaptors */
    protected Class adaptorHtmlClass = SwingParserAdaptor.class;
    protected Class adaptorXmlClass = SaxParserAdaptor.class;

    public JetspeedRewriterController( String mappingFile ) throws RewriterException
    {
        this.mappingFile = mappingFile;
        loadMapping();
    }

    public JetspeedRewriterController( String mappingFile, List rewriterClasses, List adaptorClasses )
            throws RewriterException
    {
        this.mappingFile = mappingFile;
        if (rewriterClasses.size() > 0)
        {
            this.basicRewriterClass = (Class) rewriterClasses.get(0);
            if (rewriterClasses.size() > 1)
            {
                this.rulesetRewriterClass = (Class) rewriterClasses.get(1);
            }
        }
        if (adaptorClasses.size() > 0)
        {
            this.adaptorHtmlClass = (Class) adaptorClasses.get(0);
            if (adaptorClasses.size() > 1)
            {
                this.adaptorXmlClass = (Class) adaptorClasses.get(1);
            }
        }

        loadMapping();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.rewriter.RewriterService#createRewriter()
     */
    public Rewriter createRewriter() throws InstantiationException, IllegalAccessException
    {
        return (Rewriter) basicRewriterClass.newInstance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.rewriter.RewriterService#createRewriter(org.apache.jetspeed.rewriter.rules.Ruleset)
     */
    public RulesetRewriter createRewriter( Ruleset ruleset ) throws RewriterException
    {
        try
        {
            RulesetRewriter rewriter = (RulesetRewriter) rulesetRewriterClass.newInstance();
            rewriter.setRuleset(ruleset);
            return rewriter;
        }
        catch (Exception e)
        {
            log.error("Error creating rewriter class", e);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.rewriter.RewriterService#createParserAdaptor(java.lang.String)
     */
    public ParserAdaptor createParserAdaptor( String mimeType ) throws RewriterException
    {
        try
        {
            if (mimeType.equals("text/html"))
            {
                return (ParserAdaptor) adaptorHtmlClass.newInstance();
            }
            else if (mimeType.equals("text/xml"))
            {
                return (ParserAdaptor) adaptorXmlClass.newInstance();
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
    protected void loadMapping() throws RewriterException
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

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.rewriter.RewriterService#lookupRuleset(java.lang.String)
     */
    public Ruleset lookupRuleset( String id )
    {
        return (Ruleset) rulesets.get(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.rewriter.RewriterService#loadRuleset(java.io.Reader)
     */
    public Ruleset loadRuleset( Reader reader )
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