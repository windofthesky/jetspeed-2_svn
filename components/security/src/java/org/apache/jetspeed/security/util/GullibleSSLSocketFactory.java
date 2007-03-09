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
package org.apache.jetspeed.security.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Socket Factory for SSL connections which do not provide an authentication
 * This is used to connect to servers where we are just interested in
 * an encypted tunnel, and not to verify that both parties trust each other.
 *
 * @author <a href="mailto:b.vanhalderen@hippo.nl">Berry van Halderen</a>
 * @version $Id$
 *
 */
public class GullibleSSLSocketFactory extends SSLSocketFactory {

  class GullibleTrustManager implements X509TrustManager
  {
    GullibleTrustManager() { }
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
    }

    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
    }
  
    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
    }
  }

   private SSLSocketFactory factory;
   protected GullibleSSLSocketFactory() {
      try {
         SSLContext context = SSLContext.getInstance("TLS");
         context.init(null, new TrustManager[] {new GullibleTrustManager()},
            new SecureRandom());
         factory = context.getSocketFactory();
      } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
      } catch (KeyManagementException e) {
         e.printStackTrace();
      }
   }
   public static SocketFactory getDefault() {
      return new GullibleSSLSocketFactory();
   }
   public String[] getDefaultCipherSuites() {
      return factory.getDefaultCipherSuites();
   }
   public String[] getSupportedCipherSuites() {
      return factory.getSupportedCipherSuites();
   }
   public Socket createSocket(final Socket s, final String host, final int port, final boolean autoClose) throws IOException {
      return factory.createSocket(s, host, port, autoClose);
   }
   public Socket createSocket(final String host, final int port) throws IOException, UnknownHostException {
      return factory.createSocket(host, port);
   }
   public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort) throws IOException, UnknownHostException {
      return factory.createSocket(host, port, localAddress, localPort);
   }
   public Socket createSocket(final InetAddress host, final int port) throws IOException {
      return factory.createSocket(host, port);
   }
   public Socket createSocket(final InetAddress address, final int port, final InetAddress localAddress, final int localPort) throws IOException {
      return factory.createSocket(address, port, localAddress, localPort);
   }
}
