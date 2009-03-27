<%--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<html>
  <title>Portal Diagnostic Information</title>
  <body>
  <h2>Portal Diagnostic Information</h2>
	<p>
	<b>General Message: <%= request.getSession().getAttribute("org.apache.portals.jestspeed.diagnostics") %></b>
	</p>
	<p>
	  To return to the server, click here: <a href='<%= request.getContextPath() + "/portal" %>'>Return to Server</a>
	</p>
    <h3>Server Information:</h3>
	<p>
	<ul>
		<li>Free Memory (KB): <%= Runtime.getRuntime().freeMemory()/1024 %></li>
		<li>Total Memory (KB): <%= Runtime.getRuntime().totalMemory()/1024  %></li>
	</ul>
	</p>
<!-- 
This page is meant to be used for diagnostics when the portal becomes unavailable
TODO: provide other diagnostic information, Heap Usage, Number of users 
-->
  </body>
</html> 

