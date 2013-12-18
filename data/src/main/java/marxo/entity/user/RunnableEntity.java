package marxo.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import marxo.entity.workflow.RunStatus;
import org.springframework.data.annotation.Transient;

public abstract class RunnableEntity extends TenantChildEntity {
	protected RunStatus status = RunStatus.IDLE;

	public RunStatus getStatus() {
		return status;
	}

	public void setStatus(RunStatus status) {
		this.status = status;
	}

	@JsonIgnore
	@Transient
	public boolean doesContinue() {
		switch (status) {
			case FINISHED:
			case TRACKED:
				return true;
			default:
				return false;
		}
	}

	@JsonIgnore
	@Transient
	public boolean hasError() {
		return status.equals(RunStatus.ERROR);
	}

	@JsonIgnore
	@Transient
	public boolean isFinished() {
		return status.equals(RunStatus.FINISHED);
	}

	@JsonIgnore
	@Transient
	public boolean isTracked() {
		return status.equals(RunStatus.TRACKED);
	}

	@JsonIgnore
	@Transient
	public boolean isRunning() {
		switch (status) {
			case STARTED:
			case TRACKED:
				return true;
			default:
				return false;
		}
	}

	/*
	Validation
	 */

	protected boolean isValidated = false;

	public Boolean getValidated() {
		return isValidated;
	}
}
