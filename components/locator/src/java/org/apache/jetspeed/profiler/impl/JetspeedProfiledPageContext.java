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
package org.apache.jetspeed.profiler.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.jetspeed.om.folder.DocumentSet;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.profiler.ProfiledPageContext;
import org.apache.jetspeed.profiler.Profiler;

/**
 * JetspeedProfiledPageContext
 *
 * @author <a href="mailto:rwatler@finali.com">Randy Watler</a>
 * @version $Id$
 */
public class JetspeedProfiledPageContext implements ProfiledPageContext
{    
    private Map locators;
    private Page page;
    private Folder folder;
    private NodeSet siblingPages;
    private Folder parentFolder;
    private NodeSet siblingFolders;
    private NodeSet rootLinks;
    private List documentSetNames;
    private Map documentSets;

    public void init(Profiler profiler, Map locators)
    {
        // save profiled context supplied by profiler
        this.locators = locators;
    }

    public Map getLocators()
    {
        return locators;
    }

    public Page getPage()
    {
        return page;
    }

    public void setPage(Page page)
    {
        this.page = page;
    }

    public Folder getFolder()
    {
        return folder;
    }

    public void setFolder(Folder folder)
    {
        this.folder = folder;
    }

    public NodeSet getSiblingPages()
    {
        return siblingPages;
    }

    public void setSiblingPages(NodeSet pages)
    {
        this.siblingPages = pages;
    }

    public Folder getParentFolder()
    {
        return parentFolder;
    }

    public void setParentFolder(Folder folder)
    {
        this.parentFolder = folder;
    }

    public NodeSet getSiblingFolders()
    {
        return siblingFolders;
    }

    public void setSiblingFolders(NodeSet folders)
    {
        this.siblingFolders = folders;
    }

    public NodeSet getRootLinks()
    {
        return rootLinks;
    }

    public void setRootLinks(NodeSet links)
    {
        this.rootLinks = links;
    }

    public DocumentSet getDocumentSet(String name)
    {
        if ((documentSets == null) || (name == null))
        {
            return null;
        }
        DocumentSetEntry entry = (DocumentSetEntry) documentSets.get(name);
        return (entry != null ? entry.documentSet : null);
    }

    public NodeSet getDocumentSetNodes(String name)
    {
        if ((documentSets == null) || (name == null))
        {
            return null;
        }
        DocumentSetEntry entry = (DocumentSetEntry) documentSets.get(name);
        return (entry != null ? entry.nodes : null);
    }

    public Iterator getDocumentSetNames()
    {
        if (documentSetNames == null)
        {
            return null;
        }
        return documentSetNames.iterator();
    }

    public void setDocumentSet(String name, DocumentSet documentSet, NodeSet nodes)
    {
        if ((name != null) && (documentSet != null) && (nodes != null))
        {
            if (this.documentSets == null)
            {
                this.documentSetNames = new ArrayList(12);
                this.documentSets = new HashMap(12);
            }
            this.documentSetNames.add(name);
            this.documentSets.put(name, new DocumentSetEntry(documentSet, nodes));
        }
    }

    private class DocumentSetEntry
    {
        DocumentSet documentSet;
        NodeSet nodes;

        DocumentSetEntry(DocumentSet documentSet, NodeSet nodes)
        {
            this.documentSet = documentSet;
            this.nodes = nodes;
        }
    }
}
