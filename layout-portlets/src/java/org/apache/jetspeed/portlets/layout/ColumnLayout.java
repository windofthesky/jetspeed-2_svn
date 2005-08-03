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
package org.apache.jetspeed.portlets.layout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.jetspeed.om.page.Fragment;

/**
 * <p>
 * <code>ColumnLayout</code> is the model used to support any 1 to <i>n</i>
 * column-based layout. <code>ColumnLayout</code> is constrained by a number
 * columns that will not be exceeded, even if a fragment specifies a column
 * outside of this constraint. Any fragment exceeded the specified column
 * constraint will be deposited into the right-most column.
 * </p>
 * Columns always start at 0.
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 * 
 */
public class ColumnLayout implements Serializable
{
    private static final String COLUMN = "column";

    private static final String ROW = "row";

    private final int numberOfColumns;

    private final SortedMap columns;

    private final String layoutType;

    /** Efficent way to always be aware of the next available row in a column */
    private final int[] nextRowNumber;
    
    /** maps Fragments (key) to it's current LayoutCoordinate (value) in this layout */
    private final Map coordinates;
    
    private final List eventListeners;

    /**
     * 
     * @param numberOfColumns
     *            the maximum number of columns this layout will have.
     * @param layoutType
     *            this value corresponds to the property settings of the
     *            fragments within your psml. Layout type allows segration of
     *            property settings based on the type of layout in use. This
     *            effectively allows for the interchange of multiple layout
     *            formats without one format effecting the settings of another.
     */
    public ColumnLayout(int numberOfColumns, String layoutType)
    {
        this.numberOfColumns = numberOfColumns;
        this.layoutType = layoutType;
        eventListeners = new ArrayList();

        columns = new TreeMap();
        coordinates = new HashMap();

        for (int i = 0; i < numberOfColumns; i++)
        {
            columns.put(new Integer(i), new TreeMap());
        }

        nextRowNumber = new int[numberOfColumns];

        for (int i = 0; i < numberOfColumns; i++)
        {
            nextRowNumber[i] = 0;
        }
    }

    public ColumnLayout(int numberOfColumns, String layoutType, Collection fragments) throws LayoutEventException
    {
        this(numberOfColumns, layoutType);
        Iterator fragmentsItr = fragments.iterator();
        try
        {
            while (fragmentsItr.hasNext())
            {
                Fragment fragment = (Fragment) fragmentsItr.next();
                doAdd(getColumn(fragment), getRow(getColumn(fragment), fragment), fragment);
            }
        }
        catch (InvalidLayoutLocationException e)
        {
            // This should NEVER happen as getColumn() should
            // automatically constrain any fragments who's column
            // setting would cause this exception.
            throw new LayoutError("A malformed fragment could not be adjusted.", e);
        }
    }

    /**
     * <p>
     * Adds a fragment to the layout using fragment properties of
     * <code> row  </code> and <code> column  </code> as hints on where to put
     * this fragment. The following rules apply to malformed fragment
     * definitions:
     * </p>
     * <ul>
     * <li>Fragments without a row defined are placed at the bottom of their
     * respective column </li>
     * <li>Fragments without a column are placed in the right-most column.
     * </li>
     * <li> Fragments with overlapping row numbers. The last fragment has
     * priority pushing the fragment in that row down one row. </li>
     * </ul>
     * 
     * @param fragment
     *            Fragment to add to this layout.
     * @throws LayoutEventException 
     * 
     */
    public void addFragment(Fragment fragment) throws LayoutEventException
    {
        try
        {
            doAdd(getColumn(fragment), getRow(getColumn(fragment), fragment), fragment);
            LayoutCoordinate coordinate = getCoordinate(fragment);
            processEvent(new LayoutEvent(LayoutEvent.ADDED, fragment, coordinate, coordinate));
        }
        catch (InvalidLayoutLocationException e)
        {
            // This should NEVER happen as getColumn() should
            // automatically constrain any fragments who's column
            // setting would cause this exception.
            throw new LayoutError("A malformed fragment could not be adjusted.", e);
        }
        catch (FragmentNotInLayoutException e)
        {        
            throw new LayoutError("Failed to add coordinate to this ColumnLayout.", e);
        }
    }
    
    public void addLayoutEventListener(LayoutEventListener eventListener)
    {
        eventListeners.add(eventListener);
    }

    /**
     * 
     * 
     * @param columnNumber
     *            Number of column to retreive
     * @return requested column (as a immutable Collection). Never returns
     *         <code>null.</code>
     * @throws InvalidLayoutLocationException
     *             if the column is outisde of the constraintes of this layout
     */
    public Collection getColumn(int columnNumber) throws InvalidLayoutLocationException
    {
        return Collections.unmodifiableCollection(getColumnMap(columnNumber).values());
    }

    /**
     * @return <code>java.util.Collection</code> all of columns (also
     *         Collection objects) in order within this layout. All Collections
     *         are immutable.
     */
    public Collection getColumns()
    {
        ArrayList columnList = new ArrayList(getNumberOfColumns());
        Iterator itr = columns.values().iterator();
        while (itr.hasNext())
        {
            columnList.add(Collections.unmodifiableCollection(((Map) itr.next()).values()));
        }

        return Collections.unmodifiableCollection(columnList);
    }
    
    /**
     * Returns an immutable Collection of all the Fragments contained within
     * this ColumnLayout in no sepcific order.
     * @return Immutable Collection of Fragments.
     */
    public Collection getFragments()
    {
        return Collections.unmodifiableCollection(coordinates.keySet());
    }

    /**
     * 
     * @param columnNumber
     * @param rowNumber
     * @return
     * @throws EmptyLayoutLocationException
     * @throws InvalidLayoutLocationException
     */
    public Fragment getFragmentAt(int columnNumber, int rowNumber) throws EmptyLayoutLocationException,
            InvalidLayoutLocationException
    {
        SortedMap column = getColumnMap(columnNumber);
        Integer rowInteger = new Integer(rowNumber);
        if (column.containsKey(rowInteger))
        {
            return (Fragment) column.get(rowInteger);
        }
        else
        {
            throw new EmptyLayoutLocationException(columnNumber, rowNumber);
        }
    }

    public Fragment getFragmentAt(LayoutCoordinate coodinate) throws EmptyLayoutLocationException,
            InvalidLayoutLocationException
    {
        return getFragmentAt(coodinate.getX(), coodinate.getY());
    }

    /**
     * 
     * @return
     */
    public int getNumberOfColumns()
    {
        return numberOfColumns;
    }

    /**
     * 
     * @return
     * @throws InvalidLayoutLocationException
     */
    public Collection getLastColumn() throws InvalidLayoutLocationException
    {
        return Collections.unmodifiableCollection((Collection) getColumnMap(numberOfColumns - 1).values());
    }

    /**
     * 
     * @return
     * @throws InvalidLayoutLocationException
     */
    public Collection getFirstColumn() throws InvalidLayoutLocationException
    {
        return Collections.unmodifiableCollection((Collection) getColumnMap(1).values());
    }

    /**
     * 
     * @param fragment
     * @throws FragmentNotInLayoutException
     * @throws InvalidLayoutLocationException
     * @throws LayoutEventException 
     */
    public void moveRight(Fragment fragment) throws FragmentNotInLayoutException, InvalidLayoutLocationException, LayoutEventException
    {
        LayoutCoordinate coordinate = getCoordinate(fragment);
        LayoutCoordinate newCoordinate = new LayoutCoordinate(coordinate.getX() + 1, coordinate.getY());

        if (newCoordinate.getX() < numberOfColumns)
        {

            doMove(fragment, coordinate, newCoordinate);
            processEvent(new LayoutEvent(LayoutEvent.MOVED_RIGHT, fragment, coordinate, newCoordinate));
            // now move the fragment below up one level.
            try
            {
                Fragment fragmentBelow = getFragmentAt(new LayoutCoordinate(coordinate.getX(), coordinate.getY() + 1));
                moveUp(fragmentBelow);
            }
            catch (EmptyLayoutLocationException e)
            {
                // indicates no fragment below
            }
        }
    }

    /**
     * 
     * @param fragment
     * @throws FragmentNotInLayoutException
     * @throws InvalidLayoutLocationException
     * @throws LayoutEventException 
     */
    public void moveLeft(Fragment fragment) throws FragmentNotInLayoutException, InvalidLayoutLocationException, LayoutEventException
    {
        LayoutCoordinate coordinate = getCoordinate(fragment);
        LayoutCoordinate newCoordinate = new LayoutCoordinate(coordinate.getX() - 1, coordinate.getY());

        if (newCoordinate.getX() >= 0)
        {
            doMove(fragment, coordinate, newCoordinate);
            processEvent(new LayoutEvent(LayoutEvent.MOVED_LEFT, fragment, coordinate, newCoordinate));
            // now move the fragment below up one level.
            try
            {
                Fragment fragmentBelow = getFragmentAt(new LayoutCoordinate(coordinate.getX(), coordinate.getY() + 1));
                moveUp(fragmentBelow);
            }
            catch (EmptyLayoutLocationException e)
            {
                // indicates no fragment below
            }
        }

    }

    /**
     * 
     * @param fragment
     * @throws FragmentNotInLayoutException
     * @throws InvalidLayoutLocationException
     * @throws LayoutEventException 
     */
    public void moveUp(Fragment fragment) throws FragmentNotInLayoutException, InvalidLayoutLocationException, LayoutEventException
    {
        LayoutCoordinate coordinate = getCoordinate(fragment);
        LayoutCoordinate aboveLayoutCoordinate = new LayoutCoordinate(coordinate.getX(), coordinate.getY() - 1);
        LayoutCoordinate newCoordinate = aboveLayoutCoordinate;

        // never go "above" 0.
        if (newCoordinate.getY() >= 0)
        {
            try
            {
                // now move the fragment above down one level.
                Fragment fragmentAbove = getFragmentAt(aboveLayoutCoordinate);
                doMove(fragment, coordinate, newCoordinate);
                processEvent(new LayoutEvent(LayoutEvent.MOVED_UP, fragment, coordinate, newCoordinate));                
            }
            catch (EmptyLayoutLocationException e)
            {
                // Nothing above??? Then scoot all elements below up one level.
                doMove(fragment, coordinate, newCoordinate);
                processEvent(new LayoutEvent(LayoutEvent.MOVED_UP, fragment, coordinate, newCoordinate));
                
                // If this the last row, make sure to update the next row pointer accordingly.
                if(coordinate.getY() == (nextRowNumber[coordinate.getX()] - 1))
                {
                    nextRowNumber[coordinate.getX()] = coordinate.getX();
                }
                
                try
                {
                    Fragment fragmentBelow = getFragmentAt(new LayoutCoordinate(coordinate.getX(),
                            coordinate.getY() + 1));
                    moveUp(fragmentBelow);
                }
                catch (EmptyLayoutLocationException e1)
                {

                }
            }

        }
    }

    /**
     * 
     * @param fragment
     * @throws FragmentNotInLayoutException
     * @throws InvalidLayoutLocationException
     * @throws LayoutEventException 
     */
    public void moveDown(Fragment fragment) throws FragmentNotInLayoutException, InvalidLayoutLocationException, LayoutEventException
    {
        LayoutCoordinate coordinate = getCoordinate(fragment);
        LayoutCoordinate newCoordinate = new LayoutCoordinate(coordinate.getX(), coordinate.getY() + 1);

        // never move past the current bottom row
        if (newCoordinate.getY() < nextRowNumber[coordinate.getX()])
        {
            try
            {
                // the best approach to move a fragment down is to actually move
                // its neighbor underneath up
                LayoutCoordinate aboveCoord = new LayoutCoordinate(coordinate.getX(), coordinate.getY() + 1);
                Fragment fragmentBelow = getFragmentAt(aboveCoord);
                doMove(fragmentBelow, aboveCoord, coordinate);
                processEvent(new LayoutEvent(LayoutEvent.MOVED_UP, fragmentBelow, aboveCoord, coordinate));
                // Since this logic path is a somewhat special case, the processing of the  MOVED_DOWN
                // event happens within the doAdd() method.
            }
            catch (EmptyLayoutLocationException e)
            {
                doMove(fragment, coordinate, newCoordinate);
                processEvent(new LayoutEvent(LayoutEvent.MOVED_DOWN, fragment, coordinate, newCoordinate));
            }
        }
    }

    /**
     * 
     * @param fragment
     * @param oldCoordinate
     * @param newCoordinate
     * @throws InvalidLayoutLocationException
     * @throws LayoutEventException 
     */
    protected void doMove(Fragment fragment, LayoutCoordinate oldCoordinate, LayoutCoordinate newCoordinate)
            throws InvalidLayoutLocationException, LayoutEventException
    {
        SortedMap oldColumn = getColumnMap(oldCoordinate.getX());
        oldColumn.remove(new Integer(oldCoordinate.getY()));
        coordinates.remove(fragment);

        doAdd(newCoordinate.getX(), newCoordinate.getY(), fragment);
    }

    /**
     * 
     * @param fragment
     * @return
     * @throws FragmentNotInLayoutException
     */
    public LayoutCoordinate getCoordinate(Fragment fragment) throws FragmentNotInLayoutException
    {
        if (coordinates.containsKey(fragment))
        {
            return (LayoutCoordinate) coordinates.get(fragment);
        }
        else
        {
            throw new FragmentNotInLayoutException(fragment);
        }
    }

    /**
     * 
     * @param columnNumber
     * @param rowNumber
     * @param fragment
     * @throws InvalidLayoutLocationException
     * @throws LayoutEventException 
     */
    protected void doAdd(int columnNumber, int rowNumber, Fragment fragment) throws InvalidLayoutLocationException, LayoutEventException
    {
        SortedMap column = getColumnMap(columnNumber);
    
        Integer rowInteger = new Integer(rowNumber);
        LayoutCoordinate targetCoordinate = new LayoutCoordinate(columnNumber, rowNumber);
        if (column.containsKey(rowInteger))
        {
            // If the row has something in it, push everythin down 1
            Fragment existingFragment = (Fragment) column.get(rowInteger);
            column.put(rowInteger, fragment);
            coordinates.put(fragment, targetCoordinate);
            doAdd(columnNumber, ++rowNumber, existingFragment);
            
            LayoutCoordinate oneDownCoordinate = new LayoutCoordinate(targetCoordinate.getX(), targetCoordinate.getY() + 1);
            processEvent(new LayoutEvent(LayoutEvent.MOVED_DOWN, existingFragment, targetCoordinate, oneDownCoordinate));
        }
        else
        {
            column.put(rowInteger, fragment);
            coordinates.put(fragment, targetCoordinate);
            rowNumber++;
            if(rowNumber > nextRowNumber[columnNumber])
            {
                nextRowNumber[columnNumber] = rowNumber;
            }
        }
    
    }

    /**
     * 
     * @param columnNumber
     * @return
     * @throws InvalidLayoutLocationException
     */
    protected final SortedMap getColumnMap(int columnNumber) throws InvalidLayoutLocationException
    {
        Integer columnNumberIneteger = new Integer(columnNumber);

        if (columns.containsKey(columnNumberIneteger))
        {
            return ((SortedMap) columns.get(columnNumberIneteger));
        }
        else
        {
            throw new InvalidLayoutLocationException(columnNumber);
        }

    }

    /**
     * 
     * @param currentColumn
     * @param fragment
     * @return
     */
    protected final int getRow(int currentColumn, Fragment fragment)
    {
        String propertyValue = fragment.getPropertyValue(layoutType, ROW);

        if (propertyValue != null)
        {
            return Integer.parseInt(propertyValue);
        }
        else
        {
            return nextRowNumber[currentColumn];
        }

    }

    /**
     * 
     * @param fragment
     * @return
     */
    protected final int getColumn(Fragment fragment)
    {
        String propertyValue = fragment.getPropertyValue(layoutType, COLUMN);
        if (propertyValue != null)
        {
            int columnNumber = Integer.parseInt(propertyValue);

            // Exceeded columns get put into the last column
            if (columnNumber >= numberOfColumns)
            {
                columnNumber = (numberOfColumns - 1);
            }
            // Columns less than 1 go in the first column
            else if (columnNumber < 0)
            {
                columnNumber = 0;
            }

            return columnNumber;
        }
        else
        {
            return (numberOfColumns - 1);
        }
    }
    
    protected final void processEvent(LayoutEvent event) throws LayoutEventException
    {
        Iterator itr = eventListeners.iterator();
        while(itr.hasNext())
        {
            LayoutEventListener eventListener = (LayoutEventListener) itr.next();
            eventListener.handleEvent(event);
        }
        
    }

}
