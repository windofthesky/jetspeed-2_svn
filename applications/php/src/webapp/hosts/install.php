<?php
/*******************************************************************************
**	file:	install.php
********************************************************************************
**	author:	Scott Parris
**	date:	2004/6/2
********************************************************************************
**	
*******************************************************************************/

$step = $_GET['step'];
$samdat = $_GET['samdat'];
require_once "conf/config.php";
//connect and select the proper database....die if database not found.


echo '
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<title> Hosts Installation </title>
<meta name="Author" content="Scott Parris">
<meta name="Description" content="GroundWork Hosts Installation">
<link rel="stylesheet" type="text/css" href="conf/style.css" />
</head>
<body>
<table width=70% align="center" cellpadding=5 cellspacing=0 border=1>
<tr>
<td>
<table width=100% cellpadding=5>
<tr>
<td class=dkred><h4>GroundWork Configuration Management Installation</td></h4>
</tr>';

if ($step == 1) {
	$connection = mysql_connect($mysql_host, $mysql_user, $mysql_pwd);
	mysql_select_db($mysql_db) or die(mysql_error());

	mysql_query("
			CREATE TABLE services (
			  id int(11) NOT NULL auto_increment,
			  name varchar(60) default NULL,
			  PRIMARY KEY (id)
			)")or die(mysql_errno() . " " . mysql_error());

	mysql_query("
			CREATE TABLE interfaces (
			  id int(11) NOT NULL auto_increment,
			  name varchar(60) default NULL,
			  PRIMARY KEY (id)
			)")or die(mysql_errno() . " " . mysql_error());

	mysql_query("
			CREATE TABLE host_interface (
			  id int(11) NOT NULL auto_increment,
			  host_id int(11) NOT NULL,
			  interface_id int(11) NOT NULL,
			  PRIMARY KEY (id)
			)")or die(mysql_errno() . " " . mysql_error());

	mysql_query("
			CREATE TABLE host_service (
			  id int(11) NOT NULL auto_increment,
			  host_id int(11) NOT NULL,
			  service_id int(11) NOT NULL,
			  PRIMARY KEY (id)
			)")or die(mysql_errno() . " " . mysql_error());

	mysql_query("
			CREATE TABLE hosts (
			  id int(11) NOT NULL auto_increment,
			  name varchar(60) default NULL,
			  address varchar(60) default NULL,
			  os varchar(60) default NULL,
			  serial varchar(60) default NULL,
			  type varchar(60) default NULL,
			  PRIMARY KEY (id)
			)")or die(mysql_errno() . " " . mysql_error());

	if (isset($samdat)) {
		mysql_query("insert into hosts values(NULL, 'mercury', '192.168.2.7', 'redhat', 'F897342HG', 'intel')"); 
		mysql_query("insert into hosts values(NULL, 'venus', '192.168.2.3', 'aix', 'F897342HG', 'aix')"); 
		mysql_query("insert into hosts values(NULL, 'earth', '192.168.2.2', 'os400', 'F897342HG', 'as400')"); 
		mysql_query("insert into hosts values(NULL, 'mars', '192.168.2.16', 'redhat', 'F897342HG', 'intel')"); 
		mysql_query("insert into hosts values(NULL, 'jupiter', '192.168.2.17', 'redhat', 'F897342HG', 'intel')"); 
		mysql_query("insert into hosts values(NULL, 'saturn', '192.168.2.15', 'redhat', 'F897342HG', 'intel')"); 
		mysql_query("insert into hosts values(NULL, 'uranus', '192.168.2.11', 'redhat', 'F897342HG', 'intel')"); 
		mysql_query("insert into hosts values(NULL, 'neptune', '192.168.2.13', 'solaris', 'F897342HG', 'sun')"); 
		mysql_query("insert into hosts values(NULL, 'pluto', '192.168.2.12', 'windows', 'F897342HG', 'intel')"); 
		mysql_query("insert into services values(NULL, 'cpu')"); 
		mysql_query("insert into services values(NULL, 'disk')"); 
		mysql_query("insert into services values(NULL, 'memory')"); 
		mysql_query("insert into services values(NULL, 'process')"); 
		mysql_query("insert into services values(NULL, 'service')"); 

	}

	echo '
	<form action=index.php>
	<tr>
	<td><br><br>
	<h2>Install GroundWork Configuration Management</h2>
	</td>
	</tr>
	<tr>
	<td>
	<h2>Installation Comptete!</h2>
	</td>
	</tr>	<tr>
	<td>
	<input type=submit name=submit value="Enter GroundWork Hosts">
	</td>
	</tr>
	</form>';

} else {

	echo '
	<form action=install.php>
	<input type=hidden name=step value=1>
	<tr>
	<td><br><br>
	<h2>Install GroundWork Configuration Management</h2>
	</td>
	</tr>
	<tr>
	<td>
	<h3><input type=checkbox name=samdat value=1> Include sample data</h3>
	</td>
	</tr>	
	<tr>
	<td>
	<input type=submit name=submit value="Install GroundWork Hosts">
	</td>
	</tr>
	</form>';
}

echo '
</table>
</td>
</tr>
</table>
</body>
</html>';

?>
