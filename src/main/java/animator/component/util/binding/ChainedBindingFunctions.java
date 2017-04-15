package animator.component.util.binding;

import java.util.function.Function;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.FloatBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.LongBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableFloatValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;

public class ChainedBindingFunctions<S> {

    private final ObservableValue<S> rootProperty;

    private ChainedBindingFunctions(ObservableValue<S> rootProperty) {
        this.rootProperty = rootProperty;
    }

    public static <S> ChainedBindingFunctions<S> from(ObservableValue<S> rootProperty) {
        return new ChainedBindingFunctions<>(rootProperty);
    }

    public <T> ChainedBindingFunctions<T> select(Function<S, ? extends ObservableValue<T>> childPropertyAccessor) {
        return new ChainedBindingFunctions<>(new ChainBinding<>(rootProperty, childPropertyAccessor));
    }

    public <T> ChainedBindingFunctions<T> map(Function<S, ? extends T> childPropertyAccessor) {
        return new ChainedBindingFunctions<>(new MappedBinding<>(rootProperty, childPropertyAccessor));
    }

    public <T> ObjectBinding<T> selectObject(Function<S, ? extends ObservableValue<T>> childPropertyAccessor) {
        return new ChainBinding<>(rootProperty, childPropertyAccessor);
    }

    public <T> ObjectBinding<T> mapToObject(Function<S, ? extends T> childPropertyAccessor) {
        return new MappedBinding<>(rootProperty, childPropertyAccessor);
    }

    public IntegerBinding selectInteger(Function<S, ? extends ObservableIntegerValue> childPropertyAccessor) {
        return selectInteger(childPropertyAccessor, 0);
    }

    public IntegerBinding selectInteger(Function<S, ? extends ObservableIntegerValue> childPropertyAccessor, int defaultValue) {
        return new IntegerBindingAdapter(new ChainBinding<>(rootProperty, childPropertyAccessor), defaultValue);
    }

    public IntegerBinding mapToInteger(Function<S, Integer> childValueAccessor) {
        return mapToInteger(childValueAccessor, 0);
    }

    public IntegerBinding mapToInteger(Function<S, Integer> childValueAccessor, Integer defaultValue) {
        return new IntegerBindingAdapter(mapToObject(childValueAccessor), defaultValue);
    }

    public LongBinding selectLong(Function<S, ? extends ObservableLongValue> childPropertyAccessor) {
        return selectLong(childPropertyAccessor, 0L);
    }

    public LongBinding selectLong(Function<S, ? extends ObservableLongValue> childPropertyAccessor, long defaultValue) {
        return new LongBindingAdapter(new ChainBinding<>(rootProperty, childPropertyAccessor), defaultValue);
    }

    public LongBinding mapToLong(Function<S, Long> childValueAccessor) {
        return mapToLong(childValueAccessor, 0L);
    }

    public LongBinding mapToLong(Function<S, Long> childValueAccessor, Long defaultValue) {
        return new LongBindingAdapter(mapToObject(childValueAccessor), defaultValue);
    }

    public FloatBinding selectFloat(Function<S, ? extends ObservableFloatValue> childPropertyAccessor) {
        return selectFloat(childPropertyAccessor, 0.0f);
    }

    public FloatBinding selectFloat(Function<S, ? extends ObservableFloatValue> childPropertyAccessor, float defaultValue) {
        return new FloatBindingAdapter(new ChainBinding<>(rootProperty, childPropertyAccessor), defaultValue);
    }

    public FloatBinding mapToFloat(Function<S, Float> childValueAccessor) {
        return mapToFloat(childValueAccessor, 0.0f);
    }

    public FloatBinding mapToFloat(Function<S, Float> childValueAccessor, Float defaultValue) {
        return new FloatBindingAdapter(mapToObject(childValueAccessor), defaultValue);
    }

    public DoubleBinding selectDouble(Function<S, ? extends ObservableDoubleValue> childPropertyAccessor) {
        return selectDouble(childPropertyAccessor, 0.0);
    }

    public DoubleBinding selectDouble(Function<S, ? extends ObservableDoubleValue> childPropertyAccessor, double defaultValue) {
        return new DoubleBindingAdapter(new ChainBinding<>(rootProperty, childPropertyAccessor), defaultValue);
    }

    public DoubleBinding mapToDouble(Function<S, Double> childValueAccessor) {
        return mapToDouble(childValueAccessor, 0.0);
    }

    public DoubleBinding mapToDouble(Function<S, Double> childValueAccessor, Double defaultValue) {
        return new DoubleBindingAdapter(mapToObject(childValueAccessor), defaultValue);
    }

    public BooleanBinding selectBoolean(Function<S, ? extends ObservableBooleanValue> childPropertyAccessor) {
        return selectBoolean(childPropertyAccessor, false);
    }

    public BooleanBinding selectBoolean(Function<S, ? extends ObservableBooleanValue> childPropertyAccessor, boolean defaultValue) {
        return new BooleanBindingAdapter(new ChainBinding<>(rootProperty, childPropertyAccessor), defaultValue);
    }

    public BooleanBinding mapToBoolean(Function<S, Boolean> childValueAccessor) {
        return mapToBoolean(childValueAccessor, false);
    }

    public BooleanBinding mapToBoolean(Function<S, Boolean> childValueAccessor, boolean defaultValue) {
        return new BooleanBindingAdapter(mapToObject(childValueAccessor), defaultValue);
    }

    public StringBinding selectString(Function<S, ? extends ObservableStringValue> childPropertyAccessor) {
        return selectString(childPropertyAccessor, "");
    }

    public StringBinding selectString(Function<S, ? extends ObservableStringValue> childPropertyAccessor, String defaultValue) {
        return new StringBindingAdapter(new ChainBinding<>(rootProperty, childPropertyAccessor), defaultValue);
    }

    public StringBinding mapToString(Function<S, String> childValueAccessor) {
        return mapToString(childValueAccessor, "");
    }

    public StringBinding mapToString(Function<S, String> childValueAccessor, String defaultValue) {
        return new StringBindingAdapter(mapToObject(childValueAccessor), defaultValue);
    }

    private static class IntegerBindingAdapter extends IntegerBinding {

        private ObjectBinding<Number> integerObjectBinding;
        private final int defaultValue;

        IntegerBindingAdapter(ObjectBinding<Number> integerObjectBinding, int defaultValue) {
            this.integerObjectBinding = integerObjectBinding;
            this.defaultValue = defaultValue;

            bind(integerObjectBinding);
        }

        @Override
        protected int computeValue() {
            Number number = integerObjectBinding.get();
            if (number == null) {
                return defaultValue;
            }
            return number.intValue();
        }
    }

    private static class LongBindingAdapter extends LongBinding {

        private ObjectBinding<Number> longObjectBinding;
        private final long defaultValue;

        LongBindingAdapter(ObjectBinding<Number> longObjectBinding, long defaultValue) {
            this.longObjectBinding = longObjectBinding;
            this.defaultValue = defaultValue;

            bind(longObjectBinding);
        }

        @Override
        protected long computeValue() {
            Number number = longObjectBinding.get();
            if (number == null) {
                return defaultValue;
            }
            return number.longValue();
        }
    }

    private static class FloatBindingAdapter extends FloatBinding {

        private final ObjectBinding<Number> floatObjectBinding;
        private final float defaultValue;

        FloatBindingAdapter(ObjectBinding<Number> floatObjectBinding, float defaultValue) {
            this.floatObjectBinding = floatObjectBinding;
            this.defaultValue = defaultValue;

            bind(floatObjectBinding);
        }

        @Override
        protected float computeValue() {
            Number number = floatObjectBinding.get();
            if (number == null) {
                return defaultValue;
            }
            return number.floatValue();
        }
    }

    private static class DoubleBindingAdapter extends DoubleBinding {

        private final ObjectBinding<Number> doubleObjectBinding;
        private final double defaultValue;

        DoubleBindingAdapter(ObjectBinding<Number> doubleObjectBinding, double defaultValue) {
            this.doubleObjectBinding = doubleObjectBinding;
            this.defaultValue = defaultValue;

            bind(doubleObjectBinding);
        }

        @Override
        protected double computeValue() {
            Number number = doubleObjectBinding.get();
            if (number == null) {
                return defaultValue;
            }
            return number.doubleValue();
        }
    }

    private static class BooleanBindingAdapter extends BooleanBinding {

        private ObjectBinding<Boolean> booleanObjectBinding;
        private final boolean defaultValue;

        BooleanBindingAdapter(ObjectBinding<Boolean> booleanObjectBinding, boolean defaultValue) {
            this.booleanObjectBinding = booleanObjectBinding;
            this.defaultValue = defaultValue;

            bind(booleanObjectBinding);
        }

        @Override
        protected boolean computeValue() {
            Boolean booleanValue = booleanObjectBinding.get();
            if (booleanValue == null) {
                return defaultValue;
            }
            return booleanValue;
        }
    }

    private static class StringBindingAdapter extends StringBinding {

        private ObjectBinding<String> stringObjectBinding;
        private final String defaultValue;

        StringBindingAdapter(ObjectBinding<String> stringObjectBinding, String defaultValue) {
            this.stringObjectBinding = stringObjectBinding;
            this.defaultValue = defaultValue;

            bind(stringObjectBinding);
        }

        @Override
        protected String computeValue() {
            String stringValue = stringObjectBinding.get();
            if (stringValue == null) {
                return defaultValue;
            }
            return stringValue;
        }
    }

    private class ChainBinding<S, T> extends ObjectBinding<T> {

        private final ObservableValue<S> rootProperty;
        private final Function<S, ? extends ObservableValue<T>> childPropertyAccessor;
        private ObservableValue<T> childProperty;

        ChainBinding(ObservableValue<S> rootProperty, Function<S, ? extends ObservableValue<T>> childPropertyAccessor) {
            this.rootProperty = rootProperty;
            this.childPropertyAccessor = childPropertyAccessor;
            bind(rootProperty);
        }

        @Override
        protected T computeValue() {
            if (childProperty != null) {
                unbind(childProperty);
                childProperty = null;
            }

            S rootPropertyValue = rootProperty.getValue();
            if (rootPropertyValue == null) {
                return null;
            }

            childProperty = childPropertyAccessor.apply(rootPropertyValue);
            if (childProperty == null) {
                return null;
            }

            bind(childProperty);
            return childProperty.getValue();
        }
    }

    class MappedBinding<S, T> extends ObjectBinding<T> {

        private final ObservableValue<S> rootProperty;
        private final Function<S, ? extends T> childValueAccessor;

        MappedBinding(ObservableValue<S> rootProperty, Function<S, ? extends T> childValueAccessor) {
            this.rootProperty = rootProperty;
            this.childValueAccessor = childValueAccessor;
            bind(rootProperty);
        }

        @Override
        protected T computeValue() {
            S rootPropertyValue = rootProperty.getValue();
            if (rootPropertyValue == null) {
                return null;
            }
            return childValueAccessor.apply(rootPropertyValue);
        }
    }
}