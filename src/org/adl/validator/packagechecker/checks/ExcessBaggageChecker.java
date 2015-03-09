package org.adl.validator.packagechecker.checks;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.Set;

import org.adl.validator.packagechecker.PackageChecker;
import org.adl.validator.packagechecker.parsers.ExcessBaggageSaxParser;
import org.adl.validator.util.CheckerStateData;
import org.adl.validator.util.Messages;
import org.adl.validator.util.Result;
import org.adl.validator.util.ValidatorCheckerNames;
import org.adl.validator.util.ValidatorKeyNames;
import org.adl.validator.util.ValidatorMessage;
import org.adl.validator.util.processor.URIHandler;

/**
 * This checker will verify only the files referenced in the xml are present
 * at the root of the package
 * 
 * @author ADL Technical Team
 *
 */
public class ExcessBaggageChecker extends PackageChecker
{
   /**
    * A string containing the full path to the package root
    */
   final private String mRootDirectory;
   
   /**
    * Default Constructor. Sets the attributes to their initial values.
    */
   public ExcessBaggageChecker()
   {
      //   default constructor
      super();
      mResult = new Result();
      mResult.setPackageCheckerName(ValidatorCheckerNames.EXCESS_BAG);
      
      mRootDirectory = 
         CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.ROOT_DIRECTORY_KEY).toString();      
      
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
         Messages.getString("ExcessBaggageChecker.0")));
         mResult.setPackageCheckerPassed(false);  
         return mResult;
      } 
      
      // If the test is for the manifest only skip this checker
      if ( CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.MANIFEST_ONLY_KEY) != null && 
            CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.MANIFEST_ONLY_KEY).toString().equals("true") )
      {
         mResult.setCheckerSkipped(true);
         return mResult;
      }
      
      //  Get files referenced in the manifest
      final Set<String> resourceFiles = getResourceFiles();
      
      final Set<String> processedResourceFiles = new HashSet<String>();
      
      final URIHandler uriHandler = new URIHandler();
      
      if ( resourceFiles == null) 
      {
         return mResult;
      }
            
      final Iterator<String> resourceIter = resourceFiles.iterator();
      String tempResource = "";
      
      while ( resourceIter.hasNext() )
      {
         tempResource = resourceIter.next().toString();
         tempResource = URIHandler.decode(tempResource, URIHandler.ENCODING);
         tempResource = uriHandler.escapeDirectories(tempResource);
         tempResource = tempResource.replaceAll("/", File.separator + File.separator);
         
         processedResourceFiles.add(tempResource);
      }      
      
      if ( processedResourceFiles == null) 
      {
         return mResult;
      }
      
      // Traverse directory and get package file list
      final Set<String> packageFiles = getPackageFiles(URIHandler.decode(mRootDirectory,URIHandler.ENCODING));
      
      // Set main result items
      mResult.setCheckerSkipped(false);
      mResult.setPackageCheckerPassed(true);
      
      // Remove all referenced files from set to leave excess baggage
      packageFiles.removeAll(processedResourceFiles);
      
      final Iterator<String> excessFileIter = packageFiles.iterator();
      final String rootDir = mRootDirectory.replaceAll(File.separator + File.separator, File.separator + File.separator + File.separator + File.separator);
      String fileName = "";
      while ( excessFileIter.hasNext() )
      {  
         fileName = excessFileIter.next().toString();
         fileName = fileName.replaceAll(rootDir, "");
         mResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.WARNING,
               Messages.getString("ExcessBaggageChecker.1", fileName)));
      }
      return mResult;      
   }
   
   /**
    * This method will call a SAX parser to obtain the files referened in the
    * manifest file.
    * 
    * @return a Set containing the files referenced in the manifest file
    */
   private Set<String> getResourceFiles()
   {
      Set<String> fileSet = new LinkedHashSet<String>();
      
      final ExcessBaggageSaxParser resourceParser = new ExcessBaggageSaxParser();
      final String manifestFile = 
         CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.ROOT_DIRECTORY_KEY).toString() +
         CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.XML_FILE_NAME_KEY).toString();
      
      // We have to stop if the checkerStateData access did not work correctly
      if ( manifestFile == null )
      {
         mResult.addOverallStatusMessage(new ValidatorMessage(ValidatorMessage.FAILED,
         Messages.getString("ExcessBaggageChecker.2")));
         mResult.setPackageCheckerPassed(false);
         return fileSet;
      }
      
      final boolean success = resourceParser.performParse(manifestFile);
         
      if ( success )
      {
         fileSet = resourceParser.getFileHrefs();
      }
      else
      {
         
         mResult.setPackageCheckerPassed(false);    
         final Iterator<ValidatorMessage> resultIter = resourceParser.getParseMessages().iterator();
         while ( resultIter.hasNext())
         {
            final ValidatorMessage vMessage = (ValidatorMessage)resultIter.next();
            mResult.addPackageCheckerMessage(vMessage);
         }
         return null;
      }
      
      // Format resource set to match package file set
      final Set<String> newFileSet = new LinkedHashSet<String>();      
      final Iterator<String> fileIter = fileSet.iterator();
      String newFile;     
      
      while ( fileIter.hasNext() )
      {
         newFile = "";            
         newFile = mRootDirectory + fileIter.next().toString();
         newFile = newFile.replaceAll("/", File.separator + File.separator);  
         newFileSet.add(newFile);
      }      
      return newFileSet;      
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
            
            if ( currentFile.isFile() && 
                 currentFile.getName().indexOf(".xsd") < 0 &&
                 currentFile.getName().indexOf(".dtd") < 0 &&
                 CheckerStateData.getInstance().
                 getObjectValue(ValidatorKeyNames.XML_FILE_NAME_KEY) != null &&
                 !currentFile.getName().equals(CheckerStateData.getInstance().
                       getObjectValue(ValidatorKeyNames.XML_FILE_NAME_KEY)))
            {               
               files.add(currentFile.getAbsolutePath());               
            }
            else // File is a directory
            {               
               files.addAll(getPackageFiles(currentFile.getAbsolutePath()));
            }            
         }
      }
      return files;
   }
}
