/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security;

import java.util.List;

/**
 * A container for principals that have been retrieved from storage with ranged
 * queries. Additional to the principals itself it also contains the total size
 * of the query result.
 * 
 * @author <a href="mailto:joachim@wemove.com">Joachim Mueller</a>
 * 
 */
public class JetspeedPrincipalResultList {

	private long totalSize = 0;

	private List<? extends JetspeedPrincipal> results = null;

	public JetspeedPrincipalResultList(List<? extends JetspeedPrincipal> results, long totalSize) {
		this.results = results;
		this.totalSize = totalSize;
	}

	/**
	 * Creates a result list, <em>totalSize</em> will be set to the lists size.
	 * 
	 * @param results
	 */
	public JetspeedPrincipalResultList(List<? extends JetspeedPrincipal> results) {
		this.results = results;
		this.totalSize = results.size();
	}

	/**
	 * Get the total size of search results. This can be higher than the number
	 * of returned principals.
	 * 
	 * @return
	 */
	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	/**
	 * Get the principal results.
	 * 
	 * @return
	 */
	public List<? extends JetspeedPrincipal> getResults() {
		return results;
	}
}
