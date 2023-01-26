/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;


import org.apache.commons.lang3.SerializationUtils;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UDPTransmission {
    public static class Data {
        public byte[] content;
        public int packetsReceived;
    }

    private static final String HOST = "230.1.1.1";
    private static final int PORT = 4000;
    private static InetAddress GROUP;

    static {
        try {
            GROUP = InetAddress.getByName(HOST);
        } catch (UnknownHostException ex) {
            Logger.getLogger(UDPTransmission.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void sendPacket(DatagramSocket socket, byte[] data, int sequenceNumber, byte ack) throws IOException, PackageNotSentException {
        char tries = 3;

        Packet packetToSend = new Packet(ack, sequenceNumber, data);
        Packet res;
        byte[] packet = packetToSend.getPacket();
        DatagramPacket p = new DatagramPacket(packet, packet.length, GROUP, PORT);
        DatagramPacket response = new DatagramPacket(new byte[Packet.PACKET_SIZE], Packet.PACKET_SIZE);

        while( tries > 0 ) {
//            try {
                socket.send(p);
                System.out.println("Enviando fragmento " + sequenceNumber);
                break;
//                socket.receive(response);
//                res = new Packet(response.getData());
//                System.out.println("Recibiendo fragmento " + res.getSequenceNumber());
//
//                if(res.getACK() == Packet.RECEIVED_ACK && res.getSequenceNumber() == ++sequenceNumber)
//                    break;
//                else tries--;
//            } catch (SocketTimeoutException e) {
//                System.out.println("Received timeout");
//                tries--;
//            } catch (IOException e) {
//                System.out.println(e);
//                e.printStackTrace();
//                System.out.println("Error in sendPacket method, trying again");
//                tries--;
//            }
        }

        if(tries == 0) throw new PackageNotSentException("Package was not sent");
    }

    /**
     * Idea: Return the ack
     * @param sequenceNumber
     */
    public static Packet receivePacket(DatagramSocket socket, int sequenceNumber) throws PackageNotReceivedException {
        byte[] b = new byte[Packet.PACKET_SIZE];

        DatagramPacket p = new DatagramPacket(b, b.length);
        Packet packet = null;

        int tries = 3;
        while(tries > 0) {
            try {
                socket.receive(p);

                packet = new Packet(p.getData());
                System.out.println("Fragmento recibido " + packet.getSequenceNumber());

                if(packet.getSequenceNumber() != sequenceNumber)
                    tries--;
                else break;
//                Sends the confirmation ACK
//                else {
//                    Packet confirmationACK = new Packet(Packet.RECEIVED_ACK, ++sequenceNumber);
//                    byte[] responseB = confirmationACK.getPacket();
//                    DatagramPacket responseDP = new DatagramPacket(
//                            responseB,
//                            responseB.length
//                    );
//                    socket.send(responseDP);
//                    System.out.println("Enviando fragmento " + sequenceNumber);
//                    break;
//                }
            } catch (SocketTimeoutException e) {
                System.out.println("PACKET NOT RECEIVED, TRYING AGAIN");
                tries--;
            } catch (IOException e) {
                tries--;
                System.out.println("Package not received as expected");
                e.printStackTrace();
//                throw new PackageNotReceivedException("Package not received as expected", e);
            }
        }

        if(tries == 0)
            throw new PackageNotReceivedException("Package not received as expected");

        return packet;
    }

    private static byte[] getBytes(Serializable content) {
        return SerializationUtils.serialize(content);
    }

    /**
     * Splits the data into packages and send each packet through the server
     * used for small data
     * @param content
     * @param sequenceNumber number to put in the first packet to send
     * @return Quantity of packets sent
     */
    public static int sendData(DatagramSocket socket, Serializable content, int sequenceNumber) throws PackageNotSentException {
        int packetsSent = 0;
        byte[] data = getBytes(content);
        int bytesToBeRead = data.length;
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

        try {
            while (bytesToBeRead > 0) {
                byte[] packetContent = new byte[
                   bytesToBeRead >= Packet.MAX_CONTENT_SIZE ? Packet.MAX_CONTENT_SIZE : bytesToBeRead
                ];

                bytesToBeRead -= dis.read(packetContent);

                sendPacket(socket, packetContent, sequenceNumber, Packet.ZERO);
                sequenceNumber++;
                packetsSent++;
            }

//            sendPacket(socket, new byte[0], sequenceNumber, Packet.END_CONTENT);
//            sequenceNumber ++;
//            packetsSent++;
        } catch (IOException e) {
            throw new PackageNotSentException("Problem reading/packing the package #" + sequenceNumber);
        } catch (PackageNotSentException e) {
            throw new PackageNotSentException("Unable to send the package #" + sequenceNumber);
        }

        return packetsSent;
    }

    /**
     * Receives packets till get a END_CONTENT as ACK
     * @param sequenceNumber sequence number expected to receive in the
     *                           in the first receoved package
     *
     * @return Quantity of packets that were received and the data receivd
     */
    public static Data receiveData(DatagramSocket socket, int sequenceNumber) throws PackageNotReceivedException {
        Data data = new Data();
        int receivedPackets = 0;
        Packet p;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

//        while( (p = receivePacket(socket, sequenceNumber)).getACK() != Packet.END_CONTENT ) {
//            try {
//                baos.write(p.getContent());
//                sequenceNumber++;
//                receivedPackets++;
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

        try {
            p = receivePacket(socket, sequenceNumber);
            baos.write(p.getContent());
            sequenceNumber++;
            receivedPackets++;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        receivedPackets++;
        data.content = baos.toByteArray();
        data.packetsReceived = receivedPackets++;

        return data;
    }

    /**
     * Sends a file through the socket
     * @param filePath
     * @param sequenceNumber number to put in the first packet to send
     * @return the number of packets sent
     */
    public static int sendFile(DatagramSocket socket, String filePath, int sequenceNumber) throws PackageNotSentException {
        int packetsSent = 0;
        try {
            DataInputStream reader = new DataInputStream(new FileInputStream(filePath));
            int bytesToSend = reader.available();
            while(bytesToSend > 0) {
                byte[] bytes = new byte[ bytesToSend > Packet.MAX_CONTENT_SIZE ?
                        Packet.MAX_CONTENT_SIZE : bytesToSend ];
                bytesToSend -= reader.read(bytes);
                sendPacket(socket, bytes, sequenceNumber, Packet.ZERO);
                sequenceNumber++;
                packetsSent++;
            }

            sendPacket(socket, new byte[0], sequenceNumber, Packet.END_CONTENT);
            packetsSent++;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return packetsSent;
    }

    /**
     *
     * @param socket
     * @param sequenceNumber
     * @param filename
     * @return Quantity of packets sent
     * @throws PackageNotReceivedException
     */
    public static int receiveFile(DatagramSocket socket, int sequenceNumber, String filename) throws PackageNotReceivedException {
        int packetsReceived = 0;
        Packet packet;

        try(FileOutputStream outputStream = new FileOutputStream(filename);) {
            while( (packet = receivePacket(socket, sequenceNumber)).getACK() != Packet.END_CONTENT ) {
                outputStream.write(packet.getContent());
                sequenceNumber++;
                packetsReceived++;
            }

            packetsReceived++;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("ARCHIVO RECIBIDO 100%: " + filename);
        return packetsReceived;
    }

    public static int receiveFileAndExtension(DatagramSocket socket, int sequenceNumber, String filename) throws PackageNotReceivedException {
        UDPTransmission.Data data = UDPTransmission.receiveData(socket, sequenceNumber);
        sequenceNumber += data.packetsReceived;
        String extension = SerializationUtils.deserialize(data.content);

        sequenceNumber += receiveFile(socket, sequenceNumber, filename + "." + extension);

        return  sequenceNumber;
    }
}

