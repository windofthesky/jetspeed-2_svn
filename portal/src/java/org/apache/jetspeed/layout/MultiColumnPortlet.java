/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.layout;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Collections;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Property;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    public void init(PortletConfig config)
    throws PortletException
    {
        super.init(config);
        this.numColumns = Integer.parseInt(config.getInitParameter(PARAM_NUM_COLUMN));
        this.colSizes = config.getInitParameter(PARAM_COLUMN_SIZES);
        this.portletName = config.getPortletName();
    }

    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        List[] columns = buildColumns(getFragment(request), this.numColumns);

        request.setAttribute("columns", columns);

        // now invoke the JSP associated with this portlet
        super.doView(request,response);

        request.removeAttribute("columns");
    }

    protected List[] buildColumns(Fragment f, int colNum)
    {
        // normalize the constraints and calculate max num of rows needed
        Iterator iterator = f.getFragments().iterator();
        int row = 0;
        int col = 0;
        int rowNum = 0;

        while (iterator.hasNext())
        {
            Fragment fChild = (Fragment) iterator.next();
            List properties = fChild.getProperties(this.portletName);

            if (properties != null)
            {
                Iterator pIterator = properties.iterator();

                while(pIterator.hasNext())
                {
                    Property prop = (Property)pIterator.next();

                    try
                    {
                        if (prop.getName().equals("row"))
                        {
                            row = Integer.parseInt(prop.getValue());
                            if (row > rowNum)
                            {
                                rowNum = row;
                            }
                        }
                        else if (prop.getName().equals("column"))
                        {
                            col = Integer.parseInt(prop.getValue());
                            if (col > colNum)
                            {
                                prop.setValue(String.valueOf(col % colNum));
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        //ignore any malformed layout properties
                    }
                }
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
            addElement((Fragment)f.getFragments().get(i), table, work, colNum);
        }

        //insert the unconstrained elements in the table
        Iterator i = work.iterator();
        for (row = 0; row < rowNum; row++)
        {
            for (col = 0; i.hasNext() && (col < colNum); col++)
            {
                if (table[col].get(row) == null)
                {
                    table[col].set(row, i.next());
                }
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

        return table;
    }

    /** Parses the size config info and returns a list of
     *  size values for the current set
     *
     *  @param sizeList java.lang.String a comma separated string a values
     *  @return a List of values
     */
    protected static List getCellSizes(String sizeList)
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

    protected static List getCellClasses(String classlist)
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
     * Add an element to the "table" or "work" objects.  If the element is
     * unconstrained, and the position is within the number of columns, then
     * the element is added to "table".  Othewise the element is added to "work"
     *
     * @param f fragment to add
     * @param table of positioned elements
     * @param work list of un-positioned elements
     * @param columnCount Number of colum
     */
    protected void addElement(Fragment f, List[] table, List work, int columnCount)
    {
        int row = -1;
        int col = -1;

        List properties = f.getProperties(this.portletName);

        if (properties != null)
        {
            Iterator pIterator = properties.iterator();

            while(pIterator.hasNext())
            {
                Property prop = (Property)pIterator.next();

                try
                {
                    if (prop.getName().equals("row"))
                    {
                        row = Integer.parseInt(prop.getValue());
                    }
                    else if (prop.getName().equals("column"))
                    {
                        col = Integer.parseInt(prop.getValue());
                    }
                }
                catch (Exception e)
                {
                    //ignore any malformed layout properties
                }
            }
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
}