package org.adl.validator.util.processor;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipException;

import org.adl.validator.util.Messages;
import org.adl.validator.util.Result;
import org.adl.validator.util.ValidatorMessage;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

/**
 * Provides the ability to extract the contents of a zipped file to a given 
 * directory.
 * 
 * @author ADL Technical Team
 *
 */
public class UnZipHandler
{
   /**
    * Buffer size for use in extracting Zip File
    */
   public static final int BUFFER_SIZE = 16384;
   
   /**
    * A reference to the Zip File to be extracted
    */
   private File mZipFile;

   /**
    * The directory to which the Zip File will be extracted
    */
   private String mExtractToDir;
   
   /**
    * The name of the zip used to make a unique directory name.
    */
   private String mZipName;
   
   /**
    * List of messages thrown by exceptions.
    */
   private List<ValidatorMessage> mExceptionMsg = new ArrayList<ValidatorMessage>();
   
   /**
    * Constructor
    */
   public UnZipHandler()
   {
      //Default constructor
   }
   /**
    * Overload Constructor
    * 
    * @param iZipFile - Name and path of the <code>.zip</code> file.
    */
   public UnZipHandler(String iZipFile)
   {
      setFile( iZipFile );
   }
   
   /**
    * Set the name and location of the Zip File to be extracted.
    *
    * @param iFileName Name and location of the Zip File to be extracted.
    */
   private void setFile(String iFileName)
   {
      String fileName = iFileName;
      String tempName;
      try
      {
         String name1[] = fileName.split(File.separator + File.separator);
         tempName = name1[name1.length - 1];
         mZipName = tempName.toLowerCase().replaceAll(".zip", "");
         mZipFile = new File( iFileName ); 
      }
      catch ( NullPointerException npe )
      {
         npe.printStackTrace();
      }
   }
   
   /**
    * Set the target directory of the extracted contents of the Zip File.
    *
    */
   private void setTargetDirectory()
   {
      try
      {
         mExtractToDir = System.getProperty("java.io.tmpdir") +
                         "tempZipFolder" + File.separator + 
                         "Course1" + File.separator;
      }
      catch ( NullPointerException npe )
      {
         npe.printStackTrace();
      }
   }
   
   /**
    * Extracts the Zip File into the destination directory
    * 
    * @return Result object indicating if the zip was extracted to the temp 
    * directory.
    */
   public Result extractZipFile()
   {
      String fileName = "";
      String destFileName = "";
      String exceptionMessage = "";
      InputStream in = null;
      OutputStream out = null;      

      // Create a byte buffer
      byte[] buffer = new byte[BUFFER_SIZE];

      setTargetDirectory();
      
      try
      {  
       ZipFile archive;
       
       // protect against the case that the user only installs the JRE
       // with no language support
       try
       {
          archive = new ZipFile( mZipFile, "CP437");
       }
       catch ( ZipException ze )
       {
          archive = new ZipFile( mZipFile );
       }
       
         for ( Enumeration e = archive.getEntries(); e.hasMoreElements(); )
         {
            // Get the next entry in the Zip File
            ZipEntry entry = (ZipEntry)e.nextElement();

            if ( !entry.isDirectory() )
            {
               fileName = entry.getName();
               fileName = fileName.replace('/', File.separatorChar);

               destFileName = mExtractToDir + fileName;

               File destFile = new File(destFileName);

               // Create the destination path, if needed
               String parent = destFile.getParent();
               if ( parent != null )
               {
                  File parentFile = new File(parent);
                  if ( !parentFile.exists() )
                  {
                     // Create the chain of sub-directories to the file
                     parentFile.mkdirs();
                  }
               }

               // Get a stream of the archive entry's bytes
               in = archive.getInputStream(entry);

               // Open a stream to the destination file
               out = new FileOutputStream(destFileName);

               // Repeat reading into buffer and writing buffer to file,
               // until done.  Count will always be # bytes read, until
               // EOF when it is -1.
               int count;
               while ( (count = in.read(buffer)) != -1 )
               {
                  out.write(buffer, 0, count );
               }
               
               // Close the input stream and output stream
               in.close();
               out.close();
            }
         }
         archive.close();
      }
      catch ( ZipException ze )
      {
         exceptionMessage = getExceptionMessage(ze.getMessage());
         mExceptionMsg.add(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("UnZipHandler.1", exceptionMessage )));
      }
      catch ( NullPointerException npe )
      {
         exceptionMessage = getExceptionMessage(npe.getMessage());
         mExceptionMsg.add(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("UnZipHandler.2", exceptionMessage )));
      }
      catch ( EOFException eof)
      {
         exceptionMessage = getExceptionMessage(eof.getMessage());
         mExceptionMsg.add(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("UnZipHandler.3", exceptionMessage )));
      }
      catch ( IOException ioe )
      {
         exceptionMessage = getExceptionMessage(ioe.getMessage());
         mExceptionMsg.add(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("UnZipHandler.4", exceptionMessage )));
      }
      catch ( SecurityException se )
      {
         exceptionMessage = getExceptionMessage(se.getMessage());
         mExceptionMsg.add(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("UnZipHandler.5", exceptionMessage )));
      }      
      finally
      {
         // In case an exception is thrown prior to closing the input stream
         // and output stream, close the streams
         
         // Check to make sure the input stream has not been closed
         if (in != null)
         {
            try
            { 
               in.close();
            }
            catch (IOException e)
            {
               //e.printStackTrace();
               exceptionMessage = getExceptionMessage(e.getMessage());
               mExceptionMsg.add(new ValidatorMessage(ValidatorMessage.FAILED,
                     Messages.getString("UnZipHandler.4", exceptionMessage )));
            }
         }

         // Check to make sure the output stream has not been closed
         if (out != null)
         {
            try
            {
               out.close();
            }
            catch (IOException e)
            {
               //e.printStackTrace();
               exceptionMessage = getExceptionMessage(e.getMessage());
               mExceptionMsg.add(new ValidatorMessage(ValidatorMessage.FAILED,
                     Messages.getString("UnZipHandler.4", exceptionMessage )));
            }
         }        
      } // end finally
      
      return setExceptionMsg(mExceptionMsg);
   }

   /**
    * Returns the target directory of the extracted contents of the Zip 
    * File.
    *
    * @return target The target directory of the extracted contents of the Zip 
    *                File.
    */
   public String getTargetDirectory()
   {
      return mExtractToDir;
   }
   
   /**
    * Clears the temp directory that the zip file was extracted to.
    * 
    * @param iTempDir - Location of the extracted zip.
    */
   public void clearTempDir(File iTempDir)
   {
      if (iTempDir.exists())
      {
         File[] allFiles = iTempDir.listFiles();
         for ( int i = 0; i < allFiles.length; i++ )
         {
            if ( allFiles[i].isDirectory())
            {
               clearTempDir(allFiles[i]);
            }
            else
            {
               allFiles[i].delete();
            }
         }
      }
    iTempDir.delete();  
   }
   
   /**
    * Adds the Exception messages from a list to a Result object for logging 
    * purposes.
    * 
    * @param iExceptionMsg - List of messages related to different exceptions 
    * thrown in the extractZipFile method.
    * @return Result object indicating if the zip was extracted to the temp 
    * directory.
    */
   protected Result setExceptionMsg(List iExceptionMsg)
   {
      Result mResult = null;
      if ( iExceptionMsg.size() != 0 )
      {
         mResult = new Result();
      }
      for ( int i = 0; i < iExceptionMsg.size(); i++)
      {
         mResult.addPackageCheckerMessage((ValidatorMessage)iExceptionMsg.get(i));
      }
      return mResult;
   }
   
   /**
    * Sets a generic exception message if the detail message string for the 
    * Throwable instance is null.
    * 
    * @param iMessage - detail message string of the throwable instance.
    * @return String - Message corrected if null and as is if not null.
    */
   private String getExceptionMessage(String iMessage)
   {
      if ( iMessage == null )
      {
         return Messages.getString("UnZipHandler.0");
      }
      else
      {
         return iMessage;
      }
   }
}
