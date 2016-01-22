import os
import wget
import xml.etree.ElementTree as ET

mapURL = "http://overpass-api.de/api/map?bbox="
config_path = "./config.xml"

config = ET.parse(config_path).getroot()

phase2_config = None
bottom_left = None
top_right = None
for child in config:
	if child.tag == 'map':
		phase2_config = child
		break

for child in phase2_config:
	if child.tag == 'bottom-left':
		bottom_left = child
	elif child.tag == 'top-right':
		top_right = child
	else:
		print "error, unknown child "+child.tag
		exit

mapURL = mapURL + bottom_left.attrib['lon'] + "," + bottom_left.attrib['lat'] + ",";
mapURL = mapURL + top_right.attrib['lon'] + "," + top_right.attrib['lat'];

print "Downloading area map from "+mapURL
map_file = wget.download(mapURL)

if not os.path.exists('../resources'):
	os.makedirs('../resources')

if not os.path.exists('../resources/city'):
		os.makedirs('../resources/city')

if os.path.exists('../resources/city/map.osm'):
	os.remove('../resources/city/map.osm')


basedir = os.environ['HOME'] + "/otp" 
dist = basedir+'/map.osm'

if not os.path.exists(basedir):
	os.makedirs(basedir)


os.rename(map_file, dist)

try:
	os.remove('map')
	os.remove('map.osm')
except OSError:
	pass

if not os.path.exists(basedir+'/graph'):
	os.makedirs(basedir+'/graph')
	os.makedirs(basedir+'/graph/new')
	os.makedirs(basedir+'/graph/old')
