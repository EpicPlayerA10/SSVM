package dev.xdark.ssvm.util;

import lombok.experimental.UtilityClass;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * Hacks and utils for io java classes
 * @author Justus Garbe
 */
@UtilityClass
public class IOUtil {

    /**
     * Returns either the file descriptor of a {@link FileDescriptor} object or a handle depending on the platform.
     * @param fd the file descriptor
     * @return the file descriptor or handle
     */
    public long getHandleOrFd(FileDescriptor fd) {
		return fdHandleOffset == -1
			? UnsafeUtil.get().getInt(fd, fdFieldOffset)
			: UnsafeUtil.get().getLong(fd, fdHandleOffset);
	}

	private static final long fdFieldOffset;
	private static final long fdHandleOffset;

    static {
		long fdOffset = -1;
		long handleOffset = -1;
        try {
            Field fd = FileDescriptor.class.getDeclaredField("fd");
			fdOffset = UnsafeUtil.get().objectFieldOffset(fd);
        } catch (NoSuchFieldException e) {
            // ignore
        }
        try {
			Field handle = FileDescriptor.class.getDeclaredField("handle");
			handleOffset = UnsafeUtil.get().objectFieldOffset(handle);
        } catch (NoSuchFieldException e) {
            // ignore
        }
		fdFieldOffset = fdOffset;
		fdHandleOffset = handleOffset;
    }

    /**
     * @param inputStream Input stream, may be {@code null}.
     * @return Bytes of stream, or {@code null} if the stream was {@code null}
     * @throws IOException When the stream cannot be read from.
     */
    public static byte[] readAll(InputStream inputStream) throws IOException {
        if (inputStream == null) return null;
        int bufferSize = 2048;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] data = new byte[bufferSize];
            int bytesRead;
            int readCount = 0;
            while ((bytesRead = inputStream.read(data, 0, bufferSize)) != -1) {
                outputStream.write(data, 0, bytesRead);
                readCount++;
            }
            outputStream.flush();
            if (readCount == 1) {
                return data;
            }
            return outputStream.toByteArray();
        } finally {
            inputStream.close();
        }
    }
}
