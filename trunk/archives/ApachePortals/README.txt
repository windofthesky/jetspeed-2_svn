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

$Id$

The Apache Portals Website Instructions
----------------------------------------

The Apache Portals web site is based on .xml files which are transformed
into .html files using Maven.

http://maven.apache.org/

Once you have the site checked out locally, cd into your
portals-site directory and execute:

maven site

This will build the documentation into the target/docs/ directory. The output
will show you which files got re-generated.

If you would like to make modifications to the web site documents,
you simply need to edit the files in the xdocs/ directory.

Once you have built your documentation and confirmed that your changes are
ok, you can check your .xml files back into CVS.

To deploy the site execute:

maven site:deploy

To do this you need an account on the jakarta.apache.org machine!!

