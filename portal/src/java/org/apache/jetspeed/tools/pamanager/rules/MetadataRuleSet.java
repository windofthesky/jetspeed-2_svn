/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.tools.pamanager.rules;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

/**
 * RuleSet for adding metadata
 *
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 */
public class MetadataRuleSet extends RuleSetBase
{
    private String prefix;

    /**
     * @param string
     */
    public MetadataRuleSet(String prefix)
    {
        this.prefix = prefix;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.digester.RuleSet#addRuleInstances(org.apache.commons.digester.Digester)
     */
    public void addRuleInstances(Digester digester)
    {
        LocalizedFieldRule fieldRule = new LocalizedFieldRule();
        
        digester.addRule(prefix + "title", fieldRule);
        digester.addRule(prefix + "contributor", fieldRule);
        digester.addRule(prefix + "creator", fieldRule);
        digester.addRule(prefix + "coverage", fieldRule);
        digester.addRule(prefix + "description", fieldRule);
        digester.addRule(prefix + "format", fieldRule);
        digester.addRule(prefix + "identifier", fieldRule);
        digester.addRule(prefix + "language", fieldRule);
        digester.addRule(prefix + "publisher", fieldRule);
        digester.addRule(prefix + "relation", fieldRule);
        digester.addRule(prefix + "right", fieldRule);
        digester.addRule(prefix + "source", fieldRule);
        digester.addRule(prefix + "subject", fieldRule);
        digester.addRule(prefix + "type", fieldRule);
        digester.addRule(prefix + "metadata", fieldRule);
        /*
        digester.addRule(prefix + "title", new LocalizedFieldRule());
        digester.addRule(prefix + "contributor", new LocalizedFieldRule());
        digester.addRule(prefix + "creator", new LocalizedFieldRule());
        digester.addRule(prefix + "coverage", new LocalizedFieldRule());
        digester.addRule(prefix + "description", new LocalizedFieldRule());
        digester.addRule(prefix + "format", new LocalizedFieldRule());
        digester.addRule(prefix + "identifier", new LocalizedFieldRule());
        digester.addRule(prefix + "language", new LocalizedFieldRule());
        digester.addRule(prefix + "publisher", new LocalizedFieldRule());
        digester.addRule(prefix + "relation", new LocalizedFieldRule());
        digester.addRule(prefix + "right", new LocalizedFieldRule());
        digester.addRule(prefix + "source", new LocalizedFieldRule());
        digester.addRule(prefix + "subject", new LocalizedFieldRule());
        digester.addRule(prefix + "type", new LocalizedFieldRule());
        digester.addRule(prefix + "metadata", new LocalizedFieldRule());
        */

    }

}
