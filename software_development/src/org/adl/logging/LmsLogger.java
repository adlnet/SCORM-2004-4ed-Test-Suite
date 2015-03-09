package org.adl.logging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.adl.testsuite.rte.lms.LMSTestApplet;
import org.adl.testsuite.util.AppletList;
import org.adl.testsuite.util.TestSubjectData;
import org.adl.util.LogMessage;
import org.adl.util.MessageConverter;
import org.adl.util.MessageType;

/**
 * This class acts as the single point of contact for routing messages to the
 * summary and detailed logs during execution of the ADL Test Suite
 * LMS test. This is the class that kicks off the Summary and Detailed log
 * writers for the LMS test.
 * @author Concurrent Technologies Corp
 */
public class LmsLogger implements LmsLoggerInterface
{
   private static final String DETAILED_KEY = "Detailed";

   private static final String SUMMARY_KEY = "Summary";

   /**
    * holds a reference to the Applet that exists in test suite browser 
    */
   private final transient LMSTestApplet mLogPane;
   
   /**
    * Collection of messages bound for the detailed log
    */
   private transient List<LogMessage> mDetailedMessages = new LinkedList<LogMessage>();
   
   /**
    * Collection of messages bound for the summary log
    */
   private transient List<LogMessage> mSummaryMessages = new LinkedList<LogMessage>();
   
   /**
    * Collection of every html formatted message written to the
    * summary log browser pane.
    */
//   private LinkedList mSummaryLogMaster = new LinkedList();
   
   /**
    * Collection of the html log messages for the current test case.
    * These will be added to mSummaryLogComplete upon a commit
    */
//   private LinkedList mSummaryLogNotepad = new LinkedList();
   
   /**
    * used to format the messages destined for the summary log in the 
    * test suite browser
    */
   private static final HTMLMessageFormatter FORMATTER = new HTMLMessageFormatter();
   
   /**
    * holds the complete URI of the master detailed log file
    */
   private transient String mDetailedLogFile;
   
   /**
    * used to generate the test folder and the log files
    */
   protected transient LogFileGenerator mLfg;
   
   /**
    * true if the detailed log writer thread has started, false if now
    */
   private transient boolean mDetailedLogStarted = false;
   
   /**
    * true if the endlog message was sent to the summary log message collection,
    * false otherwise
    */
   private transient boolean mSummaryLogEnded = false;
   
   /**
    * true if the endlog message was sent to the detailed log message collection
    * and a NEWLOG didn't follow, false otherwise
    */
   private transient boolean mDetailedLogEnded = false;
   
   /**
    * true if the logger needs to skip the first NEWLOG message, false otherwise
    */
   private transient boolean mSkipFirst = false;
   
   /**
    * true if this is the first testcase, false otherwise
    */
   private transient boolean mIsFirstTestcase = true;
   
   /**
    * Logger object used for debug logging
    */
   protected static final Logger LOGGER = Logger.getLogger("org.adl.util.debug.testsuite");
   
   private transient List<String> newLog;

   
   /**
    * the only job of this constructor is to get a reference to the
    * LMSTestApplet so that information can be written to the browser
    * the user is viewing and get most of the class attributes initialized.
    */
   public LmsLogger()
   {
      LOGGER.entering(getClass().getSimpleName(),"LmsLogger()");
      mLogPane = (LMSTestApplet)AppletList.getApplet("LMSTestApplet");
      LOGGER.exiting(getClass().getSimpleName(),"LmsLogger()");
   }

   /**
    * used during a regular test (not restarting a saved test)
    * @param iTSHome used to create the log folder and the logs
    */
   public void startLogs(final String iTSHome)
   {
      LOGGER.entering(getClass().getSimpleName(),"startLogs(S)");
      
      AccessController.doPrivileged(new PrivilegedStartLogs(iTSHome, true, ""));
      
      // send the NEWLOG message to create the summary log file
      SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(MessageType.NEWLOG,""));
      
      LOGGER.exiting(getClass().getSimpleName(),"startLogs(S)");
   }

   
   /**
    * Used when an old (saved) test is being restarted
    * @param iTSHome Environment Variable for log purposes
    * @param oldLogURI the URI of the old Detailed log
    * @return true if the old logs were found and opened, false otherwise
    */
   public boolean startLogs(final String iTSHome, final String oldLogURI)
   {     
      LOGGER.entering(getClass().getSimpleName(),"startLogs(S,S)");
      
      final String tempFolder = 
         AccessController.doPrivileged(new PrivilegedStartLogs(iTSHome, false, oldLogURI));

      // summary log was not found
      if(tempFolder == null)
      {
         return false;
      }

      mLfg.setTestLogFolder(tempFolder + File.separatorChar);

      // now that we've read through the old log and saved each line to the 
      // newLog collection, iterate thru and present them to the user via the
      // web browser's summary log pane
      final Iterator<String> iter = newLog.iterator();
      
      // if we're loading a saved session we need to set this flag to false
      mIsFirstTestcase = false;

      final MessageConverter converter = new MessageConverter(tempFolder + File.separator);
      
      String tempstr = "";
      // get down to the header info
      while(iter.hasNext())
      {
         tempstr = iter.next();
         if(tempstr.startsWith("<head"))
         {
            break;
         }
      }

      // now we're at the header info, we need to store, then set this info so 
      // that detailed logs for test cases about to be run will have the correct
      // info in the header

      // this is Test Environment Info Header
      writeToBrowser(converter.convertToLogMessage(tempstr));
      
      // this is the OS
      tempstr = iter.next();
      writeToBrowser(converter.convertToLogMessage(tempstr));
      TestSubjectData.getInstance().setCurrentOS(tempstr);
      
      // this is the JRE
      tempstr = iter.next();
      writeToBrowser(converter.convertToLogMessage(tempstr));
      TestSubjectData.getInstance().setCurrentJRE(tempstr);
      
      // this is the browser
      tempstr = (String)iter.next();
      writeToBrowser(converter.convertToLogMessage(tempstr));
      TestSubjectData.getInstance().setCurrentBrowser(tempstr);
      
      // this is the spacer for the summary log
      writeToBrowser(converter.convertToLogMessage((String)iter.next()));
      
      // this is Test ID Info
      writeToBrowser(converter.convertToLogMessage((String)iter.next()));
      
      // this is the date
      tempstr = iter.next();
      writeToBrowser(converter.convertToLogMessage(tempstr));
      TestSubjectData.getInstance().setDate(tempstr);
      
      // this is the LMS Product
      tempstr = iter.next();
      writeToBrowser(converter.convertToLogMessage(tempstr));
      TestSubjectData.getInstance().setProduct(tempstr);
      
      // this is the LMS Version
      tempstr = (String)iter.next();
      writeToBrowser(converter.convertToLogMessage(tempstr));
      TestSubjectData.getInstance().setVersion(tempstr);
      
      // this is the LMS Vendor
      tempstr = (String)iter.next();
      writeToBrowser(converter.convertToLogMessage(tempstr));
      TestSubjectData.getInstance().setVendor(tempstr);
      
      // start sending the LogMessage objects
      while(iter.hasNext())
      {
         // convert String to LogMessage and send to method for html formatting
         writeToBrowser(converter.convertToLogMessage((String)iter.next()));
      }
      
      LOGGER.exiting(getClass().getSimpleName(),"startLogs(S,S)");
      return true;
   }
   
   /**
    * This method is called during the LMS test and acts as the single point of
    * contact for sending information to the summary and detailed logs
    * @param toLog String representing the log type, either "Detailed",
    * "Summary", or "Both"
    * @param msg the LogMessage being added
    */
   public void addMessage(final String toLog, final LogMessage msg)
   {
      LOGGER.finest("adding to " + toLog + " Log: message = " + msg.getMessageText());

      synchronized(this)
      {
         if("Both".equals(toLog))
         {
             addToMessageCollection(mSummaryMessages, msg);
             writeToBrowser(msg);
             addToMessageCollection(mDetailedMessages, msg);
         }
         else if(SUMMARY_KEY.equals(toLog))
         {
            LOGGER.finest("inside if(toLog.equals(\"Summary\")\n" +
               "msgType is: " + msg.getMessageType() + "\tmsgText is: " + msg.getMessageText());
            addToMessageCollection(mSummaryMessages, msg);
            writeToBrowser(msg);
         
            if(msg.getMessageType() == MessageType.ENDLOG)
            {
               mSummaryLogEnded = true;
            }
         }         
         else if(DETAILED_KEY.equals(toLog))
         {
            // if the detailed log writer hasnt been kicked off, start it
            if(msg.getMessageType() == MessageType.NEWLOG)
            {
               if(!mDetailedLogStarted)
               {
                  // we need to 
                  new LmsDetailedLogWriter(msg.getMessageText());
                  mDetailedLogStarted = true;
                  mSkipFirst = true;
               }   
                     
               mDetailedLogFile = msg.getMessageText();
               // make sure the NEWLOG message is first, then add
               // the preliminary info
               addToMessageCollection(mDetailedMessages, msg);
               writePreliminaryInfo(false);
               mDetailedLogEnded = false;
            }
            else 
            {
               if(msg.getMessageType() == MessageType.ENDLOG)
               {
                  mDetailedLogEnded = true;
               }
            
               addToMessageCollection(mDetailedMessages, msg);
            }
         }
      }
   }// end of addMessage()
   
   /**
    * When a RETRY message is received all messages in the local collections are
    * deleted and the RETRY messages is added to the DetailedLogMessageCollection
    */
   public boolean retry()
   {
      LOGGER.entering(getClass().getSimpleName(),"retry()");
      
      final SimpleDateFormat formatter = 
         new SimpleDateFormat("yyyy-MM-dd_kk.mm.ssS", Locale.getDefault());
      final String date = formatter.format(new Date());
      final LogMessage msg = new LogMessage(MessageType.RETRY, date);
      
      // if this is the first testcase send the prelim info to the summary log
      // again
      if(mIsFirstTestcase)
      {
         // declare a list to hold the header info msgs
         final LogMessage[] headers = new LogMessage[18];
         
         // copy them to the temp collection
         for(int i=0;i<headers.length;i++)
         {
            headers[i] = (LogMessage)mSummaryMessages.get(i);
         }
         
         // clear the summary log collection
         mSummaryMessages.clear();
         
         // copy the header info back over to the summary messages
         for(int i=0;i<headers.length;i++)
         {
            mSummaryMessages.add(headers[i]);
         }
      } 
      else
      {
         mSummaryMessages.clear();
      }
         
      mDetailedMessages.clear();
           
      
      // need to add something to the summary log to say "Retrying test blah blah blah"
      SummaryLogMessageCollection.getInstance().addMessage(msg);
      
      // add a newlog message so it's the first in the collection
      addMessage(DETAILED_KEY,new LogMessage(MessageType.NEWLOG, mDetailedLogFile));
      
      // send the info to the summary html log
      writeToBrowser(new LogMessage(MessageType.OTHER,""));
      writeToBrowser(new LogMessage(MessageType.OTHER,">>>  Retrying Test Package " + 
         getTestCaseName(mDetailedLogFile)));
      writeToBrowser(new LogMessage(MessageType.OTHER,">>>  Data from previous run will not" + 
         " be stored in the summary log file"));
      writeToBrowser(new LogMessage(MessageType.OTHER,""));
      
      // clear out the summary log in the browser window
      //System.out.println("clearing log***************************************");
      //mLogPane.clearLog();
      
      // write the summary log back to the browser minus the info from the test
      // case being retry'd
 /*     Iterator iter = mSummaryLogMaster.listIterator();
      
      while(iter.hasNext())
      {
         writeToBrowser((String)iter.next());
      }
 */     
      LOGGER.exiting(getClass().getSimpleName(),"retry()");
      
      // if we get this far without throwing an exception, then it worked
      return true;
   }// end of retry()
   
   /**
    * Takes the full URL of the current detailed log and pulls out the name
    * of the testcase being run
    * @return the name of the test case
    */
   protected String getTestCaseName(final String messageText)
   {
      final Pattern pattern = 
         Pattern.compile("([A-Z]{3})|([A-Z]{1,2})-\\d{2}[a-z]{0,2}");
      final Matcher matcher = pattern.matcher(messageText);
      // go to the last match
      String name = "";
      while (matcher.find())
      {
         name = matcher.group();  
      }
      return name;
   }
   
   /**
    * allows the user to view the current detailed log
    * @return String representation of the file location (URI)
    */
   public String viewCurrentLog()
   {
      LOGGER.entering(getClass().getSimpleName(),"viewCurrentLog()");
      final String link = 
         AccessController.doPrivileged(new PrivilegedViewLog());
      LOGGER.exiting(getClass().getSimpleName(),"viewCurrentLog()");
      return  link;
   }// end of viewCurrentLog()
   
   /**
    * returns the URI of the detailed log. This is used during the SAVE operation
    * @return mDetailedLogFile String object holding the value of the URI to the
    * detailed log
    */
   public String getDetailedFileURI()
   {
      LOGGER.info("in LmsLogger.getDetailedFileURI() returning: " + mDetailedLogFile);
      return mDetailedLogFile;
   }
   
   /**
    * returns the path to the folder the logs are being written to
    * @return String value of the folder location
    */
   public String getLogFolder()
   {
      return mLfg.getTestLogFolder();
   }
   
   /**
    * This method is called when the applet is shutting itself down. This allows
    * end tags to be added to the summary and detailedlog message collection if
    * they haven't already 
    */
   public void testStopped()
   {
      LOGGER.entering(getClass().getSimpleName(),"testStopped()");
      // if the ENDLOG message HAS been sent then mSummaryLogEnded will be true
      // and this statement will be false
      if(!mSummaryLogEnded)
      {
         // if this session was ended prematurely make sure to include a link
         // in the summary log pointing to the last detailed log generated
         addMessage(SUMMARY_KEY, new LogMessage(MessageType.LINKLMS, mDetailedLogFile));
         // if the ENDLOG message was NOT sent, then the browser was closed
         // before the test session completed, add the ENDLOG message
         addMessage(SUMMARY_KEY, new LogMessage(MessageType.ENDLOG, ""));
      }
      
      // if the ENDLOG message HAS been sent then mDetailedLogEnded will be true
      // and this statement will be false
      if(!mDetailedLogEnded)
      {
         // if the ENDLOG message was NOT sent, then the browser was closed
         // before the test session completed, add the ENDLOG message
         addMessage(DETAILED_KEY, new LogMessage(MessageType.ENDLOG, ""));
      }
      LOGGER.exiting(getClass().getSimpleName(),"testStopped()");
   }
   
   /**
    * Writes the initial info to the logs (OS, JRE, Browser, etc)
    * @param writeToSummaryLog true if  we're only writing to the summary 
    * log
    */
   public void writePreliminaryInfo(final boolean writeToSummaryLog)
   {
      final LogMessage[] msgs = TestSubjectData.getInstance().getPreliminaryInfo("LMS");
      
      // we only need to write this info to the summary log one time, 
      // all of the detailed logs will have this info
      if(writeToSummaryLog)
      {
         for(int i=0;i<msgs.length;i++)
         {
            addMessage(SUMMARY_KEY,msgs[i]);
         }         
      }
      else
      {
         // the last message is a header for learner ID, we'll skip that one
         for(int i=0;i<msgs.length -1;i++)
         {
            addMessage(DETAILED_KEY,msgs[i]);
         }
      }
   }
   
   // BEGIN PRIVATE METHODS
   
   /**
    * Called when an ENDLOG message is received, this method adds all of the
    * messages in the collections to their particular message collection objects
    * (SummaryLogMessageCollection|DetailedLogMessageCollection) to be written 
    * to file
    */
   private void sendMessages()
   {
      synchronized(this)
      {
         LOGGER.entering(getClass().getSimpleName(),"sendMessages()");
         LOGGER.finest("size of summary message collection is: " + mSummaryMessages.size());
         // tese case complete, time to add the messages to the collection objects
      
         // if we're in here then the info is being written to file and it's no
         // longer the first test case
         mIsFirstTestcase = false;
      
         SummaryLogMessageCollection.getInstance().addAll(mSummaryMessages);
         mSummaryMessages = new LinkedList<LogMessage>();
         LOGGER.finest("size of summary message collection is now: " + mSummaryMessages.size());
         LOGGER.finest("size of detailed message collection is: " + mDetailedMessages.size());
      
         DetailedLogMessageCollection.getInstance().addAll(mDetailedMessages);
         mDetailedMessages = new LinkedList<LogMessage>();
         LOGGER.finest("size of detailed message collection is now: " + mDetailedMessages.size());
         LOGGER.exiting(getClass().getSimpleName(),"sendMessages()");
      }
   }// end of sendMessages()

   /**
    * adds the LogMessage object to the message collection passed in
    * @param list LinkedList of messages
    * @param msg message to be added to the list
    */
   private void addToMessageCollection(final List<LogMessage> list, final LogMessage msg)
   {
      LOGGER.entering(getClass().getSimpleName(),"addToMessageCollection()");
//      System.out.println("messageType is: " + msg.getMessageType() +
//         "\nmessageText is: " + msg.getMessageText());
      switch(msg.getMessageType())
      {
         case MessageType.NEWLOG: 
            // we have to skip the first NEWLOG message for the detailed log so we
            // dont initialize it twice
            if(mSkipFirst)
            {
               mSkipFirst = false;
            }
            else
            {
               list.add(msg);
            }
            break;
         
         case MessageType.ENDLOG:         
            list.add(msg);
            LOGGER.finest("received ENDLOG message, time to send the messages to file");
            sendMessages();
            break;
            
         case MessageType.LINKLMS:
            final String tcName = getTestCaseName(msg.getMessageText());
            final LogMessage tempmsg = 
               new LogMessage(msg.getMessageType(),msg.getMessageText(),tcName);
            list.add(tempmsg);
            break;
         
         case MessageType.ABORT: // only sent to the summary log, so we take care of both here
            addMessage(DETAILED_KEY, new LogMessage(MessageType.FAILED, "The user has aborted the test"));
            addMessage(SUMMARY_KEY, new LogMessage(MessageType.FAILED, "The user has aborted the test"));
            addMessage(SUMMARY_KEY, new LogMessage(MessageType.LINKLMS, mDetailedLogFile));
            addMessage(SUMMARY_KEY, new LogMessage(MessageType.OTHER, "&lt;HR&gt;"));
            addMessage(SUMMARY_KEY, new LogMessage(MessageType.FAILED, "The user has aborted the test"));
            addMessage(DETAILED_KEY, new LogMessage(MessageType.ENDLOG, ""));
            addMessage(SUMMARY_KEY, new LogMessage(MessageType.ENDLOG, ""));
            break;
            
         case MessageType.SAVE:            
         default:
            list.add(msg);
      }// end switch
      
      LOGGER.exiting(getClass().getSimpleName(),"addToMessageCollection()");
   }// end of addToMessageCollection()
   
   /**
    * sends the formatted message to the Applet to write to the browser
    * @param msg the LogMessage object to be written to the browser
    */
   private void writeToBrowser(final LogMessage msg)
   {
      if(!(msg.getMessageType() == MessageType.RETRY ||
         msg.getMessageType() == MessageType.XMLOTHER ||
         msg.getMessageType() == MessageType.ABORT))
      {
         LOGGER.entering(getClass().getSimpleName(),"writeToBrowser()");
         final String temp = "<tr>" + FORMATTER.formatMessage(msg) + "</tr><br>";
         LOGGER.finest("formatted msg is: " + temp);
         mLogPane.writeToBrowser(temp);
         LOGGER.exiting(getClass().getSimpleName(),"writeToBrowser()");
      }
   }// end of writeToBrowser()
   
   
   
   /* BEGIN NESTED CLASSES NECESSARY FOR SECURITY OPERATIONS */
   private class PrivilegedViewLog implements PrivilegedAction<String>
   {

      public String run()
      {
         return CurrentDetailedLog.viewLog(mLfg.getTestLogFolder(), mDetailedMessages);
      }
      
   }// end class PrivilegedViewLog

   private class PrivilegedStartLogs implements PrivilegedAction<String>
   {
      private final transient String uri;
      private transient String mLogFolder;
      private final transient boolean isNewTest;
      private final transient String mTSHome;
      
      PrivilegedStartLogs(final String iTSHome, final boolean iNewTest, final String iUri)
      {
         uri = iUri;
         isNewTest = iNewTest;
         mTSHome = iTSHome;
      }
      
      /**
       * If this is a new test this method simply kicks off the detailed and
       * summary log writers, and returns the LogFileGenerator. If this is
       * starting a saved test, then this method also finds the old summary
       * log, copies it into a temp collection up to the <save> tag,
       * then overwrites the old Summary log file with data from the new
       * test cases run
       * @return LogFileGenerator if this is a new test, String value of the
       * path to the log folder if restarting a saved session
       */
      public String run()
      {
         mLfg = new LogFileGenerator(mTSHome);
         if(isNewTest)
         {
            new LmsSummaryLogWriter(mLfg);
            LOGGER.info("instantiating LmsDetailedLogWriter");
            new LmsDetailedLogWriter("");
            return "";
         }

         // if we're here then we're restarting an old test
         final File oldSummaryLog = findOldSummaryLog(uri);
         if(oldSummaryLog == null)
         {
            LOGGER.severe("Summary Log File not found");
            return null;
         }
         // open the old log, get a filewriter for it, and pass that
         // filewriter into the new thread
         new LmsSummaryLogWriter(openOldLog(oldSummaryLog));
         new LmsDetailedLogWriter("");
         
         // when we return we need to set the detailed log in the LogFileGenerator,
         // assing lfg to the class LogFileGenerator to avoid npe
         //mLfg = lfg;
         // now that we've overwritten the old summary log, we need to delete
         // any detailed log files that have a modified date AFTER that of the
         // detailed log pulled from the session
         deleteOldFiles(uri);
         
         LOGGER.info("leaving PrivilegedStartLogs.run(): mLogFolder is: " + mLogFolder);
         return mLogFolder;
      } 
      
      /**
       * When a saved test is restarted the old logs need to be reopened, this method
       * opens the old logs, copies all but the end tag to memory, then overwrites the
       * old log minus the end tag
       * @param oldFileURI log file to be extended/overwritten
       * @return the FileWriter object that is writing to the file
       */
      private OutputStreamWriter openOldLog(final File oldFile)
      {
         LOGGER.entering("LMSLogger$PrivilegedStartLogs","openOldLog()");
         LOGGER.finer("inside openOldLog(), value of oldFile is: " + oldFile.getAbsolutePath());
         //FileWriter out = null;
         OutputStreamWriter out = null;
         try
         {
            final BufferedReader oldLogStream = new BufferedReader(new InputStreamReader(
                  new FileInputStream(oldFile), "UTF-16"));
            
            final List<String> temp = new LinkedList<String>();
            newLog = new LinkedList<String>();
            String tempstring = oldLogStream.readLine();

            // copy each line of the old file to the linked list until we reach the
            // <SAVE> tag, then copy those messages to newLog until the last <SAVE>
            // is encountered, </logmessages> is found, or null is returned for eof
            while((tempstring != null) && !"</logmessages>".equals(tempstring))
            {
               // copy until the line read in is a save tag or an end tag
               // (or null for eof), then write to the newLog collection
               while(((tempstring != null) &&
                  !"</logmessages>".equals(tempstring)) &&
                  !"<message type=\"save\"></message>".equals(tempstring))
               {
                  temp.add(tempstring);
                  tempstring = oldLogStream.readLine();
               }

               // if we've fallen out of the inner while loop then we either
               // encountered a save tag, or the end tag. If it's the save tag
               // we want to copy the temp to the newLog collection, if it's the
               // end tag we want to continue without writing to the newLog
               if("<message type=\"save\"></message>".equals(tempstring))
               {
                  // add the save tag to the collection
                  temp.add(tempstring);
                  // copy from the temp to the newLog collection
                  newLog = addToNewLog(temp, newLog);
               }
               // if this isnt the first <save> tag we need to look for the next
               tempstring = oldLogStream.readLine();
            }// end outer while

            // now all lines of the file up to (and including) the save tag
            // have been copied to the newLog linked list, time to write them
            // to the File object and overwrite the old
            
            // create the new File object in memory and the file writer
            //out = new FileWriter(oldFile);
            out = new OutputStreamWriter(new FileOutputStream(oldFile), "UTF-16");
            LOGGER.fine(newLog.size() + " messages in newLog");
            
            final Iterator<String> iter = newLog.iterator();
            while(iter.hasNext())
            {
               final String tempLine = iter.next();
               LOGGER.finest("writing to log: " + tempLine);
               out.write(tempLine);
            }
            out.flush();
            oldLogStream.close();
         }
         catch(Exception e)
         {
            final String message = e.getClass().getSimpleName()
               + " occurred in LmsLogger$PrivilegedStartLogs.openOldLog()" +
               "\n" + e;
            System.out.println(message);
            LOGGER.severe(message);
         }
         LOGGER.exiting("LMSLogger$PrivilegedStartLogs$PrivilegedStartLogs","openOldLog()");
         return out;
      }   
      
      /**
       * Adds message objects to the temporary storage collection to overwrite
       * the old summary log
       * @param temp temp collection of messages to write to the summary log
       * @param iNewLog the complete collection of messages being written to the
       * summary log
       * @return iNewLog the total collection of summary log messages (up to and
       * including the <SAVE> message
       */
      private List<String> addToNewLog(final List<String> temp, final List<String> iNewLog)
      {
         final Iterator<String> iter = temp.listIterator();
         // copy lines from temp collection to newLog collection 
         while(iter.hasNext())
         {
            iNewLog.add(iter.next() + "\n");
         }
         
         // remove the messages copied to newLog from the temp collection
         temp.clear();
         
         return iNewLog;
      }
      
      /**
       * If a session is saved and other tests are run after the session is saved,
       * this method removes the detailed logs from the log folder so there is no
       * duplication of test case logs
       * @param filePath
       */
      private void deleteOldFiles(final String filePath)
      {
         LOGGER.entering("LmsLogger$PrivilegedStartLogs","deleteOldFiles()");
         
         // create a File object to hold a reference to the detailed log
         // referenced in the saved session
         final File lastDetailedLog = new File(filePath);
         final File logFolder = new File(lastDetailedLog.getParent());
         final File[] logs = logFolder.listFiles();
         File tempLog;
       
         for(int i=0;i<logs.length;i++)
         {
            tempLog = logs[i];
            if((tempLog.lastModified() > lastDetailedLog.lastModified()) &&
               !tempLog.getName().endsWith("SummaryLog.xml"))
            {
               LOGGER.info("Deleting old log file: " + tempLog.getName());
               tempLog.delete();
            }
         }
         LOGGER.exiting("LmsLogger$PrivilegedStartLogs","deleteOldFiles()");
      }
      
      
      /**
       * Given the URI of the detailed log this method finds the summary log for
       * the same test
       * @param oldUri the URI if the detailed log
       * @return The File object referencing the summary log or <b>null</b> if one is not found
       */
      private File findOldSummaryLog(final String oldUri)
      {
         LOGGER.entering(getClass().getSimpleName(),"findOldSummaryLog()");
         LOGGER.finest("oldUri is: " + oldUri);
         File sumLog = null;
         try
         {
            // get a reference to the folder holding the log files
            final File temp = new File(oldUri.substring(0,oldUri.lastIndexOf(File.separatorChar)));
            
            // store the path to the log folder
            mLogFolder = temp.getPath();
            
            LOGGER.finest("looking for summary log in: " + temp.getPath());
            // get all the files in this directory
            final String[] files = temp.list();
   
            // iterate through the file names until one has "Summary" in it
            for(int i=0;i<files.length;i++)
            {
               if(files[i].indexOf(SUMMARY_KEY) != -1)
               {
                   sumLog = new File(temp.getPath() + File.separatorChar + files[i]);
                   LOGGER.finest("returning summary log: " + sumLog.getPath());
                   LOGGER.exiting(getClass().getSimpleName(),"findOldSummaryLog():returning summary log");
                   break;
               }
                  
            }// end for
         }
         catch(NullPointerException npe)
         {
            JOptionPane.showMessageDialog(null,"Log files not found for this saved session\n" +
               "Please select another session to load or start a new test");
            LOGGER.severe("NullPointerException occurred in LmsLogger$PrivilegedStartLogs.findOldSummaryLog()()" +
               "\n" + npe);
         }

         // if the list DOES NOT contain a file with "Summary" in it return null
         LOGGER.exiting(getClass().getSimpleName(),"$PrivilegedStartLogs.findOldSummaryLog():returning null");
         return sumLog;      
      }
   }// end class PrivilegedStartLogs
   
   
}// end of class LmsLogger
