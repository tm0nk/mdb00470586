package com.cisco.insieme.mdb00470586;

import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoClientConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoClientConfiguration.class);

  private static final String ADMIN_DB_NAME = "admin";

  private static final String ADMIN_DB_USER = "admin";

  // Not specified here, but known to be correct because almost all connections succeed
  private static final String ADMIN_DB_PASS = "";

  private static final int PORT = 27017;

  private static final int CONNECTION_CONNECTION_PER_HOST = 100;

  private static final int BLOCKING_THREAD_MULTIPLIER = 4;

  private static final int MAX_WAIT_TIME = 120 * 1000;

  private static final int MAX_CONNECTION_TIMEOUT = 10 * 1000;

  private static final int MAX_CONNECTION_IDLE_TIMEOUT = 30 * 1000;

  private static final int MAX_SOCKET_TIMEOUT = 60 * 1000;

  private MongoClient mongoClient;

  @Bean
  public MongoClient mongo() throws UnknownHostException {

    MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
    builder = builder.connectionsPerHost(CONNECTION_CONNECTION_PER_HOST)
            .threadsAllowedToBlockForConnectionMultiplier(BLOCKING_THREAD_MULTIPLIER)
            .maxWaitTime(MAX_WAIT_TIME)
            .connectTimeout(MAX_CONNECTION_TIMEOUT)
            .maxConnectionIdleTime(MAX_CONNECTION_IDLE_TIMEOUT)
            .socketKeepAlive(true)
            .socketTimeout(MAX_SOCKET_TIMEOUT)
            .readPreference(ReadPreference.primary())
            .writeConcern(WriteConcern.W1)
            .sslEnabled(true);

    List<MongoCredential> credentials = new ArrayList<>();
    credentials.add(MongoCredential
            .createScramSha1Credential(ADMIN_DB_USER, ADMIN_DB_NAME, ADMIN_DB_PASS.toCharArray()));

    MongoClientOptions mongoClientOptions = builder.build();

    String[] hosts = {"mdb-base-29", "mdb-base-30"};
    List<ServerAddress> servers = new ArrayList<>();
    for (String host : hosts) {
      servers.add(new ServerAddress(host, PORT));
    }

    mongoClient = new MongoClient(servers, credentials, mongoClientOptions);

    return mongoClient;
  }

  @PreDestroy
  public void cleanup() {
    if (mongoClient != null) {
      mongoClient.close();
      LOGGER.info("mongoClient destroyed!");
    }
  }

}
