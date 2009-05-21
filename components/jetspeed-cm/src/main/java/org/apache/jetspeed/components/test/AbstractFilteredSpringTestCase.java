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
package org.apache.jetspeed.components.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.jetspeed.components.JetspeedBeanDefinitionFilter;

/**
 * Base class for tests that want to use spring with the Jetspeed filtering feature enabled. The added value here is that you can simply point your
 * tests at a assembly directory, and have the filters decide which spring beans to load based on filtering categories.
 * 
 * @author taylor
 */
public abstract class AbstractFilteredSpringTestCase extends AbstractSpringTestCase
{
    /**
     * Override the location of the test properties by using the jetspeed properties found in the default package.
     * Make sure to have your unit test copy in jetspeed properties into the class path root like:
     <blockquote><pre>
        &lt;resource&gt;
            &lt;path&gt;conf/jetspeed&lt;/path&gt;
            &lt;include&gt;*.properties&lt;/include&gt;                                        
        &lt;/resource&gt;                                         
     </pre></blockquote>
     */
    @Override    
    protected Properties getInitProperties()
    {
        Properties props = new Properties();
        try 
        {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("jetspeed.properties");
            if (is != null)
                props.load(is);
        } catch (FileNotFoundException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return props;
    }
           
    /**
     * Override the location of the Jetspeed Spring assemblies files by pulling in all xml files found in the assembly package.
     * Make sure to have your unit test copy assemblies into the class path assembly directory like:
     <blockquote><pre>
        &lt;resource&gt;
            &lt;path&gt;assembly&lt;/path&gt;
            &lt;include&gt;*.xml,boot/*.xml&lt;/include&gt;
            &lt;destination&gt;assembly&lt;/destination&gt;
         &lt;/resource&gt;
     </pre></blockquote>
     */
    @Override
    protected String[] getConfigurations() 
    {
        return getResourcesFromClasspath("assembly");
    }   
    
    /**
     * Override the location of the Jetspeed Boot Spring assemblies files by pulling in all xml files found in the assembly package.
     * Make sure to have your unit test copy assemblies into the class path assembly directory like:
     <blockquote><pre>
        &lt;resource&gt;
            &lt;path&gt;assembly&lt;/path&gt;
            &lt;include&gt;*.xml,boot/*.xml&lt;/include&gt;
            &lt;destination&gt;assembly&lt;/destination&gt;
         &lt;/resource&gt;
     </pre></blockquote>
     */
    protected String[] getBootConfigurations()
    {
        return new String[]
        { "assembly/boot/datasource.xml"};
    }
    
    /**
     * Support for loading all Spring assembly resources from the class path and then letting the bean filtering mechanism sort out what beans to load.
     * You can call it like above: 
     * <blockquote><pre>
     *   protected String[] getConfigurations() 
     *   {
     *       return getResourcesFromClasspath("assembly");
     *   }
     * </pre></blockquote>
     * @param classPathDirectory
     * @return an array of all possible assembly files on the classpath (non-filtered). 
     */
    protected String[] getResourcesFromClasspath(String classPathDirectory)
    {
        List<String> result = new ArrayList<String>();
        FilenameFilter filter = new AssemblyFilter();
        try 
        {
            Enumeration<URL> locations = this.getClass().getClassLoader().getResources(classPathDirectory);
            while (locations.hasMoreElements())
            {
                URL url = locations.nextElement();
                File dir = new File(url.getFile());
                String assemblies[] = dir.list(filter);
                if (assemblies != null)
                {
                    for (int ix=0; ix < assemblies.length; ix++)
                    {
                        assemblies[ix] = classPathDirectory + "/" + assemblies[ix];
                    }
                    result.addAll(Arrays.asList(assemblies));
                }
            }           
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return (String[])result.toArray(new String[0]);
    }
    
    protected class AssemblyFilter implements FilenameFilter
    {
        public boolean accept(File dir, String name)
        {
            if (name.equals("jetspeed-properties.xml"))
                return false;
            return name.toLowerCase().endsWith(".xml");             
        }
    }

    @Override
    protected String getBeanDefinitionFilterCategories()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    protected String getBeanDefinitionFilterCategoryKey()
    {
        return "default";
    }

    protected String getFilterFile()
    {
        return "spring-filter.properties";        
    }
    
    /**
     * Helper to either use a list of categories, comma separated, with <code>getBeanDefinitionFilterCategories</code> or
     * use a single category key with <code>getBeanDefinitionFilterCategoryKey</code> combined with <code>getFilterFile</code> 
     */
    @Override
    protected JetspeedBeanDefinitionFilter getBeanDefinitionFilter() throws IOException
    {
        String categories = getBeanDefinitionFilterCategories();
        if (categories == null)
            return new JetspeedBeanDefinitionFilter(getFilterFile(), getBeanDefinitionFilterCategoryKey());
        else
            return new JetspeedBeanDefinitionFilter(categories);
    }

}
