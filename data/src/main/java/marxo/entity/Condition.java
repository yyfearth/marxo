package marxo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Condition extends TenantChildEntity {
	@JsonProperty("left_operand")
	public String leftOperand;
	@JsonProperty("left_operand_type")
	public String leftOperandType;
	@JsonProperty("right_operand")
	public String rightOperand;
	@JsonProperty("right_operand_type")
	public String rightOperandType;
	public String operator;
}
