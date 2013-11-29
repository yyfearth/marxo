package marxo.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiTestConfiguration {
	String value() default "http://localhost:8080/api/";

	String facebookId() default "635503102";

	String facebookToken() default "CAADCM9YpGYwBAGANsWfvdO3aEPcqWE8NM2AqeKZBjrjv3MquGBWMTDHBy8LKwd8klnZCigONqGubLv7ZAmX3dl5b2kmnx8b86ZAtK6XL63yb7BnxXd0OcYvZCjt6ZCINSd4wbdcwMzT3FHQfo91rAdWrKfSZBL47YDthTbmv1ZAbdZAZBdYaEkw6FG34gOL8P4qkkZD";
}
