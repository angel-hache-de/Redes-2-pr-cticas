package network;


import org.apache.commons.lang3.SerializationUtils;
import utils.Utils;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;


public class UDPTransmission {
    public static class Data {
        public byte[] content;
        public int packetsReceived;
    }

    public static void sendPacket(DatagramSocket socket, byte[] data, int sequenceNumber, byte ack) throws IOException, PackageNotSentException {
        char tries = 3;

        Packet packetToSend = new Packet(ack, sequenceNumber, data);
        Packet res;
        byte[] packet = packetToSend.getPacket();
        DatagramPacket p = new DatagramPacket(packet, packet.length);
        DatagramPacket response = new DatagramPacket(new byte[Packet.PACKET_SIZE], Packet.PACKET_SIZE);

        while( tries > 0 ) {
            try {
                socket.send(p);
                System.out.println("Enviando fragmento " + sequenceNumber);

                socket.receive(response);
                res = new Packet(response.getData());
                System.out.println("Recibiendo fragmento " + res.getSequenceNumber());

                if(res.getACK() == Packet.RECEIVED_ACK && res.getSequenceNumber() == ++sequenceNumber)
                    break;
                else tries--;
            } catch (SocketTimeoutException e) {
                System.out.println("Received timeout");
                tries--;
            } catch (IOException e) {
//                throw new RuntimeException(e);
                System.out.println(e);
                e.printStackTrace();
                System.out.println("Error in sendPacket method, trying again");
                tries--;
            }
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
//                Sends the confirmation ACK
                else {
                    Packet confirmationACK = new Packet(Packet.RECEIVED_ACK, ++sequenceNumber);
                    byte[] responseB = confirmationACK.getPacket();
                    DatagramPacket responseDP = new DatagramPacket(
                            responseB,
                            responseB.length
                    );
                    socket.send(responseDP);
                    System.out.println("Enviando fragmento " + sequenceNumber);
                    break;
                }
            } catch (SocketTimeoutException e) {
                System.out.println("PACKET NOT RECEIVED, SENDING AGAIN");
                tries--;
            } catch (IOException e) {
                throw new PackageNotReceivedException("Package not received as expected", e);
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
                sequenceNumber += 2;
                packetsSent ++;
            }

            sendPacket(socket, new byte[0], sequenceNumber, Packet.END_CONTENT);
            sequenceNumber += 2;
            packetsSent ++;
        } catch (IOException e) {
            throw new PackageNotSentException("Problem reading/packing the package #" + sequenceNumber);
        } catch (PackageNotSentException e) {
            throw new PackageNotSentException("Unable to send the package #" + sequenceNumber);
        }

        return packetsSent << 1;
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

        while( (p = receivePacket(socket, sequenceNumber)).getACK() != Packet.END_CONTENT ) {
            try {
                baos.write(p.getContent());
                sequenceNumber += 2;
                receivedPackets++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        receivedPackets++;
        data.content = baos.toByteArray();
        data.packetsReceived = receivedPackets << 1;

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
           cartButton1ActionPerformed     sendPacket(socket, bytes, sequenceNumber, Packet.ZERO);
                sequenceNumber += 2;
                packetsSent++;
            }

            sendPacket(socket, new byte[0], sequenceNumber, Packet.END_CONTENT);
            packetsSent++;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return packetsSent << 1;
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
                sequenceNumber += 2;
                packetsReceived++;
            }

            packetsReceived++;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("ARCHIVO RECIBIDO 100%: " + filename);
        return packetsReceived << 1;
    }

    /**
     * Receives a file and its type, eg. mp3
     * @param filename w/o extension
     * @return The amount of packets received and the name of the received file
     */
    public static Data receiveFileAndExtension(DatagramSocket socket, int sequenceNumber, String filename, String folder) throws PackageNotReceivedException {
        UDPTransmission.Data dataToReturn = new Data();

        UDPTransmission.Data data = UDPTransmission.receiveData(socket, sequenceNumber);
        dataToReturn.packetsReceived = data.packetsReceived;
        sequenceNumber += data.packetsReceived;
        String extension = SerializationUtils.deserialize(data.content);

        dataToReturn.packetsReceived += receiveFile(socket, sequenceNumber, folder + "/" + filename + "." + extension);
        dataToReturn.content = new String(filename + "." + extension).getBytes();
        return dataToReturn;
    }

    /**
     * Get the extension from the file to send
     * Send the extension
     * Send the file
     * @return number of packets sent
     */
    public static int sendFileAndExtension(DatagramSocket socket, String filePath, int sequenceNumber) throws PackageNotSentException {
        int packetsSent = 0;

        packetsSent += UDPTransmission.sendData(socket, Utils.getExtension(filePath), sequenceNumber);
        sequenceNumber += packetsSent;

        packetsSent += UDPTransmission.sendFile(socket, filePath, sequenceNumber);

        return packetsSent;
    }
}
