create table test_user
(
	id int identity,
	login_name varchar,
	first_name varchar, 
	last_name varchar
);
go

create table test_group
(
	id int identity,
	name varchar
);
go

create table test_user_group
(
	id int identity,
	user_id int,
	group_id int,
	foreign key (user_id) references test_user(id),
	foreign key (group_id) references test_group(id)
);
go
