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
package org.apache.jetspeed.serializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javolution.xml.XMLBinding;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.engine.JetspeedEngineConstants;
import org.apache.jetspeed.serializer.objects.JSSnapshot;

public abstract class JetspeedSerializerBase
{

    /** Logger */
    protected static final Log log = LogFactory.getLog(JetspeedSerializer.class);

    private ComponentManager cm = null;
    private Object sem = new Object();

    int refCouter = 0;

    /** the main wrapper class for an XML file */
    private JSSnapshot snapshot;

    /** processing flags */
    /** export/import instructions */

    private HashMap processSettings = new HashMap();

    private boolean initialized = false;

    /** current indent for XML files - defaults to tab */
    private String currentIndent = null;

    private static String ENCODING_STRING = "JETSPEED 2.1 - 2006";
    private static String JETSPEED = "JETSPEED";
    
    protected final ComponentManager getCM()
    {
        if (cm == null)
        {
            cm = Jetspeed.getComponentManager();
        }
    	return cm;
    }
    
    public JetspeedSerializerBase()
    {
    }

    
    
    /**
     * hand over existing component manager
     * 
     * @param cm
     */
    public JetspeedSerializerBase(ComponentManager cm)
    {
        this.setComponentManager(cm);
        this.initialized = true;
    }

    /**
     * This constructor takes the application root, the search path for the boot
     * component configuration files and the search path for the application
     * component configuration files.
     * <p>
     * For example: new JetspeedSerializerSecondaryImpl("./", "assembly/boot/*.xml",
     * "assembly/*.xml") will establish the current directory as the root,
     * process all xml files in the assembly/boot directory before processing
     * all xml files in the assembly directory itself.
     * 
     * @param appRoot
     *            working directory
     * @param bootConfig
     *            boot (primary) file or files (wildcards are allowed)
     * @param appConfig
     *            application (secondary) file or files (wildcards are allowed)
     */
    public JetspeedSerializerBase(String appRoot, String[] bootConfig,
            String[] appConfig) throws SerializerException
    {
        this.initializeComponentManager(appRoot, bootConfig, appConfig);
        this.initialized = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedSerializer#nitializeComponentManager(String,String[],String[])
     */
    public final void initializeComponentManager(String appRoot, String[] bootConfig,
            String[] appConfig) throws SerializerException
    {

    	
    	
    	if (this.initialized)
            throw new SerializerException(
                    SerializerException.COMPONENT_MANAGER_EXISTS.create(""));
        SpringComponentManager cm = new SpringComponentManager(bootConfig,
                appConfig, appRoot);
        cm.start();
        Configuration properties = new PropertiesConfiguration();
        properties.setProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY,
                appRoot);
        this.setComponentManager(cm);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedSerializer#setComponentManager(ComponentManager)
     */
    public final void setComponentManager(ComponentManager cm)
    {
        this.cm = cm;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedSerializer#closeUp()
     */
    public final void closeUp()
    {
        if (cm != null) cm.stop();
        cm = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedSerializer#setDefaultIndent(String)
     */
    public final void setDefaultIndent(String indent)
    {
        this.currentIndent = indent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedSerializer#getDefaultIndent()
     */
    public final String getDefaultIndent()
    {
        return this.currentIndent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedSerializer#importData(String,
     *      Map)
     */
    public final  void importData(String importFileName, Map settings)
            throws SerializerException
    {
        if (cm == null)
        {
            cm = Jetspeed.getComponentManager();
        }        
        /** pre-processing homework... */
        XMLBinding binding = new XMLBinding();
        setupAliases(binding);
        checkSettings(settings);
        setSnapshot(readFile(importFileName, binding));
        if (getSnapshot() == null)
            throw new SerializerException(
                    SerializerException.FILE_PROCESSING_ERROR
                            .create(new String[]
                            { importFileName, "Snapshot is NULL"}));

        if (!(getSnapshot().checkVersion()))
            throw new SerializerException(
                    SerializerException.INCOMPETIBLE_VERSION
                            .create(new String[]
                            {
                                    importFileName,
                                    String.valueOf(getSnapshot()
                                            .getSoftwareVersion()),
                                    String.valueOf(getSnapshot()
                                            .getSavedSubversion())}));

        /** ok, now we have a valid snapshot and can start processing it */

        /** ensure we can work undisturbed */
        synchronized (sem)
        {
            logMe("*********Reading data*********");
            this.processImport();
        }
        return;
    }

   
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedSerializer#exportData(String,String,Map)
     */
    public final void exportData(String name, String exportFileName, Map settings)
            throws SerializerException
    {
        /** pre-processing homework... */
        XMLBinding binding = new XMLBinding();
        setupAliases(binding);
        checkSettings(settings);
        if (cm == null)
        {
            cm = Jetspeed.getComponentManager();
        }
        /** ensure we can work undisturbed */
        synchronized (sem)
        {
            /** get the snapshot construct */
            this.processExport(name, binding);
            XMLObjectWriter writer = openWriter(exportFileName);
            writer.setBinding(binding);

            if (this.getDefaultIndent() != null)
                writer.setIndentation(this.getDefaultIndent());

            try
            {
                logMe("*********Writing data*********");
                writer.write(getSnapshot(),  getSerializerDataTag(),
                		getSerializerDataClass());

            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.FILE_PROCESSING_ERROR
                                .create(new String[]
                                { exportFileName, e.getMessage()}));
            } finally
            {
                /** ensure the writer is closed */
                try
                {
                    logMe("*********closing up********");
                    writer.close();
                } catch (Exception e)
                {
                    logMe("Error in closing writer " + e.getMessage());
                    /**
                     * don't do anything with this exception - never let the
                     * bubble out of the finally block
                     */
                }
            }
        }
        return;
    }

    /**
     * create a backup of the current environment in case the import fails
     * 
     */
    protected final void doBackupOfCurrent(String importFileName, Map currentSettings)
    {
        // TODO: HJB create backup of current content
    }

    /**
     * read a snapshot and return the reconstructed class tree
     * 
     * @param importFileName
     * @throws SerializerException
     */

    /**
     * read a snapshot and return the reconstructed class tree
     * 
     * @param importFileName
     * @throws SerializerException
     */

    protected final  JSSnapshot readFile(String importFileName, XMLBinding binding)
            throws SerializerException
    {
        XMLObjectReader reader = null;
        JSSnapshot snap = null;
        try
        {
            reader = XMLObjectReader.newInstance(new FileInputStream(
                    importFileName));
        } catch (Exception e)
        {
            throw new SerializerException(SerializerException.FILE_READER_ERROR
                    .create(new String[]
                    { importFileName, e.getMessage()}));
        }
        try
        {
            if (binding != null) reader.setBinding(binding);
            snap = (JSSnapshot) reader.read(this.getSerializerDataTag(),
            		getSerializerDataClass());

        } catch (Exception e)
        {
            e.printStackTrace();
            new SerializerException(SerializerException.FILE_PROCESSING_ERROR
                    .create(new String[]
                    { importFileName, e.getMessage()}));
        } finally
        {
            /** ensure the reader is closed */
            try
            {
                logMe("*********closing up reader ********");
                reader.close();
            } catch (Exception e1)
            {
                logMe("Error in closing reader " + e1.getMessage());
                /**
                 * don't do anything with this exception - never let the bubble
                 * out of the finally block
                 */
                return null;
            }
        }
        return snap;
    }   
    /**
     * create or open a given file for writing
     */
    protected final  XMLObjectWriter openWriter(String filename)
            throws SerializerException
    {
        File f;

        try
        {
            f = new File(filename);
        } catch (Exception e)
        {
            throw new SerializerException(
                    SerializerException.FILE_PROCESSING_ERROR
                            .create(new String[]
                            { filename, e.getMessage()}));
        }
        boolean exists = f.exists();

        if (exists)
        {
            if (!(this.getSetting(JetspeedSerializer.KEY_OVERWRITE_EXISTING)))
                throw new SerializerException(
                        SerializerException.FILE_ALREADY_EXISTS
                                .create(filename));
            if (this.getSetting(JetspeedSerializer.KEY_BACKUP_BEFORE_PROCESS))
            {
                String backName = createUniqueBackupFilename(f.getName());
                if (backName == null)
                    throw new SerializerException(
                            SerializerException.FILE_BACKUP_FAILED
                                    .create(filename));
                File ftemp = new File(backName);
                f.renameTo(ftemp);
            }
        }
        try
        {
            XMLObjectWriter writer = XMLObjectWriter
                    .newInstance(new FileOutputStream(filename));
            return writer;
        } catch (Exception e)
        {
            throw new SerializerException(SerializerException.FILE_WRITER_ERROR
                    .create(new String[]
                    { filename, e.getMessage()}));
        }
    }

    /**
     * returns the key for a particular process setting. False if the key
     * doesn't exist.
     * 
     * @param key
     * @return
     */
    public final boolean getSetting(String key)
    {
        Object o = processSettings.get(key);
        if ((o == null) || (!(o instanceof Boolean))) return false;
        return ((Boolean) o).booleanValue();
    }

    /**
     * set a process setting for a given key
     * 
     * @param key
     *            instruction to set
     * @param value
     *            true or false
     */
    protected final void setSetting(String key, boolean value)
    {
        processSettings.put(key, (value ? Boolean.TRUE : Boolean.FALSE));
    }


    /**
     * set instruction flags to new settings
     * 
     * @param settings
     */
    protected final void checkSettings(Map settings)
    {
        /** ensure we don't have settings from a previous run */
        resetSettings();
        /** process the new isntructionSet */
        if ((settings == null) || (settings.size() == 0)) return;
        Iterator _it = settings.keySet().iterator();
        while (_it.hasNext())
        {
            try
            {
                String key = (String) _it.next();
                Object o = settings.get(key);
                if ((o != null) && (o instanceof Boolean))
                    setSetting(key, ((Boolean) o).booleanValue());
            } catch (Exception e)
            {
                log.error("checkSettings", e);
            }
        }
    }

    /**
     * On import, get the basic SnapShot data
     * 
     */
    protected void getSnapshotData()
    {
        logMe("date created : " + getSnapshot().getDateCreated());
        logMe("software Version : " + getSnapshot().getSavedVersion());
        logMe("software SUbVersion : " + getSnapshot().getSavedSubversion());
    }

    /**
     * On export, set the basic SnapShot data
     * 
     */
    protected void setSnapshotData()
    {
    	java.util.Date d1 = new java.util.Date();
        Date d = new Date(d1.getTime());
        getSnapshot().setDateCreated(d.toString());
        getSnapshot().setSavedVersion(getSnapshot().getSoftwareVersion());
        getSnapshot().setSavedSubversion(getSnapshot().getSoftwareSubVersion());
    }



 
    /**
     * simple lookup for object from a map
     * @param map
     * @param _fullPath
     * @return
     */
    protected final Object getObjectBehindPath(Map map, String _fullPath)
    {
        return map.get(_fullPath);
    }

    
    /**
     * ++++++++++++++++++++++++++++++HELPERS
     * +++++++++++++++++++++++++++++++++++++++++++++
     */

    /**
     * remove a given sequence from the beginning of a string
     */
    protected final String removeFromString(String base, String excess)
    {
        return base.replaceFirst(excess, "").trim();
    }

    /**
     * 
     * just a Simple helper to make code more readable
     * 
     * @param text
     */
    protected final void logMe(String text)
    {
        if (log.isDebugEnabled()) 
            log.debug(text);
    }

    /**
     * Helper routine to create a unique filename for a backup of an existing
     * filename....not intended to be rocket science...
     * 
     * @param name
     * @return
     */
    protected final String createUniqueBackupFilename(String name)
    {
        String newName = name + ".bak";

        File f = new File(newName);
        int counter = 0;
        if (!(f.exists())) return newName;
        while (counter < 100)
        {
            String newName1 = newName + counter;
            if (!(new File(newName1).exists())) return newName1;
            counter++;
        }
        return null;
    }

 /**
  * convert a list of elements in a string, seperated by ',' into an arraylist of strings
  * @param _line Strinbg containing one or more elements seperated by ','
  * @return list of elements of null
  */    
    protected final ArrayList getTokens(String _line)
    {
        if ((_line == null) || (_line.length() == 0)) return null;

        StringTokenizer st = new StringTokenizer(_line, ",");
        ArrayList list = new ArrayList();

        while (st.hasMoreTokens())
            list.add(st.nextToken());
        return list;
    }


    protected final String recreatePassword(char[] savedPassword)
	{
		if (savedPassword == null)
			return null;
		return new String(savedPassword);
	}

    
    /**
     * reset instruction flags to default settings (all true)
     * 
     */
    protected abstract void resetSettings();

    
    /**
     * The workhorse for exporting data
     * 
     * @param binding
     *            established XML binding
     * @return
     * @throws SerializerException
     */
    protected abstract void processExport(String name, XMLBinding binding)
            throws SerializerException; 

    /**
     * The workhorse for importing data
     * 
     * @param binding
     *            established XML binding
     * @return
     * @throws SerializerException
     */
    protected abstract  void processImport() throws SerializerException;

    /**
     * Setup the binding for the different classes, mapping each extracted class
     * to a unique tag name in the XML
     * 
     * @param binding
     */
    protected abstract void setupAliases(XMLBinding binding);

    
	/**
	 * return the class for the serializer data , for example JSSeedData.class)
	 * 
	 * @return
	 */    
    protected abstract Class getSerializerDataClass();


	/**
	 * return the XML tag for the serializer data , for example "JSSnapShot")
	 * 
	 * @return
	 */    
    protected abstract String getSerializerDataTag();

	public JSSnapshot getSnapshot()
	{
		return snapshot;
	}



	public void setSnapshot(JSSnapshot snapshot)
	{
		this.snapshot = snapshot;
	}

}
