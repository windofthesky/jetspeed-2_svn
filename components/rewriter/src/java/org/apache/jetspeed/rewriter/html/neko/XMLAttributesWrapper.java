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

import org.apache.jetspeed.rewriter.MutableAttributes;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;

/**
 * <p>
 * XMLAttributesWrapper
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class XMLAttributesWrapper implements MutableAttributes
{
    protected XMLAttributes attrs;
    
    /**
     * 
     */
    public XMLAttributesWrapper(XMLAttributes attrs)
    {
        super();
        this.attrs = attrs;
    }

    /**
     * <p>
     * addAttribute
     * </p>
     *
     * @param arg0
     * @param arg1
     * @param arg2
     * @return
     */
    public int addAttribute( QName arg0, String arg1, String arg2 )
    {
        return attrs.addAttribute(arg0, arg1, arg2);
    }
    /**
     * <p>
     * equals
     * </p>
     *
     * @see java.lang.Object#equals(java.lang.Object)
     * @param obj
     * @return
     */
    public boolean equals( Object obj )
    {
        return attrs.equals(obj);
    }
    /**
     * <p>
     * getAugmentations
     * </p>
     *
     * @param arg0
     * @return
     */
    public Augmentations getAugmentations( int arg0 )
    {
        return attrs.getAugmentations(arg0);
    }
    /**
     * <p>
     * getAugmentations
     * </p>
     *
     * @param arg0
     * @return
     */
    public Augmentations getAugmentations( String arg0 )
    {
        return attrs.getAugmentations(arg0);
    }
    /**
     * <p>
     * getAugmentations
     * </p>
     *
     * @param arg0
     * @param arg1
     * @return
     */
    public Augmentations getAugmentations( String arg0, String arg1 )
    {
        return attrs.getAugmentations(arg0, arg1);
    }
    /**
     * <p>
     * getIndex
     * </p>
     *
     * @param arg0
     * @return
     */
    public int getIndex( String arg0 )
    {
        return attrs.getIndex(arg0);
    }
    /**
     * <p>
     * getIndex
     * </p>
     *
     * @param arg0
     * @param arg1
     * @return
     */
    public int getIndex( String arg0, String arg1 )
    {
        return attrs.getIndex(arg0, arg1);
    }
    /**
     * <p>
     * getLength
     * </p>
     *
     * @return
     */
    public int getLength()
    {
        return attrs.getLength();
    }
    /**
     * <p>
     * getLocalName
     * </p>
     *
     * @param arg0
     * @return
     */
    public String getLocalName( int arg0 )
    {
        return attrs.getLocalName(arg0);
    }
    /**
     * <p>
     * getName
     * </p>
     *
     * @param arg0
     * @param arg1
     */
    public void getName( int arg0, QName arg1 )
    {
        attrs.getName(arg0, arg1);
    }
    /**
     * <p>
     * getNonNormalizedValue
     * </p>
     *
     * @param arg0
     * @return
     */
    public String getNonNormalizedValue( int arg0 )
    {
        return attrs.getNonNormalizedValue(arg0);
    }
    /**
     * <p>
     * getPrefix
     * </p>
     *
     * @param arg0
     * @return
     */
    public String getPrefix( int arg0 )
    {
        return attrs.getPrefix(arg0);
    }
    /**
     * <p>
     * getQName
     * </p>
     *
     * @param arg0
     * @return
     */
    public String getQName( int arg0 )
    {
        return attrs.getQName(arg0);
    }
    /**
     * <p>
     * getType
     * </p>
     *
     * @param arg0
     * @return
     */
    public String getType( int arg0 )
    {
        return attrs.getType(arg0);
    }
    /**
     * <p>
     * getType
     * </p>
     *
     * @param arg0
     * @return
     */
    public String getType( String arg0 )
    {
        return attrs.getType(arg0);
    }
    /**
     * <p>
     * getType
     * </p>
     *
     * @param arg0
     * @param arg1
     * @return
     */
    public String getType( String arg0, String arg1 )
    {
        return attrs.getType(arg0, arg1);
    }
    /**
     * <p>
     * getURI
     * </p>
     *
     * @param arg0
     * @return
     */
    public String getURI( int arg0 )
    {
        return attrs.getURI(arg0);
    }
    /**
     * <p>
     * getValue
     * </p>
     *
     * @param arg0
     * @return
     */
    public String getValue( int arg0 )
    {
        return attrs.getValue(arg0);
    }
    /**
     * <p>
     * getValue
     * </p>
     *
     * @param arg0
     * @return
     */
    public String getValue( String arg0 )
    {
        return attrs.getValue(arg0);
    }
    /**
     * <p>
     * getValue
     * </p>
     *
     * @param arg0
     * @param arg1
     * @return
     */
    public String getValue( String arg0, String arg1 )
    {
        return attrs.getValue(arg0, arg1);
    }
    /**
     * <p>
     * hashCode
     * </p>
     *
     * @see java.lang.Object#hashCode()
     * @return
     */
    public int hashCode()
    {
        return attrs.hashCode();
    }
    /**
     * <p>
     * isSpecified
     * </p>
     *
     * @param arg0
     * @return
     */
    public boolean isSpecified( int arg0 )
    {
        return attrs.isSpecified(arg0);
    }
    /**
     * <p>
     * removeAllAttributes
     * </p>
     *
     * 
     */
    public void removeAllAttributes()
    {
        attrs.removeAllAttributes();
    }
    /**
     * <p>
     * removeAttributeAt
     * </p>
     *
     * @param arg0
     */
    public void removeAttributeAt( int arg0 )
    {
        attrs.removeAttributeAt(arg0);
    }
    /**
     * <p>
     * setAugmentations
     * </p>
     *
     * @param arg0
     * @param arg1
     */
    public void setAugmentations( int arg0, Augmentations arg1 )
    {
        attrs.setAugmentations(arg0, arg1);
    }
    /**
     * <p>
     * setName
     * </p>
     *
     * @param arg0
     * @param arg1
     */
    public void setName( int arg0, QName arg1 )
    {
        attrs.setName(arg0, arg1);
    }
    /**
     * <p>
     * setNonNormalizedValue
     * </p>
     *
     * @param arg0
     * @param arg1
     */
    public void setNonNormalizedValue( int arg0, String arg1 )
    {
        attrs.setNonNormalizedValue(arg0, arg1);
    }
    /**
     * <p>
     * setSpecified
     * </p>
     *
     * @param arg0
     * @param arg1
     */
    public void setSpecified( int arg0, boolean arg1 )
    {
        attrs.setSpecified(arg0, arg1);
    }
    /**
     * <p>
     * setType
     * </p>
     *
     * @param arg0
     * @param arg1
     */
    public void setType( int arg0, String arg1 )
    {
        attrs.setType(arg0, arg1);
    }
    /**
     * <p>
     * setValue
     * </p>
     *
     * @param arg0
     * @param arg1
     */
    public void setValue( int arg0, String arg1 )
    {
        attrs.setValue(arg0, arg1);
    }
    /**
     * <p>
     * toString
     * </p>
     *
     * @see java.lang.Object#toString()
     * @return
     */
    public String toString()
    {
        return attrs.toString();
    }
    /**
     * <p>
     * addAttribute
     * </p>
     *
     * @see org.apache.jetspeed.rewriter.MutableAttributes#addAttribute(java.lang.String, java.lang.Object)
     * @param name
     * @param value
     */
    public void addAttribute( String name, Object value )
    {
        addAttribute(new QName(null, name.toUpperCase(), name.toUpperCase(), null),"CDATA", value.toString());

    }
}
