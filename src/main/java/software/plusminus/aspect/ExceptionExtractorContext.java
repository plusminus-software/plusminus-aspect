package software.plusminus.aspect;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ExceptionExtractorContext {

    private Map<Class<? extends Exception>,
            List<ExceptionExtractor<? extends Exception, ? extends Exception>>> exceptionExtractors;

    @SuppressWarnings("java:S1452")
    public <E extends Exception> List<? extends Exception> extract(E exception) {
        return exceptionExtractors.getOrDefault(exception.getClass(), Collections.emptyList()).stream()
                .map(extractor -> (ExceptionExtractor<E, ? extends Exception>) extractor)
                .map(extractor -> extractor.extract(exception))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
