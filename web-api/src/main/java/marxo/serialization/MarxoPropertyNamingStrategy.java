package marxo.serialization;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarxoPropertyNamingStrategy extends PropertyNamingStrategy {
	static final Pattern pattern = Pattern.compile("[A-Z]");

	@Override
	public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
		return convert(defaultName);
	}

	@Override
	public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
		return convert(defaultName);
	}

	@Override
	public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
		return convert(defaultName);
	}

	@Override
	public String nameForConstructorParameter(MapperConfig<?> config, AnnotatedParameter ctorParam, String defaultName) {
		return convert(defaultName);
	}

	String convert(String defaultName) {
		Matcher matcher = pattern.matcher(defaultName);
		StringBuffer stringBuffer = new StringBuffer();
		while (matcher.find()) {
			String captured = matcher.group();
			matcher.appendReplacement(stringBuffer, "_" + captured.toLowerCase());
		}
		matcher.appendTail(stringBuffer);
		return stringBuffer.toString();
	}
}
