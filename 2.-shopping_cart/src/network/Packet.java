package network;

import java.io.*;

public class Packet {
//    TODO correct the size
//    TODO Change this for a enum
    public static final int PACKET_SIZE = 65000;
    public static final int HEADERS_SIZE = 9;
    public static final int  MAX_CONTENT_SIZE = PACKET_SIZE - HEADERS_SIZE;
    public static final byte INIT_CON = 1;
    public static final byte CON_ESTABLISHED = 6;
    public static final byte END_CON = 2;
    public static final byte END_CONTENT = 5;
    public static final byte REQ_RESENT_LST_PACK = 3;
    public static final byte RECEIVED_ACK = 4;
    public static final byte ZERO = 0;

    private byte ack;
    private int sequenceNumber; //4 bytes
    private byte[] data;

    private int contentLength;

    /**
     * Unpack the ack, content length and data
     * @param data
     */
    public Packet(byte[] data) throws IOException {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data))) {
            ack = dis.readByte();
            sequenceNumber = dis.readInt();
            contentLength = dis.readInt();
            this.data = new byte[contentLength];
            dis.read(this.data);
        }

//        this.ack = getACK(data);
//        this.sequenceNumber = getSequenceNumber(data);
//        getPacketContent(data);
    }

    public Packet(byte ack, int sequenceNumber, byte[] data) {
        this.ack = ack;
        this.sequenceNumber = sequenceNumber;
        this.contentLength = data.length;
        this.data = data;
    }

    public Packet(byte ack, int sequenceNumber) {
        this(ack, sequenceNumber, new byte[0]);
    }

    public Packet(byte ack) {
        this(ack, 0, new byte[0]);
    }

    /**
     * Pack together all the info
     * 0 --> ACK
     * 1-4 --> Sequence
     * 5-8 --> Content length
     * 8- --> Content
     * @return byte array with the ack, sequenceNumber and the content
     */
    public byte[] getPacket() throws IOException {
        byte[] packet;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {
            dos.writeByte(ack);
            dos.writeInt(sequenceNumber);
            dos.writeInt(contentLength);
            dos.write(data);

            packet = baos.toByteArray();
        }

        return packet;
    }

//    private void putSeqNumIntoPacket(byte[] packet) {
//        ByteBuffer b = ByteBuffer.allocate(4);
//        b.putInt(sequenceNumber);
//        System.arraycopy(b.array(), 0, packet, 1, 4);
//    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setACK(byte ack) { this.ack = ack; }

    public void setData(byte[] data) { this.data = data; }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public byte getACK() { return ack; }

    public byte[] getContent() { return data; }
}
