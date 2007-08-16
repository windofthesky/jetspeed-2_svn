This sample uses the following custom attributes and objectClasses

attributetype ( 3.16.840.1.113730.3.1.13
	NAME 'memberOf'
	DESC 'memberOf'
	EQUALITY caseIgnoreIA5Match
	SYNTAX 1.3.6.1.4.1.1466.115.121.1.26{256} )

objectclass ( 3.16.840.1.113730.3.2.147
	NAME 'JetspeedRole'
	DESC 'JetspeedRole'
	SUP groupOfNames STRUCTURAL
	MAY	( memberOf ) )

objectclass ( 3.16.840.1.113730.3.2.148
	NAME 'JetspeedGroup'
	DESC 'JetspeedGroup'
	SUP groupOfUniqueNames STRUCTURAL
	MAY	( memberOf ) )

objectclass ( 3.16.840.1.113730.3.2.149
	NAME 'JetspeedUser'
	DESC 'JetspeedUser'
	SUP person STRUCTURAL
	MUST	( uid )
	MAY	( memberOf ) )



