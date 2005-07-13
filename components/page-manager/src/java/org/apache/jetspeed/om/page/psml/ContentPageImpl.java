package org.apache.jetspeed.om.page.psml;

import java.util.HashMap;
import java.util.Locale;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.document.Node;

public class ContentPageImpl implements ContentPage
{
    private final Page page;
    private final Map cachedFragments;
    private ContentFragment rootContentFragment;
    
    public ContentPageImpl(Page page)
    {
        this.page = page;
        this.cachedFragments = new HashMap();
    }

    public String toString()
    {
        return page.toString();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getContentFragmentById(java.lang.String)
     */
    public ContentFragment getContentFragmentById(String id)
    {
        ContentFragment contentFragment = null;
        if(cachedFragments.containsKey(id))
        {
            contentFragment = (ContentFragment) cachedFragments.get(id);
        }
        else
        {
            Fragment f = page.getFragmentById(id);
            if(f != null)
            {
                contentFragment = new ContentFragmentImpl(f, cachedFragments);
                cachedFragments.put(id, contentFragment);                
            }
        }
        return contentFragment;        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getFragmentById(java.lang.String)
     */
    public Fragment getFragmentById(String id)
    {
        return getContentFragmentById(id);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getRootContentFragment()
     */
    public ContentFragment getRootContentFragment()
    {
        if(rootContentFragment == null)
        {
            rootContentFragment = new ContentFragmentImpl(page.getRootFragment(), cachedFragments);
            cachedFragments.put(rootContentFragment.getId(), rootContentFragment);
        }
        return rootContentFragment;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#setRootFragment(org.apache.jetspeed.om.page.Fragment)
     */
    public Fragment getRootFragment()
    {
        return getRootContentFragment();        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#clone()
     */
    public Object clone() throws CloneNotSupportedException
    {
        
        return new ContentPageImpl((Page)page.clone());
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getDefaultDecorator(java.lang.String)
     */
    public String getDefaultDecorator(String fragmentType)
    {        
        return page.getDefaultDecorator(fragmentType);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getDefaultSkin()
     */
    public String getDefaultSkin()
    {
        
        return page.getDefaultSkin();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setDefaultDecorator(java.lang.String, java.lang.String)
     */
    public void setDefaultDecorator(String decoratorName, String fragmentType)
    {
        
        page.setDefaultDecorator(decoratorName, fragmentType);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setDefaultSkin(java.lang.String)
     */
    public void setDefaultSkin(String skinName)
    {
        
        page.setDefaultSkin(skinName);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setRootFragment(org.apache.jetspeed.om.page.Fragment)
     */
    public void setRootFragment(Fragment fragment)
    {
        
        page.setRootFragment(fragment);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getMenuDefinitions()
     */
    public List getMenuDefinitions()
    {
        return page.getMenuDefinitions();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setMenuDefinitions(java.util.List)
     */
    public void setMenuDefinitions(List definitions)
    {
        page.setMenuDefinitions(definitions);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getMetadata()
     */
    public GenericMetadata getMetadata()
    {
        
        return page.getMetadata();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getName()
     */
    public String getName()
    {
        
        return page.getName();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getParent()
     */
    public Node getParent()
    {
        
        return page.getParent();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getPath()
     */
    public String getPath()
    {
        
        return page.getPath();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getShortTitle(java.util.Locale)
     */
    public String getShortTitle(Locale locale)
    {
        
        return page.getShortTitle(locale);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getTitle(java.util.Locale)
     */
    public String getTitle(Locale locale)
    {
        
        return page.getTitle(locale);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getType()
     */
    public String getType()
    {
        
        return page.getType();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getUrl()
     */
    public String getUrl()
    {
        
        return page.getUrl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#isHidden()
     */
    public boolean isHidden()
    {
        
        return page.isHidden();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#setParent(org.apache.jetspeed.page.document.Node)
     */
    public void setParent(Node parent)
    {
        
        page.setParent(parent);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#setPath(java.lang.String)
     */
    public void setPath(String path)
    {
        
        page.setPath(path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#checkAccess(java.lang.String)
     */
    public void checkAccess(String actions) throws SecurityException
    {
        
        page.checkAccess(actions);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#checkConstraints(java.lang.String)
     */
    public void checkConstraints(String actions) throws SecurityException
    {
        
        page.checkConstraints(actions);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#checkPermissions(java.lang.String)
     */
    public void checkPermissions(String actions) throws SecurityException
    {
        
        page.checkPermissions(actions);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getConstraintsEnabled()
     */
    public boolean getConstraintsEnabled()
    {
        
        return page.getConstraintsEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getPermissionsEnabled()
     */
    public boolean getPermissionsEnabled()
    {
        
        return page.getPermissionsEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getSecurityConstraints()
     */
    public SecurityConstraints getSecurityConstraints()
    {
        
        return page.getSecurityConstraints();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#setSecurityConstraints(org.apache.jetspeed.om.common.SecurityConstraints)
     */
    public void setSecurityConstraints(SecurityConstraints constraints)
    {
        
        page.setSecurityConstraints(constraints);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#getId()
     */
    public String getId()
    {
        
        return page.getId();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#getShortTitle()
     */
    public String getShortTitle()
    {
        
        return page.getShortTitle();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#getTitle()
     */
    public String getTitle()
    {
        
        return page.getTitle();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#setId(java.lang.String)
     */
    public void setId(String id)
    {
        
        page.setId(id);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#setShortTitle(java.lang.String)
     */
    public void setShortTitle(String title)
    {        
        page.setShortTitle(title);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#setTitle(java.lang.String)
     */
    public void setTitle(String title)
    {
        
        page.setTitle(title);
    }
    
    /**
     * getPage - access wrapped page
     *
     * @return wrapped page
     */
    public Page getPage()
    {
        return page;
    }
}
