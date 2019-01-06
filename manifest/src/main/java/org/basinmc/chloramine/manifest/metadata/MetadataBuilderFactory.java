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
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;

/**
 * Provides a factory capable of creating builders for arbitrary metadata format versions.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface MetadataBuilderFactory {

  /**
   * Retrieves a metadata factory for the specified format revision.
   *
   * @param version a format revision.
   * @return a compatible builder, or if none is defined, an empty optional.
   */
  @NonNull
  static Optional<MetadataBuilderFactory> get(short version) {
    return ServiceLoader.load(MetadataBuilderFactory.class).stream()
        .map(Provider::get)
        .filter((builder) -> builder.accepts(version))
        .findAny();
  }

  /**
   * Evaluates whether this particular builder accepts the specified format revision.
   *
   * @param version a format revision.
   * @return true if format is compatible, false otherwise.
   */
  boolean accepts(short version);

  /**
   * Constructs a new empty builder for the specified version.
   *
   * @param version a compatible format revision.
   * @return an empty factory.
   */
  @NonNull
  Metadata.Builder newBuilder(short version);
}
