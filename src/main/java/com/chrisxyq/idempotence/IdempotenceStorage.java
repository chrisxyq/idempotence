package com.chrisxyq.idempotence;

public interface IdempotenceStorage {
    boolean saveIfAbsent(String idempotenceId);
    void delete(String idempotenceId);
}
