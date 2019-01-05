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

/**
 * Represents a provided service implementation.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface Service {

  /**
   * <p>Retrieves a globally unique identifier for this service implementation.</p>
   *
   * <p>This value is expected to be formatted similarly to a Java class name (e.g. {@code
   * org.example.project.module.ServiceType}).</p>
   *
   * @return a globally unique service identifier.
   */
  @NonNull
  String getIdentifier();

  /**
   * <p>Retrieves a service-unique implementation version.</p>
   *
   * <p>This value is expected to the <a href="https://semver.org/">Semantic Versioning</a>
   * specification in order to provide support for parsing and version ranges within
   * dependencies.</p>
   *
   * <p>Versions are expected to be unique within the namespace of a single service (as denoted
   * by its identifier).</p>
   *
   * @return an implementation version
   */
  @NonNull
  String getVersion();
}
