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

import org.apache.commons.lang.ArrayUtils;
import org.apache.jetspeed.rewriter.MutableAttributes;
import org.apache.jetspeed.rewriter.Rewriter;
import org.apache.jetspeed.rewriter.html.SwingAttributes;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.filters.ElementRemover;

/**
 * <p>
 * CallbackElementRemover
 * </p>
 * <p>
 *  Extended version of the NekoHTML ElementRemover which provides
 *  tag stripping/removal based on Rewriter settings.
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class CallbackElementRemover extends ElementRemover
{

    private Rewriter rewriter;

    /**
     * Construct with reference to the rewriter context to consult for rewriting advice
     */
    public CallbackElementRemover( Rewriter rewriter )
    {
        super();
        
        this.rewriter = rewriter;
    }
    
    
    // Base Class Protocol
    
    /**
     * <p>
     * comment
     * </p>
     * 
     * @see org.apache.xerces.xni.XMLDocumentHandler#comment(org.apache.xerces.xni.XMLString text, org.apache.xerces.xni.Augmentations augs)
     * @param text
     * @param augs
     * @throws org.apache.xerces.xni.XNIException
     */
    public void comment(XMLString text,Augmentations augs) throws XNIException
    {
        if (rewriter.shouldRemoveComments())
            return;
        super.comment(text,augs);
    }

    /**
     * <p>
     * emptyElement
     * </p>
     * 
     * @see org.apache.xerces.xni.XMLDocumentHandler#emptyElement(org.apache.xerces.xni.QName,
     *      org.apache.xerces.xni.XMLAttributes,
     *      org.apache.xerces.xni.Augmentations)
     * @param element
     * @param arg1
     * @param arg2
     * @throws org.apache.xerces.xni.XNIException
     */
    public void emptyElement( QName element, XMLAttributes attrs, Augmentations arg2 ) throws XNIException
    {
        processTag(element,attrs) ;
        super.emptyElement(element, attrs, arg2);
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
     * @param arg1
     * @param arg2
     * @throws org.apache.xerces.xni.XNIException
     */
    public void startElement( QName element, XMLAttributes attrs, Augmentations arg2 ) throws XNIException
    {
        processTag(element,attrs);
        super.startElement(element, attrs, arg2);
    }
    
    
    // Support Methods

    /**
     * <p>
     * processTag
     * </p>
     * 
     * @param tag
     */
    protected void processTag(QName element, XMLAttributes attrs)
    {
        String tag = element.rawname.toLowerCase();
        if (fRemovedElements.contains(tag))
        {
            // alread removed
            return ;
        }
        else if (rewriter.shouldStripTag(tag))
        {
            // first time for this tag...
            // strip - remove tag and any text associated with it
            removeElement(tag);
            return ;
        }
        else if (rewriter.shouldRemoveTag(tag))
        {
            // BOZO - block intentially left EMPTY
            
            // first time for this tag...
            // remove - no directive necessary, the default behavior of ElementRemover is to drop tags that it does not know about (but the assocated text will remain)
            return ;
        }

        // OTHERWISE - explicitly accept (keep tag and associated text)
        // NOTE: even if fAcceptedElements contains the tag already, we need to reset the attribute names for this invocation context
        rewriter.enterConvertTagEvent(tag,new XMLAttributesWrapper(attrs));
        String[] attrNames = getAttributeNames(attrs);
        acceptElement(tag,getAttributeNames(attrs));
    }
    protected String[] getAttributeNames(XMLAttributes attrs)
    {
        int length = attrs != null ? attrs.getLength() : 0 ;
        String[] names = length > 0 ? new String[ length ] : null ;
        
        for( int i = 0, limit = length;i<limit;i++)
        {
            names[i] = attrs.getQName(i) ;
        }
        return names ;
    }
}
