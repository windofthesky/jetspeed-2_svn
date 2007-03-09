/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.testhelpers;

import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;
import org.springmodules.orm.ojb.PersistenceBrokerTransactionManager;
import org.springmodules.orm.ojb.support.LocalOjbConfigurer;

public class OJBHelper extends DatasourceHelper
{

    public static final String DATASOURCE_BEAN = "JetspeedDS";

    private GenericApplicationContext appCtx;

    private DefaultListableBeanFactory bf;

    public OJBHelper(Map context)
    {
        super(context);
    }

    public void setUp() throws Exception
    {
        super.setUp();
        bf = new DefaultListableBeanFactory();
        bf.registerSingleton(DATASOURCE_BEAN, datasource);
        LocalOjbConfigurer ojbConfigurer = new LocalOjbConfigurer();
        ojbConfigurer.setBeanFactory(bf);
        addBeanFactory(bf);
        appCtx = new GenericApplicationContext(bf);
        bf.preInstantiateSingletons();
        getContext().put(APP_CONTEXT, appCtx);
    }

    public void tearDown() throws Exception
    {
        bf.destroySingletons();
        super.tearDown();
    }

    /**
     * Surrounds the <code>object</code> with <code>TransactionProxyFactoryBean</code> that implements all
     * interfaces specified in <code>interfacesToProxyAs</code>
     * 
     * @param object
     *            object to wrap with a TX Proxy
     * @param interfacesToProxyAs
     *            interfeaces to proxy as
     * @return Tx Wrapped version of the priginal object
     * @throws Exception
     */
    public Object getTxProxiedObject(Object object, String[] interfacesToProxyAs) throws Exception
    {
        Class[] ifaces = new Class[interfacesToProxyAs.length];
        for(int i = 0; i < interfacesToProxyAs.length; i++) {
                ifaces[i] = Class.forName(interfacesToProxyAs[i]);
        }

        TransactionProxyFactoryBean txfb = new TransactionProxyFactoryBean();
        txfb.setTransactionManager(new PersistenceBrokerTransactionManager());
        Properties txProps = new Properties();
        txProps.setProperty("*", "PROPAGATION_REQUIRED");
        txfb.setTransactionAttributes(txProps);
        txfb.setTarget(object);
        txfb.setProxyInterfaces(ifaces);
        txfb.afterPropertiesSet();
        return txfb.getObject();
    }

}
