package software.plusminus.aspect.fixtures;

import org.omg.CORBA.portable.UnknownException;
import org.springframework.stereotype.Component;
import software.plusminus.aspect.ExceptionListener;

@Component
public class UnknownExceptionListener implements ExceptionListener<UnknownException> {

    private int triggered;

    @Override
    public void onException(UnknownException exception) {
        triggered++;
    }

    public int getTriggered() {
        return triggered;
    }
}
