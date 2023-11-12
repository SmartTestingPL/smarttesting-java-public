package pl.smarttesting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adnotacja wskazująca, że dany test musi zostać zrefaktorowany w ramach pracy domowej.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Homework {
	
	/**
	 * @return opis co trzeba zrobić w pracy domowej
	 */
	String value() default "";
}
