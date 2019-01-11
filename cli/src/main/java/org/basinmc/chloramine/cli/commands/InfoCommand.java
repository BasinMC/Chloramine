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
import java.util.stream.Collectors;
import org.basinmc.chloramine.cli.Chloramine;
import org.basinmc.chloramine.manifest.Manifest;
import org.basinmc.chloramine.manifest.error.ManifestException;
import picocli.CommandLine.Command;

/**
 * Displays a human-readable representation of the information contained within an existing
 * extension container file.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Command(
    name = "info",
    aliases = {"i", "open"},
    description = "Displays extension container information"
)
public class InfoCommand extends AbstractContainerCommand {

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(@NonNull Chloramine ctx) {
    try {
      var manifest = Manifest.read(this.containerPath);

      System.out.println("=== Header Section ===");
      System.out
          .println(String.format("Estimated Size: %,d byte(s)", manifest.getSerializedLength()));
      System.out.println(String.format("Container Flags: 0x%04X", manifest.getFlags()));
      System.out.println(String.format("Content Length: %,d byte(s)", manifest.getContentLength()));
      System.out.println();

      var metadata = manifest.getMetadata();
      System.out
          .println(String.format("=== Metadata Section (v%d) ===", metadata.getFormatVersion()));
      System.out.println("Product: " + metadata.getProductIdentifier());
      System.out.println("Environment Type: " + metadata.getEnvironmentType());
      System.out.println(String.format("Flags: 0x%04X", metadata.getFlags())); // TODO: Resolve flags
      System.out.println();

      System.out.println("Extension Id: " + metadata.getIdentifier());
      System.out.println("Version: " + metadata.getVersion());
      metadata.getDistributionUrl()
          .ifPresent((uri) -> System.out.println("Distribution URL: " + uri));
      metadata.getDocumentationUrl()
          .ifPresent((uri) -> System.out.println("Documentation URL: " + uri));
      metadata.getIssueReportingUrl()
          .ifPresent((uri) -> System.out.println("Issue Reporting URL: " + uri));
      System.out.println("Author(s): " + metadata.getAuthors().stream()
          .map(Object::toString)
          .collect(Collectors.joining(", ")));
      System.out.println("Contributor(s): " + metadata.getContributors().stream()
          .map(Object::toString)
          .collect(Collectors.joining(", ")));
      System.out.println();

      var services = metadata.getProvidedServices();
      if (!services.isEmpty()) {
        System.out.println("Service(s): " + services.stream()
            .map(Object::toString)
            .collect(Collectors.joining(", ")));
      }

      var extensionDependencies = metadata.getExtensionDependencies();
      if (!extensionDependencies.isEmpty()) {
        System.out.println("Extension Dependencies: " + extensionDependencies.stream()
            .map(Object::toString)
            .collect(Collectors.joining(", ")));
      }

      var serviceDependencies = metadata.getServiceDependencies();
      if (!serviceDependencies.isEmpty()) {
        System.out.println("Extension Dependencies: " + serviceDependencies.stream()
            .map(Object::toString)
            .collect(Collectors.joining(", ")));
      }
    } catch (IOException ex) {
      System.err.println("Failed to read container file: " + ex.getMessage());
      handleError(ex);
    } catch (ManifestException ex) {
      System.err.println("Malformed container manifest: " + ex.getMessage());
      handleError(ex);
    }
  }

  private static void handleError(@NonNull Throwable ex) {
    System.err.println();
    System.err.println(
        "Please forward this information to the rubber duck in charge if you believe this to be a bug:");
    System.err.println();
    ex.printStackTrace();
  }
}
