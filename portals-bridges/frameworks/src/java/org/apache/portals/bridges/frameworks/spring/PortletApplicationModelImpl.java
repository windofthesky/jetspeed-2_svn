/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.portals.bridges.frameworks.spring;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanFactory;

import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResults;
import org.apache.portals.bridges.frameworks.ExternalComponentSupport;
import org.apache.portals.bridges.frameworks.Lookup;
import org.apache.portals.bridges.frameworks.model.ModelBean;
import org.apache.portals.bridges.frameworks.model.PortletApplicationModel;
import org.apache.portals.bridges.frameworks.spring.ModelBeanImpl;


/**
 * PortletApplicationModelImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletApplicationModelImpl implements PortletApplicationModel
{
    /**
     * Spring configuration: view to bean name map
     */
    private static final String PORTLET_VIEW_BEAN_MAP = "portlet-view-bean-map";
    
    /**
     * Spring configuration: view to validator name map
     */
    private static final String PORTLET_VIEW_VALIDATOR_MAP = "portlet-view-validator-map";
    
    /**
     * logical view to template map
     */
    private static final String PORTLET_LOGICAL_VIEW_MAP = "portlet-views";

    /**
     * map for action forward definitions (success, failure)
     */
    private static final String PORTLET_ACTION_FORWARD_MAP = "portlet-action-forward-map";
        
    /**
     * Spring Factory 
     */
    private XmlBeanFactory springFactory = null;

    /**
     * View Bean Map
     */
    private Map viewBeanMap = null;
    
    /**
     * Validation resources
     */
    private ValidatorResources validations = null;
        
    /**
     * View Validation Map
     */
    private Map viewValidatorMap = null;

    /**
     * Map from logical views to templates
     */
    private Map logicalViewMap = null;

    /**
     * Map from view:status to view
     */    
    private Map actionForwardMap = null;
    
    private Map modelBeanMap = new HashMap();
    
    private Map externalSupportMap = new HashMap();
    
    private static Object semaphore = new Object();
    
    private String springConfig;
    private String validatorConfig = null;
    
    public PortletApplicationModelImpl(String springConfig, String validatorConfig)
    {
        this.springConfig = springConfig;
        this.validatorConfig = validatorConfig;
    }
    
    public void setExternalSupport(Map map)
    {
        this.externalSupportMap = map;
    }
    
    public void init(PortletConfig config)
    throws PortletException
    {
        // load Spring
        try 
        {
            synchronized (semaphore)
            {
                if (null == springFactory)
                {
                    InputStream is = config.getPortletContext().getResourceAsStream(springConfig);                    
                    springFactory = new XmlBeanFactory(is);
                    is.close();
                }
            }
         } 
         catch (Exception e) 
         {
             throw new PortletException("Failed to load spring configuration.", e);
         }   
                           
         // load validator
         synchronized (semaphore)
         {             
             if (validatorConfig != null && null == validations)
             {
                 InputStream is = null;
                 
                 try
                 {
                     // TODO: support extensible user-defined validator resources
                     //is = this.getClass().getResourceAsStream("/org/apache/portals/bridges/velocity/validation/default-portlet-validation.xml");
                     is = config.getPortletContext().getResourceAsStream(validatorConfig);                    
                     
                     validations = new ValidatorResources(is);
                 }
                 catch (Exception e)
                 {
                     throw new PortletException("Failed to load validator configuration.", e);
                 }
                 finally 
                 {
                     // Make sure we close the input stream.
                     if (is != null) 
                     {
                         try
                         {
                             is.close();
                         }
                         catch (Exception e)
                         {}
                     }
                 }                     
             }
         }

         // Logical Views to templates
         synchronized (semaphore)
         {
             logicalViewMap = (Map)springFactory.getBean(PORTLET_LOGICAL_VIEW_MAP);
             if (logicalViewMap == null)
             {
                 logicalViewMap = new HashMap(); 
             }
         }
         
         // View to Validator Map
         synchronized (semaphore)
         {             
             viewValidatorMap = (Map)springFactory.getBean(PORTLET_VIEW_VALIDATOR_MAP);
             if (viewValidatorMap == null)
             {
                 viewValidatorMap = new HashMap(); 
             }
         }
         
         // View to Bean Map
         synchronized (semaphore)
         {
             viewBeanMap = (Map)springFactory.getBean(PORTLET_VIEW_BEAN_MAP);
             if (viewBeanMap == null)
             {
                 viewBeanMap = new HashMap();              
             }
         }        

         // Action Forward map
         synchronized (semaphore)
         {
             actionForwardMap = (Map)springFactory.getBean(PORTLET_ACTION_FORWARD_MAP);
             if (actionForwardMap == null)
             {
                 actionForwardMap = new HashMap();              
             }
         }
         
         
    }
    
    public ModelBean getModelBean(String view)
    {
        ModelBean modelBean;
        String beanName = (String)viewBeanMap.get(view);
        if (beanName != null)
        {
            modelBean = (ModelBean)modelBeanMap.get(beanName);
            if (modelBean == null)
            {
                BeanDefinition bd = springFactory.getBeanDefinition(beanName);
                Object bean = springFactory.getBean(beanName);
                if (bd == null || bean == null)
                {
                    return new ModelBeanImpl(beanName, ModelBean.POJO);
                }                   
                String lookup = null;
                boolean requiresExternalSupport = false;
                PropertyValue value = bd.getPropertyValues().getPropertyValue("lookupKey");
                if (value != null)
                {
                    lookup = (String)value.getValue();
                }                
                if (bean instanceof ExternalComponentSupport)
                {
                    requiresExternalSupport = true;
                }
                modelBean = new ModelBeanImpl(beanName, ModelBean.POJO, lookup, requiresExternalSupport);
                modelBeanMap.put(beanName, modelBean);
            }
        }
        else
        {
            modelBean = new ModelBeanImpl(beanName, ModelBean.PREFS_MAP);
        }        
        return modelBean;
    }
    
    public String getTemplate(String view)
    {
        return (String)logicalViewMap.get(view);
    }
    
    public Object lookupBean(ModelBean mb, String key)
    {
        Object bean = springFactory.getBean(mb.getBeanName());
        if (bean != null)
        {
            if (mb.isRequiresExternalSupport())
            {
                ExternalComponentSupport ecs = (ExternalComponentSupport)bean;
                ecs.setExternalSupport(externalSupportMap.get(mb.getBeanName()));
            }
            if (mb.isRequiresLookup())
            {
                ((Lookup)bean).lookup(key);
            }
        }
        return bean;
    }

    public Object createBean(ModelBean mb)
    {
        Object bean = springFactory.getBean(mb.getBeanName());
        if (bean != null)
        {
            if (mb.isRequiresExternalSupport())
            {
                ExternalComponentSupport ecs = (ExternalComponentSupport)bean;
                ecs.setExternalSupport(externalSupportMap.get(mb.getBeanName()));
            }
        }
        return bean;
    }
    
    public Map createPrefsBean(ModelBean mb, Map original)
    {
        Map prefs = new HashMap();
        Iterator it = original.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry)it.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String)
            {
                prefs.put(key, value);
            }
            else if (value instanceof String[])
            {
                prefs.put(key, ((String[])value)[0]);
            }
        }
        return prefs;        
    }

    public Map validate(Object bean, String view, ResourceBundle bundle)
    throws PortletException
    {
        Map result = new HashMap();
        if (validations == null)
        {
            return result; // no validation configured
        }
        // Get the bean name from the bean-view map
        String validatorFormName = (String)viewValidatorMap.get(view);
        if (validatorFormName == null)
        {
            return result; // no validation for this bean
        }

        Validator validator = new Validator(validations, validatorFormName);

        // Tell the validator which bean to validate against.
        validator.setParameter(Validator.BEAN_PARAM, bean);
        
        // Parameters used by our validators
        validator.setParameter("java.util.Map", result);
        validator.setParameter("java.util.ResourceBundle", bundle);
        
        ValidatorResults results = null;
                
        try
        {
            validator.setOnlyReturnErrors(true);
            results = validator.validate();
            if (results.isEmpty())
            {
                return result;
            }
        }
        catch (ValidatorException e)
        {
            throw new PortletException("Error in processing validation: ", e);            
        }
        
        return result;        
    }
    
    public String getForward(String view, String status)
    {
        return (String)actionForwardMap.get(view + ":" + status);
    }

    public String getForward(String view)
    {
        return (String)actionForwardMap.get(view);
    }
    
}
