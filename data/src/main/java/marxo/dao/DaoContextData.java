package marxo.dao;

/**
 * MapNode field and value pair to represent a criteria or updateFirst query.
 */
public class DaoContextData {
	public String field;
	public DaoContextOperator operator;
	public Object value;

	public DaoContextData(String field, DaoContextOperator operator, Object value) {
		this.field = field;
		this.operator = operator;
		this.value = value;
	}

	public DaoContextData(String field, DaoContextOperator operator) {
		this(field, operator, null);
	}

	/**
	 * Use DaoContextOperator.IS as the default operator.
	 */
	public DaoContextData(String field, Object value) {
		this(field, DaoContextOperator.DEFAULT, value);
	}
}
