@rem
@rem Each step starts in the Jetspeed project root directory
@rem and must complete in the same directory
@rem

@echo off
set _JETSPEED_HOME=.
echo "JETSPEED_HOME:" %_JETSPEED_HOME%


echo
echo +++++++++++++++++++++++++++
echo 0. Clean targets
echo +++++++++++++++++++++++++++

call maven allClean

echo

echo +++++++++++++++++++++++++++
echo 1. Recreate database
echo +++++++++++++++++++++++++++

cd portal
call maven db.recreate
call maven db.test.recreate
cd ..

echo
echo +++++++++++++++++++++++++++
echo 2. Build all Jetspeed sub projects
echo +++++++++++++++++++++++++++

call maven allBuild

echo
echo +++++++++++++++++++++++++++
echo 3. Install into Catalina Shared
echo +++++++++++++++++++++++++++

call maven catalina:base-shared
call maven catalina:shared

echo
echo +++++++++++++++++++++++++++
echo 4. Deploy Jetspeed Portal WAR into Catalina
echo +++++++++++++++++++++++++++

cd portal
call maven deploy
cd ..

echo
echo +++++++++++++++++++++++++++
echo 5. Install the demo web application into Catalina
echo +++++++++++++++++++++++++++

cd portal
call maven pam.deploy 
cd ..

