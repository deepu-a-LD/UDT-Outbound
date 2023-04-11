<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:md="http://www.udtrucks.com/Customer/5_0" xmlns:UDT="http://www.udtrucks.com/group/common/1_0">
    <xsl:import href="dateTime.xsl" />
    <xsl:output method="xml" indent="yes" />


    <xsl:template match="root">
        <xsl:apply-templates/>
    </xsl:template>


    <!--  <xsl:template match="@*|node()">
          <xsl:copy>
              <xsl:apply-templates select="@*|node()"/>
          </xsl:copy>
      </xsl:template>-->

    <xsl:template match="root">

        <md:SyncAccount xmlns:UDT="http://www.udtrucks.com/group/common/1_0"
                        xmlns:md="http://www.udtrucks.com/Customer/5_0"
                        xmlns:xsd="http://www.w3.org/2001/XMLSchema-instance"
                        serviceID="String" serviceVersion="String"
                        xsd:schemaLocation="http://www.udtrucks.com/Customer/5_0/UDCommon_1_0.xsd" >
            <UDT:ApplicationArea>

                <UDT:Sender>
                    <UDT:LogicalID>
                        <xsl:value-of select="_meta/updatedBy"/>
                    </UDT:LogicalID>

                    <UDT:CreationDateTime>
                        <xsl:call-template name="millisecs-to-ISO">
                            <xsl:with-param name="millisecs" select="_meta/creationDate" />
                        </xsl:call-template>
                    </UDT:CreationDateTime>

                </UDT:Sender>



                <!--  <UDT:BODID>CF017964-8BA7-41B0-BC8C-6C8B73251CF8</UDT:BODID>-->

            </UDT:ApplicationArea>
            <md:DataArea>
                <md:Sync>
                    <UDT:ActionCriteria>SyncAccount</UDT:ActionCriteria>
                </md:Sync>
                <md:Accounts>
                    <md:Account>
                        <md:PartyID><xsl:value-of select="X_UDT_partyID"/></md:PartyID>
                        <md:PublicInformation>
                            <md:LegalName>
                                <xsl:for-each select="X_UDT_AlternateName">
                                    <xsl:if test="X_UDT_nameType/Name='Legal'">
                                        <xsl:value-of select="X_UDT_alternateName"/>
                                    </xsl:if>
                                </xsl:for-each>
                            </md:LegalName>
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
                                    <xsl:if test="addressType/Code =  'Main'">
                                        <md:MainAddress>
                                            <md:Line1>
                                                <xsl:value-of select="addressLine1"/>
                                                <!-- <xsl:choose>
                                                <xsl:when test="addressLine1!=''">
                                                    <xsl:value-of select="addressLine1"/>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:value-of select="null"/>
                                                </xsl:otherwise>
                                            </xsl:choose>--></md:Line1>
                                            <md:Line2><xsl:value-of select="addressLine2"/></md:Line2>
                                            <md:Line3><xsl:value-of select="addressLine3"/></md:Line3>
                                            <md:PostalCode><xsl:value-of select="postalCode"/></md:PostalCode>
                                            <md:PostalCodeExtension><xsl:value-of select="postalCodeExtension"/></md:PostalCodeExtension>
                                            <md:City><xsl:value-of select="city"/></md:City>
                                            <md:StateProvince><xsl:value-of select="state/Code"/></md:StateProvince>
                                            <md:County><xsl:value-of select="county"/></md:County>
                                            <md:CountryCode><xsl:value-of select="country/Code"/></md:CountryCode>
                                            <!--<md:TimeZone><xsl:value-of select="timezone"/></md:TimeZone>
                                            <md:Longitude><xsl:value-of select="longitude"/></md:Longitude>
                                            <md:Latitude><xsl:value-of select="latitude"/></md:Latitude>-->
                                        </md:MainAddress>
                                    </xsl:if>

                                    <xsl:if test="addressType/Code =  'Visit'">
                                        <md:VisitAddress>
                                            <md:Line1><xsl:value-of select="addressLine1"/></md:Line1>
                                            <md:Line2><xsl:value-of select="addressLine2"/></md:Line2>
                                            <md:Line3><xsl:value-of select="addressLine3"/></md:Line3>
                                            <md:PostalCode><xsl:value-of select="postalCode"/></md:PostalCode>
                                            <md:PostalCodeExtension><xsl:value-of select="postalCodeExtension"/></md:PostalCodeExtension>
                                            <md:City><xsl:value-of select="city"/></md:City>
                                            <md:StateProvince><xsl:value-of select="state/Code"/></md:StateProvince>
                                            <md:County><xsl:value-of select="county"/></md:County>
                                            <md:CountryCode><xsl:value-of select="country/Code"/></md:CountryCode>
                                            <!--  <md:TimeZone><xsl:value-of select="State"/></md:TimeZone>
                                              <md:Longitude><xsl:value-of select="longitude"/></md:Longitude>
                                              <md:Latitude><xsl:value-of select="latitude"/></md:Latitude>-->
                                        </md:VisitAddress>
                                    </xsl:if>
                                </xsl:for-each>
                            </md:Location>

                            <md:Communication>
                                <xsl:for-each select="X_UDT_PhoneCommunication/X_UDT_Phone">

                                    <xsl:if test="phoneType/Code ='Telephone'">
                                        <md:Phone>
                                            <md:InternationalPrefix><xsl:value-of select="internationalPrefix"/></md:InternationalPrefix>
                                            <!--                                        <md:AreaPrefix><xsl:value-of select="nationalPrefix"/></md:AreaPrefix>-->
                                            <md:Number><xsl:value-of select="phoneNumber"/></md:Number>
                                            <!--  <md:Extension><xsl:value-of select="phoneNumberExtension"/></md:Extension>-->
                                        </md:Phone>
                                    </xsl:if>

                                    <xsl:if test="phoneType/Code =  'Fax'">
                                        <md:Fax>
                                            <md:InternationalPrefix><xsl:value-of select="internationalPrefix"/></md:InternationalPrefix>
                                            <!-- <md:AreaPrefix><xsl:value-of select="nationalPrefix"/></md:AreaPrefix>-->
                                            <md:Number><xsl:value-of select="phoneNumber"/></md:Number>
                                            <!--<md:Extension><xsl:value-of select="phoneNumberExtension"/></md:Extension>-->
                                        </md:Fax>
                                    </xsl:if>
                                </xsl:for-each>
                                <md:WebSite>
                                    <xsl:for-each select="X_UDT_URL">
                                        <xsl:if test="X_UDT_uRLType/Code='Website'">
                                            <xsl:value-of select="X_UDT_link"/>
                                        </xsl:if>
                                    </xsl:for-each>
                                </md:WebSite>
                                <md:Email>
                                    <xsl:for-each select="X_UDT_Email">
                                        <xsl:if test="electronicAddressStatus/Code='Active'">
                                            <xsl:value-of select="electronicAddress"/>
                                        </xsl:if>
                                    </xsl:for-each>
                                </md:Email>
                            </md:Communication>
                        </md:PublicInformation>
                        <md:ParmaNumber><xsl:value-of select="X_UDT_parmaID"/></md:ParmaNumber>
                        <!--<md:OrganisationalClassifications>
                          <md:OrganisationalClassification>
                               <md:Class>
                                   <md:Code>C</md:Code>
                                   <md:Description>Workshop</md:Description>
                               </md:Class>
                               <md:SubClass>
                                   <md:Code>B</md:Code>
                                   <md:Description>Service Point</md:Description>
                               </md:SubClass>
                           </md:OrganisationalClassification>
                        </md:OrganisationalClassifications>  -->

                    </md:Account>
                </md:Accounts>

            </md:DataArea>
        </md:SyncAccount>
    </xsl:template>

</xsl:stylesheet>
