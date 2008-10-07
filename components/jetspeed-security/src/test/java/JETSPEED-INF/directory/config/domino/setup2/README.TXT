In order for this schema setup to work, modify the Domino schema 
so that uniqueMember becomes an optional attribute for 
organizationPerson

Domino doesn't support putting role/group membership on the user
object by default.

objectclasses: (
  2.5.6.7
  NAME 'organizationalPerson'
  DESC 'Defines entries for people employed by or associated with an organization.'
  SUP person
  STRUCTURAL
  MAY ( uniqueMember $ title $ x121Address $ registeredAddress $ destinationIndicator $ preferredDeliveryMethod $ telexNumber $ teletexTerminalIdentifier $ internationaliSDNNumber $ facsimileTelephoneNumber $ street $ postalAddress $ postalCode $ postOfficeBox $ physicalDeliveryOfficeName $ ou $ st $ l )
  )
