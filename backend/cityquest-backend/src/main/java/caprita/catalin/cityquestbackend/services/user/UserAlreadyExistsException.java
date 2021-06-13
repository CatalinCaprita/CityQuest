package caprita.catalin.cityquestbackend.services.user;

public class UserAlreadyExistsException extends Exception{
    public UserAlreadyExistsException() {
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
