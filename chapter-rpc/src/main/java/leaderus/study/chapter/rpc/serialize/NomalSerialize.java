package leaderus.study.chapter.rpc.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class NomalSerialize<T> implements SerializeProtocol<T> {

	@SuppressWarnings("unchecked")
	@Override
	public T decode(byte[] bytes,Class<T> entityClass) {
		
		T t = null;

		ByteArrayInputStream bi = null;

		ObjectInputStream oi = null;

		try {

			bi = new ByteArrayInputStream(bytes);
			oi = new ObjectInputStream(bi);
			t = (T) oi.readObject();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (oi != null) {
					oi.close();
				}
				if (bi != null) {
					bi.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return t;
	}

	@Override
	public byte[] encode(T t) {
		
		ByteArrayOutputStream bos = null;
		
		ObjectOutputStream oos = null;
		
		byte[] bytes = null;
		
		try{
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(t);
			bytes = bos.toByteArray();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			try {
				bos.close();
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return bytes;
	}

}
