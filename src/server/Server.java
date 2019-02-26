package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {

	
	public void startup(int port) throws Exception {
		
		//Netty提供了许多不同的EventLoopGroup的实现用来处理不同传输协议,因此会有2个NioEventLoopGroup会被使用
		//第一个经常被叫做‘boss’,接收进来的连接
		EventLoopGroup bossGroup= new NioEventLoopGroup();  
		//第二个经常被叫做‘worker’,处理已经被接收的连接,一旦‘boss’接收到连接，就会把连接信息注册到‘worker’上。
		EventLoopGroup workerGroup= new NioEventLoopGroup(); 
	    
	    try {
	    	
	    	//ServerBootstrap 是一个启动NIO服务的辅助启动类 你可以在这个服务中直接使用Channel
		    ServerBootstrap b = new ServerBootstrap();
		    b.group(bossGroup, workerGroup);// 配置服务器的NIO线程租
		    b.channel(NioServerSocketChannel.class);//Channel如何接收新的连接
		    b.option(ChannelOption.SO_BACKLOG, 128);
		    //业务处理程序,通过ChannelPipeline.addLast()来实现的。
		    b.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("decoder",new BytesToRequestDecoder());// decoder 解码器
                    ch.pipeline().addLast("encoder",new ResponseToBytesEncoder());// encoder 编码器
                    ch.pipeline().addLast("handler",new TcpChannelHandler());// 自己的逻辑Handler
                }
            });
		    
		    // 绑定端口，开始接收进来的连接
		    ChannelFuture f = b.bind(port).sync();
		    System.out.println("server run in port " + port + "...");
		    
		    // 等待服务器socket关闭
		    f.channel().closeFuture().sync();
		    
	    } finally {
            //出现异常，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
		
	}
	
	
	public static void main(String[] args) throws Exception {
		Server server = new Server();
		server.startup(8999);
	}
}
