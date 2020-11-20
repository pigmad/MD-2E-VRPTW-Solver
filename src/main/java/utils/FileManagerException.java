package utils;

/**
 *
 * @author LASTENNET Dorian
 */
public class FileManagerException extends Exception {
    public FileManagerException(String errorMessage) {
        super(errorMessage);
    }
    
    public FileManagerException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
