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
package org.basinmc.chloramine.cli;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.List;
import org.basinmc.chloramine.cli.commands.CommandHandler;
import org.basinmc.chloramine.cli.commands.HelpCommand;
import org.basinmc.chloramine.cli.commands.InfoCommand;
import org.basinmc.chloramine.cli.commands.VersionCommand;
import org.basinmc.chloramine.cli.commands.WrapCommand;
import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.UnmatchedArgumentException;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Command(
    name = "chloramine",
    description = "Inspects, creates and modifies Basin Extension Containers (BECs)",
    subcommands = {
        HelpCommand.class,
        InfoCommand.class,
        VersionCommand.class,
        WrapCommand.class
    }
)
public class Chloramine {

  public static void main(@NonNull String[] arguments) throws Exception {
    AnsiConsole.systemInstall();

    var instance = new Chloramine();
    var commandLine = new CommandLine(instance);

    try {
      var commands = commandLine.parse(arguments);
      instance.invoke(commands);
    } catch (UnmatchedArgumentException ex) {
      System.err.println("Malformed command invocation: " + ex.getMessage());
      System.err.println();
      CommandLine.usage(instance, System.err);

      System.exit(1);
    } finally {
      AnsiConsole.systemUninstall();
    }
  }

  private void invoke(@NonNull List<CommandLine> commands) throws Exception {
    if (commands.size() < 2) {
      CommandLine.usage(this, System.out);
      return;
    }

    var rootCommand = commands.get(0);
    if (!(rootCommand.getCommand() instanceof Chloramine)) {
      throw new IllegalStateException(
          "Expected root command of type " + Chloramine.class.getName() + " but got " + rootCommand
              .getClass().getName());
    }

    var subCommand = commands.get(1);
    if (!(subCommand.getCommand() instanceof CommandHandler)) {
      throw new IllegalStateException(
          "Expected command of type " + CommandHandler.class.getName() + " but got " + subCommand
              .getClass().getName());
    }

    ((CommandHandler) subCommand.getCommand()).execute(rootCommand, subCommand,
        rootCommand.getCommand());
  }
}
