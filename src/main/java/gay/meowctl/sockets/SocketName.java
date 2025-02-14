package gay.meowctl.sockets;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketName {
    private static final String PREFIX = "gbru-";
    private static final int TIMESTAMP_BITS = 41;
    private static final int COUNTER_BITS = 19;
    private static final long TIMESTAMP_MAX_VALUE = (1L << TIMESTAMP_BITS) - 1;
    private static final int COUNTER_MAX_VALUE = (1 << COUNTER_BITS) - 1;

    private final long pid = ProcessHandle.current().pid();
    private final AtomicInteger counter = new AtomicInteger();

    public String nextName() {
        long millis = System.currentTimeMillis() & TIMESTAMP_MAX_VALUE;
        int counterValue = counter.getAndIncrement() & COUNTER_MAX_VALUE;

        // pid (1~64bit) + timestamp + counter
        BigInteger socketId = BigInteger
                .valueOf(pid)
                .shiftLeft(TIMESTAMP_BITS + COUNTER_BITS)
                .or(BigInteger.valueOf(millis << COUNTER_BITS | counterValue));

        return PREFIX + socketId.toString(36);
    }
}
