package org.adl.testsuite.util;

import org.adl.util.Messages;
import org.adl.logging.DetailedLogMessageCollection;
import org.adl.logging.SummaryLogMessageCollection;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;

/**
 * <strong>Filename:</strong>TestSubjectData.java<br><br>
 *
 * <strong>Description:</strong>  This class provides the ability to store 
 * Test Suite information that may be needed throughout the 
 * testing process.  The class collects information about the test subject, 
 * the testing environment (Java Run-Time Environment, Browswer, Operating 
 * System, etc.).
 * 
 * @author ADL Technical Team
 */
public final class TestSubjectData
{
   /**
    * A static instance of this class
    */
   private static final TestSubjectData M_INSTANCE = new TestSubjectData();
   
   /**
    * Descriptive name for the Test Subject
    */
   private String mTestSubject = "";

   /**
    * The current operating system that the Test Suite is installed
    * on.
    */
   private String mCurOS = "";
   
   /**
    * Operating system message type
    */
   private int mOSMsgType = MessageType.HEADINFO;

   /**
    * The current Java Run-Time Environment that is installed on the system
    * for which the Test Suite is installed.
    */
   private String mCurJRE = "";
   
   /**
    * Java Run-Time Environment message type
    */
   private int mJREMsgType = MessageType.HEADINFO;

   /**
    * The current Internet Browser being used by the operator of the
    * Test Suite.
    */
   private String mCurBrow = "";
   
   /**
    * Internet Browser message type
    */
   private int mBrowMsgType = MessageType.HEADINFO;

   /**
    * The date in which the user started the test.
    */
   private String mDate = "";

   /**
    * The product being tested.  This value is taken from the
    * cooresponding Test Suite Instructions.
    */
   private String mProduct = "";

   /**
    * The version of the product being tested.  This value is taken from the
    * cooresponding Test Suite Instructions.
    */
   private String mVersion = "";

   /**
    * The vendor name of the product being tested.   This value is taken from 
    * the cooresponding Test Suite Instructions.
    */
   private String mVendor = "";

   /**
    * This method returns a reference to the instance of the TestSubjectData
    * 
    * @return TestSubjectData reference
    */
   public static TestSubjectData getInstance()
   {
      return M_INSTANCE;
   }

   /**
    * This method retruns the current browser being used to render the 
    * Test Suite.
    * 
    * @return Returns the Current Browser.
    */
   public String getCurrentBrowser()
   {
      return mCurBrow;
   }

   /**
    * This method is used to set the current browser that is rendering the 
    * Test Suite
    * 
    * @param iCurBrow The Current Browser to set.
    */
   public void setCurrentBrowser(String iCurBrow)
   {
      mCurBrow = iCurBrow;
   }

   /**
    * This method is used to return the current Java Run-Time Environment being
    * used by the Test Suite.
    * 
    * @return Returns the Current JRE.
    */
   public String getCurrentJRE()
   {
      return mCurJRE;
   }

   /**
    * This method is used to set the current Java Run-Time Environment being
    * used by the Test Suite.
    * 
    * @param iCurJRE The Current JRE to set.
    */
   public void setCurrentJRE(String iCurJRE)
   {
      mCurJRE = iCurJRE;
   }

   /**
    * This method is used to return the current Operating System being
    * used by the Test Suite.
    * 
    * @return Returns the Current OS.
    */
   public String getCurrentOS()
   {
      return mCurOS;
   }

   /**
    * This method is used to set the current Java Run-Time Environment being
    * used by the Test Suite.
    * 
    * @param iCurOS The Current OS to set.
    */
   public void setCurrentOS(String iCurOS)
   {
      mCurOS = iCurOS;
   }
   
   /**
    * 
    * This method is used to return the message type of the Operating System 
    * being used by the Test Suite.
    * 
    * @return Returns the message type for the OS
    */
   public int getOSMsgType()
   {
      return mOSMsgType;
   }
   
   /**
    * 
    * This method is used to set the message type of the Operating System 
    * being used by the Test Suite.
    * 
    * @param iOSMsgType The message type for the OS message.  The default value
    * is MessageType.HEADINFO
    */
   public void setOSMsgType(int iOSMsgType)
   {
      mOSMsgType = iOSMsgType;
   }
   
   /**
    * 
    * This method is used to get the message type of the Java Run-Time 
    * Environment being used by the Test Suite.
    * 
    * @return Returns the message type for the JRE
    */
   public int getJREMsgType()
   {
      return mJREMsgType;
   }
   
   /**
    * 
    * This method is used to set the message type of the Java Run-Time 
    * Environment being used by the Test Suite.
    * 
    * @param iJREMsgType The message type for the JRE message.  The default 
    * value is MessageType.HEADINFO
    */
   public void setJREMsgType(int iJREMsgType)
   {
      mJREMsgType = iJREMsgType;
   }
   
   /**
    * This method is used to get the message type of the browser being used by 
    * the Test Suite.
    * 
    * @return Returns the message type for the browser
    */
   public int getBrowMsgType()
   {
      return mBrowMsgType;
   }
   
   /**
    * 
    * This method is used to set the message type of the browser being used by 
    * the Test Suite.
    * 
    * @param iBrowMsgType The message type for the Browser message.  The default 
    * value is MessageType.HEADINFO
    */
   public void setBrowMsgType(int iBrowMsgType)
   {
      mBrowMsgType = iBrowMsgType;
   }

   /**
    * 
    * This method is used to retrieve the date.
    * 
    * @return Returns the Date.
    */
   public String getDate()
   {
      return mDate;
   }

   /**
    * This method is used to set the date.
    * 
    * @param iDate The Date to set.
    */
   public void setDate(String iDate)
   {
      mDate = iDate;
   }

   /**
    * This method is used to retrieve the product.
    * 
    * @return Returns the Product.
    */
   public String getProduct()
   {
      return mProduct;
   }

   /**
    * This method is used to set the product.
    * 
    * @param iProduct The Product to set.
    */
   public void setProduct(String iProduct)
   {
      mProduct = iProduct;
   }

   /**
    * This method is used to retrieve the test subject.
    * 
    * @return Returns the TestSubject.
    */
   public String getTestSubject()
   {
      return mTestSubject;
   }

   /**
    * This method is used to set the test subject. 
    * 
    * @param iTestSubject The TestSubject to set.
    */
   public void setTestSubject(String iTestSubject)
   {
      mTestSubject = iTestSubject;
   }

   /**
    * This method is used to retrieve the vendor.
    * 
    * @return Returns the Vendor.
    */
   public String getVendor()
   {
      return mVendor;
   }

   /**
    * This method is used to set the vendor.
    * 
    * @param iVendor The Vendor to set.
    */
   public void setVendor(String iVendor)
   {
      mVendor = iVendor;
   }

   /**
    * This method is used to retrieve the version.
    * 
    * @return Returns the Version.
    */
   public String getVersion()
   {
      return mVersion;
   }

   /**
    * This method is used to set the version.
    * 
    * @param iVersion The Version to set.
    */
   public void setVersion(String iVersion)
   {
      mVersion = iVersion;
   }
   
   /**
    * This method sends the test subject data to the detailed log message 
    * collection
    * 
    * @param iTest The type of test being run
    */
   public void sendToDetailedLog( String iTest )
   {
      DetailedLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.HEADER,Messages.getString("TestEnvInfo.header")));
      
      DetailedLogMessageCollection.getInstance().addMessage(
         new LogMessage(mOSMsgType,mCurOS ));
       
      DetailedLogMessageCollection.getInstance().addMessage(
         new LogMessage(mJREMsgType,mCurJRE ));
       
      DetailedLogMessageCollection.getInstance().addMessage(
         new LogMessage(mBrowMsgType,mCurBrow ));
      
      DetailedLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.HEADER, ""));
      
      DetailedLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.HEADER,Messages.getString("TestSubjectData.TIDTitle")));
      
      DetailedLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.HEADER,Messages.getString("TestSubjectData.TIDDate", mDate)));
      
      DetailedLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.HEADER,Messages.getString("TestSubjectData.TIDProd", iTest, mProduct)));
      
      DetailedLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.HEADER,Messages.getString("TestSubjectData.TIDVers", iTest, mVersion)));
      
      DetailedLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.HEADER,Messages.getString("TestSubjectData.TIDVend", iTest, mVendor)));
      
      DetailedLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.HEADER, ""));
      
   }
   
   /**
    * This method sends the test subject data to the summary log message 
    * collection
    * 
    * @param iTest The type of test being run
    */
   public void sendToSummaryLog( String iTest )
   {
      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.HEADINFO,Messages.getString("TestEnvInfo.header")));
      
      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(mOSMsgType,mCurOS ));
       
      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(mJREMsgType,mCurJRE ));
       
      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(mBrowMsgType,mCurBrow ));
      
      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.HEADER, ""));
      
      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.HEADER,Messages.getString("TestSubjectData.TIDTitle")));
      
      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.HEADER,Messages.getString("TestSubjectData.TIDDate", mDate)));
      
      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.HEADER,Messages.getString("TestSubjectData.TIDProd", iTest, mProduct)));
      
      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.HEADER,Messages.getString("TestSubjectData.TIDVers", iTest, mVersion)));
      
      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.HEADER,Messages.getString("TestSubjectData.TIDVend", iTest, mVendor)));
      
      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.HEADER, ""));
      
      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.XMLOTHER, "HR"));
   }
   
   /**
    * called by the LmsLogger during the LMS test
    * @param iTest String value "LMS"
    * @return LogMessage[] holding the messages to be added to the summary and
    * detailed logs
    */
   public LogMessage[] getPreliminaryInfo( String iTest )
   {
      LogMessage[] msgs = {
         new LogMessage(MessageType.HEADINFO,Messages.getString("TestEnvInfo.header")),
         new LogMessage(mOSMsgType,mCurOS ),
         new LogMessage(mJREMsgType,mCurJRE ),
         new LogMessage(mBrowMsgType,mCurBrow ),
         new LogMessage(MessageType.HEADER, ""),
         new LogMessage(MessageType.HEADER,Messages.getString("TestSubjectData.TIDTitle")),
         new LogMessage(MessageType.HEADER,Messages.getString("TestSubjectData.TIDDate", mDate)),
         new LogMessage(MessageType.HEADER,Messages.getString("TestSubjectData.TIDProd", iTest, mProduct)),
         new LogMessage(MessageType.HEADER,Messages.getString("TestSubjectData.TIDVers", iTest, mVersion)),
         new LogMessage(MessageType.HEADER,Messages.getString("TestSubjectData.TIDVend", iTest, mVendor)),
         new LogMessage(MessageType.HEADER, ""),
         new LogMessage(MessageType.INFO,Messages.getString("LearnerInfo.header"))};
      
      return msgs;
   }
}
