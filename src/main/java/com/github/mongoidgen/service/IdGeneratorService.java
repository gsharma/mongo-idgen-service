package com.github.mongoidgen.service;

public interface IdGeneratorService<T> {
    public T generate();

    public void start();

    public void stop();

    public boolean ping();
}
