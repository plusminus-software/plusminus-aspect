package software.plusminus.aspect;

import java.util.Optional;

public interface ExceptionExtractor<O extends Exception, E extends Exception> {

    Optional<? extends E> extract(O originalException);

}
