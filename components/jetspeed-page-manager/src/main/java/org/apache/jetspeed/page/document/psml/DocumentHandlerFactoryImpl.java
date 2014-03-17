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
package org.apache.jetspeed.page.document.psml;

import org.apache.jetspeed.page.document.DocumentHandler;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.DocumentTypeAlreadyRegisteredException;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.UnsupportedDocumentTypeException;
import org.apache.jetspeed.util.ArgUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * DocumentHandlerFactoryImpl
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class DocumentHandlerFactoryImpl implements DocumentHandlerFactory
{
    private Map<String,DocumentHandler> handlers;

    private boolean permissionsEnabled;

    private boolean constraintsEnabled;


    /**
     *  
     */
    public DocumentHandlerFactoryImpl( Map<String,DocumentHandler> handlers )
    {
        super();
        
        ArgUtil.assertNotNull(Map.class, handlers, this);        
        
        this.handlers = handlers;        

        // register this with handlers
        for (DocumentHandler handler : handlers.values())
        {
            handler.setHandlerFactory(this);
        }
    }
    
    public DocumentHandlerFactoryImpl()
    {
        this(new HashMap<String,DocumentHandler>());
    }

    /**
     * <p>
     * getDocumentHandler
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.DocumentHandlerFactory#getDocumentHandler(java.lang.String)
     * @param documentType
     * @return
     */
    public DocumentHandler getDocumentHandler( String documentType ) throws UnsupportedDocumentTypeException
    {        
        if(handlers.containsKey(documentType))
        {
            return handlers.get(documentType);
        }
        else
        {
            throw new UnsupportedDocumentTypeException("There are no DocumentHandlers defined for the type: "+documentType);
        }
    }

    /**
     * <p>
     * registerDocumentHandler
     * </p>
     *
     * @see org.apache.jetspeed.page.document.DocumentHandlerFactory#registerDocumentHandler(org.apache.jetspeed.page.document.DocumentHandler)
     * @param documentHandler
     * @throws DocumentTypeAlreadyRegisteredException
     */
    public void registerDocumentHandler( DocumentHandler documentHandler ) throws DocumentTypeAlreadyRegisteredException
    {
        if(handlers.containsKey(documentHandler.getType()))
        {
            throw new DocumentTypeAlreadyRegisteredException(documentHandler.getType()+" has already been registered.");
        }

        // register handler and this with handlers
        documentHandler.setHandlerFactory(this);
        handlers.put(documentHandler.getType(), documentHandler);
    }

    /**
     * <p>
     * getDocumentHandlerForPath
     * </p>
     *
     * @see org.apache.jetspeed.page.document.DocumentHandlerFactory#getDocumentHandlerForPath(java.lang.String)
     * @param documentPath
     * @return
     */
    public DocumentHandler getDocumentHandlerForPath( String documentPath ) throws UnsupportedDocumentTypeException
    {
        int dotIndex = documentPath.indexOf('.');
        
        if(dotIndex > -1)
        {
            try
            {
                return getDocumentHandler(documentPath.substring(dotIndex));
            }
            catch (UnsupportedDocumentTypeException e)
            {
                int lastSlash = documentPath.lastIndexOf(Node.PATH_SEPARATOR);
                if(lastSlash < 0)
                {
                    lastSlash = 0;
                }
                return getDocumentHandler(documentPath.substring(lastSlash));
            }
        }
        else
        {
            throw new UnsupportedDocumentTypeException("The path provided has no extension and may be a folder.");
        }
    }

    /**
     * <p>
     * getPermissionsEnabled
     * </p>
     *
     * @see org.apache.jetspeed.page.document.DocumentHandlerFactory#getPermissionsEnabled()
     * @return
     */
    public boolean getPermissionsEnabled()
    {
        return permissionsEnabled;
    }

    /**
     * <p>
     * setPermissionsEnabled
     * </p>
     *
     * @see org.apache.jetspeed.page.document.DocumentHandlerFactory#setPermissionsEnabled(boolean)
     * @return
     */
    public void setPermissionsEnabled(boolean enabled)
    {
        permissionsEnabled = enabled;
    }

    /**
     * <p>
     * getConstraintsEnabled
     * </p>
     *
     * @see org.apache.jetspeed.page.document.DocumentHandlerFactory#getConstraintsEnabled()
     * @return
     */
    public boolean getConstraintsEnabled()
    {
        return constraintsEnabled;
    }

    /**
     * <p>
     * setConstraintsEnabled
     * </p>
     *
     * @see org.apache.jetspeed.page.document.DocumentHandlerFactory#setConstraintsEnabled(boolean)
     * @return
     */
    public void setConstraintsEnabled(boolean enabled)
    {
        constraintsEnabled = enabled;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.DocumentHandlerFactory#shutdown()
     */
    public void shutdown()
    {
        // propagate to handlers        
        for (DocumentHandler handler : handlers.values())
        {
            handler.shutdown();
        }
    }    
}
