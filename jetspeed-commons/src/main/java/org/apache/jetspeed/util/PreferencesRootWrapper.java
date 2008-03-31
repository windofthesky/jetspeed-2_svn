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
package org.apache.jetspeed.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

/**
 * PreferencesRootWrapper is a lightweight wrapper around the Jetspeed persistent PreferencesImpl to allow
 * restarting the Jetspeed Portal.
 * <p>
 * As the (Sun) Java Preferences implementation only creates a PreferencesFactory instance *once* per JVM
 * (as static final), reloading the Jetspeed Portal (using a new classloader) requires a wrapper solution
 * to prevent ClassCastExceptions and/or out-of-sync kept proxies and caches.
 * </p>
 * <p>
 * As a newly created Jetspeed Portal classloader can no longer cast a previous Preferences root to its
 * own PreferencesImpl, a "trick" is used by also implementing the Observer interface (which is provided by
 * the Java system classloader). The Observer interface is used because it is very lightweight and allows
 * passing an Object instance through its update method. That update method is used to "inject" the newly
 * created Preferences root instance. 
 * </p>
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PreferencesRootWrapper extends Preferences implements Observer
{
    private Preferences root;
    
    public String absolutePath()
    {
        return root.absolutePath();
    }

    public void addNodeChangeListener(NodeChangeListener ncl)
    {
        root.addNodeChangeListener(ncl);
    }

    public void addPreferenceChangeListener(PreferenceChangeListener pcl)
    {
        root.addPreferenceChangeListener(pcl);
    }

    public String[] childrenNames() throws BackingStoreException
    {
        return root.childrenNames();
    }

    public void clear() throws BackingStoreException
    {
        root.clear();
    }

    public boolean equals(Object obj)
    {
        return root.equals(obj);
    }

    public void exportNode(OutputStream os) throws IOException, BackingStoreException
    {
        root.exportNode(os);
    }

    public void exportSubtree(OutputStream os) throws IOException, BackingStoreException
    {
        root.exportSubtree(os);
    }

    public void flush() throws BackingStoreException
    {
        root.flush();
    }

    public String get(String key, String def)
    {
        return root.get(key, def);
    }

    public boolean getBoolean(String key, boolean def)
    {
        return root.getBoolean(key, def);
    }

    public byte[] getByteArray(String key, byte[] def)
    {
        return root.getByteArray(key, def);
    }

    public double getDouble(String key, double def)
    {
        return root.getDouble(key, def);
    }

    public float getFloat(String key, float def)
    {
        return root.getFloat(key, def);
    }

    public int getInt(String key, int def)
    {
        return root.getInt(key, def);
    }

    public long getLong(String key, long def)
    {
        return root.getLong(key, def);
    }

    public int hashCode()
    {
        return root.hashCode();
    }

    public boolean isUserNode()
    {
        return root.isUserNode();
    }

    public String[] keys() throws BackingStoreException
    {
        return root.keys();
    }

    public String name()
    {
        return root.name();
    }

    public Preferences node(String pathName)
    {
        return root.node(pathName);
    }

    public boolean nodeExists(String pathName) throws BackingStoreException
    {
        return root.nodeExists(pathName);
    }

    public Preferences parent()
    {
        return root.parent();
    }

    public void put(String key, String value)
    {
        root.put(key, value);
    }

    public void putBoolean(String key, boolean value)
    {
        root.putBoolean(key, value);
    }

    public void putByteArray(String key, byte[] value)
    {
        root.putByteArray(key, value);
    }

    public void putDouble(String key, double value)
    {
        root.putDouble(key, value);
    }

    public void putFloat(String key, float value)
    {
        root.putFloat(key, value);
    }

    public void putInt(String key, int value)
    {
        root.putInt(key, value);
    }

    public void putLong(String key, long value)
    {
        root.putLong(key, value);
    }

    public void remove(String key)
    {
        root.remove(key);
    }

    public void removeNode() throws BackingStoreException
    {
        root.removeNode();
    }

    public void removeNodeChangeListener(NodeChangeListener ncl)
    {
        root.removeNodeChangeListener(ncl);
    }

    public void removePreferenceChangeListener(PreferenceChangeListener pcl)
    {
        root.removePreferenceChangeListener(pcl);
    }

    public void sync() throws BackingStoreException
    {
        root.sync();
    }

    public String toString()
    {
        return root.toString();
    }

    public void update(Observable o, Object arg)
    {
        root = (Preferences)arg;
    }
}
