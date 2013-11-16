package marxo.entity;

public enum UserType {
	/**
	 * User who can manage other users.
	 */
	Admin,
	/**
	 * User who can interact with the system's form.
	 */
	Participant,
	/**
	 * User who can interact with the system's form; defined by publishers.
	 */
	Evaluator,
	/**
	 * User who manage workflow.
	 */
	Publisher,
}
