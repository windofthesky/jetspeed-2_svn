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
package org.apache.jetspeed.cluster;

/**
 * Node Manager Interface
 *
 * @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
 * @version 
 */
public interface NodeManager
{

	public static final int INVALID_NODE_REQUEST = -1;
	public static final int NODE_SAVED = 0;
	public static final int NODE_OUTDATED = 1;
	public static final int NODE_NEW = 2;

	/**
	 * Returns the current "knowledge" about a given node (i.e. the portlet application).
	 * If the contextName doesn't exist NODE_NEW is returned.
	 * An revision requested newer than what is stored is indicated by NODE_OUTDATED.
     *
	 * @param revision
	 * @param contextName
	 * @return
	 */
	public int checkNode(Long revision, String contextName);

	/**
	 * Add a new node or update the revision of an existing one...(i.e. the portlet application) to the local info
	 * @param revision
	 * @param contextName
	 * @throws Exception
	 */
	public void addNode(Long revision, String contextName) throws Exception;

	/**
	 * return the number of currently stored nodes
	 * @return
	 */
	public int getNumberOfNodes();

	/**
	 * Remove a node
	 * @param contextName
	 * @throws Exception
	 */
	public void removeNode(String contextName) throws Exception;

}
