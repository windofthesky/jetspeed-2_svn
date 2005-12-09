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
     *  
     */
    public CallbackElementRemover( Rewriter rewriter )
    {
        super();
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
    public void emptyElement( QName element, XMLAttributes arg1, Augmentations arg2 ) throws XNIException
    {
        processTag(element.rawname);
        super.emptyElement(element, arg1, arg2);
    }

    /**
     * <p>
     * processTag
     * </p>
     * 
     * @param tag
     */
    protected void processTag( String tag )
    {
        if (!fAcceptedElements.contains(tag.toLowerCase()) && !fRemovedElements.contains(tag.toLowerCase()))
        {
            if (!rewriter.shouldRemoveTag(tag) && !rewriter.shouldStripTag(tag))
            {
                acceptElement(tag, null);
            }
            else if (rewriter.shouldStripTag(tag))
            {
                removeElement(tag);
            }
        }
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
    public void startElement( QName element, XMLAttributes arg1, Augmentations arg2 ) throws XNIException
    {
        processTag(element.rawname);
        super.startElement(element, arg1, arg2);
    }
}