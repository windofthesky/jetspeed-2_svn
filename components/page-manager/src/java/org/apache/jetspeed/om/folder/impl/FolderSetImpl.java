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
package org.apache.jetspeed.om.folder.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderSet;

/**
 * <p>
 * FolderSetImpl
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class FolderSetImpl implements FolderSet
{
    
    private Map folders = new TreeMap();
    private Folder forFolder;
    
    public FolderSetImpl(Folder forFolder)
    {
        this.forFolder = forFolder;
    }

    /**
     * <p>
     * get
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.FolderSet#get(java.lang.String)
     * @param folderName
     * @return
     */
    public Folder get( String folderName )
    {   
        if(!folderName.startsWith(forFolder.getName()))
        {
            folderName = forFolder.getName()+"/"+folderName;
        }
        return (Folder) folders.get(folderName);
    }

    /**
     * <p>
     * add
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.FolderSet#add(org.apache.jetspeed.om.folder.Folder)
     * @param folder
     */
    public void add( Folder folder )
    {
       folder.setParent(forFolder);
       folders.put(folder.getName(), folder);

    }

    /**
     * <p>
     * iterator
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.FolderSet#iterator()
     * @return
     */
    public Iterator iterator()
    {
        return folders.values().iterator();
    }

    /**
     * <p>
     * size
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.FolderSet#size()
     * @return
     */
    public int size()
    {
        return folders.size();
    }

}
