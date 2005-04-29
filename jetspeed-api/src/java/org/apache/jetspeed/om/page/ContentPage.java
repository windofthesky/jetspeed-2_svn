package org.apache.jetspeed.om.page;


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
 * Overridden to to indicate that the {@link Fragment} returned
 * must also be an instance of ContentFragment.
 *
 * @param id the fragment id to look for
 * @return the found ContentFragment object or null if not found
 */
 Fragment getFragmentById(String id);



/**
 * Overridden to to indicate that the {@link Fragment} returned
 * must also be an instance of ContentFragment.
 *
 * @return the base Fragment object for this page.
 */    
 Fragment getRootFragment();
}
