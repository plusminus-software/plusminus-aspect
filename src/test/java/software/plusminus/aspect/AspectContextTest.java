package software.plusminus.aspect;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.plusminus.aspect.fixtures.IllegalStateExceptionListener;
import software.plusminus.aspect.fixtures.RuntimeExceptionListener;
import software.plusminus.aspect.fixtures.TestAspect;
import software.plusminus.aspect.fixtures.UnknownExceptionListener;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.plusminus.check.Checks.check;

@SpringBootTest
class AspectContextTest {

    @Autowired
    private TestAspect testAspect;
    @Autowired
    private IllegalStateExceptionListener illegalStateExceptionListener;
    @Autowired
    private RuntimeExceptionListener runtimeExceptionListener;
    @Autowired
    private UnknownExceptionListener unknownExceptionListener;
    @Autowired
    private AspectContext aspectContext;

    @AfterEach
    void afterEach() {
        testAspect.getCalls().clear();
    }

    @Test
    void run() {
        aspectContext.run(() -> testAspect.getCalls().add("originalCall"));
        check(testAspect.getCalls()).is("before", "aroundStart", "originalCall", "aroundEnd", "after", "finish");
    }

    @Test
    void exceptionHandling() {
        IllegalStateException illegalArgumentException = assertThrows(IllegalStateException.class,
                () -> aspectContext.run(() -> {
                    throw new IllegalStateException("Test exception");
                }));

        check(illegalArgumentException.getMessage()).is("Test exception");
        check(testAspect.getCalls()).is("before", "aroundStart", "exception IllegalStateException", "finish");
        check(illegalStateExceptionListener.getTriggered()).is(1);
        check(runtimeExceptionListener.getTriggered()).is(1);
        check(unknownExceptionListener.getTriggered()).is(0);
    }
}