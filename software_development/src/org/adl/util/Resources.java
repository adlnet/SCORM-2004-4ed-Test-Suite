package org.adl.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Resources
{
   public static <T> Resources getResources(final Class<T> forClass)
   {
      return getResources(getResourceName(forClass));
   }

   private static <T> String getResourceName(final Class<T> forClass)
   {
      return forClass.getPackage().getName() + ".resources."
            + forClass.getSimpleName();
   }

   private static Resources getResources(final String name)
   {
      return new Resources(name);
   }

   private final transient ResourceBundle bundle;

   private Resources(final String bundleName)
   {
      this.bundle = ResourceBundle.getBundle(bundleName);
   }

   public String getString(final String iKey, final Object... args)
   {
      for (Object o : args)
      {
         if (o == null)
         {
            return "";
         }
      }

      try
      {
         final String preFormat = bundle.getString(iKey);
         return String.format(preFormat, args);
      }
      catch (MissingResourceException e)
      {
         return iKey;
      }
   }
}
