package software.plusminus.aspect;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.plusminus.spring.SpringUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class AspectManager {

    private List<Before> befores;
    private List<After> afters;
    private List<Around> arounds;
    private Map<Class<?>, List<ExceptionListener<?>>> exceptionListeners;
    private List<Finally> finallies;

    @Autowired
    void init(List<Before> befores,
              List<After> afters,
              List<Around> arounds,
              List<ExceptionListener<?>> exceptionListeners,
              List<Finally> finallies) {
        if (this.befores == null) {
            this.befores = befores;
        }
        if (this.afters == null) {
            this.afters = afters;
        }
        if (this.arounds == null) {
            this.arounds = arounds;
        }
        if (this.exceptionListeners == null) {
            this.exceptionListeners = SpringUtil.groupBeansByGenericType(exceptionListeners, ExceptionListener.class);
        }
        if (this.finallies == null) {
            this.finallies = finallies;
        }
    }

    public void run(ThrowingRunnable runnable) {
        try {
            befores.forEach(Before::before);
            around(runnable).run();
            afters.forEach(After::after);
        } catch (Exception e) {
            filterExceptionListeners(e)
                    .forEach(exceptionListener -> exceptionListener.onException(e));
            ThrowingRunnable.sneakyThrow(e);
        } finally {
            finallies.forEach(Finally::finish);
        }
    }

    private ThrowingRunnable around(ThrowingRunnable original) {
        ThrowingRunnable result = original;
        for (int i = arounds.size() - 1; i >= 0; i--) {
            ThrowingRunnable current = result;
            Around around = arounds.get(i);
            result = () -> around.around(current);
        }
        return result;
    }

    private <E extends Exception> Stream<ExceptionListener<? super E>> filterExceptionListeners(E exception) {
        return exceptionListeners.entrySet().stream()
                .filter(e -> e.getKey().isAssignableFrom(exception.getClass()))
                .map(Map.Entry::getValue)
                .flatMap(List::stream)
                .map(exceptionListener -> (ExceptionListener<? super E>) exceptionListener);
    }
}
