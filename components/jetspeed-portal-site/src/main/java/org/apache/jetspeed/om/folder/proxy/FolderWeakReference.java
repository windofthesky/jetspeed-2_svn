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
package org.apache.jetspeed.om.folder.proxy;

import java.lang.ref.WeakReference;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.NodeException;

/**
 * This class references PSML Folder instances weakly so that
 * site views do not hold onto instances that would otherwise
 * be reaped from the heap.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class FolderWeakReference
{
    private PageManager pageManager;
    private String path;
    private volatile WeakReference<Folder> referentFolder;
    
    /**
     * Construct folder reference capturing page manager.
     * 
     * @param pageManager
     * @param folder
     */
    public FolderWeakReference(PageManager pageManager, Folder folder)
    {
        this.pageManager = pageManager;
        this.path = folder.getPath();
        this.referentFolder = new WeakReference<Folder>(folder);
    }
    
    /**
     * Get or retrieve referent folder.
     * 
     * @return folder
     */
    public Folder getFolder()
    {
        Folder folder = referentFolder.get();
        if ((folder != null) && !folder.isStale())
        {
            return folder;
        }
        else
        {
            try
            {
                referentFolder = new WeakReference<Folder>(pageManager.getFolder(path));
                return referentFolder.get();
            }
            catch (FolderNotFoundException fnfe)
            {
                throw new RuntimeException("Folder "+path+" has been removed: "+fnfe, fnfe);
            }
            catch (NodeException ne)
            {
                throw new RuntimeException("Folder "+path+" can not be accessed: "+ne, ne);
            }
        }
    }
}
