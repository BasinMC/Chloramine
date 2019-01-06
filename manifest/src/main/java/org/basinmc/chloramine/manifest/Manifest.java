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
package org.basinmc.chloramine.manifest;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Consumer;
import org.basinmc.chloramine.manifest.error.ManifestEncoderException;
import org.basinmc.chloramine.manifest.error.ManifestException;
import org.basinmc.chloramine.manifest.error.ManifestHeaderException;
import org.basinmc.chloramine.manifest.error.MetadataVersionException;
import org.basinmc.chloramine.manifest.metadata.BinarySerializable;
import org.basinmc.chloramine.manifest.metadata.Metadata;
import org.basinmc.chloramine.manifest.metadata.MetadataBuilderFactory;
import org.basinmc.chloramine.manifest.metadata.MetadataDecoder;
import org.basinmc.chloramine.manifest.util.DataUtil;

/**
 * Represents an extension container manifest.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class Manifest implements BinarySerializable {

  /**
   * Defines the magic number which is prepended to all extension containers.
   */
  public static final int MAGIC_NUMBER = 0x0DEBAC1E;

  /**
   * Defines the total amount of bytes present within the manifest header.
   */
  public static final int HEADER_LENGTH = 30;

  private final int flags;
  private final Metadata metadata;

  private final long contentOffset;
  private final long contentLength;

  public Manifest(@NonNull ByteBuffer buffer) throws ManifestException {
    var magicNumber = buffer.getInt();
    if (magicNumber != MAGIC_NUMBER) {
      throw new ManifestHeaderException(String.format("Illegal magic number: 0x%08X", magicNumber));
    }

    this.flags = buffer.getShort() & 0xFFFF;

    var authenticationLength = buffer.getLong();
    var metadataLength = buffer.getLong();
    this.contentLength = buffer.getLong();
    this.contentOffset = HEADER_LENGTH + authenticationLength + metadataLength;

    if (authenticationLength > Integer.MAX_VALUE) {
      throw new ManifestHeaderException(String.format(
          "Illegal authentication section: Section exceeds maximum length (%d bytes > %d)",
          authenticationLength, Integer.MAX_VALUE));
    }
    if (metadataLength > Integer.MAX_VALUE) {
      throw new ManifestHeaderException(String.format(
          "Illegal metadata section: Section exceeds maximum length (%d bytes > %d)",
          metadataLength, Integer.MAX_VALUE));
    }

    // TODO: Add support for the authentication section once documented
    buffer.position(buffer.position() + (int) authenticationLength);

    var metadataBuffer = ByteBuffer.allocate((int) metadataLength);
    metadataBuffer.put(buffer);
    metadataBuffer.flip();

    var metadataVersion = buffer.get();
    this.metadata = MetadataDecoder.get(metadataVersion)
        .orElseThrow(() -> new MetadataVersionException(
            "Unsupported metadata format version: " + metadataVersion))
        .decode(metadataVersion, metadataBuffer);
  }

  public Manifest(int flags, Metadata metadata, long contentOffset, long contentLength) {
    this.flags = flags;
    this.metadata = metadata;
    this.contentOffset = contentOffset;
    this.contentLength = contentLength;
  }

  /**
   * <p>Retrieves a set of metadata flags which expose additional container information.</p>
   *
   * <p><strong>Note:</strong> Additional flags may be introduced within future versions without
   * incrementing the format version (given that these flags are purely for documentation
   * purposes).</p>
   *
   * @return a bit mask of flags.
   */
  public int getFlags() {
    return this.flags;
  }

  /**
   * Retrieves the container metadata.
   *
   * @return a representation of the metadata section.
   */
  @NonNull
  public Metadata getMetadata() {
    return this.metadata;
  }

  /**
   * Retrieves the absolute location of the actual container contents (in bytes).
   *
   * @return an offset.
   */
  public long getContentOffset() {
    return this.contentOffset;
  }

  /**
   * Retrieves the total content length (in bytes).
   *
   * @return a length.
   */
  public long getContentLength() {
    return this.contentLength;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getSerializedLength() {
    return HEADER_LENGTH + this.metadata.getSerializedLength();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void serialize(@NonNull ByteBuffer buffer) throws ManifestEncoderException {
    buffer.putInt(MAGIC_NUMBER);
    DataUtil.writeUnsignedShort(buffer, this.flags);
    buffer.putLong(0); // TODO: Authentication
    buffer.putLong(this.metadata.getSerializedLength());
    buffer.putLong(this.contentLength);
    this.metadata.serialize(buffer);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Manifest)) {
      return false;
    }
    Manifest manifest = (Manifest) o;
    return this.flags == manifest.flags &&
        this.contentOffset == manifest.contentOffset &&
        this.contentLength == manifest.contentLength &&
        Objects.equals(this.metadata, manifest.metadata);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.flags, this.metadata, this.contentOffset, this.contentLength);
  }

  /**
   * Provides a factory for arbitrary manifest POJOs.
   */
  public static class Builder {

    private int flags;
    private Metadata metadata;
    private long contentLength;

    @NonNull
    public Manifest build() {
      if (this.metadata == null) {
        throw new IllegalStateException("Missing manifest section: metadata");
      }

      return new Manifest(
          this.flags,
          this.metadata,
          HEADER_LENGTH + this.metadata.getSerializedLength(),
          this.contentLength
      );
    }

    /**
     * @throws IllegalArgumentException when the specified value is invalid.
     * @see Manifest#getFlags()
     */
    @NonNull
    public Builder setFlags(int flags) {
      this.flags = flags;
      return this;
    }

    /**
     * @throws IllegalArgumentException when the specified value is invalid.
     * @see Manifest#getMetadata()
     */
    @NonNull
    public Builder setMetadata(@NonNull Metadata metadata) {
      this.metadata = metadata;
      return this;
    }

    /**
     * Creates a new builder for the specified metadata version and passes it to a custom factory
     * method for configuration.
     *
     * @param version an arbitrary metadata version.
     * @param metadataFactory a metadata factory.
     * @throws UnsupportedOperationException when the specified metadata version is unsupported.
     */
    @NonNull
    public Builder createMetadata(short version,
        @NonNull Consumer<Metadata.Builder> metadataFactory) {
      var builder = MetadataBuilderFactory.get(version)
          .orElseThrow(() -> new UnsupportedOperationException(
              "Unsupported metadata format version: " + version))
          .newBuilder(version);
      metadataFactory.accept(builder);
      return this.setMetadata(builder.build());
    }

    /**
     * @throws IllegalArgumentException when the specified value is invalid.
     * @see Manifest#getContentLength()
     */
    @NonNull
    public Builder setContentLength(long contentLength) {
      this.contentLength = contentLength;
      return this;
    }
  }
}
