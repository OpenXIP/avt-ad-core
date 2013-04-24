:: Installing non-standard lib to local maven repo
mvn install:install-file  -Dfile=%1  -DgroupId=%2  -DartifactId=%3  -Dversion=%4  -Dpackaging=jar  -DgeneratePom=true