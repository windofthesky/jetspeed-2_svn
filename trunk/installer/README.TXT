How to build the IzPack installer for Jetspeed.

Make sure "maven allBuild" has been run before attmepting to build the installer.

1. You will probably need to download IzPack, http://www.izforge.com/izpack/index.php?page=download,
as it is GPL and will probably not be available in from your remote repo.

2. Once downloaded, copy the standalone-compiler.jar to ${MAVEN_HOME}/repository/izpack/jars
and rename it to standalone-compiler-3.6.1.jar.  You will most likely have to create the
izpack/jars directory.

3. There are 2 properties you may need to override in your ${USER_HOME}/build.properties file
in order to build.

- tomcat.dist (defaults to ${user.home}/downloads/jakarta-tomcat-5.0.30.zip)
  
  This is the tomcat distribution to be packed.  Right now it HAS to be jakarta-tomcat-5.0.30.
  
- ant.home (defaults to /usr/share/ant)
  
  Location of your ant install.  It will be packed up for use by the install program when run
  by the end user.
  
4. run "maven" as the default goal is the "build.installer"

You can test the installer by running "java -jar target/j2-installer.jar"
