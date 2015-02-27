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
package org.apache.jetspeed.portlets.layout;

import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.impl.ContentFragmentImpl;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.util.Iterator;

public class TestColumnLayout extends MockObjectTestCase
{
    private static final String[] widths=new String[]{"25%", "50%", "25%"};
    
    private ColumnLayout layout;

    private ContentFragmentImpl f1;

    private ContentFragmentImpl f2;

    private ContentFragmentImpl f3;

    private ContentFragmentImpl f4;

    private ContentFragmentImpl f5;

    private ContentFragmentImpl f6;
    
    private ContentFragmentImpl f8;

    public void testBasics() throws Exception
    {
        assertEquals(f1, layout.getFirstColumn().iterator().next());
        
        // The last column is currently empty
      //  assertTrue(layout.getLastColumn().isEmpty());

        assertEquals(3, layout.getNumberOfColumns());
        Iterator column0 = layout.getColumn(0).iterator();
        assertNotNull(column0);
        Iterator column1 = layout.getColumn(1).iterator();
        assertNotNull(column1);
        assertNotNull(layout.getColumn(2));

        int idx = 0;
        while (column0.hasNext())
        {
            idx++;
            ContentFragment fragment = (ContentFragment) column0.next();
            assertEquals("f" + idx, fragment.getId());
        }

        assertEquals(3, idx);

        assertEquals(f4, column1.next());
        assertEquals(f5, column1.next());
        assertEquals(f6, column1.next());

        ContentFragment testFragment = layout.getFragmentAt(new LayoutCoordinate(0, 0));
        assertNotNull(testFragment);
        assertEquals(f1, testFragment);

        testFragment = layout.getFragmentAt(new LayoutCoordinate(0, 1));
        assertNotNull(testFragment);
        assertEquals(f2, testFragment);

        testFragment = layout.getFragmentAt(1, 0);
        assertNotNull(testFragment);
        assertEquals(f4, testFragment);
        
        assertEquals(3, layout.getColumns().size());
        
        assertEquals(2, layout.getLastRowNumber(0));
        assertEquals(2, layout.getLastRowNumber(1));       
        
        // test widths
        assertEquals("25%", layout.getColumnWidth(0));
        assertEquals("50%", layout.getColumnWidth(1));
        assertEquals("24.99%", layout.getColumnWidth(2));        
        assertEquals("0", layout.getColumnWidth(3));        

        assertEquals("left", layout.getColumnFloat(0));
        assertEquals("left", layout.getColumnFloat(1));
        assertEquals("right", layout.getColumnFloat(2));
        assertEquals("none", layout.getColumnFloat(3));
    }

    public void testSameRowSameColumn() throws Exception
    {
        ContentFragmentImpl f1 = new ContentFragmentImpl("f1");
        f1.setName("test");
        f1.setLayoutRow(0);
        f1.setLayoutColumn(0);

        ContentFragmentImpl f2 = new ContentFragmentImpl("f2");
        f2.setName("test");
        f2.setLayoutRow(0);
        f2.setLayoutColumn(0);

        ColumnLayout layout = new ColumnLayout(3, "test", null);
        layout.addFragment(f1);
        layout.addFragment(f2);

        Iterator column0 = layout.getColumn(0).iterator();
        // all subsequent fragments that go into the same row will push
        // the existing fragment down.
        assertEquals(f2, column0.next());
        assertEquals(f1, column0.next());

        ContentFragment testFragment = layout.getFragmentAt(0, 1);
        assertNotNull(testFragment);
        assertEquals(f1, testFragment);

        testFragment = layout.getFragmentAt(0, 0);
        assertNotNull(testFragment);
        assertEquals(f2, testFragment);

    }

    public void testColumnNotSet() throws Exception
    {
        ContentFragmentImpl f1 = new ContentFragmentImpl("f1");
        f1.setName("test");
        f1.setLayoutRow(0);
        f1.setLayoutColumn(0);

        ContentFragmentImpl f2 = new ContentFragmentImpl("f2");
        f2.setName("test");
        f2.setLayoutRow(0);

        ColumnLayout layout = new ColumnLayout(3, "test", null);
        layout.addFragment(f1);
        layout.addFragment(f2);

        ContentFragment testFragment = layout.getFragmentAt(0, 0);
        assertNotNull(testFragment);
        assertEquals(f1, testFragment);

        testFragment = layout.getFragmentAt(2, 0);
        assertNotNull(testFragment);
        assertEquals(f2, testFragment);

        assertNotNull(layout.getFirstColumn());
        assertNotNull(layout.getLastColumn());
    }

    public void testRowNotSet() throws Exception
    {
        ContentFragmentImpl f1 = new ContentFragmentImpl("f1");
        f1.setName("test");
        f1.setLayoutRow(0);
        f1.setLayoutColumn(0);

        ContentFragmentImpl f2 = new ContentFragmentImpl("f2");
        f2.setName("test");
        f2.setLayoutColumn(0);

        ColumnLayout layout = new ColumnLayout(3, "test", null);
        layout.addFragment(f1);
        layout.addFragment(f2);

        ContentFragment testFragment = layout.getFragmentAt(0, 0);
        assertNotNull(testFragment);
        assertEquals(f1, testFragment);

        testFragment = layout.getFragmentAt(0, 1);
        assertNotNull(testFragment);
        assertEquals(f2, testFragment);
    }

    public void testColumnLimitExceeded() throws Exception
    {
        ContentFragmentImpl f1 = new ContentFragmentImpl("f1");
        f1.setLayoutRow(0);
        f1.setLayoutColumn(5);

        ColumnLayout layout = new ColumnLayout(3, "test", null);
        layout.addFragment(f1);

        // Exceeded columns just get dumped into the last column
        ContentFragment testFragment = layout.getFragmentAt(2, 0);
        assertNotNull(testFragment);
        assertEquals(f1, testFragment);
    }

    public void testMovingRight() throws Exception
    {
        ContentFragment movingFragment = layout.getFragmentAt(new LayoutCoordinate(0, 0));
        assertEquals(f1, movingFragment);
        assertEquals(f4, layout.getFragmentAt(new LayoutCoordinate(1, 0)));

        Mock listenerMock = mock(LayoutEventListener.class);
        layout.addLayoutEventListener((LayoutEventListener) listenerMock.proxy());
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f1, layout, LayoutEvent.MOVED_RIGHT)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f2, layout, LayoutEvent.MOVED_UP)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f3, layout, LayoutEvent.MOVED_UP)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f4, layout, LayoutEvent.MOVED_DOWN)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f5, layout, LayoutEvent.MOVED_DOWN)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f6, layout, LayoutEvent.MOVED_DOWN)));
        
        
        // moving right
        layout.moveRight(movingFragment);
        
        assertEquals(f1, layout.getFragmentAt(new LayoutCoordinate(1, 0)));
        assertEquals(f2, layout.getFragmentAt(new LayoutCoordinate(0, 0)));
        assertEquals(f3, layout.getFragmentAt(new LayoutCoordinate(0, 1)));
        assertEquals(f4, layout.getFragmentAt(new LayoutCoordinate(1, 1)));
        assertEquals(f5, layout.getFragmentAt(new LayoutCoordinate(1, 2)));
        assertEquals(f6, layout.getFragmentAt(new LayoutCoordinate(1, 3)));
    }

    public void testMovingLeft() throws Exception
    {
        ContentFragment movingFragment = layout.getFragmentAt(new LayoutCoordinate(1, 0));
        assertEquals(f4, movingFragment);
        assertEquals(f1, layout.getFragmentAt(new LayoutCoordinate(0, 0)));
        
        Mock listenerMock = mock(LayoutEventListener.class);
        layout.addLayoutEventListener((LayoutEventListener) listenerMock.proxy());
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f4, layout, LayoutEvent.MOVED_LEFT)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f5, layout, LayoutEvent.MOVED_UP)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f6, layout, LayoutEvent.MOVED_UP)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f1, layout, LayoutEvent.MOVED_DOWN)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f2, layout, LayoutEvent.MOVED_DOWN)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f3, layout, LayoutEvent.MOVED_DOWN)));
        
        layout.moveLeft(f4);

        assertEquals(f1, layout.getFragmentAt(new LayoutCoordinate(0, 1)));
        assertEquals(f2, layout.getFragmentAt(new LayoutCoordinate(0, 2)));
        assertEquals(f3, layout.getFragmentAt(new LayoutCoordinate(0, 3)));
        assertEquals(f4, layout.getFragmentAt(new LayoutCoordinate(0, 0)));
        assertEquals(f5, layout.getFragmentAt(new LayoutCoordinate(1, 0)));
        assertEquals(f6, layout.getFragmentAt(new LayoutCoordinate(1, 1)));
        
        listenerMock.reset();       
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f6, layout, LayoutEvent.MOVED_LEFT)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f1, layout, LayoutEvent.MOVED_DOWN)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f2, layout, LayoutEvent.MOVED_DOWN)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f3, layout, LayoutEvent.MOVED_DOWN)));

        layout.moveLeft(f6);

        assertEquals(f1, layout.getFragmentAt(new LayoutCoordinate(0, 2)));
        assertEquals(f2, layout.getFragmentAt(new LayoutCoordinate(0, 3)));
        assertEquals(f3, layout.getFragmentAt(new LayoutCoordinate(0, 4)));
        assertEquals(f4, layout.getFragmentAt(new LayoutCoordinate(0, 0)));
        assertEquals(f5, layout.getFragmentAt(new LayoutCoordinate(1, 0)));
        assertEquals(f6, layout.getFragmentAt(new LayoutCoordinate(0, 1)));

        
        listenerMock.reset();
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f5, layout, LayoutEvent.MOVED_LEFT)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f4, layout, LayoutEvent.MOVED_DOWN)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f6, layout, LayoutEvent.MOVED_DOWN)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f1, layout, LayoutEvent.MOVED_DOWN)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f2, layout, LayoutEvent.MOVED_DOWN)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f3, layout, LayoutEvent.MOVED_DOWN)));
        
        
        layout.moveLeft(f5);

        assertEquals(f1, layout.getFragmentAt(new LayoutCoordinate(0, 3)));
        assertEquals(f2, layout.getFragmentAt(new LayoutCoordinate(0, 4)));
        assertEquals(f3, layout.getFragmentAt(new LayoutCoordinate(0, 5)));
        assertEquals(f4, layout.getFragmentAt(new LayoutCoordinate(0, 1)));
        assertEquals(f5, layout.getFragmentAt(new LayoutCoordinate(0, 0)));
        assertEquals(f6, layout.getFragmentAt(new LayoutCoordinate(0, 2)));

        // This is a test to make sure the next row pointer is being decremented
        // correctly
        ContentFragmentImpl f7 = new ContentFragmentImpl("f7");
        f7.setName("test");
        f7.setLayoutRow(0);
        f7.setLayoutColumn(1);

        listenerMock.reset();
        LayoutCoordinate coordinate = new LayoutCoordinate(1, 0);
        LayoutEvent event = new LayoutEvent(LayoutEvent.ADDED, f7, coordinate, coordinate);
        listenerMock.expects(once()).method("handleEvent").with(eq(event));
        
        layout.addFragment(f7);

        assertEquals(f1, layout.getFragmentAt(new LayoutCoordinate(0, 3)));
        assertEquals(f2, layout.getFragmentAt(new LayoutCoordinate(0, 4)));
        assertEquals(f3, layout.getFragmentAt(new LayoutCoordinate(0, 5)));
        assertEquals(f4, layout.getFragmentAt(new LayoutCoordinate(0, 1)));
        assertEquals(f5, layout.getFragmentAt(new LayoutCoordinate(0, 0)));
        assertEquals(f6, layout.getFragmentAt(new LayoutCoordinate(0, 2)));
        assertEquals(f7, layout.getFragmentAt(new LayoutCoordinate(1, 0)));

        // test that column consistency is maintained
        Iterator itr1 = layout.getColumn(1).iterator();

        itr1.next().equals(f7);

        Iterator itr0 = layout.getColumn(0).iterator();

        itr0.next().equals(f5);
        itr0.next().equals(f4);
        itr0.next().equals(f6);
        itr0.next().equals(f1);
        itr0.next().equals(f2);
        itr0.next().equals(f3);
    }

    public void testInvalidOperations() throws Exception
    {
        // Create a mock that verifies events are NOT being fired on invalid operations
        Mock listenerMock = mock(LayoutEventListener.class);
        layout.addLayoutEventListener((LayoutEventListener) listenerMock.proxy());
        listenerMock.expects(never()).method("handleEvent").withAnyArguments();
        
        layout.moveUp(f1); // Nothing at all should happen, not even exceptions

        assertEquals(f1, layout.getFragmentAt(new LayoutCoordinate(0, 0)));
        assertEquals(f2, layout.getFragmentAt(new LayoutCoordinate(0, 1)));
        assertEquals(f3, layout.getFragmentAt(new LayoutCoordinate(0, 2)));
        assertEquals(f4, layout.getFragmentAt(new LayoutCoordinate(1, 0)));
        assertEquals(f5, layout.getFragmentAt(new LayoutCoordinate(1, 1)));
        assertEquals(f6, layout.getFragmentAt(new LayoutCoordinate(1, 2)));

        layout.moveDown(f3); // Nothing at all should happen, not even
                                // exceptions

        assertEquals(f1, layout.getFragmentAt(new LayoutCoordinate(0, 0)));
        assertEquals(f2, layout.getFragmentAt(new LayoutCoordinate(0, 1)));
        assertEquals(f3, layout.getFragmentAt(new LayoutCoordinate(0, 2)));
        assertEquals(f4, layout.getFragmentAt(new LayoutCoordinate(1, 0)));
        assertEquals(f5, layout.getFragmentAt(new LayoutCoordinate(1, 1)));
        assertEquals(f6, layout.getFragmentAt(new LayoutCoordinate(1, 2)));

        layout.moveLeft(f1); // Nothing at all should happen, not even
        // exceptions

        assertEquals(f1, layout.getFragmentAt(new LayoutCoordinate(0, 0)));
        assertEquals(f2, layout.getFragmentAt(new LayoutCoordinate(0, 1)));
        assertEquals(f3, layout.getFragmentAt(new LayoutCoordinate(0, 2)));
        assertEquals(f4, layout.getFragmentAt(new LayoutCoordinate(1, 0)));
        assertEquals(f5, layout.getFragmentAt(new LayoutCoordinate(1, 1)));
        assertEquals(f6, layout.getFragmentAt(new LayoutCoordinate(1, 2)));
    }

    public void testMoveDown() throws Exception
    {
        Mock listenerMock = mock(LayoutEventListener.class);
        layout.addLayoutEventListener((LayoutEventListener) listenerMock.proxy());
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f1, layout, LayoutEvent.MOVED_DOWN)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f2, layout, LayoutEvent.MOVED_UP)));

        layout.moveDown(f1);

        assertEquals(f1, layout.getFragmentAt(new LayoutCoordinate(0, 1)));
        assertEquals(f2, layout.getFragmentAt(new LayoutCoordinate(0, 0)));
        assertEquals(f3, layout.getFragmentAt(new LayoutCoordinate(0, 2)));
        assertEquals(f4, layout.getFragmentAt(new LayoutCoordinate(1, 0)));
        assertEquals(f5, layout.getFragmentAt(new LayoutCoordinate(1, 1)));
        assertEquals(f6, layout.getFragmentAt(new LayoutCoordinate(1, 2)));
        
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f1, layout, LayoutEvent.MOVED_DOWN)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f3, layout, LayoutEvent.MOVED_UP)));

        layout.moveDown(f1);

        assertEquals(f1, layout.getFragmentAt(new LayoutCoordinate(0, 2)));
        assertEquals(f2, layout.getFragmentAt(new LayoutCoordinate(0, 0)));
        assertEquals(f3, layout.getFragmentAt(new LayoutCoordinate(0, 1)));
        assertEquals(f4, layout.getFragmentAt(new LayoutCoordinate(1, 0)));
        assertEquals(f5, layout.getFragmentAt(new LayoutCoordinate(1, 1)));
        assertEquals(f6, layout.getFragmentAt(new LayoutCoordinate(1, 2)));
        
        //try moving a fragment below the bottom-most row.
        listenerMock.expects(never()).method("handleEvent").withAnyArguments();
        layout.moveDown(f6);
        assertEquals(f6, layout.getFragmentAt(new LayoutCoordinate(1, 2)));

    }

    public void testMoveUp() throws Exception
    {

        Mock listenerMock = mock(LayoutEventListener.class);
        layout.addLayoutEventListener((LayoutEventListener) listenerMock.proxy());
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f3, layout, LayoutEvent.MOVED_UP)));
        listenerMock.expects(once()).method("handleEvent").with(eq(createEvent(f2, layout, LayoutEvent.MOVED_DOWN)));
        layout.moveUp(f3);

        assertEquals(f1, layout.getFragmentAt(new LayoutCoordinate(0, 0)));
        assertEquals(f2, layout.getFragmentAt(new LayoutCoordinate(0, 2)));
        assertEquals(f3, layout.getFragmentAt(new LayoutCoordinate(0, 1)));
        assertEquals(f4, layout.getFragmentAt(new LayoutCoordinate(1, 0)));
        assertEquals(f5, layout.getFragmentAt(new LayoutCoordinate(1, 1)));
        assertEquals(f6, layout.getFragmentAt(new LayoutCoordinate(1, 2)));
    }

    protected void setUp() throws Exception
    {
        f1 = new ContentFragmentImpl("f1");
        f1.setName("test");
        f1.setLayoutRow(0);
        f1.setLayoutColumn(0);

        f2 = new ContentFragmentImpl("f2");
        f2.setName("test");
        f2.setLayoutRow(1);
        f2.setLayoutColumn(0);

        f3 = new ContentFragmentImpl("f3");
        f3.setName("test");
        f3.setLayoutRow(2);
        f3.setLayoutColumn(0);
        
        f4 = new ContentFragmentImpl("f4");
        f4.setName("test");
        f4.setLayoutRow(0);
        f4.setLayoutColumn(1);
        
        f5 = new ContentFragmentImpl("f5");
        f5.setName("test");
        f5.setLayoutRow(1);
        f5.setLayoutColumn(1);

        f6 = new ContentFragmentImpl("f6");
        f6.setName("test");
        f6.setLayoutRow(2);
        f6.setLayoutColumn(1);
        
        f8 = new ContentFragmentImpl("f8");
        f8.setName("test");
        f8.setLayoutRow(1);
        f8.setLayoutColumn(2);

        layout = new ColumnLayout(3, "test", widths);
        layout.addFragment(f1);
        layout.addFragment(f2);
        layout.addFragment(f3);
        layout.addFragment(f4);
        layout.addFragment(f5);
        layout.addFragment(f6);
        layout.addFragment(f8);
    }

    protected LayoutEvent createEvent(ContentFragment fragment, ColumnLayout layout, int eventType) throws Exception
    {
        LayoutCoordinate fragmentOriginal = layout.getCoordinate(fragment);
        LayoutCoordinate fragmentNew;

        switch (eventType)
        {
        case LayoutEvent.MOVED_UP:
            fragmentNew = new LayoutCoordinate(fragmentOriginal.getX(), fragmentOriginal.getY() - 1);
            break;
        case LayoutEvent.MOVED_DOWN:
            fragmentNew = new LayoutCoordinate(fragmentOriginal.getX(), fragmentOriginal.getY() + 1);
            break;
        case LayoutEvent.MOVED_LEFT:
            fragmentNew = new LayoutCoordinate(fragmentOriginal.getX() - 1, fragmentOriginal.getY());
            break;
        case LayoutEvent.MOVED_RIGHT:
            fragmentNew = new LayoutCoordinate(fragmentOriginal.getX() + 1, fragmentOriginal.getY());
            break;
        default:
            fragmentNew = new LayoutCoordinate(fragmentOriginal.getX(), fragmentOriginal.getY());
            break;
        }

        return new LayoutEvent(eventType, fragment, fragmentOriginal, fragmentNew);

    }

}
