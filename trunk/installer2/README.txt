The best way to build the jetspeed installer is via maven (see below).  

MAVEN BUILD:
If you are using the maven plugin, there are three tasks defined which help out:
j2:buildDerbyInstaller
j2:buildMultiInstaller
j2:cleanInstaller
Run these from the top level of the jetspeed project.
Make sure that you've done a build in both the bridges and jetspeed2 projects.
The installer pulls the bridges war files from the local repo.

INSTALL:
The installer is run on the target machine by typing

java -jar Jetspeed2.0-MultiDB-install.jar
or
java -jar Jetspeed2.0-derby-install.jar

depending on which installer you've created.

For the Multi-DB installer you will need to supply
a username
a password
a JDBC connection string 
a JDBC driver name
the location of a valid JDBC driver.  The installer will copy this to the installed location.

Finally the database must be empty because the installer
does not know how to drop tables.
 
