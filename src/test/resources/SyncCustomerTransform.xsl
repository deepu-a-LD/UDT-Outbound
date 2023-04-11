<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="dateTime.xsl" />
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />

    <xsl:template match="/">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="root">
        <xsl:element name="md:SyncCustomer" xmlns:ud="http://www.udt.com/group/common/1_0"
                     xmlns:md="http://www.cdb.udt.com/masterdata/4_0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://www.cdb.udt.com/masterdata/4_0 Account_4_0.xsd">

            <ud:ApplicationArea>
                <ud:Sender>
                    <ud:LogicalID>
                        <xsl:value-of select="_meta/updatedBy" />
                    </ud:LogicalID>
                </ud:Sender>
                <ud:Receiver>
                    <ud:LogicalID>
                        <xsl:value-of select="_meta/updatedBy" />
                    </ud:LogicalID>
                </ud:Receiver>
                <ud:CreationDateTime>
                    <xsl:call-template name="millisecs-to-ISO">
                        <xsl:with-param
                                name="millisecs" select="_meta/creationDate" />
                    </xsl:call-template>
                </ud:CreationDateTime>
                <!-- <ud:BODID>92B37C66-E8D3-4382-BA35-BF53C7A4FEDC</ud:BODID> -->

            </ud:ApplicationArea>
            <md:DataArea>
                <md:Sync>

                    <ud:ActionCriteria>SyncCustomer</ud:ActionCriteria>
                    <!--Optional:->
                    <ud:UserArea>
                        <!-Zero
                    or more repetitions:->
                        <ud:BooleanValue
                    name="string">true</ud:BooleanValue>
                        <!-Zero or more
                    repetitions:->
                        <ud:DateTimeValue
                    name="string">2003-08-09T05:48:37+05:30</ud:DateTimeValue>
                        <!-Zero
                    or more repetitions:->
                        <ud:DateValue
                    name="string">2012-09-13+05:30</ud:DateValue>
                        <!-Zero
                    or more repetitions:->
                        <ud:DecimalValue
                    name="string">1000.00</ud:DecimalValue>
                        <!-Zero or more
                    repetitions:->
                        <ud:IntegerValue
                    name="string">100</ud:IntegerValue>
                        <!-Zero or more
                    repetitions:->
                        <ud:StringValue
                    name="string">string</ud:StringValue>
                    </ud:UserArea> -->
                </md:Sync>
                <md:Customers>
                    <md:Customer>

                        <md:PartyID>
                            <xsl:value-of select="X_UDT_partyID" />
                        </md:PartyID>

                        <md:PublicInformation>

                            <xsl:choose>
                                <xsl:when
                                        test="X_UDT_AlternateName/X_UDT_nameType/Code =  'LEG'">
                                    <md:LegalName>
                                        <xsl:value-of
                                                select="X_UDT_AlternateName/X_UDT_alternateName" />
                                    </md:LegalName>
                                </xsl:when>
                                <xsl:otherwise>
                                    <md:LegalName>
                                        <xsl:value-of select="X_UDT_name" />
                                    </md:LegalName>
                                </xsl:otherwise>
                            </xsl:choose>


                            <md:CommonName>
                                <xsl:value-of select="X_UDT_name" />
                            </md:CommonName>

                            <md:GlobalID>
                                <xsl:for-each select="X_UDT_Identifier">
                                    <xsl:if test="X_UDT_identifierType/Code='VAT'">
                                        <md:VATNumber>
                                            <xsl:value-of select="X_UDT_identifierValue" />
                                        </md:VATNumber>
                                    </xsl:if>
                                    <xsl:if
                                            test="X_UDT_identifierType/Code='COC'">
                                        <md:ChamberOfCommerce>
                                            <xsl:value-of select="X_UDT_identifierValue" />
                                        </md:ChamberOfCommerce>
                                    </xsl:if>
                                    <xsl:if
                                            test="X_UDT_identifierType/Code='ON'">
                                        <md:OrganisationNumber>
                                            <xsl:value-of select="X_UDT_identifierValue" />
                                        </md:OrganisationNumber>
                                    </xsl:if>
                                </xsl:for-each>
                            </md:GlobalID>
                            <md:Location>

                                <xsl:for-each select="X_UDT_Address">
                                    <xsl:if test="./addressType/Code = 'Main'">
                                        <md:MainAddress>
                                            <md:Line1>
                                                <xsl:value-of
                                                        select="addressLine1" />
                                            </md:Line1>

                                            <md:Line2>
                                                <xsl:value-of
                                                        select="addressLine2" />
                                            </md:Line2>

                                            <md:Line3>
                                                <xsl:value-of
                                                        select="addressLine3" />
                                            </md:Line3>

                                            <md:PostalCode>
                                                <xsl:value-of
                                                        select="postalCode" />
                                            </md:PostalCode>

                                            <md:PostalCodeExtension>
                                                <xsl:value-of
                                                        select="postalCodeExtension" />
                                            </md:PostalCodeExtension>

                                            <md:City>
                                                <xsl:value-of select="city" />
                                            </md:City>

                                            <md:StateProvince>
                                                <xsl:value-of
                                                        select="state/Code" />
                                            </md:StateProvince>

                                            <md:County>
                                                <xsl:value-of select="county" />
                                            </md:County>
                                            <md:CountryCode>
                                                <xsl:value-of
                                                        select="state/Code" />
                                            </md:CountryCode>

                                            <!-- <md:TimeZone>
                                                <xsl:value-of
                                                    select="NA" />
                                            </md:TimeZone>

                                            <md:Longitude>
                                                <xsl:value-of
                                                    select="longitude" />
                                            </md:Longitude>

                                            <md:Latitude>
                                                <xsl:value-of
                                                    select="latitude" />
                                            </md:Latitude> -->
                                        </md:MainAddress>
                                    </xsl:if>

                                    <xsl:if
                                            test="./addressType/Code =  'Visit'">

                                        <md:VisitAddress>
                                            <md:Line1>
                                                <xsl:value-of
                                                        select="addressLine1" />
                                            </md:Line1>

                                            <md:Line2>
                                                <xsl:value-of
                                                        select="addressLine2" />
                                            </md:Line2>

                                            <md:Line3>
                                                <xsl:value-of
                                                        select="addressLine3" />
                                            </md:Line3>

                                            <md:PostalCode>
                                                <xsl:value-of
                                                        select="postalCode" />
                                            </md:PostalCode>

                                            <md:PostalCodeExtension>
                                                <xsl:value-of
                                                        select="postalCodeExtension" />
                                            </md:PostalCodeExtension>

                                            <md:City>
                                                <xsl:value-of select="city" />
                                            </md:City>

                                            <md:StateProvince>
                                                <xsl:value-of
                                                        select="state/Code" />
                                            </md:StateProvince>

                                            <md:County>
                                                <xsl:value-of select="county" />
                                            </md:County>
                                            <md:CountryCode>
                                                <xsl:value-of
                                                        select="state/Code" />
                                            </md:CountryCode>

                                            <!-- <md:TimeZone>
                                                <xsl:value-of
                                                    select="NA" />
                                            </md:TimeZone>

                                            <md:Longitude>
                                                <xsl:value-of
                                                    select="longitude" />
                                            </md:Longitude>

                                            <md:Latitude>
                                                <xsl:value-of
                                                    select="latitude" />
                                            </md:Latitude> -->
                                        </md:VisitAddress>
                                    </xsl:if>
                                </xsl:for-each>
                            </md:Location>

                            <md:Communication>

                                <xsl:for-each select="X_UDT_PhoneCommunication/X_UDT_Phone">
                                    <xsl:if test="not(./phoneType/Code = 'Fax')">
                                        <md:Phone>

                                            <md:InternationalPrefix>
                                                <xsl:value-of
                                                        select="X_UDT_PhoneCommunication/X_UDT_Phone/internationalPrefix" />
                                            </md:InternationalPrefix>

                                            <!--<md:AreaPrefix>
                                                <xsl:value-of
                                                    select="areaPrefix" />
                                            </md:AreaPrefix> -->

                                            <md:Number>
                                                <xsl:value-of
                                                        select="phoneNumber" />
                                            </md:Number>

                                            <!-- <md:Extension>
                                                <xsl:value-of
                                                    select="extension" />
                                            </md:Extension> -->
                                        </md:Phone>
                                    </xsl:if>
                                    <xsl:if
                                            test="./phoneType/Code = 'Fax'">

                                        <md:Fax>

                                            <md:InternationalPrefix>
                                                <xsl:value-of
                                                        select="internationalPrefix" />
                                            </md:InternationalPrefix>

                                            <!-- <md:AreaPrefix>
                                                <xsl:value-of
                                                    select="areaPrefix" />
                                            </md:AreaPrefix> -->

                                            <md:Number>
                                                <xsl:value-of
                                                        select="phoneNumber" />
                                            </md:Number>

                                            <!-- <md:Extension>
                                                <xsl:value-of
                                                    select="extension" />
                                            </md:Extension> -->
                                        </md:Fax>
                                    </xsl:if>
                                </xsl:for-each>
                                <xsl:for-each select="X_UDT_URL">

                                    <md:WebSite>
                                        <xsl:value-of
                                                select="X_UDT_link" />
                                    </md:WebSite>
                                </xsl:for-each>

                                <xsl:for-each select="X_UDT_Email">
                                    <!-- *************Is check for active inactive needed********* -->

                                    <md:Email>
                                        <xsl:value-of
                                                select="electronicAddress" />
                                    </md:Email>
                                </xsl:for-each>
                            </md:Communication>
                        </md:PublicInformation>

                        <md:KnownAs>
                            <md:Application>
                                <md:ID>
                                    <xsl:value-of
                                            select="root/X_UDT_KnownAs/_id" />
                                </md:ID>
                                <md:Name>
                                    <xsl:value-of
                                            select="root/X_UDT_KnownAs/X_UDT_applicationID" />
                                </md:Name>
                            </md:Application>
                            <!-- <md:ExternalID>string</md:ExternalID> -->
                        </md:KnownAs>

                        <md:ParmaNumber>
                            <xsl:value-of
                                    select="root/X_UDT_parmaID" />
                        </md:ParmaNumber>

                        <!-- <md:AccountStructures>
                            <md:AccountStructure>
                                <md:Role>string</md:Role>
                                <md:PartyID>
                                    <xsl:value-of select="X_UDT_partyID" />
                                </md:PartyID>
                            </md:AccountStructure>
                        </md:AccountStructures> -->

                        <!-- <md:OrganisationalClassifications>

                            <md:OrganisationalClassification>
                            <md:Class>
                                <md:Code>string</md:Code>

                                <md:Description>string</md:Description>
                            </md:Class>

                            <md:SubClass>
                                <md:Code>string</md:Code>

                                <md:Description>string</md:Description>
                            </md:SubClass>
                        </md:OrganisationalClassification>
                        </md:OrganisationalClassifications>-->

                        <!-- <md:ResponsibleUnitRelationships>

                            <md:ResponsibleUnitRelationship>

                                <md:BABrand>
                                    <md:BA>
                                        <md:Code>string</md:Code>

                                        <md:Description>string</md:Description>
                                    </md:BA>
                                    <md:Brand>
                                        <md:Code>string</md:Code>

                                        <md:Description>string</md:Description>
                                    </md:Brand>
                                </md:BABrand>

                                <md:ResponsibleUnits>
                                    <md:ResponsibleUnit>
                                        <md:PartyID>
                                            <xsl:value-of select="X_UDT_partyID" />
                                        </md:PartyID>
                                    </md:ResponsibleUnit>
                                </md:ResponsibleUnits>
                            </md:ResponsibleUnitRelationship>
                        </md:ResponsibleUnitRelationships> -->

                        <!-- <md:CustomerTo>

                            <md:BABrand>
                                <md:BA>
                                    <md:Code>string</md:Code>

                                    <md:Description>string</md:Description>
                                </md:BA>
                                <md:Brand>
                                    <md:Code>string</md:Code>

                                    <md:Description>string</md:Description>
                                </md:Brand>
                            </md:BABrand>
                        </md:CustomerTo> -->

                        <md:MergeHistory>

                            <md:Mergee>
                                <md:PartyID>
                                    <xsl:value-of select="X_UDT_partyID" />
                                </md:PartyID>
                            </md:Mergee>
                        </md:MergeHistory>

                        <md:Maintainers>

                            <md:MainMaintainers>


                                <md:MainMaintainer>
                                    <md:PartyID>
                                        <xsl:value-of select="X_UDT_partyID" />
                                    </md:PartyID>
                                </md:MainMaintainer>
                            </md:MainMaintainers>

                            <md:AssignedMaintainers>

                                <md:AssignedMaintainer>
                                    <md:PartyID>
                                        <xsl:value-of select="X_UDT_partyID" />
                                    </md:PartyID>
                                </md:AssignedMaintainer>
                            </md:AssignedMaintainers>
                        </md:Maintainers>
                    </md:Customer>
                </md:Customers>
            </md:DataArea>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>