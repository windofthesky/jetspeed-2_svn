import org.apache.jetspeed.components.BaseMockComponent

v1 = parameterReader.getValue(0, Integer).intValue() 
v2 = parameterReader.getValue(1, String)

return new BaseMockComponent(v1, v2)