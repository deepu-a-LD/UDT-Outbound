<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:volvo="http://www.volvo.com/group/common/1_0" xmlns:md="http://www.cdb.volvo.com/masterdata/4_0" version="1.0" xmlns:date="java:java.util.Date" exclude-result-prefixes="date" extension-element-prefixes="date">

    <xsl:template name="millisecs-to-ISO">
        <xsl:param name="millisecs"/>
        <xsl:param name="JDN" select="floor($millisecs div 86400000) + 2440588"/>
        <xsl:param name="mSec" select="$millisecs mod 86400000"/>

        <xsl:param name="f" select="$JDN + 1401 + floor((floor((4 * $JDN + 274277) div 146097) * 3) div 4) - 38"/>
        <xsl:param name="e" select="4*$f + 3"/>
        <xsl:param name="g" select="floor(($e mod 1461) div 4)"/>
        <xsl:param name="h" select="5*$g + 2"/>

        <xsl:param name="d" select="floor(($h mod 153) div 5 ) + 1"/>
        <xsl:param name="m" select="(floor($h div 153) + 2) mod 12 + 1"/>
        <xsl:param name="y" select="floor($e div 1461) - 4716 + floor((14 - $m) div 12)"/>

        <xsl:param name="H" select="floor($mSec div 3600000)"/>
        <xsl:param name="M" select="floor($mSec mod 3600000 div 60000)"/>
        <xsl:param name="S" select="$mSec mod 60000 div 1000"/>

        <xsl:value-of select="concat($y, format-number($m, '-00'), format-number($d, '-00'))" />
        <xsl:value-of select="concat(format-number($H, 'T00'), format-number($M, ':00'), format-number($S, ':00'))" />
    </xsl:template>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="root">
        <md:GetAccount xmlns:volvo="http://www.volvo.com/group/common/1_0" xmlns:md="http://www.cdb.volvo.com/masterdata/4_0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" serviceID="String" serviceVersion="String">

            <volvo:ApplicationArea>

                <volvo:Sender>
                    <volvo:LogicalID>
                        <xsl:value-of select="_meta/updatedBy"/>
                    </volvo:LogicalID>
                </volvo:Sender>
                <volvo:CreationDateTime>
                    <xsl:call-template name="millisecs-to-ISO">
                        <xsl:with-param name="millisecs" select="_meta/creationDate" />
                    </xsl:call-template>
                </volvo:CreationDateTime>

                <volvo:BODID>CF017964-8BA7-41B0-BC8C-6C8B73251CF8</volvo:BODID>
            </volvo:ApplicationArea>



        <md:DataArea>
            <md:Sync>
                <volvo:ActionCriteria>SyncAccount</volvo:ActionCriteria>
            </md:Sync>

            <md:Accounts>
                <md:Account>

                    <md:PartyID><xsl:value-of select="X_UDT_partyID"/></md:PartyID>

                    <md:PublicInformation>
                        <md:LegalName><xsl:value-of select="X_UDT_name"/></md:LegalName>
                        <md:CommonName><xsl:value-of select="X_UDT_name"/></md:CommonName>
                        <md:GlobalID>
                            <xsl:for-each select="X_UDT_Identifier">
                                <xsl:if test="X_UDT_identifierType/Code='VAT'">
                                    <md:VATNumber><xsl:value-of select="X_UDT_identifierValue"/></md:VATNumber>
                                </xsl:if>
                                <xsl:if test="X_UDT_identifierType/Code='COC'">
                                    <md:ChamberOfCommerce><xsl:value-of select="X_UDT_identifierValue"/></md:ChamberOfCommerce>
                                </xsl:if>
                                <xsl:if test="X_UDT_identifierType/Code='ON'">
                                    <md:OrganisationNumber><xsl:value-of select="X_UDT_identifierValue"/></md:OrganisationNumber>
                                </xsl:if>
                            </xsl:for-each>
                        </md:GlobalID>


                        <md:Location>
                            <xsl:for-each select="X_UDT_Address">
                                <xsl:if test="addressType/Name =  'Main'">
                                    <md:MainAddress>
                                        <md:Line1><xsl:value-of select="addressLine1"/></md:Line1>
                                        <md:Line2><xsl:value-of select="addressLine2"/></md:Line2>
                                        <md:Line3><xsl:value-of select="addressLine3"/></md:Line3>
                                        <md:PostalCode><xsl:value-of select="postalCode"/></md:PostalCode>
                                        <!--                                    <md:PostalCodeExtension><xsl:value-of select="postalCodeExtension"/></md:PostalCodeExtension>-->
                                        <md:City><xsl:value-of select="city"/></md:City>
                                        <md:StateProvince><xsl:value-of select="State"/></md:StateProvince>
                                        <md:County><xsl:value-of select="county"/></md:County>
                                        <md:CountryCode><xsl:value-of select="country/Code"/></md:CountryCode>
                                        <md:TimeZone><xsl:value-of select="State"/></md:TimeZone>
                                        <!--                                    <md:Longitude><xsl:value-of select="longitude"/></md:Longitude>-->
                                        <!--                                    <md:Latitude><xsl:value-of select="latitude"/></md:Latitude>-->
                                    </md:MainAddress>
                                </xsl:if>

                                <xsl:if test="addressType/Name =  'Visit'">
                                    <md:VisitAddress>
                                        <md:Line1><xsl:value-of select="addressLine1"/></md:Line1>
                                        <md:Line2><xsl:value-of select="addressLine2"/></md:Line2>
                                        <md:Line3><xsl:value-of select="addressLine3"/></md:Line3>
                                        <md:PostalCode><xsl:value-of select="postalCode"/></md:PostalCode>
                                        <!--                                    <md:PostalCodeExtension><xsl:value-of select="postalCodeExtension"/></md:PostalCodeExtension>-->
                                        <md:City><xsl:value-of select="city"/></md:City>
                                        <md:StateProvince><xsl:value-of select="State"/></md:StateProvince>
                                        <md:County><xsl:value-of select="county"/></md:County>
                                        <md:CountryCode><xsl:value-of select="country/Code"/></md:CountryCode>
                                        <md:TimeZone><xsl:value-of select="State"/></md:TimeZone>
                                    </md:VisitAddress>
                                </xsl:if>
                            </xsl:for-each>
                        </md:Location>

                        <md:Communication>
                            <xsl:for-each select="X_UDT_PhoneCommunication">

                                <xsl:if test="X_UDT_Phone/phoneType/Name =  'Telephone'">
                                    <md:Phone>
                                        <md:InternationalPrefix><xsl:value-of select="X_UDT_Phone/internationalPrefix"/></md:InternationalPrefix>
                                        <md:AreaPrefix><xsl:value-of select="X_UDT_Phone/nationalPrefix"/></md:AreaPrefix>
                                        <md:Number><xsl:value-of select="X_UDT_Phone/phoneNumber"/></md:Number>
                                        <md:Extension><xsl:value-of select="X_UDT_Phone/phoneNumberExtension"/></md:Extension>
                                    </md:Phone>
                                </xsl:if>

                                <xsl:if test="X_UDT_Phone/phoneType/Name =  'Fax'">
                                    <md:Fax>
                                        <md:InternationalPrefix><xsl:value-of select="X_UDT_Phone/internationalPrefix"/></md:InternationalPrefix>
                                        <md:AreaPrefix><xsl:value-of select="X_UDT_Phone/nationalPrefix"/></md:AreaPrefix>
                                        <md:Number><xsl:value-of select="X_UDT_Phone/phoneNumber"/></md:Number>
                                        <md:Extension><xsl:value-of select="X_UDT_Phone/phoneNumberExtension"/></md:Extension>
                                    </md:Fax>
                                </xsl:if>

                                <!--  X_UDT_contactEmail-->
                                <xsl:if test="X_UDT_ServiceCommunicationType/Name =  'Legal'">
                                </xsl:if>

                            </xsl:for-each>

                            <md:Email><xsl:value-of select="X_UDT_Email/electronicAddress"/></md:Email>

                            <md:WebSite><xsl:value-of select="X_UDT_URL/X_UDT_link"/></md:WebSite>

                        </md:Communication>
                    </md:PublicInformation>
                    <md:ParmaNumber>1234567</md:ParmaNumber>
                    <!--                        <md:OrganisationalClassifications>-->
                    <!--                            <md:OrganisationalClassification>-->
                    <!--                                <md:Class>-->
                    <!--                                    <md:Code>C</md:Code>-->
                    <!--                                    <md:Description>Workshop</md:Description>-->
                    <!--                                </md:Class>-->
                    <!--                                <md:SubClass>-->
                    <!--                                    <md:Code>B</md:Code>-->
                    <!--                                    <md:Description>Service Point</md:Description>-->
                    <!--                                </md:SubClass>-->
                    <!--                            </md:OrganisationalClassification>-->
                    <!--                        </md:OrganisationalClassifications>-->
                </md:Account>

            </md:Accounts>
        </md:DataArea>
        </md:GetAccount>
    </xsl:template>
</xsl:stylesheet>