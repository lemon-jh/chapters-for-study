package leaderus.study.chapter.unsafe.sort.io;

import leaderus.study.chapter.unsafe.UnsafeConstans;
import leaderus.study.chapter.unsafe.sort.DataOffHeapStorage;

import java.lang.reflect.Field;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by zhang on 2016/8/13.
 */
public class RowDataOffheapStorage implements DataOffHeapStorage {

    private MappedByteBuffer buffer;

    private int pos = 0;

    private long address;

    public RowDataOffheapStorage(String fileName) {
        //创建一个MappedByteBuffer 的映射文件存储，
        try(FileChannel channel = FileChannel.open(Paths.get(fileName),
                StandardOpenOption.READ, StandardOpenOption.WRITE);) {
            this.buffer = channel.map(FileChannel.MapMode.READ_WRITE,0,100 * UnsafeConstans._M);
            Class clazz = MappedByteBuffer.class;

            Field personNameField = null;

            for(;clazz != Object.class;clazz= clazz.getSuperclass()){
                try {
                    personNameField = clazz.getDeclaredField("address");
                } catch (Exception e) {}
            }

            personNameField.setAccessible(true);

            this.address = (Long) personNameField.get(this.buffer);

            System.out.println("address = " + address);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //添加一行记录，返回此行的内存地址，用于RowData类记录
    public long addRow(byte[] rowData) {

        System.out.println("==> buffer = " + buffer);

        buffer.position (pos);
        buffer.put (rowData);
        buffer.force();

        return (pos += rowData.length);
    }

    //读取相应位置的数据并返回
    public byte[] getRow(long startPos,short dataLength) {

        buffer.position( (int) startPos);

        System.out.println("buffer = " + buffer);

        byte[] bytes = new byte[dataLength];

        buffer.get(bytes,0, dataLength);

        return bytes;
    }

    public long addRow1(byte[] rowData) {

        UnsafeConstans.getUnsafe().copyMemory(rowData,
                UnsafeConstans.byteArrayOffset,null,pos+this.address,rowData.length);

        return (pos += rowData.length);
    }

    public static void main(String[] args) {

        String fileName = "D://TEMP//list";

        RowDataOffheapStorage storage = new RowDataOffheapStorage(fileName);

        byte [] bytes = "abcd".getBytes();

        byte [] bytes2 = "aaaaa".getBytes();

        System.out.println(storage.addRow1(bytes));


       System.out.println(storage.addRow1(bytes2));

        byte [] toBytes1 = storage.getRow(0l,(short) bytes.length);
        byte [] toBytes = storage.getRow(3l,(short) 5);

        System.out.println("==>str =" + new String(toBytes1));
        System.out.println("==>str =" + new String(toBytes));



    }
}
