/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;


import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author angel
 */
public class Client {
    private final MulticastSocket client;
    private final InetAddress group;
//    private final EscuchaMensajes listenMessages;
//    private final Thread listenThread;

    public Client(String host, int port) {
        try {
            client = new MulticastSocket(port);
            group = InetAddress.getByName(host);
            client.joinGroup(group);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Utils.createFolder("files");
    }
    
    public MulticastSocket getSocket() {
        return client;
    }
    
    private class SendMessage implements Runnable {
        private Message message;
        private String fileAbsolutePath;

        private SendMessage(Message message) {
            this.message = message;
        }

        private SendMessage(Message message, String fileAbsolutePath) {
            this.message = message;
            this.fileAbsolutePath = fileAbsolutePath;
        }

        public void run() {
            try {
                int sequenceNumber = 0;

                sequenceNumber += UDPTransmission.sendData(client, message, sequenceNumber);

                if(message.getType() == Message.SEND_FILE)
                    UDPTransmission.sendFile(client, fileAbsolutePath, sequenceNumber);

            } catch (PackageNotSentException ex) {
                ex.printStackTrace();
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void sendMessage(Message message) {
        new Thread(new SendMessage(message)).start();
    }
    
    public void sendFile(Message message, String fileAbsolutePath) {
        new Thread(new SendMessage(message, fileAbsolutePath)).start();
    }

    public void receiveFile(String filename) throws PackageNotReceivedException {
        System.out.println("RECEIVING FILEEEEE");
        UDPTransmission.receiveFile(client, 1, filename);
    }
}
