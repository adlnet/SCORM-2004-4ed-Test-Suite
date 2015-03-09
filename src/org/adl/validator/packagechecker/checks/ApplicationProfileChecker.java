package org.adl.validator.packagechecker.checks;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.adl.validator.packagechecker.PackageChecker;
import org.adl.validator.packagechecker.parsers.SchematronValidationDOMParser;
import org.adl.validator.util.CheckerStateData;
import org.adl.validator.util.Messages;
import org.adl.validator.util.Result;
import org.adl.validator.util.ValidatorCheckerNames;
import org.adl.validator.util.ValidatorKeyNames;
import org.adl.validator.util.ValidatorMessage;

/**
 * This checker will use schematron xml validation to ensure the xml complies
 * to a defined set of IMS and ADL SCORM rules
 * 
 * @author ADL Technical Team
 *
 */
public class ApplicationProfileChecker extends PackageChecker
{
   /**
    * A Constant value indicating the type of file to be validated 
    */
   private static final String CONTENT_AGGREGATION = "contentaggregation";
   
   /**
    * A Constant value indicating the type of file to be validated 
    */
   private static final String RESOURCE = "resource";
   
   /**
    * A List containing all error messages 
    */
   final private List<ValidatorMessage> mErrorMessages;
   
   /**
    * A String containing the path to the application profile specific 
    * schematron file
    */
   final private InputStream mAppProfileSchematronFile;
   
   /**
    * A String containing the path to the xml instance being validated
    */
   final private String mFilename;
   
   /**
    * A String representing the App Profile of the package
    */
   final private String mPackageType;
   
   /**
    * A SchematronValidationDOMParser object to perform the schematron validation parses
    */
   private SchematronValidationDOMParser mParser;
   
   /**
    * Default Constructor. Sets the attributes to their initial values.
    */
   public ApplicationProfileChecker()
   {
      super();
      mErrorMessages = new ArrayList<ValidatorMessage>();      
      mParser = new SchematronValidationDOMParser(false);
      
      mFilename = 
         CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.ROOT_DIRECTORY_KEY).toString() + 
         CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.XML_FILE_NAME_KEY).toString();
      String appProfileSchematronFilePath = "";
      
      mPackageType = 
         CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.APP_PROFILE_TYPE_KEY).toString();
      
      if ( mPackageType.equals(CONTENT_AGGREGATION) )
      {
         appProfileSchematronFilePath = 
            "/org/adl/validator/packagechecker/parsers/schematron/ContentAggregationSchematron.xml";
      }
      else if ( mPackageType.equals(RESOURCE) )
      {
         appProfileSchematronFilePath = 
            "/org/adl/validator/packagechecker/parsers/schematron/ResourceSchematron.xml";
      } 
      else
      {
         final String failMessage = Messages.getString("ApplicationProfileChecker.0");
         final ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, failMessage);
         mErrorMessages.add(message);  
      }
      
      mAppProfileSchematronFile = this.getClass().getResourceAsStream(appProfileSchematronFilePath);
      
      mResult = new Result();
      mResult.setPackageCheckerName(ValidatorCheckerNames.APP_PROFILE);
      
   }
   
   /* (non-Javadoc)
    * @see org.adl.validator.packagechecker.PackageChecker#check()
    */
   public Result check()
   {
      mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.INFO,
            Messages.getString("ApplicationProfileChecker.1")));
      
      mResult.setCheckerSkipped(false);
      
      boolean overallSuccess = true;
      
      // We have to stop if the checkerStateData access did not work correctly
      if ( mFilename == null || mPackageType == null )
      {
         mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.FAILED,
         Messages.getString("ApplicationProfileChecker.2")));
         mResult.setPackageCheckerPassed(false);  
      }
      else
      {
      
         try
         {
            // Perform application profile specific checks
            overallSuccess = overallSuccess && validate(mAppProfileSchematronFile, mFilename);
            mAppProfileSchematronFile.close();
         }
         catch ( IOException ioe )
         {
            //ioe.printStackTrace();
            final String msg = Messages.getString("ApplicationProfileChecker.3");
            final ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, msg);
            mErrorMessages.add(message);
            overallSuccess = false;
         }
         
         // Loop through messages and add them to the result object
         final Iterator<ValidatorMessage> resultIter = mErrorMessages.iterator();
            
         while ( resultIter.hasNext() )
         {
            final ValidatorMessage message = (ValidatorMessage)resultIter.next();
            if ( message.getMessageType() == ValidatorMessage.FAILED )
            {
               overallSuccess = false;
            }
            mResult.addPackageCheckerMessage(message);
         }
         
         if ( overallSuccess )
         {
            mResult.setPackageCheckerPassed(true);
            mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.PASSED,
                  Messages.getString("ApplicationProfileChecker.5")));
         }
         else
         {
            mResult.setPackageCheckerPassed(false);
            mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.FAILED,
                  Messages.getString("ApplicationProfileChecker.4")));
         }
      }
      return mResult;
   }
   
   /**
    * This method will validate using the schematron file but exclude submanifests from validation
    * 
    * @param iSchematron The schematron rule file used to validate the xml
    * @param iXMLName The path to the xml instance to be vaidatidated
    * @return A boolean indicating the results of validation
    */
   private boolean validate(final InputStream iSchematron, final String iXMLName)
   {
      // Reset the parser and perform the checks
      mParser = new SchematronValidationDOMParser(false);
      
      final boolean success = mParser.performParse(iSchematron, iXMLName);        
      mErrorMessages.addAll(mParser.getErrorMessages());
      
      return success;
   }
}
