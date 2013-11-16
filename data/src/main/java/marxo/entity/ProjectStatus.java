package marxo.entity;

public enum ProjectStatus {
	/**
	 * Created but not started yet.
	 */
	IDLE,
	/**
	 * Started but not done yet.
	 */
	STARTED,
	/**
	 * Paused by user.
	 */
	PAUSED,
	/**
	 * Stopped by user.
	 */
	STOPPED,
	/**
	 * All nodes has been processed.
	 */
	FINISHED,
	/**
	 * Shit happens
	 */
	ERROR,
}
