package client;

public class Request {
	
	private int requestId;
	
	private String command;
	
	private String content;

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("id:").append(requestId).append(";");
		sb.append("command:").append(command).append(";");
		sb.append("content:").append(content).append(";");
		return sb.toString();
	}
}
