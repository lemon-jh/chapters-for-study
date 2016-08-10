package leaderus.study.chapter.rpc.serialize;

public interface SerializeProtocol<T> {

	public T decode(byte [] bytes,Class<T> entityClass);

	public byte [] encode(T t); 
	
}
