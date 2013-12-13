package marxo.entity.action;

public class TriggerAction extends Action {

	public TriggerAction() {
		type = Type.TRIGGER;
	}

	@Override
	public boolean act() {
		return false;
	}
}
