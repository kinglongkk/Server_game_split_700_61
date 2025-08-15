package com.ddm.server.http.server;

import java.io.IOException;

import org.atmosphere.nettosphere.Config;
import org.atmosphere.nettosphere.Nettosphere;

import com.ddm.server.common.utils.DefaultThreadFactory;

import BaseCommon.CommLog;

public class MGHttpServer {

    // 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例 没有绑定关系，而且只有被调用到才会装载，从而实现了延迟加载
    private static class SingletonHolder {
        // 静态初始化器，由JVM来保证线程安全
        private static MGHttpServer instance = new MGHttpServer();
    }


    // 私有化构造方法
    private MGHttpServer() {
    }

    // 获取单例
    public static MGHttpServer getInstance() {
        return MGHttpServer.SingletonHolder.instance;
    }

    // http服务
    private Nettosphere nettosphere = null;

    public void createServer(int port, HttpDispather handler, String path) throws IOException {
		CommLog.info("[MGHttpServer.init] load http begin...]");
    	this.nettosphere =  new Nettosphere.Builder().config(new Config.Builder().port(port).parentThreadSize(4).parentThreadFactory(new DefaultThreadFactory(this.getClass())).childThreadSize(4).childThreadFactory(new DefaultThreadFactory(Nettosphere.class)).resource(path, handler).build()).build();
    	this.nettosphere.start();
		CommLog.info("[MGHttpServer.init] load http success, port: {}", port);
    }




    public void stop() {
        if (null != this.nettosphere) {
            this.nettosphere.stop();
        }
    }

    
    
    
}
