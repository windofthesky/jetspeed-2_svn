/* ========================================================================
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================================
 */

import org.apache.jetspeed.rewriter.JetspeedRewriterController
import org.apache.jetspeed.rewriter.BasicRewriter
import org.apache.jetspeed.rewriter.RulesetRewriterImpl
import org.apache.jetspeed.rewriter.html.SwingParserAdaptor
import org.apache.jetspeed.rewriter.xml.SaxParserAdaptor


applicationRoot = config.getString("app.root", "./")
rulesFile = config.getString("rewriter_rules", "/WEB-INF/conf/rewriter-rules-mapping.xml")

mapping = applicationRoot + rulesFile
// rewriters [basic, ruleset] (not required)
rewriterClasses = [ org.apache.jetspeed.rewriter.BasicRewriter,
                    org.apache.jetspeed.rewriter.RulesetRewriterImpl ]
// adaptors [html, xml] (not required)
adaptorClasses = [ org.apache.jetspeed.rewriter.html.SwingParserAdaptor,
                   org.apache.jetspeed.rewriter.xml.SaxParserAdaptor ]
return new JetspeedRewriterController(mapping, rewriterClasses, adaptorClasses)

