package client_oio;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {
	private String ip = "127.0.0.1";
	private int port = 8999;
	
	public void start() throws UnknownHostException, IOException {
		Socket socket = new Socket(ip,port);
		socket.setReuseAddress(true);
		PrintWriter output = new PrintWriter(socket.getOutputStream());
		Scanner input = new Scanner(socket.getInputStream());
		Scanner in = new Scanner(System.in);
		while (true) {
			try {
				output.println(in.nextLine());	
				output.flush();
				System.out.println("收到服务器信息：" + input.nextLine());
			} catch (NoSuchElementException e) {
				System.out.println("服务器退出");
				input.close();
				output.close();
				socket.close();
				return;
			}
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Client c = new Client();
		c.start();
		
	}

}
