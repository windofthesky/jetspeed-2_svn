/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 */
public class MultiColumnPortlet extends LayoutPortlet
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(MultiColumnPortlet.class);

    protected final static String PARAM_NUM_COLUMN = "columns";
    protected final static int DEFAULT_NUM_COLUMN = 2;
    protected final static String PARAM_COLUMN_SIZES = "sizes";
    protected final static String DEFAULT_COLUMN_SIZES = "50%,50%";

    private int numColumns = 0;
    private String colSizes = null;
    private String portletName = null;
    private String layoutType;

    protected PageManager pm;

    public void init( PortletConfig config ) throws PortletException
    {
        super.init(config);
        this.numColumns = Integer.parseInt(config.getInitParameter(PARAM_NUM_COLUMN));
        this.colSizes = config.getInitParameter(PARAM_COLUMN_SIZES);
        this.portletName = config.getPortletName();
        this.layoutType = config.getInitParameter("layoutType");
        pm = (PageManager) Jetspeed.getComponentManager().getComponent(PageManager.class);
    }

    public void doView( RenderRequest request, RenderResponse response ) throws PortletException, IOException
    {
        RequestContext context = Jetspeed.getCurrentRequestContext();
        PortletWindow window = context.getPortalURL().getNavigationalState().getMaximizedWindow();

        if (request.getParameter("moveBy") != null && request.getParameter("fragmentId") != null)
        {
            Page page = getPage(request);
            Fragment f = getFragment(request, false);
            ArrayList tempFrags = new ArrayList(f.getFragments());
            doMoveFragment(page.getFragmentById(request.getParameter("fragmentId")), request.getParameter("moveBy"),
                           request, tempFrags);

            try
            {
                pm.updatePage(page);
            }
            catch (Exception e)
            {
                throw new PortletException(e.toString(), e);
            }

        }

        // if (targetState != null && targetState.isMaximized())
        if (window != null)
        {
            super.doView(request, response);
            return;
        }

        Fragment f = getFragment(request, false);
        List[] columns = buildColumns(f, this.numColumns, request);

        request.setAttribute("columns", columns);
        request.setAttribute("numberOfColumns", new Integer(numColumns));

        // now invoke the JSP associated with this portlet
        super.doView(request, response);

        request.removeAttribute("columns");
        request.removeAttribute("numberOfColumns");
    }

    protected List[] buildColumns( Fragment f, int colNum, RenderRequest request ) throws PortletException
    {
        // normalize the constraints and calculate max num of rows needed
        Iterator iterator = f.getFragments().iterator();
        int row = 0;
        int col = 0;
        int rowNum = 0;
        int[] lastRowForColumn = new int[numColumns];

        while (iterator.hasNext())
        {
            Fragment fChild = (Fragment) iterator.next();

            try
            {

                row = Integer.parseInt(fChild.getPropertyValue(this.layoutType, "row"));
                if (row > rowNum)
                {
                    rowNum = row;
                }

                if (colNum > 1)
                {
                    col = Integer.parseInt(fChild.getPropertyValue(this.layoutType, "column"));
                    if (col > colNum)
                    {
                        fChild.setPropertyValue(this.layoutType, "column", String.valueOf(col % colNum));
                    }
                }
                else
                {
                    col = 0;
                    fChild.setPropertyValue(this.layoutType, "column", String.valueOf(col));
                }

                if (row > lastRowForColumn[col])
                {
                    lastRowForColumn[col] = row;
                }

            }
            catch (Exception e)
            {
                //ignore any malformed layout properties
            }

        }

        int sCount = f.getFragments().size();
        row = (sCount / colNum) + 1;
        if (row > rowNum)
        {
            rowNum = row;
        }

        // initialize the result position table and the work list
        List[] table = new List[colNum];
        List filler = Collections.nCopies(rowNum + 1, null);
        for (int i = 0; i < colNum; i++)
        {
            table[i] = new ArrayList();
            table[i].addAll(filler);
        }

        List work = new ArrayList();

        //position the constrained elements and keep a reference to the
        //others
        for (int i = 0; i < sCount; i++)
        {
            addElement((Fragment) f.getFragments().get(i), table, work, colNum);
        }

        //insert the unconstrained elements in the table
        Iterator i = work.iterator();
        boolean unconstrainedFound = false;
        for (row = 0; row < rowNum; row++)
        {
            for (col = 0; i.hasNext() && (col < colNum); col++)
            {
                if (table[col].get(row) == null)
                {
                    Fragment ucf = (Fragment) i.next();
                    table[col].set(row, ucf);

                    ucf.setPropertyValue(layoutType, "row", String.valueOf(row));
                    ucf.setPropertyValue(layoutType, "column", String.valueOf(col));
                    unconstrainedFound = true;
                    if (row > lastRowForColumn[col])
                    {
                        lastRowForColumn[col] = row;
                    }

                }
            }
        }

        if (unconstrainedFound)
        {
            try
            {
                pm.updatePage(getPage(request));
            }
            catch (Exception e)
            {
                log.warn("Unable to update Page information: "+e.toString(), e);
            }           
        }

        // now cleanup any remaining null elements
        for (int j = 0; j < table.length; j++)
        {
            i = table[j].iterator();
            while (i.hasNext())
            {
                Object obj = i.next();

                if (obj == null)
                {
                    i.remove();
                }

            }
        }
        
        ArrayList lastRowForColList = new ArrayList(lastRowForColumn.length);
        for(int j=0; j < lastRowForColumn.length; j++)
        {
            lastRowForColList.add(new Integer(lastRowForColumn[j]));
        }
        
        request.setAttribute("lastRowForColumn", lastRowForColList);

        return table;
    }

    /**
     * Parses the size config info and returns a list of size values for the
     * current set
     * 
     * @param sizeList
     *            java.lang.String a comma separated string a values
     * @return a List of values
     */
    protected static List getCellSizes( String sizeList )
    {
        List list = new Vector();

        if (sizeList != null)
        {
            StringTokenizer st = new StringTokenizer(sizeList, ",");
            while (st.hasMoreTokens())
            {
                list.add(st.nextToken());
            }
        }

        return list;
    }

    protected static List getCellClasses( String classlist )
    {
        List list = new Vector();

        if (classlist != null)
        {
            StringTokenizer st = new StringTokenizer(classlist, ",");
            while (st.hasMoreTokens())
            {
                list.add(st.nextToken());
            }
        }

        return list;
    }

    /**
     * Add an element to the "table" or "work" objects. If the element is
     * unconstrained, and the position is within the number of columns, then the
     * element is added to "table". Othewise the element is added to "work"
     * 
     * @param f
     *            fragment to add
     * @param table
     *            of positioned elements
     * @param work
     *            list of un-positioned elements
     * @param columnCount
     *            Number of colum
     */
    protected void addElement( Fragment f, List[] table, List work, int columnCount )
    {
        int row = -1;
        int col = -1;

        try
        {
            row = Integer.parseInt(f.getPropertyValue(this.layoutType, "row"));
            col = Integer.parseInt(f.getPropertyValue(this.layoutType, "column"));
        }
        catch (Exception e)
        {
            //ignore any malformed layout properties
        }

        if ((row >= 0) && (col >= 0) && (col < columnCount))
        {
            table[col].set(row, f);
        }
        else
        {
            work.add(f);
        }
    }

    protected void doMoveFragment( Fragment fToMove, String coordinates, RenderRequest request, List fragments )
    {

        StringTokenizer coorTk = new StringTokenizer(coordinates, ",");
        int x = Integer.parseInt(coorTk.nextToken());
        int y = Integer.parseInt(coorTk.nextToken());
        if (x == 0 && y == 0)
        {
            return;
        }

        String rowValue = fToMove.getPropertyValue(layoutType, "row");
        int row = Integer.parseInt(rowValue);
        int column = Integer.parseInt(fToMove.getPropertyValue(layoutType, "column"));

        int newRow = row + y;
        int newColumn = column + x;
        doMoveFragmentTo(fToMove, newColumn, newRow, request, fragments, true);

    }

    protected void doMoveFragmentTo( Fragment fToMove, int column, int row, RenderRequest request, List fragments,
            boolean firstCall )
    {
        //Wrapping logic
        if (column >= numColumns)
        {
            column = 0;
            row += 1;
            doMoveFragmentTo(fToMove, column, row, request, fragments, false);
            return;

        }
        else if (column < 0)
        {
            row -= 1;
            column = (numColumns - 1);
            doMoveFragmentTo(fToMove, column, row, request, fragments, false);
            return;
        }
        else if (row < 0)
        {
            row = getLastRowInColumn(column, fragments, fToMove) + 1;
            doMoveFragmentTo(fToMove, column, row, request, fragments, false);
            return;
        }
        else
        {

            int currentRow = Integer.parseInt(fToMove.getPropertyValue(layoutType, "row"));
            int currentColumn = Integer.parseInt(fToMove.getPropertyValue(layoutType, "column"));

            int lastRow = getLastRowInColumn(column, fragments, fToMove);

            // Prevent wacky row 999 if there are only 2 rows in the column
            if (row > (lastRow + 1))
            {
                row = lastRow + 1;
            }

            fToMove.setPropertyValue(layoutType, "row", String.valueOf(row));
            fToMove.setPropertyValue(layoutType, "column", String.valueOf(column));

            for (int i = 0; i < fragments.size(); i++)
            {
                Fragment aFragment = (Fragment) fragments.get(i);
                if (!aFragment.equals(fToMove))
                {
                    int aRow = Integer.parseInt(aFragment.getPropertyValue(layoutType, "row"));
                    int aColumn = Integer.parseInt(aFragment.getPropertyValue(layoutType, "column"));
                    if (aColumn == column && aRow == row)
                    {
                        if (currentColumn == column && currentRow < row && firstCall)
                        {
                            doMoveFragmentTo(aFragment, column, (row - 1), request, fragments, false);
                        }
                        else
                        {
                            doMoveFragmentTo(aFragment, column, (row + 1), request, fragments, false);
                        }
                    }
                }

            }
            return;
        }

    }

    protected int getLastRowInColumn( int column, List fragments, Fragment f )
    {
        int row = 0;
        Iterator allFrags = fragments.iterator();
        while (allFrags.hasNext())
        {
            Fragment aFrag = (Fragment) allFrags.next();
            int currentRow = Integer.parseInt(aFrag.getPropertyValue(layoutType, "row"));
            int currentColumn = Integer.parseInt(aFrag.getPropertyValue(layoutType, "column"));
            if (currentRow > row && currentColumn == column && (f == null || !f.equals(aFrag)))
            {
                row = currentRow;
            }
        }
        return row;
    }
}
