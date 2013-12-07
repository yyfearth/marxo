package marxo.entity.link;

import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.entity.user.TenantChildEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Condition extends TenantChildEntity {
	@JsonProperty("left_operand")
	public String leftOperand;
	@JsonProperty("left_operand_type")
	public OperandType leftOperandType = OperandType.NONE;
	@JsonProperty("right_operand")
	public String rightOperand;
	@JsonProperty("right_operand_type")
	public OperandType rightOperandType = OperandType.NONE;
	public String operator;

	public static enum OperandType {
		NONE,
		NUMBER,
		LIKES_COUNT,
		RUN_STATUS,
	}

	@Override
	public void save() {
		throw new UnsupportedOperationException(String.format("%s should not be saved into database", this));
	}
}
