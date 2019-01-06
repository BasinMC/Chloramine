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
 * Represents a dependency to one or more compatible versions of a single resource.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface Dependency extends BinarySerializable {

  /**
   * <p>Retrieves the product-specific identifier of the target resource.</p>
   *
   * <p>Identifiers are expected to adhere to the Java package format (e.g. {@code
   * org.example.package.module}).</p>
   *
   * <p>This value is guaranteed to be unique within the ecosystem of a single product and may
   * never conflict with other loaded extensions.</p>
   *
   * @return a resource identifier.
   */
  @NonNull
  String getIdentifier();

  /**
   * <p>Retrieves a service-unique implementation version.</p>
   *
   * <p>This value is expected to provide a range which is compatible with the <a
   * href="https://semver.org/">Semantic Versioning</a> specification. Specifically, ranges will be
   * donated via the {@code (}, {@code [}, {@code )} {@code ]} bound indicators where:</p>
   *
   * <ul>
   * <li>{@code (1.0.0} indicates a range which includes v1.0.0 and everything beyond</li>
   * <li>{@code [1.0.0} indicates a range which includes everything beyond v1.0.0</li>
   * <li>{@code 1.0.0)} indicates a range which includes v1.0.0 and everything below</li>
   * <li>{@code 1.0.0]} indicates a range which includes anything below v1.0.0</li>
   * </ul>
   *
   * <p>Additionally a full bound may be provided via two parameters (separated by the comma
   * ({@code ,}) character) where:</p>
   *
   * <ul>
   * <li>{@code (1.0.0,2.0.0]} indicates a range which includes v1.0.0 and everything up until
   * v2.0.0 (excluding)</li>
   * <li>{@code [1.0.0,2.0.0)} indicates a range which includes everything beyond v1.0.0 up until
   * v2.0.0 (including)</li>
   * </ul>
   *
   * <p>The range parameters may, of course, be combined in any way shape or form as outlined
   * above.</p>
   *
   * <p>Note that a single version number without a prefix or suffix (e.g. {@code 1.0.0}) is
   * considered to only match a single version for the purposes of this definition.</p>
   *
   * @return an implementation version
   */
  @NonNull
  String getVersionRange();

  /**
   * <p>Evaluates whether this dependency is optional.</p>
   *
   * <p>When a dependency is marked optional, it will not prevent the loading of the extension if
   * the referenced resource is unavailable.</p>
   *
   * @return true if optional, false otherwise.
   */
  boolean isOptional();
}
