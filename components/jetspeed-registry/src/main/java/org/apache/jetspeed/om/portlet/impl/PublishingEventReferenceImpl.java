package org.apache.jetspeed.om.portlet.impl;

import javax.xml.namespace.QName;


public class PublishingEventReferenceImpl extends EventDefinitionReferenceImpl
{
    private static final long serialVersionUID = 1L;

    public PublishingEventReferenceImpl()
    {
        super();
    }
    
    public PublishingEventReferenceImpl(QName qname)
    {
        super(qname);
    }

    public PublishingEventReferenceImpl(String qname)
    {
        super(new QName(qname));
    }

}
