package org.apache.jetspeed.serializer;

import org.apache.jetspeed.components.test.AbstractFilteredSpringTestCase;

public class TestSerializer extends AbstractFilteredSpringTestCase 
{        
    public void testImport() throws Exception
    {
    	JetspeedSerializer serializer = (JetspeedSerializer)scm.getComponent("org.apache.jetspeed.serializer.JetspeedSerializer");
    	assertNotNull(serializer);
    	//serializer.importData("j2-seed.xml");
    }
    
    @Override
    protected String getBeanDefinitionFilterCategoryKey()
    {
        return "serializer";
    }
   	
}
