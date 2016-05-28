package psd.reflect;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 映射对象
 * 
 * @author roy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { TYPE, METHOD, FIELD })
public @interface PsdAn {
	// 引用 PSD 元素
	public String[] value() default {};

	// 引用 PSD 元素的下标
	public int index() default 0;
}
