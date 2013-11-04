package marxo.controller;

/**
 * Interface that allows MarxoInterceptor to call preHandle before a controller runs.
 */
public interface IInterceptroPreHandlable {
	/**
	 * Whenever MarxoInterceptor is in use. This method is called before all request handling methods in controllers.
	 */
	void preHandle();
}
