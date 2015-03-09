package org.adl.validator.packagechecker.checks;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.adl.validator.packagechecker.PackageChecker;
import org.adl.validator.util.CheckerStateData;
import org.adl.validator.util.Messages;
import org.adl.validator.util.Result;
import org.adl.validator.util.ValidatorCheckerNames;
import org.adl.validator.util.ValidatorKeyNames;
import org.adl.validator.util.ValidatorMessage;
import org.adl.validator.util.processor.SchemaHandler;
import org.adl.validator.util.processor.URIHandler;

/**
 * This checker will ensure all required files and controlling documents are at
 * the root of the package
 * 
 * @author ADL Technical Team
 *
 */
public class RequiredFilesChecker extends PackageChecker
{
   /**
    * String representing the full path of the imsmanifest.xml file
    */
   final private String mIMSManifestFile; 
   
   /**
    * String containing the full path of the package root
    */
   final private String mRootDirectory;
   
   /**
    * SchemaHandler used to obtain the list of required files
    */
   final private SchemaHandler mHandler;   
   
   /**
    * Default Constructor. Sets the attributes to their initial values.
    */
   public RequiredFilesChecker()
   {
      // default constructor
      super();
      mRootDirectory = 
         CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.ROOT_DIRECTORY_KEY).toString();
      
      mIMSManifestFile = mRootDirectory + 
         CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.XML_FILE_NAME_KEY);
      
      mHandler = new SchemaHandler(mIMSManifestFile); 
      
      mResult = new Result();
      mResult.setPackageCheckerName(ValidatorCheckerNames.REQ_FILES);  
   }
   
   /* (non-Javadoc)
    * @see org.adl.validator.packagechecker.PackageChecker#check()
    */
   public Result check()
   {
      mResult.setCheckerSkipped(false);
      
      // We have to stop if the checkerStateData access did not work correctly
      if ( mIMSManifestFile == null || mRootDirectory == null )
      {
         mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.FAILED,
         Messages.getString("RequiredFilesChecker.0")));
         mResult.setPackageCheckerPassed(false);
         mResult.setTestStopped(true);
         return mResult;
      }
      
      boolean passed = true;
      List<ValidatorMessage> schemaMessages;
      
      final String fileList = mHandler.createRequiredFilesList();
      schemaMessages = mHandler.getErrorMessages();

      mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.INFO,
            Messages.getString("RequiredFilesChecker.1")));
      
      // Add schema errors to results
      if ( schemaMessages != null )
      {
         final Iterator<ValidatorMessage> schemaIter = schemaMessages.iterator();
         while ( schemaIter.hasNext() )
         {
            final ValidatorMessage vMessage = (ValidatorMessage)schemaIter.next();
            if ( vMessage.getMessageType() == ValidatorMessage.FAILED )   
            {
               passed = false;
            }
            mResult.addPackageCheckerMessage(vMessage);
         }          
      }
      
      // Add file list to CheckerStateData for use with other checks      
      CheckerStateData.getInstance().setObject(ValidatorKeyNames.CONTROLLING_SCHEMAS_KEY, fileList);
      CheckerStateData.getInstance().addReservedKey(ValidatorKeyNames.CONTROLLING_SCHEMAS_KEY);
            
      // If the fileList is empty, a parse error has occurred, do not check for files at root
      if ( !"".equals(fileList) )
      {
         String fileName = "";
         final String requiredFiles[] = fileList.split(" ");
         final List<String> checkedRootFileList = new ArrayList<String>();
         
         for ( int i = 0; i < requiredFiles.length; i++ )
         {
            fileName = requiredFiles[i];
            
            // First check to ensure all required exist at root
            String rootFileName = fileName;
            rootFileName = rootFileName.replaceAll("/", File.separator + File.separator);
            final int splitIndex = rootFileName.lastIndexOf(File.separator);
            
            // The location is a directory, not a schema
            if ( splitIndex >= rootFileName.length() - 2 )
            {
               passed = false;                
               mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED, 
                     Messages.getString("RequiredFilesChecker.2")));
            }
            else
            {
               // Obtain only file name, not path
               rootFileName = rootFileName.substring(splitIndex+1);
               final String rootFile = mRootDirectory + rootFileName;
               
               // Check to ensure file has not already  been checked
               if ( !checkedRootFileList.contains(rootFile) )
               {
                  // Add this file to the list of files which have been checked
                  checkedRootFileList.add(rootFile);
                  if ( fileExists(rootFile) )
                  {
                     mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.PASSED,
                           Messages.getString("RequiredFilesChecker.3", rootFileName)));
                  }
                  else
                  {
                     passed = false;                
                     mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED, 
                           Messages.getString("RequiredFilesChecker.4", rootFileName)));
                  }
               }
            
               // if URL
               if ( URIHandler.isURL(requiredFiles[i]) )
               {
                  fileName = requiredFiles[i];
               }
               else
               {
                  fileName = mRootDirectory + requiredFiles[i];
               }

               // Check to see if the schema is located somewhere other than
               // the root and ensure it is present at that location
               if ( !rootFile.equals(fileName) )
               {
               
                  // Check specified location                  
                  if ( fileExists(fileName) )
                  {
                     mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.PASSED,
                           Messages.getString("RequiredFilesChecker.5", requiredFiles[i])));
                  }
                  else
                  {
                     passed = false;                
                     mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED, 
                           Messages.getString("RequiredFilesChecker.6", requiredFiles[i])));
                  }  
               }
            }
         }
      }
      
      if ( passed )
      {
         mResult.setPackageCheckerPassed(true);
         mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.PASSED,
               Messages.getString("RequiredFilesChecker.7")));
         mResult.setTestStopped(false); 
      }
      else
      {
         mResult.setPackageCheckerPassed(false);
         mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("RequiredFilesChecker.8")));
         mResult.setTestStopped(true);
      }
      
      final String extensionMsg = mHandler.isExtensionsFound() ? Messages.getString("RequiredFilesChecker.9") :
                                            Messages.getString("RequiredFilesChecker.10");
      mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.PASSED, extensionMsg));
      
      return mResult;
   }
   
   /**
    * Checks to make sure the URL File exists.
    * 
    * @param iFilePath is a String that represents the URL.
    * @return boolean that indicates is the file exists.
    */
   private boolean fileExists( final String iFilePath )
   {
      boolean passed = true;

      try
      {
         //    This is an external file
         if( URIHandler.isURL(iFilePath) )
         {
         
            final URL url = new URL(iFilePath);
            final URLConnection urlConn = url.openConnection();
            final HttpURLConnection httpUrlConn = (HttpURLConnection)urlConn;
            final int code = httpUrlConn.getResponseCode();
  
            // try to access the address
            if( code == HttpURLConnection.HTTP_OK )
            {
               mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.PASSED,
                     Messages.getString("RequiredFilesChecker.11", iFilePath)));
               passed = true;
            }
            else
            {
               mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED,
                     Messages.getString("RequiredFilesChecker.12", iFilePath)));
               passed = false;
            }
         
         }
         else
         {
            final URIHandler uriHandler = new URIHandler();
            // Process file path
            String tempPath = iFilePath;
            tempPath = URIHandler.decode(tempPath, URIHandler.ENCODING);
            tempPath = uriHandler.escapeDirectories(tempPath);
            tempPath = tempPath.replaceAll("/", File.separator + File.separator);
            
            final File tempFile = new File(tempPath);
         
            passed = tempFile.exists();
         }
      
      }
      catch( MalformedURLException mfue )
      {
         mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("RequiredFilesChecker.13", iFilePath)));
         passed = false;
      }
      catch( IOException ioe )
      {
         mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("RequiredFilesChecker.14", iFilePath)));
         passed = false;
      }
      
      return passed;
   }
}