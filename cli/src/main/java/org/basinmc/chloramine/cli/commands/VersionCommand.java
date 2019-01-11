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
import java.io.IOException;
import java.util.Properties;
import org.basinmc.chloramine.cli.Chloramine;
import picocli.CommandLine.Command;

/**
 * Displays version and build information on this application.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Command(
    name = "version",
    description = "Displays the tool version number and build info"
)
public class VersionCommand implements CommandHandler {

  private final String versionNumber;
  private final String versionNumberFull;
  private final String commitHash;
  private final String buildTimestamp;
  private final String ciJobNumber;

  public VersionCommand() throws IOException {
    try (var inputStream = this.getClass().getResourceAsStream("/chloramine-version.properties")) {
      var metadata = new Properties();
      metadata.load(inputStream);

      this.versionNumber = metadata.getProperty("application.version", "0.0.0");
      this.commitHash = metadata.getProperty("application.commitHash", "dev");
      this.buildTimestamp = metadata.getProperty("application.timestamp");
      this.ciJobNumber = metadata.getProperty("application.ciJobNumber");

      var versionBuilder = new StringBuilder(this.versionNumber);
      if (!"dev".equals(this.commitHash)) {
        versionBuilder.append("+git-").append(this.commitHash);
      } else {
        versionBuilder.append("+dev");
      }
      this.versionNumberFull = versionBuilder.toString();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(@NonNull Chloramine ctx) throws IOException {
    System.out.println(String.format("Chloramine v%s", this.versionNumberFull));
    System.out.println("Licensed under the terms of the Apache License, Version 2.0");
    System.out.println();
    System.out.println("Version: " + this.versionNumber);
    System.out.println("Commit Hash: " + this.commitHash);
    System.out.println("Build Timestamp: " + this.buildTimestamp);
    if (!this.ciJobNumber.isEmpty()) {
      System.out.println(String.format("CI Build: #%s", this.ciJobNumber));
    }
    System.out.println();
    System.out.println("No animals were harmed in the making of this command line utility");
  }
}
