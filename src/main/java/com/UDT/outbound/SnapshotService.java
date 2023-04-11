package com.UDT.outbound;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.UDT.outbound.SessionCheck.validateToken;
import static com.UDT.outbound.OutboundController.value;
import static com.UDT.outbound.constants.*;

@Service
public class SnapshotService {
    private static final Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("SnapshotService");
    private String finalSnap;
    @Value("${xml.customer.be.base}")
    private String customerBaseDir;
    @Value("#{${UD.common.transform}}")
    private Map<String, String> transformInputs;
    @Autowired
    HeadersService serv;
    @Autowired
    FunctionalityService funcService;



//    @Value("${project.mq.queue-name}")
//    private String queueName;

//    @Value("${msg.Formats}")
//    private String[] messageFormats;

    public void createSnapCustomer(String record) throws Exception {
        LOGGER.info("Received the Source JSON  "); //+record

        String jsonString = record;

//        value=validateToken();

        org.json.simple.JSONObject object = (org.json.simple.JSONObject) JSONValue.parse(jsonString);

        // fetch the BE details of master record
        JSONObject responseBE=funcService.parseBe(object,BUSINESS_ID);

        // the fleet relationship details code
        JSONObject responseFilter=funcService.parseFilter(object,responseBE);

        finalSnap = new ObjectMapper().writeValueAsString(responseFilter);

        System.out.println("Final JSON SNap   " + finalSnap);

        Path mdmPath= Paths.get(String.valueOf(funcService.writeMessageToFile(funcService.jsonToXml(finalSnap), customerBaseDir)));

        //call the transform method to generate different types of msgs using xsl

//        Map<String,String> prop=new HashMap<>();
//        prop.put("src/test/resources/SyncAccountTransform.xsl","src/test/resources/SyncAccount.xml");
//        prop.put("src/test/resources/GetAccountTransform.xsl","src/test/resources/GetAccount.xml");

        // transform the snap to diff downstream msgs and send to queue
        if(funcService.transform(mdmPath,transformInputs));{
            LOGGER.info("Completed ");
        }
    }

    public void createSnapOrganization(String record) throws UnsupportedEncodingException, JsonProcessingException {
        String organizationJson=record;

//        value=validateToken();

        HashMap<String, Object> masterObj = new ObjectMapper().readValue(organizationJson, HashMap.class);

        org.json.simple.JSONObject object1 = (org.json.simple.JSONObject) JSONValue.parse(organizationJson);
        org.json.simple.JSONObject metaObj = (org.json.simple.JSONObject) object1.get(META);
        Object masterId= metaObj.get(BUSINESS_ID);

        // filter call to fetch the related records
        HashMap<String, Object> organizationBERelation=funcService.filterRelationship(masterId,masterObj);
        System.out.println(organizationBERelation);

        /// call to fetch the related BE record
        org.json.simple.JSONObject getSth = (org.json.simple.JSONObject) object1.get(ORGANIZATION_GROUP_TYPE);
        Object level = getSth.get(BUSINESS_ID);
        String recdJsonString = serv.getCallMDM("0RKCQmsgVw0clvVRJkoFU2", ORGANIZATION_I_URL_ID_BE, level);

        HashMap<String, Object> beObj = new ObjectMapper().readValue(recdJsonString, HashMap.class);
        getSth.put(BUSINESS_ID, beObj);  // replaces the group BE ID with the JSON
//        System.out.println(masterObj);
        masterObj.replace(ORGANIZATION_GROUP_TYPE,getSth);

        String finalSnap=new ObjectMapper().writeValueAsString(masterObj);
        System.out.println(finalSnap);

    }
}