/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.maven.plugins.db.ddlutils.io;

import org.xml.sax.InputSource;

/**
 * DatabaseIO extension to override the betwixt mapping.xml to allow overriding ddlutils 1.0 behavior at runtime for 
 * https://issues.apache.org/jira/browse/DDLUTILS-75 which is fixed in ddlutils 1.1 but hasn't been released yet.
 * @version $Id$
 *
 */
public class DatabaseIO extends org.apache.ddlutils.io.DatabaseIO {

	public DatabaseIO() {
	}

    /**
     * Returns the commons-betwixt mapping file as an {@link org.xml.sax.InputSource} object.
     * Per default, this will be classpath resource under the path <code>/mapping.xml</code>.
     *  
     * @return The input source for the mapping
     */
    protected InputSource getBetwixtMapping()
    {
        return new InputSource(getClass().getResourceAsStream("mapping.xml"));
    }
}
