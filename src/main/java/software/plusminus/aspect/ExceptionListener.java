package software.plusminus.aspect;

public interface ExceptionListener<T extends Exception> extends Advice {

    void onException(T exception);

}
