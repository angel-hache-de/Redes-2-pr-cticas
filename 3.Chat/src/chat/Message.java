/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.Serializable;

/**
 *
 * @author angel
 */
public class Message implements Serializable {
//    MESSAGE TYPES
    public static final byte CONNECTED_USER = 1;
    public static final byte DISCONNECTED_USER = 2;
    public static final byte CONNECTED_USER_RESPONSE = 3;
    public static final byte SEND_FILE = 4;
    public static final byte TEXT = 5;

    private int type;
    private String payload;
    private String sender;
    private String receiver;
    
    public Message(int type, String payload, String sender) {
        this.type = type;
        this.payload = payload;
        this.sender = sender;
        this.receiver = "";
    }

    public Message(int type, String payload, String sender, String receiver) {
        this.type = type;
        this.payload = payload;
        this.sender = sender;
        this.receiver = receiver;
    }

    
    
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
    
    @Override 
    public String toString() {
        return "TYPE: " + type +
                "\nFROM: " + sender +
                "\nTO: " + receiver +
                "\n PAYLOAD: " + payload;
    }
}
