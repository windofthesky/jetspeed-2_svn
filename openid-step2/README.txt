Copyright:
-----------------------------------------------------------------------
Copyright 2009 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Step2 Implementation adapted from release 1-SNAPSHOT here:
-----------------------------------------------------------------------
URL: http://step2.googlecode.com/svn/code/java/trunk
Repository Root: http://step2.googlecode.com/svn
Repository UUID: c427a029-b451-0410-ae65-13123b152969
Revision: 478
Node Kind: directory
Schedule: normal
Last Changed Author: dirk.balfanz
Last Changed Rev: 478
Last Changed Date: 2010-01-26 14:29:33 -0700 (Tue, 26 Jan 2010)

Minor Source Adaptations:
-----------------------------------------------------------------------
1. Strip unused classes and deps from common module project.
2. Remove Guice assembly annotations.
3. Utilize standard Java collections API in place of Google collections.
4. Make compatible with released openid4java version 0.9.5.
5. Set release version to '0'.
6. Simplify Maven2 POMs.
7. Compile with Java 1.5 compatibility.
8. Strip LGPL JUG 1.1.2 library from dependencies, (apparently used
   only for XRI, SAML, and test cases), stub JUG UUIDGenerator.
