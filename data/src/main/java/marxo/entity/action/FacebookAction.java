package marxo.entity.action;

import marxo.exception.Errors;

public class FacebookAction extends Action {

	@Override
	public boolean validate(Errors errors) {
		if (tenantId == null) {
			errors.add(String.format("%s [%s] has no tenant", this, id));
		}

		if (getTenant().facebookData == null) {
			errors.add(String.format("%s [%s] has no tenant", this, id));
		}

		if (contentId == null) {
			errors.add(String.format("%s [%s] has no content", this, id));
		}

//		if (getTenant().facebookData.status == FacebookStatus.DISCONNTECTED) {
//			errors.add(String.format("%s Tenant [%s] has no Facebook access", this, getTenant()));
//		}

		return super.validate(errors);
	}
}
