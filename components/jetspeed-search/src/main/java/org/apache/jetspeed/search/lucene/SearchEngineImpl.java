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
package org.apache.jetspeed.search.lucene;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto: jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 */
public class SearchEngineImpl implements SearchEngine
{
    protected final static Logger log = LoggerFactory.getLogger(SearchEngineImpl.class);
    private Directory directory;
    private Analyzer analyzer;
    private boolean optimizeAfterUpdate = true;
    private HandlerFactory handlerFactory;
    
    private static final int KEYWORD = 0;
    private static final int TEXT = 1;
    
    private int defaultTopHitsCount = 1000;
    
    public SearchEngineImpl(Directory directory, Analyzer analyzer, boolean optimzeAfterUpdate, HandlerFactory handlerFactory)
    throws Exception
    {
        this(directory, analyzer, optimzeAfterUpdate, handlerFactory, 0);
    }
    
    public SearchEngineImpl(Directory directory, Analyzer analyzer, boolean optimzeAfterUpdate, HandlerFactory handlerFactory, int defaultTopHitsCount)
    throws Exception
    {
        this.directory = directory;
        this.analyzer = analyzer;
        this.optimizeAfterUpdate = optimzeAfterUpdate;
        this.handlerFactory = handlerFactory;
        
        if (defaultTopHitsCount > 0)
        {
            this.defaultTopHitsCount = defaultTopHitsCount;
        }
        
        validateIndexDirectory();
    }
    
    public SearchEngineImpl(String indexRoot, String analyzerClassName, boolean optimzeAfterUpdate, HandlerFactory handlerFactory)
    throws Exception
    {
        this(indexRoot, analyzerClassName, optimzeAfterUpdate, handlerFactory, 0);
    }
    
    public SearchEngineImpl(String indexRoot, String analyzerClassName, boolean optimzeAfterUpdate, HandlerFactory handlerFactory, int defaultTopHitsCount)
    throws Exception
    {
        if(analyzerClassName != null)
        {
            try {
                Class analyzerClass = Class.forName(analyzerClassName);
                analyzer = (Analyzer) analyzerClass.newInstance();
            } catch(InstantiationException ce) {
                //logger.error("InstantiationException", e);
            } catch(ClassNotFoundException ce) {
                //logger.error("ClassNotFoundException", e);
            } catch(IllegalAccessException ce) {
                //logger.error("IllegalAccessException", e);
            }
        }
        
        if (analyzer == null) 
        {
            analyzer = new StandardAnalyzer(Version.LUCENE_30);
        }
        
        this.optimizeAfterUpdate = optimzeAfterUpdate;
        this.handlerFactory = handlerFactory;
        
        if (defaultTopHitsCount > 0)
        {
            this.defaultTopHitsCount = defaultTopHitsCount;
        }
        
        //assume it's full path for now
        File rootIndexDir = new File(indexRoot);
        
        if (!rootIndexDir.isDirectory())
        {
            rootIndexDir.mkdirs();
        }
        
        directory = FSDirectory.open(rootIndexDir);
        
        validateIndexDirectory();
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
        IndexReader indexReader = null;
        int deleteCount = 0;
        
        try 
        {
            Iterator it = objects.iterator();
            while (it.hasNext()) 
            {
                if (indexReader == null)
                {
                    indexReader = IndexReader.open(directory, false);
                }
                
                Object o = it.next();
                // Look up appropriate handler
                ObjectHandler handler = handlerFactory.getHandler(o);

                // Parse the object
                ParsedObject parsedObject = handler.parseObject(o);

                // Create term
                Term term = null;

                if (parsedObject.getKey() != null)
                {
                    term = new Term(ParsedObject.FIELDNAME_KEY, parsedObject.getKey());
                    // Remove the document from search index
                    deleteCount += indexReader.deleteDocuments(term);
                    //logger.info("Attempted to delete '" + term.toString() + "' from index, documents deleted = " + rc);
                    //System.out.println("Attempted to delete '" + term.toString() + "' from index, documents deleted = " + rc);
                }
            }
            
            if (indexReader != null)
            {
                indexReader.close();
                indexReader = null;
            }
            
            if (deleteCount > 0 && optimizeAfterUpdate)
            {
                optimizeIndex();
            }
        }
        catch (Exception e)
        {
            log.error("Exception during removing documents in the search index.", e);
        }
        finally
        {
            if (indexReader != null)
            {
                try
                {
                    indexReader.close();
                }
                catch (IOException ce)
                {
                }
            }
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
     * @see org.apache.jetspeed.search.SearchEnging#optimize()
     */
    public synchronized boolean optimize()
    {
        return optimizeIndex();
    }
    
    private boolean optimizeIndex()
    {
        boolean result = false;

        try
        {
            IndexWriter indexWriter = new IndexWriter(directory, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
            indexWriter.optimize();
            indexWriter.close();
            result = true;
        }
        catch (IOException e)
        {
             //logger.error("Error while trying to optimize index.");
        }
        return result;
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
        return search(queryString, defaultFieldName, defaultTopHitsCount);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.SearchEngine#search(java.lang.String, java.lang.String, int)
     */
    public SearchResults search(String queryString, String defaultFieldName, int topHitsCount)
    {
        SearchResults results = null;
        
        IndexReader indexReader = null;
        Searcher searcher = null;
        
        try
        {
            indexReader = IndexReader.open(directory);
            searcher = new IndexSearcher(indexReader);
            
            QueryParser queryParser = new QueryParser(Version.LUCENE_30, defaultFieldName, analyzer);
            Query query = queryParser.parse(queryString);
            TopDocs topDocs = searcher.search(query, topHitsCount);
            
            int count = Math.min(topHitsCount, topDocs.totalHits);
            List<ParsedObject> resultList = new ArrayList<ParsedObject>(count);
            
            for (int i = 0; i < count; i++)
            {
                ParsedObject result = new BaseParsedObject();
                
	            Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
	        
		        addFieldsToParsedObject(doc, result);
		        
		        result.setScore(topDocs.scoreDocs[i].score);
		        Field type = doc.getField(ParsedObject.FIELDNAME_TYPE);
		        if(type != null)
		        {
		            result.setType(type.stringValue());
		        }
		        
		        Field key = doc.getField(ParsedObject.FIELDNAME_KEY);
		        if(key != null)
		        {
		            result.setKey(key.stringValue());
		        }
		        
		        Field description = doc.getField(ParsedObject.FIELDNAME_DESCRIPTION);
		        if(description != null)
		        {
		            result.setDescription(description.stringValue());
		        }
		        
		        Field title = doc.getField(ParsedObject.FIELDNAME_TITLE);
		        if(title != null)
		        {
		            result.setTitle(title.stringValue());
		        }
		        
		        Field content = doc.getField(ParsedObject.FIELDNAME_CONTENT);
		        if(content != null)
		        {
		            result.setContent(content.stringValue());
		        }
		        
		        Field language = doc.getField(ParsedObject.FIELDNAME_LANGUAGE);
		        if (language != null)
		        {
		        	result.setLanguage(language.stringValue());
		        }
		        
		        Field classname = doc.getField(ParsedObject.FIELDNAME_CLASSNAME);
		        if (classname != null)
		        {
		        	result.setClassName(classname.stringValue());
		        }
		        
		        Field url = doc.getField(ParsedObject.FIELDNAME_URL);
		        if (url != null)
		        {
		            result.setURL(new URL(url.stringValue()));
		        }
		        
		        Field[] keywords = doc.getFields(ParsedObject.FIELDNAME_KEYWORDS);
		        if(keywords != null)
		        {
		        	String[] keywordArray = new String[keywords.length];
		        	
		        	for(int j=0; j<keywords.length; j++)
		        	{
		        		Field keyword = keywords[j];
		        		keywordArray[j] = keyword.stringValue();
		        	}
		        	
		        	result.setKeywords(keywordArray);
		        }
		        
		        resultList.add(i, result);
            }
            
            results = new SearchResultsImpl(resultList);
        }
        catch (Exception e)
        {
            log.error("Failed to search. ", e);
        }
        finally
        {
            if (searcher != null)
            {
                try
                {
                    searcher.close();
                }
                catch (IOException ioe)
                {
                    //logger.error("Closing Searcher", ioe);
                }
            }
            
            if (indexReader != null)
            {
                try
                {
                    indexReader.close();
                }
                catch (IOException ioe)
                {
                    //logger.error("Closing Index Reader", ioe);
                }
            }
        }
        
        return (results != null ? results : new SearchResultsImpl(new ArrayList<ParsedObject>()));
    }
    
    private synchronized boolean removeIfExistsAndAdd(Collection objects)
    {
        IndexWriter indexWriter = null;
        IndexReader indexReader = null;
        Searcher searcher = null;
        
        try
        {
            Iterator it = objects.iterator();
            while (it.hasNext()) 
            {
                if (indexWriter == null)
                {
                    indexWriter = new IndexWriter(directory, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
                    indexReader = indexWriter.getReader();
                    searcher = new IndexSearcher(indexReader);
                }
                
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
                    Term keyTerm = new Term(ParsedObject.FIELDNAME_KEY, key);
                    TopDocs topDocs = searcher.search(new TermQuery(keyTerm), 1);
                    if (topDocs.totalHits > 0)
                    {
                        indexWriter.deleteDocuments(keyTerm);
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
                Document doc = new Document();
                
                // Populate document from the parsed object
                if (key != null)
                {
                    doc.add(new Field(ParsedObject.FIELDNAME_KEY, key, Field.Store.YES, Field.Index.NOT_ANALYZED));
                }
                if (type != null)
                {
                    doc.add(new Field(ParsedObject.FIELDNAME_TYPE, type, Field.Store.YES, Field.Index.ANALYZED));
                }
                if (title != null)
                {
                    doc.add(new Field(ParsedObject.FIELDNAME_TITLE, title, Field.Store.YES, Field.Index.ANALYZED));
                }
                if (description != null)
                {
                    doc.add(new Field(ParsedObject.FIELDNAME_DESCRIPTION, description, Field.Store.YES, Field.Index.ANALYZED));
                }
                if (content != null)
                {
                    doc.add(new Field(ParsedObject.FIELDNAME_CONTENT, content, Field.Store.NO, Field.Index.ANALYZED));
                }
                if (language != null)
                {
                    doc.add(new Field(ParsedObject.FIELDNAME_LANGUAGE, language, Field.Store.YES, Field.Index.ANALYZED));
                }
                if (url != null)
                {
                    String urlString = url.toString();
                    doc.add(new Field(ParsedObject.FIELDNAME_URL, urlString, Field.Store.YES, Field.Index.ANALYZED));
                }
                if (className != null)
                {
                    doc.add(new Field(ParsedObject.FIELDNAME_CLASSNAME, className, Field.Store.YES, Field.Index.ANALYZED));
                }
                
                String[] keywordArray = parsedObject.getKeywords();
                if(keywordArray != null)
                {
                    for(int i=0; i<keywordArray.length; ++i)
                    {
                        String keyword = keywordArray[i];
                        doc.add(new Field(ParsedObject.FIELDNAME_KEYWORDS, keyword, Field.Store.YES, Field.Index.NOT_ANALYZED));
                    }
                }
    
                Map keywords = parsedObject.getKeywordsMap();
                addFieldsToDocument(doc, keywords, KEYWORD);
                
                Map fields = parsedObject.getFields();
                addFieldsToDocument(doc, fields, TEXT);
                
                List<String> syntheticField = new ArrayList<String>();
                for (Fieldable fieldable : doc.getFields())
                {
                    String value = fieldable.stringValue();
                    if (value != null)
                    {
                        syntheticField.add(value);
                    }
                }
                doc.add(new Field(ParsedObject.FIELDNAME_SYNTHETIC, StringUtils.join(syntheticField, ' '), Field.Store.NO, Field.Index.ANALYZED));

                // Add the document to search index
                indexWriter.addDocument(doc);
                //logger.debug("Index Document Count = " + indexWriter.docCount());
                //logger.info("Added '" + parsedObject.getTitle() + "' to index");
            }

            if (objects.size() > 0 && optimizeAfterUpdate && indexWriter != null)
            {
                try
                {
                    indexWriter.optimize();
                }
                catch (IOException e)
                {
                    log.error("Error while trying to optimize index.", e);
                }
            }
        }
        catch (IOException e)
        {
            log.error("Error while writing index.", e);
            return false;
        }
        finally
        {
            if (searcher != null)
            {
                try
                {
                    searcher.close();
                }
                catch (IOException ce)
                {
                }
            }
            if (indexReader != null)
            {
                try
                {
                    indexReader.close();
                }
                catch (IOException ce)
                {
                }
            }
            if (indexWriter != null)
            {
                try
                {
                    indexWriter.close();
                }
                catch (IOException ce)
                {
                }
            }
        }
        
        return true;
    }

    private void addFieldsToDocument(Document doc, Map fields, int type)
    {
        if(fields != null)
        {
            Iterator keyIter = fields.keySet().iterator();
            while(keyIter.hasNext())
            {
                Object key = keyIter.next();
                if(key != null)
                {
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
                                    if(type == TEXT)
                                    {
                                        doc.add(new Field(key.toString(), value.toString(), Field.Store.YES, Field.Index.ANALYZED));
                                    }
                                    else
                                    {
                                        doc.add(new Field(key.toString(), value.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
                                    }
                                }
                            }
                        }
                        else
                        {
                            if(type == TEXT)
                            {
                                doc.add(new Field(key.toString(), values.toString(), Field.Store.YES, Field.Index.ANALYZED));
                            }
                            else
                            {
                                doc.add(new Field(key.toString(), values.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
                            }
                        }
                    }
                }
            } 
        }
    }
    
    private void addFieldsToParsedObject(Document doc, ParsedObject o)
    {
        try
        {
            MultiMap multiKeywords = new MultiValueMap();
            MultiMap multiFields = new MultiValueMap();
            HashMap fieldMap = new HashMap();
            
            Field classNameField = doc.getField(ParsedObject.FIELDNAME_CLASSNAME);
            if(classNameField != null)
            {
                String className = classNameField.stringValue();
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
        catch(Exception e)
        {
            //logger.error("Error trying to add fields to parsed object.", e);
        }
    }
    
    private void addFieldsToMap(Document doc, Set fieldNames, Map fields)
    {
        Iterator fieldIter = fieldNames.iterator();
        while(fieldIter.hasNext())
        {
            String fieldName = (String)fieldIter.next();
            Field[] docFields = doc.getFields(fieldName);
            if(docFields != null)
            {
                for(int i=0; i<docFields.length; i++)
                {
                    Field field = docFields[i];
                    if(field != null)
                    {
                        String value = field.stringValue();
                        fields.put(fieldName, value);
                    }
                }
            }
        }
    }
    
    private void validateIndexDirectory() throws Exception
    {
        boolean recreateIndex = false;
        
        IndexReader indexReader = null;
        Searcher searcher = null;
        
        try
        {
            indexReader = IndexReader.open(directory);
            searcher = new IndexSearcher(indexReader);
            searcher.close();
            searcher = null;
            indexReader.close();
            indexReader = null;
        }
        catch (Exception e)
        {
            recreateIndex = true;
        }
        finally
        {
            if (searcher != null)
            {
                try 
                {
                    searcher.close();
                }
                catch (Exception ce)
                {
                }
            }
            if (indexReader != null)
            {
                try 
                {
                    indexReader.close();
                }
                catch (Exception ce)
                {
                }
            }
        }
        
        if (recreateIndex)
        {
            IndexWriter indexWriter = null;
            
            try
            {
                indexWriter = new IndexWriter(directory, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
                indexWriter.close();
                indexWriter = null;
            }
            catch (Exception e1)
            {
                String message = "Cannot RECREATE Portlet Registry indexes in "  + directory;
                log.error(message, e1);
                throw new Exception(message);
            }
            finally
            {
                if (indexWriter != null)
                {
                    try 
                    {
                        indexWriter.close();
                    }
                    catch (Exception ce)
                    {
                    }
                }
            }
        }
    }
}
