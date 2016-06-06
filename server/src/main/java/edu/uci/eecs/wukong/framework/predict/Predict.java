package edu.uci.eecs.wukong.framework.predict;

public class Predict {
	private int componentId;
	private PredictType type;
	private PredictOperatorType operator;
	private String value;
	
	public Predict(int componentId, PredictType type,
			PredictOperatorType operator, String value) {
		this.componentId = componentId;
		this.type = type;
		this.operator = operator;
		this.value = value;
	}

	public int getComponentId() {
		return componentId;
	}

	public void setComponentId(int componentId) {
		this.componentId = componentId;
	}

	public PredictType getType() {
		return type;
	}

	public void setType(PredictType type) {
		this.type = type;
	}

	public PredictOperatorType getOperator() {
		return operator;
	}

	public void setOperator(PredictOperatorType operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}	
}
