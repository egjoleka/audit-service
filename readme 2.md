How to run the application:

1.Download cassandra from datastax
https://academy.datastax.com/planet-cassandra/cassandra

2.Follow the tutorial how to install cassandra
https://academy.datastax.com/downloads/welcome
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


(if cassandra does not stop/start)
bin/nodetool drain
 bin/cqlsh