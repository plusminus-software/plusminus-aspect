package software.plusminus.aspect;

import software.plusminus.listener.Trigger;

public interface After extends Advice, Trigger {

    void after();

}
