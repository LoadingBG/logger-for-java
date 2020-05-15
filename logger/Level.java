package logger;

/**
 * Represents the level of severity for logs.
 */
enum Level {
	/**
	 * Generic text, no color.
	 */
	GENERIC, 
	/**
	 * Information, blue color.
	 */
	INFO, 
	/**
	 * Debugging information, green color.
	 */
	DEBUG, 
	/**
	 * Warnings, yellow colors.
	 */
	WARN, 
	/**
	 * Errors, red color.
	 */
	ERROR;
	
	/**
	 * ANSI code for blue-colored text.
	 */
	private static final String BLUE_TEXT_COLOR = "\u001b[36;1m";
	/**
	 * ANSI code for green-colored text.
	 */
	private static final String GREEN_TEXT_COLOR = "\u001b[32;1m";
	/**
	 * ANSI code for yellow-colored text.
	 */
	private static final String YELLOW_TEXT_COLOR = "\u001b[33;1m";
	/**
	 * ANSI code for red-colored text.
	 */
	private static final String RED_TEXT_COLOR = "\u001b[31;1m";
	
	/**
	 * Gives the specific prefix for the given level.
	 * 
	 * @param level The level.
	 * @param ansi Whether or not to include ANSI text coloring.
	 * @return The prefix for the level.
	 */
	static String getLevelPrefix(Level level, boolean ansi) {
		switch (level) {
		case INFO:
			return (ansi ? BLUE_TEXT_COLOR : "") + "[INFO]: ";
		case DEBUG:
			return (ansi ? GREEN_TEXT_COLOR : "") + "[DEBUG]: ";
		case WARN:
			return (ansi ? YELLOW_TEXT_COLOR : "") + "[WARN]: ";
		case ERROR:
			return (ansi ? RED_TEXT_COLOR : "") + "[ERROR]: ";
		case GENERIC:
		default:
			return "";
		}
	}
}
