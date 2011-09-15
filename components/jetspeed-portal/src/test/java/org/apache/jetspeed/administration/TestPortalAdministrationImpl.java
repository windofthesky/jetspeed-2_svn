/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.administration;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.jetspeed.aggregator.TestWorkerMonitor;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * TestPortalAdministrationImpl
 * 
 * @version $Id$
 */
public class TestPortalAdministrationImpl extends  TestCase
{
    private String smtpHost;
    private String adminEmail = "admin@localhost";
    private String userEmail = "user@localhost";
    
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestWorkerMonitor.class.getName()});
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        
        // If the following sys properties are provided (e.g. '-DTestPortalAdministrationImpl.smtp.host=localhost -DTestPortalAdministrationImpl.user.email=jdoe@localhost')
        // and if the destination smtp server is provided, then
        // this test case will send message to the recipient and the target server.
        // Otherwise, by default, this test case uses a mock object. 
        
        String prop = System.getProperty("TestPortalAdministrationImpl.smtp.host");
        if (prop != null) {
        	smtpHost = prop;
        }
        
        prop = System.getProperty("TestPortalAdministrationImpl.admin.email");
        if (prop != null) {
        	adminEmail = prop;
        }
        
        prop = System.getProperty("TestPortalAdministrationImpl.user.email");
        if (prop != null) {
        	userEmail = prop;
        }
    }
    
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPortalAdministrationImpl.class);
    }

    public void testPasswordGen() throws Exception
    {
        PortalAdministrationImpl pai = new PortalAdministrationImpl(null,null,null,null,null,null,null,null);
        String newPassword = pai.generatePassword();
        assertNotNull("new password was NULL!!!",newPassword);
        assertTrue("password is not long enough",(newPassword.length() > 4) );
        
    }
    
    public void testSendEmail() throws Exception 
    {
        JavaMailSenderImpl javaMailSender = null;
        
        if (smtpHost != null)
        {
            javaMailSender = new JavaMailSenderImpl();
            javaMailSender.setHost(smtpHost);
        }
        else
        {
            javaMailSender = new MockJavaMailSender();
            javaMailSender.setHost("mocksmtpserver");
        }
        
        PortalAdministrationImpl pai = new PortalAdministrationImpl(null,null,null,null,null,null,javaMailSender,null);
        pai.sendEmail(adminEmail,"this is a unittest",userEmail,"this is the content of the message");
        
        if (javaMailSender instanceof MockJavaMailSender)
        {
            MockJavaMailSender mockJavaMailSender = (MockJavaMailSender) javaMailSender;
            
            assertTrue(mockJavaMailSender.transport.isCloseCalled());
            
            assertEquals(1, mockJavaMailSender.transport.getSentMessages().size());
            
            MimeMessage sentMessage = mockJavaMailSender.transport.getSentMessage(0);
            
            List<Address> froms = Arrays.asList(sentMessage.getFrom());
            assertEquals(1, froms.size());
            assertEquals(adminEmail, ((InternetAddress) froms.get(0)).getAddress());
            
            List<Address> tos = Arrays.asList(sentMessage.getRecipients(Message.RecipientType.TO));
            assertEquals(1, tos.size());
            assertEquals(userEmail, ((InternetAddress) tos.get(0)).getAddress());
            
            assertEquals("this is a unittest", sentMessage.getSubject());
            
            assertEquals("this is the content of the message", sentMessage.getContent());
            
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            sentMessage.writeTo(output);
            String payload = new String(output.toByteArray());
            
            System.out.println("Mail message payload:\n\n" + payload + "\n");
        }
    }
    
    public void testSendMailFromAnotherThread() throws Exception {
    	final List<Exception> exceptions = new ArrayList<Exception>();
    	
    	Thread t = new Thread(new Runnable() {
    		public void run() {
    			ClassLoader cl = Thread.currentThread().getContextClassLoader();
    			try {
    				Thread.currentThread().setContextClassLoader(new URLClassLoader(new URL[0]));
    				testSendEmail();
    			} catch (Exception e) {
    				exceptions.add(e);
    				throw new RuntimeException(e);
    			} finally {
    				Thread.currentThread().setContextClassLoader(cl);
    			}
    		}
    	});
    	t.start();
    	t.join();
    	
    	if (!exceptions.isEmpty()) {
    		fail("testSendMail was not successful when the service is invoked from another thread having a different context classloader. " + exceptions.get(0));
    	}
    }
    
    // this needs too much init to test easily right now
    public void xtestRegUser() throws Exception
    {
        PortalAdministrationImpl pai = new PortalAdministrationImpl(null,null,null,null,null,null,null,null);
        String user = "user"+(Math.abs(new Date().getTime()));
        String password = "password";
        List emptyList = new ArrayList();
        Map emptyMap = new HashMap();
        Map userAttributes = new HashMap();
        String emailTemplate = "";
        pai.registerUser(user, 
                password, 
                emptyList, 
                emptyList, 
               userAttributes,              // note use of only PLT.D  values here.
               emptyMap, 
               emailTemplate);
        
    }
    
    private static class MockJavaMailSender extends JavaMailSenderImpl 
    {
        private MockTransport transport;

        @Override
        protected Transport getTransport(Session session) throws NoSuchProviderException 
        {
            this.transport = new MockTransport(session, null);
            return transport;
        }
    }
    
    private static class MockTransport extends Transport 
    {
        private String connectedHost = null;
        private int connectedPort = -2;
        private String connectedUsername = null;
        private String connectedPassword = null;
        private boolean closeCalled = false;
        private List<Message> sentMessages = new ArrayList<Message>();

        private MockTransport(Session session, URLName urlName) 
        {
            super(session, urlName);
        }

        public String getConnectedHost() 
        {
            return connectedHost;
        }

        public int getConnectedPort() 
        {
            return connectedPort;
        }

        public String getConnectedUsername() 
        {
            return connectedUsername;
        }

        public String getConnectedPassword() 
        {
            return connectedPassword;
        }

        public boolean isCloseCalled() 
        {
            return closeCalled;
        }

        public List<Message> getSentMessages() 
        {
            return sentMessages;
        }

        public MimeMessage getSentMessage(int index) 
        {
            return (MimeMessage) this.sentMessages.get(index);
        }

        @Override
        public void connect(String host, int port, String username, String password) throws MessagingException 
        {
            if (host == null) 
            {
                throw new MessagingException("no host");
            }
            
            this.connectedHost = host;
            this.connectedPort = port;
            this.connectedUsername = username;
            this.connectedPassword = password;
        }

        @Override
        public synchronized void close() throws MessagingException 
        {
            this.closeCalled = true;
        }

        @Override
        public void sendMessage(Message message, Address[] addresses) throws MessagingException 
        {
            List<Address> addr1 = Arrays.asList(message.getAllRecipients());
            List<Address> addr2 = Arrays.asList(addresses);
            
            if (!addr1.equals(addr2)) 
            {
                throw new MessagingException("addresses not correct");
            }
            
            if (message.getSentDate() == null) {
                throw new MessagingException("No sentDate specified");
            }
            
            this.sentMessages.add(message);
        }
    }
}
