package com.chat.crazy.front.api.sms;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

public class SendMessageClient {

    public static void main(String[] args) throws IOException, ParseException {
        
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid);
    }
}
