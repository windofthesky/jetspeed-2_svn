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

    private String username;

    private int numberOfSessions;
    
    private InetAddress inetAddress;

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

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.statistics.UserStats#getInetAddress()
	 */
	public InetAddress getInetAddress() {
		return inetAddress;
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.statistics.UserStats#setInetAddress(java.net.InetAddress)
	 */
	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.statistics.UserStats#setInetAddressFromIp(java.lang.String)
	 */
	public void setInetAddressFromIp(String ip) throws UnknownHostException {
		this.inetAddress = InetAddress.getByName(ip);		
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
		return this.inetAddress.equals(userstat.getInetAddress()) && this.username.equals(userstat.getUsername());
	}
}
