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
package org.apache.jetspeed.i18n;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.jetspeed.exception.JetspeedException; // for javadoc ref
import org.apache.jetspeed.security.SecurityException; // for javadoc ref

/**
 * KeyedMessage provides an automatically derived i18n message key based on its static instance definition and can be
 * used as comparable constant too.
 * <h3>Purpose</h3>
 * <p>
 * With a KeyedMessage a named constant message (format) can be statically defined which automatically translate
 * themselves for a specific locale using an automatically derived ResourceBundle or even a specified one.
 * </p>
 * <h3>Key derivation</h3>
 * <p>
 * Because KeyedMessages are created with a default message (format), even if no ResourceBundle or its key is defined or
 * can't be found, message translation is still possible.
 * </p>
 * <p>
 * A KeyedMessage automatically derives the ResourceBundle lookup key from its (statically defined) instance field name
 * using the following format: <br/><br/><code>
 *     &nbsp;&nbsp;&lt;containingClass.name&gt;.&lt;staticInstanceField.name&gt;
 * </code>
 * <br/>
 * </p>
 * <p>
 * The containingClass is derived at construction time by analyzing the StackTraceElements of a thrown exception. This
 * <em><b>requires</b></em> the instance to be defined as a public static field!
 * </p>
 * <p>
 * At first access, the key is resolved by inspecting the derived containingClass for the <em>declared</em> field
 * defining this instance.
 * </p>
 * <p>
 * If the KeyedMessage instance <em><b>wasn't</b></em> defined as public static field, the key can't be resolved and
 * message translation using a ResourceBundle won't be possible. Translation using the default message will still work
 * though. Furthermore, this instance can't be used as comparable named constant as the {@link #equals(Object)}method
 * will always return false in this case.
 * </p>
 * <h3>Default ResourceBundle name derivation</h3>
 * <p>
 * When the key of a KeyedMessage is resolved, the default ResourceBundle name for message translation is retrieved from
 * the defined public static String field named {@link #KEYED_MESSAGE_BUNDLE_FIELD_NAME "KEYED_MESSAGE_BUNDLE"}defined
 * in its containingClass or one of its superClasses or interfaces.
 * </p>
 * <p>
 * If this field cannot be found, the fully qualified name of the containingClass is used.
 * </p>
 * <p>
 * ResourceBundle names are cached in a Map for each containingClass and only derived for the first KeyedMessage defined
 * in a containingClass.
 * </p>
 * <p>
 * <em>Again: only <b>resolved</b> instances can use a ResourceBundle for message translation.</em>
 * </p>
 * <h3>Default Locale lookup</h3>
 * <p>
 * When a message is translated without a specified Locale, {@link CurrentLocale#get()}is used to determine the default
 * Locale for the current Thread.
 * </p>
 * <p>
 * In Jetspeed, the <code>LocalizationValve</code> initializes the {@link CurrentLocale} on each request.
 * KeyedMessages accessed within the context of an Jetspeed request therefore will always be translated using the
 * current user Locale with the {@link #getMessage()}or {@link #toString()}methods.
 * </p>
 * <h3>Default ResourceBundle lookup</h3>
 * <p>
 * If a message translation is done using the default ResourceBundle name the ResourceBundle is retrieved using the
 * ClassLoader of the containingClass. This means the bundle(s) must be provided in the same context as from where the
 * containingClass is loaded. Usually (and preferably), this will be from the shared classpath of the webserver.
 * </p>
 * <h3>MessageFormat parameters</h3>
 * <p>
 * MessageFormat patterns can also be used for a KeyedMessage.<br/>
 * With the {@link #create(Object[])}method a specialized copy of a KeyedMessage instance can be created containing the
 * arguments to be used during message translation.
 * </p>
 * <p>
 * This new copy remains {@link equals(Object)}to its source and can still be used for named constant comparison.
 * </p>
 * <p>
 * For simplified usage, three {@link #create(Object)},{@link #create(Object, Object)}and
 * {@link #create(Object, Object, Object)}methods are provided which delegate to {@link #create(Object[])}with their
 * argument(s) transformed into an Object array.
 * </p>
 * <h3>Extending KeyedMessage</h3>
 * <p>
 * An statically defined KeyedMessage can be used as a "simple" named constant. <br/>If additional metadata is required
 * like some kind of status, level or type indication, the KeyedMessage class can easily be extended by providing a
 * specialized version of the {@link #create(KeyedMessage, Object[])}copy factory.
 * </p>
 * <h3>Usage</h3>
 * <p>
 * KeyedMessage has been used to replace the hardcoded {@link SecurityException} String constants. <br/>The
 * ResourceBundle name used is defined by {@link JetspeedException#KEYED_MESSAGE_BUNDLE} which is the superClass of
 * {@link SecurityException}.<br/>
 * <p>
 * <em>For a different ResourceBundle to be used for SecurityException messages a KEYED_MESSAGE_BUNDLE field can be defined
 * in {@link SecurityException} too, overriding the one in {@link JetspeedException}.</em>
 * </p>
 * <p>
 * Example:
 * </p>
 * <pre>
 *       public class JetspeedException extends Exception {
 *           public static final String KEYED_MESSAGE_BUNDLE = &quot;org.apache.jetspeed.exception.JetspeedExceptionMessages&quot;;
 *           ...
 *    
 *           public String getMessage() {
 *                if ( keyedMessage != null ) {
 *                   return keyedMessage.getMessage(); // translated using current Locale and default ResourceBundle
 *                }
 *                return super.getMessage();
 *           }
 *       }
 *    
 *       public class SecurityException extends JetspeedException {
 *           public static final KeyedMessage USER_DOES_NOT_EXIST = new KeyedMessage(&quot;The user {0} does not exist.&quot;);
 *           ...
 *       }
 *    
 *       // resource file: org.apache.jetspeed.exception.JetspeedExceptionMessages_nl.properties
 *       org.apache.jetspeed.security.SecurityException.USER_DOES_NOT_EXIST = De gebruiker {0} bestaat niet.
 *       ...
 *    
 *       public class UserManagerImpl implements UserManager {
 *           public User getUser(String username) throws SecurityException {
 *               ...
 *               if (null == userPrincipal) { 
 *                   throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(username));
 *               }
 *               ...
 *           }
 *           ...
 *       }
 *    
 *       // example get User
 *       try {
 *           User user = userManager.getUser(userName);
 *       } catch (SecurityException sex) {
 *           if ( SecurityException.USER_DOES_NOT_EXISTS.equals(sex.getKeyedMessage()) {
 *               // handle USER_DOES_NOT_EXISTS error
 *           }
 *       }    
 * </pre>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class KeyedMessage implements Serializable
{
    private static final long serialVersionUID = -2741118913698034852L;

    /**
     * Static String Field name searched for in the class defining a KeyedMessage containing the default resource bundle
     * to use for translation. <br/><em>Note: this Field is looked up using definingClass.getField thus it may also be
     * defined in a superclass or interface of the definingClass.</em>
     */
    public static final String   KEYED_MESSAGE_BUNDLE_FIELD_NAME = "KEYED_MESSAGE_BUNDLE";

    /**
     * Key value for an unresolved KeyMessage.
     */
    private static final String  UNRESOLVED_KEY                  = KeyedMessage.class.getName() + ".<unresolved>";

    /**
     * Map caching default resource bundle names keyed on containingClass
     */
    private static final HashMap<Class<?>, String> resourceNameMap                 = new HashMap<Class<?>, String>();

    /**
     * Default message used when key couldn't be looked up in the default or a specified resource bundle
     */
    private String               message;

    /**
     * Dynamically derived key based on the definingClass name, postfixed with the static field name of this instance
     * </br>
     * 
     * @see #getKey()
     */
    private String               key;
    
    private String               scopedKey;

    /**
     * Optional message format arguments which can only be set using a derived KeyedMessage using the
     * {@link #create(Object[])}method(s).
     */
    private Object[]             arguments;

    /**
     * The class in which this instance is defined as a static Field.
     */
    private Class<?>             containingClass;

    /**
     * Indicates if this instance could be {@link #resolve() resolved}.
     */
    private boolean              resolved;

    /**
     * Constructs a derived KeyedMessage from another KeyedMessage to provide additional message format arguments.
     * 
     * @see #create(Object[])
     * @param source the KeyedMessage to derive this instance from
     * @param arguments this instance specific message format arguments
     */
    protected KeyedMessage(KeyedMessage source, String scope, Object[] arguments)
    {
        this.key = source.getKey();
        this.message = source.message;
        this.resolved = source.resolved;
        this.containingClass = source.containingClass;
        this.arguments = arguments;
        if (resolved && scope != null)
        {
            int split = source.containingClass.getName().length()+1;
            this.scopedKey = key.substring(0,split)+scope+"."+key.substring(split);
        }
    }

    /**
     * Constructs a new KeyedMessage which will dynamically derive its own {@link #getKey()}.
     * 
     * @param message the default message used when the {@link #getKey()}could not be found in the default or a
     *            specified resource bundle.
     */
    public KeyedMessage(String message)
    {
        try
        {
            throw new Exception();
        }
        catch (Exception e)
        {
            StackTraceElement[] elements = e.getStackTrace();
            if (elements.length >= 2)
            {
                String containingClassName = elements[1].getClassName();
                try
                {
                    containingClass = Thread.currentThread().getContextClassLoader().loadClass(containingClassName);
                }
                catch (ClassNotFoundException e1)
                {
                    key = UNRESOLVED_KEY;
                }
            }
        }
        this.message = message;
    }

    private String getResourceName()
    {
        synchronized (resourceNameMap)
        {
            return resourceNameMap.get(containingClass);
        }
    }

    /**
     * @see KeyedMessage
     */
    private void resolve()
    {
        if (key == null)
        {
            // search for this instance as a statically declared field in the containingClass to find out the name
            // to use.
            Field[] fields = containingClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++)
            {
                try
                {
                    if (fields[i].getType() == this.getClass() && Modifier.isStatic(fields[i].getModifiers())
                        && fields[i].get(null) == this)
                    {
                        // resolved: save the key
                        key = containingClass.getName() + "." + fields[i].getName();
                        resolved = true;

                        // Now derive the default resource bundle if not already done before
                        synchronized (resourceNameMap)
                        {
                            if (getResourceName() == null)
                            {
                                // Find resource bundle name by looking up the statically defined
                                // KEYED_MESSAGE_BUNDLE_FIELD_NAME String field in the containingClass.
                                String resourceName = null;
                                try
                                {
                                    Field field = containingClass.getField(KEYED_MESSAGE_BUNDLE_FIELD_NAME);
                                    if (field != null && field.getType() == String.class
                                        && Modifier.isStatic(field.getModifiers()))
                                    {
                                        resourceName = (String) field.get(null);
                                    }
                                }
                                catch (Exception e)
                                {
                                }
                                if (resourceName == null)
                                {
                                    // fallback to containingClass name as resource bundle name
                                    resourceName = containingClass.getName();
                                }
                                resourceNameMap.put(containingClass, resourceName);
                            }
                        }

                        break;
                    }
                }
                catch (Exception e)
                {
                }
            }
            if (key == null)
            {
                key = UNRESOLVED_KEY;
            }
        }
    }

    /**
     * Formats a message using MessageFormat if arguments are defined, otherwise simply returns the argument.
     * 
     * @param message the message format
     * @return formatted message
     */
    private String format(String message)
    {
        if (arguments != null && arguments.length > 0)
        {
            return new MessageFormat(message).format(arguments);
        }
        else
        {
            return message;
        }
    }

    /**
     * Extendable KeyedMessage factory
     * 
     * @param source the source to copy from
     * @param arguments the optional message format arguments
     * @return copied instance with new arguments set
     */
    protected KeyedMessage create(KeyedMessage source, Object[] arguments)
    {
        return new KeyedMessage(source, null, arguments);
    }

    /**
     * Extendable scoped KeyedMessage factory
     * 
     * @param source the source to copy from
     * @param scope the optional scope key infix between the containing class name and the field name
     * @param arguments the optional message format arguments
     * @return copied instance with new arguments set
     */
    protected KeyedMessage createScoped(KeyedMessage source, String scope, Object[] arguments)
    {
        return new KeyedMessage(source, scope, arguments);
    }

    /**
     * Creates a derived KeyedMessage from this instance to provide additional message format arguments. <br/>The new
     * instance will be {@link #equals(Object)}to this instance with only different arguments. <br/><br/>Note: the
     * argument objects should be lightweight types and preferably Serializable instances
     * 
     * @param arguments The derived instance specific message format arguments
     * @return derived KeyedMessage {@link #equals(Object) equal}to this with its own message format arguments
     */
    public KeyedMessage create(Object[] arguments)
    {
        return new KeyedMessage(this, null, arguments);
    }

    /**
     * Creates a derived scoped KeyedMessage from this instance to provide additional message format arguments. <br/>
     * The new instance will be {@link #equals(Object)}to this instance with only different arguments. <br/><br/>
     * To allow reusing of the original KeyedMessage message format translatable not only by language but also for 
     * specific contexts, this method allows providing a "scope" message key infix for lookup of a subset of the
     * localized message specific for the specified "scope".
     * Note: the argument objects should be lightweight types and preferably Serializable instances
     * 
     * @param scope the optional scope key infix between the containing class name and the field name
     * @param arguments The derived instance specific message format arguments
     * @return derived KeyedMessage {@link #equals(Object) equal}to this with its own message format arguments
     */
    public KeyedMessage createScoped(String scope, Object[] arguments)
    {
        return new KeyedMessage(this, scope, arguments);
    }

    /**
     * Simplified version of {@link #create(Object[])}with only one message argument
     * 
     * @param single message format argument
     * @see #create(Object[])
     * @return derived KeyedMessage {@link #equals(Object) equal}to this with its own message format argument
     */
    public KeyedMessage create(Object o)
    {
        return create(new Object[] { o });
    }

    /**
     * Simplified version of {@link #createScoped(String, Object[])} without message arguments
     * 
     * @param scope the optional scope key infix between the containing class name and the field name
     * @see #createScoped(String,Object[])
     * @return derived KeyedMessage {@link #equals(Object) equal}to this with its own message format argument
     */
    public KeyedMessage createScoped(String scope)
    {
        return createScoped(scope, (Object []) null);
    }

    /**
     * Simplified version of {@link #createScoped(String, Object[])}with only one message argument
     * 
     * @param scope the optional scope key infix between the containing class name and the field name
     * @param single message format argument
     * @see #createScoped(String,Object[])
     * @return derived KeyedMessage {@link #equals(Object) equal}to this with its own message format argument
     */
    public KeyedMessage createScoped(String scope, Object o)
    {
        return createScoped(scope, new Object[] { o });
    }

    /**
     * Simplified version of {@link #create(Object[])}with only two arguments
     * 
     * @param single message format argument
     * @see #create(Object[])
     * @return derived KeyedMessage {@link #equals(Object) equal}to this with its own message format arguments
     */
    public KeyedMessage create(Object o1, Object o2)
    {
        return create(new Object[] { o1, o2 });
    }

    /**
     * Simplified version of {@link #createScoped(String, Object[])}with only two arguments
     * 
     * @param scope the optional scope key infix between the containing class name and the field name
     * @param single message format argument
     * @see #createScoped(String,Object[])
     * @return derived KeyedMessage {@link #equals(Object) equal}to this with its own message format arguments
     */
    public KeyedMessage createScoped(String scope, Object o1, Object o2)
    {
        return createScoped(scope, new Object[] { o1, o2 });
    }

    /**
     * Simplified version of {@link #create(Object[])}with only three arguments
     * 
     * @param single message format argument
     * @see #create(Object[])
     * @return derived KeyedMessage {@link #equals(Object) equal}to this with its own message format arguments
     */
    public KeyedMessage create(Object o1, Object o2, Object o3)
    {
        return create(new Object[] { o1, o2, o3 });
    }

    /**
     * Simplified version of {@link #createScoped(String, Object[])}with only three arguments
     * 
     * @param scope the optional scope key infix between the containing class name and the field name
     * @param single message format argument
     * @see #createScoped(String,Object[])
     * @return derived KeyedMessage {@link #equals(Object) equal}to this with its own message format arguments
     */
    public KeyedMessage createScoped(String scope, Object o1, Object o2, Object o3)
    {
        return createScoped(scope, new Object[] { o1, o2, o3 });
    }

    /**
     * Dynamically derived key based on the definingClass name, postfixed with the static field name of this instance.
     * <br/><br/>Format: <br/><code>
     *     &nbsp;&nbsp;&lt;containingClass.name&gt;.&lt;staticInstanceField.name&gt;
     * </code>
     * <br/><br/>If this instance couldn't be resolved, generic value UNRESOLVED_KEY will have been set.
     * 
     * @return derived key
     */
    public final String getKey()
    {
        resolve();
        return key;
    }

    /**
     * Loads and returns a Locale specific default ResourceBundle for this instance. <br/>If this instance couldn't be
     * {@link #resolve() resolved}or the bundle couldn't be loadednull will be returned. <br/>The ResourceBundle will
     * be loaded using the {@link #containingClass}its ClassLoader.
     * 
     * @param locale the Locale to lookup the locale specific default ResourceBundle
     * @return a Locale specific default ResourceBundle
     */
    public ResourceBundle getBundle(Locale locale)
    {
        resolve();
        if (resolved)
        {
            try
            {
                return ResourceBundle.getBundle(getResourceName(), locale, containingClass.getClassLoader());
            }
            catch (RuntimeException e)
            {
            }

        }
        return null;
    }

    /**
     * Loads and returns the default ResourceBundle for this instance using the
     * {@link CurrentLocale#get() current Locale}.
     * 
     * @see #getBundle(Locale)
     * @see CurrentLocale
     * @return the default ResourceBundle for the current Locale
     */
    public ResourceBundle getBundle()
    {
        return getBundle(CurrentLocale.get());
    }

    /**
     * @return formatted message using the default ResourceBundle using the {@link CurrentLocale current Locale}.
     * @see #getBundle()
     */
    public String getMessage()
    {
        return getMessage(getBundle());
    }

    /**
     * @param bundle a specific ResourceBundle defining this instance {@link #getKey() key}
     * @return formatted message using a specific ResourceBundle.
     */
    public String getMessage(ResourceBundle bundle)
    {
        resolve();
        String message = this.message;
        if (resolved && bundle != null)
        {
            if (scopedKey != null)
            {
                try
                {
                    message = bundle.getString(scopedKey);
                    return format(message);
                }
                catch (RuntimeException e)
                {
                    // ignore: fallback to default non-scoped message
                }
            }
            try
            {
                message = bundle.getString(key);
            }
            catch (RuntimeException e)
            {
                // ignore: fallback to default message
            }
        }
        return format(message);
    }

    /**
     * @param locale a specific Locale
     * @return formatted message using the default ResourceBundle using a specific Locale.
     */
    public String getMessage(Locale locale)
    {
        return getMessage(getBundle(locale));
    }

    /**
     * @return the arguments defined for this {@link #create(Object[]) derived}instance
     * @see #create(Object[])
     */
    public Object[] getArguments()
    {
        return arguments;
    }

    /**
     * @param index argument number
     * @return an argument defined for this {@link #create(Object[]) derived}instance
     */
    public Object getArgument(int index)
    {
        return arguments[index];
    }

    /**
     * @return formatted message using the default ResourceBundle using the {@link CurrentLocale current Locale}.
     * @see #getMessage()
     */
    public String toString()
    {
        return getMessage();
    }

    /**
     * @param otherObject KeyedMessage instance to compare with
     * @return true only if otherObject is a KeyedMessage {@link create(Object[]) derived}from this instance (or visa
     *         versa) and (thus both are) {@link #resolve() resolved}.
     * @see #create(Object[])
     * @see #resolve()
     */
    public boolean equals(Object otherObject)
    {
        if (otherObject != null && otherObject instanceof KeyedMessage)
        {
            resolve();
            return (resolved && key.equals(((KeyedMessage) otherObject).getKey()));
        }
        return false;
    }
}