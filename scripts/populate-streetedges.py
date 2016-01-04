import sys
import mysql.connector as mariadb
import xml.etree.ElementTree as ET

config_path = "./config.xml"
map_path		= "./map.osm"

config = ET.parse(config_path).getroot()
osm	 = ET.parse(map_path).getroot()

repository 	= None

#Database connection fields
_user 		= None
_password 	= None
_database	= None

for child in config:
	if child.tag == 'repository':
		repository = child;

for child in repository:
	if child.tag == 'user':
		_user = child.attrib['val']
	elif child.tag == 'password':
		_password = child.attrib['val']
	elif child.tag == 'database':
		_database = child.attrib['val']
	else:
		print "Error, unknown tag "+child.tag
		exit

#mariadb_connection = mariadb.connect(user=_user, password=_password)
#cursor = mariadb_connection.cursor()
#database = _database

#cmd = 'USE '+database;
#cursor.execute(cmd)
#print "Connected to the database "+_database+" as user '"+_user+"'..."

#street_edges = osm.findall('way')

#for edge in street_edges:
#	id = edge.

command = "java -Xmx2G --build . --basePath ./graph"

os.system(command)
