package software.plusminus.aspect;

public interface ExceptionExtractor<O extends Exception, E extends Exception> {

    E extract(O originalException);

}
