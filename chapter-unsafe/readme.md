# java unsafe 类的使用 #


----------
**1.用MappedByteBuffer 结合Unsafe ，一个特殊的数据结构，这个数据结构的用途是优化Mycat在内存中对MySQL结果集（RowSet）排序时的内存占用**

	public class RowData {
          private long rowStartPos;//本行数据在堆外内存的起始位置
          private short rowLength; //一行数据的长度，字节数
      }


堆外存储，一次性使用，即记录保存完成后，读取完成后， 就关闭删除文件，不会修改数据
    
	public class RowDataOffheapStorage {
         
		public RowDataOffheapStorage(String fileName) {
            //创建一个MappedByteBuffer 的映射文件存储，
		}
		//添加一行记录，返回此行的内存地址，用于RowData类记录
		public long addRow(byte[] rowData);
		//读取相应位置的数据并返回
		public byte[] getRow(long startPos,short dataLength);
	}

----------
	public class RowDataSorter {
            //添加一个新行，排序字段从第几个字节开始，长度是多少，比如一个字节，2个，4个，八个等，简单比较如下：按照ascii字符的大小排序，比如ab999<ba999
            //建议添加的过程中就拍好序（RowData序列）
		public viod addNewRow(byte[] rowData, int orderColStart,int orderColLen )
		
		//输出从FromM开始的N个排好序的结果集到屏幕，打印每一行的ascii字符串，以及其在内存中的偏移量位置
		public void outputLimtMN(int fromM ,int count)
		
		//删除内存映射文件
		public void close()
	}   

----------
    public class TestSorter {
		//先随机生成1000万的随机Row，放入RowDataSorter中，然后输出 从100万开始的1000条记录，并记录整个时间消耗。
	}


**2.用properties文件定义一个通用数据类（值对象）的metadata信息， 然后用Unsafe类实现高速序列化与反序列化API**


----------
以Person对象为例
	
	public class Person {
		private int age;
		private String name;
		private boolean hasMoney;
	}

其metadata信息如下

	classname=leaderus.study8.Person
    prop.age=int
    prop.name=String
    prop.hasMoney=boolean

序列化反序列化类
	
	public class MyCustomSearlizer

根据类名和属性值，反序列化一个Java对象

	public Object deSearlize(String className,Map<String,String> props)

根据类名和属性值，反序列化一个Java对象,返回其属性到Map里

	public Map<String,String> searlize(Object obj)