package org.apache.jetspeed.administration;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.jetspeed.aggregator.TestWorkerMonitor;


public class TestPortalAdministrationImpl extends  TestCase

{

    
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestWorkerMonitor.class.getName()});
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        
        
        
    }
    
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPortalAdministrationImpl.class);
    }

    private static final int JOB_COUNT = 2;
    
    public void testPasswordGen() throws Exception
    {
        PortalAdministrationImpl pai = new PortalAdministrationImpl(null,null,null,null,null,null,null,null);
        String newPassword = pai.generatePassword();
        assertNotNull("new password was NULL!!!",newPassword);
        assertTrue("password is not long enough",(newPassword.length() > 4) );
        
    }
    
    public void xtestSendEmail() throws Exception {
        PortalAdministrationImpl pai = new PortalAdministrationImpl(null,null,null,null,null,null,null,null);
        pai.sendEmail("chris@bluesunrise.com","this is a unittest","chris@bluesunrise.com","this is the content of the message");
        
    }
    
    // this needs too much init to test easily right now
    public void xtestRegUser() throws Exception
    {
        PortalAdministrationImpl pai = new PortalAdministrationImpl(null,null,null,null,null,null,null,null);
        String user = "user"+(Math.abs(new Date().getTime()));
        String password = "password";
        List emptyList = new ArrayList();
        Map emptyMap = new HashMap();
        Map userAttributes = new HashMap();
        String emailTemplate = "";
        pai.registerUser(user, 
                password, 
                emptyList, 
                emptyList, 
               userAttributes,              // note use of only PLT.D  values here.
               emptyMap, 
               emailTemplate);
        
    }
    

}
