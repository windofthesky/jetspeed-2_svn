package org.apache.jetspeed.contentserver;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.StreamUtils;

/**
 * Created on Dec 30, 2003
 *
 * 
 * @author
 */

/**
 * <p>
 * ContentFilter
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class ContentFilter implements Filter
{
    public static final String SESSION_THEME_ATTR = "org.apache.jetspeed.theme";
    private FilterConfig config;
    private String defaultTheme;
    private String contentDir;
    private String themesDir;
    private File contentDirFile;
    private Map fileCache;

    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException
    {
        this.config = config;
        this.defaultTheme = config.getInitParameter("default.theme");
        String dir = config.getInitParameter("content.directory");
        this.contentDir = config.getServletContext().getRealPath(dir);
        this.themesDir = this.contentDir + "/themes";
        this.contentDirFile = new File(this.contentDir);
        this.fileCache = new HashMap();
        if (!contentDirFile.exists())
        {
            throw new ServletException("The specified content directory " + contentDirFile.getAbsolutePath() + " does not exist!");
        }

    }

    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        try
        {
            if (request instanceof HttpServletRequest)
            {

                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                String requestURI = httpRequest.getRequestURI();
                String mimeType = config.getServletContext().getMimeType(requestURI);

                if (mimeType == null)
                {
                    throw new NullPointerException(
                        "MIME-TYPE for "
                            + requestURI
                            + " could not be located.  Make sure your container is properly configured to detect MIME types.");
                }

                if (mimeType.equals("image/gif"))
                {
                    System.out.println("GIF detected: " + requestURI);

                    boolean found = setThemeContent(requestURI, httpRequest, httpResponse, mimeType);

                    if (found)
                    {
                        System.out.println("Setting status to OK");
                        httpResponse.setStatus(HttpServletResponse.SC_OK);
                    }
                    else
                    {
                        chain.doFilter(request, response);
                    }

                    return;
                }
            }
        }
        catch (Exception e)
        {

            System.out.println("Error filtering image, " + e.toString());
            e.printStackTrace();
        }

        chain.doFilter(request, response);
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy()
    {

    }

    protected boolean setThemeContent(String URI, HttpServletRequest request, HttpServletResponse response, String mimeType)
    {
        int rootLen = 13;
        int rootStart = URI.indexOf("content/theme");
        if (rootStart != -1)
        {
            String dir = URI.substring(rootStart + rootLen);
            String themeName = getCurrentTheme(request);
            File fqFile = null;
            if (fileCache.containsKey(themeName + ":" + URI))
            {
                fqFile = (File) fileCache.get(themeName + ":" + URI);
				System.out.println("Found cached theme file for URI: " + URI);
            }
            else
            {
                String fqPath = themesDir + "/" + themeName + "/html" + dir;
                fqFile = new File(fqPath);
                System.out.println("Actual theme content located at: " + fqPath);
                System.out.println("Theme content exists? " + fqFile.exists());
				fileCache.put(themeName + ":" + URI, fqFile);
            }

            BufferedInputStream bis = null;
            try
            {

                bis = new BufferedInputStream(new FileInputStream(fqFile));
                response.setContentType(mimeType);
                response.setContentLength((int) fqFile.length());
                ServletOutputStream sos = response.getOutputStream();
                for (int i = bis.read(); i != -1; i = bis.read())
                {
                    sos.write((byte) i);
                }
                System.out.println("Wrote " + fqFile.length() + " to the response output stream.");

                return true;

            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if (bis != null)
                    {
                        bis.close();
                    }
                }
                catch (IOException e1)
                {
                    // ignore

                }
            }
        }
        return false;

    }

    protected String getCurrentTheme(HttpServletRequest request)
    {
        String themeName = (String) request.getSession().getAttribute(SESSION_THEME_ATTR);
        if (themeName == null)
        {
            themeName = defaultTheme;
        }

        return themeName;
    }

}
