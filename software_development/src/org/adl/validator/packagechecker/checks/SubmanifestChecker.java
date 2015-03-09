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
public class SubmanifestChecker extends PackageChecker
{
   /**
    * A List containing all error messages 
    */
   final private List<ValidatorMessage> mErrorMessages;
   
   /**
    * A String containing the path to the (sub)manifest schematron file
    */
   final private InputStream submanifestSchematronFile;
   
   /**
    * A String containing the path to the xml instance being validated
    */
   final private String mFilename;
   
   /**
    * A SchematronValidationDOMParser object to perform the schematron validation parses
    */
   private SchematronValidationDOMParser mParser;
   
   /**
    * Default Constructor. Sets the attributes to their initial values.
    */
   public SubmanifestChecker()
   {
      super();
      mErrorMessages = new ArrayList<ValidatorMessage>();      
      mParser = new SchematronValidationDOMParser(false);
      
      mFilename = 
         CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.ROOT_DIRECTORY_KEY).toString() + 
         CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.XML_FILE_NAME_KEY).toString();
      
      String submanifestSchematronFilePath = 
    	  "/org/adl/validator/packagechecker/parsers/schematron/SubmanifestSchematron.xml";
      
      submanifestSchematronFile = this.getClass().getResourceAsStream(submanifestSchematronFilePath);
      
      mResult = new Result();
      mResult.setPackageCheckerName(ValidatorCheckerNames.SUBMANIFEST);
   }
   
   /* (non-Javadoc)
    * @see org.adl.validator.packagechecker.PackageChecker#check()
    */
   public Result check()
   {
      mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.INFO,
            Messages.getString("SubmanifestChecker.0")));
      
      mResult.setCheckerSkipped(false);
      
      boolean overallSuccess = true;
      
      // We have to stop if the checkerStateData access did not work correctly
      if ( mFilename == null )
      {
         mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.FAILED,
         Messages.getString("SubmanifestChecker.1")));
         mResult.setPackageCheckerPassed(false);  
         return mResult;
      }
      
      try
      {
         // Perform submanifest checks      	 
    	 // Reset the parser to include submanifests and perform the checks
         mParser = new SchematronValidationDOMParser(true);
          
         overallSuccess = mParser.performParse(submanifestSchematronFile, mFilename);        
         mErrorMessages.addAll(mParser.getErrorMessages());
         submanifestSchematronFile.close();
      
      }
      catch ( IOException ioe )
      {
         final String msg = 
            Messages.getString("SubmanifestChecker.2");
         final ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, msg);
         mErrorMessages.add(message);
         overallSuccess = false;
      }
      
      // Loop through messages and add them to the result object
      final Iterator<ValidatorMessage> resultIter = mErrorMessages.iterator();
         
      while ( resultIter.hasNext() )
      {
         final ValidatorMessage message = (ValidatorMessage)resultIter.next();
         if ( message.getMessageType() == ValidatorMessage.WARNING )
         {
            overallSuccess = false;
         }
         mResult.addPackageCheckerMessage(message);
      }
      
      // We want to warn if one is present, but only stop if it is referenced
      if ( mParser.isSubmanifestPresent() )
      {
         // If a submanifest was used but not referenced, we must tell them one is present
         if ( overallSuccess )
         {
            mResult.addPackageCheckerMessage(
                  new ValidatorMessage(ValidatorMessage.WARNING, 
                  Messages.getString("SubmanifestChecker.3")));
         }
         
         mResult.addPackageCheckerMessage(
               new ValidatorMessage(ValidatorMessage.WARNING, 
               Messages.getString("SubmanifestChecker.7")));
      }
      
      if ( overallSuccess )
      {
         mResult.addPackageCheckerMessage(
               new ValidatorMessage(ValidatorMessage.PASSED, 
                    Messages.getString("SubmanifestChecker.5")));
              mResult.setPackageCheckerPassed(true);
              mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.PASSED,
                    Messages.getString("SubmanifestChecker.6")));
      }
      else
      {
         mResult.setTestStopped(true);
         mResult.setPackageCheckerPassed(true);
         mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.WARNING,
               Messages.getString("SubmanifestChecker.4")));
      }
      return mResult;
   }
}
