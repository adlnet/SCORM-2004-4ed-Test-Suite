package org.adl.testsuite.rte.lms;

import java.io.File;
import java.util.List;

import org.adl.logging.LmsLogger;
import org.adl.logging.LmsLoggerInterface;
import org.adl.testsuite.rte.lms.interfaces.FileManager;
import org.adl.testsuite.rte.lms.interfaces.Initializer;
import org.adl.testsuite.rte.lms.util.LMSInfo;
import org.adl.testsuite.rte.lms.util.LMSSession;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;
import org.adl.util.support.SupportVerifier;

/**
 * This class is responsible for handling all of the initial steps of an LMS test
 * session.
 * 
 * @author ADL Technical Team
 */
public class LMSTestInitializer implements Initializer
{
   /**
    * Constant used to indicate to logging to send the message to both the 
    * summary and detailed logs 
    */
   private static final String BL = "Both";
   
   /**
    * Constant used to indicate to logging to send the message to the summary
    * log
    */
   private static final String SL = "Summary";
   
   /**
    * The logging object - use this to send messages to the logs
    */
   LmsLoggerInterface mLLI;
   
   /**
    * The file manager object - use this to load and save stuff
    */
   FileManager mLFM;
   
   /**
    * Lets the system know if LMSTestContentPackage_API was selected to be testing
    * for the current session
    */
   boolean mAPIChosen = false;
   
   /**
    * Lets the system know if LMSTestContentPackage_DMI was selected to be testing
    * for the current session
    */
   boolean mDMIChosen = false;
   
   /**
    *  Lets the system know if there was a loaded save during this session
    */
   private boolean mSaveLoaded = false;
   
   
   /**
    * Default constructor 
    */
   public LMSTestInitializer()
   {
   }
   
   /**
    * Creates an initializer object with a specified logger interface and file 
    * manager.
    * 
    * @param iLLI The logging interface to use to send messages to the logs
    * 
    * @param iLFM The file management object to use to save and load files
    */
   public LMSTestInitializer(LmsLoggerInterface iLLI, FileManager iLFM)
   {
      mLLI = iLLI;
      mLFM = iLFM;
   }
   
   /* 
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#getListOfSaves()
    * 
    */
   public List<File> getListOfSaves()
   {
      return mLFM.getListOfSaves();
   }
      
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#checkEnvironmentInfo()
    */
   public void checkEnvironmentInfo()
   {
      // SupportVerifier checks
      SupportVerifier sv = new SupportVerifier();
      sv.verifyEnvironmentVariable(LMSInfo.getInstance().getCTSHome());
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#setTestingInfo(java.lang.String, java.lang.String, java.lang.String)
    */
   public void setTestingInfo(String iLMSName, String iLMSVersion, String iLMSVendor)
   {
      LMSSession.getInstance().setLMSName(iLMSName);
      LMSSession.getInstance().setLMSVersion(iLMSVersion);
      LMSSession.getInstance().setLMSVendor(iLMSVendor);
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#getTestPackages()
    * 
    */
   public List<String> getTestPackages()
   {
      return LMSInfo.getInstance().getTestPackageNames();
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#setChosenTestPackages(java.util.List)
    */
   public void setChosenTestPackages(List<String> iList)
   {
      mAPIChosen = iList.contains("API");
      mDMIChosen = iList.contains("DMI");
      

      Object[] list = iList.toArray();
      String[] packages = new String[list.length];
      
      if ( packages.length == LMSInfo.getInstance().getNumOfTestCases() )
      {
         addMessage(SL, new LogMessage(MessageType.INFO, "All ADL LMS Test Content Packages have been selected."));
      }
      else
      {
         addMessage(SL, new LogMessage(MessageType.WARNING, 
               "Not all ADL LMS Test Content Packages have been selected. " +
               "This test will not produce a Conformance Label."));
      }
      addMessage(SL,new LogMessage(MessageType.OTHER,""));
      addMessage(SL,new LogMessage(MessageType.INFO,"Begin Test Packages..."));
      addMessage(SL,new LogMessage(MessageType.OTHER,"&lt;HR&gt;"));
      
      for ( int i = 0; i < list.length; i++ )
      {
         packages[i] = (String)list[i];
      }
      
      LMSSession.getInstance().setPackagesToRun(packages);
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#apiIsChosen()
    */
   public boolean apiIsChosen()
   {
      return mAPIChosen;
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#apiIsChosen()
    */
   public boolean dmiIsChosen()
   {
      return mDMIChosen;
   }

   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#setLearnerIDInfo(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    */
   public void setLearnerIDInfo(String iL1Name, String iL1ID, String iL2Name, String iL2ID)
   {
      LMSSession.getInstance().setL1Name(iL1Name);
      if ( !iL1Name.equals("") )
      {
         addMessage(SL, new LogMessage(MessageType.INFO, "Learner Name 1: " + LMSSession.getInstance().getL1Name()));
      }
      
      LMSSession.getInstance().setL1ID(iL1ID);
      if ( !iL1ID.equals("") )
      {
         addMessage(SL, new LogMessage(MessageType.INFO, "Learner ID 1: " + LMSSession.getInstance().getL1ID()));
      }
      
      LMSSession.getInstance().setL2Name(iL2Name);
      if ( !iL2Name.equals("") )
      {
         addMessage(SL, new LogMessage(MessageType.INFO, "Learner Name 2: " + LMSSession.getInstance().getL2Name()));
      }
      
      LMSSession.getInstance().setL2ID(iL2ID);
      if ( !iL2ID.equals("") )
      {
         addMessage(SL, new LogMessage(MessageType.INFO, "Learner ID 2: " + LMSSession.getInstance().getL2ID()));
      }
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#setL1EnrollSuccess(boolean)
    */
   public void setL1EnrollSuccess(boolean iL1EnrollSuccess)
   {
      LMSSession.getInstance().setL1EnrollSuccess(iL1EnrollSuccess);
      if ( iL1EnrollSuccess )
      {
         addMessage(BL, new LogMessage(MessageType.PASSED, 
               "The tester has indicated that " + 
               LMSSession.getInstance().getL1Name() + 
               " was successfully enrolled in the test LMS."));
      }
      else
      {
         addMessage(BL, new LogMessage(MessageType.FAILED, 
               "The tester has indicated that " + 
               LMSSession.getInstance().getL1Name() + 
               " was not successfully enrolled in the test LMS."));
      }
   }
   
   /*
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.Initializer#setL2EnrollSuccess(boolean)
    */
   public void setL2EnrollSuccess(boolean iL2EnrollSuccess)
   {
      LMSSession.getInstance().setL2EnrollSuccess(iL2EnrollSuccess);
      
      if ( iL2EnrollSuccess )
      {
         addMessage(BL, new LogMessage(MessageType.PASSED, 
               "The tester has indicated that " + 
               LMSSession.getInstance().getL2Name() + 
               " was successfully enrolled in the test LMS."));
      }
      else
      {
         addMessage(BL, new LogMessage(MessageType.FAILED, 
               "The tester has indicated that " + 
               LMSSession.getInstance().getL2Name() + 
               " was not successfully enrolled in the test LMS."));
      }
   }

   /*
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.Initializer#startNewTest(java.lang.String)
    */
   public void startNewTest(String iEnvVar)
   {
      startLogs(iEnvVar);
   }
   
   /*
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.Initializer#startSavedTest(java.lang.String, java.lang.String)
    */
   public boolean startSavedTest(String iEnvVar, String iSessionName)
   {
      boolean loadSuccessful = false;
      if ( ! mSaveLoaded )
      {
         mSaveLoaded = LMSSession.getInstance().setSessionStateFromMemento(mLFM.load(iSessionName));
         if ( mSaveLoaded )
         {                 
            try
            {
              
               boolean oldLogFound = startLogs(iEnvVar, LMSSession.getInstance().getDetailedFileURI());
               if(oldLogFound)
               {
                  loadSuccessful = true;
               }
               else
               {
                  LMSSession.getInstance().resetState();
                  mSaveLoaded = false;
               }
                  
            }
            catch (NullPointerException npe)
            {
               startLogs(iEnvVar);
               LMSSession.getInstance().setDetailedFileURI(getDetailedFileURI());

               loadSuccessful = false;
            }
         }
         else
         {
            startLogs(iEnvVar);
            LMSSession.getInstance().setDetailedFileURI(getDetailedFileURI());
            loadSuccessful = false;
         }
      }
      return loadSuccessful;
   }

   /*
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.Initializer#getL1Name()
    */
   public String getL1Name()
   {
      return LMSSession.getInstance().getL1Name();
   }

   /*
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.Initializer#getL2Name()
    */
   public String getL2Name()
   {
      return LMSSession.getInstance().getL2Name();
   }
   
   /*
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.Initializer#getADLTitle()
    */
   public String getADLTitle()
   {
      return LMSInfo.getInstance().getADLTitle();
   }
   
   /*
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.Initializer#getCTSName()
    */
   public String getCTSName()
   {
      return LMSInfo.getInstance().getCTSName();
   }

   /*
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.Initializer#getCTSVersion()
    */
   public String getCTSVersion()
   {
      return LMSInfo.getInstance().getCTSVersion();
   }
   
   /*
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.Initializer#getTestName()
    */
   public String getTestName()
   {
      return LMSInfo.getInstance().getTestName();
   }

   /**
    * Writes the messages to the logs if a logger is available. If not then the 
    * messages are sent to the console. This is done to enable us to continue 
    * using our JUnit tests.
    * 
    * @param iLog The log 
    * 
    * @param iMessage the message
    */
   private void addMessage(String iLog, LogMessage iMessage)
   {
      if ( mLLI != null )
      {
         mLLI.addMessage(iLog, iMessage);
      }
      else
      {
         System.out.println("LMSTestInitializer.addMessage()");
         System.out.println("Log: " + iLog + 
                          "\nMessage type: " + iMessage.getMessageType() + 
                          "\nMessage: " + iMessage.getMessageText());
      }
   }
   
   /**
    * starts the logs
    * @param iEnvVar the env var
    */
   private void startLogs(String iEnvVar)
   {
      if ( mLLI != null )
      {
         ((LmsLogger)mLLI).startLogs(iEnvVar);
      }
      else
      {
         System.out.println("LMSTestInitializer.startLogs(String) called");
      }
   }
   
   /**
    * starts the logs
    * @param iEnvVar the env var
    * @param iDetailedFileURI the dl uri
    * @return boolean
    */
   private boolean startLogs(String iEnvVar, String iDetailedFileURI)
   {
      boolean result = false;      
      if ( mLLI != null )
      {
         result = ((LmsLogger)mLLI).startLogs(iEnvVar, iDetailedFileURI);
      }
      else
      {
         System.out.println("LMSTestInitializer.startLogs(String, String) called");
      }
      return result;
   }
   
   /**
    * returns the dl uri
    * @return string
    */
   private String getDetailedFileURI()
   {
      if ( mLLI != null )
      {
         String uri = ((LmsLogger)mLLI).getDetailedFileURI();

         return uri;
      }
      return "LMSTestInitializer.getDetailedFileURI()";
   }
}