package network;

import dao.*;
import model.NetworkLineItem;
import model.Product;
import model.Transaction;
import model.User;
import org.apache.commons.lang3.SerializationUtils;
import utils.Utils;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Server {
    private final DatagramSocket  server;

    private final ProductDAO productDAO;
    private final LoginDAO loginDAO;
    private final TransactionDAO transactionDAO;
    private final UserDAO userDAO;

    /**
     * Control the messages flow in the communication between the
     * server and the client. Indicates the number of packet that
     * is going to be sent or received.
     * Is reset when a new client connects.
     */
    private int sequenceNumber = 0;

    public Server(int port, ProductDAO pdao, LoginDAO ldao, TransactionDAO tdao, UserDAO udao) throws IOException {
        server = new DatagramSocket(port);
        server.setReuseAddress(true);

        this.userDAO = udao;
        this.productDAO = pdao;
        this.loginDAO = ldao;
        this.transactionDAO = tdao;
    }

    public void listen() {
        System.out.println("Server listening");

        for(;;) {
            try {
                sequenceNumber = 0;
                //    Add timeout to receive packets
                server.setSoTimeout(0);
                Packet packet = initConnection();
                server.setSoTimeout(2000);

                int option;
                try(DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.getContent()))) {
                    option = dis.readInt();
                }

//                TODO: switch to enum
                switch (option) {
                    case 1:
//                        send products
                        sendProducts();
                        break;
                    case 2:
//                        register transaction
                        registerTransaction();
                        break;
                    case 3:
//                        login
                        login();
                        break;
                    case 4:
//                        signup
                        signup();
                        break;
                    case 5:
//                        create product
                        createProduct();
                        break;
                    case 6:
//                        update product
                        updateProduct();
                        break;
                    case 7:
//                        delete product
                        deleteProduct();
                        break;
                    case 8:
//                        send user transactions
                        sendUserTransactions();
                        break;
                    default:
                        break;
                }

//              To receive from anyone
                server.disconnect();
            } catch (IOException e) {
//                TODO better exception handling
                e.printStackTrace();
            } catch (PackageNotSentException e) {
//                TODO better exception handling
                System.out.println("Error sending packet");
                e.printStackTrace();
            } catch (PackageNotReceivedException e) {
//                TODO better exception handling
                System.out.println("Error receiving packet");
                e.printStackTrace();
            }
        }
    }

    /**
     * Receives the first packet with INIT CON as ACK
     * and respond with a packet with CONNECTION ESTABLISHED as ACK
     * @return Packet received
     * @throws IOException
     */
    private Packet initConnection() throws IOException {
        byte[] b = new byte[65535];
        DatagramPacket p = new DatagramPacket(b, b.length);
        Packet packet = null;

        while(packet == null || packet.getACK() != Packet.INIT_CON) {
            server.receive(p);
            packet = new Packet(p.getData());
        }
//      To Only receive from this client
        server.connect(p.getAddress(), p.getPort());
        sequenceNumber++;

//      Answer the connection request
        Packet responsePacket = new Packet(Packet.RECEIVED_ACK, sequenceNumber);
        byte[] packetBytes = responsePacket.getPacket();
        DatagramPacket pResponse = new DatagramPacket(packetBytes, packetBytes.length);
        server.send(pResponse);
        sequenceNumber++;

        return packet;
    }

    /**
     * Receives a packet that contains an ID
     * @return the ID
     */
    private int receiveId() throws PackageNotReceivedException {
        UDPTransmission.Data data = UDPTransmission.receiveData(server, sequenceNumber);
        Integer id = SerializationUtils.deserialize(data.content);
        sequenceNumber += data.packetsReceived;

        return id;
    }

    /**
     * Sends through the socket the product list
     * @throws PackageNotSentException
     */
    private void sendProducts() throws PackageNotSentException {
        List<Product> products = productDAO.selectAll();

//        Resize the images that are going to be sent
        products = products.stream().sorted(Comparator.reverseOrder()).peek(
            product -> product.setImage(Utils.resizeImage(product.getImage()))
        ).collect(Collectors.toList());

//        Send the products
        sequenceNumber += UDPTransmission.sendData(server, (Serializable) products, sequenceNumber);
    }

    /**
     * Receives a LineItem arraylist through the socket
     * and stores the transaction into db.
     * Then the bought songs are sent
     */
    private void registerTransaction() throws PackageNotReceivedException, PackageNotSentException {
//       receive the id
        int userId = receiveId();

//       receive the items to buy
        UDPTransmission.Data data = UDPTransmission.receiveData(server, sequenceNumber);
        sequenceNumber += data.packetsReceived;
        List<NetworkLineItem> items = SerializationUtils.deserialize(data.content);

        productDAO.getProductsPrice(items);
        transactionDAO.insertTransaction(items, userId);
        items.forEach(item -> productDAO.updateDownloadsNumber(item.getProductId()));

//        Send the songs
        for (NetworkLineItem song : items) {
            String[] productPaths = productDAO.getProductPaths(song.getProductId());
            System.out.println("Sending song: " + productPaths[1]);

            sequenceNumber += UDPTransmission.sendFileAndExtension(server, "media/"+productPaths[1], sequenceNumber);
        }
    }

    /**
     * LOGIN
     * The client sends a User object
     * the servers sends the client id or -1
     * Client ends the connection
     */
    private void login() throws PackageNotReceivedException, PackageNotSentException {
        UDPTransmission.Data data = UDPTransmission.receiveData(server, sequenceNumber);
        sequenceNumber += data.packetsReceived;

        User u = SerializationUtils.deserialize(data.content);

        boolean validCredentials = loginDAO.login(u);
        if(!validCredentials) u.setId(-1);

//        TODO return a token
        sequenceNumber += UDPTransmission.sendData(server, u, sequenceNumber);
    }

    /**
     * SIGNUP
     * The client sends a User object
     * the servers sends the client id or error message
     * Client ends the connection
     */
    private void signup() throws PackageNotReceivedException, PackageNotSentException {
        UDPTransmission.Data data = UDPTransmission.receiveData(server, sequenceNumber);
        sequenceNumber += data.packetsReceived;
        User u = SerializationUtils.deserialize(data.content);
//        u.setId(-1);
        loginDAO.signUp(u);

//        TODO return a token
        sequenceNumber += UDPTransmission.sendData(server, u, sequenceNumber);
    }

    /**
     * Create product
     * The client sends the product w/o id
     * The client sends the song
     * The server sends a confirmation message
     *
     */
    private void createProduct() throws PackageNotReceivedException, PackageNotSentException, IOException {
        UDPTransmission.Data data = UDPTransmission.receiveData(server, sequenceNumber);
        sequenceNumber += data.packetsReceived;
        Product product = SerializationUtils.deserialize(data.content);

//        Receives the song
        data = UDPTransmission.receiveFileAndExtension(server, sequenceNumber, Utils.generateRandomName(), "media");
        sequenceNumber += data.packetsReceived;

//        creates the image
        String imageName = Utils.saveImage(product.getImage(), Utils.generateRandomName() + ".jpg");

//        TODO error handling where deleting the files created if something fails
        System.out.println("SAVING: " + product);
        System.out.println("IMAGE CREATED: " + imageName);
        System.out.println("SONG FILE: " + new String(data.content));
        int id = productDAO.insertProduct(product, imageName, new String(data.content));
        System.out.println("INSERTED ID: " + id);
        sequenceNumber += UDPTransmission.sendData(server, "Song saved", sequenceNumber);
    }

    /**
     * Update product
     * The client sends an integer:
     *      0   -> just updates the product
 *          1  --> update the image
 *          2 --> receives a new song
 *          3 --> update both the image and song
     *          NOTE: The product is always overwritten
     */
    private void updateProduct() throws PackageNotReceivedException, PackageNotSentException, IOException {
        String[] productPaths = new String[3];

//        Receives the integer that indicates what is going to be sent
        UDPTransmission.Data data = UDPTransmission.receiveData(server, sequenceNumber);
        sequenceNumber += data.packetsReceived;
        Integer option = SerializationUtils.deserialize(data.content);

        data = UDPTransmission.receiveData(server, sequenceNumber);
        sequenceNumber += data.packetsReceived;
        Product product = SerializationUtils.deserialize(data.content);

        if(option != 0) {
            productPaths = productDAO.getProductPaths(product.getId());
            System.out.println("PATHS OBTAINED");
        }

//       if we have to update the song image
        if(option == 1 || option == 3) {
            Utils.saveImage(product.getImage(), productPaths[0]);
            System.out.println("IMAGE OVERWRITTEN");
        }


//       if we have to update the song file
        if(option == 2 || option == 3) {
            System.out.println("DELETING: " + productPaths[1]);
            Utils.deleteSong(productPaths[1]);

            data = UDPTransmission.receiveFileAndExtension(server, sequenceNumber, Utils.generateRandomName(), "media");
            sequenceNumber += data.packetsReceived;

            productPaths[1] = new String(data.content);
            System.out.println("SONG CREATED: " + productPaths[1]);
        }

        productDAO.updateProduct(product);
        if(option != 0)
            productDAO.updateProductPaths(product, productPaths[0], productPaths[1]);

        sequenceNumber += UDPTransmission.sendData(server, "Song modified", sequenceNumber);
    }


    /**
     * Delete product
     * The admin sends the song
     */
    private void deleteProduct() throws PackageNotReceivedException, PackageNotSentException {
        UDPTransmission.Data data =  UDPTransmission.receiveData(server, sequenceNumber);
        sequenceNumber += data.packetsReceived;
        
        Integer productId = SerializationUtils.deserialize(data.content);

        String[] productPaths = productDAO.getProductPaths(productId);

//      remove the song and its image
        if(!Utils.deleteImage(productPaths[0]) || !Utils.deleteSong(productPaths[1])){
//            TODO handle the error
        }

        productDAO.deleteProduct(productId);

        sequenceNumber += UDPTransmission.sendData(server, "Song deleted", sequenceNumber);
    }

    /**
     * Receives the user id
     * Sends an array list with the transactions array
     */
    private void sendUserTransactions() throws PackageNotReceivedException, PackageNotSentException {
        int userId = receiveId();

        List<Transaction> userTransactions = transactionDAO.getUserTransactions(userId);

//        Resizes the images that are going to be sent
        userTransactions.forEach(transaction -> {
            transaction.getCart().getLineItems().forEach(lineItem -> {
                Product p = lineItem.getProduct();
                p.setImage(Utils.resizeImage(p.getImage()));
            });
        });

        sequenceNumber += UDPTransmission.sendData(server, (Serializable) userTransactions, sequenceNumber);
    }
}
