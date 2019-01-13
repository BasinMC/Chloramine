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
package org.basinmc.chloramine.manifest.util;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Optional;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public final class MappingUtil {

  private MappingUtil() {
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public static <I, O, E extends Throwable> Optional<O> map(@NonNull Optional<I> in,
      @NonNull ThrowingFunction<I, O, E> conversionFunction) throws E {
    if (!in.isPresent()) {
      return Optional.empty();
    }

    return Optional.of(conversionFunction.apply(in.get()));
  }

  @FunctionalInterface
  public interface ThrowingFunction<I, O, E extends Throwable> {

    O apply(I input) throws E;
  }
}
