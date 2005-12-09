/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.portals.applications.transform.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.apache.portals.applications.util.Streams;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * TransformDTDEntityResolver
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TransformDTDEntityResolver implements EntityResolver
{
    private Map dtds;
    
    public TransformDTDEntityResolver(Map dtds)
    {
        this.dtds = dtds;
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
     */
    public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException, IOException
    {        
        try 
        {
            // System.out.println("TSER: ( " + publicId  + " Taking " + systemId + " from cache");
                            
            byte[] dtd = (byte[])dtds.get(systemId);
            if (dtd == null)
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                URL url = new URL(systemId);
                Streams.drain(url.openStream(), baos);
                dtd = baos.toByteArray();
                synchronized(dtds)
                {
                    dtds.put(systemId, dtd);
                }                    
            }
            
            if (dtd != null)
            {
                ByteArrayInputStream bais = new ByteArrayInputStream(dtd);
                InputSource is = new InputSource(bais);
                is.setPublicId( publicId );
                is.setSystemId( systemId );
                                    
                return is;
            }
           
        } 
        catch(Throwable t ) // java.io.IOException x  
        {
            t.printStackTrace();
        }
            
        return null;
        
    }
}
