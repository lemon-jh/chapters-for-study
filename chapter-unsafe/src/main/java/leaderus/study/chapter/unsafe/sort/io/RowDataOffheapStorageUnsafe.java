package leaderus.study.chapter.unsafe.sort.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;

import leaderus.study.chapter.unsafe.UnsafeConstans;
import leaderus.study.chapter.unsafe.sort.DataOffHeapStorage;
import sun.nio.ch.FileChannelImpl;

/**
 * Created by zhang on 2016/8/13.
 */
public class RowDataOffheapStorageUnsafe implements DataOffHeapStorage {

	public static final Method mmap;
	
	public static final Method unmmap;
	
	public static final int BYTE_ARRAY_OFFSET;
	
	private static long size = 0;
	
	private long address = 0l,pos=0l;

	static {
		try {
			mmap = UnsafeConstans.getMethod(FileChannelImpl.class, "map0", int.class, long.class, long.class);
			mmap.setAccessible(true);
			unmmap = UnsafeConstans.getMethod(FileChannelImpl.class, "unmap0", long.class, long.class);
			unmmap.setAccessible(true);
			BYTE_ARRAY_OFFSET = UnsafeConstans.getUnsafe().arrayBaseOffset(byte[].class);
			size = 100* UnsafeConstans._M;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public RowDataOffheapStorageUnsafe(String fileName) { 
		
		System.out.println("==> RowDataOffheapStorageUnsafe init---");
		
		try {
			
			RandomAccessFile backingFile = new RandomAccessFile(fileName, "rw");
			
			new File(fileName).delete();
			
			backingFile.setLength(size);
			
			FileChannel ch = backingFile.getChannel();
			
			this.address = (long) mmap.invoke(ch, 1, 0, size);
			
			ch.close();
			
			backingFile.close();
			
		} catch (IllegalAccessException | IOException | IllegalArgumentException | InvocationTargetException e) {
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
    
    public void close() {
    	try {
			unmmap.invoke(null, this.address, size);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
    }


    public static void main(String[] args) {

        String fileName = "src/list";

        RowDataOffheapStorageUnsafe storage = new RowDataOffheapStorageUnsafe(fileName);

        byte [] bytes = "abcd".getBytes();

        byte [] bytes2 = "aaaaa".getBytes();

        System.out.println(storage.addRow(bytes));

//        storage.close();

       System.out.println(storage.addRow(bytes2));

        byte [] toBytes1 = storage.getRow(0l,(short) bytes.length);
        byte [] toBytes = storage.getRow(3l,(short) 5);

        System.out.println("==>str =" + new String(toBytes1));
        System.out.println("==>str =" + new String(toBytes));



    }
}
