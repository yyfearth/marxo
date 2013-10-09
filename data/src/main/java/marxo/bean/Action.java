package marxo.bean;

public class Action extends BasicEntity {
	public String type;
	public String content;

	@Override
	public void fillWithDefaultValues() {
		super.fillWithDefaultValues();

		if (type == null) {
			type = "None";
		}

		if (content == null) {
			content = "";
		}
	}
}
