package com.chrisxyq.idempotence;

/**
 * 设计实现一个通用的接口幂等框架
 */
public class Idempotence {
    private final IdempotenceStorage storage;

    public Idempotence(IdempotenceStorage storage) {
        this.storage = storage;
    }

    public boolean saveIfAbsent(String idempotenceId) {
        return storage.saveIfAbsent(idempotenceId);
    }

    public void delete(String idempotenceId) {
        storage.delete(idempotenceId);
    }
}
