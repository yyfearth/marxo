package marxo.serialization;

import org.joda.time.Period;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PeriodReadConverter implements Converter<Integer, Period> {
	@Override
	public Period convert(Integer source) {
		return new Period(source.intValue());
	}
}
