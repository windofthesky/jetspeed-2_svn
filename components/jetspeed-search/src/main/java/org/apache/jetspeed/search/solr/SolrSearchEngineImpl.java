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
package org.apache.jetspeed.search.solr;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.search.BaseParsedObject;
import org.apache.jetspeed.search.HandlerFactory;
import org.apache.jetspeed.search.ObjectHandler;
import org.apache.jetspeed.search.ParsedObject;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.search.SearchResults;
import org.apache.jetspeed.search.SearchResultsImpl;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Id: SolrSearchEngineImpl.java 1086464 2011-03-29 02:08:39Z woonsan $
 */
public class SolrSearchEngineImpl implements SearchEngine
{
    private final static Logger log = LoggerFactory.getLogger(SolrSearchEngineImpl.class);

    private SolrServer server;
    private boolean optimizeAfterUpdate = true;
    private HandlerFactory handlerFactory;
    private Set<String> searchableMetadataFieldNames = 
        new HashSet<String>(Arrays.asList(
                                          "ID", 
                                          "url", 
                                          "portlet", 
                                          "portlet_application",
                                          "subject",
                                          "creator",
                                          "publisher",
                                          "title",
                                          "fieldname.title",
                                          "contributor",
                                          "description",
                                          "fieldname.description"
                                          ));
    
    public SolrSearchEngineImpl(SolrServer server, boolean optimzeAfterUpdate, HandlerFactory handlerFactory)
    {
        this.server = server;
        this.optimizeAfterUpdate = optimzeAfterUpdate;
        this.handlerFactory = handlerFactory;
    }
    
    public Set<String> getSearchableMetadataFieldNames()
    {
        return searchableMetadataFieldNames;
    }

    public void setSearchableMetadataFieldNames(Set<String> searchableMetadataFieldNames)
    {
        this.searchableMetadataFieldNames = searchableMetadataFieldNames;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.SearchEnging#add(java.lang.Object)
     */
    public boolean add(Object o)
    {
        Collection c = new ArrayList(1);
        c.add(o);

        return add(c);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.SearchEnging#add(java.util.Collection)
     */
    public boolean add(Collection objects)
    {
        return removeIfExistsAndAdd(objects);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.SearchEnging#remove(java.lang.Object)
     */
    public boolean remove(Object o)
    {
        Collection c = new ArrayList(1);
        c.add(o);

        return remove(c);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.SearchEnging#remove(java.util.Collection)
     */
    public synchronized boolean remove(Collection objects)
    {
        int deleteCount = 0;
        
        try 
        {
            Iterator it = objects.iterator();
            while (it.hasNext()) 
            {
                Object o = it.next();
                // Look up appropriate handler
                ObjectHandler handler = handlerFactory.getHandler(o);

                // Parse the object
                ParsedObject parsedObject = handler.parseObject(o);

                if (parsedObject.getKey() != null)
                {
                    String queryString = new StringBuilder(40).append(ParsedObject.FIELDNAME_KEY).append(':').append(ClientUtils.escapeQueryChars(parsedObject.getKey())).toString();
                    
                    SolrQuery query = new SolrQuery();
                    query.setQuery(queryString);
                    QueryResponse qrsp = server.query(query);
                    int count = qrsp.getResults().size();
                    
                    if (count > 0)
                    {
                        // Remove the document from search index
                        UpdateResponse rsp = server.deleteByQuery(queryString);
                        
                        if (rsp.getStatus() < 300) 
                        {
                            deleteCount += count;
                        }
                    }
                }
            }
            
            if (deleteCount > 0)
            {
                server.commit();
                
                if (optimizeAfterUpdate) {
                    server.optimize();
                }
            }
        }
        catch (Exception e)
        {
            log.error("Exception during removing documents in the search index.", e);
        }

        return deleteCount > 0;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.SearchEnging#update(java.lang.Object)
     */
    public boolean update(Object o)
    {
        Collection c = new ArrayList(1);
        c.add(o);
        
        return update(c);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.SearchEnging#update(java.util.Collection)
     */
    public boolean update(Collection objects)
    {
        return removeIfExistsAndAdd(objects);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.SearchEngine#search(java.lang.String)
     */
    public SearchResults search(String queryString)
    {
        return search(queryString, ParsedObject.FIELDNAME_SYNTHETIC);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.SearchEngine#search(java.lang.String, java.lang.String)
     */
    public SearchResults search(String queryString, String defaultFieldName)
    {
        return search(queryString, defaultFieldName, 0);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.SearchEngine#search(java.lang.String, java.lang.String, int)
     */
    public SearchResults search(String queryString, String defaultFieldName, int topHitsCount)
    {
        SearchResults results = null;
        
        try
        {
            SolrQuery query = new SolrQuery();
            query.setQuery(queryString);
            QueryResponse rsp = server.query(query);
            SolrDocumentList docList = rsp.getResults();
            List<ParsedObject> resultList = new ArrayList<ParsedObject>();
            
            for (SolrDocument doc : docList)
            {
                ParsedObject result = new BaseParsedObject();
                
                addFieldsToParsedObject(doc, result);
                
                Object type = doc.getFirstValue(ParsedObject.FIELDNAME_TYPE);
                if(type != null)
                {
                    result.setType(type.toString());
                }
                
                Object key = doc.getFirstValue(ParsedObject.FIELDNAME_KEY);
                if(key != null)
                {
                    result.setKey(key.toString());
                }
                
                Object description = doc.getFirstValue(ParsedObject.FIELDNAME_DESCRIPTION);
                if(description != null)
                {
                    result.setDescription(description.toString());
                }
                
                Object title = doc.getFirstValue(ParsedObject.FIELDNAME_TITLE);
                if(title != null)
                {
                    result.setTitle(title.toString());
                }
                
                Object content = doc.getFirstValue(ParsedObject.FIELDNAME_CONTENT);
                if(content != null)
                {
                    result.setContent(content.toString());
                }
                
                Object language = doc.getFirstValue(ParsedObject.FIELDNAME_LANGUAGE);
                if (language != null)
                {
                    result.setLanguage(language.toString());
                }
                
                Object classname = doc.getFirstValue(ParsedObject.FIELDNAME_CLASSNAME);
                if (classname != null)
                {
                    result.setClassName(classname.toString());
                }
                
                Object url = doc.getFirstValue(ParsedObject.FIELDNAME_URL);
                if (url != null)
                {
                    result.setURL(new URL(url.toString()));
                }
                
                Collection<Object> keywords = doc.getFieldValues(ParsedObject.FIELDNAME_KEYWORDS);
                if(keywords != null)
                {
                    String[] keywordArray = new String[keywords.size()];
                    int index = 0;
                    
                    for (Object keyword : keywords)
                    {
                        keywordArray[index++] = keyword.toString();
                    }
                    
                    result.setKeywords(keywordArray);
                }
                
                resultList.add(result);
            }
            
            results = new SearchResultsImpl(resultList);
        }
        catch (Exception e)
        {
            log.error("Failed to search. ", e);
        }
        
        return (results != null ? results : new SearchResultsImpl(new ArrayList<ParsedObject>()));
    }

    public boolean optimize()
    {
        try
        {
            server.optimize();
            return true;
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled()) {
                log.error("Error while trying to optimize index. " + e, e);
            } else {
                log.error("Error while trying to optimize index. {}", e.toString());
            }
        }
        
        return false;
    }
    
    private synchronized boolean removeIfExistsAndAdd(Collection objects)
    {
        try
        {
            Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
            
            Iterator it = objects.iterator();
            
            while (it.hasNext()) 
            {
                Object o = it.next();
                // Look up appropriate handler
                ObjectHandler handler = null;
                try
                {
                    handler = handlerFactory.getHandler(o);
                }
                catch (Exception e)
                {
                    log.error("Failed to create hanlder for object " + o.getClass().getName());
                    continue;
                }
    
                // Parse the object
                ParsedObject parsedObject = handler.parseObject(o);
                
                String key = parsedObject.getKey();
                // if there's an existing one with the same key, then remove it first.
                if (parsedObject.getKey() != null)
                {
                    SolrQuery query = new SolrQuery();
                    String queryString = new StringBuilder(40).append(ParsedObject.FIELDNAME_KEY).append(':').append(ClientUtils.escapeQueryChars(key)).toString();
                    query.setQuery(queryString);
                    QueryResponse qrsp = server.query(query);
                    
                    if (!qrsp.getResults().isEmpty())
                    {
                        UpdateResponse ursp = server.deleteByQuery(queryString);
                        
                        if (ursp.getStatus() < 300)
                        {
                            server.commit();
                        }
                    }
                }
                
                String type = parsedObject.getType();
                String title = parsedObject.getTitle();
                String description = parsedObject.getDescription();
                String content = parsedObject.getContent();
                String language = parsedObject.getLanguage();
                URL url = parsedObject.getURL();
                String className = parsedObject.getClassName();
                
                // Create document
                SolrInputDocument doc = new SolrInputDocument();
                
                // Populate document from the parsed object
                if (key != null)
                {
                    doc.addField(ParsedObject.FIELDNAME_KEY, key, 1.0f);
                }
                if (type != null)
                {
                    doc.addField(ParsedObject.FIELDNAME_TYPE, type, 1.0f);
                }
                if (title != null)
                {
                    doc.addField(ParsedObject.FIELDNAME_TITLE, title, 1.0f);
                }
                if (description != null)
                {
                    doc.addField(ParsedObject.FIELDNAME_DESCRIPTION, description, 1.0f);
                }
                if (content != null)
                {
                    doc.addField(ParsedObject.FIELDNAME_CONTENT, content, 1.0f);
                }
                if (language != null)
                {
                    doc.addField(ParsedObject.FIELDNAME_LANGUAGE, language, 1.0f);
                }
                if (url != null)
                {
                    String urlString = url.toString();
                    doc.addField(ParsedObject.FIELDNAME_URL, urlString, 1.0f);
                }
                if (className != null)
                {
                    doc.addField(ParsedObject.FIELDNAME_CLASSNAME, className, 1.0f);
                }
                
                String[] keywordArray = parsedObject.getKeywords();
                if(keywordArray != null)
                {
                    for(int i=0; i<keywordArray.length; ++i)
                    {
                        String keyword = keywordArray[i];
                        doc.addField(ParsedObject.FIELDNAME_KEYWORDS, keyword, 1.0f);
                    }
                }
                
                Map keywords = parsedObject.getKeywordsMap();
                addFieldsToDocument(doc, keywords);
                
                Map fields = parsedObject.getFields();
                addFieldsToDocument(doc, fields);
                
                List<String> syntheticField = new ArrayList<String>();
                for (Map.Entry<String, SolrInputField> entry : doc.entrySet())
                {
                    SolrInputField field = entry.getValue();
                    Object value = field.getFirstValue();
                    
                    if (value != null)
                    {
                        syntheticField.add(value.toString());
                    }
                }
                
                doc.addField(ParsedObject.FIELDNAME_SYNTHETIC, StringUtils.join(syntheticField, ' '), 1.0f);

                docs.add(doc);
            }
            
            if (objects.size() > 0)
            {
                server.add(docs);
                server.commit();
                
                if (optimizeAfterUpdate) {
                    try
                    {
                        server.optimize();
                    }
                    catch (IOException e)
                    {
                        log.error("Error while trying to optimize index.", e);
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("Error while writing index.", e);
            return false;
        }
        
        return true;
    }

    private void addFieldsToDocument(SolrInputDocument doc, Map fields)
    {
        if(fields != null)
        {
            Iterator keyIter = fields.keySet().iterator();
            while(keyIter.hasNext())
            {
                Object key = keyIter.next();
                
                if(key != null)
                {
                    if (!searchableMetadataFieldNames.contains(key))
                    {
                        continue;
                    }
                    
                    Object values = fields.get(key);
                    if(values != null)
                    {
                        if(values instanceof Collection)
                        {
                            Iterator valueIter = ((Collection)values).iterator();
                            while(valueIter.hasNext())
                            {
                                Object value = valueIter.next();
                                if(value != null)
                                {
                                    doc.addField(key.toString(), value.toString(), 1.0f);
                                }
                            }
                        }
                        else
                        {
                            doc.addField(key.toString(), values.toString(), 1.0f);
                        }
                    }
                }
            } 
        }
    }

    private void addFieldsToParsedObject(SolrDocument doc, ParsedObject o)
    {
        try
        {
            MultiMap multiKeywords = new MultiValueMap();
            MultiMap multiFields = new MultiValueMap();
            HashMap fieldMap = new HashMap();
            
            Object classNameField = doc.getFirstValue(ParsedObject.FIELDNAME_CLASSNAME);
            
            if(classNameField != null)
            {
                String className = classNameField.toString();
                o.setClassName(className);
                ObjectHandler handler = handlerFactory.getHandler(className);
                
                Set fields = handler.getFields();
                addFieldsToMap(doc, fields, multiFields);
                addFieldsToMap(doc, fields, fieldMap);
                
                Set keywords = handler.getKeywords();
                addFieldsToMap(doc, keywords, multiKeywords);
            }
            
            o.setKeywordsMap(multiKeywords);
            o.setFields(multiFields);
            o.setFields(fieldMap);
        }
        catch (Exception e)
        {
            log.error("Error trying to add fields to parsed object.", e);
        }
    }

    private void addFieldsToMap(SolrDocument doc, Set fieldNames, Map fields)
    {
        Iterator fieldIter = fieldNames.iterator();
        while(fieldIter.hasNext())
        {
            String fieldName = (String)fieldIter.next();
            Collection<Object> values = doc.getFieldValues(fieldName);
            
            if (values != null)
            {
                for (Object value : values)
                {
                    fields.put(fieldName, value.toString());
                }
            }
        }
    }

}
