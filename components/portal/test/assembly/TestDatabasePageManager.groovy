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
import org.apache.jetspeed.idgenerator.JetspeedIdGenerator
import org.apache.jetspeed.page.impl.DatabasePageManager
import org.apache.jetspeed.components.ComponentAssemblyTestCase

// create the root container
container = new DefaultPicoContainer()

//
// ID Generator
//
idgenerator = new JetspeedIdGenerator()
container.registerComponentInstance("IdGenerator", idgenerator)


//
// Persistence Store Service
//
// TODO: get persistence store service

//
// Page Manager
//
container.registerComponentInstance("DatabasePageManager", 
                                     new DatabasePageManager(null, idgenerator, null))

return container
