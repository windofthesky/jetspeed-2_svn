package org.apache.jetspeed.testhelpers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;

import junit.framework.TestCase;

public class TestOJBHelper extends TestCase
{
    public void testHelper() throws Exception
    {
        Map context = new HashMap();
        OJBHelper helper = new OJBHelper(context);
        helper.setUp();
        ApplicationContext appCtx = (ApplicationContext) context.get(AbstractTestHelper.APP_CONTEXT);
        assertNotNull(appCtx);
        assertNotNull(appCtx.getBean(OJBHelper.DATASOURCE_BEAN));
        helper.tearDown();
    }

}
