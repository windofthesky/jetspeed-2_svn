package org.apache.jetspeed.portlets.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.serializer.JetspeedSerializer;
import org.apache.jetspeed.serializer.JetspeedSerializerFactory;
import org.apache.portals.gems.dojo.AbstractDojoVelocityPortlet;

/**
 * Manage the Portal Site
 * 
 * @author <a href="mailto:firevelocity@gmail.com">Vivek Kumar</a>
 * 
 * @version $Id$
 */
public class JetspeedDataImporter extends AbstractDojoVelocityPortlet
{

    protected final Log log = LogFactory.getLog(this.getClass());

    // components
    protected UserManager userManager;

    protected GroupManager groupManager;

    protected RoleManager roleManager;

    private HashMap roleMap = new HashMap();

    private HashMap groupMap = new HashMap();

    private HashMap userMap = new HashMap();

    private HashMap mimeMap = new HashMap();

    private HashMap mimeMapInt = new HashMap();

    private HashMap mediaMap = new HashMap();

    private HashMap capabilityMap = new HashMap();

    private HashMap capabilityMapInt = new HashMap();

    private HashMap clientMap = new HashMap();

    private HashMap permissionMap = new HashMap();

    private HashMap rulesMap = new HashMap();

    int refCouter = 0;

    private static String ENCODING_STRING = "JETSPEED 2.1 - 2006";

    private static String JETSPEED = "JETSPEED";
    
    protected JetspeedSerializerFactory serializerFactory;    

    protected void includeHeaderContent(HeaderResource headerResource)
    {
        headerResource.dojoAddCoreLibraryRequire("dojo.lang.*");
        // headerResource.dojoAddCoreLibraryRequire("dojo.dnd.*");
        headerResource.dojoAddCoreLibraryRequire("dojo.dnd.HtmlDragManager");
        headerResource.dojoAddCoreLibraryRequire("dojo.dnd.DragAndDrop");
        headerResource.dojoAddCoreLibraryRequire("dojo.dnd.HtmlDragAndDrop");

        headerResource.dojoAddCoreLibraryRequire("dojo.event.*");
        headerResource.dojoAddCoreLibraryRequire("dojo.io");

        headerResource.dojoAddCoreLibraryRequire("dojo.widget.ContentPane");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.LayoutContainer");

        headerResource.dojoAddCoreLibraryRequire("dojo.widget.Tree");
        headerResource
                .dojoAddCoreLibraryRequire("dojo.widget.TreeRPCController");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.TreeSelector");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.TreeNode");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.TreeContextMenu");

        headerResource
                .dojoAddCoreLibraryRequire("dojo.widget.ValidationTextbox");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.ComboBox");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.Checkbox");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.Dialog");
        headerResource.dojoAddCoreLibraryRequire("dojo.widget.Button");

        headerResource.dojoAddModuleLibraryRequire("jetspeed.desktop.core");
        headerResource
                .dojoAddModuleLibraryRequire("jetspeed.widget.EditorTable");
    }

    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        userManager = (UserManager) getPortletContext().getAttribute(
                CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager)
        {
            PortletException pe = new PortletException(
                    "Failed to find the User Manager on SiteViewController initialization");
            throw new RuntimeException(pe);
        }
        groupManager = (GroupManager) getPortletContext().getAttribute(
                CommonPortletServices.CPS_GROUP_MANAGER_COMPONENT);
        if (null == groupManager)
        {
            PortletException pe = new PortletException(
                    "Failed to find the Group Manager on SiteViewController initialization");
            throw new RuntimeException(pe);
        }
        roleManager = (RoleManager) getPortletContext().getAttribute(
                CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleManager)
        {
            PortletException pe = new PortletException(
                    "Failed to find the Group Manager on SiteViewController initialization");
            throw new RuntimeException(pe);
        }
        serializerFactory = (JetspeedSerializerFactory) getPortletContext().getAttribute(
                CommonPortletServices.CPS_JETSPEED_SERIALIZER_FACTORY);
        if (null == serializerFactory)
        {
            PortletException pe = new PortletException(
                    "Failed to find the SerializerFactory on SiteViewController initialization");
            throw new RuntimeException(pe);
        }
        
    }

    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        super.doView(request, response);
        request.getPortletSession().removeAttribute("status");
    }

    public void processAction(ActionRequest request,
            ActionResponse actionResponse) throws PortletException,
            java.io.IOException
    {
        String export = request.getParameter("export");
        String fileName = "";
        String destPath = "";
        String fileType = "";
        String path = "";
        String usrFolder = "";
        boolean success = false;
        String filePath = "";
        cleanUserFolder(request.getUserPrincipal().toString());
        try
        {
            DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
            PortletFileUpload portletFileUpload = new PortletFileUpload(
                    diskFileItemFactory);
            if (PortletFileUpload.isMultipartContent(request))
            {
                Iterator fileIt = portletFileUpload.parseRequest(request)
                        .iterator();
                while (fileIt.hasNext())
                {
                    FileItem fileItem = (FileItem) fileIt.next();
                    if (fileItem.getFieldName().equals("importFile"))
                    {
                        synchronized (this)
                        {
                            fileName = fileItem.getName();
                            usrFolder = getTempFolder(request);
                            path = System.getProperty("file.separator");
                            filePath = usrFolder + path + fileItem.getName();
                            FileOutputStream out = new FileOutputStream(
                                    filePath);
                            out.write(fileItem.get());
                            out.close();
                        }

                    }
                }
                success = importJetspeedData(filePath);
            }
            if (success)
            {
                request.getPortletSession().setAttribute("status", fileName);
            } else
            {
                request.getPortletSession().setAttribute("status", "false");
            }
        } catch (Exception e)
        {
            request.getPortletSession().setAttribute("status", "false");
            // throw new PortletException("Error occured in file uplodad");
        }

        try
        {

        } catch (Exception e)
        {
            // TODO: handle exception
        }
        // serializer.exportData(name, exportFileName, settings)
    }

    private boolean importJetspeedData(String filePath)
    {
        try
        {
            Map settings = new HashMap();
            settings.put(JetspeedSerializer.KEY_PROCESS_USERS, Boolean.TRUE);
            settings.put(JetspeedSerializer.KEY_PROCESS_CAPABILITIES,
                    Boolean.TRUE);
            settings.put(JetspeedSerializer.KEY_PROCESS_PROFILER, Boolean.TRUE);
            settings.put(JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES,
                    Boolean.TRUE);
            settings.put(JetspeedSerializer.KEY_OVERWRITE_EXISTING,
                    Boolean.TRUE);
            settings.put(JetspeedSerializer.KEY_BACKUP_BEFORE_PROCESS,
                    Boolean.FALSE);
            JetspeedSerializer serializer = serializerFactory.create(JetspeedSerializerFactory.PRIMARY);
            serializer.importData(filePath, settings);
            // TODO: secondarySerializer            
            return true;
        } catch (Exception e)
        {
            return false;
        }

    }

    private boolean cleanUserFolder(String userName)
    {
        boolean success = false;
        synchronized (this)
        {
            String tmpdir = System.getProperty("java.io.tmpdir");
            String path = System.getProperty("file.separator");
            String folder = tmpdir + path + userName;
            File dir = new File(folder);
            if (dir.exists())
            {
                success = deleteDir(dir);
            }
            success = dir.mkdir();
        }
        return success;
    }

    private boolean deleteDir(File dir)
    {
        if (dir.exists())
        {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                if (files[i].isDirectory())
                {
                    deleteDir(files[i]);
                } else
                {
                    files[i].delete();
                }
            }
        }
        return (dir.delete());
    }

    private String getTempFolder(ActionRequest request)
    {
        String dir = System.getProperty("java.io.tmpdir");
        String path = System.getProperty("file.separator");
        File file = new File(dir + path + request.getUserPrincipal());
        file.mkdir();
        return dir + path + request.getUserPrincipal();
    }
}
