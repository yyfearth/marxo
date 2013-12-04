package marxo.entity.user;

import marxo.entity.workflow.RunStatus;

public abstract class RunnableEntity extends TenantChildEntity {
	public RunStatus status = RunStatus.IDLE;

	/*
	Validation
	 */

	protected boolean isValidated = false;

	public Boolean getValidated() {
		return isValidated;
	}

	public void wire() {

	}
}
