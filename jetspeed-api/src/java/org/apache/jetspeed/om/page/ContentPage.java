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
package org.apache.jetspeed.om.page;

import java.util.List;

/**
 * PageFragment is a volatile wrapper around a 
 * {@link org.apache.jetspeed.om.page.Page} metadata
 * object for use in rendering.  As with 
 * the {@link org.apache.jetspeed.om.page.Fragment} object,
 * <code>Page</code> objects are persistent, single-instance 
 * metadata objects that should not be used to hold per-request
 * content.  ContentPage solves this by providing a thin, wrapper
 * interface that can be used for rendering requested content associated
 * with the wrapped page relative to the currect user-request. 
 * 
 * @author weaver@apache.org
 *
 */
public interface ContentPage extends Page
{
  /**
   * Provides access to a per-request safe ContentFragment.
   * ContentFragments add the additional ability to temporarily
   * store rendered content of the current request along with
   * original, persistent metadata of the Fragment itself.
   * 
   * @return ContentFragment wrapping the actual root Fragment.
   */
  ContentFragment getRootContentFragment();
  
  

  /**
   * Returns a ContentFragment that wraps the actual
   * Fragment metadata represented by the id argument.
   * @param id unique id of the Fragment we want to retrieve.
   * @return
   */
  ContentFragment getContentFragmentById(String id);



  /**
   * Returns a list of ContentFragment that wrap the actual
   * Fragment metadata represented by the name argument.
   * @param name name of the Fragments we want to retrieve.
   * @return
   */
  List getContentFragmentsByName(String name);



/**
 * Overridden to to indicate that the {@link Fragment} returned
 * must also be an instance of ContentFragment.
 *
 * @param id the fragment id to look for
 * @return the found ContentFragment object or null if not found
 */
 Fragment getFragmentById(String id);



/**
 * Overridden to to indicate that the list of {@link Fragment}
 * instances returned must also be instances of ContentFragment.
 *
 * @param name the fragments name to look for
 * @return the list of found ContentFragment object or null if not found
 */
 List getFragmentsByName(String name);



/**
 * Overridden to to indicate that the {@link Fragment} returned
 * must also be an instance of ContentFragment.
 *
 * @return the base Fragment object for this page.
 */    
 Fragment getRootFragment();
}
