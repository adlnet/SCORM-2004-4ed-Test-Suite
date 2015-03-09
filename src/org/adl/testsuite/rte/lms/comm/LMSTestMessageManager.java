package org.adl.testsuite.rte.lms.comm;

import org.adl.testsuite.rte.lms.interfaces.MessageManager;
import org.adl.testsuite.rte.lms.interfaces.TestCommunication;
import org.adl.testsuite.rte.lms.util.Command;
import org.adl.testsuite.rte.lms.util.LMSSocketThread;
import org.adl.testsuite.rte.lms.util.Results;

/**
 * handles socket messages on the server side
 */
public class LMSTestMessageManager implements MessageManager
{
   /**
    * test communication
    */
   private final transient TestCommunication mTC;
   
   /**
    * socket thread
    */
   private transient LMSSocketThread mLST;
   
   /**
    * constructor
    * @param iTC test communication
    */
   public LMSTestMessageManager(final TestCommunication iTC)
   {
      mTC = iTC;
   }

   /**
    * what to do when it receives a message
    * @param iMessage the message
    */
   public void receiveMessage(final LMSMessageObject iMessage)
   {
      if ( iMessage.getValue() instanceof String )
      {  
         final String function = iMessage.getFunction();
         final String value = (String)iMessage.getValue();
         
         if ( "evalID".equals(function) )
         {
            final String[] evalStr = value.split("!");
            final String pkg = evalStr[0];
            final String act = evalStr[1];
            final boolean launchedRightPackage = mTC.evaluateCorrectTCLaunch(pkg);
            if ( launchedRightPackage )
            {
               final boolean deliveredRightActivity = mTC.evaluateCorrectActivityLaunch(act);
//               long postProcessing = (new Date()).getTime();
//               System.out.println("evalID processing time: " + 
//                               (postProcessing - preProcessing) + "ms");
               sendMessage(new LMSMessageObject(function, deliveredRightActivity));
            }
            else
            {
               sendMessage(new LMSMessageObject("wrongPkg", ""));
            }
         }
         else if ( "uiQ".equals(function) )
         {
//            long preProcessing = (new Date()).getTime();
            final String uiQ = mTC.getUIQuestions();
//            long postProcessing = (new Date()).getTime();
//            System.out.println("uiq processing time: " + 
//                               (postProcessing - preProcessing) + "ms");
            sendMessage(new LMSMessageObject(function, uiQ));
         }
         else if ("evalUIQ".equals(function))
         {
//            long preProcessing = (new Date()).getTime();
            final boolean evalUIQ = mTC.evaluateUIQuestions(value);
//            long postProcessing = (new Date()).getTime();
//            System.out.println("evalUIQ processing time: " + 
//                               (postProcessing - preProcessing) + "ms");
            sendMessage(new LMSMessageObject(function, evalUIQ));
         }
         else if ( "writeToLog".equals(function) )
         {
            mTC.writeToLog(value);
         }
         else if ( "command".equals(function) )
         {
//            long preProcessing = (new Date()).getTime();
            final Command command = mTC.getCommands();
//            long postProcessing = (new Date()).getTime();
//            System.out.println("command processing time: " + 
//                               (postProcessing - preProcessing) + "ms");
            sendMessage(new LMSMessageObject(command));
         }
         else if ( "userInstr".equals(function) )
         {
//            long preProcessing = (new Date()).getTime();
            final String userInstr = mTC.getCurrentUserInstructions();
//            long postProcessing = (new Date()).getTime();
//            System.out.println("userInstr processing time: " + 
//                               (postProcessing - preProcessing) + "ms");
            sendMessage(new LMSMessageObject(function, userInstr));
         }
         else if ( "commit".equals(function))
         {
//            long preProcessing = (new Date()).getTime();
            final boolean commit = mTC.commit();
//            long postProcessing = (new Date()).getTime();
//            System.out.println("commit processing time: " + 
//                               (postProcessing - preProcessing) + "ms");
            sendMessage(new LMSMessageObject(function, commit));
         }
         else if ( "terminateTest".equals(function) )
         {
            mTC.terminateTest();
         }
         else if ( "completed".equals(function) )
         {
            mTC.completed();
         }
      }
      else if ( iMessage.getValue() instanceof Results )
      {
//         long preProcessing = (new Date()).getTime();
         final boolean commit = mTC.evaluateResults((Results)iMessage.getValue());
//         long postProcessing = (new Date()).getTime();
//         System.out.println("evalRes processing time: " + 
//                            (postProcessing - preProcessing) + "ms");
         sendMessage(new LMSMessageObject("evalRes~", commit));
      }
   }
   
   /**
    * Commented out method used for debugging diagnostics
    * print diagnostics
    * @param iObject message
    * @param iRTS recieved timestamp
    */ 
   public static void printDiagnostics(final LMSMessageObject iObject, final long iRTS)
   {
      //long socketTime = iRTS - iObject.getTimestamp();
      
      /* 
      System.out.println("");
      System.out.println("function: " + iObject.getFunction());
      System.out.println("object: " + iObject.getValue());
      System.out.println("socket time: " + socketTime);
      */
   }
   
   /**
    * This implementation of registerSocketThread requires that the 
    * thread is an instance of LMSSocketThread.
    * @param iLST thread 
    */
   public void registerSocketThread(final LMSSocketThread iLST)
   {
      mLST = iLST;
   }

   /**
    * Send message
    * @param iMessage the message
    */
   public void sendMessage(final LMSMessageObject iMessage)
   {
      mLST.write(iMessage);
   }
}
