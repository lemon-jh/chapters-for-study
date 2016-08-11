package leaderus.study.chapter.unsafe;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class UnsafeConstans {

	private static final Unsafe _UNSAFE;
	
    public static final int SIZE_OF_BOOLEAN = 1;
    
    public static final int SIZE_OF_INT = 4;
    
    public static final int SIZE_OF_LONG = 8;
    
    private static final long charArrayOffset ;
	
	static {
		Unsafe unsafe;
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
        } catch (Throwable cause) {
            unsafe = null;
        }
        _UNSAFE = unsafe;
        charArrayOffset = _UNSAFE.arrayBaseOffset(char[].class);
	}
	
	public static Unsafe getUnsafe() {
		return _UNSAFE;
	}
	
	public static void _putInt(Object o,long offset,int value) {
		_UNSAFE.putInt(o, offset, value);
	}
	
	public static void _putBoolean(Object o,long offset,boolean value) {
		_UNSAFE.putBoolean(o, offset, value);
	}
	
	public static void _putIntByField(Object o,Field field,int value) {
		_putInt(o,_UNSAFE.objectFieldOffset(field),value);
	}
	
	public static void _putBooleanByField(Object o,Field field,boolean value) {
		_UNSAFE.putBoolean(o, _UNSAFE.objectFieldOffset(field), value);
	}
	
	public static void _putStringByField(Object o,Field field,String value) {
		_putString(o, _UNSAFE.objectFieldOffset(field), value);
	}
	
	public static final void _putString(Object o,long offset,String str) {
		char [] values = str.toCharArray();
	    long bytesToCopy = values.length;
	    _UNSAFE.copyMemory(values, charArrayOffset, o, offset, bytesToCopy);
	}
	
}
