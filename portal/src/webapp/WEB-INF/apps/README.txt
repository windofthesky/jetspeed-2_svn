Copyright 2004 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

The apps directory holds Portlet Applications stored locally inside Jetspeed.
This is an alternative to storing Portlet Applications in another web application, which requires a cross-context servlet invoker (as of Servlet spec 2.3). Some application servers do not support cross-context Servlet invokers, such as Weblogic 8.1 and prior versions. If your application server doesn't support cross-context, recommend deploying your portlet applications here. See the Portlet Application Manager documentation for details on how to deploy portlet applications internally.