package org.apache.jetspeed.profiler.impl;

import java.security.Principal;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.login.LoginConstants;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.PageProfilerValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.portalsite.PortalSite;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.ProfilerException;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityAttribute;
import org.apache.jetspeed.security.SecurityAttributes;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RefreshUserHomepageValveImpl
 * <P>
 * This valve copies all folders and files from the user template folder to a user's homepage folder after the user logs on
 * if a security attribute named 'org.apache.jetspeed.profiler.refreshUserHomepage' is set to 'true' for the user.
 * </P>
 * <P><EM>Note: this valve should be located after {@link PageProfilerValve} in the pipeline.</EM></P>
 * 
 * @version $Id$
 */
public class RefreshUserHomepageValveImpl extends ProfilerValveImpl
{
    private static final Logger log = LoggerFactory.getLogger(RefreshUserHomepageValveImpl.class);

    protected PageManager pageManager;
    protected UserManager userManager;
    protected String defaultTemplateFolder = "/_user/template/";
    protected boolean removeBeforeCopy;

    /**
     * CreateUserTemplatePagesValveImpl - constructor
     *
     * @param profiler profiler component reference
     * @param portalSite portal site component reference
     * @param requestFallback flag to enable root folder fallback
     * @param useHistory flag to enable selection of last visited folder page
     * @param pageManager pageManagerComponent reference
     */
    public RefreshUserHomepageValveImpl(Profiler profiler, PortalSite portalSite, boolean requestFallback, boolean useHistoryPageManager, PageManager pageManager, UserManager userManager)
    {
        super(profiler, portalSite, requestFallback, useHistoryPageManager);
        this.pageManager = pageManager;
        this.userManager = userManager;
    }
    
    public void setDefaultTemplateFolder(String defaultTemplateFolder)
    {
        this.defaultTemplateFolder = defaultTemplateFolder;
    }
    
    public void setRemoveBeforeCopy(boolean removeBeforeCopy)
    {
        this.removeBeforeCopy = removeBeforeCopy;
    }
    
    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        boolean userHomepagesUpdated = false;
        
        if ("true".equals(request.getRequest().getSession().getAttribute(LoginConstants.LOGIN_CHECK)))
        {
            try
            {
                userHomepagesUpdated = updateUserHomepagesByTemplate(request);
            }
            catch (Exception e)
            {
                log.error("Exception occurred during creating user folder.", e);
            }
        }

        if (userHomepagesUpdated)
        {
            super.invoke(request, context);
        }
        else
        {
            context.invokeNext(request);
        }
    }
    
    private boolean updateUserHomepagesByTemplate(RequestContext request) throws Exception
    {
        boolean userHomepagesUpdated = false;
        
        Principal userPrincipal = request.getUserPrincipal();
        
        if (userPrincipal == null)
        {
            throw new ProfilerException("Missing user principal for request: " + request.getPath());
        }
        
        User user = userManager.getUser(userPrincipal.getName());
        
        if (user == null)
        {
            throw new ProfilerException("Cannot retrieve user for " + userPrincipal.getName());
        }
        
        SecurityAttributes secAttrs = user.getSecurityAttributes();
        SecurityAttribute secAtttr = secAttrs.getAttribute(PortalReservedParameters.USER_HOMEPAGE_REFRESH_FLAG);
        
        if (secAtttr != null && Boolean.parseBoolean(secAtttr.getStringValue()))
        {
            try
            {
                String templateFolder = (String) request.getAttribute(PortalReservedParameters.USER_HOMEPAGE_TEMPLATE_PATH);
                
                if (templateFolder == null)
                {
                    SecurityAttribute secAttrTemplatePath = secAttrs.getAttribute(PortalReservedParameters.USER_HOMEPAGE_TEMPLATE_PATH);
                    
                    if (secAttrTemplatePath != null)
                    {
                        templateFolder = secAttrTemplatePath.getStringValue();
                        
                        if (templateFolder != null && "".equals(templateFolder.trim()))
                        {
                            templateFolder = null;
                        }
                    }
                }
                
                if (templateFolder == null)
                {
                    templateFolder = this.defaultTemplateFolder;
                }
                
                Folder source = this.pageManager.getFolder(templateFolder);
                
                // copy the entire dir tree from the template folder
                String userName = user.getName();
                String userFolder = Folder.USER_FOLDER + userName;
                
                boolean found = true;
                Folder destFolder = null;
                
                try
                {
                    destFolder = this.pageManager.getFolder(userFolder);
                    
                    if (removeBeforeCopy)
                    {
                        this.pageManager.removeFolder(destFolder);
                        found = false;
                    }
                }
                catch (FolderNotFoundException e)
                {
                    found = false;
                }

                if (!found)
                {
                    this.pageManager.deepCopyFolder(source, userFolder, userName);
                }
                else
                {
                    this.pageManager.deepMergeFolder(source, userFolder, userName);
                }
                
                // The user folder will have titles named after the user name.
                destFolder = this.pageManager.getFolder(userFolder);
                destFolder.setTitle(userName);
                destFolder.setShortTitle(userName);   
                this.pageManager.updateFolder(destFolder);
                
                userHomepagesUpdated = true;
            }
            catch (Exception e)
            {
                throw e;
            }
            finally
            {
                secAtttr.setStringValue("false");
                userManager.updateUser(user);
            }
        }
        
        return userHomepagesUpdated;
    }

}
