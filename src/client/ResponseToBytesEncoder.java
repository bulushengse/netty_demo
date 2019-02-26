package client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ResponseToBytesEncoder extends MessageToByteEncoder<Object>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		// TODO Auto-generated method stub
			
		//自定义编码...
		String fullMsg = "["+msg+"]";
			
		System.out.println("---发送消息："+fullMsg);
			
		out.writeBytes(fullMsg.getBytes());
	}

	
}
