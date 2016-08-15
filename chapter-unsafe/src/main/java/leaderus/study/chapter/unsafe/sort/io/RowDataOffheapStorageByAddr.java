package leaderus.study.chapter.unsafe.sort.io;

import leaderus.study.chapter.unsafe.UnsafeConstans;
import leaderus.study.chapter.unsafe.sort.DataOffHeapStorage;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Created by zhang on 2016/8/13.
 */
public class RowDataOffheapStorageByAddr implements DataOffHeapStorage {

    private MappedByteBuffer buffer;

    private int pos = 0;

    private long address;

    public RowDataOffheapStorageByAddr(String fileName) {
        //创建一个MappedByteBuffer 的映射文件存储，
    	
    	System.out.println("==> RowDataOffheapStorageByAddr init---");
    	
    	new File(fileName).delete();
    	
        try(FileChannel channel = FileChannel.open(Paths.get(fileName),
                StandardOpenOption.READ, StandardOpenOption.WRITE);) {

            this.buffer = channel.map(FileChannel.MapMode.READ_WRITE,0,300 * UnsafeConstans._M);

            Class<?> clazz = MappedByteBuffer.class;

            Field personNameField = null;

            for(;clazz != Object.class;clazz= clazz.getSuperclass()){
                try {
                    personNameField = clazz.getDeclaredField("address");
                } catch (Exception e) {}
            }

            personNameField.setAccessible(true);

            this.address = (Long) personNameField.get(this.buffer);

            System.out.println("address = " + address);

            channel.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long addRow(byte[] rowData) {

        UnsafeConstans.getUnsafe().copyMemory(rowData,
                UnsafeConstans.byteArrayOffset,null,pos+this.address,rowData.length);

        return (pos += rowData.length);
    }

    //读取相应位置的数据并返回
    public byte[] getRow(long startPos,short dataLength) {

        byte[] bytes = new byte[dataLength];

        UnsafeConstans.getUnsafe().copyMemory(null,
                startPos + this.address,bytes,UnsafeConstans.byteArrayOffset,dataLength);

        return bytes;
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void close() {
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				try {
					Method getCleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
					getCleanerMethod.setAccessible(true);
					sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(buffer, new Object[0]);
					cleaner.clean();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

    public static void main(String[] args) {

        String fileName = "src/list";

        RowDataOffheapStorageByAddr storage = new RowDataOffheapStorageByAddr(fileName);

        byte [] bytes = "abcs".getBytes();

        byte [] bytes2 = "aaaaa".getBytes();

        System.out.println(storage.addRow(bytes));

        System.out.println(storage.addRow(bytes2));
        
      //  storage.close();

        byte [] toBytes1 = storage.getRow(0l,(short) 4);
        byte [] toBytes = storage.getRow(4l,(short) 5);

        System.out.println("==>str =" + new String(toBytes1));
        System.out.println("==>str =" + new String(toBytes));



    }
}
