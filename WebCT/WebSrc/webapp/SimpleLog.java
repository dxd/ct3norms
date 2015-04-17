package webapp;

/**
 * Utilities log
 */
import java.io.*;
import java.text.*;
import java.util.*;

public class SimpleLog {


    private static String logFile = "/Users/dxd/git/ct/WebCT/logsh/CTserver/TC"+ System.nanoTime() +".log"; 
    	//WebCTConfiguration.LogFileFullPath;
    private final static DateFormat df = new SimpleDateFormat ("yyyy.MM.dd  hh:mm:ss ");

    private SimpleLog() { 
    	setLogFilename();
    }
    
    public static void setLogFilename() {
        //logFile = "./TC"+ new Date(System.currentTimeMillis()) +".log";
        try {
        	File file = new File(logFile);

            // Create file if it does not exist
            boolean success = file.createNewFile();
            if (success) {
                // File did not exist and was created
            } else {
                // File already exists
            }
            
            PrintStream printStream;
    		try {
    			printStream = new PrintStream(new FileOutputStream(file));
    			System.setOut(printStream);
    		} catch (FileNotFoundException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
        } catch (IOException e) {
        	
        }

    }
    
    public static void write(String msg) {
    	//System.out.println("Log file: " + logFile);

        write(logFile, msg);
    }
    
    public static void write(Exception e) {
        write(logFile, stack2string(e));
    }

    public static void write(String file, String msg) {
        try {
            Date now = new Date();
            String currentTime = SimpleLog.df.format(now); 
            FileWriter aWriter = new FileWriter(file, true);
            aWriter.write(currentTime + " " + msg 
                    + System.getProperty("line.separator"));
            System.out.println(currentTime + " " + msg);
            aWriter.flush();
            aWriter.close();
        }
        catch (Exception e) {
            System.out.println(stack2string(e));
        }
    }
    
    private static String stack2string(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "------\r\n" + sw.toString() + "------\r\n";
        }
        catch(Exception e2) {
            return "bad stack2string";
        }
    }
}