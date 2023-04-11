package com.UDT.outbound;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsOperations;

import static junit.framework.TestCase.assertEquals;

public class SnapshotServiceTest {

    @Autowired
    SnapshotService snapService;

    @Autowired
    JmsOperations jmsOperations;
    @Test
    public void createRecord() throws Exception {
    String record="{\t\"X_UDT_KnownAs\": [\n" +
            "\t\t{\n" +
            "\t\t\t\"_id\": \"e28c859829e145b696288cdbd05f5f2f\",\n" +
            "\t\t\t\"X_UDT_applicationType\": {\n" +
            "\t\t\t\t\"Code\": \"SALES FORCE\",\n" +
            "\t\t\t\t\"Name\": \"Sales force\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"X_UDT_applicationID\": \"csdvs\"\n" +
            "\t\t}\n" +
            "\t],\n" +
            "    \"x_udt_custmarkt_cust_org\": {\n" +
            "        \"businessId\": \"MDM000000IGFR8\"\n" +
            "    },\n" +
            "\t\"x_udt_custsvcdlr_cust_org\": {\n" +
            "        \"businessId\": \"MDM000000IGFR6\"\n" +
            "    },\n" +
            "\t\"x_udt_custslsdlr_cust_org\": {\n" +
            "        \"businessId\": \"MDM000000IGFR6\"\n" +
            "    },\n" +
            "\t\"x_udt_custlgl_cust_cust\": {\n" +
            "        \"businessId\": \"MDM000000IGFR6\"\n" +
            "    }\n" +
            "}";
    snapService.createSnapCustomer(record);

   String msg= String.valueOf(jmsOperations.receive("queue_SA"));
   String expected="";
   assertEquals(expected,msg);

    }
}
