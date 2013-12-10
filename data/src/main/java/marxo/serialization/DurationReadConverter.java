package marxo.serialization;

import org.joda.time.Duration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DurationReadConverter implements Converter<Long, Duration> {
	@Override
	public Duration convert(Long source) {
		return new Duration(source);
	}
}
