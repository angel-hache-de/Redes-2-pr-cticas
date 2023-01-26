package network;

public class PackageNotSentException extends Exception{
    public PackageNotSentException(String message) {
        super(message);
    }

    public PackageNotSentException(String message, Exception e) {
        super(message, e);
    }
}
