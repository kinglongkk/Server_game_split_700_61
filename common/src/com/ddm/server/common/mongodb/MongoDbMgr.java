package com.ddm.server.common.mongodb;

import BaseCommon.CommLog;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.rocketmq.ClassFind;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;
import lombok.Data;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class MongoDbMgr {
    private MongoDbConfig mongoDbConfig;
    private MongoClient mongoClient;
    private MongoDatabase database;

    private boolean isOpen = true;

    private MongoDbMgr() {

    }

    public static MongoDbMgr get() {
        return MongoDbMgr.SingleCase.INSTANCE;
    }

    public void loadConfig(String path, String basePackages) throws Exception {
        loadConfig(new FileInputStream(path));
        init(basePackages);
    }

    public void loadConfig(InputStream in) throws Exception {
        Properties pro = new Properties();
        pro.load(in);
        mongoDbConfig = new MongoDbMgr.MongoDbConfig();
        mongoDbConfig.setOpen(Boolean.parseBoolean(pro.getProperty("mongodb.open")));
        mongoDbConfig.setHost(pro.getProperty("mongodb.host"));
        mongoDbConfig.setPort(Integer.parseInt(pro.getProperty("mongodb.port")));
        mongoDbConfig.setDatabase(pro.getProperty("mongodb.database"));
        mongoDbConfig.setUsername(pro.getProperty("mongodb.username"));
        mongoDbConfig.setPassword(pro.getProperty("mongodb.password"));
        mongoDbConfig.setMinConnectionsPerHost(Integer.parseInt(pro.getProperty("mongodb.min-connections-per-host")));
        mongoDbConfig.setMaxConnectionsPerHost(Integer.parseInt(pro.getProperty("mongodb.max-connections-per-host")));
        mongoDbConfig.setThreadsAllowed(Integer.parseInt(pro.getProperty("mongodb.threads-allowed-to-block-for-connection-multiplier")));
        mongoDbConfig.setServerSelectionTimeout(Integer.parseInt(pro.getProperty("mongodb.server-selection-timeout")));
        mongoDbConfig.setMaxWaitTime(Integer.parseInt(pro.getProperty("mongodb.max-wait-time")));
        mongoDbConfig.setMaxConnectionIdelTime(Integer.parseInt(pro.getProperty("mongodb.max-connection-idel-time")));
        mongoDbConfig.setMaxConnectionLifeTime(Integer.parseInt(pro.getProperty("mongodb.max-connection-life-time")));
        mongoDbConfig.setConnectTimeout(Integer.parseInt(pro.getProperty("mongodb.connect-timeout")));
        mongoDbConfig.setSocketTimeout(Integer.parseInt(pro.getProperty("mongodb.socket-timeout")));
        mongoDbConfig.setSocketKeepAlive(Boolean.parseBoolean(pro.getProperty("mongodb.socket-keep-alive")));
        mongoDbConfig.setSslEnabled(Boolean.parseBoolean(pro.getProperty("mongodb.ssl-enabled")));
        mongoDbConfig.setSslInvalidHostNameAllowed(Boolean.parseBoolean(pro.getProperty("mongodb.ssl-invalid-host-name-allowed")));
        mongoDbConfig.setAlwaysUseMBeans(Boolean.parseBoolean(pro.getProperty("mongodb.always-use-m-beans")));
        mongoDbConfig.setHeartbeatSocketTimeout(Integer.parseInt(pro.getProperty("mongodb.heartbeat-socket-timeout")));
        mongoDbConfig.setHeartbeatConnectTimeout(Integer.parseInt(pro.getProperty("mongodb.heartbeat-connect-timeout")));
        mongoDbConfig.setMinHeartbeatFrequency(Integer.parseInt(pro.getProperty("mongodb.min-heartbeat-frequency")));
        mongoDbConfig.setHeartbeatFrequency(Integer.parseInt(pro.getProperty("mongodb.heartbeat-frequency")));
        mongoDbConfig.setLocalThreshold(Integer.parseInt(pro.getProperty("mongodb.local-threshold")));
        mongoDbConfig.setAuthenticationDatabase(pro.getProperty("mongodb.authentication-database"));
        in.close();
        pro.clear();
        if (mongoDbConfig.isOpen()) {
            MongoClientOptions.Builder build = new MongoClientOptions.Builder();
            build.connectionsPerHost(mongoDbConfig.getMaxConnectionsPerHost());   //与目标数据库能够建立的最大connection数量为
            build.minConnectionsPerHost(mongoDbConfig.getMinConnectionsPerHost());   //与目标数据库能够建立的最小connection数量
            build.threadsAllowedToBlockForConnectionMultiplier(mongoDbConfig.getThreadsAllowed()); //如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
            build.serverSelectionTimeout(mongoDbConfig.getServerSelectionTimeout());
            /*
             * 一个线程访问数据库的时候，在成功获取到一个可用数据库连接之前的最长等待时间为2分钟
             * 这里比较危险，如果超过maxWaitTime都没有获取到这个连接的话，该线程就会抛出Exception
             * 故这里设置的maxWaitTime应该足够大，以免由于排队线程过多造成的数据库访问失败
             */
            build.maxWaitTime(mongoDbConfig.getMaxWaitTime());
            build.maxConnectionIdleTime(mongoDbConfig.getMaxConnectionIdelTime());
            build.maxConnectionLifeTime(mongoDbConfig.getMaxConnectionLifeTime());
            build.connectTimeout(mongoDbConfig.getConnectTimeout());//与数据库建立连接的timeout设置为1分钟
            build.socketTimeout(mongoDbConfig.getSocketTimeout());
            build.socketKeepAlive(mongoDbConfig.isSocketKeepAlive());
            build.sslEnabled(mongoDbConfig.isSslEnabled());
            build.sslInvalidHostNameAllowed(mongoDbConfig.isSslInvalidHostNameAllowed());
            build.alwaysUseMBeans(mongoDbConfig.isAlwaysUseMBeans());
            build.heartbeatSocketTimeout(mongoDbConfig.getHeartbeatSocketTimeout());
            build.heartbeatConnectTimeout(mongoDbConfig.getHeartbeatConnectTimeout());
            build.minHeartbeatFrequency(mongoDbConfig.getMinHeartbeatFrequency());
            build.heartbeatFrequency(mongoDbConfig.getHeartbeatFrequency());
            build.localThreshold(mongoDbConfig.getLocalThreshold());
            MongoClientOptions myOptions = build.build();
            mongoClient = new MongoClient(new ServerAddress(mongoDbConfig.getHost(), mongoDbConfig.getPort()), myOptions);
            database = mongoClient.getDatabase(mongoDbConfig.getDatabase());
            CommLog.info("MongoDbMgr loadConfig isOpen init");
        }
    }

    /***
     * 初始化mongodb文档
     * @param basePackages
     */
    public void init(String basePackages) {
        Set<Class<?>> clazzs = ClassFind.getClasses(basePackages);
        for (Class<?> clazz : clazzs)
            try {
                if (clazz.isAnnotationPresent(MongoDbs.class)) {
                    MongoDbs mongoDbs = clazz.getAnnotation(MongoDbs.class);
                    for (MongoDb mongoDb : mongoDbs.value()) {
                        String collectionName = mongoDb.doc().collection();
                        //创建文档
                        createCollection(collectionName);
                        Map<String, Map<String, Integer>> indexMap = new HashMap<>();
                        for (CompoundIndex compoundIndex : mongoDb.indexes().value()) {
                            String indexName = compoundIndex.name();
                            Map<String, Integer> indexes = new Gson().fromJson(compoundIndex.def(), Map.class);
                            indexMap.put(indexName, indexes);
                        }
                        if (!indexMap.isEmpty()) {
                            //创建索引
                            createIndexes(collectionName, indexMap);
                        }

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                CommLogD.error(e.getMessage(), e);
            }
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }


    /**
     * 显示创建集合
     */
    public void createCollection(String collectionName) {
        MongoCollection<Document> collection = getDatabase().getCollection(collectionName);
        if (collection == null) {
            getDatabase().createCollection(collectionName, new CreateCollectionOptions().capped(false));
        }
    }

    /**
     * 功能描述: 创建索引
     *
     * @param collectionName 集合名称
     * @return:void
     */
    public synchronized void createIndexes(String collectionName, Map<String, Map<String, Integer>> indexMap) {
        if (!indexMap.isEmpty()) {
            MongoCollection<Document> collection = getCollection(collectionName);
            for (Map.Entry<String, Map<String, Integer>> indexes : indexMap.entrySet()) {
                IndexOptions indexOptions = new IndexOptions().name(indexes.getKey());
                BasicDBObject basicDBObject = new BasicDBObject();
                for (Map.Entry<String, Integer> index : indexes.getValue().entrySet()) {
                    basicDBObject.append(index.getKey(), index.getValue());
                }
                collection.createIndex(basicDBObject, indexOptions);
            }
        }
    }

    /**
     * 是否打开MongoDb
     *
     * @return
     */
    public boolean isOpenMongoDb() {
        return Objects.nonNull(this.mongoDbConfig) && this.mongoDbConfig.isOpen() && this.isOpen;
    }

    /**
     * 关闭客户端
     */
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        //MongoCollection实例是不可变的
        if (!collectionExists(collectionName)) {
            database.createCollection(collectionName, new CreateCollectionOptions().capped(false));
        }
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection;
    }

    public boolean collectionExists(final String collectionName) {
        boolean collectionExists = database.listCollectionNames().into(new ArrayList<String>()).contains(collectionName);
        return collectionExists;
    }

    private static class SingleCase {
        public static final MongoDbMgr INSTANCE = new MongoDbMgr();
    }

    @Data
    private static class MongoDbConfig {
        private boolean open;
        private String host;
        private int port;
        private String database;
        private String username;
        private String password;
        private int minConnectionsPerHost;
        private int maxConnectionsPerHost;
        private int threadsAllowed;
        private int serverSelectionTimeout;
        private int maxWaitTime;
        private int maxConnectionIdelTime;
        private int maxConnectionLifeTime;
        private int connectTimeout;
        private int socketTimeout;
        private boolean socketKeepAlive;
        private boolean sslEnabled;
        private boolean sslInvalidHostNameAllowed;
        private boolean alwaysUseMBeans;
        private int heartbeatSocketTimeout;
        private int heartbeatConnectTimeout;
        private int minHeartbeatFrequency;
        private int heartbeatFrequency;
        private int localThreshold;
        private String authenticationDatabase;
    }
}
