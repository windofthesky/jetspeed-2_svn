/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.tools.pamanager;

import junit.framework.TestCase;

public class NumericVersionTest extends TestCase {

    public void testDotCompare() throws Exception {
        assert(VersionedPortletApplicationManager.compareVersions("2.9.8", "2.10.0", true) == -1);
        assert(VersionedPortletApplicationManager.compareVersions("2.9.5.4.3.2.1", "2.9.5.4.3.2.1", true) == 0);
        assert(VersionedPortletApplicationManager.compareVersions("2.9.5.4.3.2.2", "2.9.5.4.3.2.1", true) == 1);
        assert(VersionedPortletApplicationManager.compareVersions("2.9.5.4.3.2.1", "2.9.5.4.3.2.2", true) == -1);
        assert(VersionedPortletApplicationManager.compareVersions("2.9", "2.9.5", true) == -1);
        assert(VersionedPortletApplicationManager.compareVersions("2.9.5", "2.9", true) == 1);
        assert(VersionedPortletApplicationManager.compareVersions("2", "3", true) == -1);
        assert(VersionedPortletApplicationManager.compareVersions("3", "2", true) == 1);
        assert(VersionedPortletApplicationManager.compareVersions("3.4.3", "3.4.2", true) == 1);
        assert(VersionedPortletApplicationManager.compareVersions("3.4.2", "3.4.3", true) == -1);

        assert (VersionedPortletApplicationManager.compareVersions("a.b.c", "a.b.d", false) == -1);
        assert (VersionedPortletApplicationManager.compareVersions("aaaa", "bbb", false) == -1);
        assert (VersionedPortletApplicationManager.compareVersions("big", "Big", false) > 0);
        boolean error = false;
        try {
            assert (VersionedPortletApplicationManager.compareVersions("a.b.c", "a.b.d", true) == -1);
        }
        catch (NumberFormatException e)  {
            error = true;
        }
        assert(error);
    }

}
