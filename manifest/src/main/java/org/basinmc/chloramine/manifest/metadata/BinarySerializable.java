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
import org.basinmc.chloramine.manifest.error.ManifestEncoderException;

/**
 * Provides a base interface for objects which may be serialized into an arbitrary binary format.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface BinarySerializable {

  /**
   * Retrieves the estimated serialized length of this object (in bytes).
   *
   * @return a serialized length.
   */
  long getSerializedLength();

  /**
   * Encodes this object into its binary representation.
   *
   * @param buffer a buffer to serialize into.
   * @throws ManifestEncoderException when encoding into binary fails.
   */
  void serialize(@NonNull ByteBuffer buffer) throws ManifestEncoderException;
}
