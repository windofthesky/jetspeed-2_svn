@ECHO OFF
REM ***
@java -classpath "%CLASSPATH%;../../../src/webapp/WEB-INF/db/hsql/hsqldb.jar" org.hsqldb.util.DatabaseManager -url jdbc:hsqldb:Registry