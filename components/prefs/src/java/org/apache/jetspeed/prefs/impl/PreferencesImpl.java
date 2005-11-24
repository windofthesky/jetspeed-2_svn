/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.prefs.FailedToCreateNodeException;
import org.apache.jetspeed.prefs.NodeAlreadyExistsException;
import org.apache.jetspeed.prefs.NodeDoesNotExistException;
import org.apache.jetspeed.prefs.PreferencesProvider;
import org.apache.jetspeed.prefs.PropertyException;
import org.apache.jetspeed.prefs.om.Node;
import org.apache.jetspeed.prefs.om.Property;
import org.apache.jetspeed.prefs.om.PropertyKey;
import org.apache.jetspeed.prefs.om.impl.PropertyImpl;
import org.apache.jetspeed.prefs.om.impl.PropertyKeyImpl;

/**
 * <p>S
 * {@link Preferences}implementation relying on Jetspeed OJB based persistence
 * plugin.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class PreferencesImpl extends AbstractPreferences
{

    /** User <tt>Preferences<tt> node type. */
    public static final int USER_NODE_TYPE = 0;

    /** System <tt>Preferences</tt> node type. */
    public static final int SYSTEM_NODE_TYPE = 1;

    /** The current <code>Node</code> object. */
    private Node node = null;

    /** Logger. */
    private static final Log log = LogFactory.getLog(PreferencesImpl.class);


    protected static PreferencesProvider prefsProvider;

    static PreferencesImpl systemRoot;
    static PreferencesImpl userRoot;
   

    /**
     * <p>
     * Constructs a root node in the underlying datastore if they have not yet
     * been created.
     * </p>
     * <p>
     * Logs a warning if the underlying datastore is unavailable.
     * </p>
     * 
     * @param parent
     *            The parent object.
     * @param nodeName
     *            The node name.
     * @param nodeType
     *            The node type.
     */
    public PreferencesImpl( PreferencesImpl parent, String nodeName, int nodeType ) throws IllegalStateException
    {
        super(parent, nodeName);
        
        try
        {
            if (parent != null)
            {
                this.node = prefsProvider.createNode(parent.getNode(), nodeName, nodeType, this.absolutePath());
            }
            else
            {
                this.node = prefsProvider.createNode(null, nodeName, nodeType, this.absolutePath());
            }

            newNode = true;
        }
        catch (FailedToCreateNodeException e)
        {
            IllegalStateException ise = new IllegalStateException("Failed to create new Preferences of type "
                    + nodeType + " for path " + this.absolutePath());
            ise.initCause(e);
            throw ise;
        }
        catch (NodeAlreadyExistsException e)
        {
            try
            {
                node = prefsProvider.getNode(this.absolutePath(), nodeType);
                newNode = false;
            }
            catch (NodeDoesNotExistException e1)
            {
                // If we get this at this point something is very wrong
                IllegalStateException ise = new IllegalStateException("Unable to create node for Preferences of type "+nodeType+" for path "+this.absolutePath() +
                               ".  If you see this exception at this, it more than likely means that the Preferences backing store is corrupt.");
                ise.initCause(e1);
                throw ise;
            }            
        }

    }

    /**
     * @see java.util.prefs.Preferences#childrenNamesSpi()
     */
    public String[] childrenNamesSpi() throws BackingStoreException
    {
        Collection nodes = prefsProvider.getChildren(getNode());

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

    /**
     * @see java.util.prefs.Preferences#childSpi(java.lang.String)
     */
    public AbstractPreferences childSpi( String name )
    {
        return new PreferencesImpl(this, name, node.getNodeType());
    }

    /**
     * @see java.util.prefs.Preferences#flushSpi()
     */
    public void flushSpi() throws BackingStoreException
    {
        prefsProvider.storeNode(this.node);
    }

    /**
     * @see java.util.prefs.Preferences#getSpi(java.lang.String)
     */
    public String getSpi( String key )
    {

        Collection properties = node.getNodeProperties();

        for (Iterator i = properties.iterator(); i.hasNext();)
        {
            Property curProp = (Property) i.next();
            if (curProp.getPropertyKey().getPropertyKeyName().equals(key))
            {
                return curProp.getPropertyValue(curProp.getPropertyKey().getPropertyKeyType());
            }
        }

        //prop not found
        return null;
    }

    /**
     * @see java.util.prefs.Preferences#keysSpi()
     */
    public String[] keysSpi()
    {
        ArrayList propertyNames = new ArrayList();

        Collection propCol = node.getNodeProperties();
        if ((null != propCol) && propCol.size() > 0)
        {
            for (Iterator j = propCol.iterator(); j.hasNext();)
            {
                Property curprop = (Property) j.next();
                propertyNames.add(curprop.getPropertyKey().getPropertyKeyName());
            }
        }    

        return (String[]) propertyNames.toArray(new String[propertyNames.size()]);
    }

    /**
     * @see java.util.prefs.Preferences#putSpi(java.lang.String,
     *      java.lang.String)
     *      <p>
     *      In addition to java.util.prefs.Preferences, this implementation is
     *      enforcing that node used as property sets have been defined as such
     *      and that only the keys defined associated to the property set can be
     *      added as properties of the current node.
     *      </p>
     */
    public void putSpi( String key, String value )
    {
      
        Collection nodeKeys = node.getNodeKeys();
        Collection properties = node.getNodeProperties();
        if (null == properties)
        {
            log.error("Could not retrieve node property: [key: " + key + ", value:" + value + "]");
            return;
        }

        boolean foundProp = false;
        boolean foundKey = false;
        // First if the property exists, update its value.
        for (Iterator i = properties.iterator(); i.hasNext();)
        {
            Property curProp = (Property) i.next();
            if (curProp.getPropertyKey().getPropertyKeyName().equals(key))
            {
                foundProp = true;
                foundKey = true;
                if (log.isDebugEnabled())
                    log.debug("Update existing property: [" + key + ", " + value + "]");

                curProp.setPropertyValue(curProp.getPropertyKey().getPropertyKeyType(), value);
                curProp.setModifiedDate(new Timestamp(System.currentTimeMillis()));
            }

        }
        // The property does not already exist. Create a new property, if
        // the property key exits and is associated to this node.
        if (prefsProvider.isPropertyManagerEnabled() && !foundProp)
        {
            for (Iterator i = nodeKeys.iterator(); i.hasNext();)
            {
                PropertyKey curpk = (PropertyKey) i.next();
                if (curpk.getPropertyKeyName().equals(key))
                {
                    foundKey = true;
                    if (log.isDebugEnabled())
                        log.debug("New property value: [" + key + ", " + value + "]");

                    properties.add(new PropertyImpl(node.getNodeId(), curpk.getPropertyKeyId(), curpk, curpk
                            .getPropertyKeyType(), value));
                }
            }
        }
        else if (!prefsProvider.isPropertyManagerEnabled() && !foundProp)
        {
            foundKey = true;
            PropertyKey pKey = new PropertyKeyImpl(key, Property.STRING_TYPE);
            properties.add(new PropertyImpl(node.getNodeId(), pKey.getPropertyKeyId(), pKey, pKey
                    .getPropertyKeyType(), value));

        }

        if (!foundKey)
        {
            if (log.isWarnEnabled())
                log.warn(PropertyException.PROPERTYKEY_NOT_FOUND);
            return;
        }
       
        prefsProvider.storeNode(node);

    }

    /**
     * @see java.util.prefs.Preferences#removeNodeSpi()
     */
    public void removeNodeSpi() throws BackingStoreException
    {      
        Node parentNode = null;
        Preferences parent = parent();
        if ( parent != null && parent instanceof PreferencesImpl )
        {
            parentNode = ((PreferencesImpl)parent).getNode();
        }
        prefsProvider.removeNode(parentNode, node);
    }

    /**
     * @see java.util.prefs.Preferences#removeSpi(java.lang.String)
     */
    public void removeSpi( String key )
    {
        Collection properties = node.getNodeProperties();

        for (Iterator i = properties.iterator(); i.hasNext();)
        {
            Property curProp = (Property) i.next();

            if ((curProp.getPropertyKey().getPropertyKeyName().equals(key)))
            {
                i.remove();
            }
        }
        
        // Update node.
        prefsProvider.storeNode(node);
    }

    /**
     * @see java.util.prefs.Preferences#syncSpi()
     */
    public void syncSpi() throws BackingStoreException
    {
        flushSpi();
    }

    /**
     * 
     * <p>
     * getNode
     * </p>
     * 
     * @return
     */
    public Node getNode()
    {
        return node;
    }
    
    /**
     * 
     * <p>
     * setPreferencesProvider
     * </p>
     * Sets the <code>org.apache.jetspeed.prefs.PreferencesProvider</code> that
     * will support backing store operations for all <code>PreferencesImpls</code>
     * @param prefsProvider
     */
    public static void setPreferencesProvider(PreferencesProvider prefsProvider)
    {
        PreferencesImpl.prefsProvider = prefsProvider;
    }
}