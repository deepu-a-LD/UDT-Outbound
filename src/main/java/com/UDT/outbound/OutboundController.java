package com.UDT.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.UDT.outbound.SessionCheck.check;

@RestController
public class OutboundController {

    @Autowired
    private SnapshotService snapshotService;
    @Autowired
    private RelationshipService relationshipService;

    static String value;

    static {
        try {
            value = check();
        } catch (UnsupportedEncodingException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/BE")
    public ResponseEntity<String> receiveCustomerJson(@RequestBody String record) throws Exception {
        snapshotService.createSnapCustomer(record);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/fleet")
    public ResponseEntity<String> receiveFleetRelationJson(@RequestBody String record) throws IOException {
        relationshipService.createSnap(record);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/organizationRelationship")
    public ResponseEntity<String> receiveOrganizationRelationshipJson(@RequestBody String record) throws IOException {
        relationshipService.createSnap(record);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/organization")
    public ResponseEntity<String> receiveOrganizationJson(@RequestBody String record) throws IOException {
        snapshotService.createSnapOrganization(record);
        return new ResponseEntity<>(HttpStatus.OK);
    }





}

