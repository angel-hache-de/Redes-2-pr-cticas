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
public class PackageNotSentException extends Exception{
    public PackageNotSentException(String message) {
        super(message);
    }

    public PackageNotSentException(String message, Exception e) {
        super(message, e);
    }
}
