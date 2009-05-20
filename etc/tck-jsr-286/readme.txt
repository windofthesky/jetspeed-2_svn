Running the JSR-286 TCK against Jetspeed-2 Enterprise Portal

1) Setup the Portlet TCK (only needs to be done once)

  a) Download the Portlet TCK
  
     Go to http://jcp.org/aboutJava/communityprocess/final/jsr286/index.html
     The TCK download link is provided there, which (currently) is at: 
     
       http://hnsp.inf-bb.uni-jena.de/tck/

     Note: this is a license agreement click-through link
     
     Save the downloaded portlet-tck070508.zip at a location of your convenience.
     
  b) Create a directory for the TCK and extract the downloaded portlet-tck070508.zip *inside* that directory.

  c) Add an environment variable pointing to your TCK installation directory called TS_HOME
     For example on Linux, add the following to your ~/.bashrc:
     
     export TS_HOME=~/portlet-tck070508
     
     Note: the TCK itself requires this TS_HOME environment variable.

  d) If on Linux, make the extracted $TS_HOME/bin/tsant shell script executable:

     chmod +x $TS_HOME/bin/tsant
     
  e) Configure $TS_HOME/bin/build.properties:

     Set your local timezone in $TS_HOME/bin/build.properties (default is US/Eastern).
     For example, if you are in Amsterdam, use:
   
       tz=Europe/Amsterdam

     Replace:
     
       j2ee.home.ri=${env.J2EE_HOME}
       j2ee.classes.ri=${j2ee.home.ri}/lib/j2ee.jar

     with:
     
       j2ee.home.ri=${TS_HOME}
       j2ee.classes.ri=${j2ee.home.ri}/lib/j2ee_1_3.jar

     Replace
          
       webapp.dir=C:/tmp/webapps
         
     with
       
       webapp.dir=${ts.home}/webapps
         
  f) Configure $TS_HOME/bin/ts.jte:

     Replace:
      
       portalURLFetcherMode=1
      
     with
      
       portalURLFetcherMode=0
      
     Replace:
     
       vendorTestsToURLMappingFile=/tmp/vendorTestsToURLMapping.xml

     with
     
       vendorTestsToURLMappingFile=${TS_HOME}/bin/jetspeedTestsToURLMapping.xml
     
  g) Copy the jetspeedTestsToURLMapping.xml to $TS_HOME/bin:
  
     cp jetspeedTestsToURLMapping.xml $TS_HOME/bin

2) Setup Jetspeed for running the TCK

  a) build and deploy Jetspeed, for example using:
   
     mvn jetspeed:mvn -Dtarget=min
  
  b) If you don't have ANT installed on your machine, install Apache ANT. (See http://ant.apache.org/.)
     Please make sure that $ANT_HOME is properly set. 
     
  c) Deploy the TCK portlet applications to jetspeed.
     If you installed jetspeed under $CATALINA_HOME, then execute the following:
   
     $TS_HOME/bin/tsant deploy.all -Dwebapp.dir=$CATALINA_HOME/webapps/jetspeed/WEB-INF/deploy
     
  d) Unzip the TCK test psml files to the jetspeed installation:
  
     unzip tck-jsr286-psml.zip $CATALINA/HOME/webapps/jetspeed/WEB-INF/pages/
     
  e) Startup Jetspeed once to ensure the TCK portlet applications are deployed

3) Running the TCK against Jetspeed
  
  a) Start Tomcat
  
  b) Start the TCK gui:
  
     $TS_HOME/bin/tsant gui
     
  c) In the gui, create a new TCK work directory somewhere
  
       Menu: File|New Work Directory
  
  d) Finally, in the gui run the TCK
  
      Menu: Run Tests|Start
     
