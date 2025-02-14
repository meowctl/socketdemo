package gay.meowctl.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SocketDemo {
    private final SocketName socketName;

    public SocketDemo(SocketName socketName) {
        this.socketName = socketName;
    }

    public static void main(String[] args) throws IOException {
        setShutdownHook();

        boolean isUnix = false;

        for (String arg : args) {
            switch (arg) {
                case "--unix" -> isUnix = true;
            }
        }

        var socketDemo = new SocketDemo(new SocketName());

        while (true) {
            try {
                if (isUnix) {
                    socketDemo.printUnixSocket();
                } else {
                    socketDemo.printTcpSocket();
                }
            } catch (ClosedByInterruptException e) {
                break;
            } catch (UncheckedIOException e) {
                if (e.getCause() instanceof ClosedByInterruptException) {
                    break;
                }
                throw e;
            }
        }
    }

    private static void setShutdownHook() {
        var mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("exiting ...");

            mainThread.interrupt();
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public void printTcpSocket() throws IOException {
        var address = new InetSocketAddress(InetAddress.getLoopbackAddress(), 0);

        try (var socket = ServerSocketChannel.open(StandardProtocolFamily.INET).bind(address)) {
            printSocket(socket);
        }
    }

    public void printUnixSocket() throws IOException {
        var address = UnixDomainSocketAddress.of(
                Path.of(System.getProperty("java.io.tmpdir"), socketName.nextName()));

        try (var socket = ServerSocketChannel.open(StandardProtocolFamily.UNIX).bind(address)) {
            printSocket(socket);
        } finally {
            Files.deleteIfExists(address.getPath());
        }
    }

    public void printSocket(ServerSocketChannel socket) throws IOException {
        System.out.println(socket.getLocalAddress());

        try (SocketChannel conn = socket.accept();
             var reader = new BufferedReader(Channels.newReader(conn, StandardCharsets.UTF_8))) {

            reader.lines().forEach(System.out::println);
        }
    }
}
