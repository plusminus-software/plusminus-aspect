package software.plusminus.aspect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import software.plusminus.aspect.Advice;
import software.plusminus.aspect.After;
import software.plusminus.aspect.Around;
import software.plusminus.aspect.Before;
import software.plusminus.aspect.ExceptionListener;
import software.plusminus.aspect.Finally;
import software.plusminus.spring.SpringUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@ComponentScan("software.plusminus.aspect")
public class AspectAutoconfig {

    @Bean
    Map<Class<?>, List<Before>> befores(List<Before> befores) {
        return befores.stream()
                .collect(Collectors.groupingBy(Advice::scope));
    }

    @Bean
    Map<Class<?>, List<After>> afters(List<After> afters) {
        return afters.stream()
                .collect(Collectors.groupingBy(Advice::scope));
    }

    @Bean
    Map<Class<?>, List<Around>> arounds(List<Around> arounds) {
        return arounds.stream()
                .collect(Collectors.groupingBy(Advice::scope));
    }

    @Bean
    Map<Class<?>, Map<Class<? extends Exception>, List<ExceptionListener<? extends Exception>>>> exceptionListeners(
            List<ExceptionListener<?>> exceptionListeners) {

        Map<Class<?>, List<ExceptionListener<?>>> groupedByScode = exceptionListeners.stream()
                .collect(Collectors.groupingBy(Advice::scope));
        return groupedByScode.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> groupByExceptionType(e.getValue())));
    }

    @Bean
    Map<Class<?>, List<Finally>> finallies(List<Finally> finallies) {
        return finallies.stream()
                .collect(Collectors.groupingBy(Advice::scope));
    }

    private Map<Class<? extends Exception>, List<ExceptionListener<? extends Exception>>> groupByExceptionType(
            List<ExceptionListener<? extends Exception>> exceptionListeners) {
        return SpringUtil.groupBeansByGenericType(exceptionListeners, ExceptionListener.class).entrySet().stream()
                .collect(Collectors.toMap(e -> (Class<? extends Exception>) e.getKey(),
                        Map.Entry::getValue));
    }
}
