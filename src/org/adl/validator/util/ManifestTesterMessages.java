package org.adl.validator.util;

import org.adl.util.Resources;

public class ManifestTesterMessages
{

   public static String getString(String key, Object... args)
   {
      return Resources.getResources(ManifestTesterMessages.class).getString(
            key, args);
   }
}
