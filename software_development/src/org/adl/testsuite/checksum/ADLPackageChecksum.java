package org.adl.testsuite.checksum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.CRC32;

import org.adl.util.zip.UnZipHandler;

public class ADLPackageChecksum
{            
   /**
    * Given a file or folder, this method will generate the checksum for the file or folder
    * 
    * @param iPath - The path of the file
    * @return - The checksum value generated from the file or folder
    */
   public static long createChecksum(String iPath )
   { 
	   CRC32 crc = new CRC32();
	   byte[] tempBuf = new byte[128];
	   boolean zip = false;
      
      try
      {
         // Zip File
         if ( iPath.toLowerCase(Locale.ENGLISH).endsWith(".zip"))
         {
            String extractDir = System.getProperty("java.io.tmpdir") +
               "tempZipFolder" + File.separator + 
               "Checksum1" + File.separator;
            UnZipHandler handler = new UnZipHandler(iPath, extractDir);
            handler.extract();
            iPath = extractDir;
            zip = true;
         }
         final File pathFile = new File(iPath);
         List files = new ArrayList();

         // Folder
         if ( pathFile.isDirectory() )
         {
            files = getPackageFiles(pathFile);
         }
         // imsmanifest.xml
         else if (iPath.toLowerCase(Locale.ENGLISH).endsWith("imsmanifest.xml"))
         {
            files = getPackageFiles(pathFile.getParentFile());
         }
         // Single File
         else if ( pathFile.isFile() )
         {
            files.add(pathFile);
         }
         // Invalid entry
         else
         {
            return 0;
         }

         // Loop through the list of files
         for ( int i = 0; i < files.size(); i++ )
         {
            InputStream fis = new FileInputStream((File)files.get(i));
            int numRead;
            do
            {
               numRead = fis.read(tempBuf);
               if ( numRead > 0 )
               {
                  crc.update(tempBuf, 0, numRead);
               }
            }
            while ( numRead != -1 );
            fis.close();
         }
         if ( zip )
         {
            // Clear the temp Dir
            UnZipHandler.clearDir(new File(iPath));
         }
      }
      catch ( IOException ioe )
      {
         return crc.getValue();
      }
      return crc.getValue();
   }

   /**
    * This method will compare a checksum value and the checksum value calculated from a file
    * 
    * @param iPath - path of the file whose checksum will be calculated
    * @param iChecksum - the current checksum value to which the file will be compared
    * @return - The result of the checksum comparison
    */
   public static boolean compareChecksum(final String iPath, final long iChecksum )
   {
      try
      {        
         return iChecksum != 0 && iChecksum == createChecksum(iPath);
      }
      catch ( NumberFormatException nfe )
      {
         return false;
      }
   }
   
   /**
    * This method will create a list of all files present in the package
    * 
    * @param iPackageRoot is a String containing the path to the root of the package
    * @return a List containing the files the package contains
    */
   private static List getPackageFiles(final File iPackageRoot)
   {
      File currentFile;
      List files = new ArrayList();
      
      if (iPackageRoot.isDirectory()) 
      {         
         String[] directoryContents = iPackageRoot.list();
         
         for (int i=0; i < directoryContents.length; i++) 
         {        	
            currentFile = new File( iPackageRoot + File.separator + directoryContents[i] );
            
            if ( currentFile.isFile() ) 
            {               
               files.add(currentFile);               
            }
            else // File is a directory
            {               
               files.addAll(getPackageFiles(currentFile));
            }            
         }
      }
      return files;
   }
}
