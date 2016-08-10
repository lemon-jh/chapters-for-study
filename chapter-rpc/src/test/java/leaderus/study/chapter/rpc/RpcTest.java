package leaderus.study.chapter.rpc;

import leaderus.study.chapter.rpc.http.RPCRequest;
import leaderus.study.chapter.rpc.http.RPCResponse;
import leaderus.study.chapter.rpc.io.RPCCaller;
import leaderus.study.chapter.rpc.serialize.SerializeStrategy;

public class RpcTest {
	
	private static SerializeStrategy.SerializeType type;
	
	static{
		type = SerializeStrategy.SerializeType.NOMAL;
		//type = SerializeStrategy.SerializeType.MESSAGEPACK;
	}

	public static void main(String[] args) {
		
		
		
		final RPCCaller caller = new RPCCaller(type);
		
		for(int i=0;i<10;i++){
			final int sessionid = i; 
			new Thread(){
				public void run() {
					for(int j=0 ;j<10;j++) {
						RPCResponse res = caller.doRPCCall(new RPCRequest(sessionid, (short) j, "method".getBytes(), "param".getBytes()));
						System.out.println(Thread.currentThread().getId() + "==>" + res);
					}
				};
			}.start();
		}
	}
	
	
	
}
