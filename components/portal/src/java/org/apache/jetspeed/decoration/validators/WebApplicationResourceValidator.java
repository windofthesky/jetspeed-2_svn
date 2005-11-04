package org.apache.jetspeed.decoration.validators;

import java.net.MalformedURLException;

import javax.servlet.ServletContext;

import org.apache.jetspeed.decoration.ResourceValidator;

public class WebApplicationResourceValidator implements ResourceValidator
{
    private final ServletContext servletContext;
    
    public WebApplicationResourceValidator(ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }

    public boolean resourceExists(String path)
    {
        try
        {
            return servletContext.getResource(path) != null;
        }
        catch (MalformedURLException e)
        {
            IllegalArgumentException iae = new IllegalArgumentException(path+" is not a valid path.");
            iae.initCause(e);
            throw iae;            
        }
    }

}
