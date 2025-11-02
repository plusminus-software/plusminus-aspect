package software.plusminus.aspect;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class AspectManager {

    private Map<Class<?>, List<Before>> befores;
    private Map<Class<?>, List<After>> afters;
    private Map<Class<?>, List<Around>> arounds;
    private Map<Class<?>, Map<Class<? extends Exception>,
            List<ExceptionListener<? extends Exception>>>> exceptionListeners;
    private Map<Class<?>, List<Finally>> finallies;

    public void run(ThrowingRunnable runnable) {
        run(DefaultScope.class, runnable);
    }

    public void run(Object scope, ThrowingRunnable runnable) {
        run(scope.getClass(), runnable);
    }

    private void run(Class<?> scopeType, ThrowingRunnable runnable) {
        try {
            before(scopeType);
            around(scopeType, runnable).run();
            after(scopeType);
        } catch (Exception e) {
            onException(scopeType, e);
            ThrowingRunnable.sneakyThrow(e);
        } finally {
            onFinally(scopeType);
        }
    }

    public void before() {
        before(DefaultScope.class);
    }

    public void before(Object scope) {
        before(scope.getClass());
    }

    private void before(Class<?> scopeType) {
        befores.getOrDefault(scopeType, Collections.emptyList())
                .forEach(Before::before);
    }

    public ThrowingRunnable around(ThrowingRunnable original) {
        return around(DefaultScope.class, original);
    }

    public ThrowingRunnable around(Object scope, ThrowingRunnable original) {
        return around(scope.getClass(), original);
    }

    private ThrowingRunnable around(Class<?> scopeType, ThrowingRunnable original) {
        ThrowingRunnable result = original;
        List<Around> scopedArounds = arounds.getOrDefault(scopeType, Collections.emptyList());
        for (int i = scopedArounds.size() - 1; i >= 0; i--) {
            ThrowingRunnable current = result;
            Around around = scopedArounds.get(i);
            result = () -> around.around(current);
        }
        return result;
    }

    public void after() {
        after(DefaultScope.class);
    }

    public void after(Object scope) {
        after(scope.getClass());
    }

    private void after(Class<?> scopeType) {
        afters.getOrDefault(scopeType, Collections.emptyList())
                .forEach(After::after);
    }

    public void onException(Exception e) {
        onException(DefaultScope.class, e);
    }

    public void onException(Object scope, Exception e) {
        onException(scope.getClass(), e);
    }

    private void onException(Class<?> scopeType, Exception e) {
        filterExceptionListeners(scopeType, e)
                .forEach(exceptionListener -> exceptionListener.onException(e));
    }

    public void onFinally() {
        onFinally(DefaultScope.class);
    }

    public void onFinally(Object scope) {
        onFinally(scope.getClass());
    }

    private void onFinally(Class<?> scopeType) {
        finallies.getOrDefault(scopeType, Collections.emptyList())
                .forEach(Finally::finish);
    }

    public void notSupportedAround() {
        notSupportedAround(DefaultScope.class);
    }

    public void notSupportedAround(Object scope) {
        notSupportedAround(scope.getClass());
    }

    private void notSupportedAround(Class<?> scopeType) {
        List<Around> scopedArounds = arounds.get(scopeType);
        if (scopedArounds != null && !scopedArounds.isEmpty()) {
            throw new IllegalStateException("Arounds are not supported for the scope " + scopeType);
        }
    }

    private <E extends Exception> Stream<ExceptionListener<? super E>> filterExceptionListeners(
            Class<?> scopeType, E exception) {
        Map<Class<? extends Exception>, List<ExceptionListener<? extends Exception>>> scopedExceptionListeners =
                exceptionListeners.getOrDefault(scopeType, Collections.emptyMap());
        return scopedExceptionListeners.entrySet().stream()
                .filter(e -> e.getKey().isAssignableFrom(exception.getClass()))
                .map(Map.Entry::getValue)
                .flatMap(List::stream)
                .map(exceptionListener -> (ExceptionListener<? super E>) exceptionListener);
    }
}
