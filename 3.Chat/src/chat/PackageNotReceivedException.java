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
public class PackageNotReceivedException extends Exception {
    public PackageNotReceivedException(String message) {
        super(message);
    }

    public PackageNotReceivedException(String message, Exception e) {
        super(message, e);
    }
}
