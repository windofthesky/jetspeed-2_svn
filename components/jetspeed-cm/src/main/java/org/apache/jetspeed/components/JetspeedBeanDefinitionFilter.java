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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
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
 * The bean categories value may contain multiple comma separated values.
 * </p>
 * <p>
 * Unmatched beans (having a "j2:cat" meta value for which none were active) will not be registered in the
 * Spring beanFactory (see: {@link FilteringListableBeanFactory}. By not defining an id or name attribute,
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
 * <p>
 * Optionally, all beans without a "j2:cat" meta value can be set to lazy initialization
 * too, through method {@link #setDefaultLazyInit} (default value: null). This property can also
 * be defined directly as <code>CategoryFilteringPostProcessor.defaultLazyInit</code> property in one
 * of the loaded properties files or as System parameter. The order of evaluation is the same as with
 * the <code>categoriesKey</code> (see above).
 * </p>
 * <p>
 * For beans which explicitely need lazy initialization (or not), a meta value can
 * be defined to overrule/enforce the default behavior:
 * <pre>&lt;meta key="j2:lazy" value="&lt;true|false&gt;"/&gt;</pre>
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @since 2.2
 * @version $Id$
 */
public class JetspeedBeanDefinitionFilter extends PropertiesLoaderSupport
{
    private static Log log = LogFactory.getLog(JetspeedBeanDefinitionFilter.class);
    
    public static final String SYSTEM_PROPERTY_CATEGORIES_KEY = "JetspeedBeanDefinitionFilter.categoriesKey";
    public static final String SYSTEM_PROPERTY_DEFAULT_LAZY_INIT = "JetspeedBeanDefinitionFilter.defaultLazyInit";
    
    public static final String CATEGORY_META_KEY = "j2:cat";
    public static final String ALIAS_META_KEY = "j2:alias";
    public static final String LAZY_META_KEY = "j2:lazy";

    private String categoriesKey;
    private Boolean defaultLazyInit;
    private Properties props;
    private Set categories;
    private boolean initialized;
    
    public JetspeedBeanDefinitionFilter()
    {
    }
    
    public JetspeedBeanDefinitionFilter(Boolean defaultLazyInit, String categories)
    {
        setDefaultLazyInit(defaultLazyInit);
        setCategories(categories);
    }
    
    public JetspeedBeanDefinitionFilter(Boolean defaultLazyInit, Set categories)
    {
        setDefaultLazyInit(defaultLazyInit);
        setCategories(categories);
    }
    
    public JetspeedBeanDefinitionFilter(String categoriesKey, Boolean defaultLazyInit)
    {
        setCategoriesKey(categoriesKey);
        setDefaultLazyInit(defaultLazyInit);
    }
    
    public JetspeedBeanDefinitionFilter(String propertiesLocation) throws IOException
    {
        loadProperties(new String[]{propertiesLocation});
    }
    
    public JetspeedBeanDefinitionFilter(String propertiesLocation, String categoriesKey, Boolean defaultLazyInit) throws IOException
    {
        this(categoriesKey, defaultLazyInit);
        loadProperties(new String[]{propertiesLocation});
    }
    
    public JetspeedBeanDefinitionFilter(String[] propertiesLocations, String categoriesKey, Boolean defaultLazyInit) throws IOException
    {
        this(categoriesKey, defaultLazyInit);
        loadProperties(propertiesLocations);
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
            HashSet circularRefs = new HashSet();
            ArrayList visitedProps = new ArrayList();
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

    public void setDefaultLazyInit(Boolean defaultLazyInit)
    {
        this.defaultLazyInit = defaultLazyInit;
    }
    
    public void setCategories(String categories)
    {
        if (categories != null && categories.length()>0)
        {            
            this.categories = new HashSet();
            StringTokenizer st = new StringTokenizer(categories, " ,;\t");
            while (st.hasMoreTokens())
            {
                this.categories.add(st.nextToken());
            }
        }
        else
        {
            this.categories = null;
        }
    }

    public void setCategories(Set categories)
    {
        this.categories = categories;
    }
    
    public void init() throws IllegalStateException
    {
        if (!initialized)
        {
            initialized = true;
            
            if (props != null && categories == null)
            {
                this.categories = new HashSet();
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
                this.categories = new HashSet();
            }
            String value = System.getProperty(SYSTEM_PROPERTY_DEFAULT_LAZY_INIT);
            if (value == null && props != null)
            {
                value = props.getProperty(SYSTEM_PROPERTY_DEFAULT_LAZY_INIT);
            }
            if (value != null)
            {
                defaultLazyInit = Boolean.valueOf(value);
            }
        }
    }
    
    public boolean match(BeanDefinition bd)
    {
        String beanCategories = (String)bd.getAttribute(CATEGORY_META_KEY);
        Boolean lazyInit = defaultLazyInit;
        boolean matched = true;
        if (beanCategories != null)
        {
            matched = false;
            StringTokenizer st = new StringTokenizer(beanCategories, " ,;\t");
            while (st.hasMoreTokens())
            {
                if (categories.contains(st.nextToken()))
                {
                    matched = true;
                    break;
                }
            }
        }
        if (matched)
        {
            if (bd instanceof AbstractBeanDefinition)
            {
                AbstractBeanDefinition abd = (AbstractBeanDefinition)bd;
                String j2Lazy = (String)bd.getAttribute(LAZY_META_KEY);
                if (j2Lazy != null)
                {
                    lazyInit = Boolean.valueOf((String)bd.getAttribute(LAZY_META_KEY));
                }
                if (lazyInit != null)
                {
                    abd.setLazyInit(lazyInit.booleanValue());
                }
            }
        }
        return matched;
    }
    
    public void registerDynamicAlias(BeanDefinitionRegistry registry, String beanName, BeanDefinition bd)
    {
        String alias = (String)bd.getAttribute(ALIAS_META_KEY);
        if (alias != null && !alias.equals(beanName))
        {
            if (registry.isBeanNameInUse(alias))
            {
                String src = "";
                if (bd.getSource() != null)
                {
                    src = "("+bd.getSource().toString()+")";
                }
                throw new BeanDefinitionValidationException("j2:alias '"+alias+"' for bean '"+beanName+"' already in use "+src);
            }
            registry.registerAlias(beanName, alias);
        }
    }
}
