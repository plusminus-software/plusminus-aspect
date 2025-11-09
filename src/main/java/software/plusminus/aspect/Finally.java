package software.plusminus.aspect;

import software.plusminus.listener.Trigger;

public interface Finally extends Advice, Trigger {

    void onFinally();

}
