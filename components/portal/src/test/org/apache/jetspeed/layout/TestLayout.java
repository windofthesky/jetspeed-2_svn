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
package org.apache.jetspeed.layout;

import org.apache.jetspeed.AbstractPortalContainerTestCase;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.layout.impl.LayoutValve;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.request.RequestContext;

/**
 * Test for Fragment placement
 * 
 * @author <a>David Gurney </a>
 * @version $Id: $
 */
public class TestLayout extends AbstractPortalContainerTestCase
{

    private ComponentManager m_oComponentManager;

    private LayoutValve m_oLv;

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(TestLayout.class);
    }

    /**
     * Setup the request context
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        // Load the Spring configs
        String[] bootConfigs = null;
        String[] appConfigs =
        { "src/webapp/WEB-INF/assembly/layout-api.xml",
                "src/test/resources/assembly/test-layout-api.xml"};
        m_oComponentManager = new SpringComponentManager(bootConfigs,
                appConfigs, null, ".");
        m_oComponentManager.start();

        // Get a valid LayoutValve from Spring
        m_oLv = (LayoutValve) m_oComponentManager.getComponent("layoutValve");
    }

    protected void tearDown() throws Exception
    {
        m_oComponentManager.stop();
    }

    public void xtestNullRequestContext()
    {
        // Get the layout that has a null request context
        LayoutValve lv = new LayoutValve(null);
        try
        {
            lv.invoke(null, null);
            TestLayout.fail("should have thrown an exception");
        } catch (PipelineException e)
        {
            TestLayout.assertTrue("detected null request context", true);
        }
    }

    public void xtestNullParameters()
    {
        try
        {
            // Test the success case
            RequestContext rc = FragmentUtil
                    .setupRequestContext(null, "1234", "0", "0");
            m_oLv.invoke(rc, null);
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "failure"));
        } catch (PipelineException e)
        {
            TestLayout.fail("unexpected exception");
        }

        try
        {
            // Test the success case
            RequestContext rc = FragmentUtil.setupRequestContext("moveabs", null, "0",
                    "0");
            m_oLv.invoke(rc, null);

            // Take a look at the response to verify a failiure
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "failure"));
        } catch (PipelineException e)
        {
            TestLayout.fail("unexpected exception");
        }

        try
        {
            // Test the success case
            RequestContext rc = FragmentUtil.setupRequestContext("moveabs", "1234",
                    null, "0");
            m_oLv.invoke(rc, null);

            // Take a look at the response to verify a failiure
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "failure"));
        } catch (PipelineException e)
        {
            TestLayout.fail("unexpected exception");
        }

        try
        {
            // Test the success case
            RequestContext rc = FragmentUtil.setupRequestContext("moveabs", "1234",
                    "0", null);
            m_oLv.invoke(rc, null);

            // Take a look at the response to verify a failiure
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "failure"));
        } catch (PipelineException e)
        {
            TestLayout.fail("unexpected exception");
        }
    }

    public void xtestMoveSuccess()
    {
        moveSuccess("moveabs", "1", "0", "0", "0", "0", "0", "0"); // Doesn't
                                                                    // really
                                                                    // move
        moveSuccess("moveabs", "1", "0", "0", "0", "1", "0", "1"); // Move down
        moveSuccess("moveabs", "2", "0", "1", "0", "0", "0", "0"); // Move up
        moveSuccess("moveabs", "1", "0", "0", "1", "0", "1", "0"); // Move
                                                                    // right
        moveSuccess("moveabs", "3", "1", "0", "0", "0", "0", "0"); // Move left
        moveSuccess("moveabs", "2", "0", "1", "1", "2", "1", "2"); // Move
                                                                    // right &
                                                                    // move down
        moveSuccess("moveabs", "3", "1", "0", "0", "1", "0", "1"); // Move left
                                                                    // & move
                                                                    // down
        moveSuccess("moveabs", "4", "1", "1", "0", "0", "0", "0"); // Move left
                                                                    // & move up

        moveSuccess("moveabs", "1", "0", "0", "0", "2", "0", "1"); // Move too
                                                                    // far down,
                                                                    // should be
                                                                    // at end of
                                                                    // row
        moveSuccess("moveabs", "2", "0", "1", "0", "2", "0", "1"); // Move too
                                                                    // far down,
                                                                    // should be
                                                                    // at end of
                                                                    // row
        moveSuccess("moveabs", "3", "1", "0", "1", "3", "1", "2"); // Move too
                                                                    // far down,
                                                                    // should be
                                                                    // at end of
                                                                    // row
        moveSuccess("moveabs", "4", "1", "1", "1", "3", "1", "2"); // Move too
                                                                    // far down,
                                                                    // should be
                                                                    // at end of
                                                                    // row
        moveSuccess("moveabs", "5", "1", "2", "1", "3", "1", "2"); // Move too
                                                                    // far down,
                                                                    // should be
                                                                    // at end of
                                                                    // row
        moveSuccess("moveabs", "1", "0", "0", "1", "4", "1", "3"); // Move too
                                                                    // far down,
                                                                    // should be
                                                                    // at end of
                                                                    // row
        moveSuccess("moveabs", "2", "0", "1", "1", "4", "1", "3"); // Move too
                                                                    // far down,
                                                                    // should be
                                                                    // at end of
                                                                    // row

        moveSuccess("moveleft", "1", "0", "0", "0", "0", "0", "0"); // Shouldn't
                                                                    // move
        moveSuccess("moveleft", "2", "0", "1", "0", "1", "0", "1"); // Shouldn't
                                                                    // move
        moveSuccess("moveleft", "3", "1", "0", "0", "0", "0", "0"); // Straight
                                                                    // across
        moveSuccess("moveleft", "4", "1", "1", "0", "1", "0", "1"); // Straight
                                                                    // across
        moveSuccess("moveleft", "5", "1", "2", "0", "2", "0", "2"); // Straight
                                                                    // across

        moveSuccess("moveright", "1", "0", "0", "1", "0", "1", "0"); // Straight
                                                                        // across
        moveSuccess("moveright", "2", "0", "1", "1", "1", "1", "1"); // Straight
                                                                        // across
        moveSuccess("moveright", "3", "1", "0", "1", "0", "1", "0"); // Shouldn't
                                                                        // move
        moveSuccess("moveright", "4", "1", "1", "1", "1", "1", "1"); // Shouldn't
                                                                        // move
        moveSuccess("moveright", "5", "1", "2", "1", "2", "1", "2"); // Shouldn't
                                                                        // move

        moveSuccess("moveup", "2", "0", "1", "0", "0", "0", "0"); // Straight
                                                                    // across
        moveSuccess("moveup", "4", "1", "1", "1", "0", "1", "0"); // Straight
                                                                    // across
        moveSuccess("moveup", "5", "1", "2", "1", "1", "1", "1"); // Straight
                                                                    // across

        moveSuccess("movedown", "1", "0", "0", "0", "1", "0", "1"); // Straight
                                                                    // across
        moveSuccess("movedown", "2", "0", "1", "0", "1", "0", "1"); // Shouldn't
                                                                    // move
        moveSuccess("movedown", "3", "1", "0", "1", "1", "1", "1"); // Straight
                                                                    // across
        moveSuccess("movedown", "4", "1", "1", "1", "2", "1", "2"); // Straight
                                                                    // across
        moveSuccess("movedown", "5", "1", "2", "1", "2", "1", "2"); // Shouldn't
                                                                    // move
    }

    public void xtestMoveFailure()
    {
        moveFailure("moveabs", "bogus", "0", "0", "0", "0"); // non integer
                                                                // portlet id
        moveFailure("moveleft", "0", "0", "0", "0", "0"); // portlet not found
        moveFailure("moveabs", "1", "0", "0", "3", "0"); // non existent
                                                            // column
        moveFailure("bogus", "0", "0", "0", "0", "0"); // bogus action
        moveFailure("moveabs", "1", "0", "0", "a", "0"); // non integer value
        moveFailure("moveabs", "1", "0", "0", "0", "b"); // non integer value
    }

    public void moveSuccess(String a_sMoveType, String p_sPortletId,
            String p_sOldCol, String p_sOldRow, String p_sNewCol,
            String p_sNewRow, String p_sExpectedNewCol, String p_sExpectedNewRow)
    {
        try
        {
            // Test the success case
            RequestContext rc = null;

            if (a_sMoveType.equalsIgnoreCase("moveabs"))
            {
                rc = FragmentUtil.setupRequestContext(a_sMoveType, p_sPortletId,
                        p_sNewCol, p_sNewRow);
            } else
            {
                rc = FragmentUtil.setupRequestContext(a_sMoveType, p_sPortletId, null,
                        null);
            }

            m_oLv.invoke(rc, null);

            // Take a look at the response to verify a failiure
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "success"));
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "<js>"));
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "<status>success</status>"));
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "<action>" + a_sMoveType + "</action>"));
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "<id>" + p_sPortletId + "</id>"));
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "<old_position><col>" + p_sOldCol + "</col><row>"
                            + p_sOldRow + "</row></old_position>"));
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "<new_position><col>" + p_sExpectedNewCol + "</col><row>"
                            + p_sExpectedNewRow + "</row></new_position>"));
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "</js>"));
        } catch (PipelineException e)
        {
            e.printStackTrace();
            TestLayout.fail("layout valve failed");
        }
    }

    public void moveFailure(String a_sMoveType, String p_sPortletId,
            String p_sOldCol, String p_sOldRow, String p_sNewCol,
            String p_sNewRow)
    {
        try
        {
            // Test failure case
            RequestContext rc = null;

            if (a_sMoveType.equalsIgnoreCase("moveabs"))
            {
                rc = FragmentUtil.setupRequestContext(a_sMoveType, p_sPortletId,
                        p_sNewCol, p_sNewRow);
            } else
            {
                rc = FragmentUtil.setupRequestContext(a_sMoveType, p_sPortletId, null,
                        null);
            }
            m_oLv.invoke(rc, null);

            // Take a look at the response to verify a failiure
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "<status>failure</status>"));
        } catch (PipelineException p)
        {
            TestLayout.fail("unexpected exception");
        }

        try
        {
            // Test failure case
            RequestContext rc = null;

            if (a_sMoveType.equalsIgnoreCase("moveabs"))
            {
                rc = FragmentUtil.setupRequestContext(a_sMoveType, "1234", "0", "foo");
            } else
            {
                rc = FragmentUtil.setupRequestContext(a_sMoveType, null, null, null);
            }

            m_oLv.invoke(rc, null);

            // Take a look at the response to verify a failiure
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "<status>failure</status>"));
        } catch (PipelineException p)
        {
            TestLayout.fail("unexpected exception");
        }
    }

    public void xtestRemove()
    {
        remove("1");
        remove("2");
        remove("3");
        remove("4");
    }

    private void remove(String p_sPortletId)
    {
        try
        {
            // Test the success case
            RequestContext rc = null;

            rc = FragmentUtil.setupRequestContext("remove", p_sPortletId, null, null);

            m_oLv.invoke(rc, null);

            // Take a look at the response to verify a failiure
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "success"));
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "<js>"));
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "<status>success</status>"));
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "<action>" + "remove" + "</action>"));
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "<id>" + p_sPortletId + "</id>"));
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "<old_position>"));
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "<col>"));
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "<row>"));
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "</old_position>"));
            TestLayout.assertTrue("couldn't find value", FragmentUtil.findValue(rc,
                    "</js>"));
        } catch (PipelineException e)
        {
            e.printStackTrace();
            TestLayout.fail("layout valve failed");
        }

    }
}
