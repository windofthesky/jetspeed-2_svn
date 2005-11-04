package org.apache.jetspeed.decoration.validators;

import org.apache.jetspeed.decoration.ResourceValidator;

public class ClasspathResourceValidator implements ResourceValidator
{
    private ClassLoader classLoader;
    
    public ClasspathResourceValidator(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }
    
    public ClasspathResourceValidator()
    {
        this(ClasspathResourceValidator.class.getClassLoader());
    }

    public boolean resourceExists(String path)
    {
        return classLoader.getResource(path) != null;
    }

}
