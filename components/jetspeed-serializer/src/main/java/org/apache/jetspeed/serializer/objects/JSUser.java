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
package org.apache.jetspeed.serializer.objects;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.sql.Date;
import java.util.Iterator;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jetspeed.security.Credential;
import org.apache.jetspeed.security.SecurityAttribute;
import org.apache.jetspeed.security.SecurityAttributes;

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

    private JSPWAttributes pwData = null;

    private List<JSPrincipal> roles = null;

    private List<JSPrincipal> groups = null;

    private JSUserAttributes userInfo = null;

    private JSSecurityAttributes attributes = null;

    private List<Credential> publicCredentials = null;

    private List<Credential> privateCredentials = null;

    private JSUserRoles roleString;

    private JSUserGroups groupString;

    private JSPrincipalRules rules = new JSPrincipalRules();

    private String userTemplate;
    private String subsite;
    
    private transient Principal principal;

    public JSUser()
    {
    }

    public void addPublicCredential(Credential o)
    {
        if (publicCredentials == null) 
            publicCredentials = new ArrayList<Credential>();
        publicCredentials.add(o);
    }

    public void addPrivateCredential(Credential o)
    {
        if (privateCredentials == null) 
            privateCredentials = new ArrayList<Credential>();
        privateCredentials.add(o);
    }

    public void addGroup(JSPrincipal group)
    {
        if (groups == null) 
            groups = new ArrayList<JSPrincipal>();
        groups.add(group);
    }

    public void addRole(JSPrincipal role)
    {
        if (roles == null) 
            roles = new ArrayList<JSPrincipal>();
        roles.add(role);
    }

    public List<JSPrincipal> getGroups()
    {
        return groups;
    }

    public void setGroups(List<JSPrincipal> groups)
    {
        this.groups = groups;
    }

    public char[] getPassword()
    {
        return password;
    }

    public void setUserCredential(String name, char[] password, Date expirationDate, boolean isEnabled, boolean isExpired, boolean requireUpdate)
    {
        setName(name);
        setPassword(password);
        pwData = new JSPWAttributes();
        if (password != null)
        {
	        pwData.getMyMap().put("password",this.getPasswordString());
	        if (expirationDate != null)
	        {
	        	pwData.getMyMap().put("expirationDate",expirationDate.toString());
	        }
	        pwData.getMyMap().put("enabled",(isEnabled?"TRUE":"FALSE"));
	        pwData.getMyMap().put("requiresUpdate",(requireUpdate?"TRUE":"FALSE"));
        }
    }

    protected void resetPassword()
    {
    	try
    	{
	    	if (pwData != null)
	    	{
	    		Object o = pwData.getMyMap().get("password");
    		
	    		String pw = StringEscapeUtils.unescapeHtml((String)o);
	    		if ((pw != null) && (pw.length()>0))
	    			password = pw.toCharArray();
	    		else
	    			password = null;
	    	}
    	}
    	catch (Exception e)
    	{
			password = null;
    	}
    }
    
    public boolean getPwEnabled()
    {
       	return getPWBoolean("enabled",false);
    }
    public boolean getPwRequiredUpdate()
    {
       	return getPWBoolean("requiresUpdate",false);
    }

    

    
    
    public Date getPwExpirationDate()
    {
    	if (pwData != null)
    	{
    		Object o = pwData.getMyMap().get("expirationDate");
    		if (o == null)
    			return null;
    		if ( o instanceof Date)
    			return (Date)o;
    		
    		Date d = Date.valueOf((String)o);
    		return d;
    		
    	}
    	return null;
    }
    
    
   private boolean getPWBoolean(String property, boolean defaultSetting)
    {
       	if (pwData == null)
       		return defaultSetting;
       	try
       	{
	   		Object o = pwData.getMyMap().get(property);
			if (o == null)
				return defaultSetting;
			return ((String)o).equalsIgnoreCase("TRUE");
       	}
       	catch (Exception e)
       	{
       		return defaultSetting;
       	}
    }
   
    public void setPassword(char[] password)
    {
        this.password = password;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<JSPrincipal> getRoles()
    {
        return roles;
    }

    public void setRoles(List<JSPrincipal> roles)
    {
        this.roles = roles;
    }

    public String getName()
    {
        return name;
    }

    public JSSecurityAttributes getSecurityAttributes()
    {
        return attributes;
    }

    public void setSecurityAttributes(Map<String, SecurityAttribute> sa)
    {
        this.attributes = new JSSecurityAttributes();
        for (Map.Entry<String, SecurityAttribute> e : sa.entrySet())
        {
            SecurityAttribute attrib = e.getValue();
            JSNVPElement element = new JSNVPElement(attrib.getName(), attrib.getStringValue());
            this.attributes.add(element);
        }
    }
    
    public JSUserAttributes getUserInfo()
    {
        return userInfo;
    }

    public void setUserInfo(Map<String, SecurityAttribute> sa)
    {
        this.userInfo = new JSUserAttributes(); 
        for (Map.Entry<String, SecurityAttribute> e : sa.entrySet())
        {
            SecurityAttribute attrib = e.getValue();
            JSNVPElement element = new JSNVPElement(attrib.getName(), attrib.getStringValue());
            this.userInfo.add(element);
        }
    }
    

    /**
     * @return Returns the privateCredentials.
     */
    public List<Credential> getPrivateCredentials()
    {
        return privateCredentials;
    }

    /**
     * @param privateCredentials
     *            The privateCredentials to set.
     */
    public void setPrivateCredentials(List<Credential> privateCredentials)
    {
        this.privateCredentials = privateCredentials;
    }

    /**
     * @return Returns the publicCredentials.
     */
    public List<Credential> getPublicCredentials()
    {
        return publicCredentials;
    }

    /**
     * @param publicCredentials
     *            The publicCredentials to set.
     */
    public void setPublicCredentials(List<Credential> publicCredentials)
    {
        this.publicCredentials = publicCredentials;
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
                if ((s == null) || (s.length() == 0))
                {
                    s = "guest";
                }
                xml.setAttribute("name", s);
                s = g.getUserTemplate();
                if ((s != null) && (s.length() > 0))
                {
                    xml.setAttribute("userTemplate", s);
                }
                s = g.getSubsite();
                if ((s != null) && (s.length() > 0))
                {
                    xml.setAttribute("subsite", s);
                }
                                
                xml.add(g.getPwData());

                /** named fields HERE */
 
                /** implicitly named (through binding) fields here */
                g.groupString = new JSUserGroups(g.putTokens(g.getGroups()));
                g.roleString = new JSUserRoles(g.putTokens(g.getRoles()));

                xml.add(g.roleString);
                xml.add(g.groupString);
                xml.add(g.attributes);
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
                g.name = StringEscapeUtils.unescapeHtml(xml.getAttribute("name", "unknown"));
                g.userTemplate = StringEscapeUtils.unescapeHtml(xml.getAttribute("userTemplate", ""));
                if (g.userTemplate.equals(""))
                    g.userTemplate = null;
                g.subsite = StringEscapeUtils.unescapeHtml(xml.getAttribute("subsite", ""));
                if (g.subsite.equals(""))
                    g.subsite = null;
                
                Object o1 = null;
				while (xml.hasNext())
				{
					o1 = xml.getNext(); // mime
					
					
					if (o1 instanceof JSPWAttributes)
					{
						g.pwData = (JSPWAttributes) o1;
						g.resetPassword();
					}
					else if (o1 instanceof JSUserGroups)
						g.groupString = (JSUserGroups) o1;
					else if (o1 instanceof JSUserRoles)
	                    g.roleString = (JSUserRoles) o1;
	                else if (o1 instanceof JSUserAttributes)
	                    g.userInfo  = (JSUserAttributes) o1;
	                else if (o1 instanceof JSSecurityAttributes)
		                g.attributes  = (JSSecurityAttributes) o1;
                    else if (o1 instanceof JSPrincipalRules)
	                    g.rules  = (JSPrincipalRules) o1;
                    else if (o1 instanceof JSNVPElements)
                    {
                        g.attributes  = new JSSecurityAttributes();
                        for (JSNVPElement element : ((JSNVPElements)o1).getValues())
                        {
                            JSNVPElement clonedElement = new JSNVPElement(element.getKey(), element.getValue());
                            g.attributes.add(clonedElement);
                        }
                    }
                }
            } 
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    };


    private String append(JSPrincipal principal)
    {
        return principal.getName();
    }
    
    private String append(JSRole role)
    {
        return role.getName();
    }

    private String append(JSGroup group)
    {
        return group.getName();
    }

    private String append(Object s)
    {
        if (s instanceof JSPrincipal) return append((JSPrincipal) s);
        if (s instanceof JSRole) return append((JSRole) s);
        if (s instanceof JSGroup) return append((JSGroup) s);

        return s.toString();
    }

    private String putTokens(List _list)
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

	public JSUserGroups getGroupString()
	{
		return groupString;
	}

	public JSUserRoles getRoleString()
	{
		return roleString;
	}

	public JSPWAttributes getPwData()
	{
		return pwData;
	}

    public String getPwDataValue(String key)
    {
        return getPwDataValue(key, null);
    }
    
    public String getPwDataValue(String key, String defValue)
    {
        String value = (this.pwData != null ? this.pwData.getMyMap().get(key) : null);
        return (value != null ? value : defValue);
    }
    
    public boolean getPwDataValueAsBoolean(String key)
    {
        return getPwDataValueAsBoolean(key, false);
    }
    
    public boolean getPwDataValueAsBoolean(String key, boolean defValue)
    {
        String sv = getPwDataValue(key);        
        return (sv != null ? Boolean.parseBoolean(sv) : defValue);
    }
    
    public Date getPwDataValueAsDate(String key)
    {
        return getPwDataValueAsDate(key, null);
    }
    
    public Date getPwDataValueAsDate(String key, Date defValue)
    {
        Date value = null;
        String sv = getPwDataValue(key, null);
        
        if (sv != null)
        {
            value = Date.valueOf(sv);
        }
        
        return (value != null ? value : defValue);
    }

    public void setPwData(JSPWAttributes pwData)
	{
		this.pwData = pwData;
	}

    
    public String getSubsite()
    {
        return subsite;
    }

    public void setSubsite(String subsite)
    {
        this.subsite = subsite;
    }

    
    public String getUserTemplate()
    {
        return userTemplate;
    }

    
    public void setUserTemplate(String userTemplate)
    {
        this.userTemplate = userTemplate;
    }

}