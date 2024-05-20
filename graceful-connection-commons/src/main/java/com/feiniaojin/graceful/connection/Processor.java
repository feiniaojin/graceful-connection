package com.feiniaojin.graceful.connection;

public interface Processor<IN, OUT> {

    OUT process(IN in);
}
