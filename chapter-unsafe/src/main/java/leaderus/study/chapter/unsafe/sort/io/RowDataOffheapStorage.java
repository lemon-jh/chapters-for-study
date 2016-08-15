package leaderus.study.chapter.unsafe.sort.io;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.AccessController;
import java.security.PrivilegedAction;

import leaderus.study.chapter.unsafe.UnsafeConstans;
import leaderus.study.chapter.unsafe.sort.DataOffHeapStorage;

/**
 * Created by zhang on 2016/8/13.
 */
public class RowDataOffheapStorage implements DataOffHeapStorage {

	private MappedByteBuffer buffer;

	private int pos = 0;

	public RowDataOffheapStorage(String fileName) {
		// 创建一个MappedByteBuffer 的映射文件存储，
		
		System.out.println("==> RowDataOffheapStorage init---");
		
		new File(fileName).delete();
		
		try (FileChannel channel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ,
				StandardOpenOption.WRITE);) {
			this.buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 300 * UnsafeConstans._M);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 添加一行记录，返回此行的内存地址，用于RowData类记录
	public long addRow(byte[] rowData) {

		//System.out.println("==> buffer = " + buffer);

		buffer.position(pos);
		buffer.put(rowData);
		//buffer.force();

		return (pos += rowData.length);
	}

	// 读取相应位置的数据并返回
	public byte[] getRow(long startPos, short dataLength) {

		buffer.position((int) startPos);

		//System.out.println("buffer = " + buffer);

		byte[] bytes = new byte[dataLength];

		buffer.get(bytes, 0, dataLength);

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

		RowDataOffheapStorage storage = new RowDataOffheapStorage(fileName);

		byte[] bytes = "abcd".getBytes();

		byte[] bytes2 = "aaaaa".getBytes();

		System.out.println(storage.addRow(bytes));

		System.out.println(storage.addRow(bytes2));

		byte[] toBytes1 = storage.getRow(0l, (short) bytes.length);
		byte[] toBytes = storage.getRow(3l, (short) 5);

		System.out.println("==>str =" + new String(toBytes1));
		System.out.println("==>str =" + new String(toBytes));

	}
}
