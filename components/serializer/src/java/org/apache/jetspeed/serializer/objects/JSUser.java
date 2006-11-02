/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.serializer.objects;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

/**
 * Jetspeed Serialized (JS) User
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JSUser
{

    private String name;

    private char[] password;

    private ArrayList roles = null;

    private ArrayList groups = null;

    private JSUserAttributes userInfo = null;

    private JSNameValuePairs preferences = null;

    private ArrayList publicCredentials = null;

    private ArrayList privateCredentials = null;

    private JSUserRoles roleString;

    private JSUserGroups groupString;

    private JSPrincipalRules rules = new JSPrincipalRules();

    private transient Principal principal;

    public JSUser()
    {
    }

    public void addPublicCredential(Object o)
    {
        if (publicCredentials == null) publicCredentials = new ArrayList();
        publicCredentials.add(o);
    }

    public void addPrivateCredential(Object o)
    {
        if (privateCredentials == null) privateCredentials = new ArrayList();
        privateCredentials.add(o);
    }

    public void addGroup(JSGroup group)
    {
        if (groups == null) groups = new ArrayList();
        groups.add(group);
    }

    public void addRole(JSRole role)
    {
        if (roles == null) roles = new ArrayList();
        roles.add(role);
    }

    public ArrayList getGroups()
    {
        return groups;
    }

    public void setGroups(ArrayList groups)
    {
        this.groups = groups;
    }

    public char[] getPassword()
    {
        return password;
    }

    public void setUserCredential(String name, char[] password)
    {
        setName(name);
        setPassword(password);

    }

    public void setPassword(char[] password)
    {
        this.password = password;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ArrayList getRoles()
    {
        return roles;
    }

    public void setRoles(ArrayList roles)
    {
        this.roles = roles;
    }

    public String getName()
    {
        return name;
    }

    /*
     * private void initUser() throws Exception { User user = null; try {
     * ums.addUser("test", "password01"); user = ums.getUser("test"); } catch
     * (SecurityException sex) { assertTrue("user exists. should not have thrown
     * an exception.", false); }
     * 
     * Preferences userInfoPrefs = user.getPreferences().node("userinfo");
     * userInfoPrefs.put("user.name.given", "Test Dude");
     * userInfoPrefs.put("user.name.family", "Dudley"); }
     * 
     */

    /**
     * @return Returns the preferences.
     */
    public JSNameValuePairs getPreferences()
    {
        return preferences;
    }

    /**
     * @param preferences
     *            The preferences to set.
     */
    public void setPreferences(Preferences preferences)
    {
        this.preferences = new JSNameValuePairs(preferences);
    }

    /**
     * @return Returns the privateCredentials.
     */
    public ArrayList getPrivateCredentials()
    {
        return privateCredentials;
    }

    /**
     * @param privateCredentials
     *            The privateCredentials to set.
     */
    public void setPrivateCredentials(ArrayList privateCredentials)
    {
        this.privateCredentials = privateCredentials;
    }

    /**
     * @return Returns the publicCredentials.
     */
    public ArrayList getPublicCredentials()
    {
        return publicCredentials;
    }

    /**
     * @param publicCredentials
     *            The publicCredentials to set.
     */
    public void setPublicCredentials(ArrayList publicCredentials)
    {
        this.publicCredentials = publicCredentials;
    }

    /**
     * @param userInfo
     *            The userInfo to set.
     */
    public void setUserInfo(Preferences userInfo)
    {
        this.userInfo = new JSUserAttributes(userInfo);
    }

    /**
     * @return Returns the userInfo.
     */
    public JSUserAttributes getUserInfo()
    {
        return userInfo;
    }

    /***************************************************************************
     * SERIALIZER
     */
    private static final XMLFormat XML = new XMLFormat(JSUser.class)
    {

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
            try
            {
                JSUser g = (JSUser) o;
                String s = g.getName();
                if ((s == null) || (s.length() == 0)) s = "guest";
                xml.setAttribute("name", s);
                xml.setAttribute("password", g.getPasswordString());
 

                /** named fields HERE */
 
                /** implicitly named (through binding) fields here */
                g.groupString = new JSUserGroups(g.putTokens(g.getGroups()));
                g.roleString = new JSUserRoles(g.putTokens(g.getRoles()));

                xml.add(g.roleString);
                xml.add(g.groupString);
                xml.add(g.preferences);
                xml.add(g.userInfo);
                xml.add(g.rules);

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void read(InputElement xml, Object o)
        {
            try
            {
                JSUser g = (JSUser) o;
                g.name = xml.getAttribute("name", "unknown");
                g.password = xml.getAttribute("password","").toCharArray();
                
                Object o1 = null;
 

                while (xml.hasNext())
                {
                     o1 = xml.getNext(); // mime

                    if (o1 instanceof JSUserRoles)
                        g.roleString = (JSUserRoles) o1;
                    else
                    if (o1 instanceof JSUserGroups)
                        g.groupString = (JSUserGroups) o1;
                    else
                        if (o1 instanceof JSNameValuePairs)
                        g.preferences  = (JSNameValuePairs) o1;
                        else
                            if (o1 instanceof JSUserAttributes)
                            g.userInfo  = (JSUserAttributes) o1;
                            else
                                if (o1 instanceof JSPrincipalRules)
                                g.rules  = (JSPrincipalRules) o1;
                }
                
                g.roles = g.getTokens((String) xml.get("roles"));
                g.groups = g.getTokens((String) xml.get("groups"));

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    };

    // todo - needs work....outcome should be the right collection for lookup
    // etc....
    private ArrayList getTokens(String _line)
    {
        if ((_line == null) || (_line.length() == 0)) return null;

        StringTokenizer st = new StringTokenizer(_line, ",");
        ArrayList list = new ArrayList();

        while (st.hasMoreTokens())
            list.add(st.nextToken());
        return list;
    }

    private String append(JSRole rule)
    {
        return rule.getName();
    }

    private String append(JSGroup group)
    {
        return group.getName();
    }

    private String append(Object s)
    {
        if (s instanceof JSRole) return append((JSRole) s);
        if (s instanceof JSGroup) return append((JSGroup) s);

        return s.toString();
    }

    private String putTokens(ArrayList _list)
    {
        if ((_list == null) || (_list.size() == 0)) return "";
        boolean _start = true;
        Iterator _it = _list.iterator();
        StringBuffer _sb = new StringBuffer();
        while (_it.hasNext())
        {
            if (!_start)
                _sb.append(',');
            else
                _start = false;

            _sb.append(append(_it.next()));
        }
        return _sb.toString();
    }

    private String getPasswordString()
    {
        if ((this.getPassword() == null) || (this.getPassword().length == 0))
            return "";
        else
            return new String(this.getPassword());
    }

    /**
     * @return Returns the rules.
     */
    public JSPrincipalRules getRules()
    {
        return rules;
    }

    /**
     * @param rules
     *            The rules to set.
     */
    public void setRules(JSPrincipalRules rules)
    {
        this.rules = rules;
    }

    /**
     * @return Returns the principal.
     */
    public Principal getPrincipal()
    {
        return principal;
    }

    /**
     * @param principal
     *            The principal to set.
     */
    public void setPrincipal(Principal principal)
    {
        this.principal = principal;
    }

}