package logger;

/**
 * An exception thrown when there is an attempt to write to a closed logger. 
 * This exception is only used in {@link Logger#log(Level, String, boolean) Legger.log()} method.
 */
class ClosedLoggerException extends RuntimeException {
	/**
	 * Generated serialVersionUID from {@code RuntimeException}.
	 */
	private static final long serialVersionUID = 2803660197996707478L;

	/**
	 * Creates an exception with a message.
	 * 
	 * @param message The message.
	 */
	ClosedLoggerException(String message) {
		super(message);
	}
}
