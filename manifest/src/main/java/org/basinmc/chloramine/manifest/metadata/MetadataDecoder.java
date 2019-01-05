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
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import org.basinmc.chloramine.manifest.error.MetadataException;

/**
 * Provides a decoder capable of de-serializing arbitrary format revisions into their POJO
 * representations.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface MetadataDecoder {

  /**
   * Evaluates whether this metadata factory is capable of decoding the supplied format revision.
   *
   * @param version a version number.
   * @return true if version is accepted, false otherwise.
   */
  boolean accepts(short version);

  /**
   * Decodes a serialized metadata representation into its POJO representation.
   *
   * @param version the format revision.
   * @param buffer a buffer containing the serialized version.
   * @return a metadata POJO.
   * @throws MetadataException when the decoder fails to convert the data.
   */
  @NonNull
  Metadata decode(short version, @NonNull ByteBuffer buffer) throws MetadataException;

  /**
   * Retrieves a metadata decoder capable of de-serializing the specified format revision.
   *
   * @param version a format revision.
   * @return a decoder or, if none accepts the specified revision, an empty optional.
   */
  @NonNull
  static Optional<MetadataDecoder> get(short version) {
    return ServiceLoader.load(MetadataDecoder.class).stream()
        .map(Provider::get)
        .filter((decoder) -> decoder.accepts(version))
        .findAny();
  }
}
