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

import junit.framework.TestCase;

import org.apache.jetspeed.layout.impl.CoordinateImpl;
import org.apache.jetspeed.layout.impl.PortletPlacementManagerImpl;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;

/**
 * Test for Fragment placement
 * 
 * @author <a>David Gurney </a>
 * @version $Id: $
 */
public class TestPPM extends TestCase
{

    public void testGetFragmentAt()
    {
        // Build a request object and populate it with fragments
        RequestContext a_oRC = FragmentUtil.buildFullRequestContext();

        try
        {
            PortletPlacementManager ppm = new PortletPlacementManagerImpl(a_oRC);
            int a_iNumCols = ppm.getNumCols();
            assertEquals(a_iNumCols, 2);

            int a_iNumRows = ppm.getNumRows(0);
            assertEquals(a_iNumRows, 2);

            a_iNumRows = ppm.getNumRows(1);
            assertEquals(a_iNumRows, 3);

            // Check the fragments
            Fragment a_oFrag = ppm
                    .getFragmentAtNewCoordinate(new CoordinateImpl(0, 0, 0, 0));
            assertNotNull("null fragment found at 0,0", a_oFrag);
            assertEquals(a_oFrag.getId(), "1");
            assertEquals(a_oFrag.getName(), "frag1");

            a_oFrag = ppm.getFragmentAtNewCoordinate(new CoordinateImpl(0, 0,
                    0, 1));
            assertNotNull("null fragment found at 0,0", a_oFrag);
            assertEquals(a_oFrag.getId(), "2");
            assertEquals(a_oFrag.getName(), "frag2");

            a_oFrag = ppm.getFragmentAtNewCoordinate(new CoordinateImpl(0, 0,
                    1, 0));
            assertNotNull("null fragment found at 0,0", a_oFrag);
            assertEquals(a_oFrag.getId(), "3");
            assertEquals(a_oFrag.getName(), "frag3");

            a_oFrag = ppm.getFragmentAtNewCoordinate(new CoordinateImpl(0, 0,
                    1, 1));
            assertNotNull("null fragment found at 0,0", a_oFrag);
            assertEquals(a_oFrag.getId(), "4");
            assertEquals(a_oFrag.getName(), "frag4");

            a_oFrag = ppm.getFragmentAtNewCoordinate(new CoordinateImpl(0, 0,
                    1, 2));
            assertNotNull("null fragment found at 0,0", a_oFrag);
            assertEquals(a_oFrag.getId(), "5");
            assertEquals(a_oFrag.getName(), "frag5");

        } catch (PortletPlacementException e)
        {
            fail("creating the PortletPlacementManager failed");
        }
    }

    public void testGetFragmentById()
    {
        // Build a request object and populate it with fragments
        RequestContext a_oRC = FragmentUtil.buildFullRequestContext();

        try
        {
            PortletPlacementManager ppm = new PortletPlacementManagerImpl(a_oRC);

            // Check the fragments
            Fragment a_oFrag = ppm.getFragmentById("1");
            assertNotNull("null fragment with id 1", a_oFrag);
            assertEquals(a_oFrag.getId(), "1");
            assertEquals(a_oFrag.getName(), "frag1");

            a_oFrag = ppm.getFragmentById("2");
            assertNotNull("null fragment with id 2", a_oFrag);
            assertEquals(a_oFrag.getId(), "2");
            assertEquals(a_oFrag.getName(), "frag2");

            a_oFrag = ppm.getFragmentById("3");
            assertNotNull("null fragment with id 3", a_oFrag);
            assertEquals(a_oFrag.getId(), "3");
            assertEquals(a_oFrag.getName(), "frag3");

            a_oFrag = ppm.getFragmentById("4");
            assertNotNull("null fragment with id 4", a_oFrag);
            assertEquals(a_oFrag.getId(), "4");
            assertEquals(a_oFrag.getName(), "frag4");

            a_oFrag = ppm.getFragmentById("5");
            assertNotNull("null fragment with id 5", a_oFrag);
            assertEquals(a_oFrag.getId(), "5");
            assertEquals(a_oFrag.getName(), "frag5");

        } catch (PortletPlacementException e)
        {
            fail("creating the PortletPlacementManager failed");
        }
    }

    public void testRemoveFragment()
    {
        RequestContext a_oRC = FragmentUtil.buildFullRequestContext();

        try
        {
            PortletPlacementManager ppm = new PortletPlacementManagerImpl(a_oRC);

            Fragment a_oFrag = ppm
                    .getFragmentAtNewCoordinate(new CoordinateImpl(0, 0, 0, 0));

            Coordinate a_oCoordinate = ppm.remove(a_oFrag);

            assertEquals(a_oCoordinate.getOldCol(), 0);
            assertEquals(a_oCoordinate.getOldRow(), 0);

            // Should be the second fragment now that the first has been deleted
            a_oFrag = ppm.getFragmentAtNewCoordinate(new CoordinateImpl(0, 0,
                    0, 0));
            assertEquals(a_oFrag.getId(), "2");
            assertEquals(a_oFrag.getName(), "frag2");
        } catch (PortletPlacementException e)
        {
            fail("creating the PortletPlacementManager failed");
        }
    }

    public void footestFragmentMoveabs()
    {
        RequestContext a_oRC = FragmentUtil.buildFullRequestContext();

        try
        {
            PortletPlacementManager ppm = new PortletPlacementManagerImpl(a_oRC);

            Fragment a_oFrag = ppm
                    .getFragmentAtNewCoordinate(new CoordinateImpl(0, 0, 0, 0));

            Coordinate a_oCoordinate = ppm.moveAbs(a_oFrag, new CoordinateImpl(
                    0, 0, 0, 1));

            assertEquals(a_oCoordinate.getOldCol(), 0);
            assertEquals(a_oCoordinate.getOldRow(), 0);
            assertEquals(a_oCoordinate.getNewCol(), 0);
            assertEquals(a_oCoordinate.getNewRow(), 1);

            // Should be the second fragment now that the first has been moved
            a_oFrag = ppm.getFragmentAtNewCoordinate(new CoordinateImpl(0, 0,
                    0, 0));
            assertEquals(a_oFrag.getId(), "2");
            assertEquals(a_oFrag.getName(), "frag2");

            a_oFrag = ppm.getFragmentAtNewCoordinate(new CoordinateImpl(0, 0,
                    0, 1));
            assertEquals(a_oFrag.getId(), "1");
            assertEquals(a_oFrag.getName(), "frag1");
        } catch (PortletPlacementException e)
        {
            fail("creating the PortletPlacementManager failed");
        }
    }

    public void footestFragmentMoveUp()
    {
        RequestContext a_oRC = FragmentUtil.buildFullRequestContext();

        try
        {
            PortletPlacementManager ppm = new PortletPlacementManagerImpl(a_oRC);

            Fragment a_oFrag = ppm
                    .getFragmentAtNewCoordinate(new CoordinateImpl(0, 0, 0, 1));

            Coordinate a_oCoordinate = ppm.moveUp(a_oFrag);

            assertEquals(a_oCoordinate.getOldCol(), 0);
            assertEquals(a_oCoordinate.getOldRow(), 1);
            assertEquals(a_oCoordinate.getNewCol(), 0);
            assertEquals(a_oCoordinate.getNewRow(), 0);

            // Should be the second fragment since it was moved up
            a_oFrag = ppm.getFragmentAtNewCoordinate(new CoordinateImpl(0, 0,
                    0, 0));
            assertEquals(a_oFrag.getId(), "2");
            assertEquals(a_oFrag.getName(), "frag2");

            a_oFrag = ppm.getFragmentAtNewCoordinate(new CoordinateImpl(0, 0,
                    0, 1));
            assertEquals(a_oFrag.getId(), "1");
            assertEquals(a_oFrag.getName(), "frag1");
        } catch (PortletPlacementException e)
        {
            fail("creating the PortletPlacementManager failed");
        }
    }

}
