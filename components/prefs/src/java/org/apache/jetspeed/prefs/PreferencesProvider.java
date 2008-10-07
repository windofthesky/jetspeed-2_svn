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

import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;

/**
 * <p>Utility component used to pass the {@link PersistenceStoreContainer} and
 * store name to the {@link Preferences} SPI implementation.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface PreferencesProvider
{

    /**
     * <p>Getter for the {@link PersistenceStoreContainer}.</p>
     * @return The PersistenceStoreContainer.
     */
    PersistenceStoreContainer getStoreContainer();

    /**
     * <p>Setter for the store key name.</p>
     * @return The store key name.
     */
    String getStoreKeyName();

}
