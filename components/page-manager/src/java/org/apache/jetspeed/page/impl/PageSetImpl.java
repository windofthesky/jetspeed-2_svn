/*
 * Created on Jul 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.page.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSet;

/**
 * <p>
 * PageSetImpl
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PageSetImpl implements PageSet
{
    
    
    private Map pages = new TreeMap();
    private Folder forFolder;
    
    public PageSetImpl(Folder forFolder)
    {
        this.forFolder = forFolder;
    }

    /**
     * 
     * <p>
     * get
     * </p>
     *
     * @see org.apache.jetspeed.om.page.PageSet#get(java.lang.String)
     * @param name
     * @return
     */
    public Page get( String name )
    {    
        if(!name.startsWith(forFolder.getName()))
        {
            name = forFolder.getName()+"/"+name;
        }
        return (Page) pages.get(name);
    }

    /**
     * <p>
     * addPage
     * </p>
     *
     * @see org.apache.jetspeed.om.page.PageSet#addPage()
     * 
     */
    public void add(Page page)
    {      
        page.setParent(forFolder);
        pages.put(page.getId(), page);        
    }

    /**
     * <p>
     * size
     * </p>
     *
     * @see org.apache.jetspeed.om.page.PageSet#size()
     * @return
     */
    public int size()
    {       
        return pages.size();
    }
    /**
     * <p>
     * iterator
     * </p>
     *
     * @see org.apache.jetspeed.om.page.PageSet#iterator()
     * @return
     */
    public Iterator iterator()
    {        
        return pages.values().iterator();
    }
    
    

}
