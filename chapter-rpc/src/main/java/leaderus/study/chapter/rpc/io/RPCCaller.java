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
import leaderus.study.chapter.rpc.serialize.SerializeStrategy;
import leaderus.study.chapter.rpc.utils.RpsConstans;

public class RPCCaller {
	
	private final TransferQueue<RPCRequestEvent> requestQueue = new LinkedTransferQueue<>();
	
	private final TransferQueue<RPCResponse> responseQueue = new LinkedTransferQueue<>();
	
	private ThreadPoolExecutor threadPoolExecutor;

	private ByteBuffer requestDataBuffer;// 长度建议为10M
	
	private ReentrantLock requestLock ;

	private final RpcThread rpcThread;
	
	private SerializeStrategy<RPCRequest> serializeStrategy;

	public RPCCaller(SerializeStrategy.SerializeType type) {
		init();
		serializeStrategy = new SerializeStrategy<>(type);
		rpcThread = new RpcThread();
		rpcThread.setDaemon(false);
		rpcThread.start();
	}
	
	public RPCCaller() {
		this(SerializeStrategy.SerializeType.NOMAL);
	}
	
	private void init() {
		requestDataBuffer = ByteBuffer.allocate(10 * RpsConstans._M);
		requestLock = new ReentrantLock();
		threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}

	public RPCResponse doRPCCall(RPCRequest request) {
		
		System.out.println(Thread.currentThread().getName() + " ==> doCall");
		
		byte [] bytes = this.serializeStrategy.encode(request);
		
		try {
			requestLock.lock();
			
			StringBuffer bu = new StringBuffer(Thread.currentThread().getName() + " before buffer = " + requestDataBuffer);
			bu.append(" , request bytes len = " + bytes.length);
			requestDataBuffer.putInt(bytes.length);
			requestDataBuffer.put(bytes);
			bu.append(" , after buffer =  " + requestDataBuffer);
			//System.out.println(bu.toString());
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
			
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			try{
				
				requestLock.lock();
				
				requestDataBuffer.flip();
				
				while(requestDataBuffer.hasRemaining()) {
					
					int length = requestDataBuffer.getInt();
					
					bys = new byte[length];
					
					requestDataBuffer.get(bys, 0, length);
					
					RPCRequest request = serializeStrategy.decode(bys,RPCRequest.class);
					
					threadPoolExecutor.submit(new RpcJob(request));
				}
				
				requestDataBuffer.compact();
				
			}finally {
				requestLock.unlock();
			}

		}
	}
	
	private final class RpcJob extends Thread {
		
		private RPCResponse response;
		
		public RpcJob(RPCRequest request) {
			super();
			System.out.println("==> 接受到 request = " + request);
			response = new RPCResponse(request);
		}
		
		@Override
		public void run() {
			try {
				responseQueue.transfer(response);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}

