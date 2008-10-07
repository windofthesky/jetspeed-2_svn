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

import org.apache.jetspeed.components.ChildAwareContainer
import org.apache.jetspeed.cache.file.FileCache
import org.apache.jetspeed.cache.file.impl.BaseFileCache


scanRate =  Long.parseLong(System.getProperty("org.apache.jetspeed.file_cache.scan_rate","120"))
cacheSize = Integer.parseInt(System.getProperty("org.apache.jetspeed.file_cache.cache_size","100"))

initialCapacity =  Integer.parseInt(System.getProperty("org.apache.jetspeed.file_cache.initial_capacity","-1"))
loadFactor = Integer.parseInt(System.getProperty("org.apache.jetspeed.file_cache.load_factor","-1"))

if(initialCapacity != -1 && loadFactor != -1)
{
	FileCache fileCache = new BaseFileCache(initialCapacity, loadFactor, scanRate, cacheSize)
}
else
{
	FileCache fileCache = new BaseFileCache(scanRate, cacheSize)
}

return fileCache