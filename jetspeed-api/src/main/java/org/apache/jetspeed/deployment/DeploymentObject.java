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
package org.apache.jetspeed.deployment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * DeploymentObject
 * </p>
 * <p> 
 *   Object representation of a deployment artifact of some type.
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface DeploymentObject
{

	/**
	 * Closes any resources that may have been opend during the use
	 * of this ObjectHandler.
	 * @throws IOException
	 */
	void close() throws IOException;
	
	/**
	 * retreives the the configuration for this deployment artifact
	 * based on the artifact-relative <code>configPath</code>
	 * provided.
	 * @param configPath artifact-relative path to the confiuration file
	 * @return Configuration of this artificat or <code>null</code> if the 
	 * configuration is not present in the artifact.
	 * @throws IOException error opening the configuration
	 */
	InputStream getConfiguration(String configPath) throws IOException;
	/**
	 * 
	 * <p>
	 * getName
	 * </p>
	 *
	 * @return name of the deployment object.  Yeilds the same result as if you were
	 * to invoke: <code>new java.io.File(getPath()).getName()</code>
	 */
	String getName();
	
	/**
	 * 
	 * <p>
	 * getPath
	 * </p>
	 *
	 * @return path the deployment object's source directory or jar/war file.
	 */
	String getPath();
	
	/**
	 * 
   * @return the underlying File object
	 */
	File getFile();
}
