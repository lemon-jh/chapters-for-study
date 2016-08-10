package leaderus.study.chapter.rpc.serialize;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.msgpack.MessagePack;

public class MessagepackSerialize<T> implements SerializeProtocol<T> {

	private Class<T> entityClass;

	private static MessagePack pack = new MessagePack();

	@SuppressWarnings("unchecked")
	public MessagepackSerialize() {
		entityClass = (Class<T>)getSuperClassGenricType(this.getClass(),0);
		System.out.println(entityClass);
	}

	@Override
	public T decode(byte[] bytes,Class<T> entityClass) {
		try {
			return pack.read(bytes,entityClass);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public byte[] encode(T t) {
		byte[] bytes = null;
		try {
			bytes = pack.write(t);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	@SuppressWarnings("unchecked")  
    public static Class<Object> getSuperClassGenricType(final Class clazz, final int index) {  
          
        //返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。  
        Type genType = clazz.getGenericInterfaces()[0];
  
        if (!(genType instanceof ParameterizedType)) {  
           return Object.class;  
        }  
        //返回表示此类型实际类型参数的 Type 对象的数组。  
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();  
  
        if (index >= params.length || index < 0) {  
                     return Object.class;  
        }  
        if (!(params[index] instanceof Class)) {  
              return Object.class;  
        }  
  
        return (Class) params[index];  
    }  

}
