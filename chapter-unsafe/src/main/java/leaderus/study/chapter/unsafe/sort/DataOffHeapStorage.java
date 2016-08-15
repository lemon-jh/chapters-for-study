package leaderus.study.chapter.unsafe.sort;

/**
 * Created by zhang on 2016/8/14.
 */
public interface DataOffHeapStorage {

    public long addRow(byte[] rowData);

    //读取相应位置的数据并返回
    public byte[] getRow(long startPos,short dataLength);
    
    public void close() ;

}
