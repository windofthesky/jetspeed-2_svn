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

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.search.BaseParsedObject;
import org.apache.jetspeed.search.HandlerFactory;
import org.apache.jetspeed.search.ObjectHandler;
import org.apache.jetspeed.search.ParsedObject;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.search.SearchResults;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;

/**
 * @author <a href="mailto: jford@apache.org">Jeremy Ford</a>
 *
 */
public class SearchEngineImpl implements SearchEngine
{
    protected final static Logger log = LoggerFactory.getLogger(SearchEngineImpl.class);
    private File rootIndexDir = null;
    private String analyzerClassName = null;
    private boolean optimizeAfterUpdate = true;
    private HandlerFactory handlerFactory;
    
    private static final int KEYWORD = 0;
    private static final int TEXT = 1;
    
    public SearchEngineImpl(String indexRoot, String analyzerClassName, boolean optimzeAfterUpdate, HandlerFactory handlerFactory)
    throws Exception
    {
        //assume it's full path for now
        rootIndexDir = new File(indexRoot);
        this.analyzerClassName = analyzerClassName;
        this.optimizeAfterUpdate = optimzeAfterUpdate;
        this.handlerFactory = handlerFactory;
        
        try
        {
            Searcher searcher = null;
            searcher = new IndexSearcher(rootIndexDir.getPath());
            searcher.close();
        }
        catch (Exception e)
        {
            if (rootIndexDir.exists())
            {
                log.error("Failed to open Portal Registry indexes in " + rootIndexDir.getPath(), e);
            }
            try
            {
                rootIndexDir.delete();
                rootIndexDir.mkdirs();
                
                IndexWriter indexWriter = new IndexWriter(rootIndexDir, newAnalyzer(), true);
                indexWriter.close();
                indexWriter = null;
                log.warn("Re-created Lucene Index in " + rootIndexDir.getPath());
            }
            catch (Exception e1)
            {
                String message = "Cannot RECREATE Portlet Registry indexes in "  + rootIndexDir.getPath();
                log.error(message, e1);
                throw new Exception(message);
            }
        }
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
    public synchronized boolean add(Collection objects)
    {
        boolean result = false;
        
        IndexWriter indexWriter;
        try
        {
            indexWriter = new IndexWriter(rootIndexDir, newAnalyzer(), false);
        }
        catch (IOException e)
        {
            //logger.error("Error while creating index writer. Skipping add...", e);
            return result;
        }

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
                //logger.error("Failed to create hanlder for object " + o.getClass().getName());
                continue;
            }

            // Parse the object
            ParsedObject parsedObject = handler.parseObject(o);

            // Create document
            Document doc = new Document();

            // Populate document from the parsed object
            if (parsedObject.getKey() != null)
            {                
                doc.add(new Field(ParsedObject.FIELDNAME_KEY, parsedObject.getKey(), Field.Store.YES, Field.Index.UN_TOKENIZED));
            }
            if (parsedObject.getType() != null)
            {
                doc.add(new Field(ParsedObject.FIELDNAME_TYPE, parsedObject.getType(), Field.Store.YES, Field.Index.TOKENIZED));
            }
            if (parsedObject.getTitle() != null)
            {
                doc.add(new Field(ParsedObject.FIELDNAME_TITLE, parsedObject.getTitle(), Field.Store.YES, Field.Index.TOKENIZED));
            }
            if (parsedObject.getDescription() != null)
            {
                doc.add(new Field(ParsedObject.FIELDNAME_DESCRIPTION, parsedObject.getDescription(), Field.Store.YES, Field.Index.TOKENIZED));
            }
            if (parsedObject.getContent() != null)
            {
                doc.add(new Field(ParsedObject.FIELDNAME_CONTENT, parsedObject.getContent(), Field.Store.YES, Field.Index.TOKENIZED));
            }
            if (parsedObject.getLanguage() != null)
            {
                doc.add(new Field(ParsedObject.FIELDNAME_LANGUAGE, parsedObject.getLanguage(), Field.Store.YES, Field.Index.TOKENIZED));
            }
            if (parsedObject.getURL() != null)
            {
                doc.add(new Field(ParsedObject.FIELDNAME_URL, parsedObject.getURL().toString(), Field.Store.YES, Field.Index.TOKENIZED));
            }
            if(parsedObject.getClassName() != null)
            {
                doc.add(new Field(ParsedObject.FIELDNAME_CLASSNAME, parsedObject.getClassName(), Field.Store.YES, Field.Index.TOKENIZED));
            }
            
            String[] keywordArray = parsedObject.getKeywords();
            if(keywordArray != null)
            {
            	for(int i=0; i<keywordArray.length; ++i)
            	{
            		String keyword = keywordArray[i];
            		doc.add(new Field(ParsedObject.FIELDNAME_KEYWORDS, keyword, Field.Store.YES, Field.Index.UN_TOKENIZED));
            	}
            }

            Map keywords = parsedObject.getKeywordsMap();
            addFieldsToDocument(doc, keywords, KEYWORD);
            
            Map fields = parsedObject.getFields();
            addFieldsToDocument(doc, fields, TEXT);
 
            // Add the document to search index
            try
            {
                indexWriter.addDocument(doc);
            }
            catch (IOException e)
            {
               //logger.error("Error adding document to index.", e);
            }
            //logger.debug("Index Document Count = " + indexWriter.docCount());
            //logger.info("Added '" + parsedObject.getTitle() + "' to index");
            result = true;
        }

        try
        {
        	if(optimizeAfterUpdate)
            {
                indexWriter.optimize();
            }
        }
        catch (IOException e)
        {
            //logger.error("Error while trying to optimize index.");
        }
        finally
        {
            try
            {
                indexWriter.close();
            }
            catch (IOException e)
            {
               //logger.error("Error while closing index writer.", e);
            }
        }
        
        return result;
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
        boolean result = false;
        
        try 
        {
            IndexReader indexReader = IndexReader.open(this.rootIndexDir);

            Iterator it = objects.iterator();
            while (it.hasNext()) 
            {
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
                    int rc = indexReader.deleteDocuments(term);
                    //logger.info("Attempted to delete '" + term.toString() + "' from index, documents deleted = " + rc);
                    //System.out.println("Attempted to delete '" + term.toString() + "' from index, documents deleted = " + rc);
                    result = rc > 0;
                }
            }

            indexReader.close();

            if(optimizeAfterUpdate)
            {
                optimize();
            }

        }
        catch (Exception e)
        {
            //logger.error("Exception", e);
            result = false;
        }

        return result;
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
    public synchronized boolean update(Collection objects)
    {
        boolean result = false;
        
        try
        {
            // Delete entries from index
            remove(objects);
            result = true;
        }
        catch (Throwable e)
        {
            //logger.error("Exception",  e);
        }

        try
        {
            // Add entries to index
        	if(result)
        	{
        		add(objects);
        		result = true;
        	}
        }
        catch (Throwable e)
        {
            //logger.error("Exception",  e);
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.SearchEnging#optimize()
     */
    public synchronized boolean optimize()
    {
        boolean result = false;

    	try
		{
    		IndexWriter indexWriter = new IndexWriter(rootIndexDir, newAnalyzer(), false);
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
        Searcher searcher = null;
        Hits hits = null;
        
        try
        {
            searcher = new IndexSearcher(rootIndexDir.getPath());
        }
        catch (IOException e)
        {
            //logger.error("Failed to create index search using path " + rootDir.getPath());
            return null;
        }
        
        Analyzer analyzer = newAnalyzer();
        
        String[] searchFields = {ParsedObject.FIELDNAME_CONTENT, ParsedObject.FIELDNAME_DESCRIPTION, ParsedObject.FIELDNAME_FIELDS,
                           ParsedObject.FIELDNAME_KEY, ParsedObject.FIELDNAME_KEYWORDS, ParsedObject.FIELDNAME_LANGUAGE,
                           ParsedObject.FIELDNAME_SCORE, ParsedObject.FIELDNAME_TITLE, ParsedObject.FIELDNAME_TYPE,
                           ParsedObject.FIELDNAME_URL, ParsedObject.FIELDNAME_CLASSNAME};
                            
        Query query= null;
        try
        {
        	String s[] = new String[searchFields.length];
        	for(int i=0;i<s.length;i++)
        		s[i] = queryString;
            query = MultiFieldQueryParser.parse(s, searchFields, analyzer);
//          Query query = QueryParser.parse(searchString, ParsedObject.FIELDNAME_CONTENT, analyzer);
        }
        catch (ParseException e)
        {
            List<ParsedObject> resultList = new ArrayList<ParsedObject>();
            SearchResults results = new SearchResultsImpl(resultList);
            return results;            
        }
        
        try
        {
            hits = searcher.search(query);
        }
        catch (IOException e)
        {
            List<ParsedObject> resultList = new ArrayList<ParsedObject>();
            SearchResults results = new SearchResultsImpl(resultList);
            return results;            
        }

        int hitNum = hits.length();
        List<ParsedObject> resultList = new ArrayList<ParsedObject>(hitNum);
        for(int i=0; i<hitNum; i++)
        {
            ParsedObject result = new BaseParsedObject();
            try
            {
	            Document doc = hits.doc(i);
	        
		        addFieldsToParsedObject(doc, result);
		        
		        result.setScore(hits.score(i));
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
            catch(IOException e)
            {
                //logger
            }
        }

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
        
        SearchResults results = new SearchResultsImpl(resultList);
        return results;
    }
    
    private Analyzer newAnalyzer() {
        Analyzer rval = null;

        if(analyzerClassName != null)
        {
	        try {
	            Class analyzerClass = Class.forName(analyzerClassName);
	            rval = (Analyzer) analyzerClass.newInstance();
	        } catch(InstantiationException e) {
	            //logger.error("InstantiationException", e);
	        } catch(ClassNotFoundException e) {
	            //logger.error("ClassNotFoundException", e);
	        } catch(IllegalAccessException e) {
	            //logger.error("IllegalAccessException", e);
	        }
        }

        if(rval == null) {
            rval = new StandardAnalyzer();
        }

        return rval;
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
                                        doc.add(new Field(key.toString(), value.toString(), Field.Store.YES, Field.Index.UN_TOKENIZED));
                                    }
                                    else
                                    {
                                        doc.add(new Field(key.toString(), value.toString(), Field.Store.YES, Field.Index.UN_TOKENIZED));
                                    }
                                }
                            }
                        }
                        else
                        {
                            if(type == TEXT)
                            {
                                doc.add(new Field(key.toString(), values.toString(), Field.Store.YES, Field.Index.UN_TOKENIZED));
                            }
                            else
                            {
                                doc.add(new Field(key.toString(), values.toString(), Field.Store.YES, Field.Index.UN_TOKENIZED));
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
            MultiMap multiKeywords = new MultiHashMap();
            MultiMap multiFields = new MultiHashMap();
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
}
