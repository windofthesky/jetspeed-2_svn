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

import org.apache.jetspeed.services.JetspeedServices;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.om.profile.Profile;


/**
 * Static accessor for the PsmlManagerService
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PsmlManager
{

    /** 
     * Commodity method for getting a reference to the service
     * singleton
     */
    public static PsmlManagerService getService()
    {
        return (PsmlManagerService)JetspeedServices
            .getInstance().getService(PsmlManagerService.SERVICE_NAME);
    }

    /**
     * Returns a PSML document for the given locator
     *
     * @param locator The locator descriptor of the document to be retrieved.
     */
    public static PSMLDocument getDocument( ProfileLocator locator )
    {
        return getService().getDocument(locator);
    }

    /** Given a ordered list of locators, find the first document matching
     *  a profile locator, starting from the beginning of the list and working
     *  to the end.
     *
     * @param locator The ordered list of profile locators.
     */
    public static PSMLDocument getDocument( List locators )
    {
        return getService().getDocument(locators);
    }

    /** Store the PSML document on disk, using its locator
     * 
     * @param profile the profile locator description.
     * @return true if the operation succeeded
     */
    public static boolean store(Profile profile)
    {
        return getService().store(profile);
    }

    /** Create a new document.
     *
     * @param profile The description and default value for the new document.
     * @return The newly created document.
     */
    public static PSMLDocument createDocument( Profile profile )
    {
        return getService().createDocument( profile );
    }

    /** Removes a document.
     *
     * @param locator The description of the profile resource to be removed.
     */
    public static void removeDocument( ProfileLocator locator )
    {
        getService().removeDocument( locator );
    }

    /** Removes all documents for a given user.
     *
     * @param user The user object.
     */
    public static void removeUserDocuments( String user )
    {
        getService().removeUserDocuments( user );
    }

    /** Removes all documents for a given group.
     *
     * @param group The group object.
     */
    public static void removeGroupDocuments( String group )
    {
        getService().removeGroupDocuments( group );
    }


    /** Removes all documents for a given role.
     *
     * @param role The role object.
     */
    public static void removeRoleDocuments( String role )
    {
        getService().removeRoleDocuments( role );
    }


    /** Query for a collection of profiles given a profile locator criteria.
     *
     * @param locator The profile locator criteria.
     */
    public static Iterator query( QueryLocator locator )
    {
        return getService().query( locator );
    }

    /** Export profiles from this service into another service
     *
     * @param consumer The PSML consumer service, receives PSML from this service.
     * @param locator The profile locator criteria.
     *
     * @return The count of profiles exported.
     */
    public int export(PsmlManagerService consumer, QueryLocator locator)
    {
        return getService().export(consumer, locator);
    }

}

