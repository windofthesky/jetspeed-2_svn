To Run the command line tool to infuse automatic registration of portlet app at app server startup:

java -jar target/jetspeed-deploy-tools-{version}.jar [-s] source.war destination.war

# for example, the 2.0 release would be
java -jar target/jetspeed-deploy-tools-2.0.jar [-s] source.war destination.war

-s: stripLoggers - remove commons-logging[version].jar and/or log4j[version].jar from war
                   required when targetting application servers like JBoss
