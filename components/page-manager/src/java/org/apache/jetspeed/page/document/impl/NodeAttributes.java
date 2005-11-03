/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.jetspeed.page.document.impl;

import java.util.StringTokenizer;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.ojb.broker.query.Criteria;

/**
 * NodeAttributes
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class NodeAttributes
{
    private static final String NONE = "";

    private String path = Folder.PATH_SEPARATOR;
    private String subsite = NONE;
    private String user = NONE;
    private String role = NONE;
    private String group = NONE;
    private String mediatype = NONE;
    private String locale = NONE;
    private String extendedAttributeName = NONE;
    private String extendedAttributeValue = NONE;

    private String canonicalPath;

    /**
     * NodeAttributes
     *
     * Constructor used for Node nested member.
     */
    public NodeAttributes()
    {
    }

    /**
     * NodeAttributes
     *
     * Constructor used to generate Node queries.
     *
     * @param path canonical path
     */
    public NodeAttributes(String path)
    {
        setCanonicalPath(path);
    }

    /**
     * getCanonicalPath
     *
     * Computed canonical path using attributes and base path.
     *
     * @retuns canonical path
     */
    public String getCanonicalPath()
    {
        // compute canonical path
        if (canonicalPath == null)
        {
            // prepend attributes to path
            StringBuffer newPath = new StringBuffer();
            if ((subsite != NONE) && (subsite.length() > 0))
            {
                newPath.append(Folder.PATH_SEPARATOR);
                newPath.append(Folder.RESERVED_SUBSITE_FOLDER_PREFIX);
                newPath.append(subsite.toLowerCase());
            }
            if ((user != NONE) && (user.length() > 0))
            {
                newPath.append(Folder.USER_FOLDER);
                newPath.append(user.toLowerCase());
            }
            if ((role != NONE) && (role.length() > 0))
            {
                newPath.append(Folder.ROLE_FOLDER);
                newPath.append(role.toLowerCase());
            }
            if ((group != NONE) && (group.length() > 0))
            {
                newPath.append(Folder.GROUP_FOLDER);
                newPath.append(group.toLowerCase());
            }
            if ((mediatype != NONE) && (mediatype.length() > 0))
            {
                newPath.append(Folder.MEDIATYPE_FOLDER);
                newPath.append(mediatype.toLowerCase());
            }
            if ((locale != NONE) && (locale.length() > 0))
            {
                newPath.append(Folder.LANGUAGE_FOLDER);
                int localeSeparatorIndex = locale.indexOf('_');
                if (localeSeparatorIndex > 0)
                {
                    newPath.append(locale.substring(0,localeSeparatorIndex).toLowerCase());
                    if (localeSeparatorIndex + 1 < locale.length())
                    {
                        newPath.append(Folder.COUNTRY_FOLDER);
                        newPath.append(locale.substring(localeSeparatorIndex+1).toLowerCase());
                    }
                }
                else
                {
                    newPath.append(locale.toLowerCase());
                }
            }
            if ((extendedAttributeName != NONE) && (extendedAttributeName.length() > 0) &&
                (extendedAttributeValue != NONE) && (extendedAttributeValue.length() > 0))
            {
                newPath.append(Folder.PATH_SEPARATOR);
                newPath.append(Folder.RESERVED_FOLDER_PREFIX);
                newPath.append(extendedAttributeName.toLowerCase());
                newPath.append(Folder.PATH_SEPARATOR);
                newPath.append(extendedAttributeValue.toLowerCase());
            }

            // append base path
            if (!path.equals(Folder.PATH_SEPARATOR))
            {
                newPath.append(path);
            }

            // save canonical path
            if (newPath.length() > 0)
            {
                canonicalPath = newPath.toString();
            }
            else
            {
                canonicalPath = Folder.PATH_SEPARATOR;
            }
        }
        return canonicalPath;
    }
    
    /**
     * setCanonicalPath
     *
     * Parses canonical path setting attributes and base path.
     *
     * @param newPath canonical path
     */
    public void setCanonicalPath(String newPath)
    {
        // cleanup path
        if ((newPath == null) || (newPath.length() == 0))
        {
            newPath = Folder.PATH_SEPARATOR;
        }
        if (!newPath.startsWith(Folder.PATH_SEPARATOR))
        {
            newPath = Folder.PATH_SEPARATOR + newPath;
        }
        if (newPath.endsWith(Folder.PATH_SEPARATOR) && !newPath.equals(Folder.PATH_SEPARATOR))
        {
            newPath = newPath.substring(0, newPath.length() - 1);
        }

        // reset attributes and base path
        path = Folder.PATH_SEPARATOR;
        subsite = NONE;
        user = NONE;
        role = NONE;
        group = NONE;
        mediatype = NONE;
        locale = NONE;
        extendedAttributeName = NONE;
        extendedAttributeValue = NONE;

        // parse attributes and base path from path
        StringBuffer basePath = new StringBuffer();
        String attributeName = null;
        StringTokenizer pathElements = new StringTokenizer(newPath, Folder.PATH_SEPARATOR);
        while (pathElements.hasMoreTokens())
        {
            String pathElement = pathElements.nextToken();
            if (attributeName != null)
            {
                // set last attribute name with attribute value
                if (attributeName.startsWith(Folder.RESERVED_USER_FOLDER_NAME))
                {
                    user = pathElement.toLowerCase();
                }
                else if (attributeName.startsWith(Folder.RESERVED_ROLE_FOLDER_NAME))
                {
                    role = pathElement.toLowerCase();
                }
                else if (attributeName.startsWith(Folder.RESERVED_GROUP_FOLDER_NAME))
                {
                    group = pathElement.toLowerCase();
                }
                else if (attributeName.startsWith(Folder.RESERVED_MEDIATYPE_FOLDER_NAME))
                {
                    mediatype = pathElement.toLowerCase();
                }
                else if (attributeName.startsWith(Folder.RESERVED_LANGUAGE_FOLDER_NAME))
                {
                    if (locale != NONE)
                    {
                        locale = pathElement.toLowerCase() + "_" + locale;
                    }
                    else
                    {
                        locale = pathElement.toLowerCase();
                    }
                }
                else if (attributeName.startsWith(Folder.RESERVED_COUNTRY_FOLDER_NAME))
                {
                    if (locale != NONE)
                    {
                        locale = locale + "_" + pathElement.toLowerCase() ;
                    }
                    else
                    {
                        locale = pathElement.toLowerCase();
                    }
                }
                else if (attributeName.startsWith(Folder.RESERVED_FOLDER_PREFIX))
                {
                    extendedAttributeName = attributeName.substring(Folder.RESERVED_FOLDER_PREFIX.length());
                    extendedAttributeValue = pathElement.toLowerCase();
                }

                // reset attribute name
                attributeName = null;
            }
            else if (pathElement.startsWith(Folder.RESERVED_SUBSITE_FOLDER_PREFIX))
            {
                subsite = pathElement.substring(Folder.RESERVED_SUBSITE_FOLDER_PREFIX.length()).toLowerCase();
            }
            else if (pathElement.startsWith(Folder.RESERVED_FOLDER_PREFIX))
            {
                // save attribute name
                attributeName = pathElement.toLowerCase();
            }
            else
            {
                // append element to base path
                basePath.append(Folder.PATH_SEPARATOR);
                basePath.append(pathElement);
            }
        }

        // set base path
        if (basePath.length() > 0)
        {
            path = basePath.toString();
        }

        // reset canonical path
        canonicalPath = null;
    }

    /**
     * getPath
     *
     * @retuns base path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * setPath
     *
     * @param basePath base path
     */
    public void setPath(String basePath)
    {
        // cleanup path
        if ((basePath == null) || (basePath.length() == 0))
        {
            basePath = Folder.PATH_SEPARATOR;
        }
        if (!basePath.startsWith(Folder.PATH_SEPARATOR))
        {
            basePath = Folder.PATH_SEPARATOR + basePath;
        }
        if (basePath.endsWith(Folder.PATH_SEPARATOR) && !basePath.equals(Folder.PATH_SEPARATOR))
        {
            basePath = basePath.substring(0, basePath.length() - 1);
        }

        // set path
        path = basePath;
    }

    /**
     * newQueryCriteria
     *
     * Construct query Criteria for Node with matching attributes.
     */
    public Criteria newQueryCriteria()
    {
        // construct filter for base path and attributes
        Criteria filter = new Criteria();
        filter.addEqualTo("attributes::path", path);
        if ((subsite != NONE) && (subsite.length() > 0))
        {
            filter.addEqualTo("attributes::subsite", subsite);
        }
        else
        {
            filter.addEqualTo("attributes::subsite", NONE);
        }
        if ((user != NONE) && (user.length() > 0))
        {
            filter.addEqualTo("attributes::user", user);
        }
        else
        {
            filter.addEqualTo("attributes::user", NONE);
        }
        if ((role != NONE) && (role.length() > 0))
        {
            filter.addEqualTo("attributes::role", role);
        }
        else
        {
            filter.addEqualTo("attributes::role", NONE);
        }
        if ((group != NONE) && (group.length() > 0))
        {
            filter.addEqualTo("attributes::group", group);
        }
        else
        {
            filter.addEqualTo("attributes::group", NONE);
        }
        if ((mediatype != NONE) && (mediatype.length() > 0))
        {
            filter.addEqualTo("attributes::mediatype", mediatype);
        }
        else
        {
            filter.addEqualTo("attributes::mediatype", NONE);
        }
        if ((locale != NONE) && (locale.length() > 0))
        {
            filter.addEqualTo("attributes::locale", locale);
        }
        else
        {
            filter.addEqualTo("attributes::locale", NONE);
        }
        if ((extendedAttributeName != NONE) && (extendedAttributeName.length() > 0) &&
            (extendedAttributeValue != NONE) && (extendedAttributeValue.length() > 0))
        {
            filter.addEqualTo("attributes::extendedAttributeName", extendedAttributeName);
            filter.addEqualTo("attributes::extendedAttributeValue", extendedAttributeValue);
        }
        else
        {
            filter.addEqualTo("attributes::extendedAttributeName", NONE);
            filter.addEqualTo("attributes::extendedAttributeValue", NONE);
        }
        return filter;
    }
}
