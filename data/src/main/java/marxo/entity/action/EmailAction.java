package marxo.entity.action;

public class EmailAction extends Action {

	public EmailAction() {
		type = Type.EMAIL;
	}

	@Override
	public boolean act() {
		return true;
	}
}
