/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
public class Prueba extends JFrame implements ActionListener {
public static CardLayout crd = new CardLayout(70, 60);
public static Container cn;
@Override
public void actionPerformed(ActionEvent e) {
crd.next(cn);
}
public static void main(String[] args) {
Prueba cld = new Prueba();
}
public Prueba() {
setVisible(true);
setDefaultCloseOperation(EXIT_ON_CLOSE);
CardLayout cl = new CardLayout();
setLayout(cl);
setTitle("Demo of Card Layout");
setSize(300, 300);
add(new Button("Layout Button 1"));
add(new Button("Layout Button 2"));
add(new Button("Layout Button 3"));
add(new Button("Layout Button 4"));
add(new Button("Layout Button 5"));
add(new Button("Layout Button 6"));
}
}
