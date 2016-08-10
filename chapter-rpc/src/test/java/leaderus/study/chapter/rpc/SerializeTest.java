package leaderus.study.chapter.rpc;

import java.io.IOException;

import org.msgpack.MessagePack;

import leaderus.study.chapter.rpc.http.RPCRequest;
import leaderus.study.chapter.rpc.serialize.SerializeStrategy;

public class SerializeTest {

	private static SerializeStrategy<RPCRequest> serializeStrategy;
	
	private static RPCRequest res;
	
	static {
		
		//serializeStrategy = new SerializeStrategy<>(SerializeStrategy.SerializeType.NOMAL);
		serializeStrategy = new SerializeStrategy<>(SerializeStrategy.SerializeType.MESSAGEPACK);
		
		res = new RPCRequest(1, (short) 1, "method".getBytes(), "param".getBytes());
	}
	
	public static void main(String [] args) throws IOException {
		
		//byte [] bytes = serializeStrategy.encode(res);
		
		MessagePack messagePack = new MessagePack();
		
		messagePack.register(RPCRequest.class);
		
		byte [] bytes = messagePack.write(res);
		
		System.out.println("==>serialize object len = " + bytes.length);
		
		RPCRequest result = serializeStrategy.decode(bytes,RPCRequest.class);
		
		System.out.println("==>unserialize bytes result = " + result);
		
	}
}
