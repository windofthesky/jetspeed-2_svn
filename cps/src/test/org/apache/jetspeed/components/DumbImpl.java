/**
 * Created on Feb 18, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.components;


public class DumbImpl implements Dumb
{
    public DumbImpl()
    {
        System.out.println("Constructing Dumb");
    }
    
    public void test()
    {
        System.out.println("DumbImpl.test() was called");
    }
}