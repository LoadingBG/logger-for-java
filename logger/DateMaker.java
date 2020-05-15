package logger;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Creates the date for a logger. 
 * The date is put at the top before any logging with the logger happens. 
 * The date is locale specific.
 */
class DateMaker {
	/**
	 * The locale for the date creation.
	 */
	private Locale locale;
	
	/**
	 * Creates an instance of this class with the given locale. 
	 */
	DateMaker(Locale locale) {
		this.locale = locale;
	}
	
	/**
	 * Creates a locale specific date for the logger.
	 * 
	 * @return The date as a String.
	 */
	String createDate() {
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG, locale);
		return formatter.format(new Date());
	}
}
