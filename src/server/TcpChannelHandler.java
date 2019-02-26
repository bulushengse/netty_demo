package server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class TcpChannelHandler extends ChannelInboundHandlerAdapter{

	
	/**
	 * 当一个连接到达，Netty会注册一个channel，然后EventLoopGroup会分配一个EventLoop绑定到这个channel,
	 * 在这个channel的整个生命周期过程中，都会由绑定的这个EventLoop来为它服务，而这个EventLoop就是一个线程。
	 */
	//所有的活动用户
    public static final ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    
    
    /**
     * 与客户端建立连接成功
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与客户端(" + ctx.channel().remoteAddress()+")连接成功...");
        //ctx.writeAndFlush("客户端"+ InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ \n");
        super.channelActive(ctx);
    }
	
	/**
	 * 读取消息通道，发送消息
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof Request){
			Request r  = (Request) msg;
			
			System.out.println("---执行业务逻辑...");
			//业务逻辑...
			if("/q".equals(r.getContent())) {
				ctx.close();
			}else {
				//返回消息
				ctx.writeAndFlush("hi,client!收到您的消息："+r.getContent());
			}
			
		}else {
			System.out.println("---???");
		}
		
	}

	/**
	 * 读取消息通道完成
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("---channelReadComplete...");
	    //ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE); 
		ctx.flush();
	}
	
	/**
	 * 出现异常，关闭连接
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
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
	         System.out.println("客户端[" + channel.remoteAddress() + "] 断开连接...");
	     } else {
	         System.out.println("客户端[" + channel.remoteAddress() + "] is online");
	     }
	  }
	
	   /**
	    * 处理新加的消息通道
	    */
	   @Override
	   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
	        Channel channel = ctx.channel();
	        for (Channel ch : group) {
	            if (!channel.id().equals(ch.id())) {
	                ch.writeAndFlush("客户端[" + channel.remoteAddress() + "] 上线了");
	            }
	        }
	        group.add(channel);
	    }

	    /**
	     * 处理退出的消息通道
	     */
	    @Override
	    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
	        Channel channel = ctx.channel();
	        for (Channel ch : group) {
	        	if (!channel.id().equals(ch.id())) {
	                ch.writeAndFlush("客户端[" + channel.remoteAddress() + "] 下线了");
	            }
	        }
	        group.remove(channel);
	    }	
}
