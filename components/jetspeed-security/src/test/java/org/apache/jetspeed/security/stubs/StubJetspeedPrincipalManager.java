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
package org.apache.jetspeed.security.stubs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationReference;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalManager;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PrincipalManagerEventListener;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 */

public abstract class StubJetspeedPrincipalManager implements JetspeedPrincipalManager
{

    private Map<String,JetspeedPrincipal> principals=new HashMap<String,JetspeedPrincipal>();
    private Map<String,Map<String,List<JetspeedPrincipal>>> principalAssociationsFrom=new HashMap<String,Map<String,List<JetspeedPrincipal>>>();
    private Map<String,Map<String,List<JetspeedPrincipal>>> principalAssociationsTo=new HashMap<String,Map<String,List<JetspeedPrincipal>>>();
    
    private void internalAddAssocation(Map<String,Map<String,List<JetspeedPrincipal>>> associationsMap, JetspeedPrincipal from, JetspeedPrincipal to, String associationName){
        Map<String,List<JetspeedPrincipal>> fromToMap = associationsMap.get(associationName);
        if (fromToMap == null){
            fromToMap = new HashMap<String,List<JetspeedPrincipal>>();
            associationsMap.put(associationName, fromToMap);
        }
        List<JetspeedPrincipal> toPrincipals = fromToMap.get(from.getName());
        if (toPrincipals==null){
            toPrincipals=new ArrayList<JetspeedPrincipal>();
            fromToMap.put(from.getName(), toPrincipals);
        }
        if (!toPrincipals.contains(to)){
            toPrincipals.add(to);
        }
    }
    
    public void addAssociation(JetspeedPrincipal from, JetspeedPrincipal to, String associationName) throws SecurityException
    {
        internalAddAssocation(principalAssociationsFrom,from,to,associationName);
        internalAddAssocation(principalAssociationsTo,to,from,associationName);
    }

    public void addPrincipal(JetspeedPrincipal principal, Set<JetspeedPrincipalAssociationReference> associations) throws SecurityException
    {
        principals.put(principal.getName(),principal);
    }

    private List<JetspeedPrincipal> internalGetAssociatedPrincipals(Map<String,Map<String,List<JetspeedPrincipal>>> associationsMap, String principalFromName, JetspeedPrincipalType principalType, String associationName){
        Map<String,List<JetspeedPrincipal>> fromToMap = associationsMap.get(associationName);
        if (fromToMap!=null){
            return fromToMap.get(principalFromName);
        }
        return null;
    }
    
    public List<? extends JetspeedPrincipal> getAssociatedFrom(String principalFromName, JetspeedPrincipalType from, String associationName)
    {
        return internalGetAssociatedPrincipals(principalAssociationsFrom, principalFromName,from,associationName);
    }

    private List<String> internalGetAssociatedNames(Map<String,Map<String,List<JetspeedPrincipal>>> associationsMap, String principalFromName, JetspeedPrincipalType principalType, String associationName){
        Map<String,List<JetspeedPrincipal>> fromToMap = associationsMap.get(associationName);
        if (fromToMap!=null){
            List<JetspeedPrincipal> relatedPrincipals = fromToMap.get(principalFromName);
            List<String> relatedPrincipalNames = new ArrayList<String>();
            for (JetspeedPrincipal relatedPrincipal : relatedPrincipals){
                relatedPrincipalNames.add(relatedPrincipal.getName());
            }
            return relatedPrincipalNames;
        }
        return null;
    }
    
    public List<String> getAssociatedNamesFrom(String principalFromName, JetspeedPrincipalType from, String associationName)
    {
        return internalGetAssociatedNames(principalAssociationsFrom,principalFromName,from,associationName);
    }

    public List<String> getAssociatedNamesTo(String principalToName, JetspeedPrincipalType to, String associationName)
    {
        return internalGetAssociatedNames(principalAssociationsTo,principalToName,to,associationName);
    }

    public List<? extends JetspeedPrincipal> getAssociatedTo(String principalToName, JetspeedPrincipalType to, String associationName)
    {
        return internalGetAssociatedPrincipals(principalAssociationsTo, principalToName,to,associationName);
    }

    public JetspeedPrincipal getPrincipal(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> getPrincipalNames(String nameFilter)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<? extends JetspeedPrincipal> getPrincipals(String nameFilter)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<? extends JetspeedPrincipal> getPrincipalsByAttribute(String attributeName, String attributeValue)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public JetspeedPrincipalType getPrincipalType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public JetspeedPrincipal newTransientPrincipal(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean principalExists(String name)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void removeAssociation(JetspeedPrincipal from, JetspeedPrincipal to, String associationName) throws SecurityException
    {
        // TODO Auto-generated method stub
        
    }

    public void removePrincipal(JetspeedPrincipal principal) throws SecurityException
    {
        // TODO Auto-generated method stub
        
    }

    public void removePrincipal(String name) throws SecurityException
    {
        // TODO Auto-generated method stub
        
    }

    public List<? extends JetspeedPrincipal> resolveAssociatedFrom(String principalFromName, JetspeedPrincipalType from, String associationName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<? extends JetspeedPrincipal> resolveAssociatedTo(String principalToName, JetspeedPrincipalType to, String associationName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void updatePrincipal(JetspeedPrincipal principal) throws SecurityException
    {
        // TODO Auto-generated method stub
        
    }

    public List<JetspeedPrincipalAssociationType> getAssociationTypes()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public JetspeedPrincipal newPrincipal(String name, boolean mapped)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void transferAssociationFrom(JetspeedPrincipal from, JetspeedPrincipal to, JetspeedPrincipal target, String associationName) throws SecurityException
    {
        // TODO Auto-generated method stub
        
    }

    public void transferAssociationTo(JetspeedPrincipal from, JetspeedPrincipal to, JetspeedPrincipal target, String associationName) throws SecurityException
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipalManager#addListener(org.apache.jetspeed.security.PrincipalManagerEventListener)
     */
    public void addListener(PrincipalManagerEventListener listener)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipalManager#removeListener(org.apache.jetspeed.security.PrincipalManagerEventListener)
     */
    public void removeListener(PrincipalManagerEventListener listener)
    {
        // TODO Auto-generated method stub
        
    }    
}
