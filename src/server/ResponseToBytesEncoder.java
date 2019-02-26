package server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ResponseToBytesEncoder extends MessageToByteEncoder<Object>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		// TODO Auto-generated method stub
		if(msg instanceof String){
			
			//自定义编码...
			String fullMsg = "<"+msg+">";
			System.out.println("---发送消息给客户端["+ctx.channel().remoteAddress()+"]："+fullMsg);
			
			out.writeBytes(fullMsg.getBytes());
		}
	}

	
}
