<?php

/*******************************************************************************
**	file:	interfaces.php
********************************************************************************
**	author:	Scott Parris
**	date:	2004/6/2
********************************************************************************
**	Configuration Database Demo
*******************************************************************************/

$task = $_GET['task'] ? $_GET['task'] : $_POST['task'];
$id = $_GET['id'] ? $_GET['id'] : $_POST['id'];
$name = $_GET['name'] ? $_GET['name'] : $_POST['name'];
$submit = $_GET['submit'] ? $_GET['submit'] : $_POST['submit'];

switch($task) {
	case('add'):
		add();
		break;
	case('delete'):
		delete($id);
		break;
	case('edit'):
		edit();
		break;
	default:
		show();
		break;
}

function show() {
	echo '
<table width=100% bgcolor=#dddddd cellpadding=5 celspacing=0 border=0>
<tr>
<td class=head width=10%>Id</td>
<td class=head colspan=3>Name</td>
</tr>';
	$class = 'row1';
	$sql = "select * from interfaces order by name";
	$result = execsql($sql);
	while ($row = mysql_fetch_row($result)) {
		echo '
		<tr>
		<td class='.$class.'>'.$row[0].'</td>
		<td class='.$class.'>'.$row[1].'</td>
		<td class='.$class.' width=10% align=center><a class=std href=/PHP/hosts/index.php?pagedest=interfaces&task=edit&id='.$row[0].'>edit</td>
		<td class='.$class.' width=10% align=center><a class=std href=/PHP/hosts/index.php?pagedest=interfaces&task=delete&id='.$row[0].'>delete</td>
		</tr>';
		if ($class == 'row1')
			$class = 'row2';
		else
			$class = 'row1';
	}
	echo '
</table>';

}

function add() {
	global $submit, $name;
	if ($submit == 'Cancel') {
		show();
	} else if ($name != '') {
		$sql = "insert into interfaces values(NULL, '$name')";
		if (execsql($sql)) {
			formSuccess('Service '.$name.' added.');
		}
	} else {
		$action = "/PHP/hosts/index.php";
		$title = 'Add Interface';
		$rows = array();
		$row = array('hidden', 'pagedest', 'interfaces');
		array_push($rows, $row);
		$row = array('hidden', 'task', 'add');
		array_push($rows, $row);
		$row = array('text', "* Name", 'name', $name);
		array_push($rows, $row);
		$buttons = array();
		$button = array('submit', 'Add Interface', 'submit');
		array_push($buttons, $button);
		$button = array('submit', 'Cancel', 'submit');
		array_push($buttons, $button);
	
		formInput($action, $title, $rows, $buttons);
	}
}

function edit() {
	global $submit, $id, $name;
	if ($submit == 'Cancel') {
		show();
	} else if ($name != '') {
		$sql = "update interfaces set name = '$name' where id= '$id'";
		if (execsql($sql)) {
			formSuccess('Interface '.$name.' updated.');
		}
	} else {
		$info = getInterfaceInfo($id);
		$action = "/PHP/hosts/index.php";
		$title = 'Modify Interface';
		$rows = array();
		$row = array('hidden', 'pagedest', 'interfaces');
		array_push($rows, $row);
		$row = array('hidden', 'task', 'edit');
		array_push($rows, $row);
		$row = array('hidden', 'id', $id);
		array_push($rows, $row);
		$row = array('text', "* Name", 'name', $info[0]);
		array_push($rows, $row);
		$buttons = array();
		$button = array('submit', 'Update Interface', 'submit');
		array_push($buttons, $button);
		$button = array('submit', 'Cancel', 'submit');
		array_push($buttons, $button);
	
		formInput($action, $title, $rows, $buttons);
	}
}


function delete($id) {
	global $page, $submit;
	$info = getInterfaceInfo($id);
	$error = 1;
	if ($submit == 'Cancel') {
		show();
	} else if ($submit == 'Confirm Delete') {		
		$sql = "delete from host_interface where interface_id = '$id'";
		if (execsql($sql)) {
			$error = 0;
			$sql = "delete from interfaces where id = '$id'";
			if (execsql($sql)) {
				$error = 0;
			}
		}
		if ($error == 0) {
			formSuccess("Interface $info[0] removed.");
		} else {
			formError("Failed to remove interface $info[0]");
		}
	} else {
		$message = "You are about to delete interface $info[0].<br>The interface will also be removed from all hosts.<br>Are you sure?";
		formDelete($message, $page, $id);
	}
}

?>