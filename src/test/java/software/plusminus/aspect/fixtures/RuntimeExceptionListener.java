package software.plusminus.aspect.fixtures;

import org.springframework.stereotype.Component;
import software.plusminus.aspect.ExceptionListener;

@Component
public class RuntimeExceptionListener implements ExceptionListener<RuntimeException> {

    private int triggered;

    @Override
    public void onException(RuntimeException exception) {
        triggered++;
    }

    public int getTriggered() {
        return triggered;
    }
}
