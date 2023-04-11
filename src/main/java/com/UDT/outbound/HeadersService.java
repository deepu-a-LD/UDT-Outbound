package com.UDT.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udtrucks.commons.worker.CommWorker;
import org.apache.logging.log4j.util.Strings;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class HeadersService {

    private static final Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("HeadersService");

    private static String requestType;

    private String strCustMDMUrl;

    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    static public String loginSaaSMDM() throws UnsupportedEncodingException, JsonProcessingException {
        requestType="MDM.request.login";
        HashMap<String,String> prop= (HashMap<String, String>) parsePropFile(requestType);
      /*  prop.put("MDM.request.login.URL","https://dm-us.informaticacloud.com/ma/api/v2/user/login");
        prop.put("MDM.request.login.Headers","content-type,content-length");
        prop.put("MDM.request.login.Headers.content-type","application/json");
        prop.put("MDM.request.login.Headers.content-length","");
        prop.put("MDM.request.login.payload","{\n" +
                "    \"username\": \"UI_User\",\n" +
                "    \"password\": \"AbcEtr@123\"\n" +
                "}");
*/
/*prop.entrySet().forEach(e->{
    System.out.println(e.getKey());
    System.out.println(e.getValue());
});*/
        String loginUrl= requestType+".URL";
        String URL =  prop.get(loginUrl);
        System.out.println(URL);

        String payload= parsePayload(prop,requestType,"","","");
//        System.out.println(payload);
        String[] headersString = prop.get(requestType+".headers").split(",");

        MultiValueMap<String, String> headers = parseHeaders(headersString,prop,requestType,payload,"");

        ResponseEntity<String> obj2 = CommWorker.CommWorkerBuilder.acommWorker().withUrl(URL).withPayload(payload).withHeaders(headers).withMethod(HttpMethod.POST.name()).build().request();
        System.out.println("Login Content " + obj2.getBody());

        String login = obj2.getBody();

        HashMap<String, Object> loginResult = new ObjectMapper().readValue(login, HashMap.class);

        String v1 = (String) loginResult.get("icSessionId");
        System.out.println(v1);  //valid for 30mins
        return v1;
    }

   static Object validateSessionId(String sessionValue) throws UnsupportedEncodingException, JsonProcessingException {
        requestType="MDM.request.validate";

        Map<String,String> prop=parsePropFile(requestType);

        String sessionID=sessionValue;

        String URL =  prop.get(requestType+".URL");

        String payload= parsePayload(prop,requestType,sessionID,"","");

        String[] headersString = prop.get(requestType+".headers").split(",");
//      String payload= parsePayload(prop,requestType);
        MultiValueMap<String, String> headers = parseHeaders(headersString,prop,requestType,"",sessionID);
//        System.out.println(headers);

        ResponseEntity<String> obj1 = CommWorker.CommWorkerBuilder.acommWorker().withPayload(payload).withUrl(URL).withHeaders(headers).withMethod(HttpMethod.POST.name()).build().request();
        System.out.println("The info received from MDM " + obj1.getBody());

        String recdJsonFile = obj1.getBody();
        HashMap<String, Object> result1= new ObjectMapper().readValue(recdJsonFile, HashMap.class);
        Object expireTime=result1.get("timeUntilExpire");
        System.out.println(expireTime);
        return expireTime;
    }

    public static boolean logoutSaaSMDM(String sessionValue) throws UnsupportedEncodingException {
        requestType="MDM.request.logout";

        Map<String,String> prop=parsePropFile(requestType);

        String sessionID=sessionValue;

        String URL =  prop.get(requestType+".URL");


        String[] headersString = prop.get(requestType+".headers").split(",");
//      String payload= parsePayload(prop,requestType);
        MultiValueMap<String, String> headers = parseHeaders(headersString,prop,requestType,"",sessionID);
//        System.out.println(headers);

        ResponseEntity<String> obj1 = CommWorker.CommWorkerBuilder.acommWorker().withUrl(URL).withHeaders(headers).withMethod(HttpMethod.POST.name()).build().request();
        System.out.println("Logged Out Successfully");
        return true;
    }

    public static Map<String,String> parsePropFile(String requestType){
        Map<String,String > requestPropMap=new HashMap<>();
        InputStream inputStream;
        Properties properties = new Properties();
        try {
            // Class path is found under WEB-INF/classes
            String propFileName = "application.properties";

            inputStream = HeadersService.class.getClassLoader().getResourceAsStream(propFileName);
            // read the file
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        properties.entrySet().forEach(e->{
            String headerVal= (String) e.getKey();
            if(headerVal.contains(requestType)) {
                requestPropMap.put(headerVal,properties.getProperty(headerVal));
//                System.out.println(requestPropMap);
            }
        });
        return requestPropMap;
    }

    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public String getCallMDM(String sessionValue,String internalid,Object level) throws UnsupportedEncodingException {
        requestType="MDM.request.Get";

        Map<String,String> prop=parsePropFile(requestType);

        String sessionID=sessionValue;

        String URL =  prop.get(requestType+".URL");

        strCustMDMUrl=URL+ internalid + "/" + level;
        System.out.println(strCustMDMUrl);

        String[] headersString = prop.get(requestType+".headers").split(",");
//      String payload= parsePayload(prop,requestType);
        MultiValueMap<String, String> headers = parseHeaders(headersString,prop,requestType,"",sessionID);
//      System.out.println(headers);

        ResponseEntity<String> obj1 = CommWorker.CommWorkerBuilder.acommWorker().withUrl(strCustMDMUrl).withHeaders(headers).withMethod(HttpMethod.GET.name()).build().request();
        System.out.println("The info received from MDM " + obj1.getBody());

        return obj1.getBody();
    }

    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public Object filterCallFleetMDM(String sessionValue, Object idAttrFromValue, String internalURLId,String internalId){
        requestType="MDM.request.filter";
        ResponseEntity<String> response= null;
        try {
        Map<String,String> prop=parsePropFile(requestType);

        String sessionID=sessionValue;

        String URL =  prop.get(requestType+".URL");

        String internalURlID=internalURLId;

        String finalURL=URL+internalURlID+"/filter";
        System.out.println(finalURL);

        String payload= parsePayload(prop,requestType,sessionID,idAttrFromValue,internalId);

        String[] headersString = prop.get(requestType+".headers").split(",");

        MultiValueMap<String, String> headers = parseHeaders(headersString,prop,requestType,"",sessionID);

        response = CommWorker.CommWorkerBuilder.acommWorker().withPayload(payload).withUrl(finalURL).withHeaders(headers).withMethod(HttpMethod.POST.name()).build().request();
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.info("Filter Response Received");
            } else {
                LOGGER.error("Invoke Filter call Failure");
            }
        } catch (Exception e){
            e.printStackTrace();
            return  new ResponseEntity<String>("REQUEST FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response.getBody();
    }

    private static String parsePayload(Map<String, String> prop, String requestType, String sessionID, Object idAttrFromValue,String internalId) {
        if (requestType.contains("login")) {
            String payloadUsername = prop.get(requestType + ".payload" + ".username");
            String payloadPassword = prop.get(requestType + ".payload" + ".password");
            String str1 = "{\n" +
                    "    \"username\": \"" + payloadUsername + "\",\n" +
                    "    \"password\": \"" + payloadPassword + "\"\n" +
                    "}";

            return str1;
        } else if(requestType.contains("validate")) {
            String payloadtype = prop.get(requestType + ".payload" + ".@type");
            String payloadUsername = prop.get(requestType + ".payload" + ".userName");
            String payloadSessionID = prop.get(requestType + ".payload" + ".icToken");
            if(payloadSessionID.equals("")){
                payloadSessionID=sessionID;
            }
            String str1="{\n" +
                    "   \"@type\": \""+payloadtype+"\",\n" +
                    "   \"userName\": \""+payloadUsername+"\",\n" +
                    "   \"icToken\": \""+payloadSessionID+"\"\n" +
                    "}   ";
              return str1;
        }else if(requestType.contains("filter")){
            String payloadpageOffSet = prop.get(requestType + ".payload" + ".pageoffset");
            String payloadpageSize = prop.get(requestType + ".payload" + ".pageSize");
            String payloadBE = prop.get(requestType + ".payload" + ".businessEntity");
            Object payloadBID = prop.get(requestType + ".payload" + ".businessId");
            if(payloadBID.equals("")){
                payloadBID=idAttrFromValue;
            }
            if(payloadBE.equals("")){
                payloadBE=internalId;
            }
            String str1="{\n" +
                    "    \"pageoffset\": \""+payloadpageOffSet+"\",\n" +
                    "    \"pageSize\": \""+payloadpageSize+"\",\n" +
                    "    \"filter\": {\n" +
                    "        \"_from\": {\n" +
                    "            \"businessEntity\": \""+payloadBE+"\",\n" +
                    "            \"businessId\": \""+payloadBID+"\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
            return str1;
        }
        return "";
    }

    private static MultiValueMap<String, String> parseHeaders(String[] headersArray, Map<String, String> prop, String requestType, String payload, String token) throws UnsupportedEncodingException {
        MultiValueMap<String,String> headerMap=new LinkedMultiValueMap<>();

        for (String header:headersArray) {
            String headerValues = prop.get(requestType+".headers."+header);
//            System.out.println(headerValues);

            if(header.equals("content-length")){
                int contentlength = payload.getBytes("UTF-8").length;
                headerMap.put(header, Collections.singletonList(String.valueOf(contentlength)));
            }else
            if(header.equals("IDS-SESSION-ID")){
                headerMap.put(header, Collections.singletonList(token));
            }else
            if(headerValues!=null && headerValues!=""){
                String value=headerValues;
                headerMap.put(header, Collections.singletonList(value));
            }
        }
        return headerMap;
    }


}
