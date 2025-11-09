package software.plusminus.aspect;

import software.plusminus.listener.Joinpoint;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Around extends Advice {

    default Joinpoint joinpoint() {
        return Joinpoint.DEFAULT;
    }

    void around(ThrowingRunnable runnable);

    static ThrowingRunnable around(ThrowingRunnable runnable, List<Around> arounds, Joinpoint... joinpoints) {
        ThrowingRunnable result = runnable;
        List<Around> filteredArounds = arounds.stream()
                .filter(around -> Stream.of(joinpoints)
                        .anyMatch(joinpoint -> joinpoint == around.joinpoint()))
                .collect(Collectors.toList());
        for (int i = filteredArounds.size() - 1; i >= 0; i--) {
            ThrowingRunnable current = result;
            Around around = filteredArounds.get(i);
            result = () -> around.around(current);
        }
        return result;
    }
}
