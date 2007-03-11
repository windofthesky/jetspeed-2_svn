#!/bin/bash

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


# Script to stop tomcat, update build and deploy jetspeed, and start tomcat
# It also sends email if jetspeed does not build successfully

# Settings
export JAVA_HOME=/home/phil/20051223/soft/bin/java
export JRE_HOME=/home/phil/20051223/soft/bin/java/jre
export CATALINA_HOME=/home/phil/20051223/opensource/bin/apache-tomcat-5.5.17
export JAVA_OPTS="-Xms32m -Xmx32m -XX:PermSize=32m -XX:MaxPermSize=32m"
export M2_HOME=/home/phil/20051223/opensource/bin/maven-2.0.4

# Stop tomcat
$CATALINA_HOME/bin/shutdown.sh

# Another technique to shutdown with force (Used on machines with low memory)
#CATALINA_PID=`ps aux | grep catalina | head -2 | head -1 | awk '{print $2}'`
#kill $CATALINA_PID

# Suggestions for a Super clean environment
#rm -rf ~/.m2/repository
#rm -rf /tmp/j2
#rm -rf $CATLINA_HOME
#tar xzf apache-tomcat-5.5.17.tar.gz
#cp tomcat-users.xml $CATLINA_HOME/conf

# Update, clean, build and deploy jetspeed
cd /home/phil/20051223/opensource/svnProjects/jetspeed-2
svn update
$M2_HOME/bin/mvn clean
$M2_HOME/bin/mvn -P tomcat

# Send email when build fails
grep "ERROR" /tmp/deployPortalsJetspeed2.log
ERRORSTATUS=$?
if [ $ERRORSTATUS -ne 1 ]; then
    mail -s "Jetspeed 2 BUILD FAILED" phil@linux.site < /tmp/deployPortalsJetspeed2.log
fi

# Start tomcat
$CATALINA_HOME/bin/startup.sh
