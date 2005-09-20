/* Copyright 2004 Apache Software Foundation
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
 */
package org.apache.jetspeed.security.impl;

import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.HierarchyResolver;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>
 * Implementation for "part of" hierarchy. For Example: There're roles:
 * <ul>
 * <li>roleA</li>
 * <li>roleA.roleB</li>
 * <li>roleA.roleB.roleC</li>
 * </ul>
 * if a user has the role [roleA] than
 * </p>
 * <code>user.getSubject().getPrincipals()</code> returns:
 * <ul>
 * <li>/role/roleA</li>
 * <li>/role/roleA/roleB</li>
 * <li>/role/roleA/roleB/roleC</li>
 * </ul>
 * </p>
 * 
 * @author <a href="mailto:Artem.Grinshtein@t-systems.com">Artem Grinshtein </a>
 * @version $Id: AggregationHierarchyResolver.java,v 1.2 2004/09/18 19:33:58
 *          dlestrat Exp $
 */
public class AggregationHierarchyResolver extends BaseHierarchyResolver implements HierarchyResolver
{
    private static final Log log = LogFactory.getLog(AggregationHierarchyResolver.class);

    /**
     * @see org.apache.jetspeed.security.HierarchyResolver#resolve(Preferences)
     */
    public String[] resolve(Preferences prefs)
    {
        ArgUtil.notNull(new Object[] { prefs }, new String[] { "preferences" }, "resolve(java.util.prefs.Preferences)");

        return resolveChildren(prefs);
    }

}