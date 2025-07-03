package mo.kilate.ide.utils.function;

@FunctionalInterface
public interface Listener<T> {
  void call(final T value);
}
