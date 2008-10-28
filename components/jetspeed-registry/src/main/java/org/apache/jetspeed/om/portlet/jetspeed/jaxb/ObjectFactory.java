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

import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * org.apache.jetspeed.om.portlet.jetspeed.jaxb package. <p>An ObjectFactory allows you to programatically construct
 * new instances of the Java representation for XML content. The Java representation of XML content can consist of
 * schema derived interfaces and classes representing the binding of schema type definitions, element declarations and
 * model groups. Factory methods for each of these are provided in this class.
 */
@XmlRegistry
public class ObjectFactory
{
    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
     * org.apache.jetspeed.om.portlet.jetspeed.jaxb
     */
    public ObjectFactory()
    {
    }

    public Subject createSubject()
    {
        return new Subject();
    }

    public Metadata createMetadata()
    {
        return new Metadata();
    }

    public CustomWindowState createCustomWindowState()
    {
        return new CustomWindowState();
    }

    public Service createService()
    {
        return new Service();
    }

    public Services createServices()
    {
        return new Services();
    }

    public Title createTitle()
    {
        return new Title();
    }

    public Portlet createPortlet()
    {
        return new Portlet();
    }

    public CustomPortletMode createCustomPortletMode()
    {
        return new CustomPortletMode();
    }

    public UserAttributeRef createUserAttributeRef()
    {
        return new UserAttributeRef();
    }

    public PortletApp createPortletApp()
    {
        return new PortletApp();
    }

    @XmlElementDecl(namespace = "http://www.purl.org/dc", name = "contributor")
    public Contributor createContributor(String value)
    {
        return new Contributor(value);
    }

    @XmlElementDecl(namespace = "http://www.purl.org/dc", name = "description")
    public Description createDescription(String value)
    {
        return new Description(value);
    }

    @XmlElementDecl(namespace = "http://www.purl.org/dc", name = "creator")
    public Creator createCreator(String value)
    {
        return new Creator(value);
    }

    @XmlElementDecl(namespace = "http://www.purl.org/dc", name = "identifer")
    public Identifer createIdentifer(String value)
    {
        return new Identifer(value);
    }

    @XmlElementDecl(namespace = "http://www.purl.org/dc", name = "right")
    public Right createRight(String value)
    {
        return new Right(value);
    }

    @XmlElementDecl(namespace = "http://www.purl.org/dc", name = "type")
    public Type createType(String value)
    {
        return new Type(value);
    }

    @XmlElementDecl(namespace = "http://www.purl.org/dc", name = "source")
    public Source createSource(String value)
    {
        return new Source(value);
    }

    @XmlElementDecl(namespace = "http://www.purl.org/dc", name = "format")
    public Format createFormat(String value)
    {
        return new Format(value);
    }

    @XmlElementDecl(namespace = "http://www.purl.org/dc", name = "language")
    public Language createLanguage(String value)
    {
        return new Language(value);
    }

    @XmlElementDecl(namespace = "http://www.purl.org/dc", name = "coverage")
    public Coverage createCoverage(String value)
    {
        return new Coverage(value);
    }

    @XmlElementDecl(namespace = "http://www.purl.org/dc", name = "publisher")
    public Publisher createPublisher(String value)
    {
        return new Publisher(value);
    }

    @XmlElementDecl(namespace = "http://www.purl.org/dc", name = "relation")
    public Relation createRelation(String value)
    {
        return new Relation(value);
    }
}
