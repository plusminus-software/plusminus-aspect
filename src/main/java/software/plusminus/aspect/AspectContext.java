package software.plusminus.aspect;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.plusminus.listener.DefaultJoinpoint;
import software.plusminus.listener.Joinpoint;
import software.plusminus.listener.ListenerContext;
import software.plusminus.listener.TriggerContext;

import java.util.List;

@Component
@AllArgsConstructor
public class AspectContext {

    private ListenerContext listenerContext;
    private TriggerContext triggerContext;
    private List<Around> arounds;
    private ExceptionExtractorContext exceptionExtractorContext;

    public void run(ThrowingRunnable runnable) {
        run(runnable, DefaultJoinpoint.INSTANCE);
    }

    public void run(ThrowingRunnable runnable, Joinpoint joinpoint) {
        run(runnable, joinpoint, joinpoint);
    }

    public void run(ThrowingRunnable runnable, Joinpoint joinpoint, Joinpoint aroundJoinpoint) {
        try {
            before(joinpoint);
            around(runnable, aroundJoinpoint).run();
            after(joinpoint);
        } catch (Exception e) {
            onException(e, joinpoint);
            ThrowingRunnable.sneakyThrow(e);
        } finally {
            onFinally(joinpoint);
        }
    }

    public void before() {
        before(DefaultJoinpoint.INSTANCE);
    }

    public void before(Joinpoint joinpoint) {
        triggerContext.trigger(Before.class, Before::before, joinpoint);
    }

    public ThrowingRunnable around(ThrowingRunnable runnable) {
        return around(runnable, DefaultJoinpoint.INSTANCE);
    }

    public ThrowingRunnable around(ThrowingRunnable runnable, Joinpoint joinpoint) {
        return Around.around(runnable, arounds, joinpoint);
    }

    public void after() {
        after(DefaultJoinpoint.INSTANCE);
    }

    public void after(Joinpoint joinpoint) {
        triggerContext.trigger(After.class, After::after, joinpoint);
    }

    public void onException(Exception exception) {
        onException(exception, DefaultJoinpoint.INSTANCE);
    }

    public void onException(Exception exception, Joinpoint joinpoint) {
        listenerContext.listen(exception, ExceptionListener.class, ExceptionListener::onException, joinpoint);
        exceptionExtractorContext.extract(exception)
                .forEach(e -> listenerContext.listen(
                        e, ExceptionListener.class, ExceptionListener::onException, joinpoint));
    }

    public void onFinally() {
        onFinally(DefaultJoinpoint.INSTANCE);
    }

    public void onFinally(Joinpoint joinpoint) {
        triggerContext.trigger(Finally.class, Finally::onFinally, joinpoint);
    }
}
