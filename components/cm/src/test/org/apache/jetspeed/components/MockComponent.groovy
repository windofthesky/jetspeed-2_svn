import org.apache.jetspeed.components.MockComponent

v1 = parameterReader.getValue(0, Integer).intValue() 
v2 = parameterReader.getValue(1, String)

return new MockComponent(v1, v2)