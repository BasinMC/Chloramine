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
import edu.umd.cs.findbugs.annotations.Nullable;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;
import org.basinmc.chloramine.manifest.error.MetadataDecoderException;
import org.basinmc.chloramine.manifest.metadata.Author;
import org.basinmc.chloramine.manifest.util.DataUtil;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class AuthorV0 implements Author {

  private final String name;
  private final String alias;

  public AuthorV0(@NonNull String name, @Nullable String alias) {
    this.name = name;
    this.alias = alias;
  }

  public AuthorV0(@NonNull ByteBuffer buffer) throws MetadataDecoderException {
    this.name = DataUtil.readString(buffer)
        .orElseThrow(
            () -> new MetadataDecoderException("Missing value for required author field: name"));
    this.alias = DataUtil.readString(buffer)
        .orElse(null);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public Optional<String> getAlias() {
    return Optional.ofNullable(this.alias);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getSerializedLength() {
    return DataUtil.estimateString(this.name) + DataUtil.estimateString(this.alias);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void serialize(@NonNull ByteBuffer buffer) {
    DataUtil.writeString(buffer, this.name);
    DataUtil.writeString(buffer, this.alias);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    if (this.alias == null) {
      return this.name;
    }

    return this.name + " (" + this.alias + ")";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AuthorV0)) {
      return false;
    }
    AuthorV0 authorV0 = (AuthorV0) o;
    return Objects.equals(this.name, authorV0.name) &&
        Objects.equals(this.alias, authorV0.alias);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.alias);
  }
}
