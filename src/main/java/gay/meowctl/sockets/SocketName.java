package gay.meowctl.sockets;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketName {
    private static final String PREFIX = "gb-";
    private static final SecureRandom RNG = new SecureRandom();

    private final long pid = ProcessHandle.current().pid();
    private final AtomicInteger counter = new AtomicInteger();

    public String nextName() {
        // pid (1~64bit) + counter (32bit) + random (8bit)
        BigInteger socketId = BigInteger
                .valueOf(pidOrRandom())
                .shiftLeft(32)
                .or(BigInteger.valueOf(counter.getAndIncrement()))
                .shiftLeft(8)
                .or(BigInteger.valueOf(RNG.nextInt(1<<8)));

        return PREFIX + socketId.toString(36);
    }

    public long pidOrRandom() {
        return pid != 0 ? pid : RNG.nextLong(1, Long.MAX_VALUE);
    }
}
