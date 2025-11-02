package software.plusminus.aspect;

public interface Advice {

    default Class<?> scope() {
        return DefaultScope.class;
    }

}
