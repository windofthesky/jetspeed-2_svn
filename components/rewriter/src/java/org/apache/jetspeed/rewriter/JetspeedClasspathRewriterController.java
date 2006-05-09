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


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * RewriterServiceImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: JetspeedRewriterController.java,v 1.2 2004/03/08 00:44:40 jford
 *          Exp $
 */
public class JetspeedClasspathRewriterController
       extends JetspeedRewriterController
       implements RewriterController
{
    protected final static Log log = LogFactory.getLog(JetspeedClasspathRewriterController.class);

    public JetspeedClasspathRewriterController( String mappingFile ) throws RewriterException
    {
        super(mappingFile);
    }

    public JetspeedClasspathRewriterController( String mappingFile, List rewriterClasses, List adaptorClasses )
            throws RewriterException
    {
        super(mappingFile, rewriterClasses, adaptorClasses);
    }
    
    public JetspeedClasspathRewriterController( String mappingFile, 
            String basicRewriterClassName, String rulesetRewriterClassName, 
            String adaptorHtmlClassName, String adaptorXmlClassName )
    throws RewriterException
    {
        super(mappingFile, toClassList(basicRewriterClassName,rulesetRewriterClassName), 
              toClassList(adaptorHtmlClassName,adaptorXmlClassName));
    }
    
    protected Reader getReader(String resource)
    throws RewriterException
    {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resource);
        if (stream != null)
            return new InputStreamReader(stream);

        throw new RewriterException("could not access rewriter classpath resource " + resource);        
    }
}
