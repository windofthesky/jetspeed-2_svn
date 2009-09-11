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
package org.apache.jetspeed.proxy.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.FilePartSource;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.io.IOUtils;
import org.apache.jetspeed.proxy.HttpReverseProxyPathMapper;
import org.apache.jetspeed.proxy.HttpReverseProxyPathMapperProvider;
import org.apache.jetspeed.proxy.HttpReverseProxyService;
import org.apache.portals.applications.webcontent.rewriter.ParserAdaptor;
import org.apache.portals.applications.webcontent.rewriter.Rewriter;
import org.apache.portals.applications.webcontent.rewriter.RewriterController;
import org.apache.portals.applications.webcontent.rewriter.RewriterException;

/**
 * HTTP Reverse Proxy Service Implementation
 * 
 * @version $Id$
 */
public class RewritableHttpReverseProxyServiceImpl implements HttpReverseProxyService
{
    
    public static final String REVERSE_PROXY_PATH_MAPPER_ATTRIBUTE = "org.apache.jetspeed.proxy.reverseProxyPathMapper";
    
    public static final String REWRITER_CONTROLLER_ATTRIBUTE = "rewriterController";
    
    private static final String LOCATION_HEADER = "Location";
    
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    
    private static final String HOST_HEADER = "Host";

    /**
     * Proxy path mapper provider
     */
    private HttpReverseProxyPathMapperProvider proxyPathMapperProvider;
    
    /**
     * Forced host header value
     */
    private String hostHeaderValue;
    
    /**
     * forced local base url. e.g., "http://localhost:8080/jetspeed/webcontent".
     */
    private String localBaseURL;
    
    /**
     * flag to force to prefix localBaseURL when it redirects
     */
    private boolean prependLocalBaseURL;
    
    /**
     * Maximum file upload size
     */
    private int maxFileUploadSize;
    
    /**
     * File upload directory
     */
    private File fileUploadDir;

    public RewritableHttpReverseProxyServiceImpl(HttpReverseProxyPathMapperProvider proxyPathMapperProvider)
    {
        this.proxyPathMapperProvider = proxyPathMapperProvider;
    }
    
    public void setHostHeaderValue(String hostHeaderValue)
    {
        this.hostHeaderValue = hostHeaderValue;
    }
    
    public void setLocalBaseURL(String localBaseURL)
    {
        this.localBaseURL = localBaseURL;
    }
    
    public void setPrependLocalBaseURL(boolean prependLocalBaseURL)
    {
        this.prependLocalBaseURL = prependLocalBaseURL;
    }
    
    public void setMaxFileUploadSize(int maxFileUploadSize)
    {
        this.maxFileUploadSize = maxFileUploadSize;
    }
    
    public void setFileUploadDir(File fileUploadDir)
    {
        this.fileUploadDir = fileUploadDir;
    }
    
    public void invoke(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // proxyPathMapper can be injected by using request attribute.
        HttpReverseProxyPathMapper proxyPathMapper = (HttpReverseProxyPathMapper) request.getAttribute(REVERSE_PROXY_PATH_MAPPER_ATTRIBUTE);
        
        String pathInfo = request.getPathInfo();
        
        if (proxyPathMapper == null)
        {
            proxyPathMapper = proxyPathMapperProvider.findMapper(request);
        }
        
        if (proxyPathMapper == null)
        {
            throw new IOException("Proxy configuration is not defined for " + pathInfo);
        }
        
        if (hostHeaderValue == null)
        {
            if (request.getServerPort() == 80)
            {
                hostHeaderValue = request.getServerName();
            }
            else
            {
                hostHeaderValue = request.getServerName() + ":" + request.getServerPort();
            }
        }
        
        if (localBaseURL == null)
        {
            localBaseURL = request.getScheme() + "://" + hostHeaderValue + request.getServletPath();
        }
        
        String proxyTargetURL = proxyPathMapper.getRemoteURL(pathInfo);
        
        String method = request.getMethod();
        boolean isGetMethod = "GET".equals(method);
        boolean isPostMethod = "POST".equals(method);
        boolean isPostMultipartMethod = isPostMethod && ServletFileUpload.isMultipartContent(request);
        
        HttpClient httpClient = new HttpClient();
        HttpMethod httpMethodForProxyRequest = null;
        
        if (isGetMethod)
        {
            httpMethodForProxyRequest = new GetMethod(proxyTargetURL);
        }
        else if (isPostMethod)
        {
            httpMethodForProxyRequest = new PostMethod(proxyTargetURL);
            
            if (isPostMultipartMethod)
            {
                postMultipartParams((PostMethod) httpMethodForProxyRequest, request);
            }
            else
            {
                postFormParams((PostMethod) httpMethodForProxyRequest, request);
            }
        }
        else
        {
            throw new IOException("Unsupported method: " + method);
        }
        
        // redirection should be adjusted with local host header...
        httpMethodForProxyRequest.setFollowRedirects(false);
        
        setProxyRequestHeaders(httpMethodForProxyRequest, request);
        
        if (isPostMultipartMethod)
        {
            postMultipartParams((PostMethod) httpMethodForProxyRequest, request);
        }
        else if (isPostMethod)
        {
            postFormParams((PostMethod) httpMethodForProxyRequest, request);
        }
        
        int proxyResponseCode = httpClient.executeMethod(httpMethodForProxyRequest);
        
        // Check if the proxy response is a redirect
        if (proxyResponseCode >= HttpServletResponse.SC_MULTIPLE_CHOICES /* 300 */
            && proxyResponseCode < HttpServletResponse.SC_NOT_MODIFIED /* 304 */)
        {
            String location = httpMethodForProxyRequest.getResponseHeader(LOCATION_HEADER).getValue();
            
            if (location == null)
            {
                throw new IOException("Recieved status code: " + proxyResponseCode + " but no " + LOCATION_HEADER + " header was found in the response");
            }
            
            // Modify the redirect to go to this proxy servlet rather that the proxied host
            String localPath = proxyPathMapper.getLocalPath(location);
            String redirectLocation = null;
            
            if (prependLocalBaseURL) {
                redirectLocation = localBaseURL + localPath;
            } else {
                redirectLocation = request.getContextPath() + request.getServletPath() + localPath;
            }
            
            response.sendRedirect(redirectLocation);
            return;
        }
        else if (proxyResponseCode == HttpServletResponse.SC_NOT_MODIFIED)
        {
            // 304 needs special handling. See:
            // http://www.ics.uci.edu/pub/ietf/http/rfc1945.html#Code304
            // We get a 304 whenever passed an 'If-Modified-Since'
            // header and the data on disk has not changed; server
            // responds w/ a 304 saying I'm not going to send the
            // body because the file has not changed.
            response.setIntHeader(CONTENT_LENGTH_HEADER, 0);
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            
            return;
        }
        
        // Pass the response code back to the client
        response.setStatus(proxyResponseCode);
        
        // Pass response headers back to the client
        Header[] headerArrayResponse = httpMethodForProxyRequest.getResponseHeaders();
        
        for (Header header : headerArrayResponse)
        {
            response.setHeader(header.getName(), header.getValue());
        }
        
        // Send the content to the client
        
        InputStream in = null;
        Reader reader = null;
        OutputStream out = null;
        Writer writer = null;
        
        try 
        {
            in = httpMethodForProxyRequest.getResponseBodyAsStream();
            out = response.getOutputStream();
            
            RewriterController rewriterController = (RewriterController) proxyPathMapper.getAttribute(REWRITER_CONTROLLER_ATTRIBUTE);
            
            if (rewriterController == null)
            {
                IOUtils.copy(in, out);
                out.flush();
            }
            else
            {
                String contentType = "text/html";
                Header contentTypeHeader = httpMethodForProxyRequest.getResponseHeader(CONTENT_TYPE_HEADER);
                
                if (contentTypeHeader != null && contentTypeHeader.getElements().length > 0)
                {
                    contentType = contentTypeHeader.getElements()[0].getValue();
                    int offset = contentType.indexOf(';');
                    
                    if (offset > 0)
                    {
                        contentType = contentType.substring(0, offset).trim();
                    }
                }
                
                Rewriter rewriter = rewriterController.createRewriter();
                ParserAdaptor parserAdaptor = rewriterController.createParserAdaptor(contentType);
                reader = new InputStreamReader(in);
                writer = new OutputStreamWriter(out);
                rewriter.rewrite(parserAdaptor, reader, writer);
                writer.flush();
            }
        }
        catch (RewriterException e)
        {
            throw new IOException(e.getLocalizedMessage());
        }
        catch (IllegalAccessException e)
        {
            throw new IOException(e.getLocalizedMessage());
        }
        catch (InstantiationException e)
        {
            throw new IOException(e.getLocalizedMessage());
        }
        finally
        {
            if (reader != null)
            {
                try { reader.close(); } catch (Exception ce) { }
            }
            if (in != null)
            {
                try { in.close(); } catch (Exception ce) { }
            }
            if (writer != null)
            {
                try { writer.close(); } catch (Exception ce) { }
            }
            if (out != null)
            {
                try { out.close(); } catch (Exception ce) { }
            }
        }
    }

    private void setProxyRequestHeaders(HttpMethod httpMethodForProxyRequest, HttpServletRequest request) 
    {
        // Get an Enumeration of all of the header names sent by the client
        for (Enumeration enumHeaderNames = request.getHeaderNames(); enumHeaderNames.hasMoreElements(); ) 
        {
            String headerName = (String) enumHeaderNames.nextElement();
            
            if (headerName.equalsIgnoreCase(CONTENT_LENGTH_HEADER))
                continue;
            
            // As per the Java Servlet API 2.5 documentation:
            //      Some headers, such as Accept-Language can be sent by clients
            //      as several headers each with a different value rather than
            //      sending the header as a comma separated list.
            // Thus, we get an Enumeration of the header values sent by the client
            
            for (Enumeration enumHeaderValues = request.getHeaders(headerName); enumHeaderValues.hasMoreElements(); )
            {
                String headerValue = (String) enumHeaderValues.nextElement();
                
                // In case the proxy host is running multiple virtual servers,
                // rewrite the Host header to ensure that we get content from
                // the correct virtual server
                if (headerName.equalsIgnoreCase(HOST_HEADER))
                {
                    headerValue = hostHeaderValue;
                }
                
                Header header = new Header(headerName, headerValue);
                
                // Set the same header on the proxy request
                httpMethodForProxyRequest.setRequestHeader(header);
            }
        }
    }
    
    private void postFormParams(PostMethod httpMethodForProxyRequest, HttpServletRequest request)
    {
        Map<String, String[]> paramsMap = (Map<String, String[]>) request.getParameterMap();
        List<NameValuePair> paramNameValuePairs = new ArrayList<NameValuePair>();
        
        for (String paramName : paramsMap.keySet())
        {
            String [] paramValues = paramsMap.get(paramName);
            
            for (String paramValue : paramValues)
            {
                NameValuePair nameValuePair = new NameValuePair(paramName, paramValue);
                paramNameValuePairs.add(nameValuePair);
            }
        }
        
        httpMethodForProxyRequest.setRequestBody(paramNameValuePairs.toArray(new NameValuePair[] {}));
    }
    
    private void postMultipartParams(PostMethod httpMethodForProxyRequest, HttpServletRequest request) throws IOException
    {
        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        
        if (maxFileUploadSize > 0)
        {
            fileItemFactory.setSizeThreshold(maxFileUploadSize);
        }
        
        if (fileUploadDir != null)
        {
            fileItemFactory.setRepository(fileUploadDir);
        }
        
        ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);
        
        try
        {
            List<FileItem> fileItems = (List<FileItem>) fileUpload.parseRequest(request);
            Part [] parts = new Part[fileItems.size()];
            int i = 0;
            
            for (FileItem fileItem : fileItems)
            {
                if (fileItem.isFormField())
                {
                    parts[i] = (new StringPart(fileItem.getFieldName(), fileItem.getString()));
                }
                else
                {
                    PartSource partSource = null;
                    
                    if (fileItem.isInMemory())
                    {
                        partSource = new ByteArrayPartSource(fileItem.getName(), fileItem.get());
                    }
                    else
                    {
                        partSource = new FilePartSource(fileItem.getFieldName(), ((DiskFileItem) fileItem).getStoreLocation());
                    }
                    
                    parts[i] = (new FilePart(fileItem.getFieldName(), partSource));
                }
                
                i++;
            }
            
            MultipartRequestEntity multipartRequestEntity = new MultipartRequestEntity(parts, httpMethodForProxyRequest.getParams());
            httpMethodForProxyRequest.setRequestEntity(multipartRequestEntity);
            
            // The current content-type header (received from the client) IS of
            // type "multipart/form-data", but the content-type header also
            // contains the chunk boundary string of the chunks. Currently, this
            // header is using the boundary of the client request, since we
            // blindly copied all headers from the client request to the proxy
            // request. However, we are creating a new request with a new chunk
            // boundary string, so it is necessary that we re-set the
            // content-type string to reflect the new chunk boundary string
            httpMethodForProxyRequest.setRequestHeader(CONTENT_TYPE_HEADER, multipartRequestEntity.getContentType());
        }
        catch (FileUploadException fileUploadException)
        {
            throw new IOException(fileUploadException.getLocalizedMessage());
        }
    }
    
}
