package org.adl.validator.packagechecker.checks;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.adl.validator.packagechecker.PackageChecker;
import org.adl.validator.packagechecker.parsers.ResourceHrefSaxParser;
import org.adl.validator.util.CheckerStateData;
import org.adl.validator.util.Messages;
import org.adl.validator.util.Result;
import org.adl.validator.util.ValidatorCheckerNames;
import org.adl.validator.util.ValidatorKeyNames;
import org.adl.validator.util.ValidatorMessage;
import org.adl.validator.util.processor.URIHandler;

/**
 * This checker will verify that all files referenced in the xml instance are
 * located in the package
 * 
 * @author ADL Technical Team
 *
 */
public class ResourceHrefChecker extends PackageChecker
{
   /**
    * A constant value for 3
    */
   private static final int BASE_LENGTH = 3;
   
   /**
    * A constant value for 4
    */
   private static final int FTP_LENGTH = 4;
   
   /**
    * A constant value for 5
    */
   private static final int HTTP_LENGTH = 5;
   
   /**
    * A constant value for 5
    */
   private static final int FTPS_LENGTH = 5;
   
   /**
    * A constant value for 6
    */
   private static final int HTTPS_LENGTH = 6;
  
   /**
    * String containing the full path of the package root
    */
   final private String mRootDirectory;
   
   /**
    * The default constructor
    */
   public ResourceHrefChecker()
   {
      // Default Constructor
      super();
      mRootDirectory = 
         CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.ROOT_DIRECTORY_KEY).toString();

      mResult = new Result();
      mResult.setPackageCheckerName(ValidatorCheckerNames.RES_HREF);       
   }
   
   /* (non-Javadoc)
    * @see org.adl.validator.packagechecker.PackageChecker#check()
    */
   public Result check()
   {   
      // We have to stop if the checkerStateData access did not work correctly
      if ( mRootDirectory == null )
      {
         mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.FAILED,
         Messages.getString("ResourceHrefChecker.0")));
         mResult.setPackageCheckerPassed(false);
         return mResult;
      }
      
      final String IMSManifestFile = mRootDirectory + "imsmanifest.xml";
      
      // If the test is for the manifest only skip this checker
      if ( CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.MANIFEST_ONLY_KEY) != null && 
            CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.MANIFEST_ONLY_KEY).toString().equals("true") )
      {
         mResult.setCheckerSkipped(true);
         return mResult;
      }
      String message = "";
      mResult.setCheckerSkipped(false);
      boolean passed = true;
      boolean filesExist = false;
      boolean externalHrefExists = false;
      
      final URIHandler pathHandler = new URIHandler();
      String currentFile = "";
      String escapedURI = "";
      String tempCurrentFile = "";
      
      final ResourceHrefSaxParser resourceParser = new ResourceHrefSaxParser();
      mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.INFO,
            Messages.getString("ResourceHrefChecker.1")));
      final boolean success = resourceParser.performParse(IMSManifestFile);
      
      if ( success )      
      {
         final Set<String> resourceSet = resourceParser.getFileHrefs();
         final Set<String> packageFiles = getPackageFiles(URIHandler.decode(mRootDirectory, URIHandler.ENCODING));
         
         // Store to possibly be used by excessBaggageChecker
         CheckerStateData.getInstance().setObject(ValidatorKeyNames.RESOURCE_FILE_LIST_KEY, resourceSet);
         CheckerStateData.getInstance().addReservedKey(ValidatorKeyNames.RESOURCE_FILE_LIST_KEY);
         
         final Iterator<String> resourceIter = resourceSet.iterator();
         
         while ( resourceIter.hasNext() )
         {           
            filesExist = true;
            currentFile = resourceIter.next().toString();
            tempCurrentFile = currentFile;
            
            // Handle any irregular characters are sequence in the path  
            currentFile = URIHandler.decode(currentFile, URIHandler.ENCODING);
            
            // If decoding failed, generate and error and reset the string
            if ( currentFile == null )
            {
               mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED,
                     Messages.getString("ResourceHrefChecker.8", tempCurrentFile)));
               currentFile = tempCurrentFile;
            }            
            
            escapedURI = pathHandler.escapeDirectories(currentFile);
           
            int rootExceeded = pathHandler.wasRootExceeded();
            
            if ( rootExceeded > 0 )
            {
               mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED,
                     Messages.getString("ResourceHrefChecker.9", currentFile, rootExceeded)));               
            }            
            
            // If path points to an external path, attempt to open a connection
            if( (   (escapedURI.length() > FTP_LENGTH) && (escapedURI.substring(0, HTTP_LENGTH).equals("http:")) ) || 
                  ( (escapedURI.length() > HTTP_LENGTH) && (escapedURI.substring(0, HTTPS_LENGTH).equals("https:")) ) ||
                  ( (escapedURI.length() > BASE_LENGTH) && (escapedURI.substring(0, FTP_LENGTH).equals("ftp:")) ) || 
                  ( (escapedURI.length() > FTP_LENGTH) && (escapedURI.substring(0, FTPS_LENGTH).equals("ftps:")) ) )
            {
               // This is an external file
               try
               {
                  final URL url = new URL(escapedURI);
                  final URLConnection urlConn = url.openConnection();
                  final HttpURLConnection httpUrlConn = (HttpURLConnection)urlConn;
                  final int code = httpUrlConn.getResponseCode();
        
                  // try to access the address
                  if( code == HttpURLConnection.HTTP_OK )
                  {
                     mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.PASSED,
                           Messages.getString("ResourceHrefChecker.10", currentFile)));
                  }
                  else
                  {
                     mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED,
                           Messages.getString("ResourceHrefChecker.11", currentFile)));
                     passed = false;
                  }
               }
               catch( MalformedURLException mfue )
               {
                  mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED,
                        Messages.getString("ResourceHrefChecker.12", currentFile)));
                  passed = false;
               }
               catch( IOException ioe )
               {
                  mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED,
                        Messages.getString("ResourceHrefChecker.13", currentFile)));
                  passed = false;
               }
            }
            else
            {
               final File tempFile = new File(URIHandler.decode(mRootDirectory, URIHandler.ENCODING) + escapedURI);
            
               if ( tempFile.exists() && rootExceeded == 0 )
               {                     
                  mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.PASSED,
                        Messages.getString("ResourceHrefChecker.14", currentFile)));

                  // Check to ensure the case matches as well
                  if ( !packageFiles.contains((URIHandler.decode(mRootDirectory, URIHandler.ENCODING)+escapedURI).replaceAll("/", File.separator + File.separator)))
                  {
                     mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.WARNING,
                           Messages.getString("ResourceHrefChecker.15", currentFile)));
                  }
               }
               else
               {
                  passed = false;         
                  mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED,
                        Messages.getString("ResourceHrefChecker.16", currentFile)));
               }
               rootExceeded = 0;
            }
         }
         
         // Check the resource hrefs to ensure none are external
         final Set<String> resourceHrefs = resourceParser.getResourceFileHrefs();
         final Iterator<String> resourceHrefIter = resourceHrefs.iterator();
         String currentHref = "";
         String tempHref = "";
         
         
         while ( resourceHrefIter.hasNext() )
         {
            currentHref = resourceHrefIter.next().toString();
            tempHref = currentHref;   // Saved for messages
            currentHref = URIHandler.decode(currentHref, URIHandler.ENCODING);  
            
            // If decoding failed, generate and error and reset the string
            if ( currentHref == null )
            {
               mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED,
                     Messages.getString("ResourceHrefChecker.17", tempHref)));
               currentHref = tempHref;
            }
            escapedURI = pathHandler.escapeDirectories(currentHref);
            
            if( (   (escapedURI.length() > FTP_LENGTH) && (escapedURI.substring(0, HTTP_LENGTH).equals("http:")) ) || 
                  ( (escapedURI.length() > HTTP_LENGTH) && (escapedURI.substring(0, HTTPS_LENGTH).equals("https:")) ) ||
                  ( (escapedURI.length() > BASE_LENGTH) && (escapedURI.substring(0, FTP_LENGTH).equals("ftp:")) ) || 
                  ( (escapedURI.length() > FTP_LENGTH) && (escapedURI.substring(0, FTPS_LENGTH).equals("ftps:")) ) )
            {
               mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.WARNING,
                     Messages.getString("ResourceHrefChecker.18", tempHref)));
               externalHrefExists = true;
            }
         }
         
         if ( !filesExist )
         {
            message = Messages.getString("ResourceHrefChecker.4");
            mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.PASSED, message));
            mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.PASSED,
                  message));
         }
         else if ( !passed )
         {
            mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.FAILED,
            Messages.getString("ResourceHrefChecker.5")));        
         }
         else if ( externalHrefExists )
         {
            message = Messages.getString("ResourceHrefChecker.6");
            mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.WARNING, message));
            // If they referenced something outside the package, we don't want to continue
            mResult.setTestStopped(true);
         }
         else if ( passed )
         {
            mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.PASSED, 
                  Messages.getString("ResourceHrefChecker.7")));
         }
         mResult.setPackageCheckerPassed(passed);
         
      }
      else
      {
         mResult.setPackageCheckerPassed(false);
         mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("ResourceHrefChecker.2")));
         mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("ResourceHrefChecker.3")));
      }
      return mResult;
   }
   
   /**
    * This method will create a list of all files present in the package
    * Note: Manifests and controlling documents are excluded
    * 
    * @param iPackageRoot is a String containing the path to the root of the package
    * @return a Set containing the files the package contains
    */
   private Set<String> getPackageFiles(final String iPackageRoot)
   {
      final File rootDirectory = new File(iPackageRoot);      
      File currentFile;
      final Set<String> files = new LinkedHashSet<String>();
      
      if (rootDirectory.isDirectory()) 
      {         
         final String[] directoryContents = rootDirectory.list();
         
         for (int i=0; i < directoryContents.length; i++) 
         {
            currentFile = new File( iPackageRoot + File.separator + directoryContents[i] );
            
            if ( currentFile.isDirectory() )
            {
               files.addAll(getPackageFiles(currentFile.getAbsolutePath()));
            }
            else if ( currentFile.getName().indexOf(".xsd") < 0 &&
                      currentFile.getName().indexOf(".dtd") < 0 &&
                      CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.XML_FILE_NAME_KEY) != null &&
                      !currentFile.getName().equals(CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.XML_FILE_NAME_KEY)) )
            {               
               files.add(currentFile.getAbsolutePath());               
            }
         }
      }
      return files;
   }
}