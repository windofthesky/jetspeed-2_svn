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
package org.apache.jetspeed.statistics.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.jetspeed.statistics.UserStats;

/**
 * UserStatsImpl
 * 
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class UserStatsImpl implements UserStats
{
    private static final long serialVersionUID = 1L;

    private String username;

    private int numberOfSessions;
    
    private String ipAddress;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.UserStats#getNumberOfSessions()
     */
    public int getNumberOfSessions()
    {
        return numberOfSessions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.UserStats#getUsername()
     */
    public String getUsername()
    {

        return username;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.UserStats#setNumberOfSession(int)
     */
    public void setNumberOfSession(int number)
    {
        numberOfSessions = number;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.statistics.UserStats#setUsername(java.lang.String)
     */
    public void setUsername(String username)
    {
        this.username = username;

    }
    
    public String getIpAddress()
    {
        return ipAddress;
    }

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.statistics.UserStats#getInetAddress()
	 */
	public InetAddress getInetAddress() {
		try
        {
            return InetAddress.getByName(ipAddress);
        }
        catch (UnknownHostException e)
        {
        }
        return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.statistics.UserStats#setInetAddress(java.net.InetAddress)
	 */
	public void setInetAddress(InetAddress inetAddress) {
		this.ipAddress = inetAddress.getHostAddress();
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.statistics.UserStats#setInetAddressFromIp(java.lang.String)
	 */
	public void setInetAddressFromIp(String ipAddress) throws UnknownHostException {
		this.ipAddress = ipAddress;
	}

	/**
	 * Checks whether these two object match. Simple check for
	 * just the ipaddresse and username.
	 * 
	 * @param Object instanceof UserStats
	 */
	public boolean equals(Object obj) {
		
		if(!(obj instanceof UserStats))
			return false;
		
		UserStats userstat = (UserStats)obj;
		return this.ipAddress.equals(userstat.getIpAddress()) && this.username.equals(userstat.getUsername());
	}
}
