package org.apache.jetspeed.testhelpers;

import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.orm.ojb.PersistenceBrokerTransactionManager;
import org.springframework.orm.ojb.support.LocalOjbConfigurer;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

public class OJBHelper extends DatasourceHelper
{

    public static final String DATASOURCE_BEAN = "JetspeedDS";
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
        bf.preInstantiateSingletons();
    }

    public void tearDown() throws Exception
    {
        bf.destroySingletons();
        super.tearDown();
    }
    
    /**
     * Surrounds the <code>object</code> with <code>TransactionProxyFactoryBean</code>
     * that implements all interfaces specified in <code>interfacesToProxyAs</code>
     * @param object object to wrap with a TX Proxy
     * @param interfacesToProxyAs interfeaces to proxy as
     * @return Tx Wrapped version of the priginal object
     * @throws Exception
     */
    public Object getTxProxiedObject(Object object, String[] interfacesToProxyAs) throws Exception
    {
        TransactionProxyFactoryBean txfb = new TransactionProxyFactoryBean();
        txfb.setTransactionManager(new PersistenceBrokerTransactionManager());
        Properties txProps = new Properties();
        txProps.setProperty("*","PROPAGATION_REQUIRED");
        txfb.setTransactionAttributes(txProps);
        txfb.setTarget(object);
        txfb.setProxyInterfaces(interfacesToProxyAs);
        txfb.afterPropertiesSet();
        return txfb.getObject();
    }

   

}
