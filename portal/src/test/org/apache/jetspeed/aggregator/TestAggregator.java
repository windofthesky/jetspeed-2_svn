package org.apache.jetspeed.aggregator;


import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.services.JetspeedServices;
import org.apache.jetspeed.test.JetspeedTest;

import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextFactory;
import org.apache.jetspeed.services.profiler.Profiler;
import org.apache.jetspeed.om.profile.Profile;

/**
 * <P>Test the aggregation service</P>
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 * 
 */
public class TestAggregator extends JetspeedTest
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestAggregator(String name) 
    {
        super( name );
    }
    
    public static Test suite() {
         // All methods starting with "test" will be executed in the test suite.
         return new TestSuite( TestAggregator.class );
     }
         
    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[]) 
    {
        junit.awtui.TestRunner.main( new String[] { TestAggregator.class.getName() } );
    }    
    
    /**
     * Simple test that verifies ...
     *
     * @throws Exception
     */
    public void testAggregator() throws Exception
    {
        Aggregator aggregator = (Aggregator)JetspeedServices
            .getInstance().getService(Aggregator.SERVICE_NAME);
    
        RequestContext request = RequestContextFactory.getInstance(null,null, null);

        Profile profile = Profiler.getProfile(request);
        request.setProfile(profile);
   
        aggregator.build(request);
            
    }
}
