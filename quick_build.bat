@rem
@rem Each step starts in the Jetspeed project root directory
@rem and must complete in the same directory
@rem

@echo off

REM Copyright 2004 The Apache Software Foundation
REM
REM Licensed under the Apache License, Version 2.0 (the "License");
REM you may not use this file except in compliance with the License.
REM You may obtain a copy of the License at
REM 
REM     http://www.apache.org/licenses/LICENSE-2.0
REM 
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.

set _JETSPEED_HOME=.
echo "JETSPEED_HOME:" %_JETSPEED_HOME%


maven allBuild

