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
import org.basinmc.chloramine.manifest.metadata.Service;
import org.basinmc.chloramine.manifest.util.DataUtil;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ServiceV0 implements Service {

  private final String identifier;
  private final String version;

  public ServiceV0(@NonNull String identifier, @NonNull String version) {
    this.identifier = identifier;
    this.version = version;
  }

  public ServiceV0(@NonNull ByteBuffer buffer) throws MetadataDecoderException {
    this.identifier = DataUtil.readString(buffer)
        .orElseThrow(() -> new MetadataDecoderException(
            "Missing value for required service field: identifier"));
    this.version = DataUtil.readString(buffer)
        .orElseThrow(() -> new MetadataDecoderException(
            "Missing value for required service field: version"));
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
  public String getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getSerializedLength() {
    return DataUtil.estimateString(this.identifier) + DataUtil.estimateString(this.version);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void serialize(@NonNull ByteBuffer buffer) {
    DataUtil.writeString(buffer, this.identifier);
    DataUtil.writeString(buffer, this.version);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return this.identifier + ":" + this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ServiceV0)) {
      return false;
    }
    ServiceV0 serviceV0 = (ServiceV0) o;
    return Objects.equals(this.identifier, serviceV0.identifier) &&
        Objects.equals(this.version, serviceV0.version);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.identifier, this.version);
  }
}
