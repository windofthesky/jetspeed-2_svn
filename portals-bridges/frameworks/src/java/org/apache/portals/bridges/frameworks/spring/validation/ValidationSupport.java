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
import org.apache.commons.validator.util.ValidatorUtils;


/**
 * ValidationSupport
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ValidationSupport
{
    public static String getValueAsString(Object bean, String property)
    {
        Object value = null;
        
        if (bean instanceof Map)
        {
            value = ((Map)bean).get(property);
        }
        else if (bean instanceof DynaBean)
        {
            value = ((DynaBean)bean).get(property);            
        }
        else
        {
            try {
                value = PropertyUtils.getProperty(bean, property);
    
            } catch(IllegalAccessException e) {
                //log.error(e.getMessage(), e);
            } catch(InvocationTargetException e) {
                //log.error(e.getMessage(), e);
            } catch(NoSuchMethodException e) {
                //log.error(e.getMessage(), e);
            }
        }
        
        if (value == null) 
        {
            return null;
        }

        if (value instanceof String[]) 
        {
            return ((String[]) value).length > 0 ? value.toString() : "";

        } else if (value instanceof Collection) 
        {
            return ((Collection) value).isEmpty() ? "" : value.toString();

        } else {
            return value.toString();
        }        
    }
    
    /**
     * Checks if the field is required.
     *
     * @return boolean If the field isn't <code>null</code> and
     * has a length greater than zero, <code>true</code> is returned.  
     * Otherwise <code>false</code>.
     */
    public static boolean validateRequired(Object bean, Field field) 
    {
       String value = getValueAsString(bean, field.getProperty());
       boolean valid = !GenericValidator.isBlankOrNull(value);
       return valid;
    }

    public static boolean validateRange(Object bean, Field field) 
    {        
       String result = ValidatorUtils.getValueAsString(bean, field.getProperty());
       if (result == null)
           return false;
              
       Integer intValue = GenericTypeValidator.formatInt(result);
       if (intValue == null)
           return false;
       
       int value = intValue.intValue();

       String minResult = field.getVarValue("min");
       if (minResult == null)
           return false;

       String maxResult = field.getVarValue("max");
       if (maxResult == null)
           return false;
       
       int min = GenericTypeValidator.formatInt(minResult).intValue();
       int max = GenericTypeValidator.formatInt(maxResult).intValue();
       
       return GenericValidator.isInRange(value, min, max);
    }

    public static boolean validateDoubleRange(Object bean, Field field) 
    {
       String result = ValidatorUtils.getValueAsString(bean, field.getProperty());
       if (result == null)
           return false;
              
       Double doubleValue = GenericTypeValidator.formatDouble(result);
       if (doubleValue == null)
           return false;
       
       double value = doubleValue.intValue();

       String minResult = field.getVarValue("min");
       if (minResult == null)
           return false;

       String maxResult = field.getVarValue("max");
       if (maxResult == null)
           return false;
       
       double min = GenericTypeValidator.formatDouble(minResult).doubleValue();
       double max = GenericTypeValidator.formatDouble(maxResult).doubleValue();
       
       return GenericValidator.isInRange(value, min, max);
    }
 
    public static boolean validateMask(Object bean, Field field)
    {
        String mask = field.getVarValue("mask");
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        
        try 
        {
            if (!GenericValidator.isBlankOrNull(value)
                && !GenericValidator.matchRegexp(value, mask)) 
            {        
              //  errors.add(
              //  field.getKey(),
              //  Resources.getActionMessage(request, va, field));
        
                return false;
            } 
            else 
            {
                return true;
            }
        } 
        catch (Exception e) 
        {
            //log.error(e.getMessage(), e);
        }
        return true;
    }
    

    public static void printResults(
            Object bean,
            ValidatorResults results,
            ValidatorResources resources,
            String formName)
    {
                
            boolean success = true;

            // Start by getting the form for the current locale and Bean.
            Form form = resources.getForm(Locale.getDefault(), formName);

            System.out.println("\n\nValidating:");
            System.out.println(bean);

            // Iterate over each of the properties of the Bean which had messages.
            Iterator propertyNames = results.getPropertyNames().iterator();
            while (propertyNames.hasNext()) {
                String propertyName = (String) propertyNames.next();

                // Get the Field associated with that property in the Form
                Field field = form.getField(propertyName);

                // Look up the formatted name of the field from the Field arg0
                String prettyFieldName = propertyName; //apps.getString(field.getArg(0).getKey());

                // Get the result of validating the property.
                ValidatorResult result = results.getValidatorResult(propertyName);

                // Get all the actions run against the property, and iterate over their names.
                Map actionMap = result.getActionMap();
                Iterator keys = actionMap.keySet().iterator();
                while (keys.hasNext()) {
                    String actName = (String) keys.next();

                    // Get the Action for that name.
                    ValidatorAction action = resources.getValidatorAction(actName);

                    // If the result is valid, print PASSED, otherwise print FAILED
                    System.out.println(
                        propertyName
                            + "["
                            + actName
                            + "] ("
                            + (result.isValid(actName) ? "PASSED" : "FAILED")
                            + ")");

                    //If the result failed, format the Action's message against the formatted field name
                    if (!result.isValid(actName)) {
                        success = false;
                        String message = "invalid field"; // apps.getString(action.getMsg());
                        if (actName.equals("doubleRange"))
                        {                        
                            Arg f1 = field.getArg(1);
                            Arg f2 = field.getArg(2);
                            Arg f0 = field.getArg(0);
                            Object[] args = { prettyFieldName, field.getVar("min").getValue(), field.getVar("max").getValue()  };
                            System.out.println(
                                    "     Error message will be: "
                                    + MessageFormat.format(message, args));                        
                        }
                        else
                        {
                            Object[] args = { prettyFieldName };
                            System.out.println(
                                    "     Error message will be: "
                                    + MessageFormat.format(message, args));
                        }

                    }
                }
            }
            if (success) {
                System.out.println("FORM VALIDATION PASSED");
            } else {
                System.out.println("FORM VALIDATION FAILED");
            }

        }
    
}
