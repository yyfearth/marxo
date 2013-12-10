package marxo.entity.user;

public enum UserType {
	/**
	 * User who can interact with the system's form.
	 */
	PARTICIPANT,
	/**
	 * User who can manage other users.
	 */
	ADMIN,
	/**
	 * User who can interact with the system's form; defined by publishers.
	 */
	EVALUATOR,
	/**
	 * User who manage workflow.
	 */
	PUBLISHER,
}
