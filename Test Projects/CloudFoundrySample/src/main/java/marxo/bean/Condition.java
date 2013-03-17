package marxo.bean;

public class Condition {
	String leftOperand, leftOperandType, rightOperand, rightOperandType, operator;

	public String getLeftOperand() {
		return leftOperand;
	}

	public void setLeftOperand(String leftOperand) {
		this.leftOperand = leftOperand;
	}

	public String getLeftOperandType() {
		return leftOperandType;
	}

	public void setLeftOperandType(String leftOperandType) {
		this.leftOperandType = leftOperandType;
	}

	public String getRightOperand() {
		return rightOperand;
	}

	public void setRightOperand(String rightOperand) {
		this.rightOperand = rightOperand;
	}

	public String getRightOperandType() {
		return rightOperandType;
	}

	public void setRightOperandType(String rightOperandType) {
		this.rightOperandType = rightOperandType;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
}
