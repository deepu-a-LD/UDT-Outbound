package com.UDT.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import static com.UDT.outbound.constants.*;

@Service
public class RelationshipService {

    @Autowired
    HeadersService serv;

    @Autowired
    FunctionalityService funcService;

    public void createSnap(String record) throws UnsupportedEncodingException, JsonProcessingException {
        String organizationJson=record;

        HashMap<String, Object> masterObj = new ObjectMapper().readValue(organizationJson, HashMap.class);

        org.json.simple.JSONObject object1 = (org.json.simple.JSONObject) JSONValue.parse(organizationJson);
        org.json.simple.JSONObject fromObj = (org.json.simple.JSONObject) object1.get(FROM_ATTRIBUTE);

        Object fromId= fromObj.get(FROM_BID_ATTRIBUTE);

        String internalID;
        Object BEValue=fromObj.get(FROM_BE_NAME);

        if(BEValue.equals(ORGANIZATION)){
            internalID=ORGANIZATION_I_URL_ID_BE;
        }else if(BEValue.equals(DISTRICT)){
            internalID = DISTRICT_BE;
        } else{
            internalID=CUSTOMER_I_URL_ID_BE;
        }

        // get the master record details
        String recdJsonString = serv.getCallMDM("0RKCQmsgVw0clvVRJkoFU2", internalID, fromId);
        HashMap<String, Object> fromBE = new ObjectMapper().readValue(recdJsonString, HashMap.class);

        System.out.println(fromObj);

        String fromJSonString= new ObjectMapper().writeValueAsString(fromBE);
        JSONObject fromJsonObject= (org.json.simple.JSONObject) JSONValue.parse(fromJSonString);
        String recdJsonStringGroup= Strings.EMPTY;
        org.json.simple.JSONObject getSth=null;
        HashMap<String, Object> groupObj;

        // perform the BE and filter and generate the final snap of FROM
        if(BEValue.equals(ORGANIZATION)) {
            //fetch the BE id
            getSth = (org.json.simple.JSONObject) fromJsonObject.get(ORGANIZATION_GROUP_TYPE);

            Object level = getSth.get(_BUSINESS_ID);

            // call to get the BE details
            recdJsonStringGroup = serv.getCallMDM("0RKCQmsgVw0clvVRJkoFU2", ORGANIZATION_I_URL_ID_BE, level);

            groupObj = new ObjectMapper().readValue(recdJsonStringGroup, HashMap.class);

            getSth.put(_BUSINESS_ID, groupObj);

            fromJsonObject.replace(ORGANIZATION_GROUP_TYPE, getSth);

            fromObj.replace(FROM_BID_ATTRIBUTE, fromJsonObject);

            String finalSnapFromBE = new ObjectMapper().writeValueAsString(fromObj);
            System.out.println(finalSnapFromBE);

            // filter call to fetch the related relationship details of master record

            HashMap<String,Object> relationshipFromObject=funcService.filterRelationship(fromId,fromJsonObject);

            //add the relationshipFromObject to master record
            fromObj.replace(FROM_BID_ATTRIBUTE,relationshipFromObject);

            masterObj.replace(FROM_ATTRIBUTE,fromObj);  // this is the final snap of from

        }else{
            String finalSnap;

            JSONObject responseBE=funcService.parseBe(fromJsonObject, _BUSINESS_ID);

            System.out.println(responseBE);

            JSONObject responseFilter=funcService.parseFilter(fromJsonObject,responseBE);

            finalSnap = new ObjectMapper().writeValueAsString(responseFilter);

            System.out.println("Final JSON SNap   " + finalSnap);

            fromObj.replace(FROM_BID_ATTRIBUTE, fromJsonObject);

            masterObj.replace(FROM_ATTRIBUTE,fromObj);   // this is the final snap of from
        }

        // to related code
        org.json.simple.JSONObject toObj = (org.json.simple.JSONObject) object1.get(TO_ATTRIBUTE);
        Object toId = toObj.get(TO_BID_ATTRIBUTE);
        String BEOfTo = (String) toObj.get(TO_BE_NAME);

        if(BEOfTo.equals(ORGANIZATION)){
            internalID=ORGANIZATION_I_URL_ID_BE;
        }else if(BEOfTo.equals(DISTRICT)){
            internalID = DISTRICT_BE;
        } else{
            internalID=CUSTOMER_I_URL_ID_BE;
        }

        String recdJsonStringTo = serv.getCallMDM("0RKCQmsgVw0clvVRJkoFU2", internalID, toId);
        HashMap<String, Object> toBE = new ObjectMapper().readValue(recdJsonStringTo, HashMap.class);
        toObj.replace(TO_BID_ATTRIBUTE, toBE);

        // update to record to master record
        masterObj.replace(TO_ATTRIBUTE,toObj);

        String finalSnapOfBER = new ObjectMapper().writeValueAsString(masterObj);
        System.out.println("FINAL SNAP OF TO AND FROM \n"+finalSnapOfBER);


    }

}
