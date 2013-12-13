package marxo.entity.action;

public class WaitAction extends Action {

	public WaitAction() {
		type = Type.WAIT;
	}

	@Override
	public boolean act() {
		return true;
	}
}
