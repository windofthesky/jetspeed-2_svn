/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.portlet.impl;
import java.util.Collection;
import java.util.Locale;

import org.apache.jetspeed.om.portlet.DublinCore;
import org.apache.jetspeed.om.portlet.GenericMetadata;

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
    public static final String TITLE = "title";
    public static final String CONTRIBUTOR = "contributor";
    public static final String COVERAGE = "coverage";
    public static final String CREATOR = "creator";
    public static final String DESCRIPTION = "description";
    public static final String FORMAT = "format";
    public static final String IDENTIFIER = "identifier";
    public static final String LANGUAGE = "language";
    public static final String PUBLISHER = "publisher";
    public static final String RELATION = "relation";
    public static final String RIGHT = "right";
    public static final String SOURCE = "source";
    public static final String SUBJECT = "subject";
    public static final String TYPE = "type";
    
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
        return metadata.getFields(TITLE);
    }

    /** 
     * @param titles The titles to set
     */
    public void setTitles(Collection titles)
    {
        metadata.setFields(TITLE, titles);
    }

    /**
     * @return Returns the contributors.
     */
    public Collection getContributors() {
        return metadata.getFields(CONTRIBUTOR);
    }

    /**
     * @param contributors The contributors to set.
     */
    public void setContributors(Collection contributors) {
        metadata.setFields(CONTRIBUTOR, contributors);
    }

    /**
     * @return Returns the coverages.
     */
    public Collection getCoverages() {
        return metadata.getFields(COVERAGE);
    }

    /**
     * @param coverages The coverages to set.
     */
    public void setCoverages(Collection coverages) {
        metadata.setFields(COVERAGE, coverages);
    }

    /**
     * @return Returns the creators.
     */
    public Collection getCreators() {
        return metadata.getFields(CREATOR);
    }

    /**
     * @param creators The creators to set.
     */
    public void setCreators(Collection creators) {
        metadata.setFields(CREATOR, creators);
    }

    /**
     * @return Returns the descriptions.
     */
    public Collection getDescriptions() {
        return metadata.getFields(DESCRIPTION);
    }

    /**
     * @param descriptions The descriptions to set.
     */
    public void setDescriptions(Collection descriptions) {
        metadata.setFields(DESCRIPTION, descriptions);
    }

    /**
     * @return Returns the formats.
     */
    public Collection getFormats() {
        return metadata.getFields(FORMAT);
    }

    /**
     * @param formats The formats to set.
     */
    public void setFormats(Collection formats) {
        metadata.setFields(FORMAT, formats);
    }

    /**
     * @return Returns the identifiers.
     */
    public Collection getIdentifiers() {
        return metadata.getFields(IDENTIFIER);
    }

    /**
     * @param identifiers The identifiers to set.
     */
    public void setIdentifiers(Collection identifiers) {
        metadata.setFields(IDENTIFIER, identifiers);
    }

    /**
     * @return Returns the languages.
     */
    public Collection getLanguages() {
        return metadata.getFields(LANGUAGE);
    }

    /**
     * @param languages The languages to set.
     */
    public void setLanguages(Collection languages) {
        metadata.setFields(LANGUAGE, languages);
    }

    /**
     * @return Returns the publishers.
     */
    public Collection getPublishers() {
        return metadata.getFields(PUBLISHER);
    }

    /**
     * @param publishers The publishers to set.
     */
    public void setPublishers(Collection publishers) {
        metadata.setFields(PUBLISHER, publishers);
    }

    /**
     * @return Returns the relations.
     */
    public Collection getRelations() {
        return metadata.getFields(RELATION);
    }

    /**
     * @param relations The relations to set.
     */
    public void setRelations(Collection relations) {
        metadata.setFields(RELATION, relations);
    }

    /**
     * @return Returns the rights.
     */
    public Collection getRights() {
        return metadata.getFields(RIGHT);
    }

    /**
     * @param rights The rights to set.
     */
    public void setRights(Collection rights) {
        metadata.setFields(RIGHT, rights);
    }

    /**
     * @return Returns the sources.
     */
    public Collection getSources() {
        return metadata.getFields(SOURCE);
    }

    /**
     * @param sources The sources to set.
     */
    public void setSources(Collection sources) {
        metadata.setFields(SOURCE, sources);
    }

    /**
     * @return Returns the subjects.
     */
    public Collection getSubjects() {
        return metadata.getFields(SUBJECT);
    }

    /**
     * @param subjects The subjects to set.
     */
    public void setSubjects(Collection subjects) {
        metadata.setFields(SUBJECT, subjects);
    }

    /**
     * @return Returns the types.
     */
    public Collection getTypes() {
        return metadata.getFields(TYPE);
    }

    /**
     * @param types The types to set.
     */
    public void setTypes(Collection types) {
        metadata.setFields(TYPE, types);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addContributor(java.util.Locale, java.lang.String)
     */
    public void addContributor(Locale locale, String contributor) {
        metadata.addField(locale, CONTRIBUTOR, contributor);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addCoverage(java.util.Locale, java.lang.String)
     */
    public void addCoverage(Locale locale, String coverage) {
        metadata.addField(locale, COVERAGE, coverage);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addCreator(java.util.Locale, java.lang.String)
     */
    public void addCreator(Locale locale, String creator) {
        metadata.addField(locale, CREATOR, creator);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addDescription(java.util.Locale, java.lang.String)
     */
    public void addDescription(Locale locale, String description) {
        metadata.addField(locale, DESCRIPTION, description);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addFormat(java.util.Locale, java.lang.String)
     */
    public void addFormat(Locale locale, String format) {
        metadata.addField(locale, FORMAT, format);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addIdentifier(java.util.Locale, java.lang.String)
     */
    public void addIdentifier(Locale locale, String identifier) {
        metadata.addField(locale, IDENTIFIER, identifier);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addLanguage(java.util.Locale, java.lang.String)
     */
    public void addLanguage(Locale locale, String language) {
        metadata.addField(locale, LANGUAGE, language);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addPublisher(java.util.Locale, java.lang.String)
     */
    public void addPublisher(Locale locale, String publisher) {
        metadata.addField(locale, PUBLISHER, publisher);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addRelation(java.util.Locale, java.lang.String)
     */
    public void addRelation(Locale locale, String relation) {
        metadata.addField(locale, RELATION, relation);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addRight(java.util.Locale, java.lang.String)
     */
    public void addRight(Locale locale, String right) {
        metadata.addField(locale, RIGHT, right);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addSource(java.util.Locale, java.lang.String)
     */
    public void addSource(Locale locale, String source) {
        metadata.addField(locale, SOURCE, source);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addSubject(java.util.Locale, java.lang.String)
     */
    public void addSubject(Locale locale, String subject) {
        metadata.addField(locale, SUBJECT, subject);
        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addDisplayName(java.util.Locale, java.lang.String)
     */
    public void addTitle(Locale locale, String title) {
        metadata.addField(locale, TITLE, title);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.DublinCore#addType(java.util.Locale, java.lang.String)
     */
    public void addType(Locale locale, String type) {
        metadata.addField(locale, TYPE, type);
    }
}
