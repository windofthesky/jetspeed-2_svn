#!/usr/bin/perl --

print qq(

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<title>SLA Report</title>
<META HTTP-EQUIV="CONTENT-TYPE" CONTENT="text/html; charset=windows-1252">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="-1">
<style type="text/css">
<!--
td {color: #000000; font-family: Arial, Helvetica, sans-serif; font-size: 12;}
tr {color: #000000; font-family: Arial, Helvetica, sans-serif; font-size: 12;}
td.back {background-color: #ccffcc;}
td.back2 {background-color: #ffffa6;}
td.back3 {background-color: #ffffff;}
td.back4 {background-color: #fef8cb; color: #cc0000;}
td.back5 {background-color: #fef8cb; color: #cc0000; font-weight: bold;}
td.head {background-color: #990000; font-family: Arial, Helvetica, sans-serif; font-size: 12; font-weight: bold; color: #ffffff;}
td.row1 {background-color: #ffffcc; font-family: Arial, Helvetica, sans-serif; font-size: 12;}
td.row2 {background-color: #eeeeee; font-family: Arial, Helvetica, sans-serif; font-size: 12;}

a.std:link    {
color:#CC3300; 
font-size: 12px;
font-family:Arial; Helvetica; sans-serif;
text-decoration: underline;
font-weight: normal;
}
a.std:visited {
color:#CC3300; 
font-size: 12px;
font-family:Arial; Helvetica; sans-serif;
text-decoration: underline;
font-weight: normal;
}
a.std:active  {
color:#CC3300; 
font-size: 12px;
font-family:Arial; Helvetica; sans-serif;
text-decoration: underline;
font-weight: normal;
}
a.std:hover   {
color:#CC3300; 
font-size: 12px;
font-family:Arial; Helvetica; sans-serif;
text-decoration: underline;
font-weight: normal;
}

/*Center paragraph*/
p.center {
color:#000; 
font-family:arial; helvetica; sans-serif;
font-size: 12px;
font-weight: normal;
}

/*Itallic paragraph*/
p.italic {
color:#000; 
font-family:arial; Helvetica; sans-serif;
font-style: italic;
font-size: 12px;
font-weight: normal;
}

p.quote {
font-size: 14px;
font-family:Arial; Helvetica; sans-serif;
color: #000; 
line-height: 18pt;
text-align: left;
font-style: italic;
font-weight: bold;
}

h1 {
color:#000; 
font-family:Arial; Helvetica; sans-serif;
font-size: 18px;
font-weight: bold;
}

h2 {
color:#000; 
font-family:Arial; Helvetica; sans-serif;
font-size: 14px;
font-weight: bold;
}

h3 {
color:#000; 
font-family:Arial; Helvetica; sans-serif;
font-size: 12px;
font-weight: bold;
}

h4 {
color:#FFFFFF; 
font-family:Arial; Helvetica; sans-serif;
font-size: 12px;
font-weight: bold;
}

h5 {
color:#000; 
font-family:Arial; Helvetica; sans-serif;
font-size: 16px;
font-style: italic;
font-weight: normal;
}

h6 {
color:#000; 
font-family:Arial; Helvetica; sans-serif;
font-size: 18px;
font-weight: bold;
}
-->
</style>
</head>
<body>

<div id=banner>
<table class=border width=100% cellspacing=0 cellpadding=5 border=0>
<tr>
<td class=back3><img src=content/images/jetspeed-logo.gif></td>
<td class=back3 align=center><br><h5>Operations Service Levels YTD </h5></td>
</tr>
<tr>
<td class=back3><h2>J2 Factory Service - Uptime and Unscheduled Downtime</td></h2>
<td class=back3 align=right><h2></td></h2>
</tr>
</table>
</div>
<div id=report>
<table class=border width=100% cellspacing=1 cellpadding=0 border=0>
<table class=border width=100% cellspacing=0 cellpadding=5 border=0>
<tr>
<td class=back colspan=3 align=left><h2>Report Period: January 1, 2004 to April 13, 2004</td></h2>
</tr>
<tr>
<td class=back align=left><h2>50 Live Co Brands</td></h2>
<td class=back align=left><h2>Average Service Level: 99.99%</td></h2>
<td class=back align=left><h2>Unscheduled Downtime 635 minutes</td></h2>
</tr>
<tr>
<td class=back align=center colspan=8><h2>Downtime in Minutes</td></h2>
</tr>
</table>
<table class=border width=100% cellspacing=1 cellpadding=2 border=0>
<tr>
<td class=back2>&nbsp;</td>
<td class=back2 align=left><h2>Data<br>Center</td></h2>
<td class=back2 align=left><br><h2>Co-brand</td></h2>
<td class=back2 align=center><h2>Unscheduled<br>Downtime</td></h2>
<td class=back2 align=center><h2>Service<br>Availability(%)</td></h2>
<td class=back2 align=center><h2>Monthly<br>SLA(%)</td></h2>
<td class=back2 align=center><h2>Scheduled<br>Downtime</td></h2>
<td class=back2 align=center><h2>Partial<br>Downtime</td></h2>
</tr>

<tr>
<td class=back4 align=center>1</td>
<td class=back2 align=left><b>RWS</b></td>
<td class=back4 align=left>Alpha-ING</td>
<td class=back4 align=center>15</td>
<td class=back4 align=center>99.99</td>
<td class=back2 align=center><b>99.50</b></td>
<td class=back4 align=center>0</td>
<td class=back4 align=center>0</td>
</tr>

<tr>
<td class=back4 align=center>2</td>
<td class=back2 align=left><b>RWS</b></td>
<td class=back4 align=left>Mexican Express</td>
<td class=back4 align=center>15</td>
<td class=back4 align=center>99.99</td>
<td class=back2 align=center><b>99.70</b></td>
<td class=back4 align=center>0</td>
<td class=back4 align=center>0</td>
</tr>

<tr>
<td class=back4 align=center>3</td>
<td class=back2 align=left><b>RWS</b></td>
<td class=back4 align=left>OpenJMS</td>
<td class=back4 align=center>15</td>
<td class=back4 align=center>99.99</td>
<td class=back2 align=center><b>99.70</b></td>
<td class=back4 align=center>115</td>
<td class=back4 align=center>0</td>
</tr>

<tr>
<td class=back3 align=center>4</td>
<td class=back2 align=left><b>RWS</b></td>
<td class=back3 align=left>Jakarta</td>
<td class=back3 align=center>0</td>
<td class=back3 align=center>100.00</td>
<td class=back2 align=center><b>99.70</b></td>
<td class=back3 align=center>0</td>
<td class=back3 align=center>0</td>
</tr>

<tr>
<td class=back4 align=center>5</td>
<td class=back2 align=left><b>RWS</b></td>
<td class=back4 align=left>Ant</td>
<td class=back4 align=center>179</td>
<td class=back4 align=center>99.88</td>
<td class=back2 align=center><b>99.50</b></td>
<td class=back4 align=center>0</td>
<td class=back4 align=center>0</td>
</tr>

<tr>
<td class=back4 align=center>6</td>
<td class=back2 align=left><b>RWS</b></td>
<td class=back4 align=left>Maven</td>
<td class=back4 align=center>15</td>
<td class=back4 align=center>99.99</td>
<td class=back2 align=center><b>99.50</b></td>
<td class=back4 align=center>0</td>
<td class=back4 align=center>0</td>
</tr>

<tr>
<td class=back3 align=center>7</td>
<td class=back2 align=left><b>RWS</b></td>
<td class=back3 align=left>Geronimo</td>
<td class=back3 align=center>0</td>
<td class=back3 align=center>100.00</td>
<td class=back2 align=center><b>99.70</b></td>
<td class=back3 align=center>0</td>
<td class=back3 align=center>0</td>
</tr>

<tr>
<td class=back3 align=center>8</td>
<td class=back2 align=left><b>RWS</b></td>
<td class=back3 align=left>Jitta</td>
<td class=back3 align=center>0</td>
<td class=back3 align=center>100.00</td>
<td class=back2 align=center><b>99.70</b></td>
<td class=back3 align=center>535</td>
<td class=back3 align=center>0</td>
</tr>

<tr>
<td class=back3 align=center>9</td>
<td class=back2 align=left><b>RWS</b></td>
<td class=back3 align=left>James</td>
<td class=back3 align=center>0</td>
<td class=back3 align=center>100.00</td>
<td class=back2 align=center><b>99.50</b></td>
<td class=back3 align=center>0</td>
<td class=back3 align=center>0</td>
</tr>

<tr>
<td class=back4 align=center>10</td>
<td class=back2 align=left><b>RWS</b></td>
<td class=back4 align=left>Gump</td>
<td class=back4 align=center>15</td>
<td class=back4 align=center>99.99</td>
<td class=back2 align=center><b>99.70</b></td>
<td class=back4 align=center>0</td>
<td class=back4 align=center>0</td>
</tr>

<tr>
<td class=back4 align=center>11</td>
<td class=back2 align=left><b>RWS</b></td>
<td class=back4 align=left>Tomcat</td>
<td class=back4 align=center>15</td>
<td class=back4 align=center>99.99</td>
<td class=back2 align=center><b>99.70</b></td>
<td class=back4 align=center>535</td>
<td class=back4 align=center>22</td>
</tr>

<tr>
<td class=back3 align=center>12</td>
<td class=back2 align=left><b>RWS</b></td>
<td class=back3 align=left>Catalina</td>
<td class=back3 align=center>0</td>
<td class=back3 align=center>100.00</td>
<td class=back2 align=center><b>99.70</b></td>
<td class=back3 align=center>535</td>
<td class=back3 align=center>39</td>
</tr>

<tr>
<td class=back3 align=center>13</td>
<td class=back2 align=left><b>RWS</b></td>
<td class=back3 align=left>Avalon</td>
<td class=back3 align=center>0</td>
<td class=back3 align=center>100.00</td>
<td class=back2 align=center><b>98.00</b></td>
<td class=back3 align=center>535</td>
<td class=back3 align=center>39</td>
</tr>

<tr>
<td class=back3 align=center>14</td>
<td class=back2 align=left><b>RWS</b></td>
<td class=back3 align=left>Hibernate</td>
<td class=back3 align=center>0</td>
<td class=back3 align=center>100.00</td>
<td class=back2 align=center><b>99.70</b></td>
<td class=back3 align=center>535</td>
<td class=back3 align=center>0</td>
</tr>

</table>
<table class=border width=100% cellspacing=0 cellpadding=5 border=0>
<tr>
<td class=back3><h2>Production Outage Issues:</td></h2>
</tr>

<tr>
<td class=back align=left><h2>Unscheduled</td></h2>
</tr>

<tr>
<td class=back3 align=left><b>Ticket #1 - Ticket created 2004-05-20 11:54:57 for group1</b></td>
</tr><tr>
<td class=back3 align=left><b>Event Date/Time: From March 1, 2004 - 10:20:00 am to March 1, 2004 - 10:35:00 am Pacific Time</b></td>
</tr><tr>
<td class=back3 align=left><b>Detail:</b> 03/1/2004 between 10:20 am PST to 10:35 am PST One of the database servers crashed and rebooted. It did not cleanly fail-over to the backup database server. The databases were manually brought up and service was restored.</td>
</tr><tr>
<td class=back3 align=left><b>Customers:</b>  J2 Factory, Jetspeed-1, Jetspeed-2, Jakarta</td>
</tr><tr>
<td class=back3 align=left><b>Completed:</b> All affected Cobrands 15 maintenance minutes used</td>
</tr><tr>
<td class=back3><hr></td>
</tr>



<tr>
<td class=back align=left><h2>Scheduled</td></h2>
</tr>

<tr>
<td class=back3 align=left><b>Ticket #21 - Ticket created 2004-05-20 13:31:20 for J2 Factory</b></td>
</tr><tr>
<td class=back3 align=left><b>Event Date/Time: From March 13, 2004 - 10:00:00 am to March 14, 2004 - 01:00:00 am Pacific Time</b></td>
</tr><tr>
<td class=back3 align=left><b>Detail:</b> 3/13/2004 10PM to 3/14/2004 01:00 AM PST Scheduled Maintenance for Database cluster maintenance and testing Preparation for the upgrade to the new database hardware platform</td>
</tr><tr>
<td class=back3 align=left><b>Customers:</b>  Gump, Jakarta, Maven, Ant</td>
</tr><tr>
<td class=back3 align=left><b>Completed:</b> All affected Cobrands 180 maintenance minutes used</td>
</tr><tr>
<td class=back3><hr></td>
</tr>

<tr>
<td class=back3 align=left><b>Ticket #23 - Ticket created 2004-05-21 10:15:33 for group2</b></td>
</tr><tr>
<td class=back3 align=left><b>Event Date/Time: From March 27, 2004 - 10:00:00 am to March 27, 2004 - 11:55:00 am Pacific Time</b></td>
</tr><tr>
<td class=back3 align=left><b>Detail:</b> 3/27/2004 10PM to 3/27/2004 11:55 PM PST Scheduled Maintenance for Database cluster maintenance and testing Preparation for the upgrade to the new database hardware platform</td>
</tr><tr>
<td class=back3 align=left><b>Customers:</b>  Gump, Jakarta, Maven, Ant</td>
</tr><tr>
<td class=back3 align=left><b>Completed:</b> All affected Cobrands 115 maintenance minutes used</td>
</tr><tr>
<td class=back3><hr></td>
</tr>


<tr>
<td class=back3 align=left><b>Ticket #8 - Ticket created 2004-05-20 12:58:19 for J2 Factory</b></td>
</tr><tr>
<td class=back3 align=left><b>Event Date/Time: From March 5, 2004 - 2:00:00 am to March 5, 2004 - 2:18:00 am Pacific Time</b></td>
</tr><tr>
<td class=back3 align=left><b>Detail:</b> 03/05/2004 2:00 pm to 2:18 pm PST Excessive number of request on Instant server. Found the process to be defunct on some of the servers. This was fixed by bouncing the servers.</td>
</tr><tr>
<td class=back3 align=left><b>Customers:</b>  J2 Factory</td>
</tr><tr>
<td class=back3 align=left><b>Completed:</b> J2 Factory 18 maintenance minutes used.</td>
</tr><tr>
<td class=back3><hr></td>
</tr>

<tr>
<td class=back3 align=left><b>Ticket #9 - Ticket created 2004-05-20 13:01:01 for J2 Factory</b></td>
</tr><tr>
<td class=back3 align=left><b>Event Date/Time: From March 8, 2004 - 09:13:00 am to March 8, 2004 - 09:30:00 am Pacific Time</b></td>
</tr><tr>
<td class=back3 align=left><b>Detail:</b> 3/8/2004 between 9:13 am and 9:30 am PST Excessive number of request on Instant server. Found the process to be defunct on some of the servers. This was fixed by bouncing the servers.</td>
</tr><tr>
<td class=back3 align=left><b>Customers:</b>  J2 Factory</td>
</tr><tr>
<td class=back3 align=left><b>Completed:</b> J2 Factory 17 maintenance minutes used.</td>
</tr><tr>
<td class=back3><hr></td>
</tr>

<tr>
<td class=back3 align=left><b>Ticket #10 - Ticket created 2004-05-20 13:03:13 for J2 Factory</b></td>
</tr><tr>
<td class=back3 align=left><b>Event Date/Time: From March 8, 2004 - 9:30:00 am to March 8, 2004 - 9:40:00 am Pacific Time</b></td>
</tr><tr>
<td class=back3 align=left><b>Detail:</b> 3/8/2004 between 9:30 am and 9:40 am PST Excessive number of request on Instant server. Found the process to be defunct on some of the servers. This was fixed by bouncing the servers.</td>
</tr><tr>
<td class=back3 align=left><b>Customers:</b>  J2 Factory</td>
</tr><tr>
<td class=back3 align=left><b>Completed:</b> J2 Factory 10 maintenance minutes used.</td>
</tr><tr>
<td class=back3><hr></td>
</tr>

<tr>
<td class=back3 align=left><b>Ticket #15 - Ticket created 2004-05-20 13:14:27 for Gump</b></td>
</tr><tr>
<td class=back3 align=left><b>Event Date/Time: From March 15, 2004 - 11:00:00 am to March 15, 2004 - 11:39:00 am Pacific Time</b></td>
</tr><tr>
<td class=back3 align=left><b>Detail:</b> 3/15/2004 between 11:00 am to 11:39 am PST The database reached the max connection limit, instant server process was not able to generate any instant request and hence there was a backlog of requests in the instant queue This was fixed by stopping the queue for a while</td>
</tr><tr>
<td class=back3 align=left><b>Customers:</b>  Gump</td>
</tr><tr>
<td class=back3 align=left><b>Completed:</b> Gump 39 maintenance minutes used.</td>
</tr><tr>
<td class=back3><hr></td>
</tr>

<tr>
<td class=back3 align=left><b>Ticket #16 - Ticket created 2004-05-20 13:18:05 for Gump Private</b></td>
</tr><tr>
<td class=back3 align=left><b>Event Date/Time: From March 16, 2004 - 11:00:49 am to March 16, 2004 - 11:39:49 am Pacific Time</b></td>
</tr><tr>
<td class=back3 align=left><b>Detail:</b> 3/16/2004 between 11:00 am to 11:39 am PST Excessive number of request on Instant server. Found the process to be defunct on some of the servers. This was fixed by bouncing the servers.</td>
</tr><tr>
<td class=back3 align=left><b>Customers:</b>  Gump Private</td>
</tr><tr>
<td class=back3 align=left><b>Completed:</b> Gump Private 39 maintenance minutes used.</td>
</tr><tr>
<td class=back3><hr></td>
</tr>

<tr>
<td class=back3 align=left><b>Ticket #17 - Ticket created 2004-05-20 13:19:45 for Tomcat</b></td>
</tr><tr>
<td class=back3 align=left><b>Event Date/Time: From March 17, 2004 - 10:32:00 am to March 17, 2004 - 10:56:00 am Pacific Time</b></td>
</tr><tr>
<td class=back3 align=left><b>Detail:</b> 3/17/2004 between 10:32 pm to 10:56 pm PST One of the server from the web server pool was not responding to he request as it was in defunct state Problem is resolved by bouncing the server</td>
</tr><tr>
<td class=back3 align=left><b>Customers:</b>  Tomcat</td>
</tr><tr>
<td class=back3 align=left><b>Completed:</b> Tomcat 24 maintenance minutes used.</td>
</tr><tr>
<td class=back3><hr></td>
</tr>

<tr>
<td class=back3 align=left><b>Ticket #18 - Ticket created 2004-05-20 13:26:40 for Tomcat</b></td>
</tr><tr>
<td class=back3 align=left><b>Event Date/Time: From March 17, 2004 - 11:20:00 am to March 17, 2004 - 11:24:00 am Pacific Time</b></td>
</tr><tr>
<td class=back3 align=left><b>Detail:</b> 3/17/2004 between 11:20 pm to 11:24 pm PST One of the server from the web server pool was not responding to to be defunct on some of the servers. This was fixed by bouncing the servers</td>
</tr><tr>
<td class=back3 align=left><b>Customers:</b>  Tomcat</td>
</tr><tr>
<td class=back3 align=left><b>Completed:</b> Tomcat 4 maintenance minutes used.</td>
</tr><tr>
<td class=back3><hr></td>
</tr>

<tr>
<td class=back3 align=left><b>Ticket #19 - Ticket created 2004-05-20 13:28:24 for Telephia</b></td>
</tr><tr>
<td class=back3 align=left><b>Event Date/Time: From March 29, 2004 - 1:30:00 am to March 29, 2004 - 2:55:42 am Pacific Time</b></td>
</tr><tr>
<td class=back3 align=left><b>Detail:</b> 3/29/2004 between 1:30 pm and 2:55 pm PST Our monitoring system indicated a problem with the edit page for Telephia cobrand. One of the server from the web server pool was not responding to the request as it was in unstable state. Problem is resolved after bouncing server.</td>
</tr><tr>
<td class=back3 align=left><b>Customers:</b>  Jakarta</td>
</tr><tr>
<td class=back3 align=left><b>Completed:</b> Jakarta 86 maintenance minutes used.</td>
</tr><tr>
<td class=back3><hr></td>
</tr>

<tr>
<td class=back3 align=left><b>Ticket #20 - Ticket created 2004-05-20 13:29:38 for J2 Factory</b></td>
</tr><tr>
<td class=back3 align=left><b>Event Date/Time: From March 29, 2004 - 08:15:00 am to March 29, 2004 - 08:34:00 am Pacific Time</b></td>
</tr><tr>
<td class=back3 align=left><b>Detail:</b> 3/29/2004 between 8:15 am to 8:34 am PST.The backend server, which is responsible for instant refresh, was not responding to any of the refresh requests. The problem is fixed after bouncing this process</td>
</tr><tr>
<td class=back3 align=left><b>Customers:</b>  J2 Factory</td>
</tr><tr>
<td class=back3 align=left><b>Completed:</b> J2 Factory 19 maintenance minutes used.</td>
</tr><tr>
<td class=back3><hr></td>
</tr>

<tr>
<td class=back align=left><h2>Partial Ignore</td></h2>
</tr>
</table>

</div>

</body>
</html>

);

