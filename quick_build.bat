@rem
@rem Each step starts in the Jetspeed project root directory
@rem and must complete in the same directory
@rem

@echo off
set _JETSPEED_HOME=.
echo "JETSPEED_HOME:" %_JETSPEED_HOME%


maven allBuild

