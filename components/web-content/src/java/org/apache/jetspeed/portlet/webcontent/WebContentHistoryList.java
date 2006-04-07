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

package org.apache.jetspeed.portlet.webcontent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * A history of content navigations in the WebContentPortlet
 *
 * @author <a href="mailto:dyoung@phase2systems.com">David L Young</a>
 * @version $Id: $ 
 */

public class WebContentHistoryList extends Object
    implements Serializable
{
    int maxLength;
    List history;
    int currentIndex;
    
    // Constructors

    public WebContentHistoryList()
    {
        this( -1 );
    }
    public WebContentHistoryList( int maxLength )
    {
        super();
        
        this.maxLength = maxLength;
        this.history = new ArrayList();
        this.currentIndex = -1;
    }
    
    // Methods
    
    public boolean isEmpty()
    {
        return this.history.isEmpty();
    }
    public boolean hasCurrentPage()
    {
        return this.currentIndex >= 0;
    }
    public boolean hasPreviousPage()
    {
        return !isEmpty() && this.currentIndex-1 >= 0;
    }
    public boolean hasNextPage()
    {
        return !isEmpty() && this.currentIndex+1 < this.history.size();
    }
    
    public WebContentHistoryPage getCurrentPage()
    {
        if (!hasCurrentPage())
            return null ;
        return (WebContentHistoryPage)this.history.get(this.currentIndex);
    }
    public WebContentHistoryPage getPreviousPage()
    {
        if (!hasPreviousPage())
            return null;
        this.currentIndex = this.currentIndex-1;
        return getCurrentPage();
    }
    public WebContentHistoryPage getNextPage()
    {
        if (!hasNextPage())
            return null;
        this.currentIndex = this.currentIndex+1;
        return getCurrentPage();
    }
    
    public void visitPage(WebContentHistoryPage page)
    {
        if (page==null)
            throw new IllegalArgumentException("WebContentHistoryList.addPage() - non-null page required.");
        
        int i = this.history.indexOf(page);
        if (i >= 0 && i == this.currentIndex) 
        {
            // just visiting the current page
            return;
        }
        
        // otherwise - new page...
        while (hasNextPage())
        {
            // ...visiting a page discards any pages we have visited by going "back"
            this.history.remove(this.currentIndex+1);
        }
        if (i >= 0 && i < history.size())
        {
            // ...actually, new visit to an old page, only keep one reference to it
            this.history.remove(i);
        }
        
        // add in the new page, at the end
        this.history.add(page);
        this.currentIndex = this.history.size()-1;
        
        // System.out.println("WebContentHistoryList.visitPage() - current index is: "+this.currentIndex+"\nhistory list..."+ArrayUtils.toString(this.history));
    }
}
