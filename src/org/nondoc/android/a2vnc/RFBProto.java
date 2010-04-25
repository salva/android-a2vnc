package org.nondoc.android.a2vnc;

import java.net.*;
import java.io.*;
import java.lang.*;

public class RFBProto {
    Socket socket;
    BufferedInputStream in;
    BufferedOutputStream out;
    int width;
    int height;
    String name;

    byte[] bin;

    public static final int AUTH_NONE = 1;

    public RFBProto(String host, int port) throws IOException {
        InetAddress addr = InetAddress.getByName(host);
        socket = new Socket(addr, port);
        in = new BufferedInputStream(socket.getInputStream());
        out = new BufferedOutputStream(socket.getOutputStream());
        bin = new byte[4*1024];
        
        init();
    }

    private void readBytes(int len) throws IOException {
        int off = 0;
        while (off < len) {
            int bytes = in.read(bin, off, len - off);
            System.out.println("bytes read: " + bytes + ", off: " + off + ", len: " + len);
            if (bytes > 0)
                off += bytes;
            else if (bytes == 0)
                try {
                    Thread.currentThread().sleep(1000);
                }
                catch (InterruptedException e) {}
            else
                throw new IOException();
        }
    }

    private final int readU16() throws IOException {
        return ((in.read() << 8) | in.read());
    }

    private final long readU32() throws IOException {
        return (((((((long)in.read() << 8) | in.read() ) << 8) | in.read() ) << 8) | in.read());
    }

    private final void writeString(String s) throws IOException {
        out.write(s.getBytes("ISO8859_1"));
    }
    
    private final void writeU8(int b) throws IOException {
        out.write((byte)b);
    }

    private final void writeU8(boolean b) throws IOException {
        if (b)
            writeU8(1);
        else
            writeU8(0);
    }

    private final void writeU16(int l) throws IOException {
        writeU8((l >> 8) & 0xff);
        writeU8(l & 0xff);
    }

    private final void writeU32(long l) throws IOException {
        writeU8((int)((l >> 24) & 0xff));
        writeU8((int)((l >> 16) & 0xff));
        writeU8((int)((l >> 8) & 0xff));
        writeU8((int)(l & 0xff));
    }

    private void flush() throws IOException {
        out.flush();
    }

    private void init() throws IOException {
        readBytes(12);
        if(bin[0] != 'R' || bin[1] != 'F' || bin[2] != 'B')
            throw new IOException("Bad remote protocol");
        // FIXME: protocol 3.3 here, upgrade!
        writeString("RFB 003.003\n");
        flush();

        // FIXME: protocol 3.3 here, upgrade!
        int auth = (int)readU32();
        switch (auth) {
        case AUTH_NONE:
            // ok!;
            break;
        default:
            throw new IOException("unsupported authentication");
        }

        writeU8(1); // shared connection
        flush();

        width = readU16();
        height = readU16();
        readBytes(16);
        int len = (int)readU32();
        readBytes((int)len);
        name = new String(bin, 0, len, "ISO8859_1");
    }

    public void discardInput() throws IOException {
        int available = in.available();
        if (available != 0) {
            System.out.println("discarding input, bytes: " + available);
            in.skip(available);
        }
    }

    public void sendKeyEvent(long key, boolean down) throws IOException  {
        writeU8(4);
        writeU8(down);
        writeU8(0);
        writeU8(0);
        writeU32(key);
        flush();
    }

    public void sendPointerEvent(byte buttonMask, int xpos, int ypos) throws IOException {
        writeU8(5);
        writeU8(buttonMask);
        writeU16(xpos);
        writeU16(ypos);
        flush();
    }
    
    public void sendFrameBufferUpdateRequest() throws IOException {
        writeU8(3);
        writeU8(1);
        writeU16(0);
        writeU16(1);
        writeU16(1);
        writeU16(1);
    }
}