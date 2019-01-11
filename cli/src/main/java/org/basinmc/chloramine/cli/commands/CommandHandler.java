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
package org.basinmc.chloramine.cli.commands;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.basinmc.chloramine.cli.Chloramine;
import picocli.CommandLine;

/**
 * <p>Represents an executable command which performs a single designated action.</p>
 *
 * <p>Each implementation may define as many (or as little) parameters as it wishes using Picocli
 * annotations.</p>
 *
 * <p>Commands are registered with the {@link Chloramine} root as sub-commands and are invoked when
 * detected (along with some context information represented by the {@link Chloramine} main
 * class)</p>
 *
 * <p>Implementations of this interface are <strong>not</strong> registered automatically.</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface CommandHandler {

  /**
   * Invokes the commands logic using the parameters within this type.
   *
   * @param ctx an execution context.
   * @throws Exception when the command execution fails for any reason.
   */
  default void execute(@NonNull CommandLine rootCommand, @NonNull CommandLine subCommand,
      @NonNull Chloramine ctx) throws Exception {
    this.execute(subCommand, ctx);
  }

  default void execute(@NonNull CommandLine subCommand, @NonNull Chloramine ctx) throws Exception {
    this.execute(ctx);
  }

  default void execute(@NonNull Chloramine chloramine) throws Exception {
    throw new UnsupportedOperationException("No such command implementation");
  }
}
