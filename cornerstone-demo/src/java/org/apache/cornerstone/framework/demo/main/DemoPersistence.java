/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cornerstone.framework.demo.main;

import java.util.List;

import org.apache.cornerstone.framework.api.factory.CreationException;
import org.apache.cornerstone.framework.api.factory.IFactory;
import org.apache.cornerstone.framework.api.implementation.IImplementationManager;
import org.apache.cornerstone.framework.api.implementation.ImplementationException;
import org.apache.cornerstone.framework.bean.visitor.BeanPrinter;
import org.apache.cornerstone.framework.demo.bo.api.IGroup;
import org.apache.cornerstone.framework.demo.bo.api.IUser;
import org.apache.cornerstone.framework.init.Cornerstone;
import org.apache.cornerstone.framework.init.InitException;
import org.apache.log4j.PropertyConfigurator;

public class DemoPersistence
{
    public static final String REVISION = "$Revision$";

    public static void main(String[] args) throws InitException, ImplementationException, CreationException
    {
        // init log4j
        String log4jConfigFilePath = System.getProperty("log4j.configuration", "log4j.properties");
        PropertyConfigurator.configure(log4jConfigFilePath);

        Cornerstone.init();

        IImplementationManager implementationManager = (IImplementationManager) Cornerstone.getImplementation(IImplementationManager.class);
        IFactory groupFactory = (IFactory) implementationManager.createImplementation(
        	IFactory.class,
        	"cornerstone.demo.groupFactory"
        );
        IGroup group = (IGroup) groupFactory.createInstance(new Integer(100));
        System.out.println("group=" + BeanPrinter.getPrintString(group));

		IFactory userFactory = (IFactory) implementationManager.createImplementation(
			IFactory.class,
			"cornerstone.demo.userFactory"
		);
		IUser user = (IUser) userFactory.createInstance(new Integer(101));
		System.out.println("user=" + BeanPrinter.getPrintString(user));

		IFactory userListFactory = (IFactory) implementationManager.createImplementation(
			IFactory.class,
			"cornerstone.demo.userListFactory"
		);
		List userList = (List) userListFactory.createInstance();
		System.out.println("userList=" + BeanPrinter.getPrintString(userList));

        // TODO: auto-population of associations not implemented yet
//        IContext context = new BaseContext();
//        context.setValue(IPersistenceFactory.CTX_QUERY_NAME, "byGroup");
//        context.setValue("groupId", new Integer(100));
//        List group100UserList = (List) userListFactory.createInstance(context);
//        System.out.println("group100UserList=" + BeanPrinter.getPrintString(userList));
    }
}