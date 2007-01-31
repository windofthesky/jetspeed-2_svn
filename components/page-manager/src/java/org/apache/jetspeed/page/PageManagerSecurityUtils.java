/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.security.AccessController;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.page.SecurityConstraintImpl;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.UserPrincipal;


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
        List viewActionList = SecurityConstraintImpl.parseCSVList(actions);
        List otherActionsList = null;
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
                viewActionList = new ArrayList(1);
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
        List userPrincipals = null;
        List rolePrincipals = null;
        List groupPrincipals = null;
        Iterator principals = subject.getPrincipals().iterator();
        while (principals.hasNext())
        {
            Principal principal = (Principal) principals.next();
            if (principal instanceof UserPrincipal)
            {
                if (userPrincipals == null)
                {
                    userPrincipals = new LinkedList();
                }
                userPrincipals.add(principal.getName());
            }
            else if (principal instanceof RolePrincipal)
            {
                if (rolePrincipals == null)
                {
                    rolePrincipals = new LinkedList();
                }
                rolePrincipals.add(principal.getName());
            }
            else if (principal instanceof GroupPrincipal)
            {
                if (groupPrincipals == null)
                {
                    groupPrincipals = new LinkedList();
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
    public static boolean checkConstraints(List actions, List userPrincipals, List rolePrincipals, List groupPrincipals, SecurityConstraintsDef def) 
    throws DocumentException
    {
        
        List checkConstraints = def.getSecurityConstraints();
            // SecurityConstraint c =(SecurityConstraint)constraints.next();
        // skip missing or empty constraints: permit all access
        //List checkConstraints = getAllSecurityConstraints(pageSecurity);
        if ((checkConstraints != null) && !checkConstraints.isEmpty())
        {
            // test each action, constraints check passes only
            // if all actions are permitted for principals
            Iterator actionsIter = actions.iterator();
            while (actionsIter.hasNext())
            {
                // check each action:
                // - if any actions explicity permitted, (including owner),
                //   assume no permissions are permitted by default
                // - if all constraints do not specify a permission, assume
                //   access is permitted by default
                String action = (String)actionsIter.next();
                boolean actionPermitted = false;
                boolean actionNotPermitted = false;
                boolean anyActionsPermitted = true; // TODO:(getOwner() != null);
                
                // check against constraints
                Iterator checkConstraintsIter = checkConstraints.iterator();
                while (checkConstraintsIter.hasNext())
                {
                    SecurityConstraintImpl constraint = (SecurityConstraintImpl)checkConstraintsIter.next();
                    
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
                    //throw new SecurityException("SecurityConstraintsImpl.checkConstraints(): Access for " + action + " not permitted.");
                    return false;
                }
            }
        }
        else
        {
            // fail for any action if owner specified
            // since no other constraints were found
            if (/*(getOwner() != null) && */ !actions.isEmpty())
            {
                //String action = (String)actions.get(0);
                //throw new SecurityException("SecurityConstraintsImpl.checkConstraints(): Access for " + action + " not permitted, (not owner).");
                return false;
            }
        }
        return true;
    }
}