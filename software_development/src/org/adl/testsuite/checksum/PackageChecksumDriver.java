package org.adl.testsuite.checksum;

import java.applet.Applet;
import java.security.AccessController;
import java.security.PrivilegedAction;

import netscape.javascript.JSObject;

import org.adl.util.Messages;
import org.adl.testsuite.util.AppletList;
//import org.adl.testsuite.checksum.ADLPackageChecksum;

/**
 * <strong>Filename:</strong><br>
 * PackageChecksumDriver.java<br><br>
 *
 * <strong>Description:</strong><br>
 * The <code>PackageChecksumDriver</code> is the communication handler between
 * the Javascript code of the HTML and the actual content package test.<br><br>
 *
 * @author ADL Technical Team
 */

public class PackageChecksumDriver extends Applet
{
   /**
    * true if the browser is Netscape, false otherwise
    */
   protected boolean mIsBrowserNetscape = false;
   
   /**
    * indicates whether or not the checksum comparison was a match
    */
   protected boolean mValidLog = false;
   
   /**
    * The length of the 'File:' prefix
    */
   private static final int PREFIX_LENGTH = 5;
   
   /**
    * overwritten init() function
    * 
    * used to register this applet in an applet registry to allow the software
    * to access it at a later time
    */
   public void init()
   {
      AppletList.register("PackageChecksumDriver",this);
   }
   
   /**
    * Overwritten start() function used to activate the continue button is step 
    * one after the log applet loads
    */
   public void start()
   {
      JSObject mJsroot = JSObject.getWindow(this);
      mJsroot.eval("loadComplete()");
   }
   
   /**
    * information about the applet. is used when "getAppletInfo()" is called
    * @return String of the applet info - Title: + Author: + Name: + Description
    */
   public String getAppletInfo()
   {
      return "Title: PackageChecksumDriver \n" +
             "Author: ADLI Project, CTC \n" +
             "Name: PackageChecksumDriver \n" +
             "The PackageChecksumDriver is for " +
             "connecting checksum evaluation tools";
   }

   /**
    * Returns the result of the checksum comparison
    * 
    * @return A boolean representing the result of the checksum comparison
    */
   public boolean isValidLog()
   {
      return mValidLog;
   }
   
   /**
    * This method will perform checksum validation
    * 
    * @param iPackageFileName - the path of the content package
    * @param iChecksumValue - the path of the log file containing 
    * the checksum or the value of the checksum itself
    */
   public void startChecksumValidation( String iPackageFileName,
                                  String iChecksumValue)
   {
      try
      {
         PrivilegedCPTest ps;
         
         // It is a log file
         if ( iChecksumValue.indexOf("File:") != -1 )
         {
            ps = new PrivilegedCPTest(Messages.unNull(iPackageFileName),
                  Messages.unNull(iChecksumValue.substring(PREFIX_LENGTH, iChecksumValue.length())),
                  mIsBrowserNetscape);
         }
         // It is a checksum value
         else
         {
            ps = new PrivilegedCPTest(Messages.unNull(iPackageFileName),
                  Long.parseLong(iChecksumValue),
                  mIsBrowserNetscape);
         }
         AccessController.doPrivileged(ps);
      }
      catch ( NumberFormatException fne )
      {
         mValidLog = false;
      }
   }
   
   /**
    * allows the JavaScript that launches this Applet to set whether or not the
    * browser is Netscape
    * 
    * @param isnetscape true if the browser is Netscape, false otherwise
    */
   public void setIsBrowserNetscape(boolean isnetscape)
   {
      mIsBrowserNetscape = isnetscape;      
   }
   
   /**
   *
   * Class PrivilegedCPTest
   *
   * Implements:  PrivilegedAction
   *
   * Description:
   *
   *     This inner class is used to grant permission to the code in
   *     this applet to allow read/write to files on the local disk.
   *
   */
  private class PrivilegedCPTest implements PrivilegedAction
  {
     private String mPackageFileName;
     private String mLogFileName;
     private long mChecksumValue = 0;
   
     /**
      * Constructor of the inner class
      *
      * @param iPackageFileName - Path of package to processed
      *
      * @param iLogFileName - The location of the log file containing a possible checksum value 
      * 
      * @param iNetscape - true if browser is Netscape, false otherwise
      *
      */
     PrivilegedCPTest( String iPackageFileName,
                       String iLogFileName,
                       boolean iNetscape)
     {
        mPackageFileName = iPackageFileName;
        mLogFileName = iLogFileName;
        mIsBrowserNetscape = iNetscape;
     }
     
     /**
      * Constructor of the inner class
      *
      * @param iPackageFileName - Path of package to processed
      *
      * @param iChecksumValue - The checksum value given by the user 
      * 
      * @param iNetscape - true if browser is Netscape, false otherwise
      *
      */
     PrivilegedCPTest( String iPackageFileName,
           long iChecksumValue,
           boolean iNetscape)
     {
        mPackageFileName = iPackageFileName;
        mChecksumValue = iChecksumValue;
        mIsBrowserNetscape = iNetscape;
     }

     /**
      *
      * This run method grants privileged applet code access to read/write
      * to the local disk.  This allows the applet to work in Netscape 6.
      *
      * @return Object
      *
      */
     public Object run()
     {
        long logChecksum = mChecksumValue;
        
        if ( logChecksum <= 0 )
        {           
           logChecksum = ADLChecksumLogReader.getChecksumValue(mLogFileName);
        }
        
        mValidLog = ADLPackageChecksum.compareChecksum(mPackageFileName, logChecksum);
        
        // Reset checksum
        mChecksumValue = 0;
        
        return null;
     }
  }
}