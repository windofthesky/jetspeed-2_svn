#!/usr/bin/perl --

print qq(

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
iv
h6 {
color:#000; 
font-family:Arial; Helvetica; sans-serif;
font-size: 18px;
font-weight: bold;
}
-->
</style>
<div id=banner>
<table class=border width=100% cellspacing=0 cellpadding=5 border=0>
<tr>
<td class=back3><img src=content/images/jetspeed-logo.gif></td>
<td class=back3 align=center><br><h5>Operations Service Levels YTD </h5></td>
</tr>
<tr>
<td class=back3><h2>Next Gen Enterprise Service - Uptime and Unscheduled Downtime</td></h2>
<td class=back3 align=right><h2></td></h2>
</tr>
</table>
</div>
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
<td class=back4 align=left>NextGen Enterprise</td>
<td class=back4 align=center>15</td>
<td class=back4 align=center>99.99</td>
<td class=back2 align=center><b>99.50</b></td>
<td class=back4 align=center>0</td>
<td class=back4 align=center>0</td>
</tr>

<tr>
<td class=back4 align=center>2</td>
<td class=back2 align=left><b>RWS</b></td>
<td class=back4 align=left>Argentina Express</td>
<td class=back4 align=center>15</td>
<td class=back4 align=center>99.99</td>
<td class=back2 align=center><b>99.70</b></td>
<td class=back4 align=center>0</td>
<td class=back4 align=center>0</td>
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
</tr>
<tr>
<td class=back3 align=left><b>Event Date/Time: From March 1, 2004 - 10:20:00 am to March 1, 2004 - 10:35:00 am Pacific Time</b></td>
</tr><tr>
<td class=back3 align=left><b>Detail:</b> 03/1/2004 between 10:20 am PST to 10:35 am PST One of the database servers crashed and rebooted. It did not cleanly fail-over to the backup database server. The databases were manually brought up and service was restored.</td>
</tr><tr>
<td class=back3 align=left><b>Customers:</b>  Next Gen Enterprise, Jetspeed-1, Jetspeed-2, Jakarta</td>
</tr><tr>
<td class=back3 align=left><b>Completed:</b> All affected Cobrands 15 maintenance minutes used</td>
</tr><tr>
<td class=back3><hr></td>
</tr>



<tr>
<td class=back align=left><h2>Scheduled</td></h2>
</tr>

<tr>
<td class=back3 align=left><b>Ticket #21 - Ticket created 2004-05-20 13:31:20 for Next Gen Enterprise</b></td>
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
<td class=back align=left><h2>Partial Ignore</td></h2>
</tr>
</table>

);

