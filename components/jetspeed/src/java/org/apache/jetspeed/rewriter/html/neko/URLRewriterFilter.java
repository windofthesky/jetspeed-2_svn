/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.rewriter.html.neko;

import org.apache.jetspeed.rewriter.Rewriter;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.filters.DefaultFilter;

/**
 * <p>
 * URLRewriterFilter
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class URLRewriterFilter extends DefaultFilter
{
    
    private Rewriter rewriter; 
    
    
    /**
     *  
     */
    public URLRewriterFilter(Rewriter rewriter )
    {
        super();
        this.rewriter = rewriter;
    }
        

    /**
     * <p>
     * startElement
     * </p>
     * 
     * @see org.apache.xerces.xni.XMLDocumentHandler#startElement(org.apache.xerces.xni.QName,
     *      org.apache.xerces.xni.XMLAttributes,
     *      org.apache.xerces.xni.Augmentations)
     * @param element
     * @param attrs
     * @param augs
     * @throws org.apache.xerces.xni.XNIException
     */
    public void startElement( QName element, XMLAttributes attrs, Augmentations augs ) throws XNIException
    {
        if (false == rewriter.enterSimpleTagEvent(element.rawname, new XMLAttributesWrapper(attrs)))
        {
            doRewrite(element, attrs);
            String appended = rewriter.exitSimpleTagEvent(element.rawname, new XMLAttributesWrapper(attrs));
            if (null != appended)
            {
              //TODO: implement this!
            }
        }
        
        super.startElement(element, attrs, augs);
    }

    /**
     * <p>
     * doRewrite
     * </p>
     *
     * @param element
     * @param attrs
     */
    protected void doRewrite( QName element, XMLAttributes attrs )
    {
        if (element.rawname.equals("A"))
        {            
            rewriteAttribute("href", attrs);
        }
        else if (element.rawname.equals("FORM"))
        {            
            rewriteAttribute("action", attrs);
        }
    }

    protected void rewriteAttribute( String attrName, XMLAttributes attributes )
    {

        String uri = attributes.getValue(attrName);
        
        
        if (uri != null)
        {
               // attributes.setValue(attributes.getIndex(attrName), urlGenerator.createUrl(uri));
         
        }

    }
    /**
     * <p>
     * emptyElement
     * </p>
     *
     * @see org.apache.xerces.xni.XMLDocumentHandler#emptyElement(org.apache.xerces.xni.QName, org.apache.xerces.xni.XMLAttributes, org.apache.xerces.xni.Augmentations)
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws org.apache.xerces.xni.XNIException
     */
    public void emptyElement( QName element, XMLAttributes attrs, Augmentations arg2 ) throws XNIException
    {
        doRewrite(element, attrs);
        super.emptyElement(element, attrs, arg2);
    }
    /**
     * <p>
     * comment
     * </p>
     *
     * @see org.apache.xerces.xni.XMLDocumentHandler#comment(org.apache.xerces.xni.XMLString, org.apache.xerces.xni.Augmentations)
     * @param comment
     * @param augs
     * @throws org.apache.xerces.xni.XNIException
     */
    public void comment( XMLString comment, Augmentations augs ) throws XNIException
    {
        if (!rewriter.shouldRemoveComments())
        {
            super.comment(comment, augs);                  
        }
        
    }
    /**
     * <p>
     * endElement
     * </p>
     *
     * @see org.apache.xerces.xni.XMLDocumentHandler#endElement(org.apache.xerces.xni.QName, org.apache.xerces.xni.Augmentations)
     * @param arg0
     * @param arg1
     * @throws org.apache.xerces.xni.XNIException
     */
    public void endElement( QName element, Augmentations augs ) throws XNIException
    {
        String elementName = element.rawname;
        
        
        super.endElement(element, augs);
    }
    /**
     * <p>
     * characters
     * </p>
     *
     * @see org.apache.xerces.xni.XMLDocumentHandler#characters(org.apache.xerces.xni.XMLString, org.apache.xerces.xni.Augmentations)
     * @param arg0
     * @param arg1
     * @throws org.apache.xerces.xni.XNIException
     */
    public void characters( XMLString text, Augmentations arg1 ) throws XNIException
    {        
        if (!(text.ch[0] == '>') && ! rewriter.enterText(text.ch, text.offset))
        {                            
            super.characters(text, arg1);
        }         
    }
}