package leaderus.study.chapter.unsafe.sort;

public class RowDataOffheapStorage {

	public RowDataOffheapStorage(String fileName) {
		// 创建一个MappedByteBuffer 的映射文件存储，
	}

	// 添加一行记录，返回此行的内存地址，用于RowData类记录
	public long addRow(byte[] rowData) {
		return 0l;
	}

	// 读取相应位置的数据并返回
	public byte[] getRow(long startPos, short dataLength) {
		return null;
	}

}
