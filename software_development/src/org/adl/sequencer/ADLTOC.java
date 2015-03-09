package org.adl.sequencer;

import java.io.Serializable;

import org.adl.util.debug.DebugIndicator;

/**
 * Encapsulation of information required to display a valid table of contents
 * (TOC) for an activity tree.<br><br>
 * 
 * <strong>Filename:</strong> ADLTOC.java<br><br>
 *
 * <strong>Description:</strong><br>
 * The <code>ADLTOC</code> encapsulates the information required by the
 * SCORM 2004 4th Edition Sample RTE delivery system to display a valid
 * table of contents (TOC) for an activity tree<br><br>
 * 
 * <strong>Design Issues:</strong><br>
 * This implementation is intended to be used by the 
 * SCORM 2004 4th Edition Sample RTE. <br>
 * <br>
 * 
 * <strong>Implementation Issues:</strong><br><br>
 * All fields are purposefully public to allow immediate access to known data
 * elements.<br><br>
 * 
 * <strong>Known Problems:</strong><br><br>
 * 
 * <strong>Side Effects:</strong><br><br>
 * 
 * <strong>References:</strong><br>
 * <ul>
 *     <li>IMS SS 1.0
 *     <li>SCORM 2004 4th Edition
 * </ul>
 * 
 * @author ADL Technical Team
 */
public class ADLTOC implements Serializable
{
   /**
    * This controls display of log messages to the java console
    */
   private static boolean _Debug = DebugIndicator.ON;

   /**
    * The title of this entry in the TOC.
    */
   public String mTitle = "";

   /**
    * The depth of this entry in the TOC.
    */
   public int mDepth = -1;

   /**
    * The relative position of this entry in the TOC.
    */
   public int mCount = -1;

   /** 
    * Identifies if the activity is a leaf
    */
   public boolean mLeaf = false;

   /**
    * Identifies the parent of this activity in the TOC
    */
   public int mParent = -1;

   /**
    * Identifies if the parent of this activity has choice = true
    */
   public boolean mInChoice = false;

   /**
    * Indicates if the activity is enabled.
    */
   public boolean mIsEnabled = true;

   /**
    * Indicates if the activity is visible.
    */
   public boolean mIsVisible = true;

   /**
    * Indicates if the activity is the current activity.
    */
   public boolean mIsCurrent = false;

   /**
    * Indicates if the activity is a valid target of choice
    */
   public boolean mIsSelectable = true;

   /**
    * The activity ID of this entry in the TOC.
    */
   public String mID = null;


   /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
   
    Public Methods
   
   -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

   /**
    * This method provides the state this <code>ADLTOC</code> object for
    * diagnostic purposes.
    */
   public void dumpState()
   {
      if ( _Debug )
      {
         System.out.println("  :: ADLTOC       --> BEGIN - dumpState");

         System.out.println("  ::--> Title:       " + mTitle);
         System.out.println("  ::--> Depth:       " + mDepth);
         System.out.println("  ::--> Count:       " + mCount);
         System.out.println("  ::--> Activity ID: " + mID);
         System.out.println("  ::--> Leaf?        " + mLeaf);
         System.out.println("  ::--> In Choice?   " + mInChoice);
         System.out.println("  ::--> Parent:      " + mParent);
         System.out.println("  ::--> Enabled:     " + mIsEnabled);
         System.out.println("  ::--> Visible:     " + mIsVisible);
         System.out.println("  ::--> Current:     " + mIsCurrent);
         System.out.println("  ::--> Selectable:  " + mIsSelectable);

         System.out.println("  :: ADLTOC       --> END   - dumpState");
      }
   }

}  // end ADLTOC
