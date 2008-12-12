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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

/**
 * Node Information
 * 
 * @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
 * @version
 */
public class NodeInformationImpl implements NodeInformation, Serializable 
{
	static final long serialVersionUID = -598265530537353219L;

	private Long revision;
	private String contextName;
	private Date lastDeployDate = null;
	private static final int CompressVersion = 1;

	/**
	 * default class construtor required for bean management
	 *
	 */
	public NodeInformationImpl()
	{}
	
	/**
	 * extensible serialization routine - indicates the version written to allow for later structural updates
	 *
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeByte(CompressVersion);
		out.writeLong(revision.longValue());
		out.writeUTF(contextName);
		if (lastDeployDate == null)
			out.writeByte(0);
		else
		{
			out.writeByte(1);
			out.writeLong(lastDeployDate.getTime());
		}
	}
	/**
	 * extensible serialization routine 
	 * using the version byte code can identify older versions and handle updates correctly
	 *
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException
	{
		in.readByte();
		// do changes here if version dependant

		revision = new Long(in.readLong());
		contextName = in.readUTF();
		int dateSet = in.readByte();
		
		if (dateSet == 1)
			lastDeployDate = new Date(in.readLong());
		else
			lastDeployDate = null;
	}

	public boolean equals(Object object)
	{
		if (object == this)
			return true;
		if (!(object instanceof NodeInformation))
			return false;
		return equals((NodeInformation) object);
	}

	public int compareTo(Object object)
	{
		if (object == null)
			return 1;
		if (object == this)
			return 0;
		if (!(object instanceof NodeInformation))
			return 1;
		return compareTo((NodeInformation) object);
	}

	public final boolean equals(NodeInformation object)
	{
		if (object == null)
			return false;
		return object.getContextName().equalsIgnoreCase(contextName);
	}

	public final int compareTo(NodeInformation object)
	{
		return getContextName().compareToIgnoreCase(contextName);
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("revision= " + this.revision.longValue());
		buffer.append("; contextName= " + this.getContextName());
		buffer.append("; lastDeployDate= " + this.getContextName());
		if (this.lastDeployDate != null)
		{
			DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
			try
			{
				buffer.append(format.format(this.lastDeployDate));
			} catch (Exception e)
			{
				buffer.append("<invalidDate>");
			}
		} else
			buffer.append("<empty>");

		return buffer.toString();
	}

	public String getContextName()
	{
		return contextName;
	}

	public void setContextName(String contextName)
	{
		this.contextName = contextName;
	}

	public Long getRevision()
	{
		return revision;
	}

	public void setRevision(Long revision)
	{
		this.revision = revision;
	}

	public void setRevision(long revision)
	{
		this.revision = new Long(revision);
	}

	public Date getLastDeployDate()
	{
		return lastDeployDate;
	}

	public void setLastDeployDate(Date lastDeployDate)
	{
		this.lastDeployDate = lastDeployDate;
	}
}
