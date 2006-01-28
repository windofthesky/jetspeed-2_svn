package org.apache.jetspeed.om.page;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
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
     * @see org.apache.jetspeed.om.page.ContentPage#removeFragmentById(java.lang.String)
     */
    public Fragment removeFragmentById(String id)
    {
        // remove from underlying page
        Fragment removed = page.removeFragmentById(id);
        if (removed != null)
        {
            // reset content fragments if successfully removed
            if ((rootContentFragment != null) && rootContentFragment.getId().equals(id))
            {
                rootContentFragment = null;
            }
            cachedFragments.clear();
        }
        return removed;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getContentFragmentsByName(java.lang.String)
     */
    public List getContentFragmentsByName(String name)
    {
        // get list of fragments by name
        List fragments = page.getFragmentsByName(name);
        if (fragments == null)
        {
            return null;
        }

        // convert list elements to content fragments
        ListIterator fragmentsIter = fragments.listIterator();
        while (fragmentsIter.hasNext())
        {
            Fragment fragment = (Fragment)fragmentsIter.next();
            String fragmentId = fragment.getId();
            ContentFragment contentFragment = (ContentFragment)cachedFragments.get(fragmentId);
            if (contentFragment == null)
            {
                contentFragment = new ContentFragmentImpl(fragment, cachedFragments);
                cachedFragments.put(fragmentId, contentFragment);
            }
            fragmentsIter.set(contentFragment);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentPage#getFragmentsByName(java.lang.String)
     */
    public List getFragmentsByName(String name)
    {
        return getContentFragmentsByName(name);
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
     * @see org.apache.jetspeed.om.page.Page#getEffectiveDefaultDecorator(java.lang.String)
     */
    public String getEffectiveDefaultDecorator(String fragmentType)
    {        
        return page.getEffectiveDefaultDecorator(fragmentType);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getDefaultDecorator(java.lang.String)
     */
    public String getDefaultDecorator(String fragmentType)
    {        
        return page.getDefaultDecorator(fragmentType);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getSkin()
     */
    public String getSkin()
    {
        
        return page.getSkin();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setDefaultDecorator(java.lang.String, java.lang.String)
     */
    public void setDefaultDecorator(String decoratorName, String fragmentType)
    {
        
        page.setDefaultDecorator(decoratorName, fragmentType);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setSkin(java.lang.String)
     */
    public void setSkin(String skinName)
    {
        
        page.setSkin(skinName);
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
     * @see org.apache.jetspeed.om.page.Page#newMenuDefinition()
     */
    public MenuDefinition newMenuDefinition()
    {
        return page.newMenuDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuExcludeDefinition()
     */
    public MenuExcludeDefinition newMenuExcludeDefinition()
    {
        return page.newMenuExcludeDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuIncludeDefinition()
     */
    public MenuIncludeDefinition newMenuIncludeDefinition()
    {
        return page.newMenuIncludeDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuOptionsDefinition()
     */
    public MenuOptionsDefinition newMenuOptionsDefinition()
    {
        return page.newMenuOptionsDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuSeparatorDefinition()
     */
    public MenuSeparatorDefinition newMenuSeparatorDefinition()
    {
        return page.newMenuSeparatorDefinition();
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
     * @see org.apache.jetspeed.page.document.Node#setHidden(boolean)
     */
    public void setHidden(boolean hidden)
    {
        
        page.setHidden(hidden);
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
     * @see org.apache.jetspeed.om.common.SecuredResource#checkPermissions(int)
     */
    public void checkPermissions(int mask) throws SecurityException
    {
        
        page.checkPermissions(mask);
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
     * @see org.apache.jetspeed.om.common.SecuredResource#newSecurityConstraints()
     */
    public SecurityConstraints newSecurityConstraints()
    {
        
        return page.newSecurityConstraints();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#newSecurityConstraint()
     */
    public SecurityConstraint newSecurityConstraint()
    {
        
        return page.newSecurityConstraint();
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
    
    public String getVersion()
    {
        return page.getVersion();
    }
    
    public void setVersion(String version)
    {
        page.setVersion(version);
    }
}
