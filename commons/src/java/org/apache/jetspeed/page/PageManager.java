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

import java.util.List;

import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Property;
import org.apache.jetspeed.profiler.ProfileLocator;

/**
 * This service is responsible for loading and saving Pages into
 * the selected persistent store.
 *
 * @version $Id$
 */
public interface PageManager 
{
    /** The name of the service */
    public String SERVICE_NAME = "PageManager";

    /**
     * Creates a new empty Page instance
     *
     * @return a newly created Page object
     */
    public Page newPage();

    /**
     * Creates a new empty Fragment instance
     *
     * @return a newly created Fragment object
     */
    public Fragment newFragment();

    /**
     * Creates a new empty Property instance
     *
     * @return a newly created Property object
     */
    public Property newProperty();

    /**
     * Returns a PSML document for the given key
     *
     * @param locator The locator descriptor of the document to be retrieved.
     */
    public Page getPage(String id);

    /**
     * Returns a PSML document for the given locator
     *
     * @param locator The locator descriptor of the document to be retrieved.
     */
    public Page getPage(ProfileLocator locator);

    /** Query for a collection of profiles given a profile locator criteria.
     *
     * @param locator The profile locator criteria.
     *
     * @return A collection of profiles that match the criteria specified in the locator.
     */
    public List listPages();

    /** Store the PSML document on disk, using its locator
     *
     * @param profile the profile locator description.
     * @return true if the operation succeeded
     */
    public void registerPage(Page page) throws JetspeedException;

    /** Update a page in persistent storage
     *
     * @param locator The description of the profile to be removed.
     */
    public void updatePage(Page page) throws JetspeedException, PageNotUpdatedException;

    /** Remove a document.
     *
     * @param locator The description of the profile to be removed.
     */
    public void removePage(Page page)  throws PageNotRemovedException;

}

