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
package org.apache.portals.bridges.frameworks.spring.validation;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;

/**
 * ValidationSupport
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class ValidationSupport
{

    public static String getValueAsString(Object bean, String property)
    {
        Object value = null;

        if (bean instanceof Map)
        {
            value = ((Map) bean).get(property);
        }
        else if (bean instanceof DynaBean)
        {
            value = ((DynaBean) bean).get(property);
        }
        else
        {
            try
            {
                value = PropertyUtils.getProperty(bean, property);

            }
            catch (IllegalAccessException e)
            {
                //log.error(e.getMessage(), e);
            }
            catch (InvocationTargetException e)
            {
                //log.error(e.getMessage(), e);
            }
            catch (NoSuchMethodException e)
            {
                //log.error(e.getMessage(), e);
            }
        }

        if (value == null) { return null; }

        if (value instanceof String[])
        {
            return ((String[]) value).length > 0 ? value.toString() : "";

        }
        else if (value instanceof Collection)
        {
            return ((Collection) value).isEmpty() ? "" : value.toString();

        }
        else
        {
            return value.toString();
        }
    }

    /**
     * Checks if the field is required.
     * 
     * @return boolean If the field isn't <code>null</code> and has a length
     *         greater than zero, <code>true</code> is returned. Otherwise
     *         <code>false</code>.
     */
    public static boolean validateRequired(Object bean, ValidatorAction va, Field field, Map errors,
            ResourceBundle bundle)
    {
        String value = getValueAsString(bean, field.getProperty());
        boolean valid = !GenericValidator.isBlankOrNull(value);
        if (!valid)
        {
            if (bundle == null)
            {
                errors.put(field.getKey(), "Field " + field.getKey() + " is a required field.");
            }
            else
            {
                String displayName = bundle.getString(field.getArg(0).getKey());
                if (displayName == null)
                {
                    displayName = field.getKey();
                }
                Object[] args =
                { displayName};

                String message = bundle.getString(va.getMsg());
                if (message == null)
                {
                    message = "Field {0} is a required field.";
                }
                errors.put(field.getKey(), MessageFormat.format(message, args));
            }
        }
        return valid;
    }

    public static boolean validateRange(Object bean, ValidatorAction va, Field field, Map errors, ResourceBundle bundle) 
    {        
        int value = 0;
        String result = getValueAsString(bean, field.getProperty());
        if (result != null)
        {
            Integer intValue = GenericTypeValidator.formatInt(result);
            if (intValue != null)
            {    
                value = intValue.intValue();
            }
        }
        
       String minResult = field.getVarValue("min");
       if (minResult == null)
       {
           minResult = "0";
       }
       String maxResult = field.getVarValue("max");
       if (maxResult == null)
       {
           maxResult = "0";
       }
       int min = GenericTypeValidator.formatInt(minResult).intValue();
       int max = GenericTypeValidator.formatInt(maxResult).intValue();
       
       boolean valid = GenericValidator.isInRange(value, min, max);
       if (!valid)
       {
           if (bundle == null)
           {
               errors.put(field.getKey(), "Field " + field.getKey() + " is out of range: [" + min + "- " + max + "]");
           }
           else
           {
               String displayName = bundle.getString(field.getArg(0).getKey());
               if (displayName == null)
               {
                   displayName = field.getKey();
               }
               Object[] args =
               { displayName, minResult, maxResult};

               String message = bundle.getString(va.getMsg());
               if (message == null)
               {
                   message = "Field {0} is out of range: [{1} - {2}]";
               }
               errors.put(field.getKey(), MessageFormat.format(message, args));
           }
       }
       return valid;
    }   

    public static boolean validateDoubleRange(Object bean, ValidatorAction va, Field field, Map errors, ResourceBundle bundle)
    {
        double value = 0;
        String result = getValueAsString(bean, field.getProperty());
        if (result != null)
        {
            Double doubleValue = GenericTypeValidator.formatDouble(result);
            if (doubleValue != null)
            {    
                value = doubleValue.doubleValue();
            }
        }
        String minResult = field.getVarValue("min");
        if (minResult == null)
        {
            minResult = "0";
        }
        String maxResult = field.getVarValue("max");
        if (maxResult == null)
        {
            maxResult = "0";
        }

        double min = GenericTypeValidator.formatDouble(minResult).doubleValue();
        double max = GenericTypeValidator.formatDouble(maxResult).doubleValue();
        boolean valid = GenericValidator.isInRange(value, min, max);
        if (!valid)
        {
            if (bundle == null)
            {
                errors.put(field.getKey(), "Field " + field.getKey() + " is out of range: [" + min + "- " + max + "]");
            }
            else
            {
                String displayName = bundle.getString(field.getArg(0).getKey());
                if (displayName == null)
                {
                    displayName = field.getKey();
                }
                Object[] args =
                { displayName, minResult, maxResult};

                String message = bundle.getString(va.getMsg());
                if (message == null)
                {
                    message = "Field {0} is out of range: [{1} - {2}]";
                }
                errors.put(field.getKey(), MessageFormat.format(message, args));
            }
        }
        return valid;
    }

    public static boolean validateMask(Object bean, ValidatorAction va, Field field, Map errors, ResourceBundle bundle)
    {
        String value = getValueAsString(bean, field.getProperty());
        
        String mask = field.getVarValue("mask");
        if (mask == null)
        {
            return true; // no mask provide, let it pass
        }

        if (GenericValidator.isBlankOrNull(value))
        {
            return true; // this is how struts handles it
        }
        
        boolean valid = GenericValidator.matchRegexp(value, mask);
        if (!valid)
        {
            if (bundle == null)
            {
                errors.put(field.getKey(), "Field " + field.getKey() + " failed to match validation pattern: " +  mask);
            }
            else
            {
                String displayName = bundle.getString(field.getArg(0).getKey());
                if (displayName == null)
                {
                    displayName = field.getKey();
                }
                Object[] args =
                { displayName, mask};

                String message = bundle.getString(va.getMsg());
                if (message == null)
                {
                    message = "Field {0} failed to match validation pattern: {2}";
                }
                errors.put(field.getKey(), MessageFormat.format(message, args));
            }
        }        
        return valid;
    }

    public static boolean validateMaxLength(Object bean, ValidatorAction va, Field field, Map errors, ResourceBundle bundle)
    {
        String value = getValueAsString(bean, field.getProperty());
        
        int max = Integer.parseInt(field.getVarValue("maxlength"));

        if (GenericValidator.isBlankOrNull(value))
        {
            return true; 
        }
        
        boolean valid = GenericValidator.maxLength(value, max);        
        if (!valid)
        {
            if (bundle == null)
            {
                errors.put(field.getKey(), "Field " + field.getKey() + " surpasses maximum length: " +  max);
            }
            else
            {
                String displayName = bundle.getString(field.getArg(0).getKey());
                if (displayName == null)
                {
                    displayName = field.getKey();
                }
                Object[] args =
                { displayName, new Integer(max)};

                String message = bundle.getString(va.getMsg());
                if (message == null)
                {
                    message = "Field {0} surpasses maximum length {1}";                    
                }
                errors.put(field.getKey(), MessageFormat.format(message, args));
            }
        }        
        return valid;
    }
    
    public static void printResults(Object bean, ValidatorResults results, ValidatorResources resources, String formName)
    {

        boolean success = true;

        // Start by getting the form for the current locale and Bean.
        Form form = resources.getForm(Locale.getDefault(), formName);

        System.out.println("\n\nValidating:");
        System.out.println(bean);

        // Iterate over each of the properties of the Bean which had messages.
        Iterator propertyNames = results.getPropertyNames().iterator();
        while (propertyNames.hasNext())
        {
            String propertyName = (String) propertyNames.next();

            // Get the Field associated with that property in the Form
            Field field = form.getField(propertyName);

            // Look up the formatted name of the field from the Field arg0
            String prettyFieldName = propertyName; //apps.getString(field.getArg(0).getKey());

            // Get the result of validating the property.
            ValidatorResult result = results.getValidatorResult(propertyName);

            // Get all the actions run against the property, and iterate over
            // their names.
            Map actionMap = result.getActionMap();
            Iterator keys = actionMap.keySet().iterator();
            while (keys.hasNext())
            {
                String actName = (String) keys.next();

                // Get the Action for that name.
                ValidatorAction action = resources.getValidatorAction(actName);

                // If the result is valid, print PASSED, otherwise print FAILED
                System.out.println(propertyName + "[" + actName + "] ("
                        + (result.isValid(actName) ? "PASSED" : "FAILED") + ")");

                //If the result failed, format the Action's message against the
                // formatted field name
                if (!result.isValid(actName))
                {
                    success = false;
                    String message = "invalid field"; // apps.getString(action.getMsg());
                    if (actName.equals("doubleRange"))
                    {
                        Arg f1 = field.getArg(1);
                        Arg f2 = field.getArg(2);
                        Arg f0 = field.getArg(0);
                        Object[] args =
                        { prettyFieldName, field.getVar("min").getValue(), field.getVar("max").getValue()};
                        System.out.println("     Error message will be: " + MessageFormat.format(message, args));
                    }
                    else
                    {
                        Object[] args =
                        { prettyFieldName};
                        System.out.println("     Error message will be: " + MessageFormat.format(message, args));
                    }

                }
            }
        }
        if (success)
        {
            System.out.println("FORM VALIDATION PASSED");
        }
        else
        {
            System.out.println("FORM VALIDATION FAILED");
        }

    }

}