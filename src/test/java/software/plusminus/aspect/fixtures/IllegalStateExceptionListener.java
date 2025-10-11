package software.plusminus.aspect.fixtures;

import org.springframework.stereotype.Component;
import software.plusminus.aspect.ExceptionListener;

@Component
public class IllegalStateExceptionListener implements ExceptionListener<IllegalStateException> {

    private int triggered;

    @Override
    public void onException(IllegalStateException exception) {
        triggered++;
    }

    public int getTriggered() {
        return triggered;
    }
}
