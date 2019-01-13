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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class DataUtilTest {

  @Test
  public void testReadAndWriteBytes() {
    var buffer = ByteBuffer.allocate(4);
    DataUtil.writeBytes(buffer, new byte[]{21, 42});
    buffer.flip();

    assertEquals(2, DataUtil.readUnsignedShort(buffer));
    assertEquals(21, buffer.get());
    assertEquals(42, buffer.get());
    assertFalse(buffer.hasRemaining());

    buffer.rewind();

    var data = DataUtil.readBytes(buffer).get();
    assertEquals(2, data.length);
    assertEquals(21, data[0]);
    assertEquals(42, data[1]);
    assertFalse(buffer.hasRemaining());

    assertEquals(4, DataUtil.estimateBytes(new byte[]{21, 42}));
  }

  @Test
  public void testReadAndWriteCollection() {
    var collection = List.of(21, 42);
    var buffer = ByteBuffer.allocate(10);
    DataUtil.writeCollection(buffer, collection, ByteBuffer::putInt);
    buffer.flip();

    assertEquals(2, DataUtil.readUnsignedShort(buffer));
    assertEquals(21, buffer.getInt());
    assertEquals(42, buffer.getInt());
    assertFalse(buffer.hasRemaining());

    buffer.rewind();

    var data = DataUtil.readCollection(buffer, new ArrayList<>(), ByteBuffer::getInt);
    assertEquals(2, data.size());
    assertEquals(21, (int) data.get(0));
    assertEquals(42, (int) data.get(1));
    assertFalse(buffer.hasRemaining());

    assertEquals(10, DataUtil.estimateCollection(collection, (i) -> 4));
  }

  @Test
  public void testReadWriteString() {
    var str = "Test String";
    var buffer = ByteBuffer.allocate(13);
    DataUtil.writeString(buffer, str);
    buffer.flip();

    assertEquals(11, DataUtil.readUnsignedShort(buffer));
    for (var i = 0; i < str.length(); ++i) {
      assertEquals(str.charAt(i), (char) buffer.get()); // only works for ascii
    }
    assertFalse(buffer.hasRemaining());

    buffer.rewind();

    var data = DataUtil.readString(buffer);
    assertEquals(str, data.get());
  }

  @Test
  public void testReadWriteUnsignedByte() {
    var buffer = ByteBuffer.allocate(1);
    DataUtil.writeUnsignedByte(buffer, (short) (Byte.MAX_VALUE + 1));
    buffer.flip();

    assertEquals(Byte.MIN_VALUE, buffer.get());

    buffer.rewind();

    assertEquals((short) (Byte.MAX_VALUE + 1), DataUtil.readUnsignedByte(buffer));

    buffer.rewind();

    assertThrows(IllegalArgumentException.class,
        () -> DataUtil.writeUnsignedByte(buffer, (short) -1));
    assertThrows(IllegalArgumentException.class,
        () -> DataUtil.writeUnsignedByte(buffer, (short) 257));
  }

  @Test
  public void testReadWriteUnsignedShort() {
    var buffer = ByteBuffer.allocate(2);
    DataUtil.writeUnsignedShort(buffer, Short.MAX_VALUE + 1);
    buffer.flip();

    assertEquals(Short.MIN_VALUE, buffer.getShort());

    buffer.rewind();

    assertEquals(Short.MAX_VALUE + 1, DataUtil.readUnsignedShort(buffer));

    buffer.rewind();

    assertThrows(IllegalArgumentException.class, () -> DataUtil.writeUnsignedShort(buffer, -1));
    assertThrows(IllegalArgumentException.class, () -> DataUtil.writeUnsignedShort(buffer, 65537));
  }
}
