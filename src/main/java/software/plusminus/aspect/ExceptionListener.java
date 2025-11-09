package software.plusminus.aspect;

import software.plusminus.listener.Listener;

public interface ExceptionListener<T extends Exception> extends Advice, Listener<T> {

    void onException(T exception);

}
