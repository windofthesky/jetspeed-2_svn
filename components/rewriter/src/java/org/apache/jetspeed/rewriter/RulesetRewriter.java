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

import org.apache.jetspeed.rewriter.rules.Ruleset;

/**
 * RulesetRewriter
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface RulesetRewriter extends Rewriter
{
    /**
     * Set the Ruleset configuration for this rewriter.
     * 
     * @param ruleset The Ruleset configuration.
     */
    void setRuleset(Ruleset ruleset);

    /**
     * Get the Ruleset configuration for this rewriter.
     * 
     * @return The Ruleset configuration.
     */    
    Ruleset getRuleset();
}
