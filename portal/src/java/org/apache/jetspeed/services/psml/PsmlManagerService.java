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
package org.apache.jetspeed.services.psml;

import java.util.Iterator;
import java.util.List;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.fulcrum.Service;

/**
 * This service is responsible for loading and saving PSML documents.
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 *
 * @version $Id$
 */
public interface PsmlManagerService extends Service
{
    /** The name of the service */
    public String SERVICE_NAME = "PsmlManager";

    /**
     * Returns a PSML document of the given name.
     * For this implementation, the name must be the document
     * URL or absolute filepath
     *
     * @deprecated
     * @param name the name of the document to retrieve
     */
    public PSMLDocument getDocument( String name );

    /**
     * Returns a PSML document for the given locator
     *
     * @param locator The locator descriptor of the document to be retrieved.
     */
    public PSMLDocument getDocument( ProfileLocator locator );

    /** Given a ordered list of locators, find the first document matching
     *  a profile locator, starting from the beginning of the list and working
     *  to the end.
     *
     * @param locator The ordered list of profile locators.
     */
    public PSMLDocument getDocument( List locators );

    /** Store the PSML document on disk, using its locator
     * 
     * @param profile the profile locator description.
     * @return true if the operation succeeded
     */
    public boolean store(Profile profile);
    
    /** Create a new document.
     *
     * @param profile the profile to use
     * @return The newly created document.
     */
    public PSMLDocument createDocument( Profile profile );

    /** Remove a document.
     *
     * @param locator The description of the profile to be removed.
     */
    public void removeDocument( ProfileLocator locator );

    /** Removes all documents for a given user.
     *
     * @param user The user object.
     */
    public void removeUserDocuments( String user );

    /** Removes all documents for a given group.
     *
     * @param group The group object.
     */
    public void removeGroupDocuments( String group );

    /** Removes all documents for a given role.
     *
     * @param role The role object.
     */
    public void removeRoleDocuments( String role );

    /** Query for a collection of profiles given a profile locator criteria.
     *
     * @param locator The profile locator criteria.
     *
     * @return A collection of profiles that match the criteria specified in the locator.
     */
    public Iterator query( QueryLocator locator );

    /** Export profiles from this service into another service
     *
     * @param consumer The PSML consumer service, receives PSML from this service.
     * @param locator The profile locator criteria.
     *
     * @return The count of profiles exported.
     */
    public int export(PsmlManagerService consumer, QueryLocator locator);

}

