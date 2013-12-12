package marxo.entity.user;

import marxo.entity.workflow.RunStatus;

public abstract class RunnableEntity extends TenantChildEntity {
	protected RunStatus status = RunStatus.IDLE;

	public RunStatus getStatus() {
		return status;
	}

	public void setStatus(RunStatus status) {
		this.status = status;
	}

	/*
	Validation
	 */

	protected boolean isValidated = false;

	public Boolean getValidated() {
		return isValidated;
	}
}
