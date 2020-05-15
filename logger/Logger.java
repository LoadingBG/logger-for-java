package logger;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Closeable;

import java.util.Locale;

/**
 * A simple logger for java. 
 * 
 * <p>The logger can log to standart output streams or to a .log file. 
 * When logging to the standart streams, 
 * there is an option to enable ANSI codes. 
 * When logging to a .log file, the logger will create a .log file 
 * with the given name if there is no .log file with the same name. 
 * The path to a folder can be specified to search for the .log file there.</p>
 * 
 * <p>The logger can log in 4 different levels:</p>
 * <ul>
 * 	<li>INFO: used to log information.</li>
 * 	<li>DEBUG: used to log debugging information.</li>
 * 	<li>WARNING: used to log warnings.</li>
 * 	<li>ERROR: used to log errors.</li>
 * </ul>
 */
public class Logger implements Closeable, AutoCloseable {
	/**
	 * The output stream used to write information.
	 */
	private OutputStream stream;
	
	/**
	 * The date maker. 
	 * @see DateMaker
	 */
	private DateMaker dateMaker;
	
	/**
	 * Whether or not this logger is closed.
	 */
	private boolean isClosed = false;
	
	/**
	 * Whether or not this logger should output with ANSI colors.
	 */
	private boolean ansiMode = false;
	
	/**
	 * The line separator for the machine.
	 */
	private String lineSep = System.getProperty("line.separator");
	
	/**
	 * Creates a logger with the given name. 
	 * The name is used to find the .log file 
	 * where to log messages. 
	 * If the file does not exist, it will be created.
	 * 
	 * @param name The name of the logger.
	 */
	public Logger(String name) {
		this(name, Locale.getDefault(), ".", false);
	}
	
	/**
	 * Creates a logger with the given name. 
	 * The name is used to find the .log file 
	 * where to log messages. 
	 * If the file does not exist, it will be created.
	 * 
	 * @param name The name of the logger.
	 * @param ansi Whether or not to use ANSI text coloring.
	 */
	public Logger(String name, boolean ansi) {
		this(name, Locale.getDefault(), ".", ansi);
	}
	
	/**
	 * Creates a logger with the given name. 
	 * The name is used to find the .log file 
	 * where to log messages. 
	 * If the file does not exist, it will be created.
	 * 
	 * @param name The name of the logger.
	 * @param locale The locale of the machine. 
	 * 	The locale is used to create the date.
	 * 
	 * @see DateMaker
	 */
	public Logger(String name, Locale locale) {
		this(name, locale, ".", false);
	}
	
	/**
	 * Creates a logger with the given name. 
	 * The name is used to find the .log file 
	 * where to log messages. 
	 * If the file does not exist, it will be created.
	 * 
	 * @param name The name of the logger.
	 * @param locale The locale of the machine. 
	 * 	The locale is used to create the date.
	 * @param ansi Whether or not to use ANSI text coloring.
	 * 
	 * @see DateMaker
	 */
	public Logger(String name, Locale locale, boolean ansi) {
		this(name, locale, ".", ansi);
	}
	
	/**
	 * Creates a logger with the given name. 
	 * The name is used to find the .log file 
	 * where to log messages. 
	 * If the file does not exist, it will be created.
	 * 
	 * @param name The name of the logger.
	 * @param locale The locale of the machine. 
	 * 	The locale is used to create the date.
	 * @param path The path to the directory 
	 * 	where to search for the .log file.
	 * 
	 * @see DateMaker
	 */
	public Logger(String name, Locale locale, String path) {
		this(name, locale, path, false);
	}
	
	/**
	 * Creates a logger with the given name. 
	 * The name is used to find the .log file 
	 * where to log messages. 
	 * If the file does not exist, it will be created.
	 * 
	 * @param name The name of the logger.
	 * @param locale The locale of the machine. 
	 * 	The locale is used to create the date.
	 * @param path The path to the directory 
	 * 	where to search for the .log file.
	 * @param ansi Whether or not to use ANSI text coloring.
	 * 
	 * @see DateMaker
	 */
	public Logger(String name, Locale locale, String path, boolean ansi) {
		dateMaker = new DateMaker(locale);
		ansiMode = ansi;
		
		// Get the security manager
		// Used to check if the logger can write to the file
		System.getSecurityManager();
		
		try {
			// Check the path
			File pathFile = new File(path);
			if (!pathFile.isDirectory()) {
				throw new IllegalArgumentException("The path provided does not lead to a directory.");
			}
			
			File file = new File(path + File.separator + name + ".log");
			// Create the file if it does not exist
			if (!file.exists()) {
				file.createNewFile();
			}
			stream = new FileOutputStream(file, true);
			
			// Log the date
			log(Level.GENERIC, dateMaker.createDate());
		} catch (IOException $) {
			// Not possible because of creation of file
		} catch (SecurityException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * Creates a logger which writes to the given print stream.
	 * 
	 * @param printStream The print stream.
	 */
	public Logger(PrintStream printStream) {
		this(printStream, Locale.getDefault(), false);
	}
	
	/**
	 * Creates a logger which writes to the given print stream.
	 * 
	 * @param printStream The print stream.
	 * @param ansi Whether or not the logger should use ANSI colors.
	 */
	public Logger(PrintStream printStream, boolean ansi) {
		this(printStream, Locale.getDefault(), ansi);
	}
	
	/**
	 * Creates a logger whhich writes to the given print stream.
	 * 
	 * @param printStream The print stream.
	 * @param locale The locale of the machine. 
	 * 	The locale is used to create the date.
	 */
	public Logger(PrintStream printStream, Locale locale) {
		this(printStream, locale, false);
	}
	
	public Logger(PrintStream printStream, Locale locale, boolean ansi) {
		stream = printStream;
		dateMaker = new DateMaker(locale);
		ansiMode = ansi;
		
		log(Level.GENERIC, dateMaker.createDate());
	}
	
	/**
	 * Closes the logger. Once the logger is closed, 
	 * it cannot be used again. it is recommended to 
	 * close the logger at the end of execution 
	 */
	@Override
	public void close() {
		log(Level.GENERIC, "=====end=====" + lineSep);
		try {
			stream.close();
		} catch (IOException e) {
			err(e);
		}
		isClosed = true;
	}
	
	/**
	 * Closes the logger if it is not already when the object is finalized.
	 */
	@Deprecated
	@Override
	protected void finalize() throws Throwable {
		if (!isClosed) {
			close();
		}
	}
	
	/**
	 * Logs the message with the given level.
	 * 
	 * @param level The level of the message.
	 * @param message The message.
	 */
	void log(Level level, String message) {
		try {
			// If the logger is not closed
			if (!isClosed) {
				// Get prefix
				String levelPrefix = Level.getLevelPrefix(level, ansiMode);
				// Set space length for new line
				String spaces = "";
				if (levelPrefix.length() > 0) {
					spaces = new String(new char[levelPrefix.length() - (ansiMode ? 7 : 0)])
							.replaceAll("\0", " ");
				}
				
				// Write prefix
				stream.write(levelPrefix.getBytes());
				// Write message
				String newMessage = message.replaceAll("\n|\r\n?", lineSep + spaces)
						+ (ansiMode ? "\u001b[0m" : "");
				stream.write(newMessage.getBytes());
				stream.write(lineSep.getBytes());
				// Flush
				stream.flush();
			} else {
				// If the logger is closed, throw an exception.
				throw new ClosedLoggerException("The logger is closed.");
			}
		} catch (IOException $) {
			// Not possible because everyting is checked before logging
		}
	}
	
	
	
	
	/**
	 * Logs the boolean with a severity level of INFO.
	 * 
	 * @param bool The boolean.
	 */
	public void info(boolean bool) {
		log(Level.INFO, Boolean.toString(bool));
	}
	
	/**
	 * Logs the byte with a severity level of INFO.
	 * 
	 * @param byteInteger The byte.
	 */
	public void info(byte byteInteger) {
		log(Level.INFO, Byte.toString(byteInteger));
	}
	
	/**
	 * Logs the short with a severity level of INFO.
	 * 
	 * @param shortInteger The short.
	 */
	public void info(short shortInteger) {
		log(Level.INFO, Short.toString(shortInteger));
	}
	
	/**
	 * Logs the int with a severity level of INFO.
	 * 
	 * @param integer The int.
	 */
	public void info(int integer) {
		log(Level.INFO, Integer.toString(integer));
	}
	
	/**
	 * Logs the long with a severity level of INFO.
	 * 
	 * @param longInteger The long.
	 */
	public void info(long longInteger) {
		log(Level.INFO, Long.toString(longInteger));
	}
	
	/**
	 * Logs the float with a severity level of INFO.
	 * 
	 * @param floatFraction The float.
	 */
	public void info(float floatFraction) {
		log(Level.INFO, Float.toString(floatFraction));
	}
	
	/**
	 * Logs the double with a severity level of INFO.
	 * 
	 * @param fraction The double.
	 */
	public void info(double fraction) {
		log(Level.INFO, Double.toString(fraction));
	}
	
	/**
	 * Logs the char with a severity level of INFO.
	 * 
	 * @param character The char.
	 */
	public void info(char character) {
		log(Level.INFO, Character.toString(character));
	}
	
	/**
	 * Logs the object with severity level of INFO.
	 * 
	 * @param object The object.
	 */
	public void info(Object object) {
		if (object == null) {
			log(Level.INFO, "null");
		} else {
			log(Level.INFO, object.toString());
		}
	}
	
	/**
	 * Logs the throwable with severity level of INFO.
	 * 
	 * @param throwable The throwable.
	 */
	public void info(Throwable throwable) {
		StringBuilder builder = new StringBuilder();
		builder.append(throwable.toString());
		
		for (StackTraceElement element : throwable.getStackTrace()) {
			builder.append("\tat ").append(element).append("\n");
		}
		
		log(Level.INFO, builder.toString());
	}
	
	/**
	 * Logs the boolean array with severity level of INFO. 
	 * 
	 * @param boolArray The boolean array.
	 */
	public void info(boolean[] boolArray) {
		StringBuilder res = new StringBuilder("boolean[] {");
		
		for (boolean bool : boolArray) {
			res.append(bool).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.INFO, res.toString());
	}
	
	/**
	 * Logs the byte array with severity level of INFO. 
	 * 
	 * @param byteArray The byte array.
	 */
	public void info(byte[] byteArray) {
		StringBuilder res = new StringBuilder("byte[] {");
		
		for (byte byteInteger : byteArray) {
			res.append(byteInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.INFO, res.toString());
	}
	
	/**
	 * Logs the short array with severity level of INFO. 
	 * 
	 * @param shortArray The short array.
	 */
	public void info(short[] shortArray) {
		StringBuilder res = new StringBuilder("short[] {");
		
		for (short shortInteger : shortArray) {
			res.append(shortInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.INFO, res.toString());
	}
	
	/**
	 * Logs the int array with severity level of INFO. 
	 * 
	 * @param intArray The int array.
	 */
	public void info(int[] intArray) {
		StringBuilder res = new StringBuilder("int[] {");
		
		for (int integer : intArray) {
			res.append(integer).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.INFO, res.toString());
	}
	
	/**
	 * Logs the long array with severity level of INFO. 
	 * 
	 * @param longArray The long array.
	 */
	public void info(long[] longArray) {
		StringBuilder res = new StringBuilder("long[] {");
		
		for (long longInteger : longArray) {
			res.append(longInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.INFO, res.toString());
	}
	
	/**
	 * Logs the float array with severity level of INFO. 
	 * 
	 * @param floatArray The float array.
	 */
	public void info(float[] floatArray) {
		StringBuilder res = new StringBuilder("float[] {");
		
		for (float floatFraction : floatArray) {
			res.append(floatFraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.INFO, res.toString());
	}
	
	/**
	 * Logs the double array with severity level of INFO. 
	 * 
	 * @param doubleArray The double array.
	 */
	public void info(double[] doubleArray) {
		StringBuilder res = new StringBuilder("double[] {");
		
		for (double fraction : doubleArray) {
			res.append(fraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.INFO, res.toString());
	}
	
	/**
	 * Logs the char array with severity level of INFO. 
	 * 
	 * @param charArray The char array.
	 */
	public void info(char[] charArray) {
		StringBuilder res = new StringBuilder("char[] {");
		
		for (char character : charArray) {
			res.append(character).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.INFO, res.toString());
	}
	
	/**
	 * Logs the object array with severity level INFO.
	 * 
	 * @param objectArray The object array.
	 */
	public void info(Object[] objectArray) {
		StringBuilder res = new StringBuilder("Object[] {");
		
		for (Object object : objectArray) {
			res.append(object).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.INFO, res.toString());
	}
	
	
	
	
	
	
	
	/**
	 * Logs the boolean with a severity level of DEBUG.
	 * 
	 * @param bool The boolean.
	 */
	public void debug(boolean bool) {
		log(Level.DEBUG, Boolean.toString(bool));
	}
	
	/**
	 * Logs the byte with a severity level of DEBUG.
	 * 
	 * @param byteInteger The byte.
	 */
	public void debug(byte byteInteger) {
		log(Level.DEBUG, Byte.toString(byteInteger));
	}
	
	/**
	 * Logs the short with a severity level of DEBUG.
	 * 
	 * @param shortInteger The short.
	 */
	public void debug(short shortInteger) {
		log(Level.DEBUG, Short.toString(shortInteger));
	}
	
	/**
	 * Logs the int with a severity level of DEBUG.
	 * 
	 * @param integer The int.
	 */
	public void debug(int integer) {
		log(Level.DEBUG, Integer.toString(integer));
	}
	
	/**
	 * Logs the long with a severity level of DEBUG.
	 * 
	 * @param longInteger The long.
	 */
	public void debug(long longInteger) {
		log(Level.DEBUG, Long.toString(longInteger));
	}
	
	/**
	 * Logs the float with a severity level of DEBUG.
	 * 
	 * @param floatFraction The float.
	 */
	public void debug(float floatFraction) {
		log(Level.DEBUG, Float.toString(floatFraction));
	}
	
	/**
	 * Logs the double with a severity level of DEBUG.
	 * 
	 * @param fraction The double.
	 */
	public void debug(double fraction) {
		log(Level.DEBUG, Double.toString(fraction));
	}
	
	/**
	 * Logs the char with a severity level of DEBUG.
	 * 
	 * @param character The char.
	 */
	public void debug(char character) {
		log(Level.DEBUG, Character.toString(character));
	}
	
	/**
	 * Logs the object with severity level of DEBUG.
	 * 
	 * @param object The object.
	 */
	public void debug(Object object) {
		if (object == null) {
			log(Level.DEBUG, "null");
		} else {
			log(Level.DEBUG, object.toString());
		}
	}
	
	/**
	 * Logs the throwable with severity level of DEBUG.
	 * 
	 * @param throwable The throwable.
	 */
	public void debug(Throwable throwable) {
		StringBuilder builder = new StringBuilder();
		builder.append(throwable.toString());
		
		for (StackTraceElement element : throwable.getStackTrace()) {
			builder.append("\tat ").append(element).append("\n");
		}
		
		log(Level.DEBUG, builder.toString());
	}
	
	/**
	 * Logs the boolean array with severity level of DEBUG. 
	 * 
	 * @param boolArray The boolean array.
	 */
	public void debug(boolean[] boolArray) {
		StringBuilder res = new StringBuilder("boolean[] {");
		
		for (boolean bool : boolArray) {
			res.append(bool).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.DEBUG, res.toString());
	}
	
	/**
	 * Logs the byte array with severity level of DEBUG. 
	 * 
	 * @param byteArray The byte array.
	 */
	public void debug(byte[] byteArray) {
		StringBuilder res = new StringBuilder("byte[] {");
		
		for (byte byteInteger : byteArray) {
			res.append(byteInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.DEBUG, res.toString());
	}
	
	/**
	 * Logs the short array with severity level of DEBUG. 
	 * 
	 * @param shortArray The short array.
	 */
	public void debug(short[] shortArray) {
		StringBuilder res = new StringBuilder("short[] {");
		
		for (short shortInteger : shortArray) {
			res.append(shortInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.DEBUG, res.toString());
	}
	
	/**
	 * Logs the int array with severity level of DEBUG. 
	 * 
	 * @param intArray The int array.
	 */
	public void debug(int[] intArray) {
		StringBuilder res = new StringBuilder("int[] {");
		
		for (int integer : intArray) {
			res.append(integer).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.DEBUG, res.toString());
	}
	
	/**
	 * Logs the long array with severity level of DEBUG. 
	 * 
	 * @param longArray The long array.
	 */
	public void debug(long[] longArray) {
		StringBuilder res = new StringBuilder("long[] {");
		
		for (long longInteger : longArray) {
			res.append(longInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.DEBUG, res.toString());
	}
	
	/**
	 * Logs the float array with severity level of DEBUG. 
	 * 
	 * @param floatArray The float array.
	 */
	public void debug(float[] floatArray) {
		StringBuilder res = new StringBuilder("float[] {");
		
		for (float floatFraction : floatArray) {
			res.append(floatFraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.DEBUG, res.toString());
	}
	
	/**
	 * Logs the double array with severity level of DEBUG. 
	 * 
	 * @param doubleArray The double array.
	 */
	public void debug(double[] doubleArray) {
		StringBuilder res = new StringBuilder("double[] {");
		
		for (double fraction : doubleArray) {
			res.append(fraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.DEBUG, res.toString());
	}
	
	/**
	 * Logs the char array with severity level of DEBUG. 
	 * 
	 * @param charArray The char array.
	 */
	public void debug(char[] charArray) {
		StringBuilder res = new StringBuilder("char[] {");
		
		for (char character : charArray) {
			res.append(character).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.DEBUG, res.toString());
	}
	
	/**
	 * Logs the object array with severity level DEBUG.
	 * 
	 * @param objectArray The object array.
	 */
	public void debug(Object[] objectArray) {
		StringBuilder res = new StringBuilder("Object[] {");
		
		for (Object object : objectArray) {
			res.append(object).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.DEBUG, res.toString());
	}
	
	
	
	
	
	/**
	 * Logs the boolean with a severity level of WARNING.
	 * 
	 * @param bool The boolean.
	 */
	public void warn(boolean bool) {
		log(Level.WARN, Boolean.toString(bool));
	}
	
	/**
	 * Logs the byte with a severity level of WARNING.
	 * 
	 * @param byteInteger The byte.
	 */
	public void warn(byte byteInteger) {
		log(Level.WARN, Byte.toString(byteInteger));
	}
	
	/**
	 * Logs the short with a severity level of WARNING.
	 * 
	 * @param shortInteger The short.
	 */
	public void warn(short shortInteger) {
		log(Level.WARN, Short.toString(shortInteger));
	}
	
	/**
	 * Logs the int with a severity level of WARNING.
	 * 
	 * @param integer The int.
	 */
	public void warn(int integer) {
		log(Level.WARN, Integer.toString(integer));
	}
	
	/**
	 * Logs the long with a severity level of WARNING.
	 * 
	 * @param longInteger The long.
	 */
	public void warn(long longInteger) {
		log(Level.WARN, Long.toString(longInteger));
	}
	
	/**
	 * Logs the float with a severity level of WARNING.
	 * 
	 * @param floatFraction The float.
	 */
	public void warn(float floatFraction) {
		log(Level.WARN, Float.toString(floatFraction));
	}
	
	/**
	 * Logs the double with a severity level of WARNING.
	 * 
	 * @param fraction The double.
	 */
	public void warn(double fraction) {
		log(Level.WARN, Double.toString(fraction));
	}
	
	/**
	 * Logs the char with a severity level of WARNING.
	 * 
	 * @param character The char.
	 */
	public void warn(char character) {
		log(Level.WARN, Character.toString(character));
	}
	
	/**
	 * Logs the object with severity level of WARNING.
	 * 
	 * @param object The object.
	 */
	public void warn(Object object) {
		if (object == null) {
			log(Level.WARN, "null");
		} else {
			log(Level.WARN, object.toString());
		}
	}
	
	/**
	 * Logs the throwable with severity level of WARNING.
	 * 
	 * @param throwable The throwable.
	 */
	public void warn(Throwable throwable) {
		StringBuilder builder = new StringBuilder();
		builder.append(throwable.toString());
		
		for (StackTraceElement element : throwable.getStackTrace()) {
			builder.append("\tat ").append(element).append("\n");
		}
		
		log(Level.WARN, builder.toString());
	}
	
	/**
	 * Logs the boolean array with severity level of WARNING. 
	 * 
	 * @param boolArray The boolean array.
	 */
	public void warn(boolean[] boolArray) {
		StringBuilder res = new StringBuilder("boolean[] {");
		
		for (boolean bool : boolArray) {
			res.append(bool).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.WARN, res.toString());
	}
	
	/**
	 * Logs the byte array with severity level of WARNING. 
	 * 
	 * @param byteArray The byte array.
	 */
	public void warn(byte[] byteArray) {
		StringBuilder res = new StringBuilder("byte[] {");
		
		for (byte byteInteger : byteArray) {
			res.append(byteInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.WARN, res.toString());
	}
	
	/**
	 * Logs the short array with severity level of WARNING. 
	 * 
	 * @param shortArray The short array.
	 */
	public void warn(short[] shortArray) {
		StringBuilder res = new StringBuilder("short[] {");
		
		for (short shortInteger : shortArray) {
			res.append(shortInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.WARN, res.toString());
	}
	
	/**
	 * Logs the int array with severity level of WARNING. 
	 * 
	 * @param intArray The int array.
	 */
	public void warn(int[] intArray) {
		StringBuilder res = new StringBuilder("int[] {");
		
		for (int integer : intArray) {
			res.append(integer).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.WARN, res.toString());
	}
	
	/**
	 * Logs the long array with severity level of WARNING. 
	 * 
	 * @param longArray The long array.
	 */
	public void warn(long[] longArray) {
		StringBuilder res = new StringBuilder("long[] {");
		
		for (long longInteger : longArray) {
			res.append(longInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.WARN, res.toString());
	}
	
	/**
	 * Logs the float array with severity level of WARNING. 
	 * 
	 * @param floatArray The float array.
	 */
	public void warn(float[] floatArray) {
		StringBuilder res = new StringBuilder("float[] {");
		
		for (float floatFraction : floatArray) {
			res.append(floatFraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.WARN, res.toString());
	}
	
	/**
	 * Logs the double array with severity level of WARNING. 
	 * 
	 * @param doubleArray The double array.
	 */
	public void warn(double[] doubleArray) {
		StringBuilder res = new StringBuilder("double[] {");
		
		for (double fraction : doubleArray) {
			res.append(fraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.WARN, res.toString());
	}
	
	/**
	 * Logs the char array with severity level of WARNING. 
	 * 
	 * @param charArray The char array.
	 */
	public void warn(char[] charArray) {
		StringBuilder res = new StringBuilder("char[] {");
		
		for (char character : charArray) {
			res.append(character).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.WARN, res.toString());
	}
	
	/**
	 * Logs the object array with severity level WARNING.
	 * 
	 * @param objectArray The object array.
	 */
	public void warn(Object[] objectArray) {
		StringBuilder res = new StringBuilder("Object[] {");
		
		for (Object object : objectArray) {
			res.append(object).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.WARN, res.toString());
	}
	
	
	
	
	
	/**
	 * Logs the boolean with a severity level of ERROR.
	 * 
	 * @param bool The boolean.
	 */
	public void err(boolean bool) {
		log(Level.ERROR, Boolean.toString(bool));
	}
	
	/**
	 * Logs the byte with a severity level of ERROR.
	 * 
	 * @param byteInteger The byte.
	 */
	public void err(byte byteInteger) {
		log(Level.ERROR, Byte.toString(byteInteger));
	}
	
	/**
	 * Logs the short with a severity level of ERROR.
	 * 
	 * @param shortInteger The short.
	 */
	public void err(short shortInteger) {
		log(Level.ERROR, Short.toString(shortInteger));
	}
	
	/**
	 * Logs the int with a severity level of ERROR.
	 * 
	 * @param integer The int.
	 */
	public void err(int integer) {
		log(Level.ERROR, Integer.toString(integer));
	}
	
	/**
	 * Logs the long with a severity level of ERROR.
	 * 
	 * @param longInteger The long.
	 */
	public void err(long longInteger) {
		log(Level.ERROR, Long.toString(longInteger));
	}
	
	/**
	 * Logs the float with a severity level of ERROR.
	 * 
	 * @param floatFraction The float.
	 */
	public void err(float floatFraction) {
		log(Level.ERROR, Float.toString(floatFraction));
	}
	
	/**
	 * Logs the double with a severity level of ERROR.
	 * 
	 * @param fraction The double.
	 */
	public void err(double fraction) {
		log(Level.ERROR, Double.toString(fraction));
	}
	
	/**
	 * Logs the char with a severity level of ERROR.
	 * 
	 * @param character The char.
	 */
	public void err(char character) {
		log(Level.ERROR, Character.toString(character));
	}
	
	/**
	 * Logs the object with severity level of ERROR.
	 * 
	 * @param object The object.
	 */
	public void err(Object object) {
		if (object == null) {
			log(Level.ERROR, "null");
		} else {
			log(Level.ERROR, object.toString());
		}
	}
	
	/**
	 * Logs the throwable with severity level of ERROR.
	 * 
	 * @param throwable The throwable.
	 */
	public void err(Throwable throwable) {
		StringBuilder builder = new StringBuilder();
		builder.append(throwable.toString());
		
		for (StackTraceElement element : throwable.getStackTrace()) {
			builder.append("\tat ").append(element).append("\n");
		}
		
		log(Level.ERROR, builder.toString());
	}
	
	/**
	 * Logs the boolean array with severity level of ERROR. 
	 * 
	 * @param boolArray The boolean array.
	 */
	public void err(boolean[] boolArray) {
		StringBuilder res = new StringBuilder("boolean[] {");
		
		for (boolean bool : boolArray) {
			res.append(bool).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.ERROR, res.toString());
	}
	
	/**
	 * Logs the byte array with severity level of ERROR. 
	 * 
	 * @param byteArray The byte array.
	 */
	public void err(byte[] byteArray) {
		StringBuilder res = new StringBuilder("byte[] {");
		
		for (byte byteInteger : byteArray) {
			res.append(byteInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.ERROR, res.toString());
	}
	
	/**
	 * Logs the short array with severity level of ERROR. 
	 * 
	 * @param shortArray The short array.
	 */
	public void err(short[] shortArray) {
		StringBuilder res = new StringBuilder("short[] {");
		
		for (short shortInteger : shortArray) {
			res.append(shortInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.ERROR, res.toString());
	}
	
	/**
	 * Logs the int array with severity level of ERROR. 
	 * 
	 * @param intArray The int array.
	 */
	public void err(int[] intArray) {
		StringBuilder res = new StringBuilder("int[] {");
		
		for (int integer : intArray) {
			res.append(integer).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.ERROR, res.toString());
	}
	
	/**
	 * Logs the long array with severity level of ERROR. 
	 * 
	 * @param longArray The long array.
	 */
	public void err(long[] longArray) {
		StringBuilder res = new StringBuilder("long[] {");
		
		for (long longInteger : longArray) {
			res.append(longInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.ERROR, res.toString());
	}
	
	/**
	 * Logs the float array with severity level of ERROR. 
	 * 
	 * @param floatArray The float array.
	 */
	public void err(float[] floatArray) {
		StringBuilder res = new StringBuilder("float[] {");
		
		for (float floatFraction : floatArray) {
			res.append(floatFraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.ERROR, res.toString());
	}
	
	/**
	 * Logs the double array with severity level of ERROR. 
	 * 
	 * @param doubleArray The double array.
	 */
	public void err(double[] doubleArray) {
		StringBuilder res = new StringBuilder("double[] {");
		
		for (double fraction : doubleArray) {
			res.append(fraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.ERROR, res.toString());
	}
	
	/**
	 * Logs the char array with severity level of ERROR. 
	 * 
	 * @param charArray The char array.
	 */
	public void err(char[] charArray) {
		StringBuilder res = new StringBuilder("char[] {");
		
		for (char character : charArray) {
			res.append(character).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.ERROR, res.toString());
	}
	
	/**
	 * Logs the object array with severity level ERROR.
	 * 
	 * @param objectArray The object array.
	 */
	public void err(Object[] objectArray) {
		StringBuilder res = new StringBuilder("Object[] {");
		
		for (Object object : objectArray) {
			res.append(object).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2);
		}
		res.append("}");
		
		log(Level.ERROR, res.toString());
	}
}