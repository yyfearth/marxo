package marxo.entity.action;

public class TwitterAction extends TrackableAction {

	public TwitterAction() {
		type = Type.TWITTER;
	}

	@Override
	public boolean act() {
		return true;
	}
}
