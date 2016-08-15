package leaderus.study.chapter.unsafe.sort;

import java.util.Random;

/**
 * Created by zhang on 2016/8/13.
 */
public class TestSorter {
	
	private static final String base = "abcdefghijklmnopqrstuvwxyz";
	
	private static final int loop = 10_000_00;
	// 先随机生成1000万的随机Row，放入RowDataSorter中，然后输出 从100万开始的1000条记录，并记录整个时间消耗。

	private static void sort() {
		
		Random random = new Random();

		long start = System.currentTimeMillis();

		RowDataSorter sorter = new RowDataSorter(RowDataSorter.storeEnum.unsafe, "src/list");

		long end = System.currentTimeMillis();

		System.out.println("==> init memory cost time = " + (end - start));
		
		String temp = "";
		
		int len = 0;
		
		byte [] bytes = null;

		for (int i = 0; i < loop; i++) {
			temp = getRandomString(random.nextInt(7)+1);
			len = random.nextInt(100000000);
			bytes = (temp+len).getBytes();
			sorter.addNewRow(bytes, temp.length(),String.valueOf(len).length());
		}

		sorter.outputLimtMN(1, 99);

		sorter.close();

		end = System.currentTimeMillis();

		System.out.println("==> total cost time = " + (end - start));
	}

	private static String getRandomString(int length) { // length表示生成字符串的长度
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		sort();
	}
}

