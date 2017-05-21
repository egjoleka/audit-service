# How to run the application

1.Download cassandra from datastax
https://academy.datastax.com/planet-cassandra/cassandra

2.Follow the tutorial how to install cassandra
https://academy.datastax.com/downloads/welcome
(90 seconds)

2.1 Create a cluster with one node called test

3.Checkout the project Eclipse/IntelliJ

4.Gradle ecl

5.Start cassandra
$ cd install_location
$ bin/dse cassandra 

6.Verify that cassandra is running
install_location
/bin/nodetool status

7.Run the DB script izettle_prod.cql 
8.Run Main

(if cassandra does not stop/start)
bin/nodetool drain
bin/cqlsh


# Features

1.Register User

2.User login

3.View Audits

4.Password Policy(Strength, length, similarity, encryption PBDFK2)

5.API documentation RAML

6.HTTP client to consume the API(build the project, grab the jar and just import in client application)

7.AngularJS UI app boundled inside(not fully completed as time did not permit)

# Technologies used

1.Java8

2.Gradle

3.Cassandra(production)(DSE latest)

4.Embedded Cassandra(Testing/JUNIT)(2.1.x)

5.Embedded Jetty

6.REST(CXF)

7.GIT