/*
 * Created on Oct 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.portletregistry;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * <p>
 * PortletRegistry
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface PortletRegistry
{
    Language createLanguage( Locale locale, String title, String shortTitle, String description, Collection keywords )
            throws RegistryException;

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
    MutablePortletApplication getPortletApplication( ObjectID id );

    /**
     * Retreives a PortletApplication by it's unique name.  We use
     * PortletApplicationComposite interface which extends the PortletApplication
     * and adds additional functionallity to it.
     * @param id 
     * @return PortletApplicationComposite
     */
    MutablePortletApplication getPortletApplication( String name );

    /**
     * Locates a portlet application using it's unique <code>identifier</code> 
     * field.
     * @param identifier Unique id for this portlet application
     * @return portlet application matching this unique id.
     */
    MutablePortletApplication getPortletApplicationByIdentifier( String identifier );

    List getPortletApplications();

    /**
     * Locates a portlet using it's unique <code>identifier</code> 
     * field.
     * <br/>
     * This method automatically calls {@link getStoreableInstance(PortletDefinitionComposite portlet)}
     * on the returned <code>PortletEntityInstance</code>
     * @param identifier Unique id for this portlet
     * @return Portlet matching this unique id.
     * @throws java.lang.IllegalStateException If <code>PortletDefinitionComposite != null</code> AND
     *  <code>PortletDefinitionComposite.getPortletApplicationDefinition() ==  null</code>.
     * The reason for this is that every PortletDefinition is required to
     * have a parent PortletApplicationDefinition
     */
    PortletDefinitionComposite getPortletDefinitionByIdentifier( String identifier );

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
     * @throws java.lang.IllegalStateException If <code>PortletDefinitionComposite != null</code> AND
     *  <code>PortletDefinitionComposite.getPortletApplicationDefinition() ==  null</code>.
     * The reason for this is that every PortletDefinition is required to
     * have a parent PortletApplicationDefinition
     * 
     */
    PortletDefinitionComposite getPortletDefinitionByUniqueName( String name );

    /**
     * Checks whether or not a portlet application with this identity has all ready
     * been registered to the container.
     * @param appIdentity portlet application indetity to check for.
     * @return boolean <code>true</code> if a portlet application with this identity
     * is alreay registered, <code>false</code> if it has not.
     */
    boolean portletApplicationExists( String appIentity );

    /**
     * Checks whether or not a portlet with this identity has all ready
     * been registered to the container.
     * @param portletIndentity portlet indetity to check for.
     * @return boolean <code>true</code> if a portlet with this identity
     * is alreay registered, <code>false</code> if it has not.
     */
    boolean portletDefinitionExists( String portletIndentity );

    /**
     * Checks whether or not a portlet with this identity has all ready
     * been registered to the PortletApplication.
     * @param portletIndentity portlet indetity to check for.
     * @param app PortletApplication to check .
     * @return boolean <code>true</code> if a portlet with this identity
     * is alreay registered, <code>false</code> if it has not.
     */
    boolean portletDefinitionExists( String portletName, MutablePortletApplication app );

    /**
     * Creates a new <code>PortletApplicationDefinition</code> 
     * within the Portal.          
     * @param newApp
     */
    void registerPortletApplication( PortletApplicationDefinition newApp ) throws RegistryException;

    void removeApplication( PortletApplicationDefinition app ) throws RegistryException;

    /**
     * Makes any changes to the <code>PortletApplicationDefinition</code>
     * persistent.
     * @param app
     */
    void updatePortletApplication( PortletApplicationDefinition app ) throws RegistryException;
    
    void savePortletDefinition(PortletDefinition portlet) throws RegistryException;

}