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
package org.apache.jetspeed.prefs;

import java.util.Collection;
import java.util.prefs.Preferences;

import org.apache.jetspeed.prefs.om.Node;
import org.apache.jetspeed.prefs.om.Property;

/**
 * <p>
 * Utility component used to pass the {@link PersistenceStoreContainer} and
 * store name to the {@link Preferences} SPI implementation.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface PreferencesProvider
{
    /**
     * Given the fullpath to a node, retrieve the node associated with the node path
     * 
     * @param fullPath the full path to the node such as "/portlet_entity/dp-1/guest/preferences/mypref"
     * @param nodeType either System or User node type. A value of 0 is User, a value of 1 is System
     * @return The Preference Node found when found
     * @throws NodeDoesNotExistException when a node is not found, an exception is thrown
     */
    Node getNode(String fullPath, int nodeType) throws NodeDoesNotExistException;

    /**
     * Check for the existence of a node given the full path to the node
     * 
     * @param fullPath  the full path to the node such as "/portlet_entity/dp-1/guest/preferences/mypref"
     * @param nodeType  either System or User node type. A value of 0 is User, a value of 1 is System
     * @return true if the node exists, false if it does not exist
     */
    boolean nodeExists(String fullPath, int nodeType);

    /**
     * Create a preferences node given the following parameters. Will throw an exception if the node already exists.
     * 
     * @param parent the existing parent node of this node to be created
     * @param nodeName the name of the node, which should be the same value as the last value of the full path
     *                 for example when the full path is "/portlet_entity/dp-1", the nodeName will be "dp-1"
     * @param nodeType either System or User node type. A value of 0 is User, a value of 1 is System
     * @param fullPath  the full path to the node such as "/portlet_entity/dp-1/guest/preferences/mypref"
     * @return the newly created node on success
     * @throws FailedToCreateNodeException thrown when the node fails to create
     * @throws NodeAlreadyExistsException thrown when a node already exists at the given full path
     */
    Node createNode(Node parent, String nodeName, int nodeType, String fullPath) throws FailedToCreateNodeException,
            NodeAlreadyExistsException;

    /**
     * Create a property on the given node. 
     * @param node the node to have a property added to it
     * @param name the name of the property to add to the node
     * @param value the value of the property to add to the node
     * @return the newly created property
     * @since 2.1.2
     */
    Property createProperty(Node node, String name, Object value);
    
    /**
     * Given a parent node, return a flat collection of immediate children of this node
     * 
     * @param parentNode the parent node to be searched for children
     * @return a Java collection of immediate children of this node
     */
    Collection getChildren(Node parentNode);

    /**
     * Stores a preference node to the backing preferences persistent storage.
     * If the node does not exist, it is created. If it does exist, the node 
     * is updated.
     * 
     * @param node the node to be stored.
     */
    void storeNode(Node node);

    /**
     * Removes a node from a given parent node, also removing the node from the preferences persistence store.
     * 
     * @param parentNode the parent of the node to be deleted
     * @param node the node to be deleted
     */
    void removeNode(Node parentNode, Node node);

    /**
     * Lookup a preference node given the preference name, a property name and
     * value. Options can be set to null if you dont want them included in the
     * query.
     * 
     * @param nodeName the name of the node to lookup, such as 'userinfo'
     * @param propertyName the name of the property, such as 'user.email'
     * @param propertyValue the value of the property, such as
     *            'taylor@apache.org'
     * @return a collection of found matching elements of type <code>Node</code>
     */
    Collection lookupPreference(String nodeName, String propertyName, String propertyValue);

    /**
     * Initializes the preferences node by executing configured preloads.
     * 
     * @throws Exception
     */
    void preload() throws Exception;

    /**
     * Preload preferences for specified portlet application.
     * 
     * @param portletApplicationName application name
     * @throws NodeDoesNotExistException
     */
    void preloadApplicationPreferences(String portletApplicationName) throws NodeDoesNotExistException;
    
    /**
     * Clear cached preferences for specified portlet application.
     * 
     * @param portletApplicationName application name
     * @throws NodeDoesNotExistException
     */
    void clearCachedApplicationPreferences(String portletApplicationName);
}
