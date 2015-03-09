<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output encoding="UTF-16" method="html"/>

    <xsl:template match="/">
        <html>
            <body>
                <xsl:apply-templates/>
            </body>
        </html>
    </xsl:template>

    <!-- *******************************************************NEW TEMPLATE************************************************************ -->
    <xsl:template match="logmessages">
         <span style="color:#890C08; font-family:Verdana; font-weight:bold;font-size:12px">
            Advanced Distributed Learning (ADL)<br/>
         </span>
    
         <span style="color:#363C54; font-family:Verdana;font-weight:bold;font-size:12px">
            Sharable Content Object Reference Model (SCORM <sup style="font-size:0.8em">&#174;</sup>) 2004 4<sup style="font-size:0.8em">th</sup> Edition<br/>
            Test Suite Version 1.1.1<br/>
         </span>
        <br/>
    
        <xsl:apply-templates select="header"/>
    
        <table width="800" cellspacing="0" >
            <br/>
            <tr>
                <th BGCOLOR="#363C54"></th>
    
                <th BGCOLOR="#363C54">
                    <span style="color:white">
                        MESSAGE
                    </span>
                </th>
            </tr>
    
            <tr height="25">
                <td></td>
                <td></td>
            </tr>
    
            <xsl:apply-templates select="message | other | link"/>
        </table>
    </xsl:template>
    
    
    <!-- *******************************************************NEW TEMPLATE************************************************************ -->
    <xsl:template match="title">
        <xsl:if test='@type="title"'></xsl:if>
    </xsl:template>
    
    
    <!-- *******************************************************NEW TEMPLATE************************************************************ 
    "header" covers the following types:
        info
        warn
        head
    -->
    <xsl:template match="header">
        <xsl:if test='@type="info"'>
            <span style="color:green; font-family:Verdana;font-weight: bold;font-size:12px">
                <xsl:value-of disable-output-escaping="yes" select="."/>
            </span>
            <br/>
        </xsl:if>
        
        <xsl:if test='@type="warn"'>
            <span style="color:orange; font-family:Verdana;font-weight: bold;font-size:12px">
                <xsl:value-of disable-output-escaping="yes" select="."/>
            </span>
            <br/>
        </xsl:if>
        
        <xsl:if test='@type="head"'>
            <span style="color:#363C54; font-family:Verdana;font-weight: bold;font-size:12px">
                <xsl:value-of disable-output-escaping="yes" select="."/>
            </span>
            <br/>
        </xsl:if>
    </xsl:template>
    
    <!-- *******************************************************NEW TEMPLATE************************************************************ 
    "message" covers the following message types:
        info
        pass
        other
        fail
        warn
        term
        conf
        stop
        
    as well as format for:
        not($conf)
    -->
    <xsl:template match="message  ">
        <xsl:variable name="test">
            <xsl:value-of select="."/>
        </xsl:variable>
    
        <xsl:variable name="conf" select='$test="prop_SCORM Conformance Category:"'/>
    
        <xsl:variable name="var" select="concat ('file:///', //envVar)"/>
    
        <xsl:if test='@type="info"'>
            <xsl:if test="$conf">
                <tr height="25">
                    <td></td>
                    <td></td>
                </tr>
    
                <tr>
                    <th BGCOLOR="#363C54"></th>
                    <th BGCOLOR="#363C54">
                        <span style="width: 800; background: #363C54; color: white; text-align:center;font-family:Verdana;font-weight:bold;font-size:12px">
                            CONFORMANCE
                        </span>
                    </th>
                </tr>
    
                <tr height="25">
                    <td></td>
                    <td></td>
                </tr>
    
                <tr>
                    <td>
                        <img>
                            <xsl:attribute name="src">util/smallinfo.gif</xsl:attribute>
                        </img>
                    </td>
    
                    <td>
                        <span style="color:blue;font-family:Verdana;font-weight:bold;font-size:12px">
                            <xsl:value-of select="."/>
                        </span>
                    </td>
                </tr>
            </xsl:if>
    
            <xsl:if test="not($conf)">
                <tr>
                    <td>
                        <img>
                            <xsl:attribute name="src">util/smallinfo.gif</xsl:attribute>
                        </img>
                    </td>
    
                    <td>
                        <span style="color:blue;font-family:Verdana;font-weight:bold;font-size:12px">
                            <xsl:value-of select="."/>
                        </span>
                    </td>
                </tr>
            </xsl:if>
        </xsl:if>
        
        <xsl:if test='@type="pass"'>
            <tr>
                <td>
                    <img>
                        <xsl:attribute name="src">util/smallcheck.gif</xsl:attribute>
                    </img>
                </td>
    
                <td>
                    <span style="color:green;font-family:Verdana;font-weight:bold;font-size:12px">
                        <xsl:value-of select="."/>
                    </span>
                </td>
            </tr>
        </xsl:if>
    
        <xsl:if test='@type="other"'>
            <tr>
                <td></td>
                <td>
                    <b>
                        <span style="color:black;font-family:Verdana;font-weight:bold;font-size:12px">
                            <xsl:value-of disable-output-escaping="yes" select="."/>
                        </span>
                    </b>
                </td>
            </tr>
        </xsl:if>
        
        <xsl:if test='@type="fail"'>
            <tr>
                <td>
                    <img>
                        <xsl:attribute name="src">util/smallxuser.gif</xsl:attribute>
                    </img>
                </td>
    
                <td>
                    <b>
                        <span style="color:red;font-family:Verdana;font-weight:bold;font-size:12px">
                            ERROR:
                            <xsl:value-of select="."/>
                        </span>
                    </b>
                </td>
            </tr>
        </xsl:if>
        
        <xsl:if test='@type="warn"'>
            <tr>
                <td>
                    <img>
                        <xsl:attribute name="src">util/smallwarning.gif</xsl:attribute>
                    </img>
                </td>
    
                <td>
                    <b>
                        <span style="color:orange;font-family:Verdana;font-weight:bold;font-size:12px">
                            WARNING:
                            <xsl:value-of select="."/>
                        </span>
                    </b>
                </td>
            </tr>
        </xsl:if>
        
        <xsl:if test='@type="term"'>
            <tr>
                <td>
                    <img>
                        <xsl:attribute name="src">util/smallstop.gif</xsl:attribute>
                    </img>
                </td>
    
                <td>
                    <b>
                        <span style="color:red;font-family:Verdana;font-weight:bold;font-size:12px">
                            ERROR:
                            <xsl:value-of select="."/>
                        </span>
                    </b>
                </td>
            </tr>
        </xsl:if>
        
        <xsl:if test='@type="stop"'>
            <tr>
                <td>
                    <img>
                        <xsl:attribute name="src">util/smallstop.gif</xsl:attribute>
                    </img>
                </td>
    
                <td>
                    <b>
                        <span style="color:red;font-family:Verdana;font-weight:bold;font-size:12px">
                            <xsl:value-of select="."/>
                        </span>
                    </b>
                </td>
            </tr>
        </xsl:if>
        
        <xsl:if test='@type="conf"'>
            <tr>
                <td>
                    <img>
                        <xsl:attribute name="src">util/adl_tm_small.jpg</xsl:attribute>
                    </img>
                </td>
    
                <td>
                    <b>
                        <span style="color:purple;font-family:Verdana;font-weight:bold;font-size:12px;">							
							<xsl:value-of disable-output-escaping="yes" select="."/>						
                        </span>
                    </b>
                </td>
            </tr>
        </xsl:if>        
</xsl:template>
    <!-- *******************************************************NEW TEMPLATE************************************************************ 
    "link" covers the following hrefs:
        MD
        SCO
        CP
     -->
    <xsl:template match="link">
        <tr>
            <td/>
            <td>
                <a>
                    <xsl:attribute name="href">
                        ./<xsl:value-of select="."/>
                    </xsl:attribute>
    
                    <xsl:if test='@type="MD"'>
                        Click here to view detailed MD test log for
                        <xsl:value-of select="@name"/>
                    </xsl:if>
    
                    <xsl:if test='@type="SCO"'>
                        Click here to view detailed SCO test log for
                        <xsl:value-of select="@name"/>
                    </xsl:if>
    
                    <xsl:if test='@type="CP"'>
                        Click here to view detailed CP test log for
                        <xsl:value-of select="@name"/>
                    </xsl:if>
                </a>
            </td>
        </tr>
    </xsl:template>

    <!-- *******************************************************NEW TEMPLATE************************************************************ 
    "other" covers the following:
        TH
            MD
            SCO
            HR
    -->
    <xsl:template match="other">
        <xsl:variable name="test">
            <xsl:value-of disable-output-escaping="yes" select="."/>
        </xsl:variable>
    
        <xsl:if test='@type="TH"'>
            <xsl:if test='$test="MD"'>
                <tr height="25">
                    <td></td>
                    <td></td>
                </tr>
    
                <tr>
                    <th BGCOLOR="#363C54"></th>
                    <th BGCOLOR="#363C54">
                        <span style="width: 800; background: #363C54; color: white; text-align:center;font-family:Verdana;font-weight:bold;font-size:12px">
                            DETAILED LOG RESULTS FOR METADATA INSTANCES FOUND IN THE CONTENT PACKAGE
                        </span>
                    </th>
                </tr>
    
                <tr height="25">
                    <td></td>
                    <td></td>
                </tr>
            </xsl:if>
    
            <xsl:if test='$test="SCO"'>
                <tr height="25">
                    <td></td>
                    <td></td>
                </tr>
    
                <tr>
                    <th BGCOLOR="#363C54"></th>
                    <th BGCOLOR="#363C54">
                        <span style="width: 800; background: #363C54; color: white; text-align:center;font-family:Verdana;font-weight:bold;font-size:12px">
                            DETAILED LOG RESULTS FOR SCOs FOUND IN CONTENT PACKAGE
                        </span>
                    </th>
                </tr>
    
                <tr height="25">
                    <td></td>
                    <td></td>
                </tr>
            </xsl:if>
        </xsl:if>
    
        <xsl:if test='@type="HR"'>
            <tr>
                <td></td>
                <td>
                    <hr />
                </td>
            </tr>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
