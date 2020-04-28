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
 * <p>The logger can log in 4 different levels:
 * <ul>
 * 	<li>INFO: used to log information.</li>
 * 	<li>DEBUG: used to log debugging information.</li>
 * 	<li>WARNING: used to log warnings.</li>
 * 	<li>ERROR: used to log errors.</li>
 * </ul>
 * </p>
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
			log(Level.GENERIC, dateMaker.createDate(), true);
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
		
		log(Level.GENERIC, dateMaker.createDate(), true);
	}
	
	/**
	 * Closes the logger. Once the logger is closed, 
	 * it cannot be used again. it is recommended to 
	 * close the logger at the end of execution 
	 */
	@Override
	public void close() {
		log(Level.GENERIC, "-----end-----" + lineSep, true);
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		isClosed = true;
	}
	
	/**
	 * Closes the logger if it is not already when the object is finalized.
	 */
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
	 * @param newLine Whether or not a new line character 
	 * 	should be added to the end of the line.
	 */
	void log(Level level, String message, boolean newLine) {
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
				stream.write((newLine ? lineSep : "").getBytes());
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
	 * Logs the boolean without a new line with a severity level of INFO.
	 * 
	 * @param bool The boolean.
	 */
	public void info(boolean bool) {
		log(Level.INFO, Boolean.toString(bool), false);
	}
	
	/**
	 * Logs the boolean with a new line with a severity level of INFO.
	 * 
	 * @param bool The boolean.
	 */
	public void infoln(boolean bool) {
		log(Level.INFO, Boolean.toString(bool), true);
	}
	
	/**
	 * Logs the byte without a new line with a severity level of INFO.
	 * 
	 * @param byteInteger The byte.
	 */
	public void info(byte byteInteger) {
		log(Level.INFO, Byte.toString(byteInteger), false);
	}
	
	/**
	 * Logs the byte with a new line with a severity level of INFO.
	 * 
	 * @param byteInteger The byte.
	 */
	public void infoln(byte byteInteger) {
		log(Level.INFO, Byte.toString(byteInteger), true);
	}
	
	/**
	 * Logs the short without a new line with a severity level of INFO.
	 * 
	 * @param shortInteger The short.
	 */
	public void info(short shortInteger) {
		log(Level.INFO, Short.toString(shortInteger), false);
	}
	
	/**
	 * Logs the short with a new line with a severity level of INFO.
	 * 
	 * @param shortInteger The short.
	 */
	public void infoln(short shortInteger) {
		log(Level.INFO, Short.toString(shortInteger), true);
	}
	
	/**
	 * Logs the int without a new line with a severity level of INFO.
	 * 
	 * @param integer The int.
	 */
	public void info(int integer) {
		log(Level.INFO, Integer.toString(integer), false);
	}
	
	/**
	 * Logs the int with a new line with a severity level of INFO.
	 * 
	 * @param integer The int.
	 */
	public void infoln(int integer) {
		log(Level.INFO, Integer.toString(integer), true);
	}
	
	/**
	 * Logs the long without a new line with a severity level of INFO.
	 * 
	 * @param longInteger The long.
	 */
	public void info(long longInteger) {
		log(Level.INFO, Long.toString(longInteger), false);
	}
	
	/**
	 * Logs the long with a new line with a severity level of INFO.
	 * 
	 * @param longInteger The long.
	 */
	public void infoln(long longInteger) {
		log(Level.INFO, Long.toString(longInteger), true);
	}
	
	/**
	 * Logs the float without a new line with a severity level of INFO.
	 * 
	 * @param floatFraction The float.
	 */
	public void info(float floatFraction) {
		log(Level.INFO, Float.toString(floatFraction), false);
	}
	
	/**
	 * Logs the float with a new line with a severity level of INFO.
	 * 
	 * @param floatFraction The float.
	 */
	public void infoln(float floatFraction) {
		log(Level.INFO, Float.toString(floatFraction), true);
	}
	
	/**
	 * Logs the double without a new line with a severity level of INFO.
	 * 
	 * @param fraction The double.
	 */
	public void info(double fraction) {
		log(Level.INFO, Double.toString(fraction), false);
	}
	
	/**
	 * Logs the double with a new line with a severity level of INFO.
	 * 
	 * @param fraction The double.
	 */
	public void infoln(double fraction) {
		log(Level.INFO, Double.toString(fraction), true);
	}
	
	/**
	 * Logs the char without a new line with a severity level of INFO.
	 * 
	 * @param character The char.
	 */
	public void info(char character) {
		log(Level.INFO, Character.toString(character), false);
	}
	
	/**
	 * Logs the char with a new line with a severity level of INFO.
	 * 
	 * @param character The char.
	 */
	public void infoln(char character) {
		log(Level.INFO, Character.toString(character), true);
	}
	
	/**
	 * Logs the object without a new line with severity level of INFO.
	 * 
	 * @param object The object.
	 */
	public void info(Object object) {
		if (object == null) {
			log(Level.INFO, "null", false);
		} else {
			log(Level.INFO, object.toString(), false);
		}
	}
	
	/**
	 * Logs the object with a new line with severity level of INFO.
	 * 
	 * @param object The object.
	 */
	public void infoln(Object object) {
		if (object == null) {
			log(Level.INFO, "null", true);
		} else {
			log(Level.INFO, object.toString(), true);
		}
	}
	
	/**
	 * Logs the boolean array without a new line with severity level of INFO. 
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.INFO, res.toString(), false);
	}
	
	/**
	 * Logs the boolean array with a new line with severity level of INFO. 
	 * 
	 * @param boolArray The boolean array.
	 */
	public void infoln(boolean[] boolArray) {
		StringBuilder res = new StringBuilder("boolean[] {");
		
		for (boolean bool : boolArray) {
			res.append(bool).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.INFO, res.toString(), true);
	}
	
	/**
	 * Logs the byte array without a new line with severity level of INFO. 
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.INFO, res.toString(), false);
	}
	
	/**
	 * Logs the byte array with a new line with severity level of INFO. 
	 * 
	 * @param byteArray The byte array.
	 */
	public void infoln(byte[] byteArray) {
		StringBuilder res = new StringBuilder("byte[] {");
		
		for (byte byteInteger : byteArray) {
			res.append(byteInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.INFO, res.toString(), true);
	}
	
	/**
	 * Logs the short array without a new line with severity level of INFO. 
	 * 
	 * @param shortArray The short array.
	 */
	public void info(short[] shortArray) {
		StringBuilder res = new StringBuilder("short[] {");
		
		for (short shortInteger : shortArray) {
			res.append(shortInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); if (index > -1) {res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");}
		
		log(Level.INFO, res.toString(), false);
	}
	
	/**
	 * Logs the short array with a new line with severity level of INFO. 
	 * 
	 * @param shortArray The short array.
	 */
	public void infoln(short[] shortArray) {
		StringBuilder res = new StringBuilder("short[] {");
		
		for (short shortInteger : shortArray) {
			res.append(shortInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); if (index > -1) {res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");}
		
		log(Level.INFO, res.toString(), true);
	}
	
	/**
	 * Logs the int array without a new line with severity level of INFO. 
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.INFO, res.toString(), false);
	}
	
	/**
	 * Logs the int array with a new line with severity level of INFO. 
	 * 
	 * @param intArray The int array.
	 */
	public void infoln(int[] intArray) {
		StringBuilder res = new StringBuilder("int[] {");
		
		for (int integer : intArray) {
			res.append(integer).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.INFO, res.toString(), true);
	}
	
	/**
	 * Logs the long array without a new line with severity level of INFO. 
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.INFO, res.toString(), false);
	}
	
	/**
	 * Logs the long array with a new line with severity level of INFO. 
	 * 
	 * @param longArray The long array.
	 */
	public void infoln(long[] longArray) {
		StringBuilder res = new StringBuilder("long[] {");
		
		for (long longInteger : longArray) {
			res.append(longInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.INFO, res.toString(), true);
	}
	
	/**
	 * Logs the float array without a new line with severity level of INFO. 
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.INFO, res.toString(), false);
	}
	
	/**
	 * Logs the float array with a new line with severity level of INFO. 
	 * 
	 * @param floatArray The float array.
	 */
	public void infoln(float[] floatArray) {
		StringBuilder res = new StringBuilder("float[] {");
		
		for (float floatFraction : floatArray) {
			res.append(floatFraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.INFO, res.toString(), true);
	}
	
	/**
	 * Logs the double array without a new line with severity level of INFO. 
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.INFO, res.toString(), false);
	}
	
	/**
	 * Logs the double array with a new line with severity level of INFO. 
	 * 
	 * @param doubleArray The double array.
	 */
	public void infoln(double[] doubleArray) {
		StringBuilder res = new StringBuilder("double[] {");
		
		for (double fraction : doubleArray) {
			res.append(fraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.INFO, res.toString(), true);
	}
	
	/**
	 * Logs the char array without a new line with severity level of INFO. 
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.INFO, res.toString(), false);
	}
	
	/**
	 * Logs the char array with a new line with severity level of INFO. 
	 * 
	 * @param charArray The char array.
	 */
	public void infoln(char[] charArray) {
		StringBuilder res = new StringBuilder("char[] {");
		
		for (char character : charArray) {
			res.append(character).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.INFO, res.toString(), true);
	}
	
	/**
	 * Logs the object array without a new line with severity level INFO.
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.INFO, res.toString(), false);
	}
	
	/**
	 * Logs the object array with a new line with severity level INFO.
	 * 
	 * @param objectArray The object array.
	 */
	public void infoln(Object[] objectArray) {
		StringBuilder res = new StringBuilder("Object[] {");
		
		for (Object object : objectArray) {
			res.append(object).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.INFO, res.toString(), true);
	}
	
	
	
	
	
	
	
	/**
	 * Logs the boolean without a new line with a severity level of DEBUG.
	 * 
	 * @param bool The boolean.
	 */
	public void debug(boolean bool) {
		log(Level.DEBUG, Boolean.toString(bool), false);
	}
	
	/**
	 * Logs the boolean with a new line with a severity level of DEBUG.
	 * 
	 * @param bool The boolean.
	 */
	public void debugln(boolean bool) {
		log(Level.DEBUG, Boolean.toString(bool), true);
	}
	
	/**
	 * Logs the byte without a new line with a severity level of DEBUG.
	 * 
	 * @param byteInteger The byte.
	 */
	public void debug(byte byteInteger) {
		log(Level.DEBUG, Byte.toString(byteInteger), false);
	}
	
	/**
	 * Logs the byte with a new line with a severity level of DEBUG.
	 * 
	 * @param byteInteger The byte.
	 */
	public void debugln(byte byteInteger) {
		log(Level.DEBUG, Byte.toString(byteInteger), true);
	}
	
	/**
	 * Logs the short without a new line with a severity level of DEBUG.
	 * 
	 * @param shortInteger The short.
	 */
	public void debug(short shortInteger) {
		log(Level.DEBUG, Short.toString(shortInteger), false);
	}
	
	/**
	 * Logs the short with a new line with a severity level of DEBUG.
	 * 
	 * @param shortInteger The short.
	 */
	public void debugln(short shortInteger) {
		log(Level.DEBUG, Short.toString(shortInteger), true);
	}
	
	/**
	 * Logs the int without a new line with a severity level of DEBUG.
	 * 
	 * @param integer The int.
	 */
	public void debug(int integer) {
		log(Level.DEBUG, Integer.toString(integer), false);
	}
	
	/**
	 * Logs the int with a new line with a severity level of DEBUG.
	 * 
	 * @param integer The int.
	 */
	public void debugln(int integer) {
		log(Level.DEBUG, Integer.toString(integer), true);
	}
	
	/**
	 * Logs the long without a new line with a severity level of DEBUG.
	 * 
	 * @param longInteger The long.
	 */
	public void debug(long longInteger) {
		log(Level.DEBUG, Long.toString(longInteger), false);
	}
	
	/**
	 * Logs the long with a new line with a severity level of DEBUG.
	 * 
	 * @param longInteger The long.
	 */
	public void debugln(long longInteger) {
		log(Level.DEBUG, Long.toString(longInteger), true);
	}
	
	/**
	 * Logs the float without a new line with a severity level of DEBUG.
	 * 
	 * @param floatFraction The float.
	 */
	public void debug(float floatFraction) {
		log(Level.DEBUG, Float.toString(floatFraction), false);
	}
	
	/**
	 * Logs the float with a new line with a severity level of DEBUG.
	 * 
	 * @param floatFraction The float.
	 */
	public void debugln(float floatFraction) {
		log(Level.DEBUG, Float.toString(floatFraction), true);
	}
	
	/**
	 * Logs the double without a new line with a severity level of DEBUG.
	 * 
	 * @param fraction The double.
	 */
	public void debug(double fraction) {
		log(Level.DEBUG, Double.toString(fraction), false);
	}
	
	/**
	 * Logs the double with a new line with a severity level of DEBUG.
	 * 
	 * @param fraction The double.
	 */
	public void debugln(double fraction) {
		log(Level.DEBUG, Double.toString(fraction), true);
	}
	
	/**
	 * Logs the char without a new line with a severity level of DEBUG.
	 * 
	 * @param character The char.
	 */
	public void debug(char character) {
		log(Level.DEBUG, Character.toString(character), false);
	}
	
	/**
	 * Logs the char with a new line with a severity level of DEBUG.
	 * 
	 * @param character The char.
	 */
	public void debugln(char character) {
		log(Level.DEBUG, Character.toString(character), true);
	}
	
	/**
	 * Logs the object without a new line with severity level of DEBUG.
	 * 
	 * @param object The object.
	 */
	public void debug(Object object) {
		if (object == null) {
			log(Level.DEBUG, "null", false);
		} else {
			log(Level.DEBUG, object.toString(), false);
		}
	}
	
	/**
	 * Logs the object with a new line with severity level of DEBUG.
	 * 
	 * @param object The object.
	 */
	public void debugln(Object object) {
		if (object == null) {
			log(Level.DEBUG, "null", true);
		} else {
			log(Level.DEBUG, object.toString(), true);
		}
	}
	
	/**
	 * Logs the boolean array without a new line with severity level of DEBUG. 
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), false);
	}
	
	/**
	 * Logs the boolean array with a new line with severity level of DEBUG. 
	 * 
	 * @param boolArray The boolean array.
	 */
	public void debugln(boolean[] boolArray) {
		StringBuilder res = new StringBuilder("boolean[] {");
		
		for (boolean bool : boolArray) {
			res.append(bool).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), true);
	}
	
	/**
	 * Logs the byte array without a new line with severity level of DEBUG. 
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), false);
	}
	
	/**
	 * Logs the byte array with a new line with severity level of DEBUG. 
	 * 
	 * @param byteArray The byte array.
	 */
	public void debugln(byte[] byteArray) {
		StringBuilder res = new StringBuilder("byte[] {");
		
		for (byte byteInteger : byteArray) {
			res.append(byteInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), true);
	}
	
	/**
	 * Logs the short array without a new line with severity level of DEBUG. 
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), false);
	}
	
	/**
	 * Logs the short array with a new line with severity level of DEBUG. 
	 * 
	 * @param shortArray The short array.
	 */
	public void debugln(short[] shortArray) {
		StringBuilder res = new StringBuilder("short[] {");
		
		for (short shortInteger : shortArray) {
			res.append(shortInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), true);
	}
	
	/**
	 * Logs the int array without a new line with severity level of DEBUG. 
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), false);
	}
	
	/**
	 * Logs the int array with a new line with severity level of DEBUG. 
	 * 
	 * @param intArray The int array.
	 */
	public void debugln(int[] intArray) {
		StringBuilder res = new StringBuilder("int[] {");
		
		for (int integer : intArray) {
			res.append(integer).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), true);
	}
	
	/**
	 * Logs the long array without a new line with severity level of DEBUG. 
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), false);
	}
	
	/**
	 * Logs the long array with a new line with severity level of DEBUG. 
	 * 
	 * @param longArray The long array.
	 */
	public void debugln(long[] longArray) {
		StringBuilder res = new StringBuilder("long[] {");
		
		for (long longInteger : longArray) {
			res.append(longInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), true);
	}
	
	/**
	 * Logs the float array without a new line with severity level of DEBUG. 
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), false);
	}
	
	/**
	 * Logs the float array with a new line with severity level of DEBUG. 
	 * 
	 * @param floatArray The float array.
	 */
	public void debugln(float[] floatArray) {
		StringBuilder res = new StringBuilder("float[] {");
		
		for (float floatFraction : floatArray) {
			res.append(floatFraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), true);
	}
	
	/**
	 * Logs the double array without a new line with severity level of DEBUG. 
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), false);
	}
	
	/**
	 * Logs the double array with a new line with severity level of DEBUG. 
	 * 
	 * @param doubleArray The double array.
	 */
	public void debugln(double[] doubleArray) {
		StringBuilder res = new StringBuilder("double[] {");
		
		for (double fraction : doubleArray) {
			res.append(fraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), true);
	}
	
	/**
	 * Logs the char array without a new line with severity level of DEBUG. 
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), false);
	}
	
	/**
	 * Logs the char array with a new line with severity level of DEBUG. 
	 * 
	 * @param charArray The char array.
	 */
	public void debugln(char[] charArray) {
		StringBuilder res = new StringBuilder("char[] {");
		
		for (char character : charArray) {
			res.append(character).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), true);
	}
	
	/**
	 * Logs the object array without a new line with severity level DEBUG.
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
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), false);
	}
	
	/**
	 * Logs the object array with a new line with severity level DEBUG.
	 * 
	 * @param objectArray The object array.
	 */
	public void debugln(Object[] objectArray) {
		StringBuilder res = new StringBuilder("Object[] {");
		
		for (Object object : objectArray) {
			res.append(object).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.DEBUG, res.toString(), true);
	}
	
	
	
	
	
	/**
	 * Logs the boolean without a new line with a severity level of WARNING.
	 * 
	 * @param bool The boolean.
	 */
	public void warning(boolean bool) {
		log(Level.WARNING, Boolean.toString(bool), false);
	}
	
	/**
	 * Logs the boolean with a new line with a severity level of WARNING.
	 * 
	 * @param bool The boolean.
	 */
	public void warningln(boolean bool) {
		log(Level.WARNING, Boolean.toString(bool), true);
	}
	
	/**
	 * Logs the byte without a new line with a severity level of WARNING.
	 * 
	 * @param byteInteger The byte.
	 */
	public void warning(byte byteInteger) {
		log(Level.WARNING, Byte.toString(byteInteger), false);
	}
	
	/**
	 * Logs the byte with a new line with a severity level of WARNING.
	 * 
	 * @param byteInteger The byte.
	 */
	public void warningln(byte byteInteger) {
		log(Level.WARNING, Byte.toString(byteInteger), true);
	}
	
	/**
	 * Logs the short without a new line with a severity level of WARNING.
	 * 
	 * @param shortInteger The short.
	 */
	public void warning(short shortInteger) {
		log(Level.WARNING, Short.toString(shortInteger), false);
	}
	
	/**
	 * Logs the short with a new line with a severity level of WARNING.
	 * 
	 * @param shortInteger The short.
	 */
	public void warningln(short shortInteger) {
		log(Level.WARNING, Short.toString(shortInteger), true);
	}
	
	/**
	 * Logs the int without a new line with a severity level of WARNING.
	 * 
	 * @param integer The int.
	 */
	public void warning(int integer) {
		log(Level.WARNING, Integer.toString(integer), false);
	}
	
	/**
	 * Logs the int with a new line with a severity level of WARNING.
	 * 
	 * @param integer The int.
	 */
	public void warningln(int integer) {
		log(Level.WARNING, Integer.toString(integer), true);
	}
	
	/**
	 * Logs the long without a new line with a severity level of WARNING.
	 * 
	 * @param longInteger The long.
	 */
	public void warning(long longInteger) {
		log(Level.WARNING, Long.toString(longInteger), false);
	}
	
	/**
	 * Logs the long with a new line with a severity level of WARNING.
	 * 
	 * @param longInteger The long.
	 */
	public void warningln(long longInteger) {
		log(Level.WARNING, Long.toString(longInteger), true);
	}
	
	/**
	 * Logs the float without a new line with a severity level of WARNING.
	 * 
	 * @param floatFraction The float.
	 */
	public void warning(float floatFraction) {
		log(Level.WARNING, Float.toString(floatFraction), false);
	}
	
	/**
	 * Logs the float with a new line with a severity level of WARNING.
	 * 
	 * @param floatFraction The float.
	 */
	public void warningln(float floatFraction) {
		log(Level.WARNING, Float.toString(floatFraction), true);
	}
	
	/**
	 * Logs the double without a new line with a severity level of WARNING.
	 * 
	 * @param fraction The double.
	 */
	public void warning(double fraction) {
		log(Level.WARNING, Double.toString(fraction), false);
	}
	
	/**
	 * Logs the double with a new line with a severity level of WARNING.
	 * 
	 * @param fraction The double.
	 */
	public void warningln(double fraction) {
		log(Level.WARNING, Double.toString(fraction), true);
	}
	
	/**
	 * Logs the char without a new line with a severity level of WARNING.
	 * 
	 * @param character The char.
	 */
	public void warning(char character) {
		log(Level.WARNING, Character.toString(character), false);
	}
	
	/**
	 * Logs the char with a new line with a severity level of WARNING.
	 * 
	 * @param character The char.
	 */
	public void warningln(char character) {
		log(Level.WARNING, Character.toString(character), true);
	}
	
	/**
	 * Logs the object without a new line with severity level of WARNING.
	 * 
	 * @param object The object.
	 */
	public void warning(Object object) {
		if (object == null) {
			log(Level.WARNING, "null", false);
		} else {
			log(Level.WARNING, object.toString(), false);
		}
	}
	
	/**
	 * Logs the object with a new line with severity level of WARNING.
	 * 
	 * @param object The object.
	 */
	public void warningln(Object object) {
		if (object == null) {
			log(Level.WARNING, "null", true);
		} else {
			log(Level.WARNING, object.toString(), true);
		}
	}
	
	/**
	 * Logs the boolean array without a new line with severity level of WARNING. 
	 * 
	 * @param boolArray The boolean array.
	 */
	public void warning(boolean[] boolArray) {
		StringBuilder res = new StringBuilder("boolean[] {");
		
		for (boolean bool : boolArray) {
			res.append(bool).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), false);
	}
	
	/**
	 * Logs the boolean array with a new line with severity level of WARNING. 
	 * 
	 * @param boolArray The boolean array.
	 */
	public void warningln(boolean[] boolArray) {
		StringBuilder res = new StringBuilder("boolean[] {");
		
		for (boolean bool : boolArray) {
			res.append(bool).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), true);
	}
	
	/**
	 * Logs the byte array without a new line with severity level of WARNING. 
	 * 
	 * @param byteArray The byte array.
	 */
	public void warning(byte[] byteArray) {
		StringBuilder res = new StringBuilder("byte[] {");
		
		for (byte byteInteger : byteArray) {
			res.append(byteInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), false);
	}
	
	/**
	 * Logs the byte array with a new line with severity level of WARNING. 
	 * 
	 * @param byteArray The byte array.
	 */
	public void warningln(byte[] byteArray) {
		StringBuilder res = new StringBuilder("byte[] {");
		
		for (byte byteInteger : byteArray) {
			res.append(byteInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), true);
	}
	
	/**
	 * Logs the short array without a new line with severity level of WARNING. 
	 * 
	 * @param shortArray The short array.
	 */
	public void warning(short[] shortArray) {
		StringBuilder res = new StringBuilder("short[] {");
		
		for (short shortInteger : shortArray) {
			res.append(shortInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), false);
	}
	
	/**
	 * Logs the short array with a new line with severity level of WARNING. 
	 * 
	 * @param shortArray The short array.
	 */
	public void warningln(short[] shortArray) {
		StringBuilder res = new StringBuilder("short[] {");
		
		for (short shortInteger : shortArray) {
			res.append(shortInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), true);
	}
	
	/**
	 * Logs the int array without a new line with severity level of WARNING. 
	 * 
	 * @param intArray The int array.
	 */
	public void warning(int[] intArray) {
		StringBuilder res = new StringBuilder("int[] {");
		
		for (int integer : intArray) {
			res.append(integer).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), false);
	}
	
	/**
	 * Logs the int array with a new line with severity level of WARNING. 
	 * 
	 * @param intArray The int array.
	 */
	public void warningln(int[] intArray) {
		StringBuilder res = new StringBuilder("int[] {");
		
		for (int integer : intArray) {
			res.append(integer).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), true);
	}
	
	/**
	 * Logs the long array without a new line with severity level of WARNING. 
	 * 
	 * @param longArray The long array.
	 */
	public void warning(long[] longArray) {
		StringBuilder res = new StringBuilder("long[] {");
		
		for (long longInteger : longArray) {
			res.append(longInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), false);
	}
	
	/**
	 * Logs the long array with a new line with severity level of WARNING. 
	 * 
	 * @param longArray The long array.
	 */
	public void warningln(long[] longArray) {
		StringBuilder res = new StringBuilder("long[] {");
		
		for (long longInteger : longArray) {
			res.append(longInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), true);
	}
	
	/**
	 * Logs the float array without a new line with severity level of WARNING. 
	 * 
	 * @param floatArray The float array.
	 */
	public void warning(float[] floatArray) {
		StringBuilder res = new StringBuilder("float[] {");
		
		for (float floatFraction : floatArray) {
			res.append(floatFraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), false);
	}
	
	/**
	 * Logs the float array with a new line with severity level of WARNING. 
	 * 
	 * @param floatArray The float array.
	 */
	public void warningln(float[] floatArray) {
		StringBuilder res = new StringBuilder("float[] {");
		
		for (float floatFraction : floatArray) {
			res.append(floatFraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), true);
	}
	
	/**
	 * Logs the double array without a new line with severity level of WARNING. 
	 * 
	 * @param doubleArray The double array.
	 */
	public void warning(double[] doubleArray) {
		StringBuilder res = new StringBuilder("double[] {");
		
		for (double fraction : doubleArray) {
			res.append(fraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), false);
	}
	
	/**
	 * Logs the double array with a new line with severity level of WARNING. 
	 * 
	 * @param doubleArray The double array.
	 */
	public void warningln(double[] doubleArray) {
		StringBuilder res = new StringBuilder("double[] {");
		
		for (double fraction : doubleArray) {
			res.append(fraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), true);
	}
	
	/**
	 * Logs the char array without a new line with severity level of WARNING. 
	 * 
	 * @param charArray The char array.
	 */
	public void warning(char[] charArray) {
		StringBuilder res = new StringBuilder("char[] {");
		
		for (char character : charArray) {
			res.append(character).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), false);
	}
	
	/**
	 * Logs the char array with a new line with severity level of WARNING. 
	 * 
	 * @param charArray The char array.
	 */
	public void warningln(char[] charArray) {
		StringBuilder res = new StringBuilder("char[] {");
		
		for (char character : charArray) {
			res.append(character).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), true);
	}
	
	/**
	 * Logs the object array without a new line with severity level WARNING.
	 * 
	 * @param objectArray The object array.
	 */
	public void warning(Object[] objectArray) {
		StringBuilder res = new StringBuilder("Object[] {");
		
		for (Object object : objectArray) {
			res.append(object).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), false);
	}
	
	/**
	 * Logs the object array with a new line with severity level WARNING.
	 * 
	 * @param objectArray The object array.
	 */
	public void warningln(Object[] objectArray) {
		StringBuilder res = new StringBuilder("Object[] {");
		
		for (Object object : objectArray) {
			res.append(object).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.WARNING, res.toString(), true);
	}
	
	
	
	
	
	
	/**
	 * Logs the boolean without a new line with a severity level of ERROR.
	 * 
	 * @param bool The boolean.
	 */
	public void error(boolean bool) {
		log(Level.ERROR, Boolean.toString(bool), false);
	}
	
	/**
	 * Logs the boolean with a new line with a severity level of ERROR.
	 * 
	 * @param bool The boolean.
	 */
	public void errorln(boolean bool) {
		log(Level.ERROR, Boolean.toString(bool), true);
	}
	
	/**
	 * Logs the byte without a new line with a severity level of ERROR.
	 * 
	 * @param byteInteger The byte.
	 */
	public void error(byte byteInteger) {
		log(Level.ERROR, Byte.toString(byteInteger), false);
	}
	
	/**
	 * Logs the byte with a new line with a severity level of ERROR.
	 * 
	 * @param byteInteger The byte.
	 */
	public void errorln(byte byteInteger) {
		log(Level.ERROR, Byte.toString(byteInteger), true);
	}
	
	/**
	 * Logs the short without a new line with a severity level of ERROR.
	 * 
	 * @param shortInteger The short.
	 */
	public void error(short shortInteger) {
		log(Level.ERROR, Short.toString(shortInteger), false);
	}
	
	/**
	 * Logs the short with a new line with a severity level of ERROR.
	 * 
	 * @param shortInteger The short.
	 */
	public void errorln(short shortInteger) {
		log(Level.ERROR, Short.toString(shortInteger), true);
	}
	
	/**
	 * Logs the int without a new line with a severity level of ERROR.
	 * 
	 * @param integer The int.
	 */
	public void error(int integer) {
		log(Level.ERROR, Integer.toString(integer), false);
	}
	
	/**
	 * Logs the int with a new line with a severity level of ERROR.
	 * 
	 * @param integer The int.
	 */
	public void errorln(int integer) {
		log(Level.ERROR, Integer.toString(integer), true);
	}
	
	/**
	 * Logs the long without a new line with a severity level of ERROR.
	 * 
	 * @param longInteger The long.
	 */
	public void error(long longInteger) {
		log(Level.ERROR, Long.toString(longInteger), false);
	}
	
	/**
	 * Logs the long with a new line with a severity level of ERROR.
	 * 
	 * @param longInteger The long.
	 */
	public void errorln(long longInteger) {
		log(Level.ERROR, Long.toString(longInteger), true);
	}
	
	/**
	 * Logs the float without a new line with a severity level of ERROR.
	 * 
	 * @param floatFraction The float.
	 */
	public void error(float floatFraction) {
		log(Level.ERROR, Float.toString(floatFraction), false);
	}
	
	/**
	 * Logs the float with a new line with a severity level of ERROR.
	 * 
	 * @param floatFraction The float.
	 */
	public void errorln(float floatFraction) {
		log(Level.ERROR, Float.toString(floatFraction), true);
	}
	
	/**
	 * Logs the double without a new line with a severity level of ERROR.
	 * 
	 * @param fraction The double.
	 */
	public void error(double fraction) {
		log(Level.ERROR, Double.toString(fraction), false);
	}
	
	/**
	 * Logs the double with a new line with a severity level of ERROR.
	 * 
	 * @param fraction The double.
	 */
	public void errorln(double fraction) {
		log(Level.ERROR, Double.toString(fraction), true);
	}
	
	/**
	 * Logs the char without a new line with a severity level of ERROR.
	 * 
	 * @param character The char.
	 */
	public void error(char character) {
		log(Level.ERROR, Character.toString(character), false);
	}
	
	/**
	 * Logs the char with a new line with a severity level of ERROR.
	 * 
	 * @param character The char.
	 */
	public void errorln(char character) {
		log(Level.ERROR, Character.toString(character), true);
	}
	
	/**
	 * Logs the object without a new line with severity level of ERROR.
	 * 
	 * @param object The object.
	 */
	public void error(Object object) {
		if (object == null) {
			log(Level.ERROR, "null", false);
		} else {
			log(Level.ERROR, object.toString(), false);
		}
	}
	
	/**
	 * Logs the object with a new line with severity level of ERROR.
	 * 
	 * @param object The object.
	 */
	public void errorln(Object object) {
		if (object == null) {
			log(Level.ERROR, "null", true);
		} else {
			log(Level.ERROR, object.toString(), true);
		}
	}
	
	/**
	 * Logs the boolean array without a new line with severity level of ERROR. 
	 * 
	 * @param boolArray The boolean array.
	 */
	public void error(boolean[] boolArray) {
		StringBuilder res = new StringBuilder("boolean[] {");
		
		for (boolean bool : boolArray) {
			res.append(bool).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), false);
	}
	
	/**
	 * Logs the boolean array with a new line with severity level of ERROR. 
	 * 
	 * @param boolArray The boolean array.
	 */
	public void errorln(boolean[] boolArray) {
		StringBuilder res = new StringBuilder("boolean[] {");
		
		for (boolean bool : boolArray) {
			res.append(bool).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), true);
	}
	
	/**
	 * Logs the byte array without a new line with severity level of ERROR. 
	 * 
	 * @param byteArray The byte array.
	 */
	public void error(byte[] byteArray) {
		StringBuilder res = new StringBuilder("byte[] {");
		
		for (byte byteInteger : byteArray) {
			res.append(byteInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), false);
	}
	
	/**
	 * Logs the byte array with a new line with severity level of ERROR. 
	 * 
	 * @param byteArray The byte array.
	 */
	public void errorln(byte[] byteArray) {
		StringBuilder res = new StringBuilder("byte[] {");
		
		for (byte byteInteger : byteArray) {
			res.append(byteInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), true);
	}
	
	/**
	 * Logs the short array without a new line with severity level of ERROR. 
	 * 
	 * @param shortArray The short array.
	 */
	public void error(short[] shortArray) {
		StringBuilder res = new StringBuilder("short[] {");
		
		for (short shortInteger : shortArray) {
			res.append(shortInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), false);
	}
	
	/**
	 * Logs the short array with a new line with severity level of ERROR. 
	 * 
	 * @param shortArray The short array.
	 */
	public void errorln(short[] shortArray) {
		StringBuilder res = new StringBuilder("short[] {");
		
		for (short shortInteger : shortArray) {
			res.append(shortInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), true);
	}
	
	/**
	 * Logs the int array without a new line with severity level of ERROR. 
	 * 
	 * @param intArray The int array.
	 */
	public void error(int[] intArray) {
		StringBuilder res = new StringBuilder("int[] {");
		
		for (int integer : intArray) {
			res.append(integer).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), false);
	}
	
	/**
	 * Logs the int array with a new line with severity level of ERROR. 
	 * 
	 * @param intArray The int array.
	 */
	public void errorln(int[] intArray) {
		StringBuilder res = new StringBuilder("int[] {");
		
		for (int integer : intArray) {
			res.append(integer).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), true);
	}
	
	/**
	 * Logs the long array without a new line with severity level of ERROR. 
	 * 
	 * @param longArray The long array.
	 */
	public void error(long[] longArray) {
		StringBuilder res = new StringBuilder("long[] {");
		
		for (long longInteger : longArray) {
			res.append(longInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), false);
	}
	
	/**
	 * Logs the long array with a new line with severity level of ERROR. 
	 * 
	 * @param longArray The long array.
	 */
	public void errorln(long[] longArray) {
		StringBuilder res = new StringBuilder("long[] {");
		
		for (long longInteger : longArray) {
			res.append(longInteger).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), true);
	}
	
	/**
	 * Logs the float array without a new line with severity level of ERROR. 
	 * 
	 * @param floatArray The float array.
	 */
	public void error(float[] floatArray) {
		StringBuilder res = new StringBuilder("float[] {");
		
		for (float floatFraction : floatArray) {
			res.append(floatFraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), false);
	}
	
	/**
	 * Logs the float array with a new line with severity level of ERROR. 
	 * 
	 * @param floatArray The float array.
	 */
	public void errorln(float[] floatArray) {
		StringBuilder res = new StringBuilder("float[] {");
		
		for (float floatFraction : floatArray) {
			res.append(floatFraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), true);
	}
	
	/**
	 * Logs the double array without a new line with severity level of ERROR. 
	 * 
	 * @param doubleArray The double array.
	 */
	public void error(double[] doubleArray) {
		StringBuilder res = new StringBuilder("double[] {");
		
		for (double fraction : doubleArray) {
			res.append(fraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), false);
	}
	
	/**
	 * Logs the double array with a new line with severity level of ERROR. 
	 * 
	 * @param doubleArray The double array.
	 */
	public void errorln(double[] doubleArray) {
		StringBuilder res = new StringBuilder("double[] {");
		
		for (double fraction : doubleArray) {
			res.append(fraction).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), true);
	}
	
	/**
	 * Logs the char array without a new line with severity level of ERROR. 
	 * 
	 * @param charArray The char array.
	 */
	public void error(char[] charArray) {
		StringBuilder res = new StringBuilder("char[] {");
		
		for (char character : charArray) {
			res.append(character).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), false);
	}
	
	/**
	 * Logs the char array with a new line with severity level of ERROR. 
	 * 
	 * @param charArray The char array.
	 */
	public void errorln(char[] charArray) {
		StringBuilder res = new StringBuilder("char[] {");
		
		for (char character : charArray) {
			res.append(character).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), true);
	}
	
	/**
	 * Logs the object array without a new line with severity level ERROR.
	 * 
	 * @param objectArray The object array.
	 */
	public void error(Object[] objectArray) {
		StringBuilder res = new StringBuilder("Object[] {");
		
		for (Object object : objectArray) {
			res.append(object).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), false);
	}
	
	/**
	 * Logs the object array with a new line with severity level ERROR.
	 * 
	 * @param objectArray The object array.
	 */
	public void errorln(Object[] objectArray) {
		StringBuilder res = new StringBuilder("Object[] {");
		
		for (Object object : objectArray) {
			res.append(object).append(", ");
		}
		
		int index = res.lastIndexOf(","); 
		if (index > -1) {
			res.delete(res.lastIndexOf(","), res.lastIndexOf(",") + 2).append("}");
		}
		
		log(Level.ERROR, res.toString(), true);
	}
}
