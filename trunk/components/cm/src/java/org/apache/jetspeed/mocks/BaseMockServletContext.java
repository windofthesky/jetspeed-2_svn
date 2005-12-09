package org.apache.jetspeed.mocks;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public abstract class BaseMockServletContext implements ServletContext
{
    private final Map attributes = new HashMap();

    public Object getAttribute(String arg0)
    {
        return attributes.get(arg0);
    }

    public Enumeration getAttributeNames()
    {
        unsupported();
        return null;
    }

    public ServletContext getContext(String arg0)
    {
        unsupported();
        return null;
    }

    public String getInitParameter(String arg0)
    {
        unsupported();
        return null;
    }

    public Enumeration getInitParameterNames()
    {
        unsupported();
        return null;
    }

    public int getMajorVersion()
    {
        return 2;
    }

    public String getMimeType(String arg0)
    {
        unsupported();
        return null;
    }

    public int getMinorVersion()
    {
        return 3;
    }

    public RequestDispatcher getNamedDispatcher(String arg0)
    {
        unsupported();
        return null;
    }

    public String getRealPath(String arg0)
    {
        unsupported();
        return null;
    }

    public RequestDispatcher getRequestDispatcher(String arg0)
    {
        unsupported();
        return null;
    }

    public URL getResource(String arg0) throws MalformedURLException
    {
        unsupported();
        return null;
    }

    public InputStream getResourceAsStream(String arg0)
    {
        unsupported();
        return null;
    }

    public Set getResourcePaths(String arg0)
    {
        unsupported();
        return null;
    }

    public String getServerInfo()
    {
        unsupported();
        return null;
    }

    public Servlet getServlet(String arg0) throws ServletException
    {
        unsupported();
        return null;
    }

    public String getServletContextName()
    {
        unsupported();
        return null;
    }

    public Enumeration getServletNames()
    {
        unsupported();
        return null;
    }

    public Enumeration getServlets()
    {
        unsupported();
        return null;
    }

    public void log(Exception arg0, String arg1)
    {
        unsupported();
        
    }

    public void log(String arg0, Throwable arg1)
    {
        unsupported();
        
    }

    public void log(String arg0)
    {
        unsupported();
        
    }

    public void removeAttribute(String arg0)
    {
        attributes.remove(arg0);
        
    }

    public void setAttribute(String arg0, Object arg1)
    {
        attributes.put(arg0, arg1);
        
    }
    
    protected final void unsupported() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("The method called has not been implemented.");
    }

}
