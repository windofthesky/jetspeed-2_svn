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
    
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestWorkerMonitor.class.getName()});
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        
        // If the following sys property is provided (e.g. '-DTestPortalAdministrationImpl.smtp.host=localhost')
        // and if the destination smtp server is provided, then
        // this test case will send message to the target server.
        // Otherwise, by default, this test case uses a mock object. 
        smtpHost = System.getProperty("TestPortalAdministrationImpl.smtp.host");
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
        pai.sendEmail("chris@bluesunrise.com","this is a unittest","david@bluesunrise.com","this is the content of the message");
        
        if (javaMailSender instanceof MockJavaMailSender)
        {
            MockJavaMailSender mockJavaMailSender = (MockJavaMailSender) javaMailSender;
            
            assertTrue(mockJavaMailSender.transport.isCloseCalled());
            
            assertEquals(1, mockJavaMailSender.transport.getSentMessages().size());
            
            MimeMessage sentMessage = mockJavaMailSender.transport.getSentMessage(0);
            
            List<Address> froms = Arrays.asList(sentMessage.getFrom());
            assertEquals(1, froms.size());
            assertEquals("chris@bluesunrise.com", ((InternetAddress) froms.get(0)).getAddress());
            
            List<Address> tos = Arrays.asList(sentMessage.getRecipients(Message.RecipientType.TO));
            assertEquals(1, tos.size());
            assertEquals("david@bluesunrise.com", ((InternetAddress) tos.get(0)).getAddress());
            
            assertEquals("this is a unittest", sentMessage.getSubject());
            
            assertEquals("this is the content of the message", sentMessage.getContent());
            
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            sentMessage.writeTo(output);
            String payload = new String(output.toByteArray());
            
            System.out.println("Mail message payload:\n\n" + payload + "\n");
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
