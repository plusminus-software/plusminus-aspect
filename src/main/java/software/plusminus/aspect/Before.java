package software.plusminus.aspect;

import software.plusminus.listener.Trigger;

public interface Before extends Advice, Trigger {

    void before();

}
