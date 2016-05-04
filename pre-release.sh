#!/usr/bin/env bash
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
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g applications/jetspeed-demo/jetspeed-mvn-demo-pom.xml
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g enterprise/ear-full/pom.xml
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g enterprise/ear-min/pom.xml
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g enterprise/pom.xml
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g jetspeed-archetype/pom.xml
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g jetspeed-archetype/src/main/resources/META-INF/maven/archetype-metadata.xml
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g jetspeed-archetype/src/main/resources/archetype-resources/__rootArtifactId__-portal/pom.xml
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g jetspeed-archetype/src/main/resources/archetype-resources/pom.xml
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g jetspeed-installer/BUILD.txt
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g jetspeed-installer/etc/ant-installer/antinstall-config.xml
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g jetspeed-installer/etc/ant-installer/build.xml
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g jetspeed-installer/etc/database/database.properties.template
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g jetspeed-installer/jetspeed-mvn-ant-installer-pom.xml
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g jetspeed-installer/jetspeed-mvn-database-pom.xml
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g jetspeed-installer/jetspeed-mvn-tomcat-portal-pom.xml
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g jetspeed-installer/pom.xml
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g jetspeed-mvn-db-init-pom.xml
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g src/site/site.xml
sed -i.bak s/2.3.1-SNAPSHOT/2.3.1/g jetspeed-portal-resources/src/main/resources/conf/jetspeed/jetspeed.properties
find . -name "*.bak" -type f -delete
