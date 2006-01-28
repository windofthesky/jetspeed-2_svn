package org.apache.jetspeed.om.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.decoration.Decoration;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;

public class ContentFragmentImpl implements ContentFragment
{
    

    private final Fragment fragment;
    private StringBuffer overridenContent;
    private PortletContent portletContent;
    private List contentFragments;
    private static final Log log = LogFactory.getLog(ContentFragmentImpl.class);
    private final Map cachedFragments;
    private Decoration decoration;
    

    public ContentFragmentImpl(Fragment fragment, Map cachedFagments)
    {
        this.fragment = fragment;
        this.cachedFragments = cachedFagments;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getContentFragments()
     */
    public List getContentFragments()
    {   
        if(contentFragments == null)
        {
           contentFragments = new ContentFragmentList();
        }
        return contentFragments;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getFragments()
     */
    public List getFragments()
    {
        return getContentFragments();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#getRenderedContent()
     */
    public String getRenderedContent() throws IllegalStateException
    {       
        if(overridenContent != null)
        {
            return overridenContent.toString();
        }
        
        
        if (portletContent != null)
        {
            //TODO are you sure? Intellij warns, synchronization on a non-final field is
            //unlikely to have useful semantics.
            synchronized (portletContent)
            {
                if (portletContent.isComplete())
                {
                    return portletContent.getContent();
                }
                else
                {
                    try
                    {
                        log.debug("Waiting on content for Fragment " + getId());
                        portletContent.wait();
                        return portletContent.getContent();
                    }
                    catch (InterruptedException e)
                    {
                        return e.getMessage();
                    }
                    finally
                    {
                        log.debug("Been notified that Faragment " + getId() + " is complete");
                    }
                }
            }
        }
        else
        {
            throw new IllegalStateException("You cannot invoke getRenderedContent() until the content has been set.");
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#overrideRenderedContent(java.lang.String)
     */
    public void overrideRenderedContent(String contnent)
    {
        if(overridenContent == null)
        {
            overridenContent = new StringBuffer();
        }
        overridenContent.append(contnent);
        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.ContentFragment#setPortletContent(org.apache.jetspeed.aggregator.PortletContent)
     */
    public void setPortletContent(PortletContent portletContent)
    {
        this.portletContent = portletContent;        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getDecorator()
     */
    public String getDecorator()
    {
        
        return fragment.getDecorator();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getName()
     */
    public String getName()
    {
        
        return fragment.getName();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getProperties()
     */
    public Map getProperties()
    {
        
        return fragment.getProperties();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getProperty(java.lang.String)
     */
    public String getProperty(String propName)
    {
        
        return fragment.getProperty(propName);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getIntProperty(java.lang.String)
     */
    public int getIntProperty(String propName)
    {
        
        return fragment.getIntProperty(propName);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getSkin()
     */
    public String getSkin()
    {
        
        return fragment.getSkin();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getState()
     */
    public String getState()
    {
        
        return fragment.getState();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getType()
     */
    public String getType()
    {
        
        return fragment.getType();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#isReference()
     */
    public boolean isReference()
    {
        
        return fragment.isReference();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setDecorator(java.lang.String)
     */
    public void setDecorator(String decoratorName)
    {
        
        fragment.setDecorator(decoratorName);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setName(java.lang.String)
     */
    public void setName(String name)
    {
        
        fragment.setName(name);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutRow()
     */
    public int getLayoutRow()
    {
        return fragment.getLayoutRow();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutColumn()
     */
    public int getLayoutColumn()
    {
        return fragment.getLayoutColumn();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getLayoutSizes()
     */
    public String getLayoutSizes()
    {
        return fragment.getLayoutSizes();
    }

    public void setLayoutRow(int row)
    {
        fragment.setLayoutRow(row);
    }
    
    public void setLayoutColumn(int column)
    {
        fragment.setLayoutColumn(column);
    }
    
    public void setLayoutSizes(String sizes)
    {
        fragment.setLayoutSizes(sizes);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setSkin(java.lang.String)
     */
    public void setSkin(String skinName)
    {
        
        fragment.setSkin(skinName);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setState(java.lang.String)
     */
    public void setState(String state)
    {
        
        fragment.setState(state);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setType(java.lang.String)
     */
    public void setType(String type)
    {
        
        fragment.setType(type);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#getId()
     */
    public String getId()
    {
        
        return fragment.getId();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#getShortTitle()
     */
    public String getShortTitle()
    {
        
        return fragment.getShortTitle();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#getTitle()
     */
    public String getTitle()
    {
        
        return fragment.getTitle();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#setShortTitle(java.lang.String)
     */
    public void setShortTitle(String title)
    {
        
        fragment.setShortTitle(title);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#setTitle(java.lang.String)
     */
    public void setTitle(String title)
    {
        
        fragment.setTitle(title);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#checkAccess(java.lang.String)
     */
    public void checkAccess(String actions) throws SecurityException
    {
        
        fragment.checkAccess(actions);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#checkConstraints(java.lang.String)
     */
    public void checkConstraints(String actions) throws SecurityException
    {
        
        fragment.checkConstraints(actions);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#checkPermissions(int)
     */
    public void checkPermissions(int mask) throws SecurityException
    {
        
        fragment.checkPermissions(mask);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getConstraintsEnabled()
     */
    public boolean getConstraintsEnabled()
    {
        
        return fragment.getConstraintsEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getPermissionsEnabled()
     */
    public boolean getPermissionsEnabled()
    {
        
        return fragment.getPermissionsEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#getSecurityConstraints()
     */
    public SecurityConstraints getSecurityConstraints()
    {
        
        return fragment.getSecurityConstraints();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#newSecurityConstraints()
     */
    public SecurityConstraints newSecurityConstraints()
    {
        
        return fragment.newSecurityConstraints();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#newSecurityConstraint()
     */
    public SecurityConstraint newSecurityConstraint()
    {
        
        return fragment.newSecurityConstraint();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#setSecurityConstraints(org.apache.jetspeed.om.common.SecurityConstraints)
     */
    public void setSecurityConstraints(SecurityConstraints constraints)
    {
        fragment.setSecurityConstraints(constraints);
    }
    
    
    /**
     * Checks the ContentFragment cache for a ContentFragment
     * that matches the <code>Id</code> of this fragment.  If
     * one is found, it returned.  If no matches are found, a new
     * <code>ContentFragment</code> represnentive of the {@link Fragment}
     * argument is subsequently created, stored into the cahce and returned. 
     * 
     * @param f
     * @return ContentFrament
     */
    protected ContentFragment getContentFragment(Fragment f)
    {
        ContentFragment cf;
        if(cachedFragments.containsKey(f.getId()))
        {
            cf = (ContentFragment) cachedFragments.get(f.getId());
        }
        else
        {
            cf = new ContentFragmentImpl(f, cachedFragments);
            cachedFragments.put(f.getId(), cf);
        }
        return cf;
    }
    
    
    protected final class ContentFragmentList implements List, Serializable
    {
        private List baseList = fragment.getFragments();

        /* (non-Javadoc)
         * @see java.util.List#add(int, java.lang.Object)
         */
        public void add(int index, Object element)
        {
            
            baseList.add(index, element);
        }

        /* (non-Javadoc)
         * @see java.util.List#add(java.lang.Object)
         */
        public boolean add(Object o)
        {
            
            return baseList.add(o);
        }

        /* (non-Javadoc)
         * @see java.util.List#addAll(int, java.util.Collection)
         */
        public boolean addAll(int index, Collection c)
        {
            
            return baseList.addAll(index, c);
        }

        /* (non-Javadoc)
         * @see java.util.List#addAll(java.util.Collection)
         */
        public boolean addAll(Collection c)
        {
            
            return baseList.addAll(c);
        }

        /* (non-Javadoc)
         * @see java.util.List#clear()
         */
        public void clear()
        {
            
            baseList.clear();
        }

        /* (non-Javadoc)
         * @see java.util.List#contains(java.lang.Object)
         */
        public boolean contains(Object o)
        {
            
            return baseList.contains(o);
        }

        /* (non-Javadoc)
         * @see java.util.List#containsAll(java.util.Collection)
         */
        public boolean containsAll(Collection c)
        {
            
            return baseList.containsAll(c);
        }

        /* (non-Javadoc)
         * @see java.util.List#equals(java.lang.Object)
         */
        public boolean equals(Object o)
        {
            
            return baseList.equals(o);
        }

        /* (non-Javadoc)
         * @see java.util.List#get(int)
         */
        public Object get(int index)
        {
            Fragment f= (Fragment) baseList.get(index);
            return getContentFragment(f);            
        }

        /* (non-Javadoc)
         * @see java.util.List#hashCode()
         */
        public int hashCode()
        {
            
            return baseList.hashCode();
        }

        /* (non-Javadoc)
         * @see java.util.List#indexOf(java.lang.Object)
         */
        public int indexOf(Object o)
        {
            
            return baseList.indexOf(o);
        }

        /* (non-Javadoc)
         * @see java.util.List#isEmpty()
         */
        public boolean isEmpty()
        {
            
            return baseList.isEmpty();
        }

        /* (non-Javadoc)
         * @see java.util.List#iterator()
         */
        public Iterator iterator()
        {
            return duplicateList().iterator();
        }

        /* (non-Javadoc)
         * @see java.util.List#lastIndexOf(java.lang.Object)
         */
        public int lastIndexOf(Object o)
        {
            
            return baseList.lastIndexOf(o);
        }

        /* (non-Javadoc)
         * @see java.util.List#listIterator()
         */
        public ListIterator listIterator()
        {
            return duplicateList().listIterator();
        }

        /* (non-Javadoc)
         * @see java.util.List#listIterator(int)
         */
        public ListIterator listIterator(int index)
        {
            return duplicateList().listIterator(index);
        }

        /* (non-Javadoc)
         * @see java.util.List#remove(int)
         */
        public Object remove(int index)
        {
            
            return baseList.remove(index);
        }

        /* (non-Javadoc)
         * @see java.util.List#remove(java.lang.Object)
         */
        public boolean remove(Object o)
        {
            
            return baseList.remove(o);
        }

        /* (non-Javadoc)
         * @see java.util.List#removeAll(java.util.Collection)
         */
        public boolean removeAll(Collection c)
        {
            
            return baseList.removeAll(c);
        }

        /* (non-Javadoc)
         * @see java.util.List#retainAll(java.util.Collection)
         */
        public boolean retainAll(Collection c)
        {
            
            return baseList.retainAll(c);
        }

        /* (non-Javadoc)
         * @see java.util.List#set(int, java.lang.Object)
         */
        public Object set(int index, Object element)
        {
            
            return baseList.set(index, element);
        }

        /* (non-Javadoc)
         * @see java.util.List#size()
         */
        public int size()
        {
            
            return baseList.size();
        }

        /* (non-Javadoc)
         * @see java.util.List#subList(int, int)
         */
        public List subList(int fromIndex, int toIndex)
        {
            return duplicateList().subList(fromIndex, toIndex);
        }



        /* (non-Javadoc)
         * @see java.util.List#toArray()
         */
        public Object[] toArray()
        {
            return duplicateList().toArray();
        }

        /* (non-Javadoc)
         * @see java.util.List#toArray(java.lang.Object[])
         */
        public Object[] toArray(Object[] a)
        {
              return duplicateList().toArray(a);
        }
        
        private List duplicateList()
        {            
            List rFragList = new ArrayList(baseList.size());
            for(int i=0; i < baseList.size(); i++)
            {                
                Fragment f = (Fragment)baseList.get(i);
                ContentFragment cf = getContentFragment(f);
                rFragList.add(cf);
            }
            return rFragList;
        }
        
        

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#getPreferences()
     */
    public List getPreferences()
    {
        return fragment.getPreferences();
    }

    public Decoration getDecoration()
    {
        return decoration;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Fragment#setPreferences(java.util.List)
     */
    public void setPreferences(List preferences)
    {
        fragment.setPreferences(preferences);
    }


    public void setDecoration(Decoration decoration)
    {
        this.decoration = decoration;
        
    }
    
}
