/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.net.DatagramSocket;

import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author angel
 */
public class ReceiveMessage extends Observable implements Runnable {
    private final DatagramSocket socket;

    public ReceiveMessage(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while(true) {
            try {
                int sequenceNumber = 0;
                UDPTransmission.Data data = UDPTransmission.receiveData(socket, sequenceNumber);
                sequenceNumber += data.packetsReceived;
    //                        Message message = SerializationUtils.deserialize(data.content);
                Message message = SerializationUtils.deserialize(data.content);

                if(message.getType() == Message.SEND_FILE)
                    UDPTransmission.receiveFile(socket, sequenceNumber, "files/" + message.getPayload());

                setChanged();
                notifyObservers(message);
            } catch (PackageNotReceivedException ex) {
                    Logger.getLogger(ReceiveMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    }
}
