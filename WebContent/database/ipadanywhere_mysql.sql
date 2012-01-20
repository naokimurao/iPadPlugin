INSERT INTO ofVersion (name, version) VALUES ('ipadanywhere', 1);

CREATE TABLE ofipadanywhere (
  siteID bigint(20) NOT NULL,
  name varchar(255) NOT NULL,
  dalUrl varchar(100) NOT NULL,
  itslink1 varchar(50) default NULL,
  itslink2 varchar(50) default NULL,
  itslinkPort varchar(10) default NULL,
  itslinkCos varchar(10) default NULL,
  lineStatusOnLoad varchar(10) default NULL,
  voiceCallset varchar(30) default NULL,
  vscUseHandsets34 varchar(10) default NULL,
  vscConsoleStartRange1 varchar(10) default NULL,
  vscConsoleCount1 varchar(10) default NULL,
  vscConsoleStartRange2 varchar(10) default NULL,
  vscConsoleCount2 varchar(10) default NULL,
  vscConsoleStartRange3 varchar(10) default NULL,
  vscConsoleCount3 varchar(10) default NULL,
  vscConsoleStartRange4 varchar(10) default NULL,
  vscConsoleCount4 varchar(10) default NULL,
  PRIMARY KEY  (siteID)
); 

CREATE TABLE ofipadanywhereuser (
  username varchar(255) NOT NULL,
  siteID bigint(20) default NULL,
  PRIMARY KEY  (username)
); 