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
package org.apache.jetspeed.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * ReloadablePropertyResourceBundle
 * 
 * @version $Id$
 */
public class ReloadablePropertyResourceBundle extends ResourceBundle
{
    protected PropertyResourceBundle internalBundle;
    protected String baseName;
    protected Properties overridingProps;
    
    public ReloadablePropertyResourceBundle(PropertyResourceBundle internalBundle, String baseName)
    {
        this.internalBundle = internalBundle;
        setParent(internalBundle);
        this.baseName = baseName;
    }
    
    /**
     * Reloads resource bundle from the properties file which can be decided by the baseName.
     * @param loader
     * @throws IOException
     */
    public void reload(ClassLoader loader) throws IOException
    {
        String bundleName = baseName;
        Locale locale = getLocale();
        
        if (locale != null)
        {
            String localeSuffix = locale.toString();
            
            if (localeSuffix.length() > 0) 
            {
                bundleName += "_" + localeSuffix;
            }
            else if (locale.getVariant().length() > 0) 
            {
                bundleName += "___" + locale.getVariant();
            }
        }
        
        String resPath = bundleName.replace('.', '/') + ".properties";
        
        Properties props = new Properties();
        InputStream is = null;
        BufferedInputStream bis = null;
        
        try
        {
            URL url = loader.getResource(resPath);
            
            if ("file".equals(url.getProtocol()))
            {
                is = new FileInputStream(new File(url.toURI()));
            }
            else
            {
                is = url.openStream();
            }
            
            bis = new BufferedInputStream(is);
            props.load(bis);
            
            if (!props.isEmpty())
            {
                overridingProps = props;
            }
        }
        catch (URISyntaxException e)
        {
            throw new IOException(e.toString());
        }
        finally
        {
            if (bis != null)
            {
                try
                {
                    bis.close();
                }
                catch (Exception ignore)
                {
                }
            }
            
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (Exception ignore)
                {
                }
            }
        }
    }
    
    /**
     * Resets overriding properties.
     */
    public void reset()
    {
        overridingProps = null;
    }
    
    @Override
    public Locale getLocale()
    {
        return internalBundle.getLocale();
    }
    
    @Override
    public Enumeration<String> getKeys()
    {
        if (overridingProps != null)
        {
            return new TypedEnumeration<String>(overridingProps.keys());
        }
        
        return internalBundle.getKeys();
    }
    
    @Override
    protected Object handleGetObject(String key)
    {
        if (overridingProps != null)
        {
            return overridingProps.getProperty(key);
        }
        
        return null;
    }
    
    private class TypedEnumeration<T> implements Enumeration<T>
    {
        private Enumeration<Object> internalEnum;
        
        private TypedEnumeration(Enumeration<Object> internalEnum)
        {
            this.internalEnum = internalEnum;
        }
        
        public boolean hasMoreElements()
        {
            return internalEnum.hasMoreElements();
        }

        public T nextElement()
        {
            return (T) internalEnum.nextElement();
        }
    }
}
