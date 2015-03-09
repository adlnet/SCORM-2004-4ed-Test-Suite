package org.adl.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import org.adl.testsuite.contentpackage.util.validator.LaunchInfo;
import org.adl.testsuite.util.VersionHandler;

/**
 * this class creates the Summary and Detailed File objects the LogWriters write
 * to.
 */
public class LogFileGenerator
{   
   /**
    * holds the URL of the folder to write logs to
    * 
    * if you make this non static, which you shouldn't, thousands of xml files
    * will unexpectedly and undesirably appear on your desktop
    */
   private static transient String mTestLogFolder;
   
   /**
    * true if this folder has been created for this test, false otherwise
    * don't even think about making this non-static
    */
   private static boolean mTestFolderCreated = false;
   
   /**
    * true if the string for this folder name has been created for this test,
    * false otherwise
    * don't even think about making this non-static
    */
   private static boolean mTestFolderNameCreated = false;
   
   /**
    * A String containing the value to be used in place of the non-English file name
    */
   private static final String MANIFEST_NAME = "Manifest";
   
   /**
    * holds the value of the date time string used to create the URL for the
    * file
    */
   private transient String mDateTimeString;
      
   /**
    * holds the value of the log file being created
    */
   private transient String mTestLogName;
   
   /**
    * holds the value of the Environment Variable
    */
   private final transient String mTSHome;
   
   /**
    * Logger object used for debug logging
    */
    private static final Logger LOGGER = Logger.getLogger("org.adl.util.debug.testsuite");
    
    private transient final SimpleDateFormat FORMATTER = 
       new SimpleDateFormat("yyyy-MM-dd_kk.mm.ssS", Locale.getDefault());
    
   /**
    * the type of log to create (i.e. MD, CP, etc)
    */
   private transient String mLogType;
   
   /**
    * holds the value of the file being tested
    */
   private transient String mTestFileName;
   
    /**
     * This constructor is called by the Tester class when it instantiates the
     * DetailedLogWriter.
     * @param iTSHome used to set up the URL for the folder that
     *        holds all the test log files
     */
   public LogFileGenerator(final String iTSHome)
   {
      mTSHome = iTSHome;
   }
   
   /**
    * This constructor is called by the SummaryLogWriter class. It has already
    * been called with the constructor above so the environment variable has
    * already been set.
    */
//  public LogFileGenerator()
//  {
//     // no functionality
//   
//     // no kidding. 
//  }
   
   /**
    * creates the detailed log file and returns the String representation of the
    * URL
    * @param iLogType the type of log being created. for the parent log this 
    * will be the name of the original file, for sublogs this will be MD or SCO
    * @param iTestFileName the name of the file being tested
    * @param logIsDetailed is true if the log being created is a detailed log,
    * false if it is the summary log
    * @return the URL of the detailed log file created
    */
   public String getLogName(final String iLogType, final String iTestFileName, final boolean logIsDetailed)
   {
      LOGGER.entering(getClass().getSimpleName(), "getLogName()");
      LOGGER.finest("iLogType is: " + iLogType + "\tiTestFileName is: " + 
         iTestFileName + "\tlogIsDetailed is: " + logIsDetailed);

      mLogType = iLogType;

      // determine if non-English characters exist in the file name
      if ( encode(iTestFileName, "UTF-16").equals(iTestFileName) )
      {
         mTestFileName = iTestFileName;
      }
      else
      {
         if ("SCO".equals(iLogType))
         {
            mTestFileName = LaunchInfo.getInstance().getResourceID(iTestFileName);
            if ( mTestFileName == null )
            {
               mTestFileName = "SCO";
            }
         }
         else
         {
            mTestFileName = MANIFEST_NAME;
         }

      }
      
      checkForHttpAndFtp();
      
      // Creates the timestamp for the output file
      mDateTimeString = FORMATTER.format(new Date());
      
      /* if the folder to hold all the logs for the test hasn't been created 
         yet, create it */
      if(!mTestFolderNameCreated)
      {
         createTestFolderName();
      }
      
      // generate the end of the filename based on the type (detailed or summary)
      final String logEnding = logIsDetailed?"_DetailedLog.xml":"_SummaryLog.xml";
      
      if("".equals(iTestFileName))
      {
         // this is a sublog
         mTestLogName = mTestLogFolder + mDateTimeString + "-" +
         iLogType + logEnding;
      }
      else
      {
         // create the filename for the parent detailed file
         mTestLogName = mTestLogFolder + mDateTimeString + "-" +
         mTestFileName + logEnding;
      }      
      
      createLogFile();
      LOGGER.finer("returning " + mTestLogName);
      LOGGER.exiting(getClass().getSimpleName(), "getLogName()");
      return mTestLogName;
   }
   
   /**
    * returns the path to the current log folder
    * @return mTestLogFolder
    */
   public String getTestLogFolder()
   {
      return mTestLogFolder;
   }
   
   /**
    * sets the path to the current log folder
    * @param iFolder the String value of the log folder
    */
   public void setTestLogFolder(final String iFolder)
   {
      LOGGER.entering(getClass().getSimpleName(),"setTestLogFolder()");
      LOGGER.finer("setting mTestLogFolder to: " + iFolder);
      mTestLogFolder = iFolder;
      mTestFolderNameCreated = true;
      mTestFolderCreated = true;
      LOGGER.exiting(getClass().getSimpleName(),"setTestLogFolder()");
   }
   
   /**
    * creates the File object that test information will be written to
    */
   private void createLogFile()
   {
      try
      {
         final File outFile = new File(mTestLogName);      
         final String parent = outFile.getParent();
   
         LOGGER.info("mTestLogName is: " + mTestLogName);
         
         // Check to see if the directories exist, if they don't...create them
         if ( !mTestFolderCreated )
         {            
            final File parentFile = new File(parent);
            
            if ( !parentFile.exists() )
            {
               //create the chain of subdirs to the file
               parentFile.mkdirs();

               // we only need to call this one time, so call it when the folder/directory
               // is initially created
               copyRequiredFiles();
            }    
            mTestFolderCreated = true;
         }// end if ( parent != null )
      }
      catch(Exception e)
      {
         final String message = "Exception occurred in LogFileGenerator." +
         "createLogFile()\n" + e;
         LOGGER.severe(message);
         System.out.println(message);
      }
   }// end of createLogFile()
   
   /**
    * creates the name of the test folder based on the date time stamp and the
    * name of the file used in this particular test case
    */
   private void createTestFolderName()
   {      
      //create the base path of the folder that will hold the logs
      mTestLogFolder = mTSHome + File.separator
         + VersionHandler.getTestsuiteDirectory() + File.separator + "Logs"
         + File.separator + mLogType + File.separator +  
         mDateTimeString + "-" + mTestFileName + File.separator;
      
      LOGGER.finer("Test log folder is: " + mTestLogFolder);
      
      mTestFolderNameCreated = true;
   }
   
   /**
    * checks for 'http:' and 'ftp:' in the test file name, if they exist 
    * pull just the actual name of the file and not the entire path
    */
   private void checkForHttpAndFtp()
   {
      if(mTestFileName.indexOf("http:") != -1 ||
         mTestFileName.indexOf("ftp:") != -1)
      {         
         if(mTestFileName.indexOf(File.separatorChar) >= 0)
         {
            mTestFileName = mTestFileName.substring(mTestFileName.lastIndexOf(
               File.separatorChar) + 1, mTestFileName.length());
         }
         else if(mTestFileName.indexOf('/') >= 0)
         {
            mTestFileName = mTestFileName.substring(mTestFileName.lastIndexOf(
               '/') + 1, mTestFileName.length());
         }         
      }
   }
   
   /**
    * copies the image files and xls to the test log directory so the logs
    * can be rendered correctly in a browser
    */
   private void copyRequiredFiles()
   {
      LOGGER.entering(getClass().getSimpleName(),"copyRequiredFiles()");
      // file objects to hold references to the css folder and images folder
      File xslDir, imagesDir;
      
      // initialize xsl folder
      xslDir = new File(mTSHome + File.separator
                        + VersionHandler.getTestsuiteDirectory() + File.separator +
                        "xsl");

      // initialize images folder
      imagesDir = new File(mTSHome + File.separator
         + VersionHandler.getTestsuiteDirectory() + File.separator +
         "images");

      if(xslDir.isDirectory() && imagesDir.isDirectory())
      {
         // create the util folder in the test log folder
         final File utilFolder = new File(mTestLogFolder + File.separator + "util");
         utilFolder.mkdir();
         
         File[] temp = imagesDir.listFiles();
        
         // copy the files in the imagesDir to the util dir in the test log directory
         for(int i=0;i<temp.length;i++)
         {
            copy(temp[i],new File(utilFolder,temp[i].getName()));
         }
         
         // get the files in the xsl directory
         temp = xslDir.listFiles();

         // copy the files in the xslDir to the util dir in the test log directory
         for(int i=0;i<temp.length;i++)
         {
            copy(temp[i],new File(utilFolder,temp[i].getName()));
         }         
      }
      else
      {
         LOGGER.severe("ERROR: Either css folder or images folder are not " +
            "available. Please check the following two directories:\n\t" + 
            mTSHome + File.separator + VersionHandler.getTestsuiteDirectory() +
            File.separator + "css" +
            "\n\t"+ mTSHome + File.separator
            + VersionHandler.getTestsuiteDirectory() + File.separator + "images");
         System.out.println("ERROR: Either css folder or images folder are not " +
            "available. Please check the following two directories:\n\t" + 
            mTSHome + File.separator + VersionHandler.getTestsuiteDirectory() +
            File.separator + "css" +
            "\n\t"+ mTSHome + File.separator
            + VersionHandler.getTestsuiteDirectory() + File.separator + "images");
      }
      LOGGER.exiting(getClass().getSimpleName(),"copyRequiredFiles()");
   }
   
   /**
    * Copies Files objects from one directory to another
    * @param source source File(directory) to copy from
    * @param dest source File(directory) to copy to
    */
   private void copy(final File source, final File dest)
   {
      try
      {
         final InputStream sourceStream = new FileInputStream(source);
         final OutputStream destStream = new FileOutputStream(dest);
     
         // Transfer bytes from in to out
         final byte[] buf = new byte[1024];
         int len;
         while ((len = sourceStream.read(buf)) > 0)
         {
            destStream.write(buf, 0, len);
         }
         sourceStream.close();
         destStream.flush();
         destStream.close();
      }
      catch(IOException ioe)
      {
         LOGGER.severe("IOException occurred in LogFileGenerator.copy()\n" + ioe);
         System.out.println("IOException occurred in LogFileGenerator.copy()\n" + ioe);
      }
      
  }// end of copy(File src, File dst)
   
   /**
    * This method encodes any non-English characters in the given String using the
    * specified Unicode encoding
    * 
    * @param iURIValue is a String containing the URI to be encoded
    * @param iEncoding is the Unicode encoding used to encode the given URI 
    * value
    * 
    * @return a String containing the encoded URI value
    */
   public static String encode( final String iURIValue, final String iEncoding )
   {     
      String encodedFileName = iURIValue;
      final StringBuffer encodedString = new StringBuffer();
      
      // We must also escape special characters from the basic-Latin code page
      // so they do not break the log URL
      encodedFileName = encodedFileName.replaceAll("%", "%25");
      encodedFileName = encodedFileName.replaceAll("[{]", "%7B");
      encodedFileName = encodedFileName.replaceAll("[}]", "%7D");
      encodedFileName = encodedFileName.replaceAll("\\^", "%5E");
      encodedFileName = encodedFileName.replaceAll("&", "%26");
      encodedFileName = encodedFileName.replaceAll("`", "%60");            
      encodedFileName = encodedFileName.replaceAll("#", "%23");
      
      for ( int i = 0; i < encodedFileName.length(); i++ )
      {
         // Obtain the ASCII code of each character in the fileName
         final char currentChar =  encodedFileName.charAt(i);
         if ( currentChar > 127 )
         {
            encodedString.append("%" + Integer.toString(currentChar, 16));
         }
         else
         {
            encodedString.append(currentChar);
         }
      }
      
      try
      {
         // Encode the newly encoded string in its given encoding 
         final byte[] byteString = (new String(encodedString)).getBytes(iEncoding);
         final String encodedByteString = new String(byteString, iEncoding);
         
         return encodedByteString;
         
      }
      catch(UnsupportedEncodingException uee)
      {
         return iURIValue;
      }
   }

}// end of class LogFileGenerator
