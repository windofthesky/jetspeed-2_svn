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
import org.apache.jetspeed.om.common.GenericMetadata;
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
    /*
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
    */
    public static final String TITLES = "titles";
    public static final String CONTRIBUTORS = "contributors";
    public static final String COVERAGES = "coverages";
    public static final String CREATORS = "creators";
    public static final String DESCRIPTIONS = "descriptions";
    public static final String FORMATS = "formats";
    public static final String IDENTIFIERS = "identifiers";
    public static final String LANGUAGES = "languages";
    public static final String PUBLISHERS = "publishers";
    public static final String RELATIONS = "relations";
    public static final String RIGHTS = "rights";
    public static final String SOURCES = "sources";
    public static final String SUBJECTS = "subjects";
    public static final String TYPES = "types";
    
    GenericMetadata metadata = null;
    
    /**
     * @param md
     */
    public DublinCoreImpl(GenericMetadata md) {
        
        this.metadata = md;
    }

    /** 
     * @return Returns the titles
     */
    public Collection getTitles()
    {
        return metadata.getFields(TITLES);
    }

    /** 
     * @param titles The titles to set
     */
    public void setTitles(Collection titles)
    {
        metadata.setFields(TITLES, titles);
    }

    /**
     * @return Returns the contributors.
     */
    public Collection getContributors() {
        return metadata.getFields(CONTRIBUTORS);
    }

    /**
     * @param contributors The contributors to set.
     */
    public void setContributors(Collection contributors) {
        metadata.setFields(CONTRIBUTORS, contributors);
    }

    /**
     * @return Returns the coverages.
     */
    public Collection getCoverages() {
        return metadata.getFields(COVERAGES);
    }

    /**
     * @param coverages The coverages to set.
     */
    public void setCoverages(Collection coverages) {
        metadata.setFields(COVERAGES, coverages);
    }

    /**
     * @return Returns the creators.
     */
    public Collection getCreators() {
        return metadata.getFields(CREATORS);
    }

    /**
     * @param creators The creators to set.
     */
    public void setCreators(Collection creators) {
        metadata.setFields(CREATORS, creators);
    }

    /**
     * @return Returns the descriptions.
     */
    public Collection getDescriptions() {
        return metadata.getFields(DESCRIPTIONS);
    }

    /**
     * @param descriptions The descriptions to set.
     */
    public void setDescriptions(Collection descriptions) {
        metadata.setFields(DESCRIPTIONS, descriptions);
    }

    /**
     * @return Returns the formats.
     */
    public Collection getFormats() {
        return metadata.getFields(FORMATS);
    }

    /**
     * @param formats The formats to set.
     */
    public void setFormats(Collection formats) {
        metadata.setFields(FORMATS, formats);
    }

    /**
     * @return Returns the identifiers.
     */
    public Collection getIdentifiers() {
        return metadata.getFields(IDENTIFIERS);
    }

    /**
     * @param identifiers The identifiers to set.
     */
    public void setIdentifiers(Collection identifiers) {
        metadata.setFields(IDENTIFIERS, identifiers);
    }

    /**
     * @return Returns the languages.
     */
    public Collection getLanguages() {
        return metadata.getFields(LANGUAGES);
    }

    /**
     * @param languages The languages to set.
     */
    public void setLanguages(Collection languages) {
        metadata.setFields(LANGUAGES, languages);
    }

    /**
     * @return Returns the publishers.
     */
    public Collection getPublishers() {
        return metadata.getFields(PUBLISHERS);
    }

    /**
     * @param publishers The publishers to set.
     */
    public void setPublishers(Collection publishers) {
        metadata.setFields(PUBLISHERS, publishers);
    }

    /**
     * @return Returns the relations.
     */
    public Collection getRelations() {
        return metadata.getFields(RELATIONS);
    }

    /**
     * @param relations The relations to set.
     */
    public void setRelations(Collection relations) {
        metadata.setFields(RELATIONS, relations);
    }

    /**
     * @return Returns the rights.
     */
    public Collection getRights() {
        return metadata.getFields(RIGHTS);
    }

    /**
     * @param rights The rights to set.
     */
    public void setRights(Collection rights) {
        metadata.setFields(RIGHTS, rights);
    }

    /**
     * @return Returns the sources.
     */
    public Collection getSources() {
        return metadata.getFields(SOURCES);
    }

    /**
     * @param sources The sources to set.
     */
    public void setSources(Collection sources) {
        metadata.setFields(SOURCES, sources);
    }

    /**
     * @return Returns the subjects.
     */
    public Collection getSubjects() {
        return metadata.getFields(SUBJECTS);
    }

    /**
     * @param subjects The subjects to set.
     */
    public void setSubjects(Collection subjects) {
        metadata.setFields(SUBJECTS, subjects);
    }

    /**
     * @return Returns the types.
     */
    public Collection getTypes() {
        return metadata.getFields(TYPES);
    }

    /**
     * @param types The types to set.
     */
    public void setTypes(Collection types) {
        metadata.setFields(TYPES, types);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addContributor(java.util.Locale, java.lang.String)
     */
    public void addContributor(Locale locale, String contributor) {
        metadata.addField(locale, CONTRIBUTORS, contributor);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addCoverage(java.util.Locale, java.lang.String)
     */
    public void addCoverage(Locale locale, String coverage) {
        metadata.addField(locale, COVERAGES, coverage);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addCreator(java.util.Locale, java.lang.String)
     */
    public void addCreator(Locale locale, String creator) {
        metadata.addField(locale, CREATORS, creator);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addDescription(java.util.Locale, java.lang.String)
     */
    public void addDescription(Locale locale, String description) {
        metadata.addField(locale, DESCRIPTIONS, description);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addFormat(java.util.Locale, java.lang.String)
     */
    public void addFormat(Locale locale, String format) {
        metadata.addField(locale, FORMATS, format);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addIdentifier(java.util.Locale, java.lang.String)
     */
    public void addIdentifier(Locale locale, String identifier) {
        metadata.addField(locale, IDENTIFIERS, identifier);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addLanguage(java.util.Locale, java.lang.String)
     */
    public void addLanguage(Locale locale, String language) {
        metadata.addField(locale, LANGUAGES, language);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addPublisher(java.util.Locale, java.lang.String)
     */
    public void addPublisher(Locale locale, String publisher) {
        metadata.addField(locale, PUBLISHERS, publisher);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addRelation(java.util.Locale, java.lang.String)
     */
    public void addRelation(Locale locale, String relation) {
        metadata.addField(locale, RELATIONS, relation);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addRight(java.util.Locale, java.lang.String)
     */
    public void addRight(Locale locale, String right) {
        metadata.addField(locale, RIGHTS, right);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addSource(java.util.Locale, java.lang.String)
     */
    public void addSource(Locale locale, String source) {
        metadata.addField(locale, SOURCES, source);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addSubject(java.util.Locale, java.lang.String)
     */
    public void addSubject(Locale locale, String subject) {
        metadata.addField(locale, SUBJECTS, subject);
        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addDisplayName(java.util.Locale, java.lang.String)
     */
    public void addTitle(Locale locale, String title) {
        metadata.addField(locale, TITLES, title);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addType(java.util.Locale, java.lang.String)
     */
    public void addType(Locale locale, String type) {
        metadata.addField(locale, TYPES, type);
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
