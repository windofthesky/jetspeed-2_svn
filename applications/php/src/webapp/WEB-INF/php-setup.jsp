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
<%@ page session="false"%>
<p>
<TABLE WIDTH=100% BORDER=1 BORDERCOLOR="#000000" CELLPADDING=4 CELLSPACING=0 FRAME=BELOW RULES=GROUPS STYLE="page-break-before: always">
<COLGROUP>
	<COL WIDTH=74*>
	<COL WIDTH=88*>
</COLGROUP>
<COLGROUP>
	<COL WIDTH=94*>
</COLGROUP>
<THEAD>
	<TR>
		<TH COLSPAN=3 WIDTH=100% VALIGN=TOP>
			<P>The following document describes how to install and configure
			your php-Applications</P>
		</TH>
	</TR>
</THEAD>
<TBODY>
	<TR VALIGN=TOP>
		<TD COLSPAN=2 WIDTH=63%>
			<P><U><B>System requirements</B></U></P>
		</TD>
		<TD WIDTH=37%>
			<P><U><B>Resources</B></U></P>
		</TD>
	</TR>
	<TR VALIGN=TOP>
		<TD COLSPAN=2 WIDTH=63%>
			<P><FONT SIZE=2>The PHP library needs to be in the path of the
			servlet container so that it can get invoked from the portlet.
			The library is platform specific. Download the library from the
			PHP site or from the links on the left.</FONT></P>
		</TD>
		<TD ROWSPAN=4 WIDTH=37%>
			<UL>
				<LI><P><FONT SIZE=2>Linux: </FONT><A HREF="http://www.itgroundwork.com/resources/downloads/libphp4.so"><FONT SIZE=2>libphp4.so</FONT></A></P>
				<LI><P><FONT SIZE=2>Windows: </FONT><A HREF="http://www.itgroundwork.com/resources/downloads/php4ts.dll"><FONT SIZE=2>php4ts.dll</FONT></A><FONT SIZE=2>
				&amp; </FONT><A HREF="http://www.itgroundwork.com/resources/downloads/phpsrvlt.dll"><FONT SIZE=2>phpsrvlt.dll</FONT></A></P>
				<LI><P><FONT SIZE=2>Servlet interface between PHP libraries and
				portlets. The package is automatically downloaded to the
				maven/repository/itgroundwork/jars directory during the build.<A HREF="http://www.itgroundwork.com/resources/downloads/phpportlet.jar">phportlet.jar</A></FONT></P>
			</UL>
		</TD>
	</TR>
</TBODY>
<TBODY>
	<TR>
		<TD COLSPAN=2 WIDTH=63% VALIGN=TOP>
			<P><FONT SIZE=3><U><B>Setup</B></U></FONT></P>
		</TD>
	</TR>
	<TR VALIGN=TOP>
		<TD WIDTH=29%>
			<P><FONT SIZE=2>Linux</FONT></P>
		</TD>
		<TD WIDTH=34%>
			<P><FONT SIZE=2>Windows</FONT></P>
		</TD>
	</TR>
	<TR VALIGN=TOP>
		<TD WIDTH=29%>
			<P><FONT SIZE=2>Download libphp4.so and add the directory
			containing the library to the environment var  <B>LD_LIBRARY_PATH</B>.</FONT></P>
			<P><BR>
			</P>
		</TD>
		<TD WIDTH=34%>
			<P><FONT SIZE=2>Download php4ts.dll and phpsrvlt.dll and add the
			directory containing  the libraries to the <B>PATH.</B></FONT></P>
		</TD>
	</TR>
</TBODY>
</TABLE>
</p>
