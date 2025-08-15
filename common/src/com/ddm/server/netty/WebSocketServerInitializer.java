package com.ddm.server.netty;

import com.ddm.server.websocket.codecfactory.MessageDecoder;
import com.ddm.server.websocket.codecfactory.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * netty的管道器
 */
public class WebSocketServerInitializer extends ChannelInitializer<NioSocketChannel> {

    private ServerHandler serverHandler;

    public void setHandler(ServerHandler serverHandler){
        this.serverHandler = serverHandler;
    }

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 编解码 http 请求
        pipeline.addLast(new HttpServerCodec());
        // 写文件内容
        pipeline.addLast(new ChunkedWriteHandler());
        // 聚合解码 HttpRequest/HttpContent/LastHttpContent 到 FullHttpRequest，保证接收的 Http 请求的完整性，防止类似mina出现多条同样请求
        pipeline.addLast(new HttpObjectAggregator(8192));
        // 处理其他的 WebSocketFrame
        pipeline.addLast(new TWebSocketServerProtocolHandler("/ws"));
        //编解码器
        pipeline.addLast(new MessageDecoder(1<<20, 2, 2));
        pipeline.addLast(new MessageEncoder());
        //消息处理器
        pipeline.addLast(serverHandler);
    }

}
