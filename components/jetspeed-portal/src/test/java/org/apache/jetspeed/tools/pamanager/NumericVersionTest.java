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
