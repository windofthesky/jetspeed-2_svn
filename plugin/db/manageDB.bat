@ECHO OFF
REM ***
@java -classpath %CLASSPATH%;hsqldb.jar org.hsqldb.util.DatabaseManager -url jdbc:hsqldb:Registry