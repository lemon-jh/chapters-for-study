package leaderus.study.chapter.unsafe.sort;

/**
 * Created by zhang on 2016/8/13.
 */
public class RowData {

    private long rowStartPos;//本行数据在堆外内存的起始位置

    private short rowLength; //一行数据的长度，字节数

    public long getRowStartPos() {
        return rowStartPos;
    }

    public void setRowStartPos(long rowStartPos) {
        this.rowStartPos = rowStartPos;
    }

    public short getRowLength() {
        return rowLength;
    }

    public void setRowLength(short rowLength) {
        this.rowLength = rowLength;
    }
}
