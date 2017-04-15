package animator.util;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapFunctions {

    public static <K, V, R> Map<K, R> filterByKeyCollectAndMap(Map<K, V> map, Predicate<K> predicate, Function<V, R> mapper) {
        return map.entrySet().stream().filter(e -> predicate.test(e.getKey())).collect(Collectors.toMap(Map.Entry::getKey, e -> mapper.apply(e.getValue())));
    }

    public static <K, V> void filterByKeyAndForEach(Map<K, V> map, Predicate<K> predicate, Consumer<V> consumer) {
        map.entrySet().stream().filter(e -> predicate.test(e.getKey())).forEach(e -> consumer.accept(e.getValue()));
    }

    public static <K, V, R> Stream<R> map(Map<K, V> map, BiFunction<K, V, R> mapper) {
        return map.entrySet().stream().map(e -> mapper.apply(e.getKey(), e.getValue()));
    }
}
