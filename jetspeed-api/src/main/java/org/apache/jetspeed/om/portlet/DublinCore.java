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
package org.apache.jetspeed.om.portlet;

import java.util.Collection ;
import java.util.Locale;

/**
 * DublinCore
 * <br/>
 * Interface that allows retrieving information according to the 
 * Dublin Core specification 
 * (<a href="http://www.dublincore.org">http://www.dublincore.org</a>)
 * 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 *
 */
public interface DublinCore extends java.io.Serializable
{
    public Collection getTitles();
    public void setTitles(Collection titles);
    void addTitle(Locale locale, String title);
    
    public Collection getContributors();
    public void setContributors(Collection contributors);
    void addContributor(Locale locale, String contributor);
    
    public Collection getCoverages();
    public void setCoverages(Collection coverages);
    void addCoverage(Locale locale, String coverage);
    
    public Collection getCreators();
    public void setCreators(Collection creators);
    void addCreator(Locale locale, String creator);
    
    public Collection getDescriptions();
    public void setDescriptions(Collection descriptions);
    void addDescription(Locale locale, String description);
    
    public Collection getFormats();
    public void setFormats(Collection formats);
    void addFormat(Locale locale, String format);
    
    public Collection getIdentifiers();
    public void setIdentifiers(Collection identifiers);
    void addIdentifier(Locale locale, String identifier);
    
    public Collection getLanguages();
    public void setLanguages(Collection languages);
    void addLanguage(Locale locale, String language);
    
    public Collection getPublishers();
    public void setPublishers(Collection publishers);
    void addPublisher(Locale locale, String publisher);
    
    public Collection getRelations();
    public void setRelations(Collection relations);
    void addRelation(Locale locale, String relation);
    
    public Collection getRights();
    public void setRights(Collection rights);
    void addRight(Locale locale, String right);
    
    public Collection getSources();
    public void setSources(Collection sources);
    void addSource(Locale locale, String source);
    
    public Collection getSubjects();
    public void setSubjects(Collection subjects);
    void addSubject(Locale locale, String subject);
    
    public Collection getTypes();
    public void setTypes(Collection types);
    void addType(Locale locale, String type);
}
