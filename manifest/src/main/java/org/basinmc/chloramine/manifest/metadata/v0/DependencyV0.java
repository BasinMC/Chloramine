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
package org.basinmc.chloramine.manifest.metadata.v0;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.nio.ByteBuffer;
import java.util.Objects;
import org.basinmc.chloramine.manifest.error.MetadataDecoderException;
import org.basinmc.chloramine.manifest.metadata.Dependency;
import org.basinmc.chloramine.manifest.util.DataUtil;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class DependencyV0 implements Dependency {

  private final String identifier;
  private final String versionRange;
  private final boolean optional;

  public DependencyV0(@NonNull String identifier, @NonNull String versionRange) {
    this(identifier, versionRange, false);
  }

  public DependencyV0(@NonNull String identifier, @NonNull String versionRange, boolean optional) {
    this.identifier = identifier;
    this.versionRange = versionRange;
    this.optional = optional;
  }

  public DependencyV0(@NonNull ByteBuffer buffer) throws MetadataDecoderException {
    this.identifier = DataUtil.readString(buffer)
        .orElseThrow(() -> new MetadataDecoderException(
            "Missing value for required dependency field: identifier"));
    this.versionRange = DataUtil.readString(buffer)
        .orElseThrow(() -> new MetadataDecoderException(
            "Missing value for required dependency field: versionRange"));
    this.optional = buffer.get() == 1;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String getIdentifier() {
    return this.identifier;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String getVersionRange() {
    return this.versionRange;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isOptional() {
    return this.optional;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getSerializedLength() {
    return DataUtil.estimateString(this.identifier) + DataUtil.estimateString(this.versionRange)
        + 1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void serialize(@NonNull ByteBuffer buffer) {
    DataUtil.writeString(buffer, this.identifier);
    DataUtil.writeString(buffer, this.versionRange);
    buffer.put((byte) (this.optional ? 1 : 0));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    var builder = new StringBuilder();
    if (!this.optional) {
      builder.append("!");
    }

    builder.append(this.identifier);
    builder.append(" (").append(this.versionRange).append(")");

    return builder.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DependencyV0)) {
      return false;
    }
    DependencyV0 that = (DependencyV0) o;
    return this.optional == that.optional &&
        Objects.equals(this.identifier, that.identifier) &&
        Objects.equals(this.versionRange, that.versionRange);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.identifier, this.versionRange, this.optional);
  }
}
