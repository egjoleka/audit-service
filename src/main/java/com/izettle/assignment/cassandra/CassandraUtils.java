package com.izettle.assignment.cassandra;

import java.util.List;

import com.datastax.driver.core.policies.*;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.SocketOptions;

//CREATE KEYSPACE izettle_service WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1};

public class CassandraUtils {

    private static final Logger cLogger = LoggerFactory.getLogger(CassandraUtils.class);
    private static final String CASSANDRA_NODES = "izettle.cassandra.nodes";
    private static final String CASSANDRA_USER_NAME = "izettle.cassandra.username";
    private static final String CASSANDRA_PASSWORD = "izettle.cassandra.password";
    private static final String CASSANDRA_LOCALDC = "izettle.cassandra.local.datacenter";

    //The commented lines can be used for a proper production cassandra environment with a cluster with multiple nodes
    public static Cluster initCommunicationWithCluster(final Configuration cfg) {
//        final String[] nodes = cfg.getString(CASSANDRA_NODES).split(" ");
//        final String username = cfg.getString(CASSANDRA_USER_NAME);
//        final String password = cfg.getString(CASSANDRA_PASSWORD);
//        logAndPrint("CASSANDRA: connecting to nodes (first) " + nodes[0]);
        SocketOptions socketOptions = Cluster.builder().getConfiguration().getSocketOptions();
        socketOptions.setKeepAlive(Boolean.TRUE);
//        String localDc = cfg.getString(CASSANDRA_LOCALDC);
//        System.out.println("********************" + localDc);
//        final DCAwareRoundRobinPolicy lbPolicy = DCAwareRoundRobinPolicy.builder().withLocalDc(localDc).withUsedHostsPerRemoteDc(1).allowRemoteDCsForLocalConsistencyLevel()
//                .build();
//        LoadBalancingPolicy lb = new TokenAwarePolicy(lbPolicy);
//        QueryOptions queryOptions = new QueryOptions();
//        queryOptions.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
//        Cluster cluster = Cluster.builder().withLoadBalancingPolicy(lb)
//                .withRetryPolicy(new LoggingRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE)).withSocketOptions(socketOptions)
//                .withProtocolVersion(ProtocolVersion.V3).addContactPoints(nodes)
//                //.withCredentials(username, password)
//                .withQueryOptions(queryOptions).build();
       Cluster cluster = Cluster.builder()                                                    
        		.withSocketOptions(socketOptions)
        		.addContactPoint("127.0.0.1")
                .build();
        Metadata metadata = cluster.getMetadata();
        logAndPrint("CASSANDRA: Connected to cluster: " + metadata.getClusterName());
        ConsistencyLevel consistencyLevel = cluster.getConfiguration().getQueryOptions().getConsistencyLevel();
        logAndPrint("CASSANDRA: Using consistency level: " + consistencyLevel.name());
        logAndPrint("CASSANDRA: Using Driver Version: " + Cluster.getDriverVersion());
        
//        for (Host host : metadata.getAllHosts()) {
//            logAndPrint("CASSANDRA: Datacenter: " + host.getDatacenter() + " Host: " + host.getAddress() + " Rack: "
//                    + host.getRack());
//        }
        final List<KeyspaceMetadata> keyspaces = metadata.getKeyspaces();
        for (KeyspaceMetadata keyspaceMetadata : keyspaces) {
            logAndPrint("CASSANDRA: Keyspace " + keyspaceMetadata.getName());
        }
        return cluster;
    }

    private static void logAndPrint(final String msg) {
        cLogger.info(msg);
    }

//    private static String getPassword() {
//        try {
//            final Configuration cfg = new PropertiesConfiguration("/opt/izettle/etc/cassandra.properties");
//            return cfg.getString(CASSANDRA_PASSWORD);
//        } catch (Exception e) {
//            throw new RuntimeException("Error reading /opt/izettle/etc/cassandra.properties", e);
//        }
//    }

}
