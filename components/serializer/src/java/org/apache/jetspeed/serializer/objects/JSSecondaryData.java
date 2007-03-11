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

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringEscapeUtils;

public class JSSecondaryData extends JSSnapshot
{

    public static final int softwareVersion = 1;

    public static final int softwareSubVersion = 0;

     private String encryption;


    private JSApplications applications;


    /**
     * check the software version and subvversion against the saved
     * version...and verify whether it is compatible...
     * 
     * @return the current software can process this file
     */
    public boolean checkVersion()
    {
        return true;
    }
    
    /**
     * @return Returns the softwareSubVersion.
     */
    public int getSoftwareSubVersion()
    {
        return softwareSubVersion;
    }

    /**
     * @return Returns the softwareVersion.
     */
    public int getSoftwareVersion()
    {
        return softwareVersion;
    }


    public JSSecondaryData()
    {
    	super();
        System.out.println("JSSecondaryData Class created");
    }

    public JSSecondaryData(String name)
    {
        super();
        
        applications = new JSApplications();
    }

 
    /***************************************************************************
     * SERIALIZER
     */
    protected static final XMLFormat XML = new XMLFormat(JSSecondaryData.class)
    {

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
        	
            try
            {

                JSSnapshot.XML.write(o,xml);

                JSSecondaryData g = (JSSecondaryData) o;
                
                
                /** implicitly named (through binding) fields here */

                xml.add(g.getApplications()); 
                

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void read(InputElement xml, Object o)
        {
            try
            {
            	JSSnapshot.XML.read(xml, o); // Calls parent read.
                JSSecondaryData g = (JSSecondaryData) o;

                while (xml.hasNext())
                {
                    Object o1 = xml.getNext(); // mime

                    if (o1 instanceof JSApplications)
                        g.applications = (JSApplications) o1;
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

 
    

    /**
     * @param applications
     *            The applications to set.
     */
    public void setApplications(JSApplications applications)
    {
        this.applications = applications;
    }

	public JSApplications getApplications()
	{
		return applications;
	}


}
