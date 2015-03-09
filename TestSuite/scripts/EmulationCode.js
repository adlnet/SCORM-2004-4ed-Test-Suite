/*************************************************************************
**
** Filename:  EmulationCode.js
**
** File Description:   This file provides emulation code to overload the IE specific
**                     procedures "insertAdjacentHTML()" and "scrollIntoView()"
**                     so that they can be used in Netscape 6.
**
**                     insertAdjacentHTML() procedure source code was based upon
**                     code found at:
**                     http://www.faqts.com/knowledge_base/view.phtml/aid/5756
**
**
** Browser Compatibility:
**
** Author: ADLI Project
**
** Module/Package Name:  none
** Module/Package Description: none
**
** Design Issues:
**
** Implementation Issues:
** Known Problems:
** Side Effects:
**
** References: ADL SCORM
**
***************************************************************************

 
**************************************************************************/
//Checks if the browser needs the emulation code
if (document.childNodes&&!document.childNodes[0].insertAdjacentHTML)
{
   HTMLElement.prototype.insertAdjacentElement = function(where,parsedNode)
   {
      switch (where)
      {
         case 'beforeBegin':
            this.parentNode.insertBefore(parsedNode,this);
            break;
         case 'afterBegin':
            this.insertBefore(parsedNode,this.firstChild);
           break;
         case 'BeforeEnd':
           this.appendChild(parsedNode);
           break;
         case 'afterEnd':
        if (this.nextSibling)
         {
           this.parentNode.insertBefore
           (parsedNode,this.nextSibling);
        }
         else
         {
           this.parentNode.appendChild(parsedNode);
        }
        break;
      }
   }

   HTMLElement.prototype.insertAdjacentHTML = function(where,htmlStr)
   {
      var r = this.ownerDocument.createRange();
      r.setStartBefore(this);
      var parsedHTML = r.createContextualFragment(htmlStr);
      this.insertAdjacentElement(where,parsedHTML);
   }


   HTMLElement.prototype.insertAdjacentText = function(where,txtStr)
   {
      var parsedText = document.createTextNode(txtStr);
      this.insertAdjacentElement(where,parsedText);
   }

   HTMLElement.prototype.scrollIntoView = function()
   {
      var coords = {x: 0, y: 0};
      var el = this;
      do
      {
         coords.x += el.offsetLeft;
         coords.y += el.offsetTop;
      }
      while ((el = el.offsetParent));

      window.scrollTo (coords.x, coords.y);
   }

}
