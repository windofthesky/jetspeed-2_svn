#!d:/perl/bin/perl 


use DBI;
use strict;
my $dsn = "DBI:mysql:hosts:localhost";
my $user = "root";
my $passwd = "";
my $dbh = DBI->connect($dsn, $user, $passwd, {'RaiseError' => 1});

while (<DATA>) {
	eval { $dbh->do("DROP TABLE $_") };
	print "drop cities failed: $@\n" if $@;
}

if ($dbh) {
    $dbh and $dbh->disconnect();
}


__DATA__
hosts
services
interfaces
host_service
host_interface