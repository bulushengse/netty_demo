package server_http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

public class HttpChannelHandler extends ChannelInboundHandlerAdapter{

	
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
     * 发送消息
     */
    private void send(String content, ChannelHandlerContext ctx, HttpResponseStatus status){
    	FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,status,Unpooled.copiedBuffer(content,CharsetUtil.UTF_8));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain;charset=UTF-8");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    
	/**
	 * 读取消息通道，发送消息
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		 
		 Map<String, String> requestParams = new HashMap<>();
		 
		 String result="";
		 
		 FullHttpRequest httpRequest = (FullHttpRequest)msg;
		 
		 try{
	           String path = httpRequest.uri();
	           HttpMethod method = httpRequest.method();
	           
	           //处理http GET请求
	           if(HttpMethod.GET.equals(method)){
	           	   
	               QueryStringDecoder decoder = new QueryStringDecoder(path);  
	               Map<String, List<String>> param = decoder.parameters();  
	               Iterator<Entry<String, List<String>>> iterator = param.entrySet().iterator();
	               while(iterator.hasNext()){
	                    Entry<String, List<String>> next = iterator.next();
	                    result = next.getKey()+"="+next.getValue().get(0)+",";
	                    requestParams.put(next.getKey(), next.getValue().get(0));
	               }
	               System.out.println("---获取参数："+result);
	               send(result,ctx,HttpResponseStatus.OK);
	               
	           }

	           //处理http POST请求
	           if(HttpMethod.POST.equals(method)){
	        	   HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), httpRequest);  
	               List<InterfaceHttpData> postData = decoder.getBodyHttpDatas(); //
	               for(InterfaceHttpData data:postData){
	                   if (data.getHttpDataType() == HttpDataType.Attribute) {  
	                        MemoryAttribute attribute = (MemoryAttribute) data;  
	                        result = attribute.getName()+"="+attribute.getValue()+",";
	                        requestParams.put(attribute.getName(), attribute.getValue());
	                    }
	               }
	               System.out.println("---获取参数："+result);
		           send(result,ctx,HttpResponseStatus.OK);
	           }

	       }catch(Exception e){
	           System.out.println("---处理请求失败!");
	           e.printStackTrace();
	       }finally{
	           httpRequest.release();
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
