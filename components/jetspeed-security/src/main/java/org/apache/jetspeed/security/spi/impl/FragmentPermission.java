/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.security.spi.impl;

import java.security.Permission;

/**
 * <p>Fragment permission.</p>
 * <p>This code was partially inspired from articles from:</p>
 * <ul>
 * <li><a href="http://www-106.ibm.com/developerworks/library/j-jaas/">
 * Extend JAAS for class instance-level authorization.</a></li>
 * <li>The FilePermission implementation from the JDK in order to support recursive permissions & wild card</li>
 * </ul>
 * <p/>
 * This class represents access to a fragment within a
 * content document.  A FragmentPermission consists
 * of a path, fragment name, or a simple fragment name
 * pattern and a set of actions valid for that pathname.
 * <p/>
 * Here are some examples of valid fragment permissions names:
 * <li>"/folder/page.psml/app::portlet" matches fragments
 * within a page for a specified portlet contained in a app<li>
 * <li>"security::*" matches fragments for portlets from the security app<li>
 * <li>"&lt;&lt;ALL FRAGMENTS&gt;&gt;" matches <b>any</b> fragment<li>
 * <p/>
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 */
public class FragmentPermission extends PortalResourcePermission
{
    /**
     * <p>Constructor for FragmentPermission.</p>
     *
     * @param name    The fragment name.
     * @param actions The actions on the fragment.
     */
    public FragmentPermission(String name, String actions)
    {
        super(name, actions);
    }

    /**
     * <p>Constructor for FragmentPermission.</p>
     *
     * @param name The fragment name.
     * @param mask The mask of actions on the fragment.
     */
    public FragmentPermission(String name, int mask)
    {
        super(name, mask);
    }

    public boolean implies(Permission permission)
    {
        // The permission must be an instance
        // of the FragmentPermission.
        if (!(permission instanceof FragmentPermission))
        {
            return false;
        }
        FragmentPermission fragmentPerm = (FragmentPermission) permission;

        // Test fragment permission name matches
        String ruleName = getName();
        if (!ruleName.equals("<<ALL FRAGMENTS>>"))
        {
            String testName = fragmentPerm.getName();

            // match wildcarded portlet names
            int testNamesSeparator = testName.lastIndexOf("::");
            if (ruleName.endsWith("::" + FolderPermission.WILD_CHAR_STR) && (testNamesSeparator > 0))
            {
                ruleName = ruleName.substring(0, ruleName.length() - 3);
                testName = testName.substring(0, testNamesSeparator);
            }

            // trim path components from test name if rule
            // is not prefixed with the path
            if (!ruleName.startsWith(FolderPermission.FOLDER_SEPARATOR_STR) &&
                    testName.startsWith(FolderPermission.FOLDER_SEPARATOR_STR))
            {
                int testPathIndex = testName.lastIndexOf(FolderPermission.FOLDER_SEPARATOR);
                testName = testName.substring(testPathIndex + 1);
            }

            // remaining name parts must match
            if (!ruleName.equals(testName))
            {
                return false;
            }
        }

        // The action bits in FragmentPerm (permission)
        // must be set in the current mask permission.
        return (mask & fragmentPerm.mask) == fragmentPerm.mask;

    }

    /**
     * @see java.security.Permission#equals(Object)
     */
    public boolean equals(Object object)
    {
        if (!(object instanceof FragmentPermission))
            return false;

        FragmentPermission p = (FragmentPermission) object;
        return ((p.mask == mask) && (p.getName().equals(getName())));
    }

}