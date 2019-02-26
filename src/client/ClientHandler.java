package client;

import java.util.Date;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<Request>{

	
	 /**
	  * 与服务端首次连接成功发送消息
	  */
	 @Override
	 public void channelActive(ChannelHandlerContext ctx) { 
		 System.out.println(new Date()+"  服务端连接成功..."); 
		 //ctx.writeAndFlush("hello server!");
	 }
	
	 /**
	  * 读取消息通道，发送消息
	  */
	 @Override
	 public void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception { 
		 if(msg instanceof Request){
			Request r  = (Request) msg;
			
			System.out.println("---执行业务逻辑...");
			//业务逻辑...
					
			//返回消息
			//ctx.writeAndFlush("nice to meet you!");
			//ctx.close();		
		}else {
			System.out.println("---???");
		}
			  
	 } 
	  
	 /**
	  * 读取消息通道完成
	  */
	 @Override
	 public void channelReadComplete(ChannelHandlerContext ctx) throws Exception { 
		 System.out.println("---channelReadComplete.."); 
		 ctx.flush(); 
	 }
	 
	 /**
	  * 出现异常，关闭连接
	  */
	 @Override
	 public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception { 
		 cause.printStackTrace();
		 ctx.close(); 
	 }
	 
	 /**
	  * 消息通道退出时
	  */
	 @Override
	 public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	     Channel channel = ctx.channel();
	     if (!channel.isActive()) {
	         System.out.println("与服务端 断开连接...");
	     } else {
	         System.out.println("online...");
	     }
	  }
}
