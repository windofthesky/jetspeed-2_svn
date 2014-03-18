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
package org.apache.jetspeed.serializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.apache.jetspeed.serializer.objects.JSSnapshot;

/**
 * Base class for JetspeedComponentSerializer implementations
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public abstract class AbstractJetspeedComponentSerializer implements JetspeedComponentSerializer
{
    public void deleteData(Map<String,Object> settings) throws SerializerException
    {
        deleteData(settings, (Logger)settings.get(JetspeedSerializer.KEY_LOGGER));
    }

    public void processExport(JetspeedSerializedData data, Map<String,Object> settings) throws SerializerException
    {
        processExport((JSSnapshot)data, settings, (Logger)settings.get(JetspeedSerializer.KEY_LOGGER));
    }

    public void processImport(JetspeedSerializedData data, Map<String,Object> settings) throws SerializerException
    {
        processImport((JSSnapshot)data, settings, (Logger)settings.get(JetspeedSerializer.KEY_LOGGER));
    }

    protected abstract void deleteData(Map<String,Object> settings, Logger log) throws SerializerException;

    protected abstract void processExport(JSSnapshot data, Map<String,Object> settings, Logger log) throws SerializerException;

    protected abstract void processImport(JSSnapshot data, Map<String,Object> settings, Logger log) throws SerializerException;
    
    /**
     * returns if the key for a particular setting is true. False if the key
     * doesn't exist.
     * 
     * @param key
     * @return
     */
    protected static boolean isSettingSet(Map<String,Object> settings, String key)
    {
        if ( settings != null )
        {
            Object o = settings.get(key);
            if ( o != null && o instanceof Boolean )
            {
                return ((Boolean)o).booleanValue();
            }
        }
        return false;
    }

    /**
     * convert a list of elements in a string, seperated by ',' into an
     * arraylist of strings
     * 
     * @param _line
     *            Strinbg containing one or more elements seperated by ','
     * @return list of elements of null
     */
    protected static final List<String> getTokens(String _line)
    {
        if ((_line == null) || (_line.length() == 0))
            return null;

        StringTokenizer st = new StringTokenizer(_line, ",");
        List<String> list = new ArrayList<String>();

        while (st.hasMoreTokens())
            list.add(st.nextToken());
        return list;
    }
}
