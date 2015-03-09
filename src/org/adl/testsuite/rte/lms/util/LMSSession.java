package org.adl.testsuite.rte.lms.util;

import java.io.Serializable;


/**
 * This class contains the test session information for the LMS Conformance Test.
 *  
 * @author ADL Technical Team
 *
 */
public class LMSSession implements Serializable
{
   /**
    * The instance of this class.
    */
   protected static LMSSession instance;
   
   /**
    * The list of packages that were selected by the test to be run in this
    * testing session.
    */
   protected String[] mCurrentPackagesToRun;
   
   /**
    * The current location within the list of current packages. It's where we 
    * are within the test.
    */
   protected int mIndex = 0;
   
   /**
    * The user entered name of the test LMS. 
    */
   protected String mLMSName = "";
   
   /**
    * The user entered version of the test LMS.
    */
   protected String mLMSVersion = "";
   
   /**
    * The user entered vendor of the test LMS.
    */
   protected String mLMSVendor = "";
   
   /**
    * The name of the first learner added to the test LMS. This learner will be 
    * used for the API tests.
    */
   protected String mL1Name = "";
   
   /**
    * The ID of the first learner added to the test LMS. This learner will be 
    * used for the API tests.
    */
   protected String mL1ID = "";
   
   /**
    * The name of the second learner added to the test LMS. This learner will be 
    * used for all other tests.
    */
   protected String mL2Name = "";
   
   /**
    * The ID of the second learner added to the test LMS. This learner will be 
    * used for all other tests.
    */
   protected String mL2ID = "";
   
   /**
    * Indicates whether the test LMS allowed the first learner to enroll for 
    * the specified courses.
    */
   protected boolean mL1EnrollSuccess = true;
   
   /**
    * Indicates whether the test LMS allowed the second learner to enroll for 
    * the specified courses.
    */
   protected boolean mL2EnrollSuccess = true;
   
   /**
    * Holds the overall status of the test.
    */
   protected boolean mOverallStatus = true;
   
   /**
    * Holds the location of the detailed log for this session.
    */
   protected String mDetailedFileURI = "";
   
   /**
    * holds the time that the detailed file was last changed/saved
    */
   protected long mDetailedFileLastModified = -1;
   
   /**
    * holds the name of this object.
    */
   protected String mObjName = "";
  
   /**
    * Private constructor
    */
   private LMSSession()
   {
   }
   
   /**
    * Provides a way to obtain an instance of this class.
    * 
    * @return The LMSSession instance.
    */
   public static LMSSession getInstance()
   {
      if( instance == null )
      {
         instance = new LMSSession();
      }
      return instance;
   }
   
   /**
    * Sets the list of packages to run this testing session.
    * 
    * @param iPackagesToRun String array of the names of the packages to run 
    * this testing session.
    */
   public void setPackagesToRun(String[] iPackagesToRun)
   {
      mCurrentPackagesToRun = iPackagesToRun;
   }
   
   /**
    * Returns the name of the LMSTestCase stored at a specific location within 
    * the collection.
    * 
    * name is located.
    * 
    * @return String representing the name of the LMSTestCase requested. 
    */
   public String getTestCase()
   {
      
      if( (mCurrentPackagesToRun == null) || (mIndex >= mCurrentPackagesToRun.length) )
      {
         return "";
      }
      return mCurrentPackagesToRun[mIndex++];
   }
   
   /**
    * Gets where the LMS test is currently located within the list of chosen
    * test packages.
    *  
    * @return int used to represent the location within the list of chosen 
    * test packages.
    */
   public int getIndex()
   {
      return mIndex;
   }
   
   /**
    * Returns the name of the test LMS.
    * 
    * @return String representing the name of the test LMS.
    */
   public String getLMSName()
   {
      return mLMSName;
   }
   
   /**
    * Returns the version of the test LMS.
    * 
    * @return String representing the version of the test LMS.
    */
   public String getLMSVersion()
   {
      return mLMSVersion;
   }
   
   /**
    * Returns the vendor name of the test LMS.
    * 
    * @return String representing the name of the LMS vendor.
    */
   public String getLMSVendor()
   {
      return mLMSVendor;
   }
   
   /**
    * Returns the name of the first learner entered in the test LMS.
    * 
    * @return String representation of the name of the first learner.
    */
   public String getL1Name()
   {
      return mL1Name;
   }
   
   /**
    * Returns the name of the second learner entered in the test LMS.
    * 
    * @return String representation of the name of the second learner.
    */
   public String getL2Name()
   {
      return mL2Name;
   }
   
   /**
    * Returns the id of the first learner entered in the test LMS.
    * 
    * @return String representation of the id of the first learner.
    */
   public String getL1ID()
   {
      return mL1ID;
   }
   
   /**
    * Returns the id of the second learner entered in the test LMS.
    * 
    * @return String representation of the id of the second learner.
    */
   public String getL2ID()
   {
      return mL2ID;
   }
   
   /**
    * Returns the course enrollment success for learner 1.
    * 
    * @return Boolean representing whether or not learner 1 was successfully 
    * enrolled for the specified courses.
    */
   public boolean getL1EnrollSuccess()
   {
      return mL1EnrollSuccess;
   }
   
   /**
    * Returns the course enrollment success for learner 2.
    * 
    * @return Boolean representing whether or not learner 2 was successfully 
    * enrolled for the specified courses.
    */
   public boolean getL2EnrollSuccess()
   {
      return mL2EnrollSuccess;
   }
   
   /**
    * Returns the overall status.
    * 
    * @return Boolean representing the overall status.
    */
   public boolean getOverallStatus()
   {
      return mOverallStatus && mL1EnrollSuccess && mL2EnrollSuccess;
   }
   
   /**
    * Returns the number of Test Packages chosen to be tested for this session of 
    * the LMS Test.
    * 
    * @return The number of Test Packages to be run for this session.
    */
   public int getNumOfChosenPackages()
   {
      return mCurrentPackagesToRun.length;
   }
   
   /**
    * Returns the detailed log location for this session.
    * 
    * @return - String representing the location of the detailed log.
    */
   public String getDetailedFileURI()
   {
      return mDetailedFileURI;
   }
   
   /**
    * Returns the time the detailed log file was last changed for this session.
    * 
    * @return - Long that represents the time the detailed log file was last changed.
    */
   public long getDetailedFileLastModified()
   {
      return mDetailedFileLastModified;
   }
   
   /**
    * Returns the current Test Package
    * 
    * @return The name of the current Test Package.
    */
   public String getCurrentPackage()
   {
      return mCurrentPackagesToRun[mIndex-1];
   }
   
   /**
    * Returns the name of the session object.
    * 
    * @return - String that represents the name of the session object.
    */
   public String getObjName()
   {
      return mObjName;
   }
   
   /**
    * Looks to see if the current package is the last one to test. If so, true; 
    * if not, false.
    * 
    * @return boolean indicating if the current package/test case is the last one.
    */
   public boolean wasLastTest()
   {
      return mIndex >= mCurrentPackagesToRun.length; 
   }
   
   /**
    * Stores the name of the test LMS entered by the user.
    * 
    * @param iLMSName The name of the test LMS as entered by the user.
    */
   public void setLMSName(String iLMSName)
   {
      mLMSName = iLMSName;
   }
   
   /**
    * Stores the version of the test LMS entered by the user.
    * 
    * @param iLMSVersion The version of the test LMS as entered by the user.
    */
   public void setLMSVersion(String iLMSVersion)
   {
      mLMSVersion = iLMSVersion;
   }
   
   /**
    * Stores the vendor of the test LMS entered by the user.
    * 
    * @param iLMSVendor The name of the vendor of the test LMS as entered by the 
    * user.
    */
   public void setLMSVendor(String iLMSVendor)
   {
      mLMSVendor = iLMSVendor;
   }
   
   /**
    * Stores the name associated with the first learner in the test LMS.
    * 
    * @param iL1Name The name for the name learner.
    */
   public void setL1Name(String iL1Name)
   {
      mL1Name = iL1Name;
   }
   
   /**
    * Stores the name associated with the second learner in the test LMS.
    * 
    * @param iL2Name The ID for the second learner.
    */
   public void setL2Name(String iL2Name)
   {
      mL2Name = iL2Name;
   }
   
   /**
    * Stores the ID associated with the first learner in the test LMS.
    * 
    * @param iL1ID The ID for the first learner.
    */
   public void setL1ID(String iL1ID)
   {
      mL1ID = iL1ID;
   }
   
   /**
    * Stores the ID associated with the second learner in the test LMS.
    * 
    * @param iL2ID The ID for the second learner.
    */
   public void setL2ID(String iL2ID)
   {
      mL2ID = iL2ID;
   }
   
   /**
    * Stores the success status for learner 1's course enrollment.
    * 
    * @param iL1EnrollSuccess The success of the enrollment.
    */
   public void setL1EnrollSuccess(boolean iL1EnrollSuccess)
   {
      mL1EnrollSuccess = iL1EnrollSuccess;
   }
   
   
   /**
    * Stores the success status for learner 2's course enrollment.
    * 
    * @param iL2EnrollSuccess The success of the enrollment.
    */
   public void setL2EnrollSuccess(boolean iL2EnrollSuccess)
   {
      mL2EnrollSuccess = iL2EnrollSuccess;
   }
   
   /**
    * Stores and tracks the overall status of the test.
    * 
    * @param iStatus The success of calls in the test that will change the overall
    *                conformance.
    */
   public void setOverallStatus(boolean iStatus)
   {
      mOverallStatus = iStatus && mOverallStatus;
   }
   
   /**
    * Empty commit method.
    */
   public void commit()
   {
   }
   
   /**
    * WARNING: This method will reset the state of the session back to the 
    * initial values. 
    */
   public void resetState()
   {
      instance = null;
      instance = new LMSSession();
   }
   
   /**
    * Sets where the LMS test is currently located within the list of chosen 
    * test packages.
    * 
    * @param index int used to represent the location within the list of chosen 
    * test packages.
    */
   public void setIndex(int index)
   {
      mIndex = index;
   }
   
   /**
    * Sets the URI for the detailed log file.
    * 
    * @param iLocation - String that represents the detailed log file URI.
    */
   public void setDetailedFileURI(String iLocation)
   {
      mDetailedFileURI = iLocation;
   }
   
   /**
    * Sets the time the detailed log file was last modified.
    * 
    * @param iTime - Long that represents the time the log was modified.
    */
   public void setDetailedFileLastModified(long iTime)
   {
      mDetailedFileLastModified = iTime;
   }
   
   /**
    * Sets the session state from the memento object.
    * 
    * @param iMemento - Mememto object.
    * @return boolean indicating in the session state was set successfully.
    */
   public boolean setSessionStateFromMemento(Object iMemento)
   {
      boolean success = false;
      
      if ( iMemento instanceof LMSSessionMemento )
      {
         ((LMSSessionMemento)iMemento).getState();
         success = true;
      }
      
      return success;
   }
   
   /**
    * Returns the memento object of the session saved.
    * 
    * @return - Memento object of the session saved.
    */
   public Object getMementoOfSessionState()
   {
      LMSSessionMemento memento = new LMSSessionMemento();
      memento.setState();
      return memento;
   }
   
   /**
    * Stores the name of the session object.
    * 
    * @param iObjName - String representing the name of the session.
    */
   public void setObjName(String iObjName)
   {
      mObjName = iObjName;
   }
   
   /**
    * Inner class used to store the state information for this testing session. 
    * 
    * @author ADL Technical Team
    */
   class LMSSessionMemento implements Serializable
   {
      /**
       * String array that holds the current LMS Test Content Packages to run.
       */
      private String[] memCurrentPackagesToRun;
      
      /**
       * Integer that holds the index of the current package.
       */
      private int memIndex = -1;
      
      /**
       * String that holds the LMSName.
       */
      private String memLMSName = "";
      
      /**
       * String that holds the LMSVersion.
       */
      private String memLMSVersion = "";
      
      /**
       * String that holds the LMSVender.
       */
      private String memLMSVendor = "";
      
      /**
       * String that holds the name of the learner set as Learner 1. 
       */
      private String memL1Name = "";
      
      /**
       * String that holds the ID of the learner set as Learner 1. 
       */
      private String memL1ID = "";
      
      /**
       * String that holds the name of the learner set as Leaner 2.
       */
      private String memL2Name = "";
      
      /**
       * String that holds the ID of the learner set as Learner 2. 
       */
      private String memL2ID = "";
      
      /**
       * Boolean that represents the success of learner 1's enrolling in courses. 
       */
      private boolean memL1EnrollSuccess = false;
      
      /**
       * Boolean that represents the success of learner 2's enrolling in courses. 
       */
      private boolean memL2EnrollSuccess = false;
      
      /**
       * Boolean that represents the overall status of the session. 
       */
      private boolean memOverallStatus = true;
      
      /**
       * String that holds URI of the detailed log file.
       */
      private String memDetailedFileURI = "";
      
      /**
       * Long that represents the date the detailed log was last modified. 
       */
      private long memDetailedFileLastModified = -1;
      
      /**
       * String that holds name of the session object.
       */
      private String memObjName = "";

      /**
       * Retrieves the state of the current session. 
       */
      public void getState()
      {
         instance = new LMSSession();
         instance.setPackagesToRun(memCurrentPackagesToRun);
         instance.setIndex(memIndex);
         instance.setLMSName(memLMSName);
         instance.setLMSVersion(memLMSVersion);
         instance.setLMSVendor(memLMSVendor);
         instance.setL1Name(memL1Name);
         instance.setL1ID(memL1ID);
         instance.setL2Name(memL2Name);
         instance.setL2ID(memL2ID);
         instance.setL1EnrollSuccess(memL1EnrollSuccess);
         instance.setL2EnrollSuccess(memL2EnrollSuccess);
         instance.setOverallStatus(memOverallStatus);
         instance.setDetailedFileURI(memDetailedFileURI);
         instance.setDetailedFileLastModified(memDetailedFileLastModified);
         instance.setObjName(memObjName);
      }
      
      /**
       * Stores the state of the current session.  
       */
      public void setState()
      {
         memCurrentPackagesToRun = mCurrentPackagesToRun;
         memIndex = mIndex;
         memLMSName = mLMSName;
         memLMSVersion = mLMSVersion;
         memLMSVendor = mLMSVendor;
         memL1Name = mL1Name;
         memL1ID = mL1ID;
         memL2Name = mL2Name;
         memL2ID = mL2ID;
         memL1EnrollSuccess = mL1EnrollSuccess;
         memL2EnrollSuccess = mL2EnrollSuccess;
         memOverallStatus = mOverallStatus;
         memDetailedFileURI = mDetailedFileURI;
         memDetailedFileLastModified = mDetailedFileLastModified;
         memObjName = mObjName;
      }
   }
}
