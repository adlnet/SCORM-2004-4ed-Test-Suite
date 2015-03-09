package org.adl.testsuite.rte.lms.comm;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.adl.testsuite.rte.lms.interfaces.TestCommunication;
import org.adl.testsuite.rte.lms.interfaces.TestManager;
import org.adl.testsuite.rte.lms.util.Command;
import org.adl.testsuite.rte.lms.util.Results;

/**
 * This class is responsible for managing all aspects of communication between 
 * the LMS SCO and the LMS test.
 * 
 * @author ADL Technical Team
 */
public class LMSTestCommunication implements TestCommunication
{
   protected transient TestManager mTM;
   protected transient LMSServerSocketImpl mSSI;
   
   public LMSTestCommunication()
   {
      setUpSocket();
   }
   
   public void registerManager(final TestManager iTM)
   {
      mTM = iTM;
   }
   
   public String getUIQuestions()
   {
      return mTM.getUIQuestions();
   }
   
   public boolean evaluateUIQuestions(final String iResults)
   {
      return mTM.evaluateUIQuestions(iResults);
   }
   
   public Command getCommands()
   {
      return mTM.getCommands();
   }
   
   public boolean evaluateResults(final Results iResults)
   {
      return mTM.evaluateResults(iResults);
   }
   
   public String getCurrentUserInstructions()
   {
      return mTM.getCurrentUserInstructions();
   }
   
   private void setUpSocket()
   {
      AccessController.doPrivileged(new PrivilegedSocketSession(this));
   }
   
   public void close()
   {
      mSSI.close();
   }
   
   public boolean commit()
   {
      return mTM.commit();
   }
   
   public boolean evaluateCorrectTCLaunch(final String iPkg)
   {
      return mTM.evaluateCorrectTCLaunch(iPkg);
   }
   
   public boolean evaluateCorrectActivityLaunch(final String iAct)
   {
      return mTM.evaluateCorrectActivityLaunch(iAct);
   }

   public void completed()
   {
      mTM.completed();
   }
   
   public void writeToLog(final String iMsg)
   {
      mTM.writeToLog(iMsg);
   }
   
   public void terminateTest()
   {
      mTM.terminateTest();
   }

   public void failTest()
   {
      mTM.failTest();
   }
   
   /**
    * This inner class is used to grant permission to the code in this applet to
    * allow writing to the summary log.
    */
   private class PrivilegedSocketSession implements PrivilegedAction<Void>
   {
      /**
       * String representing the session name.
       */
      protected transient final LMSTestCommunication mLTC;
      /**
       * Constructor of the inner class
       * @param iLTC The LMSRTESession object
       */
      PrivilegedSocketSession(final LMSTestCommunication iLTC)
      {
         mLTC = iLTC;
      }
      
      /**
       * 
       * This run method grants privileged applet code access to write to the
       * summary log. This allows the applet to work in Netscape 6.
       * @return null  
       */
      public Void run()
      {
         mSSI = new LMSServerSocketImpl(new LMSTestMessageManager(mLTC));
         mSSI.startServer();

         return null;
      }
   }
}
