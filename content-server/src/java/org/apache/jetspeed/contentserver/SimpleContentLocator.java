/*
 * Created on Mar 12, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.contentserver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * SimpleContentLocator
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $ $
 *  
 */
public class SimpleContentLocator implements ContentLocator
{

    private List lookupPathes;

    private String rootPath;

    private boolean useCachedLookup;

    private Map fileCache;

    private String URLHint;

    public SimpleContentLocator(String rootPath, List lookupPathes,
            String URLHint, boolean useCachedLookup)
    {
        this.lookupPathes = lookupPathes;
        this.rootPath = rootPath;
        this.useCachedLookup = useCachedLookup;
        fileCache = new HashMap();
        this.URLHint = URLHint;
    }

    public long mergeContent(String URI, OutputStream os)
    {
        File content = locateContent(URI);
        if(content != null)
        {
            return setContent(content, os);
        }
        else
        {
            return -1;
        }
    }

    protected File locateContent(String URI)
    {
        int rootLen = URLHint.length();
        int rootStart = URI.indexOf(URLHint);
        File fqFile = null;
        if (rootStart != -1)
        {
            String dir = URI.substring(rootStart + rootLen);
            
            for (int i = 0; i < lookupPathes.size(); i++)
            {
                
                if (useCachedLookup && fileCache.containsKey(lookupPathes.get(i) + ":" + URI))
                {
                    fqFile = (File) fileCache.get(lookupPathes.get(i) + ":"
                            + URI);
                    System.out.println("Found cached file for URI: " + URI);
                }
                else
                {
                    // String fqPath = pathes.get(i) + "/html" + dir;
                    String[] sep = new String[]{"", ""} ;
                    
                    if (lookupPathes.get(i).toString().trim().length() > 1)
                    {
                        sep[0] = "/";
                    }
                    
                    if (!dir.startsWith("/"))
                    {
                        sep[1] = "/";
                    }
                    
                    String fqPath = this.rootPath + sep[0] + lookupPathes.get(i)
                                    + sep[1] + dir;

                    fqFile = new File(fqPath);
                    System.out.println("Actual content located at: " + fqPath);
                    System.out.println("Content exists? " + fqFile.exists());
                    if (!fqFile.exists())
                    {
                        fqFile = null;
                        continue;
                    }
                    
                    if(useCachedLookup)
                    {
                        fileCache.put(lookupPathes.get(i) + ":" + URI, fqFile);
                    }
                    return fqFile;
                }
            }
        }
        
        return null;
        
    }
    
    protected long setContent(File fqFile, OutputStream os)
    {
        BufferedInputStream bis = null;
        try
        {

            bis = new BufferedInputStream(new FileInputStream(fqFile));
            for (int j = bis.read(); j != -1; j = bis.read())
            {
                os.write((byte) j);
            }
            System.out.println("Wrote " + fqFile.length()
                    + " to the output stream.");

            return fqFile.length();

        } catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        } 
        finally
        {
            try
            {
                if (bis != null)
                {
                    bis.close();
                }
            } catch (IOException e1)
            {
                // ignore

            }
        }
        
    }

}
