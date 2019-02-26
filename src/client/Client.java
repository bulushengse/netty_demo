package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
	
	  public void connect(String host,int port) throws Exception {
	        // 配置客户端NIO线程组
	        EventLoopGroup group = new NioEventLoopGroup();
	        try {
	            Bootstrap b = new Bootstrap();
	            b.group(group).channel(NioSocketChannel.class)
	            		.option(ChannelOption.TCP_NODELAY, true)
	                    .handler(new ChannelInitializer<SocketChannel>() {
	                        @Override
	                        protected void initChannel(SocketChannel ch) throws Exception {
	                            System.out.println("client initChannel..");
	                            ch.pipeline().addLast("decoder",new BytesToRequestDecoder());// decoder 解码器
	                            ch.pipeline().addLast("encoder",new ResponseToBytesEncoder());// encoder 编码器
	                            ch.pipeline().addLast("handler",new ClientHandler());// client自己的逻辑Handler
	                        }
	                    });
	           // 发起异步连接操作
	           //ChannelFuture f = b.connect(host, port).sync();
	           Channel ch = b.connect(host, port).sync().channel();
	           
	   	       // 控制台输入
	   	       BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	   	       String line = "";
	   	       while(true) {
	   	    	   System.out.println("请输入要发送的消息(输入'/q'断开连接)：");
	   	    	   line = in.readLine();
	   	           
	   	           ch.writeAndFlush(line);
	   	       }  
	   	           
	   	       // 等待客户端链路关闭
		       //ch.closeFuture().sync();     
	   	       
	        } finally {
	            // 出现异常，释放NIO线程组
	            group.shutdownGracefully();
	        }
	        
	    }
	  
	  
	  public static void main(String[] args) throws Exception {
		  Client client = new Client();
		  client.connect("127.0.0.1",8999);
		  
	 }
	  
	
}
