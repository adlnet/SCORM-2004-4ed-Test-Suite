package org.adl.testsuite.rte.lms.comm;

import java.applet.Applet;
import java.util.Date;

import netscape.javascript.JSObject;

import org.adl.testsuite.rte.lms.interfaces.MessageManager;
import org.adl.testsuite.rte.lms.util.Command;
import org.adl.testsuite.rte.lms.util.LMSSocketThread;
import org.adl.testsuite.rte.lms.util.Results;

/**
 * Sco applet
 */
public class LMSTestSCOApplet extends Applet
{
   /**
    * removes warning
    */
   private static final long serialVersionUID = 2;
   
   /**
    * _Debug adds additional detailed output statements.
    * */
   protected final static boolean DEBUG = false;
   
   /**
    * Response
    */
   protected transient Object mCurrentResponse;
        
   /**
    * Socket
    */
   private transient LMSSocketImpl mConnection;
    
   /**
    * javascript object
    */
   private transient JSObject mJSO;
   
   /**
    * status
    */
   private transient boolean mScoStatus = true;

   /**
    * destroy
    */
   public void destroy()
   {
      mConnection.sendObject(null);
      mConnection.close();
   }
  
   /**
    * init
    */
   public void init()
   {
      mConnection = new LMSSocketImpl(new MessageManager(){
         private LMSSocketThread mSocket;
         public void registerSocketThread(final LMSSocketThread iThread)
         {
            mSocket = iThread;
         }
         
         public void sendMessage(final LMSMessageObject iObject)
         {
            mSocket.write(iObject);
         }
         
         public void receiveMessage(final LMSMessageObject iObject)
         {
            LMSTestMessageManager.printDiagnostics(iObject, new Date().getTime());
            mCurrentResponse = iObject.getValue();
         } 
      });
      mConnection.start();
   }

   /**
    * Start
    */
   public void start()
   {
      mJSO = JSObject.getWindow(this);
   }

   /**
    * Stop
    */
   public void stop()
   {
      destroy();
   }
   
   /**
    * get response. blocks until a response has been received
    * @return response
    */
   private Object getResponse()
   {
      while (mCurrentResponse == null)
      {
         try
         {
            Thread.sleep(1);
         }
         catch (InterruptedException ie)
         {
            //
         }
      }
      final Object response = mCurrentResponse;
      mCurrentResponse = null;
      if(DEBUG){java.lang.System.out.println("In LMSTestSCOApplet -> getResponse, returning : " + response);}
      return response;
   }

   /**
    * evalid
    * @param iID id
    * @return value
    */
   public boolean evalID(final String iID)
   {
      if (mJSO == null)
      {
         mJSO = JSObject.getWindow(this);
      }
      mConnection.sendObject(new LMSMessageObject("evalID", unNull(iID)));
      final String returnedResult = (String)getResponse();
      if(DEBUG){java.lang.System.out.println("In LMSTestSCOApplet -> evalID, returnedResult is : " + returnedResult);}  
      if ( "".equals(returnedResult) )
      {
         final String[] args = {"You have launched the wrong LMS Test Content Package." +
                          "\nPlease exit this package and refer to the instruction" +
                          "\npage for the correct LMS Test Content Package to launch."};
         mJSO.call("alert", args);
         return false;
      }
      return Boolean.valueOf(returnedResult);
   }
   
   /**
    * userInstr
    * @return userInstr
    */
   public String curUserInstructions()
   {
      mConnection.sendObject(new LMSMessageObject("userInstr"));
      return (String)getResponse();
   }
   
   /**
    * uiQ
    * @return uiQ
    */
   public String getUITestQuestions()
   {
      mConnection.sendObject(new LMSMessageObject("uiQ"));
      return (String)getResponse();
   }
   
   /**
    * evalUIQ
    * @param iUIA iUIA
    * @return evalUIQ
    */
   public boolean evaluateUIResults(final String iUIA)
   {
      mConnection.sendObject(new LMSMessageObject("evalUIQ", unNull(iUIA)));
      return Boolean.valueOf((String)getResponse());
   }
   
   /**
    * Writes the message to the indicated log.
    * 
    * @param iLog - String that indicates if the message should be written to the 
    *             detailed log, summary log or both.
    * @param iType - String that indicates what type of message is to be written.
    * @param iMsg - String that represents the log message to be written.
    */
   public void writeToLog( final String iLog, final String iType, final String iMsg )
   {
      final String message = unNull(iLog) + "~" + unNull(iType) + "~" + unNull(iMsg);
      mConnection.sendObject(new LMSMessageObject("writeToLog", message));
   }
   
   /**
    * Informs the LMS test that the test has been terminated
    */
   public void terminateTest()
   {
      if(DEBUG){java.lang.System.out.println("In LMSTestSCOApplet -> terminateTest");}
      mConnection.sendObject(new LMSMessageObject("terminateTest"));
   }
   
   /**
    * Informs the LMS test that the SCO has failed
    */
   public void failed()
   {
      if(DEBUG){java.lang.System.out.println("In LMSTestSCOApplet -> failed");}
      mConnection.sendObject(new LMSMessageObject("failed"));
   }
   
   /**
    * run commands
    * @return if it did it
    */
   public boolean runCommands()
   {
      final Results results = getCommands().evaluate(JSObject.getWindow(this));
      return evaluateResults(results);
   }
   
   /**
    * set sco status
    * @param iScoStatus status
    */
   public void setScoStatus(final boolean iScoStatus)
   {
      mScoStatus = iScoStatus;
   }
   
   /**
    * get sco status
    * @return status
    */
   public boolean getScoStatus()
   {
      return mScoStatus;
   }
   
   /**
    * Informs the LMS test that the SCO is completed.
    */
   public void completed()
   {
      mConnection.sendObject(new LMSMessageObject("completed"));
   }
   
   /**
    * get commands
    * @return command
    */
   private Command getCommands()
   {
      mConnection.sendObject(new LMSMessageObject("command"));
      return (Command)getResponse();
   }
   
   /**
    * evaluate results
    * @param iResults results
    * @return boolean
    */
   private boolean evaluateResults(final Results iResults)
   {
      mConnection.sendObject(new LMSMessageObject(iResults));
      return Boolean.valueOf((String)getResponse());
   }
   
   private String unNull(final String iStr)
   {
      return (iStr == null)?"":iStr;
   }
}
