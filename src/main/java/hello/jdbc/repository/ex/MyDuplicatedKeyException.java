package hello.jdbc.repository.ex;

public class MyDuplicatedKeyException extends MyDbException {
    public MyDuplicatedKeyException() {
    }

    public MyDuplicatedKeyException(String message) {
        super(message);
    }

    public MyDuplicatedKeyException(Throwable cause) {
        super(cause);
    }

    public MyDuplicatedKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
