package remi.remotecontrol.pojo;

public class RemoteService {
	private String name ;
	private String host;
	private String port;
	public RemoteService(String name, String host, String port) {
		super();
		this.name = name;
		this.host = host;
		this.port = port;
	}
	public String getName() {
		return name;
	}
	public String getHost() {
		return host;
	}
	public String getPort() {
		return port;
	}

	@Override
	public String toString() {
		return host + ":" + port;
	}
	
	@Override
	public boolean equals(Object o) {
		
		boolean eq = o instanceof RemoteService;
		
		RemoteService r = ((RemoteService)o);
		
		eq &= r.host.equals(host);
		eq &= r.name.equals(name);
		eq &= r.port.equals(port);
		
		return eq;
	}


}
