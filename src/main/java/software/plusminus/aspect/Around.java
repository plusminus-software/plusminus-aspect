package software.plusminus.aspect;

public interface Around extends Advice {

    void around(ThrowingRunnable runnable);

}
