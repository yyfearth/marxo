package marxo.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiTestConfiguration {
	String value() default "http://localhost:8080/api/";

	String email() default "yyfearth@gmail.com";

	String password() default "2k96H29ECsJ05BJAkEGm6FC+UgjwVTc1qOd7SGG2uS8";
}
