package software.plusminus.aspect.fixtures;

import lombok.Getter;
import org.springframework.stereotype.Component;
import software.plusminus.aspect.After;
import software.plusminus.aspect.Around;
import software.plusminus.aspect.Before;
import software.plusminus.aspect.ExceptionListener;
import software.plusminus.aspect.Finally;
import software.plusminus.aspect.ThrowingRunnable;
import software.plusminus.listener.DefaultJoinpoint;
import software.plusminus.listener.Joinpoint;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class TestAspect implements Before, Around, After, ExceptionListener<IllegalStateException>, Finally {

    @Getter
    private Set<String> calls = new LinkedHashSet<>();

    @Override
    public Joinpoint joinpoint() {
        return DefaultJoinpoint.INSTANCE;
    }

    @Override
    public void before() {
        calls.add("before");
    }

    @Override
    public void around(ThrowingRunnable runnable) {
        calls.add("aroundStart");
        runnable.asRunnable().run();
        calls.add("aroundEnd");
    }

    @Override
    public void after() {
        calls.add("after");
    }

    @Override
    public void onException(IllegalStateException exception) {
        calls.add("exception " + exception.getClass().getSimpleName());
    }

    @Override
    public void onFinally() {
        calls.add("finish");
    }
}
