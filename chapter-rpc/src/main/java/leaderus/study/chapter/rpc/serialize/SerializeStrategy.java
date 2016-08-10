package leaderus.study.chapter.rpc.serialize;

public class SerializeStrategy<T> {
	
	private SerializeProtocol<T> serializeProtocol;
	
	public SerializeStrategy(SerializeType serializeType) {
		switch (serializeType) {
			case MESSAGEPACK:
				serializeProtocol = new MessagepackSerialize<>();
				break;
			case NOMAL:
			default:
				serializeProtocol = new NomalSerialize<>();
				break;
		}
	}

	public T decode(byte [] bytes,Class<T> entityClass) {
		return serializeProtocol.decode(bytes,entityClass);
	}

	public byte [] encode(T t) {
		return serializeProtocol.encode(t);
	}
	
	public static enum SerializeType {
		NOMAL(0),  
		MESSAGEPACK(1);
        int serializeType;  
        SerializeType(int serializeType) {  
               this.serializeType = serializeType;  
        }  
	}
	
}
