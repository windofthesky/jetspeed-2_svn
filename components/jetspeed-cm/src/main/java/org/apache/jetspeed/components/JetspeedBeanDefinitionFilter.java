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
package org.apache.jetspeed.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderSupport;

/**
 * The JetspeedBeanDefinitionFilter makes it possible to dynamically filter out a BeanDefinition
 * before it is registered in a Spring BeanFactory based on category meta data in the Spring configuration.
 * <p>
 * Categories definitions are loaded from one or more properties files and are simply sets of names to
 * evaluate bean definitions against. A categories set is a comma separated list of names.
 * Multiple sets of categories can be defined in the provided properties files, and also reference (include)
 * each other using standard ${property} syntax.
 * </p>
 * <p>
 * A specific property containing the categories to be used for filtering is defined through
 * method {@link #setCategoriesKey}, or as <code>JetspeedBeanDefinitionFilter.categoriesKey</code>
 * property in one of the loaded property files, or as System (-D) parameter.
 * A System parameter is checked first, then the loaded properties and only as last resort the bean property itself.
 * </p>
 * <p>
 * The categories set to be used can also be set directly through the {@link #categories} method.
 * </p>
 * <p>
 * Beans which needs to be matched against active categories must define a:<br/>
 * <pre>&lt;meta key="j2:cat" value="&lt;categories&gt;"/&gt;</pre> meta value within the bean definition.
 * The bean categories value is parsed as a standard logical expression that can utilize the unary
 * NOT operator, the binary AND and OR operators, and parentheses. Categories within the expression are
 * substituted with TRUE and FALSE values depending on whether the category is defined for filtering
 * or not, respectively. The bean matches if the whole expression evaluates to TRUE.
 * </p>
 * <p>
 * Unmatched beans (having a "j2:cat" meta value expression that evaluates to FALSE) will not be registered
 * in the Spring beanFactory (see: {@link FilteringListableBeanFactory}. By not defining an id or name attribute,
 * or a unique one not referenced by other beans with different (or no) set of categories, different versions
 * of a similar bean can be defined within a single Spring configuration file.
 * </p>
 * <p>
 * Additionally an alias can be defined as bean meta value, allowing multiple beans to
 * be defined using the same alias:<br/>
 * <pre>&lt;meta key="j2:alias" value="&lt;alias&gt;"/&gt;</pre>
 * Only a matched bean its alias will be registered and thereby able to be referenced by other beans
 * (on this dynamically created alias).
 * For this type of "enabling" only one of many possible beans definitions, these beans should
 * not define an id or name attribute directly, or only an unique one which is not referenced or
 * only referenced by other beans which have a "matching" j2:cat meta value.
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @since 2.2
 * @version $Id$
 */
public class JetspeedBeanDefinitionFilter extends PropertiesLoaderSupport
{
    private static Logger log = LoggerFactory.getLogger(JetspeedBeanDefinitionFilter.class);
    
    public static final String SYSTEM_PROPERTY_CATEGORIES_KEY = "JetspeedBeanDefinitionFilter.categoriesKey";
    
    public static final String CATEGORY_META_KEY = "j2:cat";
    public static final String ALIAS_META_KEY = "j2:alias";

    public static final String DEFAULT_CATEGORIES = "default";

    private String categoriesKey;
    private Properties props;
    private Set<String> categories;
    private JetspeedBeanDefinitionFilterMatcher matcher;
    private boolean initialized;
    
    public JetspeedBeanDefinitionFilter()
    {
        setCategories(DEFAULT_CATEGORIES);
    }
    
    public JetspeedBeanDefinitionFilter(String categories)
    {
        setCategories(categories);
    }
    
    public JetspeedBeanDefinitionFilter(Set<String> categories)
    {
        setCategories(categories);
    }
    
    public JetspeedBeanDefinitionFilter(String propertiesLocation, String categoriesKey) throws IOException
    {
        loadProperties(new String[]{propertiesLocation});
        setCategoriesKey(categoriesKey);
    }
    
    public JetspeedBeanDefinitionFilter(String[] propertiesLocations, String categoriesKey) throws IOException
    {
        loadProperties(propertiesLocations);
        setCategoriesKey(categoriesKey);
    }
    
    protected void loadProperties(String[] propertiesLocations) throws IOException
    {
        if (propertiesLocations != null)
        {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource[] resources = new Resource[propertiesLocations.length];
            for (int i = 0; i < propertiesLocations.length; i++)
            {
                resources[i] = resourceLoader.getResource(propertiesLocations[i]);
            }
            setLocations(resources);
            props = new Properties();
            loadProperties(props);
            
            // interpolation of property references within properties delimited by '${' and '}'
            // based on org.apache.maven.plugin.resources.PropertiesUtils
            HashSet<String> circularRefs = new HashSet<String>();
            ArrayList<String> visitedProps = new ArrayList<String>();
            for ( Enumeration n = props.propertyNames(); n.hasMoreElements(); )
            {
                String k = (String) n.nextElement();
                String v = props.getProperty( k );
                String result = "";
                String nk, nv;
                int idx, idx2;
                visitedProps.clear();
                visitedProps.add(k);

                while ( ( idx = v.indexOf( "${" ) ) >= 0 )
                {
                    // append prefix to result
                    result += v.substring( 0, idx );

                    // strip prefix from original
                    v = v.substring( idx + 2 );

                    // if no matching } then bail
                    if ( ( idx2 = v.indexOf( '}' ) ) < 0 )
                    {
                        break;
                    }

                    // strip out the key and resolve it
                    // resolve the key/value for the ${statement}
                    nk = v.substring( 0, idx2 );
                    v = v.substring( idx2 + 1 );
                    
                    nv = null;
                    
                    if (circularRefs.contains(nk) || visitedProps.contains(nk))
                    {
                        // prevent looping
                        log.error("Circular property reference: "+nk+" encountered for: "+k+". Property value not fully resolved");
                        circularRefs.add(k);
                    }
                    else
                    {                    
                        nv = props.getProperty( nk );

                        // try global environment..
                        if ( nv == null )
                        {
                            nv = System.getProperty( nk );
                        }
                        else
                        {
                            visitedProps.add(nk);
                        }
                    }

                    // if the key cannot be resolved,
                    // leave it alone ( and don't parse again )
                    // else prefix the original string with the
                    // resolved property ( so it can be parsed further )
                    // taking recursion into account.
                    if ( nv == null || nv.equals( k ) )
                    {
                        result += "${" + nk + "}";
                    }
                    else
                    {
                        v = nv + v;
                    }
                }
                props.setProperty( k, result + v );
            }
        }
    }
    
    public void setCategoriesKey(String categoriesKey)
    {
        this.categoriesKey = categoriesKey;
    }

    public void setCategories(String categories)
    {
        if (categories != null && categories.length()>0)
        {            
            this.categories = new HashSet<String>();
            StringTokenizer st = new StringTokenizer(categories, " ,;\t");
            while (st.hasMoreTokens())
            {
                this.categories.add(st.nextToken());
            }
            this.matcher = new JetspeedBeanDefinitionFilterMatcher(this.categories);
        }
        else
        {
            this.categories = null;
            this.matcher = null;
        }
    }

    public void setCategories(Set<String> categories)
    {
        this.categories = categories;
        this.matcher = ((this.categories != null) ? new JetspeedBeanDefinitionFilterMatcher(this.categories) : null);
    }
    
    public Properties getProperties()
    {
        props = new Properties();
        if (this.props != null)
        {
            props.putAll(this.props);
        }
        return props;
    }
    
    public void init() throws IllegalStateException
    {
        if (!initialized)
        {
            initialized = true;
            
            if (props != null && this.categories == null)
            {
                this.categories = new HashSet<String>();
                String value = System.getProperty(SYSTEM_PROPERTY_CATEGORIES_KEY);
                if (value == null && props != null)
                {
                    value = props.getProperty(SYSTEM_PROPERTY_CATEGORIES_KEY);
                }
                if (value != null)
                {
                    categoriesKey = value;
                }
                if (categoriesKey == null || categoriesKey.length() == 0)
                {            
                    throw new IllegalStateException("Required property categoriesKey undefined");
                }
                else
                {
                    String categories = (String)props.get(categoriesKey);
                    if (categories != null && categories.length()>0)
                    {
                        StringTokenizer st = new StringTokenizer(categories, " ,;\t");
                        while (st.hasMoreTokens())
                        {
                            this.categories.add(st.nextToken());
                        }
                    }
                    props = null;
                }               
            }
            if (this.categories == null)
            {
                this.categories = new HashSet<String>();
            }
            this.matcher = new JetspeedBeanDefinitionFilterMatcher(this.categories);            
        }
    }
    
    public boolean match(BeanDefinition bd)
    {
        String beanCategoriesExpression = (String)bd.getAttribute(CATEGORY_META_KEY);
        boolean matched = true;
        if (beanCategoriesExpression != null)
        {
            matched = ((matcher != null) && matcher.match(beanCategoriesExpression));
        }
        return matched;
    }
    
    public void registerDynamicAlias(BeanDefinitionRegistry registry, String beanName, BeanDefinition bd)
    {
        String aliases = (String)bd.getAttribute(ALIAS_META_KEY);
        if (aliases != null)
        {
            StringTokenizer st = new StringTokenizer(aliases, " ,");
            while (st.hasMoreTokens())
            {
                String alias = st.nextToken();
                if (!alias.equals(beanName))
                {
                    registry.registerAlias(beanName, alias);
                }
            }
        }
    }
}
