package leaderus.study.chapter.unsafe.sort;

/**
 * Created by zhang on 2016/8/13.
 */
public class RowDataSorter {

    //添加一个新行，排序字段从第几个字节开始，长度是多少，比如一个字节，2个，4个，八个等，简单比较如下：按照ascii字符的大小排序，比如ab999<ba999
    //建议添加的过程中就拍好序（RowData序列）
    public void addNewRow(byte[] rowData, int orderColStart,int orderColLen ) {

    }

    //输出从FromM开始的N个排好序的结果集到屏幕，打印每一行的ascii字符串，以及其在内存中的偏移量位置
    public void outputLimtMN(int fromM ,int count) {

    }

    //删除内存映射文件
    public void close() {

    }
}
