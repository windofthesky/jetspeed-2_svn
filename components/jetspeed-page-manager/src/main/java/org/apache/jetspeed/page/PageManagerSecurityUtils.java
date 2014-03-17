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
package org.apache.jetspeed.page;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.page.SecurityConstraintImpl;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.User;

import javax.security.auth.Subject;
import java.security.AccessController;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * PageManagerUtils
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class PageManagerSecurityUtils
{
    public static boolean checkConstraint(SecurityConstraintsDef def, String actions)
    throws DocumentException
    {
        List<String> viewActionList = SecurityConstraintImpl.parseCSVList(actions);
        List<String> otherActionsList = null;
        if (viewActionList.size() == 1)
        {
            if (!viewActionList.contains(JetspeedActions.VIEW))
            {
                otherActionsList = viewActionList;
                viewActionList = null;
            }
        }
        else
        {
            otherActionsList = viewActionList;
            viewActionList = null;
            if (otherActionsList.remove(JetspeedActions.VIEW))
            {
                viewActionList = new ArrayList<String>(1);
                viewActionList.add(JetspeedActions.VIEW);
            }
        }

        // get current request context subject
        Subject subject = JSSubject.getSubject(AccessController.getContext());
        if (subject == null)
        {
            throw new SecurityException("Security Consraint Check: Missing JSSubject");
        }

        // get user/group/role principal names
        List<String> userPrincipals = null;
        List<String> rolePrincipals = null;
        List<String> groupPrincipals = null;
        for (Principal principal : subject.getPrincipals())
        {
            if (principal instanceof User)
            {
                if (userPrincipals == null)
                {
                    userPrincipals = new LinkedList<String>();
                }
                userPrincipals.add(principal.getName());
            }
            else if (principal instanceof Role)
            {
                if (rolePrincipals == null)
                {
                    rolePrincipals = new LinkedList<String>();
                }
                rolePrincipals.add(principal.getName());
            }
            else if (principal instanceof Group)
            {
                if (groupPrincipals == null)
                {
                    groupPrincipals = new LinkedList<String>();
                }
                groupPrincipals.add(principal.getName());
            }
        }
        
        boolean result = false;
        
        // check constraints using parsed action and access lists
        if (viewActionList != null)
        {
            result = checkConstraints(viewActionList, userPrincipals, rolePrincipals, groupPrincipals, def);
        }
        if (otherActionsList != null)
        {
            result = checkConstraints(otherActionsList, userPrincipals, rolePrincipals, groupPrincipals, def);
        }
        return result;
    }

    /**
     * check access for the constraints list of a security constraints definition
     * 
     * @param actions given actions
     * @param userPrincipals set of user principals  
     * @param rolePrincipals set of role principals
     * @param groupPrincipals set oof group principals
     * @param def the security constraint definition 
     * @throws SecurityException
     */
    public static boolean checkConstraints(List<String> actions, List<String> userPrincipals, List<String> rolePrincipals, List<String> groupPrincipals, SecurityConstraintsDef def)
    throws DocumentException
    {
        List<SecurityConstraint> checkConstraints = def.getSecurityConstraints();
        if ((checkConstraints != null) && !checkConstraints.isEmpty())
        {
            // test each action, constraints check passes only
            // if all actions are permitted for principals
            for (String action : actions)
            {
                // check each action:
                // - if any actions explicitly permitted, (including owner),
                //   assume no permissions are permitted by default
                // - if all constraints do not specify a permission, assume
                //   access is permitted by default
                boolean actionPermitted = false;
                boolean actionNotPermitted = false;
                boolean anyActionsPermitted = true; // TODO:(getOwner() != null);
                
                // check against constraints
                for (SecurityConstraint checkConstraint : checkConstraints)
                {
                    SecurityConstraintImpl constraint = (SecurityConstraintImpl)checkConstraint;

                    // if permissions specified, attempt to match constraint
                    if (constraint.getPermissions() != null)
                    {
                        // explicit actions permitted
                        anyActionsPermitted = true;

                        // test action permission match and user/role/group principal match
                        if (constraint.actionMatch(action) &&
                            constraint.principalsMatch(userPrincipals, rolePrincipals, groupPrincipals, true))
                        {
                            actionPermitted = true;
                            break;
                        }
                    }
                    else
                    {
                        // permissions not specified: not permitted if any principal matched
                        if (constraint.principalsMatch(userPrincipals, rolePrincipals, groupPrincipals, false))
                        {
                            actionNotPermitted = true;
                            break;
                        }
                    }
                }
                
                // fail if any action not permitted
                if ((!actionPermitted && anyActionsPermitted) || actionNotPermitted)
                {
                    return false;
                }
            }
        }
        else
        {
            // fail if no constraints were found and
            // actions specified
            if (!actions.isEmpty())
            {
                return false;
            }
        }
        return true;
    }
}