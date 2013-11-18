package marxo.dao;

/**
 * MapNode field and value pair to represent a criteria or update query.
 */
public class DataPair {
	final public String field;
	final public DataPairOperator operator;
	final public Object value;

	/**
	 * Use DataPairOperator.IS as the default operator.
	 */
	public DataPair(String field, Object value) {
		this(field, DataPairOperator.IS, value);
	}

	public DataPair(String field, DataPairOperator operator, Object value) {
		this.field = field;
		this.operator = operator;
		this.value = value;
	}
}
