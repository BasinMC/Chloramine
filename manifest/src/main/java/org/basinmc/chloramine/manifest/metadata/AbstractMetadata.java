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
package org.basinmc.chloramine.manifest.metadata;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.nio.ByteBuffer;
import java.util.Objects;
import org.basinmc.chloramine.manifest.error.ManifestEncoderException;
import org.basinmc.chloramine.manifest.util.DataUtil;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public abstract class AbstractMetadata implements Metadata {

  private final short formatVersion;

  public AbstractMetadata(short formatVersion) {
    this.formatVersion = formatVersion;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public short getFormatVersion() {
    return this.formatVersion;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getSerializedLength() {
    return 1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void serialize(@NonNull ByteBuffer buffer) throws ManifestEncoderException {
    DataUtil.writeUnsignedByte(buffer, this.formatVersion);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AbstractMetadata)) {
      return false;
    }
    AbstractMetadata that = (AbstractMetadata) o;
    return this.formatVersion == that.formatVersion;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.formatVersion);
  }
}
