<%--
Copyright 2004 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">



<%
    java.util.List contentPathes = new java.util.ArrayList(2);
	if(request.getParameter("setTheme") != null)
	{
        contentPathes.add(request.getParameter("setTheme"));               
	}
	else
	{
		contentPathes.add("themes/blue");               
	}
	contentPathes.add("skins");
	request.getSession().setAttribute("org.apache.jetspeed.content.pathes", contentPathes);
%>
<html>
  <head>
    <title>Test content</title>
    <script language="JavaScript1.2" type="text/javascript" src="content/script/test.js"></script>
  </head>
  <body onLoad="Hello();">
   <h1 id="target">
   </h1>
   <p>
   	 This is a normal, static image: <img src="images/myimage.gif" align="middle"/>
   </p>
      <p>
   	 This is  image was located and served by the content server filter: <img src="content/images/myimage.gif" align="middle"/>
   </p>
   <p>
     Current theme is: <%=session.getAttribute("org.apache.jetspeed.theme")%>
     <br />
      <a href="index.jsp?setTheme=themes/red">Set theme to Red</a>
     <br />
      <a href="index.jsp?setTheme=themes/blue">Set theme to Blue</a>
   </p>
  <iframe src="content/page.html" width="300" height="300" ></iframe>
   <br>

   <br>
    <iframe src="content/skin1/skin_page.html" width="300" height="300" ></iframe>
    

    <br>
    
    <iframe src="content/skin2/skin_page.html" width="300" height="300" ></iframe>
   
  </body>
 </html>  