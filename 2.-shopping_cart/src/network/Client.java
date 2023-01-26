package network;

import model.*;
import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Client {
    private static final int port = 1234;
    private static final String dir = "127.0.0.1";
    private static final InetAddress dst;

    static {
        try {
            dst = InetAddress.getByName(dir);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static DatagramSocket initSocket() throws SocketException {
        DatagramSocket client = new DatagramSocket();
        client.connect(dst, port);
//        Establish 2 seconds to receive packets
        client.setSoTimeout(2000);
        return client;
    }

    private static DatagramSocket initSocketAndCommunication(int option) throws IOException, PackageNotSentException {
        DatagramSocket client = initSocket();
        initCommunication(client, option);
        return client;
    }

    public static Optional<User> sendLoginRequest(User user) throws IOException, PackageNotSentException, PackageNotReceivedException {
        DatagramSocket client = initSocketAndCommunication(3);

//        First two numbers were used establishing connection
        int sequenceNumber = 2;

        sequenceNumber += UDPTransmission.sendData(client, user, sequenceNumber);

//        Gets the user ID
        UDPTransmission.Data data = UDPTransmission.receiveData(client, sequenceNumber);

        User userReceived = SerializationUtils.deserialize(data.content);

        client.close();

        if(userReceived.getId() == -1) return Optional.empty();
        return Optional.of(userReceived);
    }

    public static Optional<User> sendSignupRequest(User user) throws IOException, PackageNotSentException, PackageNotReceivedException {
        DatagramSocket client = initSocketAndCommunication(4);

//        First two numbers were used establishing connection
        int sequenceNumber = 2;
        sequenceNumber += UDPTransmission.sendData(client, user, sequenceNumber);

//        Gets the user ID
        UDPTransmission.Data data = UDPTransmission.receiveData(client, sequenceNumber);
        User userReceived = SerializationUtils.deserialize(data.content);

        client.close();

        if(userReceived.getId() == -1) return Optional.empty();
        return Optional.of(userReceived);
    }

    
    public static List<Product> sendGetSongsRequest() throws IOException, PackageNotSentException, PackageNotReceivedException {
        List<Product> songs;
        try(DatagramSocket client = initSocketAndCommunication(1)) {
    //        First two numbers were used establishing connection
            int sequenceNumber = 2;
            UDPTransmission.Data data = UDPTransmission.receiveData(client, sequenceNumber);

            songs = SerializationUtils.deserialize(data.content);
        }
        return songs;
    }
    
    public static void sendTransactionRequest(ShippingCart cart, int userId) throws IOException, PackageNotSentException, PackageNotReceivedException {
        List<LineItem> items = cart.getLineItems();

        try(DatagramSocket client = initSocketAndCommunication(2)) {
            //The 0-1 number were used to send the menu option
            int sequenceNumber = 2;

            sequenceNumber += sendId(client, userId, sequenceNumber);
            List<NetworkLineItem> itemsToSend = items.stream().map(NetworkLineItem::new).collect(Collectors.toList());

            sequenceNumber += UDPTransmission.sendData(client, (Serializable) itemsToSend, sequenceNumber);

            for(LineItem item: items) {
//                System.out.println("INSIDE RECEIVING: " + item.getProduct().getName());
                UDPTransmission.Data data = UDPTransmission.receiveFileAndExtension(client, sequenceNumber,
                    item.getProduct().getName(), "media2"
                );
                sequenceNumber += data.packetsReceived;
            }
        }
    }
    
    private static int sendId(DatagramSocket socket, int userId, int sequenceNumber) throws PackageNotSentException {
        Integer data = userId;
        return UDPTransmission.sendData(socket, data, sequenceNumber);
    }
    
    private static void initCommunication(DatagramSocket socket, int option) throws IOException, PackageNotSentException {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream daos = new DataOutputStream(baos)) {

            daos.writeInt(option);
            daos.flush();

            UDPTransmission.sendPacket(
                    socket, baos.toByteArray(), 0, Packet.INIT_CON
            );
        }
    }

    public static String sendDeleteProductRequest(int productId) throws IOException, PackageNotSentException, PackageNotReceivedException {
        try(DatagramSocket client = initSocketAndCommunication(7)) {
            //The 0-1 number were used to send the menu option
            int sequenceNumber = 2;

            sequenceNumber += sendId(client, productId, sequenceNumber);

            UDPTransmission.Data receiveData = UDPTransmission.receiveData(client, sequenceNumber);
            return new String(receiveData.content);
        }
    }

    public static List<Transaction> sendGetTransactionsRequest(int userId) throws PackageNotSentException, IOException, PackageNotReceivedException {
        List<Transaction> transactions;
        try(DatagramSocket client = initSocketAndCommunication(8)) {
            //The 0-1 number were used to send the menu option
            int sequenceNumber = 2;

            sequenceNumber += sendId(client, userId, sequenceNumber);

            UDPTransmission.Data data = UDPTransmission.receiveData(client, sequenceNumber);
            transactions = SerializationUtils.deserialize(data.content);
        }
        return transactions;
    }

    public static String sendCreateProductRequest(Product product, String filePath) throws PackageNotSentException, IOException, PackageNotReceivedException {
        String response;
        try(DatagramSocket client = initSocketAndCommunication(5)) {
            //The 0-1 number were used to send the menu option
            int sequenceNumber = 2;

            sequenceNumber += UDPTransmission.sendData(client, product, sequenceNumber);
            sequenceNumber += UDPTransmission.sendFileAndExtension(client, filePath, sequenceNumber);

            UDPTransmission.Data data = UDPTransmission.receiveData(client, sequenceNumber);
            response = SerializationUtils.deserialize(data.content);
        }
        return response;
    }

    /**
     * Sends a request to update a product
     * @param product with the new info
     * @param filePath if the song is gonna be updated, the path of the new song file that is gonna be sent
     * @param option Indicates what is has to be updated
     *      0   -> just updates the product
 *          1  --> update the image
 *          2 --> receives a new song
 *          3 --> update both the image and song
     */
    public static String sendUpdateProductRequest(Product product, String filePath, int option) throws PackageNotSentException, IOException, PackageNotReceivedException {
        String response;
        try(DatagramSocket client = initSocketAndCommunication(6)) {
            //The 0-1 number were used to send the menu option
            int sequenceNumber = 2;

            sequenceNumber += UDPTransmission.sendData(client, option, sequenceNumber);
            sequenceNumber += UDPTransmission.sendData(client, product, sequenceNumber);

            if(option == 2 || option == 3)
                sequenceNumber += UDPTransmission.sendFileAndExtension(client, filePath, sequenceNumber);

            UDPTransmission.Data data = UDPTransmission.receiveData(client, sequenceNumber);
            response = SerializationUtils.deserialize(data.content);
        }
        return response;
    }
}
