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
package org.apache.jetspeed.security.impl;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.Permission;
import java.security.Permissions;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.i18n.KeyedMessage;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.om.InternalPermission;
import org.apache.jetspeed.security.om.InternalPrincipal;
import org.apache.jetspeed.security.om.impl.InternalPermissionImpl;
import org.apache.jetspeed.security.om.impl.InternalPrincipalImpl;
import org.apache.jetspeed.util.ArgUtil;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * <p>
 * Implementation for managing {@link Permission}and permission association to
 * {@link Principal}. Permissions are used to manage Principals access
 * entitlement on specified resources.
 * </p>
 * <p>
 * For instance:
 * </p>
 * 
 * <pre><code>
 * 
 *  
 *   grant principal o.a.j.security.UserPrincipal &quot;theUserPrincipal&quot;
 *   {
 *       permission o.a.j.security.PortletPermission &quot;myportlet&quot;, &quot;view,edit,minimize,maximize&quot;;
 *   };
 *   
 *  
 * </code>
 * 
 *  &lt;pre&gt;
 *   @author &lt;a href=&quot;mailto:dlestrat@apache.org&quot;&gt;David Le Strat&lt;/a&gt;
 * 
 * 
 */
public class PermissionManagerImpl extends PersistenceBrokerDaoSupport implements PermissionManager 
{
    private static final Log log = LogFactory.getLog(PermissionManagerImpl.class);
    private static ThreadLocal<HashMap<String, HashSet<Permission>>> permissionsCache 
        = new ThreadLocal<HashMap<String, HashSet<Permission>>>();
    
    /**
     * @see org.apache.jetspeed.security.PermissionManager#getPermissions(java.security.Principal)
     */
    public Permissions getPermissions(Principal principal)
    {        
        HashMap<String, HashSet<Permission>> permissionsMap = permissionsCache.get();
        if ( permissionsMap == null )
        {
            permissionsMap = new HashMap<String, HashSet<Permission>>();
            permissionsCache.set(permissionsMap);
        }
        HashSet<Permission> principalPermissions = permissionsMap.get(principal.getName());
        if ( principalPermissions == null )
        {
            InternalPrincipal internalPrincipal = getInternalPrincipal(principal.getName());
            if (null != internalPrincipal)
            {
                principalPermissions = getSecurityPermissions(internalPrincipal.getPermissions());
            }
            if ( principalPermissions == null)
            {
                principalPermissions = new HashSet<Permission>();
            }
            permissionsMap.put(principal.getName(), principalPermissions);
        }
        
        Permissions permissions = new Permissions();
        for (Permission p : principalPermissions)
        {
            permissions.add(p);
        }
        return permissions;
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManager#getPermissions(java.util.Collection)
     */
    public Permissions getPermissions(Collection<Principal> principals)
    {
        Permissions permissions = new Permissions();
        if ((null != principals) && principals.size() > 0)
        {
            HashSet<Permission> permissionsSet = new HashSet<Permission>();
            HashMap<String, HashSet<Permission>> permissionsMap = permissionsCache.get();
            if (permissionsMap == null)
            {
                permissionsMap = new HashMap<String, HashSet<Permission>>();
                permissionsCache.set(permissionsMap);
            }            
            Iterator<Principal> iter = principals.iterator();
            HashSet<Permission> principalPermissions;
            while (iter.hasNext())
            for (Principal p : principals)
            {
                principalPermissions = permissionsMap.get(iter.next());
                if ( principalPermissions != null )
                {
                    iter.remove();
                    permissionsSet.addAll(principalPermissions);
                }
            }
            if ( principals.size() > 0)
            {
                Criteria filter = new Criteria();
                filter.addIn("name", principals);
                Query query = QueryFactory.newQuery(InternalPrincipalImpl.class, filter);
                Collection<InternalPrincipal> internalPrincipals = getPersistenceBrokerTemplate().getCollectionByQuery(query);
                for (InternalPrincipal internalPrincipal : internalPrincipals)
                {
                    Collection<InternalPermission> internalPermissions = internalPrincipal.getPermissions();
                    if (null != internalPermissions)
                    {
                        principalPermissions = getSecurityPermissions(internalPermissions);
                        permissionsSet.addAll(principalPermissions);
                    }
                    else
                    {
                        principalPermissions = new HashSet<Permission>();
                    }
                    permissionsMap.put(internalPrincipal.getName(), principalPermissions);
                }
            }
            for (Permission permission : permissionsSet)
            {
                permissions.add(permission);
            }
        }
        return permissions;
    }

    /**
     * <p>
     * Iterate through a collection of {@link InternalPermission}and build a
     * unique collection of {@link java.security.Permission}.
     * </p>
     * 
     * @param omPermissions The collection of {@link InternalPermission}.
     */
    @SuppressWarnings("unchecked")
    private HashSet<Permission> getSecurityPermissions(Collection<InternalPermission> omPermissions)
    {     
        HashSet<Permission> permissions = new HashSet<Permission>();
        for (InternalPermission internalPermission : omPermissions)
        {
            Permission permission = null;
            try
            {
                Class permissionClass = Class.forName(internalPermission.getClassname());
                Class[] parameterTypes = { String.class, String.class };
                Constructor permissionConstructor = permissionClass.getConstructor(parameterTypes);
                Object[] initArgs = { internalPermission.getName(), internalPermission.getActions() };
                permission = (Permission) permissionConstructor.newInstance(initArgs);
                if(permissions.add(permission))
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Added permimssion: [class, " + permission.getClass().getName() + "], " + "[name, "
                                + permission.getName() + "], " + "[actions, " + permission.getActions() + "]");
                    }                   
                }
            }
            catch (Exception e)
            {
                log.error("Internal error", e);
            }
        }
        return permissions;
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManager#addPermission(java.security.Permission)
     */
    public void addPermission(Permission permission) throws SecurityException
    {
        InternalPermission internalPermission = new InternalPermissionImpl(permission.getClass().getName(), permission
                .getName(), permission.getActions());
        try
        {            
            getPersistenceBrokerTemplate().store(internalPermission);            
        }
        catch (Exception e)
        {
            KeyedMessage msg = SecurityException.UNEXPECTED.create("PermissionManager.addPermission",
                                                                   "store", e.getMessage());
            logger.error(msg, e);            
            throw new SecurityException(msg, e);
        }
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManager#removePermission(java.security.Permission)
     */
    public void removePermission(Permission permission) throws SecurityException
    {
        InternalPermission internalPermission = getInternalPermission(permission);
        if (null != internalPermission)
        {
            // clear the whole ThreadLocal permissions cache
            permissionsCache.set(null);
            try
            {
                // Remove permission.
                getPersistenceBrokerTemplate().delete(internalPermission);
            }
            catch (Exception e)
            {
                KeyedMessage msg = SecurityException.UNEXPECTED.create("PermissionManager.removePermission",
                                                                       "delete", e.getMessage());
                logger.error(msg, e);            
                throw new SecurityException(msg, e);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManager#removePermissions(java.security.Principal)
     */
    public void removePermissions(Principal principal) throws SecurityException
    {
        // Remove permissions on principal.
        InternalPrincipal internalPrincipal = getInternalPrincipal(principal.getName());
        if (null != internalPrincipal)
        {
            Collection<InternalPermission> internalPermissions = internalPrincipal.getPermissions();
            if (null != internalPermissions)
            {
                internalPermissions.clear();
            }
            // clear the whole ThreadLocal permissions cache
            permissionsCache.set(null);
            try
            {
                internalPrincipal.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                internalPrincipal.setPermissions(internalPermissions);
                
                getPersistenceBrokerTemplate().store(internalPrincipal);
            }
            catch (Exception e)
            {
                KeyedMessage msg = SecurityException.UNEXPECTED.create("PermissionManager.removePermissions",
                                                                       "store", e.getMessage());
                logger.error(msg, e);                
                throw new SecurityException(msg, e);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManager#grantPermission(java.security.Principal,
     *      java.security.Permission)
     */
    public void grantPermission(Principal principal, Permission permission) throws SecurityException
    {
        Collection<InternalPermission> internalPermissions = new ArrayList<InternalPermission>();
        InternalPrincipal internalPrincipal = getInternalPrincipal(principal.getName());
        if (null == internalPrincipal)
        {
            if ( principal instanceof UserPrincipal )
            {
                throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(principal.getName()));
            }
            else if ( principal instanceof RolePrincipal )
            {
                throw new SecurityException(SecurityException.ROLE_DOES_NOT_EXIST.create(principal.getName()));
            }
            // must/should be GroupPrincipal
            throw new SecurityException(SecurityException.GROUP_DOES_NOT_EXIST.create(principal.getName()));
        }
        InternalPermission internalPermission = getInternalPermission(permission);
        if (null == internalPermission)
        {
            throw new SecurityException(SecurityException.PERMISSION_DOES_NOT_EXIST.create(permission.getName()));
        }

        if (null != internalPrincipal.getPermissions())
        {
            internalPermissions.addAll(internalPrincipal.getPermissions());
        }
        if (!internalPermissions.contains(internalPermission))
        {
            internalPermissions.add(internalPermission);
        }
        // clear the whole ThreadLocal permissions cache
        permissionsCache.set(null);
        try
        {
            internalPrincipal.setModifiedDate(new Timestamp(System.currentTimeMillis()));
            internalPrincipal.setPermissions(internalPermissions);
            
            getPersistenceBrokerTemplate().store(internalPrincipal);
        }
        catch (Exception e)
        {
            KeyedMessage msg = SecurityException.UNEXPECTED.create("PermissionManager.grantPermission",
                                                                   "store", e.getMessage());
            logger.error(msg, e);            
            throw new SecurityException(msg, e);
        }
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManager#permissionExists(java.security.Permission)
     */
    public boolean permissionExists(Permission permission)
    {
        boolean permissionExists = true;
        InternalPermission internalPermission = getInternalPermission(permission);
        if (null == internalPermission)
        {
            permissionExists = false;
        }
        return permissionExists;
    }

    /**
     * @see org.apache.jetspeed.security.PermissionManager#revokePermission(java.security.Principal,
     *      java.security.Permission)
     */
    public void revokePermission(Principal principal, Permission permission) throws SecurityException
    {
        // Remove permissions on principal.
        InternalPrincipal internalPrincipal = getInternalPrincipal(principal.getName());
        if (null != internalPrincipal)
        {
            Collection<InternalPermission> internalPermissions = internalPrincipal.getPermissions();
            if (null != internalPermissions)
            {
                boolean revokePermission = false;
                ArrayList<InternalPermission> newInternalPermissions = new ArrayList<InternalPermission>();
                for (InternalPermission internalPermission : internalPermissions)
                {
                    if (!((internalPermission.getClassname().equals(permission.getClass().getName()))
                            && (internalPermission.getName().equals(permission.getName())) && (internalPermission.getActions()
                            .equals(permission.getActions()))))
                    {
                        newInternalPermissions.add(internalPermission);
                    }
                    else
                    {
                        revokePermission = true;
                    }
                }
                if (revokePermission)
                {
                    // clear the whole ThreadLocal permissions cache
                    permissionsCache.set(null);
                    try
                    {
                        internalPrincipal.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                        internalPrincipal.setPermissions(newInternalPermissions);

                        getPersistenceBrokerTemplate().store(internalPrincipal);
                    }
                    catch (Exception e)
                    {
                        KeyedMessage msg = SecurityException.UNEXPECTED.create("PermissionManager.revokePermission",
                                                                               "store", e.getMessage());
                        logger.error(msg, e);                      
                        throw new SecurityException(msg, e);
                    }
                }
            }
        }
    }

    /**
     * <p>
     * Returns the {@link InternalPrincipal}from the full path.
     * </p>
     * 
     * @param name The full path.
     * @return The {@link InternalPrincipal}.
     */
    InternalPrincipal getInternalPrincipal(String name)
    {
        Criteria filter = new Criteria();
        filter.addEqualTo("name", name);
        Query query = QueryFactory.newQuery(InternalPrincipalImpl.class, filter);
        InternalPrincipal internalPrincipal = (InternalPrincipal) getPersistenceBrokerTemplate().getObjectByQuery(query);
        return internalPrincipal;
    }

    /**
     * <p>
     * Returns the {@link InternalPermission} from a Permission.
     * </p>
     * 
     * @param permission The permission.
     * @return The {@link InternalPermission}.
     */
    InternalPermission getInternalPermission(Permission permission)
    {
        Criteria filter = new Criteria();
        filter.addEqualTo("classname", permission.getClass().getName());
        filter.addEqualTo("name", permission.getName());
        filter.addEqualTo("actions", permission.getActions());
        Query query = QueryFactory.newQuery(InternalPermissionImpl.class, filter);
        InternalPermission internalPermission = (InternalPermission) getPersistenceBrokerTemplate().getObjectByQuery(query);
        return internalPermission;
    }
    
    public boolean checkPermission(Subject subject, final Permission permission) 
    {
        try
        {
            //JSSubject.doAs(subject, new PrivilegedAction()
            JSSubject.doAsPrivileged(subject, new PrivilegedAction()                
            {
                public Object run()
                {
                    AccessController.checkPermission(permission);
                    return null;
                }
            }, null);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;         
    }
    
    @SuppressWarnings("unchecked")
    public Collection<InternalPermission> getInternalPermissions()
    {
        QueryByCriteria query = QueryFactory.newQuery(InternalPermissionImpl.class, new Criteria());
        query.addOrderByAscending("classname");
        query.addOrderByAscending("name");
        Collection<InternalPermission> internalPermissions = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return internalPermissions;
    }
    
    @SuppressWarnings("unchecked")
    public Permissions getPermissions(String classname, String resource)
    {
        Criteria filter = new Criteria();
        filter.addEqualTo("classname", classname);
        filter.addEqualTo("name", resource);
        Query query = QueryFactory.newQuery(InternalPermissionImpl.class, filter);
        Collection<InternalPermission> internalPermissions = getPersistenceBrokerTemplate().getCollectionByQuery(query);        
        Permissions permissions = new Permissions();
        try
        {
            for (InternalPermission internalPermission : internalPermissions)
            {
                Class permissionClass = Class.forName(internalPermission.getClassname());
                Class[] parameterTypes = { String.class, String.class };
                Constructor permissionConstructor = permissionClass.getConstructor(parameterTypes);
                Object[] initArgs = { internalPermission.getName(), internalPermission.getActions() };
                Permission permission = (Permission) permissionConstructor.newInstance(initArgs);            
                permissions.add(permission);
            }
        }
        catch (Exception e)
        {
            logger.error("Failed to retrieve permissions", e);            
        }
        return permissions;        
    }    
    
    public int updatePermission(Permission permission, Collection<Principal> principals)
    throws SecurityException
    {
        int count = 0;
        InternalPermission internal = getInternalPermission(permission);
        Collection<InternalPrincipal> newPrincipals = new LinkedList<InternalPrincipal>();
        for (Principal principal : principals)
        {
            InternalPrincipal internalPrincipal = getInternalPrincipal(principal.getName());
            newPrincipals.add(internalPrincipal);            
        }
        internal.setPrincipals(newPrincipals);
        internal.setModifiedDate(new Timestamp(System.currentTimeMillis()));
        try
        {            
            getPersistenceBrokerTemplate().store(internal);            
        }
        catch (Exception e)
        {
            KeyedMessage msg = SecurityException.UNEXPECTED.create("PermissionManager.updatePermission",
                                                                   "store", e.getMessage());
            logger.error(msg, e);            
            throw new SecurityException(msg, e);
        }
        return count;
    }
    
    public Collection<Principal> getPrincipals(Permission permission)
    {
        Collection<Principal> result = new LinkedList<Principal>();        
        InternalPermission internalPermission = this.getInternalPermission(permission);
        if (internalPermission == null)
        {
            return result;
        }
        for (InternalPrincipal internalPrincipal : internalPermission.getPrincipals())
        {
            Principal principal = SecurityHelper.createPrincipalFromInternal(internalPrincipal); 
            result.add(principal);
        }
        return  result;
    }
}
