package leaderus.study.chapter.rpc.io;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.locks.ReentrantLock;

import leaderus.study.chapter.rpc.event.RPCRequestEvent;
import leaderus.study.chapter.rpc.http.RPCRequest;
import leaderus.study.chapter.rpc.http.RPCResponse;
import leaderus.study.chapter.rpc.serialize.SerializeProtocol;
import leaderus.study.chapter.rpc.utils.RpsConstans;

public class RPCCaller {
	
	private final TransferQueue<RPCRequestEvent> requestQueue = new LinkedTransferQueue<>();
	
	private final TransferQueue<RPCResponse> responseQueue = new LinkedTransferQueue<>();
	
	private ThreadPoolExecutor threadPoolExecutor;

	private ByteBuffer requestDataBuffer;// 长度建议为10M
	
	private ReentrantLock requestLock ;

	private final RpcThread rpcThread;
	
	private SerializeProtocol<RPCRequest> res;

	public RPCCaller() {
		requestDataBuffer = ByteBuffer.allocate(10 * RpsConstans._M);
		requestLock = new ReentrantLock();
		threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		rpcThread = new RpcThread();
		rpcThread.start();
	}

	public RPCResponse doRPCCall(RPCRequest request) {
		
		byte [] bytes = this.res.encode(request);
		
		try {
			requestLock.lock();
			requestDataBuffer.putInt(bytes.length);
			requestDataBuffer.put(bytes);
		}finally{
			requestLock.unlock();
		}
		
		this.requestQueue.offer(new RPCRequestEvent());
		
		return this.waitForRes(request.getSessionId(),request.getRequestId());
	}
	
	private RPCResponse waitForRes(int sessionId,short requestId) {
		for (;;) {
			RPCResponse re = responseQueue.peek();
			if(re != null) {
				if(re.getSessionId() == sessionId && re.getRequestId() == requestId) {
					return responseQueue.poll();
				}
			}
		}
	}
	

	private final class RpcThread extends Thread {
		@Override
		public void run() {
			for(;;){
				RPCRequestEvent eve = requestQueue.poll();
				if(eve != null){
					this.handle();
				}
			}
		}
		
		public void handle() {
			
			byte [] bys = null;
			
			try{
				requestLock.lock();
				
				requestDataBuffer.flip();
				
				int length = requestDataBuffer.getInt();
				
				bys = new byte[length];
				
				requestDataBuffer.get(bys, 0, length);
				
			}finally {
				requestLock.unlock();
			}
			
			RPCRequest request = res.decode(bys);
			
			threadPoolExecutor.submit(new RpcJob(request));
		}
	}
	
	private final class RpcJob extends Thread {
		
		private RPCRequest request;
		
		private RPCResponse response;
		
		public RpcJob(RPCRequest request) {
			super();
			this.request = request;
			response = new RPCResponse(request);
		}
		
		@Override
		public void run() {
			System.out.println(this.request);
			try {
				responseQueue.transfer(response);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
		
		final RPCCaller caller = new RPCCaller();
		
		for(int i=0;i<10;i++){
			final int sessionid = i; 
			new Thread(){
				public void run() {
					for(int j=0 ;j<10;j++) {
						RPCResponse res = caller.doRPCCall(new RPCRequest(sessionid, (short) j, "method".getBytes(), "param".getBytes()));
						System.out.println(res);
					}
				};
			}.start();
		}
	}

}

