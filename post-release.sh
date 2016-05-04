#!/usr/bin/env bash
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g applications/jetspeed-demo/jetspeed-mvn-demo-pom.xml
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g enterprise/ear-full/pom.xml
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g enterprise/ear-min/pom.xml
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g enterprise/pom.xml
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g jetspeed-archetype/pom.xml
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g jetspeed-archetype/src/main/resources/META-INF/maven/archetype-metadata.xml
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g jetspeed-archetype/src/main/resources/archetype-resources/__rootArtifactId__-portal/pom.xml
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g jetspeed-archetype/src/main/resources/archetype-resources/pom.xml
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g jetspeed-installer/BUILD.txt
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g jetspeed-installer/etc/ant-installer/antinstall-config.xml
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g jetspeed-installer/etc/ant-installer/build.xml
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g jetspeed-installer/etc/database/database.properties.template
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g jetspeed-installer/jetspeed-mvn-ant-installer-pom.xml
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g jetspeed-installer/jetspeed-mvn-database-pom.xml
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g jetspeed-installer/jetspeed-mvn-tomcat-portal-pom.xml
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g jetspeed-installer/pom.xml
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g jetspeed-mvn-db-init-pom.xml
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g src/site/site.xml
sed -i.bak s/2.3.1/2.3.2-SNAPSHOT/g jetspeed-portal-resources/src/main/resources/conf/jetspeed/jetspeed.properties
find . -name "*.bak" -type f -delete
