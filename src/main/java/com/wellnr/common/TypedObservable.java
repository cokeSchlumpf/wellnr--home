package com.wellnr.common;

public interface TypedObservable<T> {

    /**
     * Adds a new observer to the observable.
     *
     * @param observer The observer to handle value changed events.
     */
    Registration addObserver(TypedObserver<T> observer);

}
