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

import org.apache.commons.lang.StringEscapeUtils;

import javolution.xml.XMLBinding;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

public abstract class JSSnapshot
{
		

	    private String name;

	    private int savedVersion;

	    private int savedSubversion;

	    private String dateCreated;

	    private String dataSource;


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

	    public JSSnapshot()
	    {
	        System.out.println(this.getClass().getName() + " created");
	    }

	    public JSSnapshot(String name)
	    {
	        this.name = name;
	    }

	 


	    /**
	     * @return Returns the name.
	     */
	    public final String getName()
	    {
	        return name;
	    }


	    /**
	     * @return Returns the softwareSubVersion.
	     */
	    public abstract int getSoftwareSubVersion();

	    /**
	     * @return Returns the softwareVersion.
	     */
	    public abstract int getSoftwareVersion();

	    /**
	     * @return Returns the dataSource.
	     */
	    public final String getDataSource()
	    {
	        return dataSource;
	    }

	    /**
	     * @param dataSource
	     *            The dataSource to set.
	     */
	    public final  void setDataSource(String dataSource)
	    {
	        this.dataSource = dataSource;
	    }

	    /**
	     * @return Returns the dateCreated.
	     */
	    public final  String getDateCreated()
	    {
	        return dateCreated;
	    }

	    /**
	     * @param dateCreated
	     *            The dateCreated to set.
	     */
	    public final  void setDateCreated(String dateCreated)
	    {
	        this.dateCreated = dateCreated;
	    }


	    /**
	     * @return Returns the savedSubversion.
	     */
	    public final  int getSavedSubversion()
	    {
	        return savedSubversion;
	    }

	    /**
	     * @param savedSubversion
	     *            The savedSubversion to set.
	     */
	    public final  void setSavedSubversion(int savedSubversion)
	    {
	        this.savedSubversion = savedSubversion;
	    }

	    /**
	     * @return Returns the savedVersion.
	     */
	    public final  int getSavedVersion()
	    {
	        return savedVersion;
	    }

	    /**
	     * @param savedVersion
	     *            The savedVersion to set.
	     */
	    public final  void setSavedVersion(int savedVersion)
	    {
	        this.savedVersion = savedVersion;
	    }

	    /**
	     * @param name
	     *            The name to set.
	     */
	    public final  void setName(String name)
	    {
	        this.name = name;
	    }



    /***************************************************************************
     * SERIALIZER
     */
    protected static final XMLFormat XML = new XMLFormat(JSSnapshot.class)
    {

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
        	
            try
            {
                JSSnapshot g = (JSSnapshot) o;

                /** attributes here */

                xml.setAttribute("name", g.getName());

                /** named fields HERE */

                xml.add(String.valueOf(g.getSoftwareVersion()),
                        "softwareVersion");
                xml.add(String.valueOf(g.getSoftwareSubVersion()),
                        "softwareSubVersion");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void read(InputElement xml, Object o)
        {
            try
            {
                JSSnapshot g = (JSSnapshot) o;
                g.name = StringEscapeUtils.unescapeHtml(xml.getAttribute("name", "unknown"));
                Object o1 = xml.get("softwareVersion",String.class);
                if (o1 instanceof String)
                    g.savedVersion = Integer.parseInt(((String) o1));
                o1 = xml.get("softwareSubVersion",String.class);
                if (o1 instanceof String)
                    g.savedSubversion = Integer.parseInt(((String) o1));
           } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

 
}
