/*
 * Copyright 2004 The Apache Software Foundation.
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderMetaData;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 * <p>
 * FolderMetaDataImpl
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class FolderMetaDataImpl implements FolderMetaData
{

    private Folder folder;
    private Document metaDataDoc;
    private Locale locale;
    private static final XPath defaultTitleXPath;
    private Map titles;

    static
    {
        try
        {
            defaultTitleXPath = XPath.newInstance("/js:folder/dc:title[not(@xml:lang)]");
            defaultTitleXPath.addNamespace("js", "http://portals.apache.org/jetspeed");
            defaultTitleXPath.addNamespace("dc", "http://www.purl.org/dc");

        }
        catch (JDOMException e)
        {
            throw new IllegalArgumentException("Invalid XPath for default title.  " + e.toString());
        }
    }

    public FolderMetaDataImpl( Folder folder, File directory) throws IOException
    {
        this.folder = folder;        
        this.titles = new HashMap();
        File metaDataFile = new File(directory, "folder.metadata");
        if (metaDataFile.exists())
        {
            SAXBuilder builder = new SAXBuilder();
            try
            {
                metaDataDoc = builder.build(metaDataFile);
            }
            catch (JDOMException e)
            {
                throw new IllegalStateException("Unable to read folder meta data: " + e.toString());
            }
        }

    }

    /**
     * <p>
     * getTitle
     * </p>
     * 
     * @see org.apache.jetspeed.om.folder.FolderMetaData#getTitle()
     * @return
     */
    public String getTitle(Locale locale)
    {
        if (titles.containsKey(locale))
        {
            return (String) titles.get(locale);
        }

        try
        {
            if (metaDataDoc != null)
            {

                XPath titleXpath = createLocalizedTitleXPath(locale);
                List nodes = titleXpath.selectNodes(metaDataDoc);
                Element titleElement = (Element) titleXpath.selectSingleNode(metaDataDoc);
                if (titleElement != null)
                {
                    titles.put(locale, titleElement.getTextTrim());
                }

                if (titleElement == null)
                {
                    titleElement = (Element) defaultTitleXPath.selectSingleNode(metaDataDoc);

                    if (titleElement != null)
                    {
                        titles.put(locale, titleElement.getTextTrim());
                    }
                    else
                    {
                        titles.put(locale, folder.getName());
                    }
                }
            }
            else
            {
                titles.put(locale, folder.getName());
            }

            return (String) titles.get(locale);
        }
        catch (JDOMException e)
        {
            throw new IllegalArgumentException("Invalid XPath query for title:  " + e.toString());
        }

    }
    
    public String getTitle()
    {
        return getTitle(Locale.getDefault());
    }

    /**
     * @param title
     *            The title to set.
     */
    void setTitle( String title )
    {

    }

    protected XPath createLocalizedTitleXPath( Locale locale ) throws JDOMException
    {
        XPath titleXpath;
        String xpathString = "/js:folder/dc:title[@xml:lang='" + locale.getLanguage() + "']";

        titleXpath = XPath.newInstance(xpathString);
        titleXpath.addNamespace(Namespace.XML_NAMESPACE);
        titleXpath.addNamespace("js", "http://portals.apache.org/jetspeed");
        titleXpath.addNamespace("dc", "http://www.purl.org/dc");
        
        return titleXpath;
    }
}