/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.picocontainer.defaults.DefaultPicoContainer
import org.picocontainer.ComponentAdapter
import org.picocontainer.defaults.ConstructorComponentAdapter
import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
import org.picocontainer.defaults.ComponentParameter
import org.hsqldb.jdbcDriver
import org.apache.jetspeed.components.jndi.JNDIComponent
import org.apache.jetspeed.components.jndi.TyrexJNDIComponent
import org.apache.jetspeed.components.datasource.BoundDBCPDatasourceComponent
import org.apache.jetspeed.components.datasource.DatasourceComponent
import org.apache.commons.pool.impl.GenericObjectPool
import org.apache.jetspeed.components.persistence.store.ojb.OJBTypeIntializer
// import org.apache.jetspeed.components.persistence.store.ojb.otm.OTMStoreImpl
import org.apache.jetspeed.components.persistence.store.ojb.pb.PBStore
import org.apache.jetspeed.components.persistence.store.impl.DefaultPersistenceStoreContainer
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer

import org.apache.jetspeed.capability.Capabilities
import org.apache.jetspeed.capability.impl.JetspeedCapabilities

import java.io.File
import java.util.Properties

// create the root container
container = new DefaultPicoContainer()

// This JNDI component helps us publish the datasource
Class jndiClass = JNDIComponent
JNDIComponent jndiImpl = new TyrexJNDIComponent()
container.registerComponentInstance(jndiClass, jndiImpl)

Class dsClass = DatasourceComponent
String url = System.getProperty("org.apache.jetspeed.database.url")
String driver = System.getProperty("org.apache.jetspeed.database.driver")
String user = System.getProperty("org.apache.jetspeed.database.user")
String password = System.getProperty("org.apache.jetspeed.database.password")

if(url != null)
{
    container.registerComponentInstance(dsClass, new BoundDBCPDatasourceComponent(user, password, driver, url, 20, 5000, GenericObjectPool.WHEN_EXHAUSTED_GROW, true, "jetspeed", jndiImpl))
}

//
// Persistence
PersistenceContainer pContainer = new DefaultPersistenceStoreContainer(300000, 10000)
Class pContainerClass = Class.forName("org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer")

// Parameter[] storeParams = new Parameter[] {new ConstantParameter("jetspeed")}



// ComponentAdapter ca = new ConstructorComponentAdapter("jetspeed", OTMStoreClass, new Parameter[] {new ConstantParameter("jetspeed")})
ComponentAdapter ca = new ConstructorComponentAdapter("jetspeed", PBStore, new Parameter[] {new ConstantParameter("jetspeed")})

pContainer.registerComponent(ca)

container.registerComponentInstance(pContainerClass, pContainer);

//
// Capabilities
//
container.registerComponentInstance(Capabilities, new JetspeedCapabilities(pContainer))

return container