/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights 
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Pluto", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 *
 * ====================================================================
 *
 * This source code implements specifications defined by the Java
 * Community Process. In order to remain compliant with the specification
 * DO NOT add / change / or delete method signatures!
 */

package javax.portlet;


    /**
     * The <CODE>PortletMode</CODE> class represents
     * the possible modes that a portlet can assume.
     * <P>
     * A portlet mode indicates the function a portlet is performing.
     * Normally, portlets perform different tasks and create different
     * content depending on the function they are currently performing.
     * When invoking a portlet, the portlet container provides the
     * current portlet mode to the portlet.
     * <p>
     * Portlets can programmatically change their portlet
     * mode when processing an action request.
     * <P>
     * This class defines the default portlet modes <code>EDIT, HELP, VIEW</code>.
     * Additional portlet modes may be defined by calling the constructor
     * of this class. If a portal/portlet-container does not support a 
     * custom portlet mode defined in the portlet application deployment descriptor, 
     * the custom portlet mode will be ignored by the portal/portlet container.
     */
    public class PortletMode
    {
        /**
         * The expected functionality for a portlet in <code>VIEW</code> portlet mode 
         * is to generate markup reflecting the current state of the portlet. 
         * For example, the <code>VIEW</code> portlet mode of a portlet may 
         * include one or more screens that the user can navigate and interact 
         * with, or it may consist of static content that does not require any 
         * user interaction.
         * <P>
         * This mode must be supported by the portlet.
         * <p>
         * The string value for this mode is <code>"view"</code>.
         */
        public final static PortletMode VIEW = new PortletMode("view");
        /**
         * Within the <code>EDIT</code> portlet mode, a portlet should provide 
         * content and logic that lets a user customize the behavior of the portlet. 
         * The EDIT portlet mode may include one or more screens among which 
         * users can navigate to enter their customization data.
         * <p>
         * Typically, portlets in <code>EDIT</code> portlet mode will 
         * set or update portlet preferences.
         * <P>
         * This mode is optional.
         * <p>
         * The string value for this mode is <code>"edit"</code>.
         */
        public final static PortletMode EDIT = new PortletMode("edit");
        /**
         * When in <code>HELP</code> portlet mode, a portlet should provide help 
         * information about the portlet. This help information could be 
         * a simple help screen explaining the entire portlet in
         * coherent text or it could be context-sensitive help.
         * <P>
         * This mode is optional.
         * <p>
         * The string value for this mode is <code>"help"</code>.
         */
        public final static PortletMode HELP = new PortletMode("help");
        
        private String name;
        
        /**
         * Creates a new portlet mode with the given name.
         * <p>
         * Upper case letters in the name are converted to
         * lower case letters.
         *
         * @param name The name of the portlet mode
         */
        public PortletMode(String name)
        {
            if (name == null)
            {
                throw new IllegalArgumentException("PortletMode name can not be NULL");
            }
            this.name = name.toLowerCase();
        }
        /**
         * Returns a String representation of this portlet mode.
         * Portlet mode names are always lower case names.
         *
         * @return  String representation of this portlet mode
         */
        public String toString()
        {
            return this.name;
        }
        /**
         * Returns the hash code value for this portlet mode.
         * The hash code is constructed by producing the
         * hash value of the String value of this mode.
         *
         * @return  hash code value for this portlet mode
         */
        public int hashCode()
        {
            return this.name.hashCode();
        }
        /**
         * Compares the specified object with this portlet mode
         * for equality. Returns <code>true</code> if the
         * Strings <code>equals</code> method for the String
         * representing the two portlet modes returns <code>true</code>.
         * 
         * @param   the portlet mode to compare this portlet mode with
         * 
         * @return  true, if the specified object is equal with this portlet mode
         */
        public boolean equals(Object object)
        {
            if (object instanceof PortletMode)
                return this.name.equals(((PortletMode) object).name);
            else
                return false;
        }
}

