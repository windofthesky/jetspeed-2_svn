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
package org.apache.jetspeed.layout.impl;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.container.session.PortalSessionsManager;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.apache.jetspeed.statistics.UserStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Returns the list of currently logged in users
 * and optionally also the offline users and
 * number of guest user sessions
 * 
 * AJAX action:
 * 		action: getuserlist
 * 
 * AJAX Parameters: 
 * 		guest:	whether we should return also the guest sessions
 * 			   	true | false (default)
 *      userinfo: whether we should include also userinfo
 *      		true | false (default)
 *      offline: whether we should include offline users
 *      		true | false (default)
 *      all: 	return every bits and piece there is
 *      		true | false (default)
 * @author <a href="mailto:mikko.wuokko@evtek.fi">Mikko Wuokko</a>
 * @version $Id: $
 */
public class GetUserListAction 
    extends BaseUserAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Logger log = LoggerFactory.getLogger(GetUserListAction.class);
    private PortalStatistics pstats = null;
    private PortalSessionsManager psm = null;
    // By default the protection is set to all
    private String protectionScope = "all";

    private final String PARAM_GUEST = "guest";
    private final String PARAM_USERINFO = "userinfo";
    private final String PARAM_OFFILE = "offline";
    private final String PARAM_ALL = "all";
    
    public GetUserListAction(String template, 
                            String errorTemplate, 
                            UserManager um,
                            PortalStatistics pstats,
                            PortalSessionsManager psm)
    {
        this(template, errorTemplate, um, pstats, psm, null); 
    }
    
    public GetUserListAction(String template, 
            String errorTemplate, 
            UserManager um,
            PortalStatistics pstats,
            PortalSessionsManager psm, 
            RolesSecurityBehavior securityBehavior)
    {
    	super(template, errorTemplate, um, securityBehavior); 
    	this.pstats = pstats;
    	this.psm = psm;
    }
    
    public GetUserListAction(String template, 
            String errorTemplate, 
            UserManager um,
            PortalStatistics pstats,
            PortalSessionsManager psm, 
            RolesSecurityBehavior securityBehavior,
            String protectionScope)
    {
    	super(template, errorTemplate, um, securityBehavior); 
    	this.pstats = pstats;
    	this.psm = psm;
    	this.protectionScope = protectionScope;
    }
    
    public boolean run(RequestContext requestContext, Map<String,Object> resultMap)
            throws AJAXException
    {
        boolean success = true;
        String status = "success";
        
    	boolean includeGuests;
    	boolean includeUserInfo;
        boolean includeOffline;
        boolean includeAll = isTrue(getActionParameter(requestContext, PARAM_ALL));
        
        // Set everything true if "all" is set to true
        if(includeAll){
        	includeGuests = true;
        	includeUserInfo = true;
        	includeOffline = true;
        }
        else
        {        
        	includeOffline = isTrue(getActionParameter(requestContext, PARAM_OFFILE));
        	includeGuests = isTrue(getActionParameter(requestContext, PARAM_GUEST));
        	includeUserInfo = isTrue(getActionParameter(requestContext, PARAM_USERINFO));
        }
        
    	// Do a security check if a behavior is set
    	if(securityBehavior != null)
    	{
    		// If protection is set to "none", everything will be allowed
    		if(!checkAccess(requestContext, JetspeedActions.EDIT) && !this.protectionScope.equals("none"))
    		{
    			// If we have set protection to private only and security check failed,
    			// will return basic information still
    			if(this.protectionScope.equals("private-offline"))
    			{
    				// If private and offline information is protected, disable that and offline users.
    				includeUserInfo = false;
    				includeOffline = false;
    			}
    			else if(this.protectionScope.equals("private"))
    			{
    				// Only private information is protected.
    				includeUserInfo = false;
    			}
    			else
    			{
    				
	    			success = false;
	                resultMap.put(REASON, "Insufficient access see user details.");                
	                return success;
    			}
    		}
    	}
        
        int numberOfCurrentUsers = 0;
        int numberOfCurrentLoggedInUsers = 0;
        
        Collection users = new ArrayList();
        Collection loggedInUsers = new ArrayList();
        Collection offlineUsers = new ArrayList();

        try
        {
            resultMap.put(ACTION, "getuserlist");
            // Check that the statistics is not disabled 
        	if(pstats != null)
        	{
        		// Get the user counts
        		numberOfCurrentUsers = psm.sessionCount();
        		numberOfCurrentLoggedInUsers = pstats.getNumberOfLoggedInUsers();

        		/*
        		 * An helper to track the users that have already been added to the resultMap 
        		 * as logged in users, so there wouldn't be an offline duplicate. Trying
        		 * to prevent some overhead with this.
        		 * Needs some more thinking, maybe some helper functions to portal statistics
        		 * to get just the names of the logged in users for contains comparison.
        		 */ 
        		List addedUserNames = new ArrayList();
        		
        		// If no logged in users, nothing to do
        		if(numberOfCurrentLoggedInUsers > 0)
        		{

        			// Logged in users is a list of UserStats actions
                    for (Map<String,UserStats> userMap : pstats.getListOfLoggedInUsers())
        			{
        				if(userMap != null && userMap.size() > 0)
        				{
        					Iterator userKeyIter = userMap.keySet().iterator();
        					while(userKeyIter.hasNext())
        					{
        						String userStatKey = String.valueOf(userKeyIter.next());
        						UserStats userStat = (UserStats)userMap.get(userStatKey);
        						
        						Map singleUserMap = new HashMap();
        						singleUserMap.put(USERNAME, userStat.getUsername());
                                singleUserMap.put(SESSIONS, new Integer(userStat.getNumberOfSessions()));
        						singleUserMap.put(STATUS, ONLINE);
        						singleUserMap.put(IPADDRESS, userStat.getIpAddress());
        						if(includeUserInfo)
        						{
        							singleUserMap.put(USERINFO, getUserInfo(userStat.getUsername()));
        						}
        						
        						// Add user to the helper if not added yet
        						if(!addedUserNames.contains(userStat.getUsername()))
        							addedUserNames.add(userStat.getUsername());
        						
        						loggedInUsers.add(singleUserMap);
        					}
        					        					
        				}
        			}
        			
        			// Adding online users to the collection
        			users.addAll(loggedInUsers);
                }
        		
        		// Check whether we should iterate through all of the users or just logged in ones
    			if(includeOffline)
    			{
    				for (User user : userManager.getUsers(""))
        			{
        				Principal userPrincipal = SubjectHelper.getPrincipal(userManager.getSubject(user), User.class);
        				if(userPrincipal != null)
        				{
        					// Check if this users is already added as online user
        					if(!addedUserNames.contains(userPrincipal.getName()))
        					{
        						Map userMap = new HashMap();
        						userMap.put(USERNAME, userPrincipal.getName());
        						userMap.put(STATUS, OFFLINE);
        						if(includeUserInfo)
        						{
        							userMap.put(USERINFO, getUserInfo(userPrincipal.getName()));        						}

        						offlineUsers.add(userMap);
        					}
        				}
        			}
    				
        			// Adding online users to the collection
        			users.addAll(offlineUsers);
    			}
        		
        		// Add the logged in users to resultMap
                resultMap.put(USERS, users);
                
        		if(includeGuests)        			
        		{
        			// Add number of guest accounts to resultMap
        			int guestUserCount = numberOfCurrentUsers - numberOfCurrentLoggedInUsers;
                    resultMap.put(GUESTUSERS, new Integer(guestUserCount));
        		}
                	
        	}
        	else
        	{
        		status = "failure";
        		resultMap.put(REASON, "Statistics not available");
        		return false;
        	}
            resultMap.put(STATUS, status);
        } 
        catch (Exception e)
        {
            log.error("exception statistics access", e);
            resultMap.put(REASON, e.toString());
            success = false;
        }
        return success;
    }    

    
    /**
     * Helper method to get the user information of an user as Map.
     * 
     * @param username Name of the user of request
     * @return Map containing the user information keyed by the name of the attribute.
     * @throws SecurityException
     */
    private Map<String, String> getUserInfo(String username) throws SecurityException
    {
    	User user =  userManager.getUser(username);
		if(user != null)
		{
            return user.getInfoMap();
		}
		return new HashMap<String, String>();
    }
    
}
