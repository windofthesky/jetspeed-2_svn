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
package org.apache.jetspeed.om.registry.base;

import org.apache.jetspeed.om.registry.ToolDescriptor;

/**
 * Bean-like implementation of the ToolDescriptor interface
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @version $Id$
 */
public class BaseToolDescriptor implements ToolDescriptor, java.io.Serializable
{
    private String name = null;
    private String scope = null;
    private String classname = null;

    /**
    * Implements the equals operation so that 2 elements are equal if
    * all their member values are equal.
    */
    public boolean equals(Object object)
    {
        if (object==null)
        {
            return false;
        }

        BaseToolDescriptor obj = (BaseToolDescriptor)object;

        if (name!=null)
        {
            if (!name.equals(obj.getName()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getName()!=null)
            {
                return false;
            }
        }

        if (scope!=null)
        {
            if(!scope.equals(obj.getScope()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getScope()!=null)
            {
                return false;
            }
        }

        if (classname!=null)
        {
            if(!classname.equals(obj.getClassname()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getClassname()!=null)
            {
                return false;
            }
        }

        return true;
    }

    /** @return the name of the tool */
    public String getName()
    {
        return this.name;
    }

    /** Sets the name for this tool
     * @param title the new name of the tool
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /** @return the scope of this tool */
    public String getScope()
    {
        return this.scope;
    }

    /** Sets the scope of this tool.
     * The currently recognized scope are "request", "session", "persistent", "global"
     * @param scope the new scope of this tool
     */
    public void setScope( String scope )
    {
        this.scope = scope;
    }

    /** @return the clasname of this tool */
    public String getClassname()
    {
        return this.classname;
    }

    /** Sets the classname of this tool
     * @param classname the new classname of this tool
     */
    public void setClassname( String classname )
    {
        this.classname = classname;
    }
}