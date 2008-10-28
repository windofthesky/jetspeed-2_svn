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
package org.apache.jetspeed.om.portlet.jetspeed.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>Java class for anonymous complex type. <p>The following schema fragment specifies the expected content contained
 * within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;portlet-name&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;security-constraint-ref&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;group ref=&quot;{http://portals.apache.org/jetspeed}metadataGroup&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name=&quot;id&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}ID&quot; /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "portletName", "securityConstraintRef", "metadataGroup" })
@XmlRootElement(name = "portlet")
public class Portlet
{
    @XmlElement(name = "portlet-name", required = true)
    protected String portletName;
    @XmlElement(name = "security-constraint-ref")
    protected String securityConstraintRef;
    @XmlElementRefs( {
                      @XmlElementRef(name = "coverage", namespace = "http://www.purl.org/dc", type = Coverage.class),
                      @XmlElementRef(name = "description", namespace = "http://www.purl.org/dc", type = Description.class),
                      @XmlElementRef(name = "metadata", namespace = "http://portals.apache.org/jetspeed", type = Metadata.class),
                      @XmlElementRef(name = "contributor", namespace = "http://www.purl.org/dc", type = Contributor.class),
                      @XmlElementRef(name = "title", namespace = "http://www.purl.org/dc", type = Title.class),
                      @XmlElementRef(name = "source", namespace = "http://www.purl.org/dc", type = Source.class),
                      @XmlElementRef(name = "right", namespace = "http://www.purl.org/dc", type = Right.class),
                      @XmlElementRef(name = "language", namespace = "http://www.purl.org/dc", type = Language.class),
                      @XmlElementRef(name = "identifer", namespace = "http://www.purl.org/dc", type = Identifer.class),
                      @XmlElementRef(name = "subject", namespace = "http://www.purl.org/dc", type = Subject.class),
                      @XmlElementRef(name = "publisher", namespace = "http://www.purl.org/dc", type = Publisher.class),
                      @XmlElementRef(name = "relation", namespace = "http://www.purl.org/dc", type = Relation.class),
                      @XmlElementRef(name = "type", namespace = "http://www.purl.org/dc", type = Type.class),
                      @XmlElementRef(name = "format", namespace = "http://www.purl.org/dc", type = Format.class),
                      @XmlElementRef(name = "creator", namespace = "http://www.purl.org/dc", type = Creator.class) })
    protected List<MetadataType> metadataGroup;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    public String getPortletName()
    {
        return portletName;
    }

    public void setPortletName(String value)
    {
        this.portletName = value;
    }

    public String getSecurityConstraintRef()
    {
        return securityConstraintRef;
    }

    public void setSecurityConstraintRef(String value)
    {
        this.securityConstraintRef = value;
    }

    /**
     * Gets the value of the metadataGroup property. <p> This accessor method returns a reference to the live list, not
     * a snapshot. 
     *
     * <p> Objects of the following type(s) are allowed in the list {@link Coverage } {@link Description }
     * {@link Metadata } {@link Contributor } {@link Title } {@link Source } {@link Right } {@link Language }
     * {@link Identifer } {@link Subject } {@link Publisher } {@link Relation } {@link Format } {@link Type }
     * {@link Creator }
     */
    public List<MetadataType> getMetadata()
    {
        if (metadataGroup == null)
        {
            metadataGroup = new ArrayList<MetadataType>();
        }
        return this.metadataGroup;
    }

    public void addMetaData(String name, String lang, String content)
    {
        PortletApp.addMetadata(getMetadata(), name, lang, content);
    }
    
    public String getId()
    {
        return id;
    }

    public void setId(String value)
    {
        this.id = value;
    }
}
