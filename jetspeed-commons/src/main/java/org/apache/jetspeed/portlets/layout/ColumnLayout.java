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

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.page.ContentFragment;

/**
 * <h2>Basics</h2>
 * <p>
 * <code>ColumnLayout</code> is the model used to support any 1 to <i>n</i>
 * column-based layout. <code>ColumnLayout</code> is constrained by a number
 * columns that will not be exceeded, even if a fragment specifies a column
 * outside of this constraint. Any fragment exceeded the specified column
 * constraint will be deposited into the right-most column.
 * </p>
 * 
 * <h2>Characteristics:</h2>
 * <ul>
 *   <li>Columns and rows always start at 0.</li>
 *   <li>Unless otherwise noted, assume all Collections returned are immutable.</li>
 *   <li>Unless otherwise noted, assume that no public method will ever return <code>null</code>.</li>
 * </ul>
 *  
 * 
 * <h2>Layout Events</h2>
 * <p>
 * When any move*() method is invoked and a portlet is actually moved (see individual
 * methods for what causes these circumstances), an initial LayoutEvent is dispatched.  
 * This may cause a cascade of LayoutEvents to be fired in turn if the movement of the
 * target fragment cause other fragments to be repositioned.  In this case a LayoutEvent
 * is dispatched for each portlet moved, which in turn may our may not cause another
 * LayoutEvent to be fired. 
 * </p>
 * @see org.apache.jetspeed.portlets.layout.LayoutEvent
 * @see org.apache.jetspeed.portlets.layout.LayoutEventListener
 * @see org.apache.jetspeed.portlets.layout.LayoutCoordinate
 * @see org.apache.jetspeed.om.page.Fragment
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 * 
 */
public class ColumnLayout implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** Percentage widths gutter width */
    private final static double PERCENTAGE_WIDTH_GUTTER = 0.01;

    /** Percentage widths format */
    private final static DecimalFormat PERCENTAGE_WIDTH_FORMAT = new DecimalFormat("0.00'%'", new DecimalFormatSymbols(
                                                                               Locale.ENGLISH));

    /** Constrains the columns for this layout */
    private final int numberOfColumns;
    
    /** SortedMap of Columns (which are also sorted maps */
    private final SortedMap<Integer, SortedMap<Integer, ContentFragment>> columns;
    
    /** Width settings for each column */
    private final String[] columnWidths;
    
    /** Efficient way to always be aware of the next available row in a column */
    private final int[] nextRowNumber;
    
    /** maps Fragments (key) to it's current LayoutCoordinate (value) in this layout */
    private final Map<ContentFragment, LayoutCoordinate> coordinates;
    
    /** All of the LayoutEventListeners registered to this layout */
    private final List<LayoutEventListener> eventListeners;

    private final List<ContentFragment> detachedPortlets;
    
    /**
     * 
     * @param numberOfColumns
     *            the maximum number of columns this layout will have.
     * @param layoutType
     *            this value corresponds to the property settings of the
     *            fragments within your psml. Layout type allows segregation of
     *            property settings based on the type of layout in use. This
     *            effectively allows for the interchange of multiple layout
     *            formats without one format effecting the settings of another.
     * @param columnWidths
     *            widths for each column that accumulate to 100% if percentages
     *            are used.
     * @see org.apache.jetspeed.om.page.Fragment#getType()
     */
    public ColumnLayout(int numberOfColumns, String layoutType, String[] columnWidths)
    {
        this.numberOfColumns = numberOfColumns;
        this.columnWidths = columnWidths;
        eventListeners = new ArrayList<LayoutEventListener>();

        columns = new TreeMap<Integer, SortedMap<Integer, ContentFragment>>();
        coordinates = new HashMap<ContentFragment, LayoutCoordinate>();
        detachedPortlets = new ArrayList<ContentFragment>();
        
        for (int i = 0; i < numberOfColumns; i++)
        {
            columns.put(new Integer(i), new TreeMap<Integer, ContentFragment>());
        }

        nextRowNumber = new int[numberOfColumns];

        for (int i = 0; i < numberOfColumns; i++)
        {
            nextRowNumber[i] = 0;
        }
    }
    
    /**
     * Same as ColumnLayout(int numberOfColumns, String layoutType) but also
     * supplies a Collection of fragments to initially populate the layout
     * with.  Adding these fragments <strong>WILL NOT</strong> cause
     * a LayoutEvent to be dispatched.
     * 
     * @see ColumnLayout(int numberOfColumns, String layoutType)
     * @param numberOfColumns
     *            the maximum number of columns this layout will have.
     * @param layoutType
     *            this value corresponds to the property settings of the
     *            fragments within your psml. Layout type allows segregation of
     *            property settings based on the type of layout in use. This
     *            effectively allows for the interchange of multiple layout
     *            formats without one format effecting the settings of another.
     * @param fragments Initial set of fragments to add to this layout.
     * @param columnWidths
     *            widths for each column that accumulate to 100% if percentages
     *            are used.
     * @throws LayoutEventException
     */
    public ColumnLayout(int numberOfColumns, String layoutType, Collection<ContentFragment> fragments, String[] columnWidths, ContentFragment maximized) throws LayoutEventException
    {
        this(numberOfColumns, layoutType, columnWidths);
        Iterator<ContentFragment> fragmentsItr = fragments.iterator();
        try
        {
            if (maximized != null)
            {
                doAdd(getColumn(maximized), getRow(getColumn(maximized), maximized), maximized);
                while (fragmentsItr.hasNext())
                {
                    ContentFragment fragment = (ContentFragment) fragmentsItr.next();
                    String windowState = fragment.getState();
                    if (windowState != null && windowState.equals(JetspeedActions.DETACH))
                    {
                        detachedPortlets.add(fragment);
                    }
                }
                return;
            }
            while (fragmentsItr.hasNext())
            {
                ContentFragment fragment = (ContentFragment) fragmentsItr.next();
                String windowState = fragment.getState();
                if (windowState != null && windowState.equals(JetspeedActions.DETACH))
                {                    
                    detachedPortlets.add(fragment);
                    continue;
                }
                //else if (nvp.isDecoratorRendered())
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
    
    public ColumnLayout(int numberOfColumns, String layoutType, Collection<ContentFragment> fragments, String[] columnWidths) throws LayoutEventException
    {
        this(numberOfColumns, layoutType, fragments, columnWidths, null);
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
     * @see org.apache.jetspeed.om.page.Fragment
     * 
     */
    public void addFragment(ContentFragment fragment) throws LayoutEventException
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
    
    /**
     * Adds a LayoutEventListener to this layout that will be fired any time
     * a LayoutEvent is dispatched.
     * 
     * @param eventListener
     * @see LayoutEventListener
     * @see LayoutEventListener
     */
    public void addLayoutEventListener(LayoutEventListener eventListener)
    {
        eventListeners.add(eventListener);
    }

    /**
     * 
     * @param columnNumber
     *            Number of column to retreive
     * @return requested column (as a immutable Collection). Never returns
     *         <code>null.</code>
     * @throws InvalidLayoutLocationException
     *             if the column is outisde of the constraints of this layout
     */
    public Collection<ContentFragment> getColumn(int columnNumber) throws InvalidLayoutLocationException
    {
        return Collections.unmodifiableCollection(getColumnMap(columnNumber).values());
    }

    /**
     * returns the width to be used with the specified column.  If
     * there is no specific column setting sfor the specified column
     * 0 is returned.
     * 
     * @param columnNumber whose width has been requested.
     * @return the width to be used with the specified column.  Or 0 if no value
     * has been specified.
     */
    public String getColumnWidth(int columnNumber)
    {
        if ((columnWidths != null) && (columnNumber < numberOfColumns))
        {
            String columnWidth = columnWidths[columnNumber];

            // subtract "gutter" width from last percentage
            // column to prevent wrapping on rounding errors
            // of column widths when rendered in the browser
            if ((numberOfColumns > 1) && (columnNumber == (numberOfColumns - 1)))
            {
                int percentIndex = columnWidth.lastIndexOf('%');
                if (percentIndex > 0)
                {
                    try
                    {
                        double width = Double.parseDouble(columnWidth.substring(0,percentIndex).trim());
                        synchronized (PERCENTAGE_WIDTH_FORMAT)
                        {
                            columnWidth = PERCENTAGE_WIDTH_FORMAT.format(width - PERCENTAGE_WIDTH_GUTTER);
                        }
                    }
                    catch (NumberFormatException nfe)
                    {
                    }
                }
            }
            return columnWidth;
        }
        return "0";
    }
    
    /**
     * returns the float to be used with the specified column.
     * 
     * @param columnNumber whose width has been requested.
     * @return "right" for the last column, "left" if more than one
     *         column, or "none" otherwise.
     */
    public String getColumnFloat(int columnNumber)
    {
        if ((numberOfColumns > 1) && (columnNumber < numberOfColumns))
        {
            if (columnNumber == (numberOfColumns - 1))
            {
                return "right";
            }
            else
            {
                return "left";
            }
        }
        return "none";
    }
    
    /**
     * @return <code>java.util.Collection</code> all of columns (also
     *         Collection objects) in order within this layout. All Collections
     *         are immutable.
     */
    public Collection<Collection<ContentFragment>> getColumns()
    {
        ArrayList<Collection<ContentFragment>> columnList = new ArrayList<Collection<ContentFragment>>(getNumberOfColumns());
        for (SortedMap<Integer, ContentFragment> map : columns.values())
        {
            columnList.add(Collections.unmodifiableCollection(map.values()));
        }
        return Collections.unmodifiableCollection(columnList);
    }
    
    /**
     * 
     * Returns the index of the last row in the specified column.
     * 
     * @param columnNumber column form whom we ant to identify the
     * last row.
     * @return the index of the last row in the specified column.
     */
    public int getLastRowNumber(int columnNumber)
    {
        return nextRowNumber[columnNumber] - 1;
    }
    
    /**
     * Returns an immutable Collection of all the Fragments contained within
     * this ColumnLayout in no sepcific order.
     * @return Immutable Collection of Fragments.
     */
    public Collection<ContentFragment> getFragments()
    {
        return Collections.unmodifiableCollection(coordinates.keySet());
    }

    /**
     * Retrieves the fragment at the specified loaction.
     * 
     * @param columnNumber Column coordinate (first column starts at 0)
     * @param rowNumber Row coordinate (first row starts at 0)
     * @return Fragment at the specified coordinate.  Never returns <code>null</code>.
     * @throws EmptyLayoutLocationException if there is no fragment currently located at the specified coordinate.
     * @throws InvalidLayoutLocationException if the coordinate lies outside the confines of this layout, i.e., the
     * <code>columnNumber</code> exceeds the max columns setting for this layout.
     */
    public ContentFragment getFragmentAt(int columnNumber, int rowNumber) throws EmptyLayoutLocationException,
            InvalidLayoutLocationException
    {
        SortedMap<Integer, ContentFragment> column = getColumnMap(columnNumber);
        Integer rowInteger = new Integer(rowNumber);
        if (column.containsKey(rowInteger))
        {
            return (ContentFragment) column.get(rowInteger);
        }
        else
        {
            throw new EmptyLayoutLocationException(columnNumber, rowNumber);
        }
    }
    
    /**
     * 
     * Retrieves the fragment at the specified loaction.
     * 
     * @param coodinate LayoutCoordinate object that will be used to located a fragment in this
     * layout.
     * @see LayoutCoordinate
     * @return Fragment at the specified coordinate.  Never returns <code>null</code>.
     * @throws EmptyLayoutLocationException if there is no fragment currently located at the specified coordinate.
     * @throws InvalidLayoutLocationException if the coordinate lies outside the confines of this layout, i.e., the
     * <code>columnNumber</code> exceeds the max columns setting for this layout.
     * @see LayoutCoordinate
     */
    public ContentFragment getFragmentAt(LayoutCoordinate coodinate) throws EmptyLayoutLocationException,
            InvalidLayoutLocationException
    {
        return getFragmentAt(coodinate.getX(), coodinate.getY());
    }

    /**
     * 
     * @return The total number of columns in this layout.
     */
    public int getNumberOfColumns()
    {
        return numberOfColumns;
    }

    /**
     * 
     * @return The last column in this layout.  The Collection is immutable.
     */
    public Collection<ContentFragment> getLastColumn() 
    {
        try
        {
            return Collections.unmodifiableCollection(getColumnMap(numberOfColumns - 1).values());
        }
        catch (InvalidLayoutLocationException e)
        {
            // This should NEVER happen as getLastColumn() is
            // always correctly constrained and should always exists.
            throw new LayoutError("It appears this layout is corrupt and cannot correctly identify its last column.", e);
        }
    }

    /**
     * 
     * @return The last column in this layout.  The Collection is immutable.
     */
    public Collection<ContentFragment> getFirstColumn()
    {
        try
        {
            return Collections.unmodifiableCollection(getColumnMap(0).values());
        }
        catch (InvalidLayoutLocationException e)
        {
            // This should NEVER happen as getLastColumn() is
            // always correctly constrained and should always exists.
            throw new LayoutError("It appears this layout is corrupt and cannot correctly identify its first column.", e);
        }
    }

    /**
     * 
     * Moves a fragment one column to the right.  A LayoutEvent is triggered by
     * this action.
     * 
     * <p>
     * If the fragment currently
     * resides in right-most column, no action is taking and no event LayoutEvent
     * is fired.
     * </p>
     * 
     * @param fragment fragment to move.
     * @throws FragmentNotInLayoutException if the specified fragment is not currently in the layout.
     * @throws LayoutEventException If a triggered LayoutEvent fails.
     */
    public void moveRight(ContentFragment fragment) throws FragmentNotInLayoutException, LayoutEventException
    {
        LayoutCoordinate coordinate = getCoordinate(fragment);
        LayoutCoordinate newCoordinate = new LayoutCoordinate(coordinate.getX() + 1, coordinate.getY());

        if (newCoordinate.getX() < numberOfColumns)
        {

            try
            {
                doMove(fragment, coordinate, newCoordinate);
                processEvent(new LayoutEvent(LayoutEvent.MOVED_RIGHT, fragment, coordinate, newCoordinate));
                // now move the fragment below up one level.
                try
                {
                    ContentFragment fragmentBelow = getFragmentAt(new LayoutCoordinate(coordinate.getX(), coordinate.getY() + 1));
                    moveUp(fragmentBelow);
                }
                catch (EmptyLayoutLocationException e)
                {
                    // indicates no fragment below
                }
            }
            catch (InvalidLayoutLocationException e)
            {
                // This should NEVER happen as the location has already been verified to be valid
                throw new LayoutError("It appears this layout is corrupt and cannot correctly identify valid column locations.", e);
            }      
        }
    }

    /**
     * Moves a fragment one column to the left.  A LayoutEvent is triggered by
     * this action.
     * 
     * <p>
     * If the fragment currently
     * resides in left-most column, no action is taking and no event LayoutEvent
     * is fired.
     * </p>
     * 
     * @param fragment
     * @throws FragmentNotInLayoutException if the specified fragment is not currently in the layout.
     * @throws LayoutEventException If a triggered LayoutEvent fails.
     */
    public void moveLeft(ContentFragment fragment) throws FragmentNotInLayoutException, LayoutEventException
    {
        LayoutCoordinate coordinate = getCoordinate(fragment);
        LayoutCoordinate newCoordinate = new LayoutCoordinate(coordinate.getX() - 1, coordinate.getY());

        if (newCoordinate.getX() >= 0)
        {
            try
            {
                doMove(fragment, coordinate, newCoordinate);
                processEvent(new LayoutEvent(LayoutEvent.MOVED_LEFT, fragment, coordinate, newCoordinate));
                // now move the fragment below up one level.
                try
                {
                    ContentFragment fragmentBelow = getFragmentAt(new LayoutCoordinate(coordinate.getX(), coordinate.getY() + 1));
                    moveUp(fragmentBelow);
                }
                catch (EmptyLayoutLocationException e)
                {
                    // indicates no fragment below
                }
            }
            catch (InvalidLayoutLocationException e)
            {
                // This should NEVER happen as the location has already been verified to be valid
                throw new LayoutError("It appears this layout is corrupt and cannot correctly identify valid column locations.", e);
            }
         
        }

    }

    /**
     * Moves a fragment one row to the up.  A LayoutEvent is triggered by
     * this action.
     * 
     * <p>
     * If the fragment currently
     * resides in top-most row, no action is taking and no event LayoutEvent
     * is fired.
     * </p>
     * @param fragment
     * @throws FragmentNotInLayoutException if the specified fragment is not currently in the layout.
     * @throws LayoutEventException If a triggered LayoutEvent fails.
     */
    public void moveUp(ContentFragment fragment) throws FragmentNotInLayoutException, LayoutEventException
    {
        LayoutCoordinate coordinate = getCoordinate(fragment);
        LayoutCoordinate aboveLayoutCoordinate = new LayoutCoordinate(coordinate.getX(), coordinate.getY() - 1);
        LayoutCoordinate newCoordinate = aboveLayoutCoordinate;

        // never go "above" 0.
        if (newCoordinate.getY() >= 0)
        {
            try
            {
                try
                {
                    // now move the fragment above down one level.
                    /*Fragment fragmentAbove =*/ getFragmentAt(aboveLayoutCoordinate);
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
                        ContentFragment fragmentBelow = getFragmentAt(new LayoutCoordinate(coordinate.getX(),
                                coordinate.getY() + 1));
                        moveUp(fragmentBelow);
                    }
                    catch (EmptyLayoutLocationException e1)
                    {

                    }
                }
            }
            catch (InvalidLayoutLocationException e)
            {
                // This should NEVER happen as the location has already been verfied to be valid
                throw new LayoutError("It appears this layout is corrupt and cannot correctly identify valid column locations.", e);
            }
        }
    }

    /**
     * 
     * @param fragment
     * @throws FragmentNotInLayoutException if the specified fragment is not currently in the layout.
     * @throws LayoutEventException If a triggered LayoutEvent fails.
     */
    public void moveDown(ContentFragment fragment) throws FragmentNotInLayoutException, LayoutEventException
    {
        LayoutCoordinate coordinate = getCoordinate(fragment);
        LayoutCoordinate newCoordinate = new LayoutCoordinate(coordinate.getX(), coordinate.getY() + 1);

        // never move past the current bottom row
        if (newCoordinate.getY() < nextRowNumber[coordinate.getX()])
        {
            try
            {
                try
                {
                    // the best approach to move a fragment down is to actually move
                    // its neighbor underneath up
                    LayoutCoordinate aboveCoord = new LayoutCoordinate(coordinate.getX(), coordinate.getY() + 1);
                    ContentFragment fragmentBelow = getFragmentAt(aboveCoord);
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
            catch (InvalidLayoutLocationException e)
            {
                // This should NEVER happen as the location has already been verfied to be valid
                throw new LayoutError("It appears this layout is corrupt and cannot correctly identify valid column locations.", e);
            }
 
        }
    }

    /**
     * Performs the actual movement of a fragment.
     * 
     * 
     * @param fragment
     * @param oldCoordinate
     * @param newCoordinate
     * @throws InvalidLayoutLocationException
     * @throws LayoutEventException 
     */
    protected void doMove(ContentFragment fragment, LayoutCoordinate oldCoordinate, LayoutCoordinate newCoordinate)
            throws InvalidLayoutLocationException, LayoutEventException
    {
        SortedMap<Integer, ContentFragment> oldColumn = getColumnMap(oldCoordinate.getX());
        oldColumn.remove(new Integer(oldCoordinate.getY()));
        coordinates.remove(fragment);

        doAdd(newCoordinate.getX(), newCoordinate.getY(), fragment);
    }

    /**
     *
     * 
     * @param fragment fragment whose LayoutCoordinate we ant.
     * @return LayoutCoordinate representing the current location of this
     * Fragment within this layout.
     * @throws FragmentNotInLayoutException if the Fragment is not present in this layout.
     * @see LayoutCoordinate
     */
    public LayoutCoordinate getCoordinate(ContentFragment fragment) throws FragmentNotInLayoutException
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
     * Adds a fragment at the indicated <code>columnNumber</code>
     * and <code>rowNumber</code>.
     * 
     * @param columnNumber
     * @param rowNumber
     * @param fragment
     * @throws InvalidLayoutLocationException if the coordinates are outside the bounds of this layout.
     * @throws LayoutEventException id a LayoutEvent fails
     */
    protected void doAdd(int columnNumber, int rowNumber, ContentFragment fragment) throws InvalidLayoutLocationException, LayoutEventException
    {
        SortedMap<Integer, ContentFragment> column = getColumnMap(columnNumber);
    
        Integer rowInteger = new Integer(rowNumber);
        LayoutCoordinate targetCoordinate = new LayoutCoordinate(columnNumber, rowNumber);
        if (column.containsKey(rowInteger))
        {
            // If the row has something in it, push everything down 1
            ContentFragment existingFragment = (ContentFragment) column.get(rowInteger);
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
     * Retrieves this specified <code>columnNumber</code> as a
     * SortedMap.
     * 
     * @param columnNumber
     * @return SortedMap<Integer, Fragment>
     * @throws InvalidLayoutLocationException if the <code>columnNumber</code> resides
     * outside the bounds of this layout.
     */
    protected final SortedMap<Integer, ContentFragment> getColumnMap(int columnNumber) throws InvalidLayoutLocationException
    {
        Integer columnNumberInteger = new Integer(columnNumber);

        if (columns.containsKey(columnNumberInteger))
        {
            return columns.get(columnNumberInteger);
        }
        else
        {
            throw new InvalidLayoutLocationException(columnNumber);
        }

    }

    /**
     * Gets the row number of this fragment to looking the <code>layoutType</code>
     * property <i>row</i>.  If this property is undefined, the bottom-most row
     * number of <code>currentColumn</code> is returned.
     * 
     * @param currentColumn
     * @param fragment
     * @return valid row for this fragment within this layout.
     */
    protected final int getRow(int currentColumn, ContentFragment fragment)
    {
        String propertyValue = fragment.getProperty(ContentFragment.ROW_PROPERTY_NAME); 
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
     * Gets the row number of this fragment to looking the <code>layoutType</code>
     * property <i>column</i>. 
     * 
     * If the <i>column</i> is undefined or exceeds the constriants of this
     * layout, the value returned is <code>numberOfColumns - 1</code>.  If the
     * value is less than 0, 0 is returned.
     * 
     * 
     * @param fragment
     * @return
     */
    protected final int getColumn(ContentFragment fragment)
    {
        String propertyValue = fragment.getProperty(ContentFragment.COLUMN_PROPERTY_NAME); 
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
    
    /**
     * Dispatches a LayoutEvent to all LayoutEventListeners registered to this layout.
     * 
     * @param event
     * @throws LayoutEventException if an error occurs while processing a the LayoutEvent.
     */
    protected final void processEvent(LayoutEvent event) throws LayoutEventException
    {
        for (LayoutEventListener eventListener : eventListeners)
        {
            eventListener.handleEvent(event);
        }
        
    }

    public List<ContentFragment> getDetachedPortlets()
    {
        return this.detachedPortlets;
    }

    public void buildDetachedPortletList(Collection<ContentFragment> fragments)
    {
        Iterator<ContentFragment> fragmentsItr = fragments.iterator();
        while (fragmentsItr.hasNext())
        {
            ContentFragment fragment = (ContentFragment) fragmentsItr.next();
            String windowState = fragment.getState();
            if (windowState != null && windowState.equals(JetspeedActions.DETACH))
            {
                detachedPortlets.add(fragment);
                continue;
            }
            //    else if (nvp.isDecoratorRendered())
        }
    }
    
}
