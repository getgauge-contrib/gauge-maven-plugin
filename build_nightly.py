from datetime import datetime
from xml.dom import minidom
from subprocess import call

pom_file_name = 'pom.xml'
pom = minidom.parse(pom_file_name)
childs = pom.getElementsByTagName('version')
version = childs[0].firstChild
version.data =  "{0}-nightly-{1}".format(version.data, datetime.today().strftime('%Y-%m-%d'))
pom_file = open(pom_file_name, 'w')
pom_file.write(pom.saveXML(None).replace('<?xml version="1.0" ?>', ''))
pom_file.close()

call(['mvn', 'package'])
call(['mvn', '-Doutput=pom.xml', 'help:effective-pom'])
