package utils;

/**
 * Classe d'exception pour la classe de lecture de fichier.
 *
 * @see utils.FileManager
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
