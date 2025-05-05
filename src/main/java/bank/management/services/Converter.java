package bank.management.services;

@FunctionalInterface
public interface Converter<T, R> {
    R convert(T input);
}
