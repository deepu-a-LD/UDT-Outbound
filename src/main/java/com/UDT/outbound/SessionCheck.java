package com.UDT.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import static com.UDT.outbound.HeadersService.loginSaaSMDM;


public class SessionCheck {

    @Autowired
    static
    HeadersService serv;

    static Map<String, String> sessions;

    static String time;

    @Value("${milliSec}")
    public static Long milliSec;

    public static String check() throws UnsupportedEncodingException, JsonProcessingException {
        String token = loginSaaSMDM();
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
        time = formatter.format(new Date());
        System.out.println(time);
        sessions = new HashMap<>();
        sessions.put(time, token);
        System.out.println(sessions);
        System.out.println(token);
//        return token;
        for (Map.Entry<String, String> val : sessions.entrySet()) {
            return val.getValue();
        }
//        return sessions.get(time);
        return "";
    }

    public static String validateToken() throws UnsupportedEncodingException, JsonProcessingException {
        DateFormat formatter = new SimpleDateFormat("hh:mm:ss");
        String time1 = formatter.format(new Date());
        System.out.println(time1);
        Long difference = null;
//        String TimeTaken = "";
        for (Map.Entry<String, String> a : sessions.entrySet()) {
            Date date1 = null;
            Date date2 = null;
            try {
                date1 = formatter.parse(a.getKey());
                date2 = formatter.parse(time1);
                difference = date2.getTime() - date1.getTime();
                System.out.println(difference);
             /*   TimeTaken = String.format("%s.%s.%s",
                        Long.toString(TimeUnit.MILLISECONDS.toHours(difference)),
                        TimeUnit.MILLISECONDS.toMinutes(difference),
                        TimeUnit.MILLISECONDS.toSeconds(difference);
                System.out.println(String.format("Time taken %s", TimeTaken));*/
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
//        300000L;  // 5min

        if (difference > milliSec) {
            String token = loginSaaSMDM();
            DateFormat formatter1 = new SimpleDateFormat("hh:mm:ss");
            String time2 = formatter1.format(new Date());
            System.out.println(time2);
            sessions.clear();
            System.out.println(sessions);
            sessions.put(time2, token);
            System.out.println(sessions);
            for (Map.Entry<String, String> val : sessions.entrySet()) {
                return val.getValue();
            }
        } else {
            System.out.println("valid Token");
            for (Map.Entry<String, String> val : sessions.entrySet()) {
                return val.getValue();
            }
        }
        return "";
      /*  String timeUntilExpire = "00.29.00";
        long diff = Long.parseLong(TimeTaken);
        long timeExpire = Long.parseLong(timeUntilExpire);

        if (diff > timeExpire) {
            System.out.println("valid Token");
            return sessions.get(time);
        } else {
            String token = loginSaaSMDM();
            DateFormat formatter1 = new SimpleDateFormat("hh:mm:ss");
            String time2 = formatter1.format(new Date());
            System.out.println(time2);
            sessions.put(time2, token);
            System.out.println(sessions.get(time2));
            return sessions.get(time2);
        }*/
    }


   /* public static String workflow() throws UnsupportedEncodingException, JsonProcessingException {
        String checkSession=value;
        Object expireTIME;
        String sessionId;
        if(checkSession==null){
            sessionId =loginSaaSMDM();
            return sessionId;
        }else{
            expireTIME=serv.validateSessionId(checkSession);
            int i = (int) expireTIME;
            if(i>1){
                sessionId=checkSession;
                return sessionId;
            }else{
                boolean b=serv.logoutSaaSMDM(checkSession);
                if(b){
                    sessionId = loginSaaSMDM();
                    return sessionId;
                }
            }
        }
        return "";
    }*/
}
