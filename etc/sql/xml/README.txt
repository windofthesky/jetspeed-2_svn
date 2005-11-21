The XML mockups included in this directly are a suggested alternate way to populate the default database data.
I think we have two choices here:

(a) DDL UTILS DML support (http://db.apache.org/ddlutils/) db population scripts (I can't actually find DML support here!)
    
(b) XML Files imported directly using the Jetspeed API

Where as I really like DDL UTILS, and it has a quicker maintainance model, I find the ability to have standard XML files 
for importing or persistence model easier to understand for end users.
Also, this will work nicely for importing the database at runtime during installs.

So this XML: 

    <roles>admin, guest, user</roles>

would map to:

	roleManager.addRole("admin");
	roleManager.addRole("guest");
	roleManager.addRole("user");
	
and so forth for new users, permissions, profiling rules, capabilities, etc:	
	
     <user name='joe' password='xxx' template='/_user/template/'>
       <roles>admin, user</roles>
       <groups>dev</groups>
       <user-info>
           <info>
               <name>user.first.name</name>
               <value>Joseph</value>
           </info>
           <info>
               <name>user.last.name</name>
               <value>Stalin</value>
           </info>
       </user-info>
       
       <preferences/>
       
       <folder-template>/_user/template/</folder-template>
       <profile-rules>
           <rule name='page'>userrolefallback</rule>
           <rule name='page'>userrolefallback</rule>
       </profile-rules>
    </user>
	