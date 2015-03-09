package org.adl.testsuite.util;

import java.applet.Applet;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * This class holds a listing of all applets running in the same classloader.
 * Additionally this class provides the ability to register an applet or get the
 * reference to an applet listed here.
 * 
 * @author ADL Technical Team
 */
public final class AppletList
{
   /**
    * Logger object used for debug logging
    */
   private static Logger mLogger = Logger.getLogger("org.adl.util.debug.testsuite");
   
   /**
    * List of applets registered
    */
   private static Hashtable mApplets = new Hashtable();   
   
   /**
    * Allows an applet to register itself to this registry.
    * @param iName - The name for which this applet will be referred
    * @param iApplet - The instance of the applet being registered
    */
   public static void register(String iName, Applet iApplet)
   {
      mLogger.finest("registering Applet " + iName);
      mApplets.put(iName, iApplet);
   }

   /**
    * Allows functionality to remove an applet from the registry using the name 
    * of the applet.
    * @param iName - The name of the applet.
    */
   public static void remove(String iName)
   {
      mLogger.finest("removing Applet " + iName);
      mApplets.remove(iName);
   }

   /**
    * Returns the Applet requested. If the Applet is not found, this will return
    * null.
    * @param iName - The name of the applet.
    * @return The instance of the Applet registered
    */
   public static Applet getApplet(String iName)
   {      
      return (Applet) mApplets.get(iName);
   }
   
   /**
    * Provides a way to find out if the requested applet is registered
    * @param iName - Name of the applet that is wanted 
    * @return - returns true if applet is found
    */
   public static boolean hasApplet(String iName)
   {
      return mApplets.containsKey(iName);
   }

   /**
    * Provides a way to get a list of applets in the registry
    * @return an Enumeration of the name/applet list of the registry
    */
   public static Enumeration getApplets()
   {
      return mApplets.elements();
   }

   /**
    * Returns the number of Applets in the registry
    * @return the size (number of applets) in registry
    */
   public static int size()
   {
      return mApplets.size();
   }
}