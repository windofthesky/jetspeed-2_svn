package org.apache.jetspeed.om.page;

import java.util.List;

import org.apache.jetspeed.aggregator.PortletContent;


/**
 * 
 * ContentFragment provides a volatile wrapper interface for
 * actual {@link org.apache.jetspeed.om.page.Fragment} metadata
 * objects.  Since Fragments are cached and are not request specific
 * they cannot be used to store request-level content.  This is where
 * we use the <code>ContentFragment</code> to solve this problem.
 * 
 * @author weaver@apache.org
 *
 */
public interface ContentFragment extends Fragment
{
    /**
     * Provides a list of of child ContentFragments that wrap
     * the actual Fragment metadata objects.
     * @return
     */
   List getContentFragments();

    /**     
     * Overridden to make it clear to the implemetor the {@link List}
     * returned <strong>MUST</strong> ContentFragments and not
     * just regular {@link org.apache.jetspeed.om.page.Fragment}s
     *
     * @return a collection containing ContentFragment objects
     */
    public List getFragments();

    /**
     * 
     * <p>
     * getRenderedContent
     * </p>
     * <p>
     *   Returns the raw,undecorated content of this fragment.  If
     *   overridenContent has been set and portlet content has not,
     *   overridden content should be returned.
     * </p>
     *  
     * @return The raw,undecorated content of this fragment.
     * @throws java.lang.IllegalStateException if the content has not yet been set.
     */
    public String getRenderedContent() throws IllegalStateException;

    /**
     * 
     * <p>
     * overrideRenderedContent
     * </p>
     * <p>
     * Can be used to store errors that may have occurred during the
     * rendering process.
     * </p>
     *
     * @param contnent
     */
    public void overrideRenderedContent(String contnent);

    /**
     * 
     * <p>
     * setPortletContent
     * </p>
     *
     * @param portletContent
     */
    public void setPortletContent(PortletContent portletContent);
}
