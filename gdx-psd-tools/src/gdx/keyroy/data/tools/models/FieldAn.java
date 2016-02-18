package gdx.keyroy.data.tools.models;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldAn {
	public boolean lock() default false;

}
