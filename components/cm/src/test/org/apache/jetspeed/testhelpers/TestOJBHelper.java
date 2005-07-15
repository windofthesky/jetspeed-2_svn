package org.apache.jetspeed.testhelpers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import junit.framework.TestCase;

public class TestOJBHelper extends TestCase
{
    public void testHelper() throws Exception
    {
        Map context = new HashMap();
        OJBHelper helper = new OJBHelper(context);
        helper.setUp();
        ConfigurableBeanFactory beanFactory = (ConfigurableBeanFactory) context.get(AbstractTestHelper.BEAN_FACTORY);
        assertNotNull(beanFactory);
        assertNotNull(beanFactory.getBean(OJBHelper.DATASOURCE_BEAN));
        helper.tearDown();
    }

}
