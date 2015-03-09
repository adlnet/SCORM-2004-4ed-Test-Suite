package org.adl.testsuite.rte.lms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.adl.testsuite.rte.lms.testcase.LMSTestCase;
import org.adl.util.EnvironmentVariable;


/**
 * This class is responsible for holding information about the LMS test, such as 
 * the official name, version, and the test cases.
 * @author ADL Technical Team
 */
public class LMSInfo
{
   private final String mCTSHome = "SCORM4ED_TS111_HOME";
   
   /**
    * The title for ADL.
    */
   private final String mADLTitle = "Advanced Distributed Learning (ADL)";
   
   /**
    * The official name of the Test Suite.
    */
   private final String mCTSName = 
      "SCORM 2004 4<sup>th</sup> Edition Test Suite";  
   
   /**
    * The version of the Test Suite.
    */
   private final String mCTSVersion = "Version 1.1.1";
      
   /**
    * The name of the test.
    */
   private final String mTestName = "LMS Conformance Test";
   
   /**
    * Logger object used for debug logging
    */
   protected Logger mLogger = Logger.getLogger("org.adl.util.debug.testsuite");
   
   /**
    * The name of the hash obj file. This is the file that is loaded to create 
    * the hash table of the Test Cases. 
    */
   protected final String mHashName = "LMSTestCases.obj";
   
   /**
    * The hash of available test cases.
    */
   private Map<String, LMSTestCase> mAvailableTestCases;
   
   private static LMSInfo instance;
   
   /**
    * Default constructor.
    */
   private LMSInfo()
   {
      mAvailableTestCases = getTCHash();
   }
   
   /**
    * Returns a protected instance of this class.
    * 
    * @return An instance of LMSInfo.
    */
   public static LMSInfo getInstance()
   {
      if(instance == null)
      {
         instance = new LMSInfo();
      }
      return instance;
   }
   
   /**
    * Returns a Test Case based on the name requested.
    * 
    * @param iName The name of the test case.
    * 
    * @return The test case requested.
    */
   public LMSTestCase getTestCase(String iName)
   {
      LMSTestCase curTC = mAvailableTestCases.get(iName);
      curTC.resetState();
      return curTC;
   }
   
   /**
    * Returns an unordered List of the available test packages.
    * 
    * @return The List of available test packages.
    */
   public List<String> getTestPackageNames()
   {
      return new ArrayList<String>(mAvailableTestCases.keySet());
   }
   
   /**
    * Returns the environment variable key associated with the root location of 
    * the Test Suite.
    * 
    * @return The environment variable key.
    */
   public String getCTSHome()
   {
      return mCTSHome;
   }
   
   /**
    * Returns the ADL title.
    * 
    * @return the ADL title.
    */
   public String getADLTitle()
   {
      return mADLTitle;
   }
   
   /**
    * Returns the name of the Test Suite
    * 
    * @return the name of the Test Suite
    */
   public String getCTSName()
   {
      return mCTSName;
   }

   /**
    * Returns the CTS version.
    * 
    * @return the CTS version
    */
   public String getCTSVersion()
   {
      return mCTSVersion;
   }
   
   /**
    * Returns the number of Test Cases for the Test Suite LMS Test.
    * 
    * @return The number of Test Cases for the LMS Test.
    */
   public int getNumOfTestCases()
   {
      return mAvailableTestCases.size();
   }

   /**
    * Returns the name of the test
    * 
    * @return name of the test
    */
   public String getTestName()
   {
      return mTestName;
   }
   
   private Map<String, LMSTestCase> getTCHash()
   {
      mLogger.entering("LMSInfo","getTCHash()");
      
      PrivilegedReadFile prf = new PrivilegedReadFile();
      mLogger.exiting("LMSInfo","getTCHash():right after call to AccessController method");
      Map<String, LMSTestCase> map = (HashMap)AccessController.doPrivileged(prf);
      if (map == null)
      {
         map = loadMapAbsolutely();
      }
      return (HashMap)map;
   }
   
   private Map<String, LMSTestCase> loadMapAbsolutely()
   {
      Map<String, LMSTestCase> map;
      String p = EnvironmentVariable.getValue(mCTSHome)
      + File.separatorChar + "build"
      + File.separatorChar + "classes"
      + File.separatorChar + "org"
      + File.separatorChar + "adl"
      + File.separatorChar + "testsuite"
      + File.separatorChar + "rte"
      + File.separatorChar + "lms"
      + File.separatorChar + "util"
      + File.separatorChar + "LMSTestCases.obj";
      try
      {
         ObjectInputStream ois = new ObjectInputStream(new FileInputStream(p));
         map = (HashMap)ois.readObject();
      }
      catch (IOException ioe)
      {
         map = null;
      }
      catch (ClassNotFoundException cnfe)
      {
         map = null;
      }
      return map;
   }
   
   private class PrivilegedReadFile implements PrivilegedAction<Object>
   {      

      public Object run()
      {
         try
         {
            InputStream is = this.getClass().getResourceAsStream("LMSTestCases.obj");
            ObjectInputStream ois = new ObjectInputStream(is);
            return ois.readObject();
         }
         catch (ClassNotFoundException cnfe)
         {
            mLogger.severe("ClassNotFoundException occurred in LMSInfo.PrivilegedReadFile.run()");
            return null;
         }
         catch (IOException ioe)
         {
            mLogger.severe("IOException occurred in LMSInfo.PrivilegedReadFile.run()");
            return null;
         }
         catch(Exception e)
         {
            mLogger.severe("Some kind of Exception occurred in LMSInfo.PrivilegedReadFile.run()");
            return null;
         }
         
      }
      
   }// end class PrivilegedViewLog
}
