package org.apache.jetspeed.security.attributes;

import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.util.test.AbstractSecurityTestcase;


public class TestSecurityAttributes extends AbstractSecurityTestcase
{
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSecurityAttributes.class);
    }
    
    protected void setUp() throws Exception
    {
        super.setUp();
    }
    
    public void testAdding() throws Exception
    {
        if (!ums.userExists("david"))
        {        
            ums.addUser("david", "secret");
        }
        User user = ums.getUser("david");        
        SecurityAttributesProvider sap = (SecurityAttributesProvider)scm.getComponent("SecurityAttributesProvider");
        assertNotNull(sap);
        // create 3 attributes
        SecurityAttributes sa = sap.createSecurityAttributes(user);
        Map<String,SecurityAttribute> attributes = sa.getAttributes();
        attributes.put("one", sa.createAttribute("one", "1000.one.1"));
        attributes.put("two", sa.createAttribute("two", "2000.two.2"));        
        attributes.put("three", sa.createAttribute("three", "3000.three.3"));        
        sap.saveAttributes(sa);
        
        // retrieve them and assert
        sa = sap.retrieveAttributes(user);
        int count = 0;
        for (String key : sa.getAttributes().keySet())
        {
            SecurityAttribute a = sa.getAttributes().get(key);
            assertTrue(a.getType().equals(SecurityAttributes.SECURITY_ATTRIBUTE));
            if (key.equals("one"))
            {
                assertTrue(a.getValue().equals("1000.one.1"));
                count++;
            }
            else if (key.equals("two"))
            {
                assertTrue(a.getValue().equals("2000.two.2"));
                count++;
            }
            else if (key.equals("three"))
            {
                assertTrue(a.getValue().equals("3000.three.3"));
                count++;
            }            
        }
        assertTrue(count == 3);
         
        // tests  2 adds and 1 remove, 1 update
        attributes = sa.getAttributes();
        attributes.put("four", sa.createAttribute("four", "4000.four.4"));
        attributes.put("five", sa.createAttribute("five", "5000.five.5"));
        attributes.put("three", sa.createAttribute("three", "MOD-3"));        
        attributes.remove("two");
        sap.saveAttributes(sa);

        // assert
        count = 0;
        sa = sap.retrieveAttributes(user);
        for (String key : sa.getAttributes().keySet())
        {
            SecurityAttribute a = sa.getAttributes().get(key);
            assertTrue(a.getType().equals("attribute"));
            if (key.equals("one"))
            {
                assertTrue(a.getValue().equals("1000.one.1"));
                count++;
            }
            else if (key.equals("two"))
            {
                assertTrue(false); // fail out, should not be here
            }
            else if (key.equals("three"))
            {
                assertTrue(a.getValue().equals("MOD-3"));
                count++;
            }
            else if (key.equals("four"))
            {
                assertTrue(a.getValue().equals("4000.four.4"));
                count++;
            }            
            else if (key.equals("five"))
            {
                assertTrue(a.getValue().equals("5000.five.5"));
                count++;
            }            
        }
        assertTrue(count == 4);
        
        // delete and assert deleted
        sap.deleteAttributes(user);        
        sa = sap.retrieveAttributes(user);
        assertTrue(sa.getAttributes().size() == 0);
        
        
    }
}
