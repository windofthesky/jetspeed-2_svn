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
package org.apache.portals.applications.rss;

import com.sun.syndication.feed.synd.SyndFeed;

/**
 *
 * @author <a href="mailto:christophe.lombart@sword-technologies.com">Lombart Christophe </a>
 * @version $Id$
 */
public class RssInfo
{
    private SyndFeed feed;
    private int itemdisplayed;
    private boolean openinpopup;
    private boolean showdescription;
    private boolean showtitle;
    private boolean showtextinput;
    
    
    
    /**
     * @param feed
     * @param itemdisplayed
     * @param openinpopup
     * @param showdescription
     * @param showtitle
     * @param showtextinput
     */
    public RssInfo(SyndFeed feed, int itemdisplayed, boolean openinpopup, boolean showdescription, boolean showtitle,
            boolean showtextinput)
    {        
        this.feed = feed;
        this.itemdisplayed = itemdisplayed;
        this.openinpopup = openinpopup;
        this.showdescription = showdescription;
        this.showtitle = showtitle;
        this.showtextinput = showtextinput;
    }
    
    public SyndFeed getFeed()
    {
        return feed;
    }
    public void setFeed(SyndFeed feed)
    {
        this.feed = feed;
    }
    public int getItemdisplayed()
    {
        return itemdisplayed;
    }
    public void setItemdisplayed(int itemdisplayed)
    {
        this.itemdisplayed = itemdisplayed;
    }
    public boolean isOpeninpopup()
    {
        return openinpopup;
    }
    public void setOpeninpopup(boolean openinpopup)
    {
        this.openinpopup = openinpopup;
    }
    public boolean isShowdescription()
    {
        return showdescription;
    }
    public void setShowdescription(boolean showdescription)
    {
        this.showdescription = showdescription;
    }
    public boolean isShowtextinput()
    {
        return showtextinput;
    }
    public void setShowtextinput(boolean showtextinput)
    {
        this.showtextinput = showtextinput;
    }
    public boolean isShowtitle()
    {
        return showtitle;
    }
    public void setShowtitle(boolean showtitle)
    {
        this.showtitle = showtitle;
    }
}
