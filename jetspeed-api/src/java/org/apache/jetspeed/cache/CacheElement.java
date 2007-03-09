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


package org.apache.jetspeed.cache;

/**
 * <p>
 *  Provides interface to cached elements
 *  Abstraction around atual cache implementation
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface CacheElement
{
	public static int ActionAdded  = 1;
	public static int ActionChanged = 2;
	public static int ActionRemoved = -1;
	public static int ActionEvicted = -2;
	public static int ActionExpired = -3;
	
     
    /**
     * 
     * @return the idle time in seconds for this cache element
     */
    int getTimeToIdleSeconds();
    
    /**
     * 
     * @return the idle time in seconds for this cache element
     */
    int getTimeToLiveSeconds();    
    
    void setTimeToLiveSeconds(int timeToLive);
    
    void setTimeToIdleSeconds(int timeToIdle);
    
    Object getContent();
    
    Object getKey();
    
    boolean isEternal();
    
    void setEternal(boolean eternal);
    
    
    
}