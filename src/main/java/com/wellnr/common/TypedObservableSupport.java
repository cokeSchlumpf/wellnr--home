package com.wellnr.common;

import lombok.Value;

import java.util.*;
import java.util.concurrent.Executor;

public class TypedObservableSupport<T> implements TypedObservable<T> {

    private final Map<RegistrationImpl, TypedObserver<T>> observers = new HashMap<>();

    private final Executor executionContext;

    public TypedObservableSupport(Executor executionContext) {
        this.executionContext = executionContext;
    }

    public TypedObservableSupport() {
        this(null);
    }

    @Override
    public Registration addObserver(TypedObserver<T> observer) {
        var registration = new RegistrationImpl();
        this.observers.put(registration, observer);

        return registration;
    }

    public void fireEvent(T event) {
        if (Objects.isNull(executionContext)) {
            for (var observer : observers.values()) {
                observer.run(event);
            }
        } else {
            for (var observer : observers.values()) {
                executionContext.execute(() -> observer.run(event));
            }
        }
    }

    @Value
    private class RegistrationImpl implements Registration {

        String id;

        public RegistrationImpl() {
            this.id = UUID.randomUUID().toString();
        }

        @Override
        public void cancel() {
            observers.remove(this);
        }

    }

}
