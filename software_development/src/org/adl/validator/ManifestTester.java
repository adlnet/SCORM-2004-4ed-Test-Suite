package org.adl.validator;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.adl.util.Resources;
import org.adl.validator.util.ManifestTesterMessages;
import org.adl.validator.util.Result;
import org.adl.validator.util.ResultCollection;
import org.adl.validator.util.ValidatorMessage;

/**
 * Provides an way to run the validator from the command prompt or through a stand
 * alone GUI.
 * 
 * @author ADL Technical Team
 *
 */
public class ManifestTester
{
   
   /**
    * Holds the Application Profile type.
    */
   private static String mPackageProfileType = "";
       
   /**
    * List of the file paths of the Manifest files to be tested.
    */
   private static List<String> mFilePathList = new ArrayList<String>();
      
   /**
    * The number of Manifest test that fail.
    */
   private static int mNumOfFailures = 0;
   
   /**
    * The number of items being tested
    */
   private static int mNumTested = 0;
   /**
    * Main class
    * 
    * @param iArgs - Array of the parameters
    */
   public static void main(String[] iArgs)
   {      
      executeTest(iArgs);      
   }
      
   /**
    * Main testing class
    * 
    * @param iArgs - Array of the parameters
    */
   protected static void executeTest( String[] iArgs )
   {
      // Reset the counters to make sure nothing was saved from previous runs
      mNumOfFailures = 0;
      mNumTested = 0;
      mFilePathList = new ArrayList<String>();
      
      boolean paramatersCorrect = processParameters(iArgs);
      
      // Something was wrong with what user provided, show user help
      if ( !paramatersCorrect )
      {
         showHelp();
         return;
      }
      
      Iterator<String> fileIter = mFilePathList.iterator();
      ResultCollection results;
      String resultOutput = "";
      File currFile;
      File[] dirFiles;
      
      while ( fileIter.hasNext() )
      {
         currFile = new File(fileIter.next().toString());
         
         // If it is a directory, we have to get everything in it we can validate
         if ( currFile.isDirectory())
         {
            dirFiles = currFile.listFiles();
            boolean validatedFile = false;
            for ( int i = 0; i < dirFiles.length; i++ )
            {
               // We only want files in the directory, no directories
               if ( !dirFiles[i].isDirectory() && checkExtension(dirFiles[i].getAbsolutePath() , true) )
               {
                  validatedFile = true;
                  results = validate(dirFiles[i].getAbsolutePath());
                  resultOutput = formatResults(dirFiles[i].getAbsolutePath(), results);
                  System.out.println(resultOutput);
               }
            }
            
            // No valid files were found in the directory, throw an error
            if ( !validatedFile )
            {
               System.out.println(ManifestTesterMessages.getString("ManifestTester.0"));
               System.out.println(ManifestTesterMessages.getString("ManifestTester.19", currFile.getAbsolutePath()));
               System.out.println(ManifestTesterMessages.getString("ManifestTester.0") + "\n");
               System.out.println(ManifestTesterMessages.getString("ManifestTester.20", currFile.getAbsolutePath()));
               System.out.println(ManifestTesterMessages.getString("ManifestTester.1"));
            }
         }
         else
         {
            if ( checkExtension(currFile.getAbsolutePath(), false ) )
            {   
               results = validate(currFile.getAbsolutePath());
               resultOutput = formatResults(currFile.getAbsolutePath(), results);
               System.out.println(resultOutput);
            }
            else
            {
               System.out.println(ManifestTesterMessages.getString("ManifestTester.0"));
               System.out.println(ManifestTesterMessages.getString("ManifestTester.19", currFile.getAbsolutePath()));
               System.out.println(ManifestTesterMessages.getString("ManifestTester.0") + "\n");
               System.out.println(ManifestTesterMessages.getString("ManifestTester.21", currFile.getAbsolutePath()));
               System.out.println(ManifestTesterMessages.getString("ManifestTester.2"));
            }
         }
      }
      
      // Testing is done, print the summary results
      resultOutput = printSummary();
      System.out.println(resultOutput);
   }

   protected static boolean checkExtension(String iExt, boolean iFolder)
   {
      // File has an extension, and it may be valid
      if ( (iExt.indexOf(".") != -1) && (iExt.length() >= 4 ) )
      {
         String ext = iExt.substring(iExt.lastIndexOf("."));
         String fileNameWithExt = iExt.substring(iExt.lastIndexOf("\\"));
         
         if ( ext.equalsIgnoreCase(".zip") )
         {
            return true;
         }
         else if ( !iFolder && fileNameWithExt.equalsIgnoreCase("\\imsmanifest.xml") )
         {
            return true;
         }
         else
         {
            return false;
         }
      }
      else
      {
         return false;
      }
   }

   /**
    * Formats the results of the tests
    * 
    * @param iFilePath - String that represents the file path of the Manifest file
    *                    being tested.
    * @param iResults - ResultCollection object the was returned from the validation
    *                   of the Manifest file.
    *                   
    * @return - String representation of the validator results.                 
    */
   protected static String formatResults( String iFilePath, ResultCollection iResults )
   {
      // Create header for each item tested
      StringBuffer mResults = new StringBuffer();
      mResults.append(ManifestTesterMessages.getString("ManifestTester.0") + "\n");
      mResults.append(ManifestTesterMessages.getString("ManifestTester.19", iFilePath) + "\n");
      mResults.append(ManifestTesterMessages.getString("ManifestTester.0") + "\n\n");
      
      List<String> failures;
      Iterator<Result> resIter = iResults.getPackageResultsCollection().iterator();
      boolean testFails = false;
      while ( resIter.hasNext() )
      {
            Result res =(Result)resIter.next();
            if ((res.getOverallStatusMessage().size() > 0) ||
                (getFailures(res).size() > 0 ))
            {
               // Get summary messages
               if (!res.getOverallStatusMessage().isEmpty())
               {
                  Iterator<ValidatorMessage> subIter = res.getOverallStatusMessage().iterator();
                  while ( subIter.hasNext() )
                  {                     
                     mResults.append(subIter.next().toString() + "\n");
                  }
               }
               
               failures = getFailures(res);
               // Ouput a seperator if there are detailed results
               if ( failures.size() > 0 )
               {                  
                  mResults.append(ManifestTesterMessages.getString("ManifestTester.22") + "\n");
               }
               
               // Get detailed results if there are any
               Iterator<String> failureIter = failures.iterator();
               while (failureIter.hasNext())
               {
                  String tempMsg = failureIter.next().toString();
                  mResults.append("\t" + tempMsg + "\n");
                  
                  if (tempMsg.startsWith("FAILED"))
                  {
                     testFails = true;
                  }
               }
            }
      }
      if (testFails)
      {
         mNumOfFailures++;  
         mResults.append(ManifestTesterMessages.getString("ManifestTester.3"));
      }
      else
      {
         mResults.append(ManifestTesterMessages.getString("ManifestTester.4"));
      }
      
      return mResults.toString();
   }


   /**
    * Validates the Manifest file specified.
    * 
    * @param iFilePath - String that represents the file path of the Manifest file
    *                    being tested.
    * @return - ResultCollection object that holds the results of the validation 
    *           of the Manifest file.
    */
   protected static ResultCollection validate(String iFilePath)
   {
      mNumTested++;
      
      Validator mSCORMValidator = new Validator(iFilePath, mPackageProfileType, true);

      List<String> checkerList = new ArrayList<String>();
      checkerList.add("org.adl.validator.packagechecker.checks.IMSManifestAtRootChecker");
      checkerList.add("org.adl.validator.packagechecker.checks.WellformednessChecker");
      checkerList.add("org.adl.validator.packagechecker.checks.SubmanifestChecker");
      checkerList.add("org.adl.validator.packagechecker.checks.ManifestRootElementChecker");
      checkerList.add("org.adl.validator.packagechecker.checks.RequiredFilesChecker");
      checkerList.add("org.adl.validator.packagechecker.checks.SchemaValidationChecker");
      checkerList.add("org.adl.validator.packagechecker.checks.ApplicationProfileChecker");

      mSCORMValidator.setCheckerList(checkerList);

      mSCORMValidator.validate();
      return mSCORMValidator.getResultCollection();
   }

   /**
    * Processes the parameters that were passed into the ManifestTester
    * 
    * @param iArgs - Array of the parameters
    * @return - boolean representing if the parameters process successfully.
    */
   protected static boolean processParameters( String[] iArgs )
   {
      mPackageProfileType = "";
      mFilePathList = new ArrayList<String>();
      
      boolean doneWithFiles = false;
      
      for ( int i = 0; i < iArgs.length; i++ )
      {
         if ( iArgs[i].equalsIgnoreCase("-" + ManifestTesterMessages.getString("ManifestTester.27")) || 
               iArgs[i].equalsIgnoreCase("-" + ManifestTesterMessages.getString("ManifestTester.28")))
         {
            // Just return false, help will be displayed due to failed result
            return false;
         }
         else if ( !doneWithFiles && iArgs[i].charAt(0) != '-')
         {
            mFilePathList.add(iArgs[i]);
         }    
         else if ( iArgs[i].equalsIgnoreCase("-" + ManifestTesterMessages.getString("ManifestTester.29")) )
         {
            doneWithFiles = true;
            // Duplicate set, return error
            if ( !mPackageProfileType.equals("") )
            {
               return false;
            }
            mPackageProfileType = "resource";
         }
         else
         {
            // Output the bad parameter so user knows what happened
            System.out.println(ManifestTesterMessages.getString("ManifestTester.23", iArgs[i]));
            System.out.println("");
            return false;
         }
      }

      if ( mPackageProfileType.equals("") )
      {
         mPackageProfileType = "contentaggregation";
      }

      if ( mFilePathList.size() == 0 )
      {
         // Output the error so user knows what happened
         System.out.println(ManifestTesterMessages.getString("ManifestTester.5"));
         System.out.println("");
         return false;
      }        
      return true;
   }
   
   /**
    * Prints a Summary of the files tested once all the test are complete.
    * 
    * @return - String that represents the summary of the test ran
    */
   private static String printSummary()
   {
      StringBuffer mSummary = new StringBuffer();
      mSummary.append(ManifestTesterMessages.getString("ManifestTester.0") + "\n");
      mSummary.append(ManifestTesterMessages.getString("ManifestTester.24", Integer.toString(mNumTested)) + "\n");
      mSummary.append(ManifestTesterMessages.getString("ManifestTester.25", Integer.toString(mNumTested - mNumOfFailures)) + "\n");
      mSummary.append(ManifestTesterMessages.getString("ManifestTester.26", Integer.toString(mNumOfFailures)) + "\n");
      mSummary.append(ManifestTesterMessages.getString("ManifestTester.0") + "\n");
      return mSummary.toString();
      
   }

   /**
    * Pulls out all of the Error, Warning and Other messages that were returned
    * by the each Checker run by the Validator.
    * 
    * @param iResult - A Result object a that maps to a Checker run by the Validator.
    * @return List - A list of Error, Warning and Other messages.
    */
   private static List<String> getFailures(Result iResult)
   {
      Iterator<ValidatorMessage> msgIter;
//      Iterator overallStatusIter;
      Result res = iResult;
      ValidatorMessage msg;
      List<String> failList = new ArrayList<String>();
      
//      overallStatusIter = res.getOverallStatusMessage().iterator();
//      while ( overallStatusIter.hasNext() )
//      {
//         msg = (ValidatorMessage)overallStatusIter.next();
//         if ( msg.getMessageType() == ValidatorMessage.FAILED )
//         {
//            failList.add(msg.toString());
//         }
//      }
      
      msgIter = res.getPackageCheckerMessages().iterator();
      while ( msgIter.hasNext() )
      {
         msg = (ValidatorMessage)msgIter.next();
         if ( (msg.getMessageType() == ValidatorMessage.FAILED) || 
              (msg.getMessageType() == ValidatorMessage.WARNING) ||
              (msg.getMessageType() == ValidatorMessage.OTHER))
         {
            failList.add(msg.toString());
         }
      }         
           
      return failList;
   }
   
   /**
    * Displays the information about the parameter
    */
   protected static void showHelp()
   {
      System.out.println(ManifestTesterMessages.getString("ManifestTester.6"));
      System.out.println(ManifestTesterMessages.getString("ManifestTester.7"));
      System.out.println("");
      System.out.println(ManifestTesterMessages.getString("ManifestTester.8"));
      System.out.println(ManifestTesterMessages.getString("ManifestTester.9"));
      System.out.println(ManifestTesterMessages.getString("ManifestTester.10"));
      System.out.println(ManifestTesterMessages.getString("ManifestTester.11"));
      System.out.println("");
      System.out.println(ManifestTesterMessages.getString("ManifestTester.12"));
      System.out.println(ManifestTesterMessages.getString("ManifestTester.13"));
      System.out.println(ManifestTesterMessages.getString("ManifestTester.14"));
      System.out.println("");
      System.out.println(ManifestTesterMessages.getString("ManifestTester.15"));
      System.out.println(ManifestTesterMessages.getString("ManifestTester.16"));
      System.out.println("");
      System.out.println(ManifestTesterMessages.getString("ManifestTester.17"));
      System.out.println(ManifestTesterMessages.getString("ManifestTester.18"));
   }

}
