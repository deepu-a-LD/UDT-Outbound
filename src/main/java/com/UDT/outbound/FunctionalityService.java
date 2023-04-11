package com.UDT.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udtrucks.commons.worker.XMLWorker;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.util.Strings;
import org.json.XML;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsOperations;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.UDT.outbound.OutboundController.value;
import static com.UDT.outbound.constants.*;


@Service
public class FunctionalityService {

    private static final Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("FunctionalityService");
    @Autowired
    HeadersService serv;
    @Autowired
    JmsOperations jmsOperations;

    @Value("${customer.readValues}")
    private String[] customer;
    private String internalID;
    @Value("${xml.customer.be.base}")
    private String customerBaseDir;
    @Value("#{${send.msgs.queue}}")
    private Map<String, String> sendTargetMsgsToQueue;

    public JSONObject relationshipResponse(Object listOfIDS, String internalIDFleet) throws UnsupportedEncodingException, JsonProcessingException {

        org.json.simple.JSONObject object = (org.json.simple.JSONObject) JSONValue.parse(listOfIDS.toString());
        org.json.simple.JSONArray getSth = (org.json.simple.JSONArray) object.get(CONTENT);

//      if cndition to check totalnumber of relation is 0 or greater
//             loop through the array of content
        for (int i = 0; i < getSth.size(); i++) {
//                store the content index in object
            org.json.simple.JSONObject explrObject = (org.json.simple.JSONObject) getSth.get(i);
//                 fetch the _toattribute
            org.json.simple.JSONObject explrObject1 = (org.json.simple.JSONObject) explrObject.get(_TO_ATTRIBUTE);

            Object IdAttrValue = explrObject1.get(BUSINESS_ID);

            System.out.println("-----------retrived id value -------------");

            String internalID="";

            if(internalIDFleet.equals("")) {
                Object bussEntity = explrObject1.get(BUSINESS_ENTITY);
                    if (bussEntity.equals(DISTRICT_BE)) {
                        internalID = DISTRICT_BE;
                    } else {
                        internalID = ORGANIZATION_I_URL_ID_BE;
                    }
                }else{
                internalID=internalIDFleet;
            }

//               URL formation
            String relationshipJson = serv.getCallMDM("0RKCQmsgVw0clvVRJkoFU2", internalID , IdAttrValue);
            System.out.println(value);
            System.out.println(relationshipJson);

            HashMap<String, Object> obj = new ObjectMapper().readValue(relationshipJson, HashMap.class);

            explrObject1.put(BUSINESS_ID, obj);

//            System.out.println(updateValue);

            object.put(CONTENT, getSth);
            System.out.println("Final Snap Relationship Records " +object);

            System.out.println("---------------------------------------------------------------------------------------------");
        }
        return object;
    }

    public JSONObject parseFilter(JSONObject object, JSONObject responseBE) throws UnsupportedEncodingException, JsonProcessingException {
        org.json.simple.JSONObject getSth = (org.json.simple.JSONObject) object.get(META);
        Object masterId = getSth.get(BUSINESS_ID);

        Object listOfIDS = serv.filterCallFleetMDM("3Gb37FHVVI9eBmT8O6g8kY", masterId, CUSTOMER_FLEET_URL_ID, CUSTOMER_I_URL_ID_BE);

        JSONObject fleetInfo=relationshipResponse(listOfIDS, CUSTOMER_I_URL_ID_BE);
        String finalRelationshipSnap = new ObjectMapper().writeValueAsString(fleetInfo);

        HashMap<String, Object> relationshipObj = new ObjectMapper().readValue(finalRelationshipSnap, HashMap.class);
        responseBE.put(CUSTOMER_FLEET_URL_ID,relationshipObj);
        return responseBE;
    }
    public JSONObject parseBe(JSONObject object, String businessId) {
        org.json.simple.JSONObject getSth = null;
        for (String msgs : customer) {
            System.out.println(msgs);
            try {
                getSth = (org.json.simple.JSONObject) object.get(msgs);
                if(!getSth.equals(null)) {
                    Object level = getSth.get(businessId);
                    System.out.println(level);

                    if (msgs.equals(CUSTOMER_LEGAL_BE)) {
                        internalID = CUSTOMER_I_URL_ID_BE;
                    } else {
                        internalID = ORGANIZATION_I_URL_ID_BE;
                    }
                    String recdJsonString = serv.getCallMDM("3Gb37FHVVI9eBmT8O6g8kY", internalID, level);
                    HashMap<String, Object> result1 = new ObjectMapper().readValue(recdJsonString, HashMap.class);
                    System.out.println("Converted to map " + result1);
                    object.replace(msgs, result1);
                    System.out.println("---------------------------------------------------------------------------");
                }
            } catch (Exception e) {
                LOGGER.info("NO Business Entities to Read");
            }
        }
        return object;
    }

    public boolean transform(Path mdmPath, Map<String, String> transformInputs){
        AtomicBoolean isDone = new AtomicBoolean(false);
        Map<String,String> inputs=new HashMap<>(transformInputs);
        inputs.entrySet().forEach(e -> {
            String identifymsg;
            XMLWorker xmlobj = XMLWorker.XMLWorkerBuilder.aXMLWorker().withSourceFilePath(String.valueOf(mdmPath)).withTargetFilePath(e.getValue()).withTargetXSLFilePath(e.getKey()).build();
            boolean done = xmlobj.transform();
            System.out.println(done);
            if (done) {
                identifymsg = XMLWorker.XMLWorkerBuilder.aXMLWorker().withSourceFilePath(e.getValue()).build().identifyMsgType();
                System.out.println(identifymsg);
                isDone.set(msgsToQueue(identifymsg, e.getValue()));
                File fileToDelete = new File(e.getValue());
                try {
                    fileToDelete.delete();
                    LOGGER.info("Generated XML transferred to queue and deleted locally");
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            } else {
                LOGGER.error("Could not get the Root element");
            }
        });
        if(isDone.get()){
            final File f = new File(customerBaseDir); // need to get path from props
            for (final File file : f.listFiles()) {
                if (!file.isDirectory() ) {
                    file.delete();
                }
                if(f.exists()){
                    f.delete();
                    LOGGER.info("Directory and files deleted");
                }
            }
        }
        return true;
    }

    public HashMap<String, Object> filterRelationship(Object fromId, HashMap<String, Object> fromObj) throws UnsupportedEncodingException, JsonProcessingException {

        Object listOfIDS= Strings.EMPTY;

        for(String a: ORGANIZATION_RELATIONSHIP_I_URL_ID) {

            listOfIDS = serv.filterCallFleetMDM("0RKCQmsgVw0clvVRJkoFU2", fromId, a, ORGANIZATION_I_URL_ID_BE);

            System.out.println(listOfIDS);

            JSONObject object=relationshipResponse(listOfIDS, "");

            String finalRelationshipSnap = new ObjectMapper().writeValueAsString(object);

            HashMap<String, Object> relationshipObj = new ObjectMapper().readValue(finalRelationshipSnap, HashMap.class);

            //adds the relationship final payload to master json
            fromObj.put(a,relationshipObj);
        }
        return fromObj;
    }

    public boolean msgsToQueue(String identifymsg, String value) {
        sendTargetMsgsToQueue.entrySet().forEach(e1 -> {
            String result = "";
            if (identifymsg.equals(e1.getKey())) {
                try {
                    result = xmltoString(value);  //convert xml to string
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (result != null) {
                    jmsOperations.convertAndSend(e1.getValue(), result);
                    System.out.println("Message sent to queue successfully  " + e1.getValue());

                }
            }
        });
        /* try {
            String message = jmsOperations.receive("queue_SA").getBody(String.class);
            System.out.println("message received from queue "+message);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }*/
        return true;
    }

    public String xmltoString(String xml) throws IOException {
        if (xml != null) {
            Reader fileReader = new FileReader(xml);
            BufferedReader bufReader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String line = bufReader.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = bufReader.readLine();
            }
            String xml2String = sb.toString();
            System.out.println("XML to String using BufferedReader done");
//        System.out.println(xml2String);
            bufReader.close();
//            sendQueue(xml2String);
            return xml2String;

        } else {
            return "";
        }
        // to check the queue has complete msg received

    }

    public String jsonToXml(String finalSnap) {
        String replacedString=finalSnap.replace(CONTENT,CONTENTS);

        org.json.JSONObject json = new org.json.JSONObject(replacedString);

        String root = "root";
        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n<" + root + ">" + XML.toString(json) + "</" + root + ">";

        String afterConversion=xml.replace(CONTENTS,CONTENT);

        System.out.println(afterConversion);

        return afterConversion;

       /* File file = new File(PREFIX_XML+new Date().getTime()+SUFFIX_XML);
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(xml);
        pw.close();*/
    }

    public Path writeMessageToFile(String finalXml, String fileBaseDir) throws Exception {
        File baseDir = new File(fileBaseDir);
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }
        String fileName = PREFIX_XML+new Date().getTime()+SUFFIX_XML;
        Path filePath = Paths.get(baseDir.getPath(),fileName);
        LOGGER.info("Writing to path:"+filePath);
        File xmlFile  = new File(String.valueOf(filePath));
        try {
            FileUtils.writeStringToFile(xmlFile , finalXml);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public void deleteXml(String value) {
        File fileToDelete = new File(value);
        try {
            fileToDelete.delete();
            System.out.println("Generated XML transferred and deleted");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
