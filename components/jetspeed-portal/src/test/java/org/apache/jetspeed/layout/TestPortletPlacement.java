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
package org.apache.jetspeed.layout;

import junit.framework.TestCase;

import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.layout.impl.CoordinateImpl;
import org.apache.jetspeed.layout.impl.PortletPlacementContextImpl;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.request.RequestContext;

/**
 * Test for Fragment placement
 * 
 * @author <a>David Gurney </a>
 * @version $Id: $
 */
public class TestPortletPlacement extends TestCase
{
    private PortletRegistry portletRegistry;

    public void setUp(){
        portletRegistry = MockPortletRegistryFactory.createMockPortletRegistry();
    }
    
    public void testGetFragmentAt()
    {
        // Build a request object and populate it with fragments
        RequestContext requestContext = FragmentUtil.buildFullRequestContext();

        try
        {
            PortletPlacementContext ppc = new PortletPlacementContextImpl(requestContext.getPage(),portletRegistry);
            int a_iNumCols = ppc.getNumberColumns();
            assertEquals(a_iNumCols, 2);

            int a_iNumRows = ppc.getNumberRows(0);
            assertEquals(a_iNumRows, 2);

            a_iNumRows = ppc.getNumberRows(1);
            assertEquals(a_iNumRows, 3);

            // Check the fragments
            ContentFragment a_oFrag = ppc
                    .getFragmentAtNewCoordinate(new CoordinateImpl(0, 0, 0, 0));
            assertNotNull("null fragment found at 0,0", a_oFrag);
            assertEquals(a_oFrag.getId(), "1");
            assertEquals(a_oFrag.getName(), "frag1");

            a_oFrag = ppc.getFragmentAtNewCoordinate(new CoordinateImpl(0, 1,
                    0, 1));
            assertNotNull("null fragment found at 0,1", a_oFrag);
            assertEquals(a_oFrag.getId(), "2");
            assertEquals(a_oFrag.getName(), "frag2");

            a_oFrag = ppc.getFragmentAtNewCoordinate(new CoordinateImpl(1, 0,
                    1, 0));
            assertNotNull("null fragment found at 1,0", a_oFrag);
            assertEquals(a_oFrag.getId(), "3");
            assertEquals(a_oFrag.getName(), "frag3");

            a_oFrag = ppc.getFragmentAtNewCoordinate(new CoordinateImpl(1, 1,
                    1, 1));
            assertNotNull("null fragment found at 1,1", a_oFrag);
            assertEquals(a_oFrag.getId(), "4");
            assertEquals(a_oFrag.getName(), "frag4");

            a_oFrag = ppc.getFragmentAtNewCoordinate(new CoordinateImpl(1, 2,
                    1, 2));
            assertNotNull("null fragment found at 1,2", a_oFrag);
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
        RequestContext requestContext = FragmentUtil.buildFullRequestContext();

        try
        {
            PortletPlacementContext ppc = new PortletPlacementContextImpl(requestContext.getPage(),portletRegistry);

            // Check the fragments
            ContentFragment a_oFrag = ppc.getFragmentById("1");
            assertNotNull("null fragment with id 1", a_oFrag);
            assertEquals(a_oFrag.getId(), "1");
            assertEquals(a_oFrag.getName(), "frag1");

            a_oFrag = ppc.getFragmentById("2");
            assertNotNull("null fragment with id 2", a_oFrag);
            assertEquals(a_oFrag.getId(), "2");
            assertEquals(a_oFrag.getName(), "frag2");

            a_oFrag = ppc.getFragmentById("3");
            assertNotNull("null fragment with id 3", a_oFrag);
            assertEquals(a_oFrag.getId(), "3");
            assertEquals(a_oFrag.getName(), "frag3");

            a_oFrag = ppc.getFragmentById("4");
            assertNotNull("null fragment with id 4", a_oFrag);
            assertEquals(a_oFrag.getId(), "4");
            assertEquals(a_oFrag.getName(), "frag4");

            a_oFrag = ppc.getFragmentById("5");
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
        RequestContext requestContext = FragmentUtil.buildFullRequestContext();

        try
        {
            PortletPlacementContext ppc = new PortletPlacementContextImpl(requestContext.getPage(),portletRegistry);

            ContentFragment a_oFrag = ppc
                    .getFragmentAtNewCoordinate(new CoordinateImpl(0, 0, 0, 0));

            Coordinate a_oCoordinate = ppc.remove(a_oFrag);

            assertEquals(a_oCoordinate.getOldCol(), 0);
            assertEquals(a_oCoordinate.getOldRow(), 0);

            // Should be the second fragment now that the first has been deleted
            a_oFrag = ppc.getFragmentAtNewCoordinate(new CoordinateImpl(0, 0,
                    0, 0));
            assertEquals(a_oFrag.getId(), "2");
            assertEquals(a_oFrag.getName(), "frag2");
        } catch (PortletPlacementException e)
        {
            fail("creating the PortletPlacementManager failed");
        }
    }

    public void testFragmentMoveabs()
    {
        RequestContext requestContext = FragmentUtil.buildFullRequestContext();

        try
        {
            PortletPlacementContext ppc = new PortletPlacementContextImpl(requestContext.getPage(),portletRegistry);

            ContentFragment a_oFrag = ppc
                    .getFragmentAtNewCoordinate(new CoordinateImpl(0, 0, 0, 0));

            Coordinate a_oCoordinate = ppc.moveAbsolute(a_oFrag, new CoordinateImpl(
                    0, 0, 0, 1));

            assertEquals(a_oCoordinate.getOldCol(), 0);
            assertEquals(a_oCoordinate.getOldRow(), 0);
            assertEquals(a_oCoordinate.getNewCol(), 0);
            assertEquals(a_oCoordinate.getNewRow(), 1);

            // Should be the second fragment now that the first has been moved
            a_oFrag = ppc.getFragmentAtNewCoordinate(new CoordinateImpl(0, 0,
                    0, 0));
            assertEquals(a_oFrag.getId(), "2");
            assertEquals(a_oFrag.getName(), "frag2");

            a_oFrag = ppc.getFragmentAtNewCoordinate(new CoordinateImpl(0, 0,
                    0, 1));
            assertEquals(a_oFrag.getId(), "1");
            assertEquals(a_oFrag.getName(), "frag1");
        } catch (PortletPlacementException e)
        {
            fail("creating the PortletPlacementManager failed");
        }
    }

    public void testFragmentMoveUp()
    {
        RequestContext requestContext = FragmentUtil.buildFullRequestContext();

        try
        {
            PortletPlacementContext ppc = new PortletPlacementContextImpl(requestContext.getPage(),portletRegistry);

            ContentFragment a_oFrag = ppc
                    .getFragmentAtNewCoordinate(new CoordinateImpl(0, 0, 0, 1));

            Coordinate a_oCoordinate = ppc.moveUp(a_oFrag);

            assertEquals(a_oCoordinate.getOldCol(), 0);
            assertEquals(a_oCoordinate.getOldRow(), 1);
            assertEquals(a_oCoordinate.getNewCol(), 0);
            assertEquals(a_oCoordinate.getNewRow(), 0);

            // Should be the second fragment since it was moved up
            a_oFrag = ppc.getFragmentAtNewCoordinate(new CoordinateImpl(0, 0,
                    0, 0));
            assertEquals(a_oFrag.getId(), "2");
            assertEquals(a_oFrag.getName(), "frag2");

            a_oFrag = ppc.getFragmentAtNewCoordinate(new CoordinateImpl(0, 0,
                    0, 1));
            assertEquals(a_oFrag.getId(), "1");
            assertEquals(a_oFrag.getName(), "frag1");
        } catch (PortletPlacementException e)
        {
            fail("creating the PortletPlacementManager failed");
        }
    }

}
