package marxo.serialization;

import org.joda.time.Period;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PeriodWriteConverter implements Converter<Period, Integer> {
	@Override
	public Integer convert(Period source) {
		return source.getMillis();
	}
}
