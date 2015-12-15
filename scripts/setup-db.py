# -*- coding: utf-8 -*-
#TODO: fazer com que estes valores possam ser adquiridos através de um ficheiro xml
import sys
import mysql.connector as mariadb

mariadb_connection = mariadb.connect(user='root', password='admin613SSH')
cursor = mariadb_connection.cursor()
database = 'UsersClassifications'


print "The execution of this script will lead removal of all information currently stored in the database.\nAre your sure you want to proceed? (y/n)"

input = raw_input()

if input == 'n' or input == 'N':
	print "Exiting..."
	sys.exit()
	

cleanup = "DROP DATABASE "+database
cursor.execute(cleanup)
mariadb_connection.commit()

print 'Setting up all CycleOurCity databases...'




cmd1 = "CREATE DATABASE IF NOT EXISTS "+database +" CHARACTER SET  = 'utf8' COLLATE = 'utf8_general_ci';"
cmd2 = 'USE '+database;
cursor.execute(cmd1)
cursor.execute(cmd2)

# Phase 1 - User Tables
print "Starting Phase 1 - Users' tables"

users = "CREATE TABLE IF NOT EXISTS users (Id int(11) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY, Username VARCHAR(20) NOT NULL UNIQUE, Password varchar(128) NOT NULL, Email varchar(128) NOT NULL UNIQUE, Salt varchar(64) NOT NULL, CreatedAt timestamp not null DEFAULT CURRENT_TIMESTAMP, LastVisit timestamp DEFAULT 0)"

users_emails = "CREATE TABLE IF NOT EXISTS users_emails (Id INT(11) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY, IdUser int(11) unsigned NOT NULL, FOREIGN KEY (IdUser) REFERENCES users(Id) ON DELETE CASCADE, RecoveryPassword tinyint(1) NOT NULL, Token varchar(160) NOT NULL UNIQUE, ExpirationDate timestamp NOT NULL DEFAULT 0)"

print "... creating all the users tables ..."
cursor.execute(users)
cursor.execute(users_emails)

# PHASE 2 - Factors
print "Starting Phase 2 - Criteria factors"
##Elevation
table = 'elevation'
factors = [['Subida impraticavel para a marioria das pessoas', '8'], ['Subida com esforço', '5'], ['Subida sem esforço', '1.5'], ['Plano', '1'],['Descida suave', '0.75'],['Descida Acentuada','0.6']]

create = 'CREATE TABLE IF NOT EXISTS '+table+' ('+'Id int(11) unsigned AUTO_INCREMENT PRIMARY KEY NOT NULL, '+'Name varchar(150), '+'Factor double NOT NULL)'
clean  = 'TRUNCATE table '+table
update = "INSERT INTO "+table+" (Name, Factor) VALUES (%s, %s)"

print '... Setting up '+table+' table ...'
cursor.execute(create)
cursor.execute(clean)
cursor.executemany(update, factors)

##Pavement
table = "pavement"
factors = [['Empedrado, calçada ou terra batida em boas condições','0.5'],['Empedrado, calçada ou terra batida difícil de pedalar','0.5'],['Asfalto/Betuminoso em más condições (buracos, desníveis perigosos)','0.5'],['Asfalto/betuminoso em boas condições','0.5']]

create = "CREATE TABLE IF NOT EXISTS "+table+"(Id INT(11) Unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY, Name VARCHAR(70), Factor DOUBLE NOT NULL)"
clean  = "TRUNCATE TABLE " +table
update = "INSERT INTO "+table+" (Name, Factor) VALUES (%s, %s)"

print '... Setting up '+table+' table ...'
cursor.execute(create)
cursor.execute(clean)
cursor.executemany(update, factors)

##Rails
table = "rails"
factors = [['Não tem carris','0.5'],['Tem carris ao nível do paviment','0.5'],['Tem carris salientes','0.5']]

create = "CREATE TABLE IF NOT EXISTS "+table+" (Id INT(11) Unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY, Name VARCHAR(40), Factor DOUBLE NOT NULL)"
clean  = "TRUNCATE TABLE "+table
update = "INSERT INTO "+table+" (Name, Factor) VALUES (%s, %s)"

print '... Setting up '+table+' table ...'
cursor.execute(create)
cursor.execute(clean)
cursor.executemany(update, factors)


##Safety
table = "safety"
factors = [['Tráfego motorizado normalmente acima dos 50 Km/h','6'],['Tráfego motorizado normalmente não passa dos 50 Km/h','3'],['Tráfego motorizado intenso, velocidades normalmente não passam dos 30 Km/h','1.5'],['Tráfego motorizado pouco intenso, velocidades normalmente não passam dos 30 Km/h','1.1'],['Nenhum tráfego motorizado permitido, peões frequentemente no caminho','0.75'],['Nenhum tráfego motorizado permitido, poucos ou nenhuns peões no caminho','0.6']]

create = "CREATE TABLE IF NOT EXISTS "+table+" (Id INT(11) Unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY, Name VARCHAR(150), Factor DOUBLE NOT NULL)"
clean  = "TRUNCATE TABLE "+table
update = "INSERT INTO "+table+" (Name, Factor) VALUES (%s, %s)"

print '... Setting up '+table+' table ...'
cursor.execute(create)
cursor.execute(clean)
cursor.executemany(update, factors)



##PHASE 3 - Street Edges Tables
print 'Starting Phase 3 - Street Edge Tables';

streetedges  = "CREATE TABLE IF NOT EXISTS streetedges ("
streetedges += "Id int(11) unsigned NOT NULL PRIMARY KEY, "
streetedges += "Name varchar(150), "
streetedges += "FromVertexLatitude double, "
streetedges += "FromVertexLongitude double, "
streetedges += "ToVertexLatitude double, "
streetedges += "ToVertexLongitude double, "
streetedges += "Geometry varchar(709) NOT NULL)"

streetedge_elevation  = "CREATE TABLE IF NOT EXISTS streetedge_elevation ("
streetedge_elevation += "Id int(11) unsigned NOT NULL PRIMARY KEY, "
streetedge_elevation += "IdStreetEdge int(11) unsigned not null, "
streetedge_elevation +=	"FOREIGN KEY(IdStreetEdge) REFERENCES streetedges(Id) ON DELETE CASCADE, "
streetedge_elevation += "IdElevation int(11) unsigned not null, "
streetedge_elevation += "FOREIGN KEY (IdElevation) REFERENCES elevation(Id) ON DELETE CASCADE, "
streetedge_elevation += "IdUser int(11) unsigned not null, "
streetedge_elevation += "FOREIGN KEY (IdUser) references users(Id) ON DELETE CASCADE, "
streetedge_elevation += "RateAt timestamp DEFAULT CURRENT_TIMESTAMP)"

streetedge_pavement  = "CREATE TABLE IF NOT EXISTS streetedge_pavement ("
streetedge_pavement += "Id int(11) unsigned NOT NULL PRIMARY KEY, "
streetedge_pavement += "IdStreetEdge int(11) unsigned not null, "
streetedge_pavement += "FOREIGN KEY(IdStreetEdge) REFERENCES streetedges(Id) ON DELETE CASCADE, "
streetedge_pavement += "IdPavement int(11) unsigned not null, "
streetedge_pavement += "FOREIGN KEY (IdPavement) REFERENCES pavement(Id), "
streetedge_pavement += "IdUser int(11) unsigned not null, "
streetedge_pavement += "FOREIGN KEY (IdUser) references users(Id) ON DELETE CASCADE, "
streetedge_pavement += "RateAt timestamp DEFAULT CURRENT_TIMESTAMP)"

streetedge_rails  = "CREATE TABLE IF NOT EXISTS streetedge_rails ("
streetedge_rails += "Id int(11) unsigned NOT NULL PRIMARY KEY, "
streetedge_rails += "IdStreetEdge int(11) unsigned not null, "
streetedge_rails += "FOREIGN KEY(IdStreetEdge) REFERENCES streetedges(Id) ON DELETE CASCADE, "
streetedge_rails += "IdRails int(11) unsigned not null, "
streetedge_rails += "FOREIGN KEY (IdRails) REFERENCES rails(Id) ON DELETE CASCADE, "
streetedge_rails += "IdUser int(11) unsigned not null, "
streetedge_rails += "FOREIGN KEY (IdUser) references users(Id) ON DELETE CASCADE, "
streetedge_rails += "RateAt timestamp DEFAULT CURRENT_TIMESTAMP)"

streetedge_safety  = "CREATE TABLE IF NOT EXISTS streetedge_safety ("
streetedge_safety += "Id int(11) unsigned NOT NULL PRIMARY KEY, "
streetedge_safety += "IdStreetEdge int(11) unsigned not null, "
streetedge_safety += "FOREIGN KEY(IdStreetEdge) REFERENCES streetedges(Id) ON DELETE CASCADE, "
streetedge_safety += "IdSafety int(11) unsigned not null, "
streetedge_safety += "FOREIGN KEY (IdSafety) REFERENCES safety(Id), "
streetedge_safety += "IdUser int(11) unsigned not null, "
streetedge_safety += "FOREIGN KEY (IdUser) references users(Id) ON DELETE CASCADE, "
streetedge_safety += "RateAt timestamp DEFAULT CURRENT_TIMESTAMP)"

streetedge_consolidatedelevation  = "CREATE TABLE IF NOT EXISTS streetedge_consolidatedelevation ("
streetedge_consolidatedelevation += "Id INT(11) UNSIGNED NOT NULL PRIMARY KEY, "
streetedge_consolidatedelevation += "IdElevation INT(11) UNSIGNED NOT NULL, "
streetedge_consolidatedelevation += "FOREIGN KEY (IdElevation) REFERENCES elevation(Id) ON DELETE CASCADE, "
streetedge_consolidatedelevation += "Rate_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

streetedge_consolidatedsafety  = "CREATE TABLE IF NOT EXISTS streetedge_consolidatedsafety ("
streetedge_consolidatedsafety += "Id INT(11) UNSIGNED NOT NULL PRIMARY KEY, "
streetedge_consolidatedsafety += "IdSafety INT(11) UNSIGNED NOT NULL, "
streetedge_consolidatedsafety += "FOREIGN KEY (IdSafety) REFERENCES safety(Id) ON DELETE CASCADE, "
streetedge_consolidatedsafety += "Rate_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

trips  = "CREATE TABLE IF NOT EXISTS trips ("
trips += "Id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, "
trips += "IdUser INT(11) UNSIGNED NOT NULL, "
trips += "FOREIGN KEY (IdUser) REFERENCES users(Id) ON DELETE CASCADE, "
trips += "Name VARCHAR(100) NOT NULL,"
trips += "SavedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"

trips_streetedges  = "CREATE TABLE IF NOT EXISTS trips_streetedges ("
trips_streetedges += "Id int(11) UNSIGNED not null AUTO_INCREMENT PRIMARY KEY, "
trips_streetedges += "IdTrip INT(11) UNSIGNED NOT NULL, "
trips_streetedges += "FOREIGN KEY (IdTrip) REFERENCES trips(Id) ON DELETE CASCADE, "
trips_streetedges += "IdStreetEdge int(11) unsigned not null, "
trips_streetedges += "FOREIGN KEY(IdStreetEdge) REFERENCES streetedges(Id) ON DELETE CASCADE, "
trips_streetedges += "BicycleMode TINYINT(1) NOT NULL)"

cursor.execute(streetedges)
cursor.execute(streetedge_rails)
cursor.execute(streetedge_safety)
cursor.execute(streetedge_pavement)
cursor.execute(streetedge_elevation)
cursor.execute(trips)
cursor.execute(trips_streetedges)

mariadb_connection.commit()
mariadb_connection.close()
print '... Setup finished. Exiting now.'
