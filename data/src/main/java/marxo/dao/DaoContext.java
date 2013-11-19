package marxo.dao;

import com.rits.cloning.Cloner;
import marxo.tool.StringTool;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.HashMap;
import java.util.regex.Pattern;

public class DaoContext {
	/**
	 * An empty context, which will result empty query or update.
	 */
	public final static DaoContext EMPTY = new DaoContext();
	protected static final Cloner cloner = new Cloner();
	protected HashMap<String, DaoContextData> map = new HashMap<>();

	private DaoContext() {
	}

	public static DaoContext newInstance() {
		return new DaoContext();
	}

	public static DaoContext newInstance(DaoContext context) {
		return cloner.deepClone(context);
	}

	public DaoContext addContext(DaoContextData... daoContextDataList) {
		DaoContext context = newInstance(this);
		for (DaoContextData daoContextData : daoContextDataList) {
			context.map.put(daoContextData.field, daoContextData);
		}
		return context;
	}

	public DaoContext addContext(String field, DaoContextOperator operator, Object value) {
		return addContext(new DaoContextData(field, operator, value));
	}

	public DaoContext addContext(String field, Object value) {
		return addContext(field, DaoContextOperator.IS, value);
	}

	public Criteria toCriteria() {
		Criteria criteria = new Criteria();
		for (DaoContextData daoContextData : map.values()) {
			switch (daoContextData.operator) {
				case DEFAULT:
				case IN:
					criteria = criteria.and(daoContextData.field).in(daoContextData.value);
					break;
				case LIKE:
					String escapedName = StringTool.escapePatternCharacters(daoContextData.value.toString());
					Pattern pattern = Pattern.compile(".*" + escapedName + ".*", Pattern.CASE_INSENSITIVE);
					criteria = criteria.and(daoContextData.field).regex(pattern);
					break;
				case IS:
					criteria = criteria.and(daoContextData.field).is(daoContextData.value);
					break;
				default:
					throw new UnsupportedOperationException("toCriteria doesn't support [" + daoContextData.operator + "] operator.");
			}
		}
		return criteria;
	}

	public Query toQuery() {
		return Query.query(toCriteria());
	}

	public Update toUpdate() {
		Update update = new Update();
		for (DaoContextData daoContextData : map.values()) {
			switch (daoContextData.operator) {
				case DEFAULT:
				case SET:
					update = update.set(daoContextData.field, daoContextData.value);
					break;
				case UNSET:
					update = update.unset(daoContextData.field);
					break;
				default:
					throw new UnsupportedOperationException("toUpdate doesn't support [" + daoContextData.operator + "] operator.");
			}
		}
		return update;
	}
}
