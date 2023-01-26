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
import java.awt.BorderLayout;
import javax.swing.*;

public class LoginView extends JFrame {

  private static final long serialVersionUID = 1L;

  public LoginView() {
    setBounds(450, 150, 300, 100);
    setResizable(false);
    setTitle("Chat");

    principalPanel = new JPanel();
    centralPanel = new JPanel();
    principalPanel.setLayout(new BorderLayout(5, 5));
    centralPanel.setLayout(new BoxLayout(this.centralPanel, BoxLayout.Y_AXIS));
    username = new JLabel("Enter your username:");
    userField = new JTextField(30);
    connectButton = new JButton("Connect");

    connectButton.addActionListener (new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          if (!userField.getText().equals("")) {
            dispose();

            final Client cl = new Client(HOST, PORT);
            final ReceiveMessage rm = new ReceiveMessage(cl.getSocket());
            final ChatView in = new ChatView(HOST, PORT, userField.getText().trim(), cl);

            rm.addObserver(in);
            new Thread(rm).start();
          } else JOptionPane.showMessageDialog(LoginView.this,
            "Enter a username",
            "Error",
            JOptionPane.ERROR_MESSAGE
          );
        }
      }
    );

    centralPanel.add(username);
    centralPanel.add(userField);
    principalPanel.add(centralPanel, BorderLayout.CENTER);
    principalPanel.add(connectButton, BorderLayout.SOUTH);
    add(principalPanel);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  public static void main(String[] args) {
    new LoginView();
  }

  private final String HOST = "230.1.1.1";
  private final int PORT = 4000;
  private JPanel principalPanel;
  private JPanel centralPanel;
  private JLabel username;
  private JTextField userField;
  private JButton connectButton;
}

