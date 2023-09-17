package com.wellnr.common;

import com.wellnr.common.functions.Procedure1;

@FunctionalInterface
public interface TypedObserver<T> extends Procedure1<T> {

}
