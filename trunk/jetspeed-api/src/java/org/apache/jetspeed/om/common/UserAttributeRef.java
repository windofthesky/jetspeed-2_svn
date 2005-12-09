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
package org.apache.jetspeed.om.common;

import java.io.Serializable;

/**
 * <p>Interface allowing mapping of user attributes between the portal implementation
 * and the portlet attribute definition according to the Portlet specs (PLT.17.2 Accessing
 * User Attributes).  This is a Jetspeed 2 specific extension that allows to map a user-attribute
 * name used in the portlet to a user attribute name-link used in the portal implementation.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 *
 */
public interface UserAttributeRef extends Serializable
{
    /** Getter for the user-attribute-ref name. */
    String getName();
    
    /** Setter for the user-attribute-ref name. */
    void setName(String name);
    
    /** Getter for the user-attribute-ref name-link. */
    String getNameLink();
    
    /** Setter for the user-attribute-ref name-link. */
    void setNameLink(String nameLink);
    
    /** Getter for the user-attribute description. */
    String getDescription();
    
    /** Setter for the user-attribute description. */
    void setDescription(String description);

}
