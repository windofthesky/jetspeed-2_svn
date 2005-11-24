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
package org.apache.jetspeed.prefs;

import java.util.Collection;
import java.util.prefs.Preferences;

import org.apache.jetspeed.prefs.om.Node;

/**
 * <p>Utility component used to pass the {@link PersistenceStoreContainer} and
 * store name to the {@link Preferences} SPI implementation.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface PreferencesProvider
{    
    boolean isPropertyManagerEnabled();
    
    Node getNode( String fullPath, int nodeType ) throws NodeDoesNotExistException;
    
    boolean nodeExists( String fullPath, int nodeType );
    
    Node createNode( Node parent, String nodeName, int nodeType, String fullPath )
    throws FailedToCreateNodeException, NodeAlreadyExistsException;
    
    Collection getChildren(Node parentNode);
    
    void storeNode(Node node);
    
    void removeNode(Node parentNode, Node node);
    
    /**
     * Lookup a preference node given the preference name, a property name and value.
     * Options can be set to null if you dont want them included in the query.
     * 
     * @param nodeName the name of the node to lookup, such as 'userinfo'
     * @param propertyName the name of the property, such as 'user.email'
     * @param propertyValue the value of the property, such as 'taylor@apache.org'
     * @return a collection of found matching elements of type <code>Node</code>
     */
    Collection lookupPreference(String nodeName, String propertyName, String propertyValue);

    void init() throws Exception;
}
