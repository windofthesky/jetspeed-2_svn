/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.components.portletregsitry;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;

/**
 * <p>
 * PortletRegistryComponentImpl
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public interface PortletRegistryComponent
{
    Language createLanguage(
        Locale locale,
        String title,
        String shortTitle,
        String description,
        Collection keywords)  throws RegistryException;
    List getAllPortletDefinitions();
    /**
     * Retreives a PortletApplication by it's unique ObjectID.
     * The unqiue ObjectID is generally a function of the native
     * storage mechanism of the container whether it be auto-generated
     * by an RDBMS, O/R tool or some other mechanism.
     * This is different than the portlet applaiction's unique indentfier
     * which is specified within the portlet.xml
     * @param id 
     * @return
     */
    MutablePortletApplication getPortletApplication(ObjectID id);
    /**
        * Retreives a PortletApplication by it's unique name.  We use
        * PortletApplicationComposite interface which extends the PortletApplication
        * and adds additional functionallity to it.
        * @param id 
        * @return PortletApplicationComposite
        */
    MutablePortletApplication getPortletApplication(String name);
    /**
     * Locates a portlet application using it's unique <code>indentifier</code> 
     * field.
     * @param ident Unique id for this portlet application
     * @return portlet application matching this unique id.
    */
    MutablePortletApplication getPortletApplicationByIndetifier(String ident);
    List getPortletApplications();
    /**
     * Locates a portlet using it's unique <code>indentifier</code> 
     * field.
     * <br/>
     * This method automatically calls {@link getStoreableInstance(PortletDefinitionComposite portlet)}
     * on the returned <code>PortletEntityInstance</code>
     * @param ident Unique id for this portlet
     * @return Portlet matching this unique id.
     */
    PortletDefinitionComposite getPortletDefinitionByIndetifier(String ident);
    /**
     * unique name is a string formed by the combination of a portlet's
     * unique within it's parent application plus the parent application's
     * unique name within the portlet container using ":" as a delimiter. 
     * <br/>
     * <strong>FORMAT: </strong> <i>application name</i>::<i>portlet name</i>
     * <br/>
     * <strong>EXAMPLE: </strong> com.myapp.portletApp1::weather-portlet
     * <br/>
     * This methos automatically calls {@link getStoreableInstance(PortletDefinitionComposite portlet)}
     * on the returned <code>PortletEntityInstance</code> 
     * @param name portlets unique name.  
     * @return Portlet that matches the unique name 
     */
    PortletDefinitionComposite getPortletDefinitionByUniqueName(String name);
 
    /**
      * Checks whether or not a portlet application with this identity has all ready
      * been registered to the container.
      * @param appIdentity portlet application indetity to check for.
      * @return boolean <code>true</code> if a portlet application with this identity
      * is alreay registered, <code>false</code> if it has not.
      */
    boolean portletApplicationExists(String appIentity);
    /**
     * Checks whether or not a portlet with this identity has all ready
     * been registered to the container.
     * @param portletIndentity portlet indetity to check for.
     * @return boolean <code>true</code> if a portlet with this identity
     * is alreay registered, <code>false</code> if it has not.
     */
    boolean portletDefinitionExists(String portletIndentity);
    /**
        * Checks whether or not a portlet with this identity has all ready
        * been registered to the PortletApplication.
        * @param portletIndentity portlet indetity to check for.
        * @param app PortletApplication to check .
        * @return boolean <code>true</code> if a portlet with this identity
        * is alreay registered, <code>false</code> if it has not.
        */
    boolean portletDefinitionExists(String portletName, MutablePortletApplication app);
    /**
        * Creates a new <code>PortletApplicationDefinition</code> 
        * within the Portal.          
        * @param newApp
        */
    void registerPortletApplication(PortletApplicationDefinition newApp) throws RegistryException;
    void removeApplication(PortletApplicationDefinition app) throws RegistryException;
    /**
        * Makes any changes to the <code>PortletApplicationDefinition</code>
        * persistent.
        * @param app
        */
    void updatePortletApplication(PortletApplicationDefinition app) throws RegistryException;
    
    /**
     * 
     * <p>
     * getStoreableInstance
     * </p>
     * Wraps the <code>PortletDefinitionComposite</code>
     * in an instance that correctly implements the 
     * <code>store()</code> method;
     * 
     * @param portlet
     * @return
     *
     */
	PortletDefinitionComposite getStoreableInstance(PortletDefinitionComposite portlet);

}
