package org.apache.jetspeed.serializer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.serializer.objects.JSSnapshot;
import org.apache.jetspeed.test.JetspeedTestCase;

public class TestSnapshot extends JetspeedTestCase
{
    protected void setUp() throws Exception
    {
        super.setUp();
    }
    
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    public void testSnapshot() throws Exception
    {
    	List<JetspeedComponentSerializer> serializers = new LinkedList<JetspeedComponentSerializer>();
    	Map<String,String> settings = new HashMap<String,String>();
    	JetspeedSerializerImpl serializer = new JetspeedSerializerImpl(serializers, settings);    
    	JSSnapshot snapshot = serializer.readSnapshot("j2-seed.xml");
    	assertNotNull(snapshot);
    }
}
