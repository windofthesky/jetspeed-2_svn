/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.mocks;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;

import com.mockrunner.mock.web.MockServletConfig;

@SuppressWarnings("deprecation")
public class ResourceLocatingServletContext extends BaseMockServletContext
{
    protected final static Logger log = LoggerFactory.getLogger(ResourceLocatingServletContext.class);
    
    private final File rootPath;
    private final Map pathOverrides = new HashMap();
    private final List servletInfoList = new ArrayList();
    private final List servletMappingInfoList = new ArrayList();
    private final Map servletInstanceMap = new HashMap();
    
    public ResourceLocatingServletContext(File rootPath)
    {
        super();        
        this.rootPath = rootPath;
    }
    
    public ResourceLocatingServletContext(File rootPath, boolean loadServlet)
    {
        super();
        this.rootPath = rootPath;

        if (loadServlet)
            loadServlets();
    }
    
    public final void addPathOverride(String path, File file)
    {
        pathOverrides.put(path, file);
    }

    public URL getResource(String path) throws MalformedURLException
    {
       if(pathOverrides.containsKey(path))
       {
           return ((File)pathOverrides.get(path)).toURL();
       }
       else
       {
           return new File(rootPath, path).toURL();
       }
    }

    public String getRealPath(String path)
    {
        if(pathOverrides.containsKey(path))
        {
            return ((File)pathOverrides.get(path)).getAbsolutePath();
        }
        else
        {
            return new File(rootPath, path).getAbsolutePath();
        }
    }

    public InputStream getResourceAsStream(String path)
    {
        try
        {
            return getResource(path).openStream();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public Set getResourcePaths(String path)
    {
        File start = new File(rootPath, path);        
        File[] children = start.listFiles();
        HashSet pathes = new HashSet();
        for(int i=0; i < children.length; i++)
        {
            File child = children[i];
            String relativePath = child.getPath().substring(rootPath.getPath().length()).replace('\\','/');
            
            if(child.isDirectory())
            {                
                pathes.add(relativePath+"/");
            }
            else
            {
                pathes.add(relativePath);
            }
        }
        
        Iterator itr = pathOverrides.keySet().iterator();
        while(itr.hasNext())
        {
            pathes.add(itr.next());
        }
        
        return pathes;
    }

    public RequestDispatcher getRequestDispatcher(String arg0)
    {
        Servlet servlet = findServletByPath(arg0);

        if (servlet == null)
        {
            throw new IllegalArgumentException("Failed to find servlet for the path: " + arg0);
        }

        return new ResourceLocatingRequestDispatcher(servlet, arg0, null);
    }

    protected Servlet findServletByPath(String path)
    {
        Servlet servlet = null;

        for (Iterator it = this.servletMappingInfoList.iterator(); it.hasNext(); )
        {
            ServletMappingInfo servletMappingInfo = (ServletMappingInfo) it.next();
            Pattern pattern = servletMappingInfo.getPattern();

            if (pattern != null)
            {
                PatternMatcher matcher = new Perl5Matcher();
                
                if ((matcher.matches(path, pattern)) || (matcher.matches(path + "/", pattern)))
                {
                    servlet = (Servlet) this.servletInstanceMap.get(servletMappingInfo.getServletName());
                    break;
                }
            }
        }
        
        return servlet;
    }

    protected void loadServlets() 
    {
        this.servletInfoList.clear();
        this.servletMappingInfoList.clear();

        Digester digester = new Digester();
        
        digester.addObjectCreate("web-app/servlet", ServletInfo.class);
        digester.addBeanPropertySetter("web-app/servlet/servlet-name", "servletName");
        digester.addBeanPropertySetter("web-app/servlet/servlet-class", "servletClass");
        digester.addCallMethod("web-app/servlet/init-param", "addInitParam", 2);
        digester.addCallParam("web-app/servlet/init-param/param-name", 0);
        digester.addCallParam("web-app/servlet/init-param/param-value", 1);
        digester.addRule("web-app/servlet", new ServletRule(this.servletInfoList));
        
        digester.addObjectCreate("web-app/servlet-mapping", ServletMappingInfo.class);
        digester.addBeanPropertySetter("web-app/servlet-mapping/servlet-name", "servletName");
        digester.addBeanPropertySetter("web-app/servlet-mapping/url-pattern", "urlPattern");
        digester.addRule("web-app/servlet-mapping", new ServletMappingRule(this.servletMappingInfoList));

        File webInfPath = new File(this.rootPath, "WEB-INF");        
        File webDescriptorFile = new File(webInfPath, "web.xml");
        log.debug("parsing webDescriptorFile: " + webDescriptorFile);

        try
        {
            digester.parse(webDescriptorFile);
        }
        catch (Exception e)
        {
            log.error("Failed to parse webDescriptorFile: " + webDescriptorFile, e);
        }
        
        for (Iterator it = this.servletInfoList.iterator(); it.hasNext(); )
        {
            ServletInfo servletInfo = (ServletInfo) it.next();
            
            try
            {
                Servlet servlet = (Servlet) Class.forName(servletInfo.getServletClass()).newInstance();
                MockServletConfig servletConfig = new MockServletConfig();
                servletConfig.setServletContext(this);
                
                Map initParamMap = servletInfo.getInitParamMap();
                
                for (Iterator itParam = initParamMap.keySet().iterator(); itParam.hasNext(); )
                {
                    String paramName = (String) itParam.next();
                    String paramValue = (String) initParamMap.get(paramName);
                    servletConfig.setInitParameter(paramName, paramValue);
                }
                
                servlet.init(servletConfig);
                
                this.servletInstanceMap.put(servletInfo.getServletName(), servlet);
            }
            catch (Exception e)
            {
                log.error("Failed to load and initialize servlet: " + servletInfo);
            }
        }
    }

    public static class ServletInfo 
    {
        protected String servletName;
        protected String servletClass;
        protected Map initParamMap = new HashMap();

        public void setServletName(String servletName) 
        {
            this.servletName = servletName;
        }
        
        public String getServletName() 
        {
            return this.servletName;
        }

        public void setServletClass(String servletClass) 
        {
            this.servletClass = servletClass;
        }

        public String getServletClass() 
        {
            return this.servletClass;
        }

        public void addInitParam(String paramName, String paramValue) 
        {
            this.initParamMap.put(paramName, paramValue);
        }

        public Map getInitParamMap() {
            return this.initParamMap;
        }

        public String toString() {
            return "ServletInfo [" + this.servletName + ", " + this.servletClass + ", " + this.initParamMap + "]";
        }
    }

    public static class ServletMappingInfo 
    {
        protected String servletName;
        protected String urlPattern;
        protected Pattern pattern;

        public void setServletName(String servletName) 
        {
            this.servletName = servletName;
        }

        public String getServletName() 
        {
            return this.servletName;
        }

        public void setUrlPattern(String urlPattern) 
        {
            this.urlPattern = urlPattern;
            this.pattern = null;

            try
            {
                PatternCompiler compiler = new GlobCompiler();
                this.pattern = compiler.compile(this.urlPattern);
            }
            catch (Exception e)
            {
                log.error("Invalid url pattern: " + this.urlPattern);
            }
        }

        public String getUrlPattern() 
        {
            return this.urlPattern;
        }

        public Pattern getPattern()
        {
            return this.pattern;
        }

        public String toString() {
            return "ServletMappingInfo [" + this.urlPattern + ", " + this.servletName + "]";
        }
    }
    
    public static class ServletRule extends Rule 
    {
        private List servletInfoList;

        public ServletRule(List servletInfoList)
        {
            this.servletInfoList = servletInfoList;
        }

        public void end(String namespace, String name) 
        {
            try
            {
                ServletInfo servletInfo = (ServletInfo) digester.peek(0);
                this.servletInfoList.add(servletInfo);
            }
            catch (Exception e)
            {
                log.error("Exception occurred in ServletRule", e);
            }
        }
    }

    public static class ServletMappingRule extends Rule
    {
        private List servletMappingInfoList;

        public ServletMappingRule(List servletMappingInfoList)
        {
            this.servletMappingInfoList = servletMappingInfoList;
        }

        public void end(String namespace, String name) 
        {
            try 
            {
                ServletMappingInfo servletMappingInfo = (ServletMappingInfo) digester.peek(0);
                this.servletMappingInfoList.add(servletMappingInfo);
            }
            catch (Exception e)
            {
                log.error("Exception occurred in ServletMappingRule", e);
            }
        }
    }

    
}
