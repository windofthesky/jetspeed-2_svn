package org.apache.cornerstone.framework.api.implementation;

public interface IImplementationManager
{
	public static final String REVISION = "$Revision$";

	public Object createImplementation(String interfaceName) throws ImplementationException;
	public Object createImplementation(Class interfaceClass) throws ImplementationException;
	public Object createImplementation(String interfaceName, String variantName) throws ImplementationException;
	public Object createImplementation(Class interfaceClass, String variantName) throws ImplementationException;
}