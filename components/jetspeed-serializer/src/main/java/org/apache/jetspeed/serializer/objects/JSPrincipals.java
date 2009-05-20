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
package org.apache.jetspeed.serializer.objects;

import java.util.ArrayList;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;


/**
 * Simple wrapper class for XML serialization
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id$
 */
public class JSPrincipals extends ArrayList<JSPrincipal>
{
    private static final long serialVersionUID = -5698435742048612881L;
    
    public JSPrincipals()
    {
    }
    
    /***************************************************************************
     * SERIALIZER
     */
    private static final XMLFormat XML = new XMLFormat(JSPrincipals.class)
    {

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
            try
            {
                JSPrincipals g = (JSPrincipals) o;
                
                for (JSPrincipal p : g)
                {
                    xml.add(p, "Principal", JSPrincipal.class);
                }
            } 
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void read(InputElement xml, Object o)
        {

            try
            {
                JSPrincipals g = (JSPrincipals) o;
                
                while (xml.hasNext())
                {
                    JSPrincipal elem = (JSPrincipal) xml.get("Principal", JSPrincipal.class);
                    g.add(elem);
                }
            } 
            catch (Exception e)
            {
                /**
                 * while annoying invalid entries in the file should be
                 * just disregarded
                 */
                e.printStackTrace();
            }
        }
    };
}