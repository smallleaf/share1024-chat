package com.share1024.chat.handler;

import com.google.common.io.Resources;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedNioFile;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.RandomAccessFile;

/**
 * Created by yesheng on 2017/3/6.
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Logger logger = LoggerFactory.getLogger(HttpHandler.class);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        //一个资源
        String uri = request.getUri();

        String resource = uri.equals("/") ? "webroot/chat.html" : "webroot/" + uri;
        resource = resource.replaceAll("//", "/");

        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(FileUtils.toFile(Resources.getResource(resource)), "r");

        } catch (Exception e) {
            //继续下一次请求，服务端不报错
            ctx.fireChannelRead(request.retain());
            //   e.printStackTrace();
            return;
        }
        HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
        String contextType = "text/html;";
        if (uri.endsWith(".css")) {
            contextType = "text/css;";
        } else if (uri.endsWith(".js")) {
            contextType = "text/javascript;";
        } else if (uri.toLowerCase().matches("(jpg|png|gif|ico)$")) {
            String ext = uri.substring(uri.lastIndexOf("."));
            contextType = "image/" + ext + ";";
        }

        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contextType + "charset=utf-8");
        boolean keepAlive = HttpHeaders.isKeepAlive(request);
        if (keepAlive) {
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        ctx.write(response);

        ctx.write(new ChunkedNioFile(file.getChannel()));

        //如果不是长连接文件也全部输出
        ChannelFuture cf = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepAlive) {
            cf.addListener(ChannelFutureListener.CLOSE);
        }
        file.close();
    }
}
