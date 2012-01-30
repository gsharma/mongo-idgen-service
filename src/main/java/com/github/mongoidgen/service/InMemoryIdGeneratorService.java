package com.github.mongoidgen.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InMemoryIdGeneratorService implements IdGeneratorService<String> {
    @Override
    public String generate() {
        String id = null;
        try {
            id = UUIDGen.makeType1UUIDFromHost(InetAddress.getLocalHost()).toString();
        } catch (UnknownHostException exception) {
            throw new RuntimeException(exception);
        }
        return id;
    }

    @Override
    public void start() {
        // no op
    }

    @Override
    public void stop() {
        // no op
    }

    @Override
    public boolean ping() {
        return true;
    }
}