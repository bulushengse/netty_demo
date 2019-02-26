package client;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class BytesToRequestDecoder extends ByteToMessageDecoder{

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		 String temp = "";
		 //解决报错java.lang.UnsupportedOperationException
		 if(buf.hasArray()) { // 处理堆缓冲区
			 temp = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
		 } else { // 处理直接缓冲区以及复合缓冲区
		     byte[] bytes = new byte[buf.readableBytes()];
		     buf.getBytes(buf.readerIndex(), bytes);
		     temp = new String(bytes, 0, buf.readableBytes());
		 }
		 
		 System.out.println("---接受消息:"+temp);
		
		//自定义解码...
		Request request = new Request();
		request.setRequestId(1);
		request.setCommand("公共报文头...");
		request.setContent(temp);
		System.out.println("---消息解码:"+request.toString());
		
		out.add(request);
		
		//解决报错did not read anything but decoded a message
		buf.skipBytes(buf.readableBytes());
	}

}
