package org.adl.testsuite.util;


/**
 * <strong>Filename:</strong>  VersionHandler.java
 * <br><br>
 *
 * <strong>Description:</strong>  The <code>VersionHandler</code> object 
 * maintains information specific to the version and release of the 
 * Test Suite.  It serves a utilitarian function providing the calling 
 * client with properly formatted textual representations of the following:
 * <ul>
 *    <li>The corresponding version of the SCORM</li>
 *    <li>The corresponding version of the Test Suite</li>
 *    <li>A standard certification confirmation statement</li>
 * </ul>
 *
 * @author ADL Technical Team
 *
 */
public class VersionHandler
{
   /**
    * The corresponding version of the SCORM.
    */
   private final static String SCORM_VERSION = "2004 4<sup>th</sup> Edition";

   /**
    * The corresponding version of the Test Suite.
    */
   private final static String TESTSUITE_VERSION = "Version 1.1.1";

   /**
    * The specific name of the corresponding Test Suite directory
    * using the following format
    * - TestSuite[major version]_[minor version]_[detailed version]
    */
   private static final String TESTSUITE_DIR = "TestSuite";

   /**
    * A standard certification confirmation statement.
    */
   private static final String CERT_STMT = "Successful outcome of this test does " +
                                     "not constitute ADL Certification " +
                                     "unless an ADL Certification Auditor " +
                                     "conducted the test.";

   /**
    * A standard certification confirmation statement.
    */
   private static final String CERT_STMT_UTIL = "Successful outcome of this test " +
                                     "does not constitute ADL Certification.";

   /**
    * This method returns corresponding version of the SCORM
    *
    * @return  The corresponding version of the SCORM.
    */
   public static String getSCORMVersion()
   {
      return SCORM_VERSION;
   }

   /**
    * This method returns corresponding version of the Test Suite.
    *
    * @return  The corresponding version of the Test Suite.
    */
   public static String getTestsuiteVersion()
   {
      return TESTSUITE_VERSION;
   }

   /**
    * This method returns the specific name of the corresponding
    * Test Suite directory using the following format:
    * - TestSuite[major version][minor version][detailed version]
    *
    * @return   The specific name of the corresponding
    *           Test Suite directory.
    */
   public static String getTestsuiteDirectory()
   {
      return TESTSUITE_DIR;
   }

   /**
    * This method returns a standard certification confirmation statement.
    *
    * @return   A standard certification confirmation statement.
    */
   public static String getCertStmt()
   {
      return CERT_STMT;
   }

   /**
    *  This method returns a standard certification confirmation statement
    *  for the utility portions of the Test Suite 
    *  (SCO, MD & Manifest).
    *
    * @return   A standard certification confirmation statement.
    */
   public static String getCertStmtUtil()
   {
      return CERT_STMT_UTIL;
   }
}