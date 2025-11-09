package software.plusminus.aspect;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
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
        run(runnable, Joinpoint.DEFAULT);
    }

    public void run(ThrowingRunnable runnable, Joinpoint joinpoint) {
        run(runnable, joinpoint, joinpoint);
    }

    public void run(ThrowingRunnable runnable, Joinpoint joinpoint, Joinpoint additionalAroundJoinpoint) {
        try {
            before(joinpoint);
            around(runnable, joinpoint, additionalAroundJoinpoint).run();
            after(joinpoint);
        } catch (Exception e) {
            onException(e, joinpoint);
            ThrowingRunnable.sneakyThrow(e);
        } finally {
            onFinally(joinpoint);
        }
    }

    public void before() {
        before(Joinpoint.DEFAULT);
    }

    public void before(Joinpoint joinpoint) {
        triggerContext.trigger(Before.class, Before::before, joinpoint);
    }

    public ThrowingRunnable around(ThrowingRunnable runnable) {
        return around(runnable, Joinpoint.DEFAULT);
    }

    public ThrowingRunnable around(ThrowingRunnable runnable, Joinpoint... joinpoints) {
        return Around.around(runnable, arounds, joinpoints);
    }

    public void after() {
        after(Joinpoint.DEFAULT);
    }

    public void after(Joinpoint joinpoint) {
        triggerContext.trigger(After.class, After::after, joinpoint);
    }

    public void onException(Exception exception) {
        onException(exception, Joinpoint.DEFAULT);
    }

    public void onException(Exception exception, Joinpoint joinpoint) {
        listenerContext.listen(exception, ExceptionListener.class, ExceptionListener::onException, joinpoint);
        exceptionExtractorContext.extract(exception)
                .forEach(e -> listenerContext.listen(
                        e, ExceptionListener.class, ExceptionListener::onException, joinpoint));
    }

    public void onFinally() {
        onFinally(Joinpoint.DEFAULT);
    }

    public void onFinally(Joinpoint joinpoint) {
        triggerContext.trigger(Finally.class, Finally::onFinally, joinpoint);
    }
}
