package software.plusminus.aspect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import software.plusminus.aspect.ExceptionExtractor;
import software.plusminus.spring.SpringUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@ComponentScan("software.plusminus.aspect")
public class AspectAutoconfig {

    @SuppressWarnings("java:S1452")
    @Bean
    Map<Class<? extends Exception>,
            List<ExceptionExtractor<? extends Exception, ? extends Exception>>> exceptionExtractors(
                    List<ExceptionExtractor<? extends Exception, ? extends Exception>> exceptionExtractors) {
        return SpringUtil.groupBeansByGenericType(exceptionExtractors, ExceptionExtractor.class).entrySet().stream()
                .collect(Collectors.toMap(e -> (Class<? extends Exception>) e.getKey(),
                        Map.Entry::getValue));
    }
}
