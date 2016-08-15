
package leaderus.study.chapter.unsafe.sort;

/**
 * Created by zhang on 2016/8/13.
 */
public class RowData {

    private long rowStartPos;//本行数据在堆外内存的起始位置

    private short rowLength; //一行数据的长度，字节数
    
    private long sort;

    public RowData(long rowStartPos, short rowLength,long sort) {
		super();
		this.rowStartPos = rowStartPos;
		this.rowLength = rowLength;
		this.sort = sort;
	}

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


	public long getSort() {
		return sort;
	}

	public void setSort(long sort) {
		this.sort = sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}
    
    
}
