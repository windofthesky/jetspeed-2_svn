/**
 * Created on Feb 18, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.components;


public class SmartImpl implements Smart
{
	private String string;
	private int integer;
	
	public SmartImpl(String string, int integer)
	{
		System.out.println("Constructing Smart");
		this.string = string;
		this.integer = integer;
		
	}
    
	public void test()
	{
		System.out.println("SmartImpl.test() was called.  String is: "+string+" Integer is: "+integer);
	}
}