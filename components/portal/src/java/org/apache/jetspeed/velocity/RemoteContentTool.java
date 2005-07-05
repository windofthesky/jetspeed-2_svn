/*
 * Created on Jul 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.velocity;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.velocity.tools.view.tools.ViewTool;

/**
 * <p>
 * ContentTool
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: RemoteContentTool.java 187163 2004-08-01 15:51:27Z weaver $
 *
 */
public class RemoteContentTool implements ViewTool
{

    /**
     * <p>
     * init
     * </p>
     *
     * @see org.apache.velocity.tools.view.tools.ViewTool#init(java.lang.Object)
     * @param arg0
     */
    public void init( Object arg0 )
    {
        
        
    }
    
    public String include(String remoteContentUrl)
    {
        GetMethod remoteContentGet = null;
        
        try
        {
            HttpClient client = new HttpClient();
            remoteContentGet = new GetMethod(remoteContentUrl);
            client.executeMethod(remoteContentGet);
            return remoteContentGet.getResponseBodyAsString();
        }
        catch (Exception e)
        {
            return e.toString()+" message:"+ e.getMessage();
        }
        finally
        {
            if(remoteContentGet != null)
            {
                remoteContentGet.releaseConnection();
            }
        }
    }

 

}
