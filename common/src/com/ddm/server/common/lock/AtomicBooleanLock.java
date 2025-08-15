package com.ddm.server.common.lock;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
@NoArgsConstructor
public class AtomicBooleanLock<T> implements Serializable{
    private AtomicBoolean atomicBooleanLock = new AtomicBoolean(false);
    public boolean booleanLock(LockImpl lock) {
        try {
            do {
                if (atomicBooleanLock.compareAndSet(false,true)){
                    return lock.run();
                }
            }while (true);
        } finally {
            atomicBooleanLock.set(false);
        }
    }

    public T booleanValueLock(LockValueImpl lock) {
        try {
            do {
                if (atomicBooleanLock.compareAndSet(false,true)){
                    return (T) lock.run();
                }
            }while (true);
        } finally {
            atomicBooleanLock.set(false);
        }
    }
}
