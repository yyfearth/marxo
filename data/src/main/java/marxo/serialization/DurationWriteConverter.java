package marxo.serialization;

import org.joda.time.Duration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DurationWriteConverter implements Converter<Duration, Long> {
	@Override
	public Long convert(Duration source) {
		return source.getMillis();
	}
}
