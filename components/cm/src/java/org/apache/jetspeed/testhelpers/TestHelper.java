package org.apache.jetspeed.testhelpers;

import java.util.Map;

public interface TestHelper
{
    public void setUp() throws Exception;
    
    public void tearDown() throws Exception;
    
    public Map getContext();
    
 }
