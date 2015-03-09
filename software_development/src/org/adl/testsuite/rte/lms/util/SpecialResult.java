package org.adl.testsuite.rte.lms.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ADL Technical Team
 *
 */
public class SpecialResult extends Result
{
   private List mObjs;
   
   public SpecialResult()
   {
      mObjs = new ArrayList();
   }
   
   public int getCount()
   {
      return mObjs.size();
   }
   
   public void addObj(String obj)
   {
      mObjs.add(obj);
   }
   
   public String getObj(int i)
   {
      return (String)mObjs.get(i);
   }
   
   public boolean contains(String obj)
   {
      return mObjs.contains(obj);
   }
}
