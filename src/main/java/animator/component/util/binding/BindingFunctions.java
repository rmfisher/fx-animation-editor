package animator.component.util.binding;

import javafx.beans.WeakListener;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionModel;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BindingFunctions {

    public static <T, U> void bind(List<T> boundList, ObservableList<U> sourceList, Function<U, T> mapper) {
        ListSynchronizer<T, U> listener = new ListSynchronizer<>(boundList, mapper);
        sourceList.removeListener(listener);
        sourceList.addListener(listener);
        boundList.clear();
        boundList.addAll(map(sourceList, mapper));
    }

    // Just adds listeners, use with care.
    public static <T> void bind(SelectionModel<T> selectionModel, ObjectProperty<T> selectedItem) {
        selectionModel.selectedItemProperty().addListener((v, o, n) -> {
            selectedItem.set(n);
        });
        selectedItem.addListener((v, o, n) -> {
            selectionModel.select(n);
        });
        selectionModel.select(selectedItem.get());
    }

    public static DoubleBinding round(NumberBinding input) {
        return new RoundBinding(input);
    }

    private static <T, U> List<T> map(List<? extends U> sourceList, Function<U, T> mapper) {
        return sourceList.stream().map(mapper).collect(Collectors.toList());
    }

    private static class ListSynchronizer<T, U> implements ListChangeListener<U>, WeakListener {

        private final WeakReference<List<T>> boundListWeakRef;
        private Function<? super U, ? extends T> mapper;

        ListSynchronizer(List<T> boundList, Function<? super U, ? extends T> mapper) {
            this.mapper = mapper;
            boundListWeakRef = new WeakReference<>(boundList);
        }

        @Override
        public void onChanged(ListChangeListener.Change<? extends U> listChange) {
            List<T> boundList = boundListWeakRef.get();

            if (boundList == null) {
                listChange.getList().removeListener(this);
                return;
            }

            while (listChange.next()) {
                if (listChange.wasRemoved()) {
                    boundList.subList(listChange.getFrom(), listChange.getFrom() + listChange.getRemovedSize()).clear();
                }
                if (listChange.wasAdded()) {
                    boundList.addAll(listChange.getFrom(), map(listChange.getAddedSubList(), mapper));
                }
            }
        }

        @Override
        public boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }

            List<T> boundList = boundListWeakRef.get();
            if (boundList == null) {
                return false;
            }

            if (otherObject instanceof ListSynchronizer) {
                ListSynchronizer<?, ?> otherListener = (ListSynchronizer<?, ?>) otherObject;
                List<?> otherBoundList = otherListener.boundListWeakRef.get();
                return boundList == otherBoundList;
            }

            return false;
        }

        @Override
        public int hashCode() {
            List<T> boundList = boundListWeakRef.get();
            return boundList != null ? boundList.hashCode() : 0;
        }

        @Override
        public boolean wasGarbageCollected() {
            return boundListWeakRef.get() == null;
        }
    }

    private static class RoundBinding extends DoubleBinding {

        private final NumberBinding input;

        public RoundBinding(NumberBinding input) {
            this.input = input;
            super.bind(input);
        }

        @Override
        protected double computeValue() {
            return Math.round(input.doubleValue());
        }
    }
}
