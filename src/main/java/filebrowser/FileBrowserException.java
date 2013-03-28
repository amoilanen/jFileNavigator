package filebrowser;

@SuppressWarnings("serial")
public class FileBrowserException extends Exception {

    public FileBrowserException() {
        super();
    }

    public FileBrowserException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileBrowserException(String message) {
        super(message);
    }

    public FileBrowserException(Throwable cause) {
        super(cause);
    }
}
