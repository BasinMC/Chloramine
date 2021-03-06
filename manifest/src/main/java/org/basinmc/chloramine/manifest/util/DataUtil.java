/*
 * Copyright 2019 Johannes Donath <johannesd@torchmind.com>
 * and other copyright owners as documented in the project's IP log.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.basinmc.chloramine.manifest.util;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.ToLongFunction;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public final class DataUtil {

  public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

  private DataUtil() {
  }

  public static long estimateBytes(@Nullable byte[] data) {
    return 2 + (data != null ? data.length : 0);
  }

  @NonNull
  @SuppressWarnings("OptionalContainsCollection")
  public static Optional<byte[]> readBytes(@NonNull ByteBuffer buffer) {
    var length = readUnsignedShort(buffer);
    if (length == 0) {
      return Optional.empty();
    }

    var data = new byte[length];
    buffer.get(data);

    return Optional.of(data);
  }

  public static void writeBytes(@NonNull ByteBuffer buffer, @Nullable byte[] data) {
    writeUnsignedShort(buffer, data != null ? data.length : 0);
    if (data != null) {
      buffer.put(data);
    }
  }

  public static <V, C extends Collection<V>> long estimateCollection(@NonNull C collection,
      @NonNull ToLongFunction<V> estimator) {
    return 2 + collection.stream()
        .mapToLong(estimator)
        .sum();
  }

  @NonNull
  public static <V, C extends Collection<V>, E extends Throwable> C readCollection(
      @NonNull ByteBuffer buffer, @NonNull C collection,
      @NonNull ValueDecoder<V, E> valueDecoder) throws E {
    var length = readUnsignedShort(buffer);
    for (var i = 0; i < length; ++i) {
      collection.add(valueDecoder.decode(buffer));
    }
    return collection;
  }

  public static <V, C extends Collection<V>> void writeCollection(@NonNull ByteBuffer buffer,
      @NonNull C collection, @NonNull BiConsumer<ByteBuffer, V> valueEncoder) {
    writeUnsignedShort(buffer, collection.size());
    collection.forEach((value) -> valueEncoder.accept(buffer, value));
  }

  public static long estimateString(@Nullable String value) {
    return estimateString(value, DEFAULT_CHARSET);
  }

  public static long estimateString(@Nullable String value, @NonNull Charset encoding) {
    return estimateBytes(value != null ? value.getBytes(encoding) : null);
  }

  @NonNull
  public static Optional<String> readString(@NonNull ByteBuffer buffer) {
    return readString(buffer, DEFAULT_CHARSET);
  }

  public static void writeString(@NonNull ByteBuffer buffer, @Nullable String value) {
    writeString(buffer, value, DEFAULT_CHARSET);
  }

  @NonNull
  public static Optional<String> readString(@NonNull ByteBuffer buffer, @NonNull Charset encoding) {
    return readBytes(buffer)
        .map((bytes) -> new String(bytes, encoding));
  }

  public static void writeString(@NonNull ByteBuffer buffer, @Nullable String value,
      @NonNull Charset encoding) {
    writeBytes(buffer, value != null ? value.getBytes(encoding) : null);
  }

  public static short readUnsignedByte(@NonNull ByteBuffer buffer) {
    return (short) (buffer.get() & 0xFF);
  }

  public static void writeUnsignedByte(@NonNull ByteBuffer buffer, short value) {
    if (value < 0 || value > 256) {
      throw new IllegalArgumentException("Value exceeds field bounds: " + value);
    }

    buffer.put((byte) (value & 0xFF));
  }

  public static int readUnsignedShort(@NonNull ByteBuffer buffer) {
    return buffer.getShort() & 0xFFFF;
  }

  public static void writeUnsignedShort(@NonNull ByteBuffer buffer, int value) {
    if (value < 0 || value >= 65536) {
      throw new IllegalArgumentException("Value exceeds field bounds: " + value);
    }

    buffer.putShort((short) (value & 0xFFFF));
  }

  @FunctionalInterface
  public interface ValueDecoder<O, E extends Throwable> {

    O decode(@NonNull ByteBuffer in) throws E;
  }
}
