/*
 * Copyright 2007 The Apache Software Foundation.
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
package org.apache.jetspeed.portlets.rpad;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PortletApplication
{
    private Date created = null;

    private Date lastModified = null;

    private String portletSpecVersion = null;

    private String groupId = null;

    private String artifactId = null;

    private String packaging = null;

    private String version = null;

    private String name = null;

    private String description = null;

    private List categories = new ArrayList();

    private String publisherName = null;

    private String publisherUrl = null;

    private String binaryUrl = null;

    private String sourceUrl = null;

    private String thumbnailUrl = null;

    //TODO
    //    private Map dependencies;

    //TODO
    //   private List licenses = new ArrayList();

    private String javaBuildVersion = null;

    private String javaRuntimeVersion = null;

    private Locale defaultLocale = Locale.ENGLISH;

    private List supportedLocales = new ArrayList();

    public PortletApplication()
    {

    }

    public void addCategory(String category)
    {
        categories.add(category);
    }

    public List getCategories()
    {
        return categories;
    }

    public void addSupportedLocale(Locale locale)
    {
        supportedLocales.add(locale);
    }

    public List getSupportedLocales()
    {
        return supportedLocales;
    }

    /**
     * @return the artifactId
     */
    public String getArtifactId()
    {
        return artifactId;
    }

    /**
     * @param artifactId the artifactId to set
     */
    public void setArtifactId(String artifactId)
    {
        this.artifactId = artifactId;
    }

    /**
     * @return the binaryUrl
     */
    public String getBinaryUrl()
    {
        return binaryUrl;
    }

    /**
     * @param binaryUrl the binaryUrl to set
     */
    public void setBinaryUrl(String binaryUrl)
    {
        this.binaryUrl = binaryUrl;
    }

    /**
     * @return the created
     */
    public Date getCreated()
    {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(Date created)
    {
        this.created = created;
    }

    /**
     * @return the defaultLocale
     */
    public Locale getDefaultLocale()
    {
        return defaultLocale;
    }

    /**
     * @param defaultLocale the defaultLocale to set
     */
    public void setDefaultLocale(Locale defaultLocale)
    {
        this.defaultLocale = defaultLocale;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return the groupId
     */
    public String getGroupId()
    {
        return groupId;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    /**
     * @return the javaBuildVersion
     */
    public String getJavaBuildVersion()
    {
        return javaBuildVersion;
    }

    /**
     * @param javaBuildVersion the javaBuildVersion to set
     */
    public void setJavaBuildVersion(String javaBuildVersion)
    {
        this.javaBuildVersion = javaBuildVersion;
    }

    /**
     * @return the javaRuntimeVersion
     */
    public String getJavaRuntimeVersion()
    {
        return javaRuntimeVersion;
    }

    /**
     * @param javaRuntimeVersion the javaRuntimeVersion to set
     */
    public void setJavaRuntimeVersion(String javaRuntimeVersion)
    {
        this.javaRuntimeVersion = javaRuntimeVersion;
    }

    /**
     * @return the lastModified
     */
    public Date getLastModified()
    {
        return lastModified;
    }

    /**
     * @param lastModified the lastModified to set
     */
    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }

    /**
     * @return the namme
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param namme the namme to set
     */
    public void setName(String namme)
    {
        this.name = namme;
    }

    /**
     * @return the packaging
     */
    public String getPackaging()
    {
        return packaging;
    }

    /**
     * @param packaging the packaging to set
     */
    public void setPackaging(String packaging)
    {
        this.packaging = packaging;
    }

    /**
     * @return the portletSpecVersion
     */
    public String getPortletSpecVersion()
    {
        return portletSpecVersion;
    }

    /**
     * @param portletSpecVersion the portletSpecVersion to set
     */
    public void setPortletSpecVersion(String portletSpecVersion)
    {
        this.portletSpecVersion = portletSpecVersion;
    }

    /**
     * @return the publisherName
     */
    public String getPublisherName()
    {
        return publisherName;
    }

    /**
     * @param publisherName the publisherName to set
     */
    public void setPublisherName(String publisherName)
    {
        this.publisherName = publisherName;
    }

    /**
     * @return the publisherUrl
     */
    public String getPublisherUrl()
    {
        return publisherUrl;
    }

    /**
     * @param publisherUrl the publisherUrl to set
     */
    public void setPublisherUrl(String publisherUrl)
    {
        this.publisherUrl = publisherUrl;
    }

    /**
     * @return the sourceUrl
     */
    public String getSourceUrl()
    {
        return sourceUrl;
    }

    /**
     * @param sourceUrl the sourceUrl to set
     */
    public void setSourceUrl(String sourceUrl)
    {
        this.sourceUrl = sourceUrl;
    }

    /**
     * @return the thumbnailUrl
     */
    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }

    /**
     * @param thumbnailUrl the thumbnailUrl to set
     */
    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
    }

    /**
     * @return the version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

}
