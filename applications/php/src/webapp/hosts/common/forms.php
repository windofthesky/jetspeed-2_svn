<?php
/*******************************************************************************
**	file:	forms.php
********************************************************************************
**	author:	Scott Parris
**	date:	2004/6/2
********************************************************************************
**	Configuration Database Demo
*******************************************************************************/

/***********************************************************************************************************
**	function formInput():
************************************************************************************************************/

function formInput($action, $title, &$rows, &$buttons) {
	$rows = array_reverse($rows);
	$buttons = array_reverse($buttons);
	echo '
	<form action='.$action.' method=get>';
	foreach ($rows as $row) {
		$type = $row[0];
		if ($type == 'hidden') {
			$name = $row[1];
			$value = $row[2];
			echo '
			<input type=hidden name='.$name.' value="'.$value.'">';
		}
	}
	echo'
	<table cellSpacing=0 cellPadding=0 width="100%" align=center border=1>
	<tr>
	<td>
	<table bgcolor=#ffffcc cellSpacing=1 cellPadding=5 width="100%" border=0>
	<tr>
	<td class=head align=middle>
	<b>'.$title.'</b>
	</td>
	</tr>
	<tr>
	<td class=back>
	<table cellSpacing=0 cellPadding=0 width=100% align=center border=0>
	<tr>
	<td>
	<table bgcolor=#ffffcc cellSpacing=1 cellPadding=5 width="100%" border=0>';
	while(!is_null($row = array_pop($rows))) {

		$type = $row[0];
		$label = $row[1];
		if ($type != 'hidden' && $type != 'mail') {
			echo '
			<tr>
			<td class=cat align=right width=20%>
			<b> '.$label.': </b>
			</td>';
		}

		if($type == 'text') {
			$name = $row[2];
			$value = $row[3];
			echo '
			<td class=back>
			<input type=text size=40 name='.$name.' value="'.$value.'">
			</td>
			</tr>';
		}
		if($type == 'password') {
			$name = $row[2];
			echo '
			<td class=back>
			<input type=password name='.$name.'>
			</td>
			</tr>';
		}
		if($type == 'checkbox') {
			$numcolumns = $row[3];
			$width = 100/$numcolumns;
			$checkboxes = $row[2];
			$checkboxes = array_reverse($checkboxes);
			echo '
			<td class=back>
			<table cellSpacing=1 cellPadding=5 width="100%" border=0>';
			$i = 0;
			while(!is_null($checkbox = array_pop($checkboxes))) {
				$i++;
				$name = $checkbox[0];
				$value = $checkbox[1];
				$display = $checkbox[2];
				if($i%$numcolumns == 1) {
					echo '<tr>';
				}
				echo '<td class=back3 width='.$width.'%><input class=box type=checkbox name='.$name.' value='.$value.'>&nbsp;&nbsp;<b>'.$display.'</b></td>';
				if($i%$numcolumns == 0) {
					echo '</tr>';
				}
			}
			$i++;
			if($i%$numcolumns != 1) {
				while($i%$numcolumns != 1) {
					echo '<td class=back>&nbsp;</td>';
					$i++;
				}
				echo '</tr>';
			}
			echo '
			</table>
			</td>
			</tr>';
		}
		if($type == 'textarea') {
			$name = $row[2];
			echo '
			<td class=back>
			<textarea name='.$name.' rows=3 cols=72></textarea>
			</td>
			</tr>';
		}
		if($type == 'select') {
			$name = $row[2];
			$ddmenu = $row[3];
			$ddmenu = array_reverse($ddmenu);
			echo '
			<td class=back>
			<select name='.$name.'>';
			while(!is_null($option = array_pop($ddmenu))) {
				$value = $option[0];
				$display = $option[1];
				echo '
				<option value='.$value.'>'.$display.'</option>';
			}
			echo '
			</select>
			</td>
			</tr>';
		}

	}
	echo '
</table>
</td>
</tr>
</table>
</td>
</tr>
</table>
</td>
</tr>
</table>
<br>
<center>';
	while(!is_null($button = array_pop($buttons))) {
		$type = $button[0];
		$label = $button[1];
		$name = $button[2];
		echo '	
			<input type='.$type.' name='.$name.' value="'.$label.'">';
	}

	echo '
'.$mail.'
</center>
</form>';
}


/***********************************************************************************************************
**	function formMembers():
************************************************************************************************************/
function formMembers($id, $title, $page, $task, &$members, &$nonmembers, $label_nonmembers, $label_members) {
	echo '
<table cellSpacing=0 cellPadding=0 width="70%" align=left border=1>
<tr><td>
<table cellSpacing=1 cellPadding=5 width="100%" border=0>
<tr>
<td class=head align=middle>'.$title.'</td>
</tr>
<tr>
<td class=back>
<table class=border cellSpacing=0 cellPadding=0 width=100% align=center border=0>
<tr>
<td>
<table cellSpacing=1 cellPadding=5 width="100%" border=0>
<tr>
<td class=cat align=right><b>'.$label_nonmembers.'</b></td>
<td>&nbsp;</td>
<td class=cat align=left><b>'.$label_members.'</b></td>
</tr>
<td align=right rowspan=2>
<form action=index.php method=get>
<input type=hidden name=pagedest value='.$page.'>
<input type=hidden name=task value='.$task.'>
<input type=hidden name=id value='.$id.'>
<input type=hidden name=submit value=add_members>
<select name=nonmembers[] size=10 multiple>';
 	while(!is_null($row = array_pop($nonmembers))) {
		$nonmember_id = $row[0];
		$label = $row[1];
		echo '
		<option value="'.$nonmember_id.'">'.$label.'</option>';
	}
	echo '
</select>
</td>
<td align=center valign=bottom>
<input type=image border=0 name=add_member src="/hosts/images/add.gif" alt="Add">
</form>
</td>
<td align=left rowspan=2>
<form action=index.php method=get>
<input type=hidden name=pagedest value='.$page.'>
<input type=hidden name=task value='.$task.'>
<input type=hidden name=id value='.$id.'>
<input type=hidden name=submit value=remove_members>
<select name=members[] size=10 multiple>';
	while(!is_null($row = array_pop($members))) {
		$member_id = $row[0];
		$label = $row[1];
		echo '
		<option value='.$member_id.'>'.$label.'</option>';
	}
	echo '
</select>
</td>
</tr>
<td align=center valign=top>
<input type=image border=0 name=add_member src="/hosts/images/remove.gif" alt="Remove">
</form>
</td>
</tr>
<tr>
<td>&nbsp;</td>
<td align=center>
<form action=index.php method=get>
<input type=hidden name=pagedest value='.$page.'>
<input type=hidden name=task value=detail>
<input type=hidden name=id value='.$id.'>
<input type=submit name=submit value="View Detail">
<form>
</td>
<td>&nbsp;</td>
</tr>
</table>
</td>
</tr>
</table>
</td>
</tr>
</table>
</td>
</tr>
</table>';
}

/***********************************************************************************************************
**	function formSuccess():
************************************************************************************************************/
function formSuccess($message) {
	echo '
<table cellSpacing=0 cellPadding=0 width="50%" align=center border=1>
<tr><td class=head align=center>Success!</td>
</tr>
<tr>
<td>
<table cellSpacing=1 cellPadding=5 width="100%" border=0>
<tr>
<td align=middle><b>'.$message.'</b></td>
</tr>
</table>
</td>
</tr>
</table>';
}

/***********************************************************************************************************
**	function formError():
************************************************************************************************************/
function formError($message) {
	echo '
<table cellSpacing=0 cellPadding=0 width="50%" align=center border=1>
<tr><td class=head align=center>Error!</td>
</tr>
<tr>
<td>
<table cellSpacing=1 cellPadding=5 width="100%" border=0>
<tr>
<td class=warn align=middle><b>'.$message.'</b></td>
</tr>
</table>
</td>
</tr>
</table>';
}


/***********************************************************************************************************
**	function formDelete():
************************************************************************************************************/
function formDelete($message, $page, $id) {
	echo '
<table cellSpacing=0 cellPadding=0 width="50%" align=center border=1>
<tr><td class=head align=center>Warning!</td>
</tr>
<tr>
<td>
<table cellSpacing=1 cellPadding=5 width="100%" border=0>
<tr>
<td class=warn align=middle><b>'.$message.'</b></td>
</tr>
<tr>
<td align=middle>
<form action=index.php method=get>
<input type=hidden name=pagedest value='.$page.'>
<input type=hidden name=task value=delete>
<input type=hidden name=id value='.$id.'>
<input type=submit name=submit value="Confirm Delete">
<input type=submit name=submit value=Cancel>
</form>
</td>
</tr>
</table>
</td>
</tr>
</table>';
}

?>