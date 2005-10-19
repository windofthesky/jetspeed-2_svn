package org.apache.jetspeed.page.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.folder.impl.FolderImpl;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.Property;
import org.apache.jetspeed.om.page.impl.FragmentImpl;
import org.apache.jetspeed.om.page.impl.PageImpl;
import org.apache.jetspeed.page.DelegatingPageManager;
import org.apache.jetspeed.page.FolderNotRemovedException;
import org.apache.jetspeed.page.FolderNotUpdatedException;
import org.apache.jetspeed.page.LinkNotRemovedException;
import org.apache.jetspeed.page.LinkNotUpdatedException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageManagerEventListener;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.PageNotRemovedException;
import org.apache.jetspeed.page.PageNotUpdatedException;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.UnsupportedDocumentTypeException;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;

/**
 * DatabasePageManager
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */

public class DatabasePageManager extends InitablePersistenceBrokerDaoSupport
        implements PageManager
{
    private static Map modelClasses = new HashMap();
    static
    {
        modelClasses.put("FragmentImpl.class", FragmentImpl.class);
        modelClasses.put("PageImpl.class", PageImpl.class);
        modelClasses.put("FolderImpl.class", FolderImpl.class);
        //modelClasses.put("LinkImpl.class", LinkImpl.class);
        //modelClasses.put("PropertyImpl.class", PropertyImpl.class);
        //modelClasses.put("MenuDefinitionImpl.class", MenuDefinitionImpl.class);
        //modelClasses.put("MenuExcludeDefinitionImpl.class", MenuExcludeDefinitionImpl.class);
        //modelClasses.put("MenuIncludeDefinitionImpl.class", MenuIncludeDefinitionImpl.class);
        //modelClasses.put("MenuOptionsDefinitionImpl.class", MenuOptionsDefinitionImpl.class);
        //modelClasses.put("MenuSeparatorDefinitionImpl.class", MenuSeparatorDefinitionImpl.class);
        //modelClasses.put("SecurityConstraintsImpl.class", SecurityConstraintsImpl.class);
        //modelClasses.put("SecurityConstraintImpl.class", SecurityConstraintImpl.class);
    }

    private DelegatingPageManager delegator;
    
    public DatabasePageManager(
            String repositoryPath,
            boolean isPermissionsSecurity, 
            boolean isConstraintsSecurity)

    {
        super(repositoryPath);
        System.out.println("Page Manager repo = " + repositoryPath);
        delegator = new DelegatingPageManager(isPermissionsSecurity, isConstraintsSecurity, modelClasses);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getConstraintsEnabled()
     */
    public boolean getConstraintsEnabled()
    {
        return delegator.getConstraintsEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPermissionsEnabled()
     */
    public boolean getPermissionsEnabled()
    {
        return delegator.getPermissionsEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPage(java.lang.String)
     */
    public Page newPage(String path)
    {
        return delegator.newPage(path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFolder(java.lang.String)
     */
    public Folder newFolder(String path)
    {
        return delegator.newFolder(path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newLink(java.lang.String)
     */
    public Link newLink(String path)
    {
        return delegator.newLink(path);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFragment()
     */
    public Fragment newFragment()
    {
        return delegator.newFragment();    
    }

    public Fragment newPortletFragment()
    {
        return delegator.newPortletFragment();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newProperty()
     */
    public Property newProperty()
    {
        return delegator.newProperty();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newMenuDefinition()
     */
    public MenuDefinition newMenuDefinition()
    {
        return delegator.newMenuDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newMenuExcludeDefinition()
     */
    public MenuExcludeDefinition newMenuExcludeDefinition()
    {
        return delegator.newMenuExcludeDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newMenuIncludeDefinition()
     */
    public MenuIncludeDefinition newMenuIncludeDefinition()
    {
        return delegator.newMenuIncludeDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newMenuOptionsDefinition()
     */
    public MenuOptionsDefinition newMenuOptionsDefinition()
    {
        return delegator.newMenuOptionsDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newMenuSeparatorDefinition()
     */
    public MenuSeparatorDefinition newMenuSeparatorDefinition()
    {
        return delegator.newMenuSeparatorDefinition();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newSecurityConstraints()
     */
    public SecurityConstraints newSecurityConstraints()
    {
        return delegator.newSecurityConstraints();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newSecurityConstraint()
     */
    public SecurityConstraint newSecurityConstraint()
    {
        return delegator.newSecurityConstraint();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPage(java.lang.String)
     */
    public Page getPage(String path) throws PageNotFoundException, NodeException
    {
        Criteria filter = new Criteria();
        filter.addEqualTo("path", path);
        QueryByCriteria query = QueryFactory.newQuery(PageImpl.class, filter);
        Page page = (Page) getPersistenceBrokerTemplate().getObjectByQuery(query);
        if (page == null)
        {
            throw new PageNotFoundException("Page " + path + " not found.");
        }
        return page;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getContentPage(java.lang.String)
     */
    public ContentPage getContentPage(String path)
            throws PageNotFoundException, NodeException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getLink(java.lang.String)
     */
    public Link getLink(String name) throws DocumentNotFoundException,
            UnsupportedDocumentTypeException, FolderNotFoundException,
            NodeException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPageSecurity()
     */
    public PageSecurity getPageSecurity() throws DocumentNotFoundException,
            UnsupportedDocumentTypeException, FolderNotFoundException,
            NodeException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getFolder(java.lang.String)
     */
    public Folder getFolder(String folderPath) throws FolderNotFoundException,
            InvalidFolderException, NodeException
    {
        Criteria filter = new Criteria();
        filter.addEqualTo("path", folderPath);
        QueryByCriteria query = QueryFactory.newQuery(FolderImpl.class, filter);
        Folder folder = (Folder) getPersistenceBrokerTemplate().getObjectByQuery(query);
        if (folder == null)
        {
            throw new FolderNotFoundException("Folder " + folderPath + " not found.");
        }
        return folder;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updatePage(org.apache.jetspeed.om.page.Page)
     */
    public void updatePage(Page page) throws JetspeedException,
            PageNotUpdatedException
    {
        System.out.println("storing page " + page.getPath());

        // look up and set parent folder if necessary
        if (page.getParent() == null)
        {
            // access folder by path
            String pagePath = page.getPath();
            String parentPath = pagePath.substring(0, pagePath.lastIndexOf(Folder.PATH_SEPARATOR));
            if (parentPath.length() == 0)
            {
                parentPath = Folder.PATH_SEPARATOR;
            }
            FolderImpl parent = null;
            try
            {
                parent = (FolderImpl)getFolder(parentPath);
            }
            catch (FolderNotFoundException fnfe)
            {
                throw new PageNotUpdatedException("Missing parent folder: " + parentPath);
            }

            // update page and parent folder
            parent.addPage((PageImpl)page);
            page.setParent(parent);
            getPersistenceBrokerTemplate().store(parent);
            getPersistenceBrokerTemplate().store(page);
        }
        else
        {
            // update page
            getPersistenceBrokerTemplate().store(page);
        }

        System.out.println("**** stored page " + page.getPath());        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removePage(org.apache.jetspeed.om.page.Page)
     */
    public void removePage(Page page) throws JetspeedException,
            PageNotRemovedException
    {
        System.out.println("deleting page " + page.getPath());

        // look up and update parent folder if necessary
        if (page.getParent() != null)
        {
            // delete page and update parent folder
            FolderImpl parent = (FolderImpl)page.getParent();
            parent.removePage((PageImpl)page);
            getPersistenceBrokerTemplate().store(parent);
            getPersistenceBrokerTemplate().delete(page);
        }
        else
        {
            // delete page
            getPersistenceBrokerTemplate().delete(page);
        }

        System.out.println("**** deleted page " + page.getPath());        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updateFolder(org.apache.jetspeed.om.folder.Folder)
     */
    public void updateFolder(Folder folder) throws JetspeedException,
            FolderNotUpdatedException
    {
        System.out.println("storing folder " + folder.getPath());

        // look up and set parent folder if necessary
        if ((folder.getParent() == null) && !folder.getPath().equals(Folder.PATH_SEPARATOR))
        {
            // access folder by path
            String folderPath = folder.getPath();
            String parentPath = folderPath.substring(0, folderPath.lastIndexOf(Folder.PATH_SEPARATOR));
            if (parentPath.length() == 0)
            {
                parentPath = Folder.PATH_SEPARATOR;
            }
            FolderImpl parent = null;
            try
            {
                parent = (FolderImpl)getFolder(parentPath);
            }
            catch (FolderNotFoundException fnfe)
            {
                throw new FolderNotUpdatedException("Missing parent folder: " + parentPath);
            }

            // update folder and parent folder
            parent.addFolder((FolderImpl)folder);
            folder.setParent(parent);
            getPersistenceBrokerTemplate().store(parent);
            getPersistenceBrokerTemplate().store(folder);
        }
        else
        {
            // update folder
            getPersistenceBrokerTemplate().store(folder);
        }

        System.out.println("**** stored folder " + folder.getPath());        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removeFolder(org.apache.jetspeed.om.folder.Folder)
     */
    public void removeFolder(Folder folder) throws JetspeedException,
            FolderNotRemovedException
    {
        System.out.println("deleting folder " + folder.getPath());

        // look up and update parent folder if necessary
        if (folder.getParent() != null)
        {
            // delete folder and update parent folder
            FolderImpl parent = (FolderImpl)folder.getParent();
            parent.removeFolder((FolderImpl)folder);
            getPersistenceBrokerTemplate().store(parent);
            getPersistenceBrokerTemplate().delete(folder);
        }
        else
        {
            // delete folder
            getPersistenceBrokerTemplate().delete(folder);
        }

        System.out.println("**** deleted folder " + folder.getPath());        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updateLink(org.apache.jetspeed.om.page.Link)
     */
    public void updateLink(Link link) throws JetspeedException,
            LinkNotUpdatedException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removeLink(org.apache.jetspeed.om.page.Link)
     */
    public void removeLink(Link link) throws JetspeedException,
            LinkNotRemovedException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#addListener(org.apache.jetspeed.page.PageManagerEventListener)
     */
    public void addListener(PageManagerEventListener listener)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removeListener(org.apache.jetspeed.page.PageManagerEventListener)
     */
    public void removeListener(PageManagerEventListener listener)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#clonePage(org.apache.jetspeed.om.page.Page, java.lang.String)
     */
    public Page clonePage(Page source, String path) throws JetspeedException,
            PageNotUpdatedException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
