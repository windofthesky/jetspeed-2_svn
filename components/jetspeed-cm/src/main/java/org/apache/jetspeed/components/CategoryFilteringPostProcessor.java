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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;

/**
 * The CategoryFilteringPostProcessor is a Spring BeanFactoryPostProcessor to dynamically switch
 * bean lazy initialization based on configured meta data and category filtering.
 * <p>
 * Categories definitions are loaded from one or more properties files, and are simply sets of
 * names to evaluate bean definitions against. A categories set is a comma separated list of names.
 * Multiple sets of categories can be defined, and also reference (include) each other using standard
 * ${property} syntax.
 * </p>
 * <p>
 * The specific property containing the categories to be used for filtering is defined through
 * method {@link #setCategoriesKey}, or as <code>CategoryFilteringPostProcessor.categoriesKey</code>
 * property in one of the loaded property files, or as System (-D) parameter.
 * A System parameter is checked first, then the loaded properties and only as last resort the bean property itself.
 * </p>
 * <p>
 * Beans which needs to be matched against active categories must define a:<br/>
 * <pre>&lt;meta key="j2:cat" value="&lt;categories&gt;"/&gt;</pre> meta value within the bean definition.
 * The bean categories value may contain multiple comma separated values.
 * </p>
 * <p>
 * Unmatched beans (having a "j2:cat" meta value for which none were active) will be set to lazy
 * initialization. By not defining an id or name attribute, or a unique one not referenced
 * by other beans with different (or no) set of categories, different versions of a similar bean
 * can be defined within one set of assembly files.
 * </p>
 * <p>
 * Additionally an alias can be defined as bean meta value, allowing multiple beans to
 * be defined using the same alias:<br/>
 * <pre>&lt;meta key="j2:alias" value="&lt;alias&gt;"/&gt;</pre>
 * Only a matched bean its alias will be registered and
 * thereby able to be referenced by other beans (on this dynamically created alias).
 * For this type of "enabling" only one of many possible beans definitions, these beans should
 * not define an id or name attribute directly, or only an unique one which is not referenced or
 * only referenced by other beans which have a "matching" j2:cat meta value.
 * </p>
 * <p>
 * Optionally, all beans without a "j2:cat" meta value can be set to lazy initialization
 * too, through method {@link #setDefaultLazyInit} (default value: false). This property can also
 * be defined directly as <code>CategoryFilteringPostProcessor.defaultLazyInit</code> property in one
 * of the loaded properties files or as System parameter. The order of evaluation is the same as with
 * the <code>categoriesKey</code> (see above).
 * </p>
 * <p>
 * For beans which explicitely need lazy initialization (or not), a meta value can
 * be defined to overrule/enforce this:
 * <pre>&lt;meta key="j2:lazy" value="&lt;true|false&gt;"/&gt;</pre>
 * </p>
 * <p>
 * The CategoryFilteringPostProcessor can be defined anywhere (but only once) within the set of loaded
 * Spring assembly files. If however configuration properties are needed (like for referencing ${applicationRoot})
 * the PropertyPlaceHolderConfigurer used should have a lower {@link PropertyResourceConfigurer#setOrder} value configured
 * than the CategoryFilteringPostProcessor to ensure the PropertyPlaceHolderConfigurer is run before.
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @since 2.2
 * @version $Id$
 *
 */
public class CategoryFilteringPostProcessor extends PropertyResourceConfigurer
{
    private static Log log = LogFactory.getLog(CategoryFilteringPostProcessor.class);
    
    public static final String SYSTEM_PROPERTY_CATEGORIES_KEY = "CategoryFilteringPostProcessor.categoriesKey";
    public static final String SYSTEM_PROPERTY_DEFAULT_LAZY_INIT = "CategoryFilteringPostProcessor.defaultLazyInit";
    
    private String categoryMetakey = "j2:cat";
    private String aliasMetakey = "j2:alias";
    private String lazyMetakey = "j2:lazy";
    private String categoriesKey;
    private boolean defaultLazyInit;
    
    public void setCategoryMetakey(String categoryMetakey)
    {
        this.categoryMetakey = categoryMetakey;
    }
        
    public void setAliasMetakey(String aliasMetakey)
    {
        this.aliasMetakey = aliasMetakey;
    }
    
    public void setLazyMetakey(String lazyMetakey)
    {
        this.lazyMetakey = lazyMetakey;
    }
    
    public void setCategoriesKey(String categoriesKey)
    {
        this.categoriesKey = categoriesKey;
    }

    public void setDefaultLazyInit(boolean defaultLazyInit)
    {
        this.defaultLazyInit = defaultLazyInit;
    }
    
    protected void interpolateProperties(Properties props)
    {
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

    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties properties) throws BeansException
    {
        interpolateProperties(properties);
        HashSet categoriesList = new HashSet();
        StringTokenizer st = null;
        BeanDefinitionRegistry beanRegistry = beanFactory instanceof BeanDefinitionRegistry ? (BeanDefinitionRegistry)beanFactory : null;

        String value = System.getProperty(SYSTEM_PROPERTY_CATEGORIES_KEY);
        if (value == null)
        {
            value = properties.getProperty(SYSTEM_PROPERTY_CATEGORIES_KEY);
        }
        if (value != null)
        {
            categoriesKey = value;
        }
        
        value = System.getProperty(SYSTEM_PROPERTY_DEFAULT_LAZY_INIT);
        if (value == null)
        {
            value = properties.getProperty(SYSTEM_PROPERTY_DEFAULT_LAZY_INIT);
        }
        if (value != null)
        {
            defaultLazyInit = Boolean.parseBoolean(value);
        }
        
        if (categoriesKey == null || categoriesKey.length() == 0)
        {            
            throw new BeanDefinitionValidationException("Required categoriesKey property undefined");
        }
        else
        {
            String categories = (String)properties.get(categoriesKey);
            if (categories != null && categories.length()>0)
            {
                st = new StringTokenizer(categories, " ,;\t");
                while (st.hasMoreTokens())
                {
                    categoriesList.add(st.nextToken());
                }
            }
        }               
        
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        log.info("Processing "+beanNames.length+" beans using defaultLazyInit: ["+defaultLazyInit+"] with categoriesKey: ["+categoriesKey+"] defining categories: "+categoriesList);
        
        int matchCount = 0;
        int filterCount = 0;
        
        for (int i = 0; i < beanNames.length; i++)
        {
            BeanDefinition bd = beanFactory.getBeanDefinition(beanNames[i]);
            String beanCategories = (String)bd.getAttribute(categoryMetakey);
            boolean lazyInit = defaultLazyInit;
            boolean matched = false;
            if (beanCategories != null)
            {
                lazyInit = true;
                st = new StringTokenizer(beanCategories, " ,;\t");
                while (st.hasMoreTokens())
                {
                    if (categoriesList.contains(st.nextToken()))
                    {
                        lazyInit = false;
                        matched = true;
                        break;
                    }
                }
                if (matched) matchCount++;
                else filterCount++;
            }
            if (bd instanceof AbstractBeanDefinition)
            {
                AbstractBeanDefinition abd = (AbstractBeanDefinition)bd;
                String j2Lazy = (String)bd.getAttribute(lazyMetakey);
                if (j2Lazy != null)
                {
                    lazyInit = Boolean.parseBoolean((String)bd.getAttribute(lazyMetakey));
                }
                abd.setLazyInit(lazyInit);
            }
            if (matched && beanRegistry != null)
            {
                String alias = (String)bd.getAttribute(aliasMetakey);
                if (alias != null && !alias.equals(beanNames[i]))
                {
                    if (beanRegistry.isBeanNameInUse(alias))
                    {
                        String src = "";
                        if (bd.getSource() != null)
                        {
                            src = "("+bd.getSource().toString()+")";
                        }
                        throw new BeanDefinitionValidationException("j2:alias '"+alias+"' for bean '"+beanNames[i]+"' already in use "+src);
                    }
                    beanRegistry.registerAlias(beanNames[i], alias);
                }
            }
        }
        log.info("Processing matched "+matchCount+" beans, filtered "+filterCount+" beans");
    }        
}
