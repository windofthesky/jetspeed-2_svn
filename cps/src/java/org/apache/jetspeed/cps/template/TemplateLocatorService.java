/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.cps.template;

import java.util.Iterator;

import org.apache.jetspeed.cps.CommonService;

/**
 * TemplateLocatorService
 *
 * <h3>Sample Configuration</h3> 
 *<pre>
 * <code>
 * # -------------------------------------------------------------------
 * #
 * # T E M P L A T E  L O C A T O R  S E R V I C E
 * #
 * # -------------------------------------------------------------------
 * services.template.classname = org.apache.jetspeed.cps.template.TemplateLocatorServiceImpl
 * # This parameter supports a comma separated list of directories
 * # Each directory is searched in order to find a template.
 * # This is useful for example, in defining application specific templates in a separate structure from the jetspeed core templates
 * services.template.roots = WEB-INF/templates/vm
 * services.template.locator.class = org.apache.jetspeed.cps.template.TemplateLocatorImpl
 * services.template.class = org.apache.jetspeed.cps.template.TemplateImpl
 * services.template.locator.default.type = portlet
 * services.template.default.template.name = default.vm
 * services.template.default.extension = vm
 *  </code>
 * </pre>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface TemplateLocatorService extends CommonService
{
    /** The name of this service */
    String SERVICE_NAME = "template";
        
    /**
     * Locate an template using Jetspeed template location algorithm
     *
     * @param locator The template locator
     * @return The template found, or null if not found.
     * @throws TemplateLocatorException
     */
    Template locateTemplate(TemplateLocator locator)
        throws TemplateLocatorException;

    /**
     * Factory to create template locators of the given type.
     * Known supported locator types, but not limited to: 
     *      <code>portlet</code>
     *      <code>email</code>
     * 
     * @param The type of locator to create
     * @return a general template locator of the given type
     * @throws TemplateLocatorException if factory exception or if not valid locator type
     */
    TemplateLocator createLocator(String type)
        throws TemplateLocatorException;

    /**
     * Creates a locator from a string of format (where brackets are optional]:
     *
     *   template/<templateType>/[media-type/<mediaType>]/[language/<language>]/[country/<country>]]/name/<templateName
     * 
     * @param string the string representation of a template locator 
     * @throws TemplateLocatorException
     */        
    TemplateLocator createFromString(String string)
        throws TemplateLocatorException;
        
    /** 
     * Query for a collection of templates given template locator criteria.
     *
     * @param locator The template locator criteria.
     * @return The result list of {@link Template} objects matching the locator criteria.
     */
    public Iterator query(TemplateLocator locator);
        
}
