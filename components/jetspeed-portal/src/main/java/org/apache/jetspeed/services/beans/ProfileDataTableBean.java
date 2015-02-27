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
package org.apache.jetspeed.services.beans;

import org.apache.jetspeed.profiler.rules.ProfilingRule;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * DTO for user search result list to be displayed in the view. The object will
 * be transformed to JSON to be transfered to the JS client.
 * 
 * @author <a href="mailto:david@bluesunrise.com">David S Taylor</a>
 * 
 */
@XmlRootElement(name = "data")
public class ProfileDataTableBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private long recordsReturned = 5;
	private long totalRecords;
	private long startIndex = 5;
	private String sort = "userName";
	private String dir = "asc";
	private long pageSize = 5;
	//private List<HashMap<String, String>> records = null;
	private List<ProfileListBean> records = new ArrayList<>();

	public ProfileDataTableBean() {
	}

	public ProfileDataTableBean(Collection<ProfilingRule> rules) {
		totalRecords = rules.size();
		for (ProfilingRule rule : rules) {
			ProfileListBean record = new ProfileListBean(rule.getId(), rule.getTitle(), rule.getClassname());
			this.records.add(record);
		}
	}

	/**
	 * @return the recordsReturned
	 */
	public long getRecordsReturned() {
		return recordsReturned;
	}

	/**
	 * @param recordsReturned
	 *            the recordsReturned to set
	 */
	public void setRecordsReturned(long recordsReturned) {
		this.recordsReturned = recordsReturned;
	}

	/**
	 * @return the totalRecords
	 */
	public long getTotalRecords() {
		return totalRecords;
	}

	/**
	 * @param totalRecords
	 *            the totalRecords to set
	 */
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}

	/**
	 * @return the startIndex
	 */
	public long getStartIndex() {
		return startIndex;
	}

	/**
	 * @param startIndex
	 *            the startIndex to set
	 */
	public void setStartIndex(long startIndex) {
		this.startIndex = startIndex;
	}

	/**
	 * @return the sort
	 */
	public String getSort() {
		return sort;
	}

	/**
	 * @param sort
	 *            the sort to set
	 */
	public void setSort(String sort) {
		this.sort = sort;
	}

	/**
	 * @return the dir
	 */
	public String getDir() {
		return dir;
	}

	/**
	 * @param dir
	 *            the dir to set
	 */
	public void setDir(String dir) {
		this.dir = dir;
	}

	/**
	 * @return the pageSize
	 */
	public long getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize
	 *            the pageSize to set
	 */
	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return the records
	 */
	public List<ProfileListBean> getRecords() {
		return records;
	}

	/**
	 * @param records
	 *            the records to set
	 */
	public void setRecords(List<ProfileListBean> records) {
		this.records = records;
	}

}
