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
import org.apache.jetspeed.cache.file.FileCache

// create the root container
container = new DefaultPicoContainer()

Long scanRate = 10
cacheSize = 20
if(parent != null)
{
	parent.registerComponentInstance(FileCache, new FileCache(scanRate, cacheSize))
}

container.registerComponentInstance(FileCache, new FileCache(scanRate, cacheSize))




return container