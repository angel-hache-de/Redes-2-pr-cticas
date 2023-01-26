package network;

public class PackageNotReceivedException extends Exception {
    public PackageNotReceivedException(String message) {
        super(message);
    }

    public PackageNotReceivedException(String message, Exception e) {
        super(message, e);
    }
}
