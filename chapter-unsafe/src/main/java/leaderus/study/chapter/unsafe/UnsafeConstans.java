package leaderus.study.chapter.unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import sun.misc.Unsafe;

public class UnsafeConstans {

	private static final Unsafe _UNSAFE;

	public static final int _M = 1024 * 1024;
	
    public static final int SIZE_OF_BOOLEAN = 1;
    
    public static final int SIZE_OF_INT = 4;
    
    public static final int SIZE_OF_LONG = 8;
    
    public static final long byteArrayOffset ;
	
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
		byteArrayOffset = _UNSAFE.arrayBaseOffset(byte[].class);
	}
	
	public static Unsafe getUnsafe() {
		return _UNSAFE;
	}

	public static int _getInt(Object o,long offset){
		return _UNSAFE.getInt(o,offset);
	}

	public static int _getIntByField(Object o,Field field) {
		return _getInt(o,_UNSAFE.objectFieldOffset(field));
	}

	public static boolean _getBoolean(Object o,long offset) {
		return _UNSAFE.getBoolean(o,offset);
	}

	public static boolean _getBooleanByField(Object o,Field field) {
		return _getBoolean(o,_UNSAFE.objectFieldOffset(field));
	}

	public static Object _getObject(Object o,long offset) {
		return _UNSAFE.getObject(o,offset);
	}

	public static Object _getObjectByField(Object o,Field field) {
		return _getObject(o,_UNSAFE.objectFieldOffset(field));
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
		_putBoolean(o, _UNSAFE.objectFieldOffset(field), value);
	}
	
	public static void _putObjectByField(Object o,Field field,Object value) {
		_putObject(o, _UNSAFE.objectFieldOffset(field), value);
	}
	
	private static final void _putObject(Object o,long offset,Object value) {
		_UNSAFE.putObject(o, offset, value);
	}

	public static long _byteArrayCopy(Object tar,Object des,int len,long pos) {
		System.out.println(UnsafeConstans.byteArrayOffset +", len = " + len);
		_UNSAFE.copyMemory(tar,0,des,pos+UnsafeConstans.byteArrayOffset,len);
		return pos + len;
	}
	
	public static Method getMethod(Class<?> cls, String name, Class<?>... params) throws Exception {
		Method m = cls.getDeclaredMethod(name, params);
		m.setAccessible(true);
		return m;
	}
	
	public static long roundTo4096(long i) {
		return (i + 0xfffL) & ~0xfffL;
	}
}
