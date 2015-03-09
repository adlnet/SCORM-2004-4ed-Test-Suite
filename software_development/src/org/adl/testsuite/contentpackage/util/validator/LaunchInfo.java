package org.adl.testsuite.contentpackage.util.validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.adl.util.decode.decodeHandler;

/**
 * This class is responsible for holding information on each resource and its launch line
 *  
 * @author ADL Technical Team
 */
public class LaunchInfo
{   
   /**
    * Instance of the LaunchInfo.
    */
   private static LaunchInfo instance;
   
   /**
    * A List of SCOs to be possibly launched
    */
   private final List mSCOList = new ArrayList();
   
    /**
    * Constructor
    */
   private LaunchInfo()
   {
      //default Constructor
   }
   
   /**
    * Returns a protected instance of this class.
    * 
    * @return An instance of LaunchInfo.
    */
   public static LaunchInfo getInstance()
   {
      if(instance == null)
      {
         instance = new LaunchInfo();
      }
      return instance;
   }
   
   /**
    * Returns the SCO List
    * 
    * @return A List of the SCOs to be launched
    */
   public final List getSCOList()
   {
      return mSCOList;
   }
   
   /**
    * This method will add a new SCO to the SCO List
    * 
    * @param iResourceID - The identifier of the resource containing the SCO
    * @param iLaunchLine - The launch line of the SCO
    * @return - A boolean indicating the sucess of the insertion operation
    */
   public final boolean addSCO(String iResourceID, String iLaunchLine )
   {
      return mSCOList.add( new LaunchItem(iResourceID, iLaunchLine.split("[.]")[0]) );
   }
   
   /**
    * This method will attempt to find a resource identifier given its matching launch line
    * 
    * @param iLaunchLine - A String representing the launch line of the SCO
    * @return - A String containing the launch line's corresponding identifier
    */
   public final String getResourceID( String iLaunchLine )
   {
      Iterator listIter = mSCOList.iterator();
      
      while ( listIter.hasNext() )
      {
         LaunchItem tempItem = (LaunchItem)listIter.next();
         decodeHandler handler = new decodeHandler(tempItem.getLocation(), "UTF-16");
         handler.decodeName();
         if ( handler.getDecodedFileName().equals(iLaunchLine) )
         {
            return tempItem.getResourceID();
         }
      }
      return "";
   }
   
   /**
    * This method will get a launch line given a resource identifier
    * 
    * @param iResourceID - A String representing the resource identifier
    * @return - A String containing the identifier's matching launch line
    */
   public final String getLaunchLine( String iResourceID )
   {
      Iterator listIter = mSCOList.iterator();
      while ( listIter.hasNext() )
      {
         LaunchItem tempLine = (LaunchItem)listIter.next();
         if ( tempLine.getResourceID().equals(iResourceID) )
         {
            return tempLine.getLocation();
         }
      }
      return "";
   }
   
   /**
    * Clears the LaunchInfo object
    */
   public final void clearCollection()
   {
      instance = null;
   }
   
   /**
    * This is a subclass created to hold the ID and location of each resource element
    *
    * @author ADL Technical Team
    *
    */
   public class LaunchItem
   {
      /**
       * A String containing the identifier of the given resource
       */
      private String mResourceID = "";
            
      /**
       * A String containing the href of each resource which will be used to launch the SCO 
       */
      private String mLocation = "";
      
      /**
       * The base constructor
       * 
       * @param iResourceID - Identifer of the given resource
       * @param iLocation - Href value of the given resource
       */
      LaunchItem( String iResourceID, String iLocation )
      {
         mResourceID = iResourceID;
         mLocation = iLocation;
      }
      
      /**
       * This method returns the identifier of the given resource
       * 
       * @return The identifier value
       */
      public String getResourceID()
      {
         return mResourceID;
      }
      
      /**
       * This method returns the href attribute of the given resource
       * 
       * @return The value of the href value
       */
      public String getLocation()
      {
         return mLocation;
      }
   }
}