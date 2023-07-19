package com.chrisxyq.idempotence;

import java.util.UUID;

public class IdempotenceIdGenerator {
    public String generateId(){
        return UUID.randomUUID().toString();
    }
}
