/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

/**
 *
 * @author angel
 */

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

public class ChatView extends JFrame implements Observer {
    private static final long serialVersionUID = 2L;

    private final MessageService messageService;
    private final Client client;
    
    private boolean receiveConnectResponses;

    public ChatView(String host, int puerto, String username, Client client) {
        messageService = new MessageService();
        receiveConnectResponses = true;
        // -----------------Recibiendo Parametros------------------//

        this.username = username;
        this.client = client;

        emojis.put(":sonrisa:",   "./emojis/sonrisa.png");
        emojis.put(":enojado:",   "./emojis/enojado.png");
        emojis.put(":enfermo:",   "./emojis/enfermo.png");
        emojis.put(":vaquero:",   "./emojis/vaquero.png");
        emojis.put(":lentes:",    "./emojis/lentes.png");
        emojis.put(":lengua:",    "./emojis/lengua.png");
        emojis.put(":llorando:",  "./emojis/llorando.png");
        emojis.put(":enamorado:", "./emojis/enamorado.png");
        emojis.put(":risa:",      "./emojis/risa.png");

        initComponents();
    }

    private void putButtons() {
        emojis.keySet().forEach((text) -> {
            BufferedImage buttonIcon;
            try {
//                String imgsrc = Cliente.class.getClassLoader().getResource(emojis.get(text)).toString();
                buttonIcon = ImageIO.read(new File(emojis.get(text)));
                JButton button = new JButton(Utils.resizeIcon(new ImageIcon(buttonIcon)));

//            JButton button = new JButton(text);

                button.addActionListener(new AppendEmoji(text));
                emojisPanel.add(button);
            } catch (IOException ex) {
                Logger.getLogger(ChatView.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private String putEmojiImage(String message) {
        Pattern pattern = Pattern.compile("\\:\\w+\\:");

        StringBuilder sb = new StringBuilder(message);
        Matcher matcher = pattern.matcher(sb);

        while (matcher.find()) {
            String imgsrc = ChatView.class.getClassLoader().getResource(emojis.get(matcher.group())).toString();

            sb.replace(
                matcher.start(),
                matcher.end(),
                "<img src = '" + imgsrc + "' width = 25 height = 25 />"
            );
            matcher = pattern.matcher(sb);
        }

        return sb.toString();
    }
    
    private void showMessages(List<Message> messages) {
        clearMessages();

        messages.forEach((message) -> {
            printMessage(message, 
                message.getSender().equals(username)
                ? ownMessageTemplate
                : othersMessageTemplate
            );
        });
    }

    @Override
    public void update(Observable obj, Object arg) {
        if (!(arg instanceof Message)) return;
        
        Message message = (Message) arg;

        System.out.println(message);
        
        switch(message.getType()){
            case Message.CONNECTED_USER:
                if(message.getSender().equals(username)) break;

                receiveConnectResponses = false;
                printOrNotifyAlert(message.getSender() + " has joined", "");

                Message m = new Message(Message.CONNECTED_USER_RESPONSE, "", username, message.getSender());
                connectedUsers.addItem(message.getSender());
                client.sendMessage(m);
                break;
            case Message.CONNECTED_USER_RESPONSE:
                if(message.getSender().equals(username) || !receiveConnectResponses) 
                    break;

                printAlert(message.getSender() + " is in the chat");
                connectedUsers.addItem(message.getSender());
                break;
            case Message.DISCONNECTED_USER:
                printOrNotifyAlert(message.getSender() + " has leave", "");
                connectedUsers.removeItem(message.getSender());
                messageService.removeUser(message.getSender());
                break;
            case Message.SEND_FILE:
                printOrNotifyAlert(message.getSender() + " has sent " +
                    message.getPayload(), 
                    message.getSender().equals(username) 
                    ? message.getReceiver()
                    : message.getSender()
                );
                break;
            case Message.TEXT:
                if( message.getSender().equals(username)   || 
                    message.getReceiver().equals(username) || 
                    message.getReceiver().equals(""))
                printOrNotifyMessage(message);
        }
    }
    
    private void saveMessage(Message message) {
        messageService.addMessage(
            message.getReceiver().equals("")
            ? "" :
            message.getSender().equals(username)
            ? message.getReceiver()
            : message.getSender(),
            message);
        
    }
    
    private void printOrNotifyMessage(Message message) {
        String otherUser = 
            message.getReceiver().equals("")
            ? "" :
            message.getSender().equals(username)
            ? message.getReceiver()
            : message.getSender();

        if(connectedUsers.getSelectedItem().equals(otherUser))
            printMessage(message,
                    message.getSender().equals(username)
                    ? ownMessageTemplate
                    : othersMessageTemplate
            );
        else 
            JOptionPane.showMessageDialog(null, otherUser + " has sent you amessage");

        saveMessage(message);
    }

    private void printOrNotifyAlert(String alertMessage, String otherUser) {        
        if(connectedUsers.getSelectedItem().equals(otherUser))
            printAlert(alertMessage);
        else 
            JOptionPane.showMessageDialog(null, alertMessage);
    }

    private void printMessage(Message message, String template) {
        template = template.replace(":sender:", message.getSender());
        template = template.replace(
                ":message:",
                putEmojiImage(message.getPayload())
        );

        printNewListElement(template);
//        d.setInnerHTML(element, "<p>a</p>");
    }

    private void printAlert(String message) {
//        TDOO: verify
        String template = alertTemplate;

        template = template.replace(":message:", message);
        
        printNewListElement(template);
    }

    private void printNewListElement(String template) {
        try {
            HTMLDocument d = (HTMLDocument) chatArea.getDocument();
            Element element = d.getElement("messages-list");
            d.insertBeforeEnd(element, template);
        } catch (BadLocationException ex) {
            Logger.getLogger(ChatView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChatView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void clearMessages() {
        try {
            HTMLDocument d = (HTMLDocument) chatArea.getDocument();
            Element element = d.getElement("messages-list");
            d.setInnerHTML(element, "<li></li>");
        } catch (BadLocationException ex) {
            Logger.getLogger(ChatView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChatView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private class AppendEmoji implements ActionListener {
        private String emoji;
        public AppendEmoji(String emoji) { this.emoji = emoji; }
        public void actionPerformed(ActionEvent e) {
            messageArea.append(" " + emoji + " ");
        }
    }

    private class InitClient extends WindowAdapter {
        public void windowOpened(WindowEvent we) {
            Message message = new Message(
                Message.CONNECTED_USER, 
                "",
                username);

            client.sendMessage(message);
        }
    }

     public void initComponents() {
        // ----------------Creando Interfaz-------------------------//
        setBounds(325, 100, 800, 500);
        setTitle("Practica 3: " + username);
        setResizable(false);
        principalPanel = new JPanel();
        centralPanel = new JPanel();
        bottomPanel = new JPanel();
        emojisPanel = new JPanel();
        functionsPanel = new JPanel();
        usersPanel = new JPanel();
        panelCombo = new JPanel();
        chatArea = new JEditorPane("text/html", null);
        chatArea.setEditable(false);
        messageArea = new JTextArea();
        messageArea.setLineWrap(true);

        sendButton = new JButton("Send");
        fileButton = new JButton("Send File");
        disconnectButton = new JButton("Disconnect");

        connectedUsersLabel = new JLabel("    Connected Users   ");

        connectedUsers = new JComboBox<>();

        connectedUsers.addItem("");

        disconnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Message message = new Message(
                        Message.DISCONNECTED_USER, 
                        "",
                        username,
                        "");

                client.sendMessage(message);
            }
        });
        
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Message message = new Message(
                        Message.TEXT, 
                        messageArea.getText(),
                        username,
                        (String) connectedUsers.getSelectedItem());

                client.sendMessage(message);

                messageArea.setText("");
            }
        });

        fileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jf = new JFileChooser();
                jf.requestFocus();
                int r = jf.showOpenDialog(ChatView.this);

                if (r == JFileChooser.APPROVE_OPTION) {
                    try {
                        Message message = new Message(
                                Message.SEND_FILE,
                                jf.getSelectedFile().getName(),
                                username,
                                (String) connectedUsers.getSelectedItem());

                        client.sendFile(message, jf.getSelectedFile().getCanonicalPath());
                    } catch (IOException ex) {
                        Logger.getLogger(ChatView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        

        connectedUsers.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                List<Message> messages = 
                        messageService.getMessages((String) connectedUsers.getSelectedItem());

                showMessages(messages);
            }
        });

        principalPanel.setLayout(new BorderLayout(5, 5));
        centralPanel.setLayout(new BorderLayout(5, 5));
        bottomPanel.setLayout(new BoxLayout(this.bottomPanel, BoxLayout.Y_AXIS));
        functionsPanel.setLayout(new BoxLayout(this.functionsPanel, BoxLayout.X_AXIS));
        usersPanel.setLayout(new BorderLayout(5, 5));
        putButtons();
        addWindowListener(new InitClient());
        panelCombo.add(connectedUsers);
        usersPanel.add(connectedUsersLabel, BorderLayout.NORTH);
        connectedUsersLabel.setAlignmentX(SwingConstants.CENTER);
        usersPanel.add(panelCombo, BorderLayout.CENTER);
        
        centralPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        
        centralPanel.add(usersPanel, BorderLayout.EAST);
        functionsPanel.add(new JScrollPane(messageArea));
        functionsPanel.add(sendButton);
        functionsPanel.add(fileButton);
        functionsPanel.add(disconnectButton);
        bottomPanel.add(emojisPanel);
        bottomPanel.add(functionsPanel);
        principalPanel.add(centralPanel, BorderLayout.CENTER);
        principalPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        chatArea.setText(docHtmlCode);

        add(principalPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    
    private JEditorPane chatArea;
    private final String username;
    private JPanel principalPanel;
    private JPanel centralPanel;
    private JPanel bottomPanel;
    private JPanel emojisPanel;
    private JPanel functionsPanel;
    private JPanel usersPanel; 

    private final HashMap<String, String> emojis = new HashMap();

    private JTextArea messageArea;
    private JButton sendButton;
    private JButton fileButton;
    private JButton disconnectButton;
    public static JComboBox<String> connectedUsers;

    private JLabel connectedUsersLabel;
    private ActionListener listenerEmojis;
    private JPanel panelCombo;

    private final String othersMessageTemplate = 
"       <div class=\"list-item\">\n" +
"          <div class=\"message-container\">\n" +
"            <span class=\"sender\">:sender:</span>\n" +
"            <p class=\"message\">:message:</p>\n" +
"          </div>\n" +
"        </div>";

    private final String ownMessageTemplate = 
"          <div class=\"own-container\">\n" +
"            <span class=\"sender\">:sender:</span>\n" +
"            <p class=\"own\">:message:</p>\n" +
"          </div>"
    ;

    private final String alertTemplate =
        "<li>\n" +
"          <p class=\"alert\">:message:</p>\n" +
"        </li>"
    ;
    
    private final String docHtmlCode = "<html>\n" +
"  <head>\n" +
"    <title>An example HTMLDocument</title>\n" +
"    <style type=\"text/css\">\n" +
"      p {\n" +
"        margin: 0;\n" +
"      }\n" +
"      .char-history {\n" +
"        padding: 30px 30px 20px;\n" +
"        border-bottom: 2px solid white;\n" +
"        overflow-y: scroll;\n" +
"      }\n" +
"      #messages-list {\n" +
"        padding: 20px;\n" +
"      }\n" +
"      .list-item {\n" +
"        margin: 3px 0;\n" +
"        width: 100%;\n" +
"      }\n" +
"      .message-container {\n" +
"        display: block;\n" +
"        width: 200px;\n" +
"      }\n" +
"      .message {\n" +
"        padding: 2em;\n" +
"        border-radius: 20px;\n" +
"        color: white;\n" +
"        background-color: #9400d3;\n" +
"      }\n" +
"      .own {\n" +
"        padding: 2em;\n" +
"        border-radius: 20px;\n" +
"        color: white;\n" +
"        text-align: right;\n" +
"        background-color: #6495ed;\n" +
"        width: 100%;\n" +
"        margin-left: 200px;\n" +
"      }\n" +
"      .own-container {\n" +
"        width: 100%;\n" +
"        text-align: right;\n" +
"      }\n" +
"      .sender {\n" +
"        font-size: larger;\n" +
"        color: #696969;\n" +
"        letter-spacing: 0.2em;\n" +
"        font-style: italic;\n" +
"      }\n" +
"      .alert {\n" +
"        text-align: center;\n" +
"        font: bold;\n" +
"        color: black;\n" +
"      }\n" +
"    </style>\n" +
"  </head>\n" +
"  <body>\n" +
"    <div class=\"chat-history\">\n" +
"      <div id=\"messages-list\">\n" +
"      </div>\n" +
"    </div>\n" +
"  </body>\n" +
"</html>";
}

