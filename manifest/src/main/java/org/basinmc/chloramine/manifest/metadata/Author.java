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

/**
 * Represents an extension author or contributor's credit information.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface Author {

  /**
   * <p>Retrieves the author's display name.</p>
   *
   * <p>This may either be the full real name of an author or an alias (depending on whether an
   * alias ({@link #getAlias()}) has been provided or not.</p>
   *
   * @return a display name.
   */
  @NonNull
  String getName();

  /**
   * <p>Retrieves the author's alias.</p>
   *
   * <p>This value is only set when a real name has been provided ({@link #getName()}) and an alias
   * is supposed to be displayed as well.</p>
   *
   * @return an alias.
   */
  @NonNull
  Optional<String> getAlias();
}
