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
package org.apache.jetspeed.prefs.impl;

import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.Preferences;
import java.util.prefs.PreferenceChangeListener;

import org.apache.commons.codec.binary.Base64;
import org.apache.jetspeed.prefs.FailedToCreateNodeException;
import org.apache.jetspeed.prefs.NodeAlreadyExistsException;
import org.apache.jetspeed.prefs.NodeDoesNotExistException;
import org.apache.jetspeed.prefs.om.Node;
import org.apache.jetspeed.prefs.om.Property;
import org.apache.jetspeed.prefs.om.impl.PropertyImpl;

/**
 * PreferencesImpl
 * 
 * transient non-caching implementation relying on Jetspeed OJB based
 * persistence plugin
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 */
public class PreferencesImpl extends Preferences
{
    /** User <tt>Preferences<tt> node type. */
    public static final int USER_NODE_TYPE = 0;

    /** System <tt>Preferences</tt> node type. */
    public static final int SYSTEM_NODE_TYPE = 1;

    private String path;
    private PreferencesProviderWrapper ppw;
    private int type;
    private boolean removed;
    
    /**
     * Construct Java preferences transient view of persistent Jetspeed
     * preferences that will not be cached.
     * 
     * @param ppw preferences provider
     * @param path absolute path
     * @param type user or system type
     */
    public PreferencesImpl(PreferencesProviderWrapper ppw, String path, int type)
    {
        this.ppw = ppw;
        this.path = path;
        this.type = type;
        this.removed = false;
        // get/create persistent node
        getNode();
    }        

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#put(java.lang.String, java.lang.String)
     */
    public void put(String key, String value)
    {
        // check removed state
        if (removed)
        {
            throw new IllegalStateException("Preferences node removed");
        }
        // get persistent node and properties
        Node node = getNode();
        Collection properties = node.getNodeProperties();
        // if the property exists, update its value.
        boolean propFound = false;
        for (Iterator i = properties.iterator(); i.hasNext();)
        {
            Property curProp = (Property) i.next();
            if ((null != curProp) && (null != curProp.getPropertyName()) && curProp.getPropertyName().equals(key))
            {
                propFound = true;
                curProp.setPropertyValue(value);
                curProp.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                break;
            }
        }
        // add new property value
        if (!propFound)
        {
            properties.add(new PropertyImpl(node.getNodeId(), key, value));
        }
        // update node
        ppw.provider().storeNode(node);
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#get(java.lang.String, java.lang.String)
     */
    public String get(String key, String def)
    {
        // check removed state
        if (removed)
        {
            throw new IllegalStateException("Preferences node removed");
        }
        // get persistent node and properties
        Node node = getNode();
        Collection properties = node.getNodeProperties();
        // return value
        for (Iterator i = properties.iterator(); i.hasNext();)
        {
            Property curProp = (Property) i.next();
            if ((null != curProp) && (null != curProp.getPropertyName()) && (curProp.getPropertyName().equals(key)))
            {
                return curProp.getPropertyValue();
            }
        }
        return def;
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#remove(java.lang.String)
     */
    public void remove(String key)
    {
        // check removed state
        if (removed)
        {
            throw new IllegalStateException("Preferences node removed");
        }
        // get persistent node and properties
        Node node = getNode();
        Collection properties = node.getNodeProperties();
        // remove property if found
        boolean removed = false;
        for (Iterator i = properties.iterator(); i.hasNext();)
        {
            Property curProp = (Property) i.next();
            if ((curProp.getPropertyName().equals(key)))
            {
                i.remove();
                removed = true;
                break;
            }
        }
        // update node if removed
        if (removed)
        {
            ppw.provider().storeNode(node);
        }
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#keys()
     */
    public String[] keys()
    {
        // check removed state
        if (removed)
        {
            throw new IllegalStateException("Preferences node removed");
        }
        // get persistent node and properties
        Node node = getNode();
        Collection properties = node.getNodeProperties();
        // extract property names from nod
        ArrayList propertyNames = new ArrayList();
        if ((null != properties) && properties.size() > 0)
        {
            for (Iterator j = properties.iterator(); j.hasNext();)
            {
                Property curprop = (Property) j.next();
                if ((null != curprop) && (null != curprop.getPropertyName()) && !propertyNames.contains(curprop.getPropertyName()))
                {
                    propertyNames.add(curprop.getPropertyName());
                }
            }
        }
        return (String[]) propertyNames.toArray(new String[propertyNames.size()]);
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#childrenNames()
     */
    public String[] childrenNames()
    {
        // check removed state
        if (removed)
        {
            throw new IllegalStateException("Preferences node removed");
        }
        // get persistent node and child nodes
        Node node = getNode();
        Collection nodes = ppw.provider().getChildren(node);
        // get child node names
        if (null != nodes)
        {
            ArrayList childrenNames = new ArrayList(nodes.size());
            for (Iterator i = nodes.iterator(); i.hasNext();)
            {
                Node curnode = (Node) i.next();
                childrenNames.add(curnode.getNodeName());
            }
            return (String[]) childrenNames.toArray(new String[0]);
        }
        else
        {
            // The returned array is of size zero if this node has no
            // preferences.
            return new String[0];
        }
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#removeNode()
     */
    public void removeNode()
    {
        // check removed state
        if (removed)
        {
            throw new IllegalStateException("Preferences node removed");
        }
        // check for removal of root node
        if (path.equals("/"))
        {
            throw new UnsupportedOperationException("Root preferences node cannot be removed");
        }
        // get persistent node and parent
        Node node = getNode(path, type, false);
        if (node != null)
        {
            Node parentNode = getNode(parentPath(path), type, false);
            if (parentNode != null)
            {
                // remove persistent node and children
                removeNodeAndChildren(parentNode, node);
            }
        }
        // flag preference as removed
        removed = true;
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#nodeExists(java.lang.String)
     */
    public boolean nodeExists(String path)
    {
        // check removed state
        if (removed)
        {
            throw new IllegalStateException("Preferences node removed");
        }
        // construct absolute path if necessary
        if (path.charAt(0) != '/')
        {
            if (!this.path.equals("/"))
            {
                path = this.path+"/"+path;
            }
            else
            {
                path = "/"+path;                
            }
        }
        // check to see if persistent node exists
        return (getNode(path, type, false) != null);
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#sync()
     */
    public void sync()
    {
    }

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#flush()
     */
    public void flush()
    {
    }

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#parent()
     */
    public Preferences parent()
    {
        // check removed state
        if (removed)
        {
            throw new IllegalStateException("Preferences node removed");
        }
        // construct parent preference
        if (!path.equals("/"))
        {
            return new PreferencesImpl(ppw, parentPath(path), type);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#node(java.lang.String)
     */
    public Preferences node(String path)
    {
        // check removed state
        if (removed)
        {
            throw new IllegalStateException("Preferences node removed");
        }
        // construct absolute path if necessary
        if (path.charAt(0) != '/')
        {
            if (!this.path.equals("/"))
            {
                path = this.path+"/"+path;
            }
            else
            {
                path = "/"+path;                
            }
        }
        // return new preference
        return new PreferencesImpl(ppw, path, type);
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#name()
     */
    public String name()
    {
        // get name from path
        return pathName(path);
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#absolutePath()
     */
    public String absolutePath()
    {
        // return path
        return path;
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#isUserNode()
     */
    public boolean isUserNode()
    {
        // test type
        return (type == USER_NODE_TYPE);
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#clear()
     */
    public void clear()
    {
        // remove all keys
        String[] keys = keys();
        for (int i = 0; (i < keys.length); i++)
        {
            remove(keys[i]);
        }
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#addPreferenceChangeListener(java.util.prefs.PreferenceChangeListener)
     */
    public void addPreferenceChangeListener(PreferenceChangeListener pcl)
    {
        // change listeners not supported
        throw new UnsupportedOperationException();
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#removePreferenceChangeListener(java.util.prefs.PreferenceChangeListener)
     */
    public void removePreferenceChangeListener(PreferenceChangeListener pcl)
    {
        // change listeners not supported
        throw new UnsupportedOperationException();
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#addNodeChangeListener(java.util.prefs.NodeChangeListener)
     */
    public void addNodeChangeListener(NodeChangeListener ncl)
    {
        // change listeners not supported
        throw new UnsupportedOperationException();
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#removeNodeChangeListener(java.util.prefs.NodeChangeListener)
     */
    public void removeNodeChangeListener(NodeChangeListener ncl)
    {
        // change listeners not supported
        throw new UnsupportedOperationException();
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#putInt(java.lang.String, int)
     */
    public void putInt(String key, int value)
    {
        put(key, Integer.toString(value));
    }

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#getInt(java.lang.String, int)
     */
    public int getInt(String key, int def)
    {
        try
        {
            String value = get(key, null);
            if (value != null)
            {
                return Integer.parseInt(value);
            }
        }
        catch (NumberFormatException nfe)
        {
        }
        return def;
    }

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#putLong(java.lang.String, long)
     */
    public void putLong(String key, long value)
    {
        put(key, Long.toString(value));
    }

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#getLong(java.lang.String, long)
     */
    public long getLong(String key, long def)
    {
        try
        {
            String value = get(key, null);
            if (value != null)
            {
                return Long.parseLong(value);
            }
        }
        catch (NumberFormatException nfe)
        {
        }
        return def;
    }

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#putBoolean(java.lang.String, boolean)
     */
    public void putBoolean(String key, boolean value)
    {
        put(key, String.valueOf(value));
    }

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#getBoolean(java.lang.String, boolean)
     */
    public boolean getBoolean(String key, boolean def)
    {
        String value = get(key, null);
        if (value != null)
        {
            if (value.equalsIgnoreCase("true"))
            {
                return true;
            }
            else if (value.equalsIgnoreCase("false"))
            {
                return false;
            }
        }
        return def;
    }

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#putFloat(java.lang.String, float)
     */
    public void putFloat(String key, float value)
    {
        put(key, Float.toString(value));
    }

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#getFloat(java.lang.String, float)
     */
    public float getFloat(String key, float def)
    {
        try
        {
            String value = get(key, null);
            if (value != null)
            {
                return Float.parseFloat(value);
            }
        }
        catch (NumberFormatException nfe)
        {
        }
        return def;
    }

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#putDouble(java.lang.String, double)
     */
    public void putDouble(String key, double value)
    {
        put(key, Double.toString(value));
    }

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#getDouble(java.lang.String, double)
     */
    public double getDouble(String key, double def)
    {
        try
        {
            String value = get(key, null);
            if (value != null)
            {
                return Double.parseDouble(value);
            }
        }
        catch (NumberFormatException nfe)
        {
        }
        return def;
    }

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#putByteArray(java.lang.String, byte[])
     */
    public void putByteArray(String key, byte[] value)
    {
        put(key, new String(Base64.encodeBase64(value)));
    }

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#getByteArray(java.lang.String, byte[])
     */
    public byte[] getByteArray(String key, byte[] def)
    {
        String value = get(key, null);
        if (value != null)
        {
            return Base64.decodeBase64(value.getBytes());
        }
        return def;
    }

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#exportNode(java.io.OutputStream)
     */
    public void exportNode(OutputStream os)
    {
        // export not supported
        throw new UnsupportedOperationException();        
    }
    
    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#exportSubtree(java.io.OutputStream)
     */
    public void exportSubtree(OutputStream os)
    {
        // export not supported
        throw new UnsupportedOperationException();        
    }

    /* (non-Javadoc)
     * @see java.util.prefs.Preferences#toString()
     */
    public String toString()
    {
        // return string representation of preference node
        return (isUserNode() ? "User" : "System")+" Preference Node: "+path;
    }
    
    /**
     * Get preferences persistence provider.
     * 
     * @return preferences provider
     */
    public PreferencesProviderWrapper getProvider()
    {
        return ppw;
    }
    
    /**
     * Get or create persistent preference node for this preferences node.
     * 
     * @return persistent preference node
     */
    private Node getNode()
    {
        return getNode(path, type, true);
    }
    
    /**
     * Get or create persistent preference node.
     * 
     * @param path absolute path
     * @param type user or system type
     * @param create flag to enable node creation on access
     * @return persistent preference node
     */
    private Node getNode(String path, int type, boolean create)
    {
        // access node
        Node node = null;
        try
        {
            node = ppw.provider().getNode(path, type);
        }
        catch (NodeDoesNotExistException e1)
        {
            // create if required and does not exist
            if (create)
            {
                try
                {
                    // create node and set new
                    String parentPath = parentPath(path);
                    String name = pathName(path);
                    if (parentPath.length() > 0)
                    {
                        node = ppw.provider().createNode(getNode(parentPath, type, true), name, type, path);
                    }
                    else
                    {
                        node = ppw.provider().createNode(null, name, type, path);
                    }
                }
                catch (FailedToCreateNodeException e)
                {
                    IllegalStateException ise = new IllegalStateException("Failed to create new Preferences of type " + type + " for path " + path);
                    ise.initCause(e);
                    throw ise;
                }
                catch (NodeAlreadyExistsException e)
                {
                    // retry to create node in case just created
                    try
                    {
                        node = ppw.provider().getNode(path, type);
                    }
                    catch (NodeDoesNotExistException e2)
                    {
                        // If we get this at this point something is very wrong
                        IllegalStateException ise = new IllegalStateException("Unable to create node for Preferences of type " + type + " for path " + path + ". " +
                                                                              "If you see this exception at this, it more than likely means that the Preferences backing store is corrupt.");
                        ise.initCause(e2);
                        throw ise;
                    }
                }
            }
        }
        return node;
    }
    
    /**
     * Recursively remove node and all child nodes.
     * 
     * @param parentNode parent of node to remove
     * @param node preference node to remove
     */
    private void removeNodeAndChildren(Node parentNode, Node node)
    {
        Collection nodes = ppw.provider().getChildren(node);
        if (null != nodes)
        {
            for (Iterator i = nodes.iterator(); i.hasNext();)
            {
                Node childNode = (Node) i.next();
                removeNodeAndChildren(node, childNode);                    
            }
        }
        ppw.provider().removeNode(parentNode, node);
    }
    
    /**
     * Utility to compute parent path from node path.
     * 
     * @param path absolute node path
     * @return parent node path
     */
    private static String parentPath(String path)
    {
        int lastIndex = path.lastIndexOf('/');
        if (lastIndex > 0)
        {
            return path.substring(0, lastIndex);
        }
        return "/";
    }
    
    /**
     * Utility to extract node name from node path.
     * 
     * @param path absolute node path
     * @return node name
     */
    private static String pathName(String path)
    {
        return path.substring(path.lastIndexOf('/')+1);
    }
}