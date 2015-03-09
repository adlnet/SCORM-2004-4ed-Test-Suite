package org.adl.testsuite.util;
import org.adl.util.Messages;

/**
 * <strong>Filename</strong>:  ConformanceLabel.java<br><br>
 * 
 * <strong>Description</strong>:  This class is used when the Conformance Label 
 * is printed for the test being performed (LMS, CP, MD or SCO).
 *  
 * @author ADL Technical Team
 */

public class ConformanceLabel
{
   /**
    *  NON CONFORMANT Conformance Label
    */
   public static final int NONCONFORMANT = 0;

   /**
    * LMS RTE Conformant
    */
   public static final int LMSRTE1 = 1;

   /**
    * LMS RTE Non-Conformant
    */
   public static final int LMSRTE0 = 2;

   /**
    * LMS CAM Conformant
    */
   public static final int LMSCAM1 = 3;

   /**
    * LMS CAM Non-Conformant
    */
   public static final int LMSCAM0 = 4;

   /**
    * LMS SN Conformant
    */
   public static final int LMSSN1 = 5;

   /**
    * LMS SN Non-Conformant
    */
   public static final int LMSSN0 = 6;

   /**
    * SCO RTE Conformant
    */
   public static final int SCORTE1 = 7;

   /**
    * SCO RTE Non-Conformant
    */
   public static final int SCORTE0 = 8;

   /**
    * Metadata XML Conformant
    */
   public static final int MDXML1 = 9;

   /**
    * Metadata XML Non-Conformant
    */
   public static final int MDXML0 = 10;

   /**
    * Manifest Conformant
    */
   public static final int MANIFEST1 = 11;

   /**
    * Manifest Non-Conformant
    */
   public static final int MANIFEST0 = 12;

   /**
    * ADL Content Package Conformant
    */
   public static final int ADLCP1 = 13;

   /**
    * ADL Content Package Non-Conformant
    */
   public static final int ADLCP0 = 14;

   /**
    * Content Package CAM Conformant
    */
   public static final int CPCAM1 = 15;

   /**
    * Content Package CAM Non-Conformant
    */
   public static final int CPCAM0 = 16;

   /**
    * Content Package RTE Conformant
    */
   public static final int CPRTE1 = 17;

   /**
    * Content Package RTE Non-Conformant
    */
   public static final int CPRTE0 = 18;

   /**
    * SCORM 2004 Conformant
    */
   public static final int SCORM2004 = 19;

   /**
    * This method returns the conformance text associated with the conformance
    * type.
    * 
    * @param iConformanceType An indicator of the conformance type
    * @param iTestName The Test name
    *
    * @return A string representing the conformance text to be
    * rendered by the Test Suite.
    */
   public static String getConformanceText( int iConformanceType,
                                            String iTestName )
   {
      return "The " + iTestName + " is " + //$NON-NLS-1$ //$NON-NLS-2$
                       getConformanceLabel( iConformanceType );       
   }

   /**
    * This method returns the conformance lable associated with the conformance
    * type.
    * 
    * @param iConformanceType An indicator of the conformance type
    *
    * @return A string representation of the conformance label 
    * associated with the conformance type.
    */
   public static String getConformanceLabel( int iConformanceType )
   {
      String  result = ""; //$NON-NLS-1$

      switch ( iConformanceType )
      {
         case NONCONFORMANT:
         {
            result = Messages.getString("ConformanceLabel.1"); 
            break;
         }
         case LMSRTE1:
         {
            result = Messages.getString("ConformanceLabel.2"); 
            break;
         }
         case LMSRTE0:
         {
            result = Messages.getString("ConformanceLabel.3"); 
            break;
         }
         case LMSCAM1:
         {
            result = Messages.getString("ConformanceLabel.4"); 
            break;
         }
         case LMSCAM0:
         {
            result = Messages.getString("ConformanceLabel.5"); 
            break;
         }
         case LMSSN1:
         {
            result = Messages.getString("ConformanceLabel.6"); 
            break;
         }
         case LMSSN0:
         {
            result = Messages.getString("ConformanceLabel.7"); 
            break;
         }
         case SCORTE1:
         {
            result = Messages.getString("ConformanceLabel.8"); 
            break;
         }
         case SCORTE0:
         {
            result = Messages.getString("ConformanceLabel.9"); 
            break;
         }
         case MDXML1:
         {
            result = Messages.getString("ConformanceLabel.10"); 
            break;
         }
         case MDXML0:
         {
            result = Messages.getString("ConformanceLabel.11"); 
            break;
         }
         case MANIFEST1:
         {
            result = Messages.getString("ConformanceLabel.12"); 
            break;
         }
         case MANIFEST0:
         {
            result = Messages.getString("ConformanceLabel.13"); 
            break;
         }
         case CPCAM1:
         {
            result = Messages.getString("ConformanceLabel.14"); 
            break;
         }
         case CPCAM0:
         {
            result = Messages.getString("ConformanceLabel.15"); 
            break;
         }
         case CPRTE1:
         {
            result = Messages.getString("ConformanceLabel.16"); 
            break;
         }
         case CPRTE0:
         {
            result = Messages.getString("ConformanceLabel.17"); 
            break;
         }
         case SCORM2004:
         {
            result = Messages.getString("ConformanceLabel.18");
                     
            break;
         }
         default:
         {
            // This default case should never happen, return an empty string
            break;
         }
      }

      return result;
   }

   /**
    * This method returns the Certification Statement associated with the 
    * Test Suite.
    * 
    * @return A string representation of the Certification Statement
    */
   public static String getCertificationStatement()
   {
      return VersionHandler.getCertStmt();
   }
}