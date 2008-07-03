cd applications/jetspeed-demo
mvn -o jetspeed-db:init -P demo 
mvn -o jetspeed-deploy:deploy -P demo
cd ../..
