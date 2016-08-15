package leaderus.study.chapter.unsafe.sort;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import leaderus.study.chapter.unsafe.sort.io.RowDataOffheapStorage;
import leaderus.study.chapter.unsafe.sort.io.RowDataOffheapStorageByAddr;
import leaderus.study.chapter.unsafe.sort.io.RowDataOffheapStorageUnsafe;

/**
 * Created by zhang on 2016/8/13.
 */
public class RowDataSorter {
	
	private DataOffHeapStorage storage;
	
	private List<RowData> list;
	
	private long pos = 0;
	
	public RowDataSorter(RowDataSorter.storeEnum storeEnum,String fileName) {
		switch (storeEnum) {
			case unsafe:
				storage = new RowDataOffheapStorageUnsafe(fileName);
				break;
			case address:
				storage = new RowDataOffheapStorageByAddr(fileName);
				break;
			case channel:
			default:
				storage = new RowDataOffheapStorage(fileName);
				break;
			}
		list = new ArrayList<>();
	}

    //添加一个新行，排序字段从第几个字节开始，长度是多少，比如一个字节，2个，4个，八个等，简单比较如下：按照ascii字符的大小排序，比如ab999<ba999
    //建议添加的过程中就拍好序（RowData序列）
    public void addNewRow(byte[] rowData, int orderColStart,int orderColLen) {
    	
    	pos = this.storage.addRow(rowData);
        
    	//byte []  bytes 长度为5,排序字段为 从orderColStart,长度为orderColLen
    	//简单对ascii 做加法操作
        RowData rd = new RowData(pos-rowData.length,(short)rowData.length,
        		this.sumStrAscii(rowData,orderColStart,orderColLen));
        
        this.list.add(rd);
    }

    //输出从FromM开始的N个排好序的结果集到屏幕，打印每一行的ascii字符串，以及其在内存中的偏移量位置
    public void outputLimtMN(int fromM ,int count) {
    	
    	//排序
        Collections.sort(list,new Comparator<RowData>() {
			public int compare(RowData o1, RowData o2) {
				return o1.getSort() == o2.getSort() ?0:o1.getSort() > o2.getSort()?-1 :1;
			}
		});
    	
        RowData temp = null;
        
    	for(int i= fromM;i<count+fromM;i++) {
    		temp = this.list.get(i);
    		byte [] bytes = this.storage.getRow(temp.getRowStartPos(), (short)temp.getRowLength());
    		System.out.println("==> str = " + new String(bytes) + ", pos = " + temp.getRowStartPos()
    				+", ");
    	}
    	
    }
    
    private long sumStrAscii(byte[] rowData,int orderColStart,int orderColLen){  
    	long sum = 0;  
         for(int i=orderColStart;i<orderColStart + orderColLen;i++){  
        	 sum = (sum <<8) + rowData[i];    
         }  
        return sum;  
    }  
    
    private static long tSumStrAscii(byte[] rowData){  
   	 long sum = 0;  
        for(int i=0;i<rowData.length;i++){  
       	 sum = (sum <<8) + rowData[i];    
        }  
       return sum;  
   }  

    //删除内存映射文件
    public void close() {
    	this.storage.close();
    }
    
    static enum storeEnum {
    	channel,address,unsafe
    }
    
    public static void main(String[] args) throws UnsupportedEncodingException {
    	
//    	for(int i=80;i<100;i++) {
//    		byte [] bytes = (i+"").getBytes();
//    		System.out.println(i+"--" + RowDataSorter.tSumStrAscii(bytes));
//    	}
//    	==> str = 919999, pos = 4889119, --960051513
//    	==> str = 459999, pos = 4971877, --960051513
//    			==> str = 899999, pos = 5007401, 
//    			==> str = 569999, pos = 5049418, 
//    			==> str = 109999, pos = 5067453, 
//    			==> str = 889999, pos = 5115035, 
    	byte [] bytes = ("459999").getBytes();
    	
		System.out.println(RowDataSorter.tSumStrAscii(bytes));
//    	
//    	RowDataSorter sorter = new RowDataSorter(storeEnum.channel,"src/list");
//    	
//    	int loop = 100;
//    	
//    	for(int i=0;i<loop;i++){
//    		
//        	byte [] bytes = ("ancd"+i).getBytes();
//        	
//        	//System.out.println(bytes.length);
//        	
//        	sorter.addNewRow(bytes, 3,String.valueOf(i).length()+1);
//        	
//    	}
//
//    	sorter.outputLimtMN(0,10);
    	
	}
}
