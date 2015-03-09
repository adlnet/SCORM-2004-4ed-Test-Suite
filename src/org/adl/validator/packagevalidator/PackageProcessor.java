package org.adl.validator.packagevalidator;

import java.io.File;
import java.util.StringTokenizer;

import org.adl.validator.util.CheckerStateData;
import org.adl.validator.util.Messages;
import org.adl.validator.util.Result;
import org.adl.validator.util.ValidatorCheckerNames;
import org.adl.validator.util.ValidatorKeyNames;
import org.adl.validator.util.ValidatorMessage;
import org.adl.validator.util.processor.URIHandler;
import org.adl.validator.util.processor.UnZipHandler;

/**
 * The package will process the given test subject to prepare it for validation
 * 
 * @author ADL Technical Team
 *
 */
public class PackageProcessor
{

   /**
    * Constructor.
    */
   public PackageProcessor()
   {
      // default contructor.
   }
   
   /**
    * Does the steps to get the root directory for the content package being 
    * tested.
    *  
    * @return Result object the indicates if the root directory was successfully
    *         found. 
    */
   public Result processPackage()
   {
      String fileName = (String)CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.FILE_NAME_KEY);
      
      Result isZipGood = new Result();
      
      // We have to stop if the checkerStateData access did not work correctly
      if ( fileName == null )
      {
         isZipGood.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.FAILED,
         Messages.getString("PackageProcessor.0")));
         isZipGood.setPackageCheckerPassed(false);
         isZipGood.setTestStopped(true);
         return isZipGood;
      }
            
      isZipGood.setPackageCheckerName(ValidatorCheckerNames.PACK_PROCESS);
      String temp = fileName;
      if ( temp.toLowerCase().endsWith(".zip"))
      {
         UnZipHandler unZip = new UnZipHandler(fileName);
         Result tempResult = unZip.extractZipFile();
         String rootDirectory;
         if ( tempResult == null )
         {
            rootDirectory = unZip.getTargetDirectory();
            CheckerStateData.getInstance().setObject(ValidatorKeyNames.ROOT_DIRECTORY_KEY, 
                                                     rootDirectory);
            CheckerStateData.getInstance().setObject(ValidatorKeyNames.XML_FILE_NAME_KEY,
               "imsmanifest.xml");
            CheckerStateData.getInstance().addReservedKey(ValidatorKeyNames.XML_FILE_NAME_KEY);
         }
         else
         {
            isZipGood.setTestStopped(true);
            isZipGood.setPackageCheckerPassed(false);
            String message = Messages.getString("PackageProcessor.1");
            isZipGood.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED, message));
            isZipGood.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.FAILED, message));
            for ( int i = 0; i < tempResult.getPackageCheckerMessages().size(); i++)
            {
               isZipGood.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED, 
                     (((ValidatorMessage)tempResult.getPackageCheckerMessages().get(i))).getMessageText()));
            }
         }
      }
      // Sets the root directory for a non-pif file (Not a .zip file)
      else
      {
         CheckerStateData.getInstance().setObject(ValidatorKeyNames.ROOT_DIRECTORY_KEY, 
                                     URIHandler.escapeSpecialCharacters(getPathOfFile(fileName)));

         // Sets XML_FILE_NAME_KEY to the name of the xml instance being validated
         String xmlFileName = "";
         xmlFileName = (String)CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.FILE_NAME_KEY);
         
         // We have to stop if the checkerStateData access did not work correctly
         if ( xmlFileName == null )
         {
            isZipGood.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.FAILED,
            Messages.getString("PackageProcessor.0")));
            isZipGood.setPackageCheckerPassed(false);
            isZipGood.setTestStopped(true);
            return isZipGood;
         }
         
         String splitFileName[] = xmlFileName.split(File.separator + File.separator);         
         xmlFileName = splitFileName[splitFileName.length - 1];
         CheckerStateData.getInstance().setObject(ValidatorKeyNames.XML_FILE_NAME_KEY,
               xmlFileName);
         CheckerStateData.getInstance().addReservedKey(ValidatorKeyNames.XML_FILE_NAME_KEY);    
      }
      return isZipGood;
   }
   
   /**
    * This method retrieves the directory location of the test subject by
    * truncating the filename off of the URL passed in.
    * 
    * @param iFileName The absolute path of the test subject file
    * @return String - the directory that the file is located
    */
   private String getPathOfFile(String iFileName)
   {
      String result = "";
      String tmp = "";

      try
      {
         StringTokenizer token = new StringTokenizer(iFileName, File.separator, true);

         int numTokens = token.countTokens();

         // We want all but the last token added
         numTokens--;

         for( int i = 0; i < numTokens; i++ )
         {
            tmp = token.nextToken();
            result = result + tmp;
         }
      }
      catch( NullPointerException npe )
      {
         npe.printStackTrace();
      }

      return result;
   }
}
