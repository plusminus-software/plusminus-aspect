package software.plusminus.aspect;

public interface ExceptionListener<T extends Exception> {

    void onException(T exception);

}
