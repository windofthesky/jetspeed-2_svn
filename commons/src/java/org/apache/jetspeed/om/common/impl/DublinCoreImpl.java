/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.common.impl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.apache.jetspeed.om.common.DublinCore;
import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.util.JetspeedObjectID;

import org.apache.pluto.om.common.ObjectID;

/**
 * DublinCoreImpl
 * <br/>
 * Implementation that allows retrieving information according to the 
 * Dublin Core specification 
 * (<a href="http://www.dublincore.org">http://www.dublincore.org</a>)
 * 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 *
 */
public class DublinCoreImpl implements DublinCore
{
    private int id;
    
    private Collection titles = null;
    private Collection creators = null;
    private Collection subjects = null;
    private Collection descriptions = null;
    private Collection publishers = null;
    private Collection contributors = null;
    //private Collection dates = null;
    private Collection types = null;
    private Collection formats = null;
    private Collection identifiers = null;
    private Collection sources = null;
    private Collection languages = null;
    private Collection relations = null;
    private Collection coverages = null;
    private Collection rights = null;
    
    /** 
     * @return Returns the titles
     */
    public Collection getTitles()
    {
        return titles;
    }

    /** 
     * @param titles The titles to set
     */
    public void setTitles(Collection titles)
    {
        this.titles = titles;
    }

    /**
     * @return Returns the contributors.
     */
    public Collection getContributors() {
        return contributors;
    }

    /**
     * @param contributors The contributors to set.
     */
    public void setContributors(Collection contributors) {
        this.contributors = contributors;
    }

    /**
     * @return Returns the coverages.
     */
    public Collection getCoverages() {
        return coverages;
    }

    /**
     * @param coverages The coverages to set.
     */
    public void setCoverages(Collection coverages) {
        this.coverages = coverages;
    }

    /**
     * @return Returns the creators.
     */
    public Collection getCreators() {
        return creators;
    }

    /**
     * @param creators The creators to set.
     */
    public void setCreators(Collection creators) {
        this.creators = creators;
    }

    /**
     * @return Returns the descriptions.
     */
    public Collection getDescriptions() {
        return descriptions;
    }

    /**
     * @param descriptions The descriptions to set.
     */
    public void setDescriptions(Collection descriptions) {
        this.descriptions = descriptions;
    }

    /**
     * @return Returns the formats.
     */
    public Collection getFormats() {
        return formats;
    }

    /**
     * @param formats The formats to set.
     */
    public void setFormats(Collection formats) {
        this.formats = formats;
    }

    /**
     * @return Returns the identifiers.
     */
    public Collection getIdentifiers() {
        return identifiers;
    }

    /**
     * @param identifiers The identifiers to set.
     */
    public void setIdentifiers(Collection identifiers) {
        this.identifiers = identifiers;
    }

    /**
     * @return Returns the languages.
     */
    public Collection getLanguages() {
        return languages;
    }

    /**
     * @param languages The languages to set.
     */
    public void setLanguages(Collection languages) {
        this.languages = languages;
    }

    /**
     * @return Returns the publishers.
     */
    public Collection getPublishers() {
        return publishers;
    }

    /**
     * @param publishers The publishers to set.
     */
    public void setPublishers(Collection publishers) {
        this.publishers = publishers;
    }

    /**
     * @return Returns the relations.
     */
    public Collection getRelations() {
        return relations;
    }

    /**
     * @param relations The relations to set.
     */
    public void setRelations(Collection relations) {
        this.relations = relations;
    }

    /**
     * @return Returns the rights.
     */
    public Collection getRights() {
        return rights;
    }

    /**
     * @param rights The rights to set.
     */
    public void setRights(Collection rights) {
        this.rights = rights;
    }

    /**
     * @return Returns the sources.
     */
    public Collection getSources() {
        return sources;
    }

    /**
     * @param sources The sources to set.
     */
    public void setSources(Collection sources) {
        this.sources = sources;
    }

    /**
     * @return Returns the subjects.
     */
    public Collection getSubjects() {
        return subjects;
    }

    /**
     * @param subjects The subjects to set.
     */
    public void setSubjects(Collection subjects) {
        this.subjects = subjects;
    }

    /**
     * @return Returns the types.
     */
    public Collection getTypes() {
        return types;
    }

    /**
     * @param types The types to set.
     */
    public void setTypes(Collection types) {
        this.types = types;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addContributor(java.util.Locale, java.lang.String)
     */
    public void addContributor(Locale locale, String contributor) {
        if(contributors == null)
        {
            contributors = new ArrayList();
        }
        
        addLocalizedFieldToCollection(contributors, locale, contributor);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addCoverage(java.util.Locale, java.lang.String)
     */
    public void addCoverage(Locale locale, String coverage) {
        if(coverages == null)
        {
            coverages = new ArrayList();
        }
        
        addLocalizedFieldToCollection(coverages, locale, coverage);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addCreator(java.util.Locale, java.lang.String)
     */
    public void addCreator(Locale locale, String creator) {
        if(creators == null)
        {
            creators = new ArrayList();
        }
        
        addLocalizedFieldToCollection(creators, locale, creator);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addDescription(java.util.Locale, java.lang.String)
     */
    public void addDescription(Locale locale, String description) {
        if(descriptions == null)
        {
            descriptions = new ArrayList();
        }
        
        addLocalizedFieldToCollection(descriptions, locale, description);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addFormat(java.util.Locale, java.lang.String)
     */
    public void addFormat(Locale locale, String format) {
        if(formats == null)
        {
            formats = new ArrayList();
        }
        
        addLocalizedFieldToCollection(formats, locale, format);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addIdentifier(java.util.Locale, java.lang.String)
     */
    public void addIdentifier(Locale locale, String identifier) {
        if(identifiers == null)
        {
            identifiers = new ArrayList();
        }
        
        addLocalizedFieldToCollection(identifiers, locale, identifier);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addLanguage(java.util.Locale, java.lang.String)
     */
    public void addLanguage(Locale locale, String language) {
        if(languages == null)
        {
            languages = new ArrayList();
        }
        
        addLocalizedFieldToCollection(languages, locale, language);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addPublisher(java.util.Locale, java.lang.String)
     */
    public void addPublisher(Locale locale, String publisher) {
        if(publishers == null)
        {
            publishers = new ArrayList();
        }
        
        addLocalizedFieldToCollection(publishers, locale, publisher);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addRelation(java.util.Locale, java.lang.String)
     */
    public void addRelation(Locale locale, String relation) {
        if(relations == null)
        {
            relations = new ArrayList();
        }
        
        addLocalizedFieldToCollection(relations, locale, relation);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addRight(java.util.Locale, java.lang.String)
     */
    public void addRight(Locale locale, String right) {
        if(rights == null)
        {
            rights = new ArrayList();
        }
        
        addLocalizedFieldToCollection(rights, locale, right);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addSource(java.util.Locale, java.lang.String)
     */
    public void addSource(Locale locale, String source) {
        if(sources == null)
        {
            sources = new ArrayList();
        }
        
        addLocalizedFieldToCollection(sources, locale, source);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addSubject(java.util.Locale, java.lang.String)
     */
    public void addSubject(Locale locale, String subject) {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addDisplayName(java.util.Locale, java.lang.String)
     */
    public void addTitle(Locale locale, String title) {
        if (titles == null)
        {
            titles = new ArrayList();
        }
        //descCollWrapper.setInnerCollection(descriptions);
        
        addLocalizedFieldToCollection(titles, locale, title);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addType(java.util.Locale, java.lang.String)
     */
    public void addType(Locale locale, String type) {
        if (types == null)
        {
            types = new ArrayList();
        }

        addLocalizedFieldToCollection(types, locale, type);
    }
    
    private void addLocalizedFieldToCollection(Collection fields, Locale locale, String value)
    {
        try
        {
            LocalizedField localizedField = new LocalizedFieldImpl();
            //TODO: switch to object creation through another mechanism
            //(LocalizedField) JetspeedPortletRegistry.getNewObjectInstance(MutableDescription.TYPE_WEB_APP, true);
            localizedField.setLocale(locale);
            localizedField.setValue(value);
            fields.add(localizedField);
        }
        catch(Exception e)
        {
            String msg = "Unable to instantiate LocalizedField implementor, " + e.toString();
            //log.error(msg, e);
            throw new IllegalStateException(msg);
        }
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getId()
     */
    public ObjectID getId()
    {
        return new JetspeedObjectID(id);
    }
    
    /**
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setId(java.lang.String)
     */
    public void setId(String oid)
    {
        id = JetspeedObjectID.createFromString(oid).intValue();
    }

}
