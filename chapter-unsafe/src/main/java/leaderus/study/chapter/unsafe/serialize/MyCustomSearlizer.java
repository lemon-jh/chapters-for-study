package leaderus.study.chapter.unsafe.serialize;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import leaderus.study.chapter.unsafe.ResourceUtils;
import leaderus.study.chapter.unsafe.UnsafeConstans;

/**
 * @author zhfq 序列化反序列化类
 */
public class MyCustomSearlizer {
	
	public MyCustomSearlizer() {
		
	}

	//根据类名和属性值，反序列化一个Java对象
	public Object deSearlize(String className,Map<String,String> props) {
		
		Map<String, String> map = ResourceUtils.getResource(className).getMap();

		Object o = null;
		
		try {
			
			Class<?> cls = Class.forName(map.get("classname"));
			
			o = UnsafeConstans.getUnsafe().allocateInstance(cls);
			
			Field [] fields = cls.getDeclaredFields();
			
			for (Field field : fields) {
				System.out.println(field.getName());
				String fieldType = map.get("prop."+field.getName());
				
				if(fieldType != null) {
					
					switch (fieldType) {
						case "int":
							UnsafeConstans._putIntByField(o, field, Integer.parseInt(props.get(field.getName())));
							break;
						case "String":
							//UnsafeConstans._putStringByField(o, field,props.get(field.getName()));
							break;
						case "boolean":
							UnsafeConstans._putBooleanByField(o, field,Boolean.parseBoolean(props.get(field.getName())));
							break;
						default:
							break;
					}
				}

			}
			
		} catch (ClassNotFoundException | InstantiationException e) {
			e.printStackTrace();
		}
		
		return o;
	}
    
    
    //根据类名和属性值，反序列化一个Java对象,返回其属性到Map里
	public Map<String,String> searlize(Object obj) {
		return null;
	}
	
	public static void main(String[] args) {
		
		Map<String,String> props = new HashMap<>();

		props.put("age", "1");
		props.put("name", "ZHANGSANN");
		props.put("hasMoney", "true");
		
		Person p = (Person) new MyCustomSearlizer().deSearlize("person", props);
		
		System.out.println(p);
		
		
	}
	
}
