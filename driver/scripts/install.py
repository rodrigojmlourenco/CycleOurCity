import os

jar = "../target/driver-0.0.1-SNAPSHOT.jar"
pom = "../pom.xml"

mvn_install_cmd  = "mvn install:install-file "
mvn_install_cmd += "-Dfile=../target/driver-0.0.1-SNAPSHOT.jar "
mvn_install_cmd += "-DgroupId=org.cycleourcity "
mvn_install_cmd += "-DartifactId=driver "
mvn_install_cmd += "-Dversion=0.0.1 "
mvn_install_cmd += "-Dpackaging=jar -DgeneratePom=true"

mvn_install_simplified = "mvn install:install-file -Dfile="+jar+" -DpomFile="+pom

os.system("mvn -f ../pom.xml package")
os.system(mvn_install_simplified)

