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
import java.io.FileNotFoundException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import org.basinmc.chloramine.cli.Chloramine;
import org.basinmc.chloramine.manifest.Manifest;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Command(
    name = "wrap",
    aliases = "w",
    description = "Wraps an arbitrary file inside an extension container"
)
public class WrapCommand implements CommandHandler {

  @Parameters(index = "0", paramLabel = "input-file", description = "Specifies the input file to wrap", arity = "1..*")
  private Path inputPath;
  @Parameters(index = "1", paramLabel = "container-file", description = "Specifies the output file", arity = "1..*")
  private Path containerPath;

  @Option(names = "format", description = "Manifest format revision")
  private short formatVersion;
  @Option(names = "target", description = "A target product")
  private String productId = "org.basinmc.faucet";
  @Option(names = "environment", description = "An extension environment")
  private String environment = "java";

  @Option(names = "id", description = "A globally unique extension identifier", required = true)
  private String extensionId;
  @Option(names = "version", description = "An extension specific revision", required = true)
  private String version;
  @Option(names = "distribution-url", description = "URI for a distribution website")
  private URI distributionUrl;
  @Option(names = "documentation-url", description = "URI for a documentation website")
  private URI documentationUrl;
  @Option(names = "issue-reporting-url", description = "URI for an issue reporting tool")
  private URI issueReportingUrl;
  @Option(names = "author", description = "Defines one or more consistent authors")
  private List<String> authors = new ArrayList<>();
  @Option(names = "contributor", description = "Defines one or more previous contributors")
  private List<String> contributors = new ArrayList<>();

  @Option(names = "service", description = "Defines one or more services")
  private List<String> services = new ArrayList<>();
  @Option(names = {"ext-dep", "extension-dependency", "dep",
      "dependency"}, description = "Defines one or more extension dependencies")
  private List<String> extensionDependencies = new ArrayList<>();
  @Option(names = {"srvc-dep",
      "service-dependency"}, description = "Defines one or more service dependencies")
  private List<String> serviceDependencies = new ArrayList<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(@NonNull Chloramine chloramine) throws Exception {
    try (var inputChannel = FileChannel.open(this.inputPath, StandardOpenOption.READ)) {
      System.out.println("==> Generating Manifest");

      var manifest = Manifest.builder()
          .setContentLength(inputChannel.size())
          .createMetadata(this.formatVersion, (b) -> {
            b.setProductIdentifier(this.productId)
                .setEnvironmentType(this.environment)
                // TODO: Flags
                .setIdentifier(this.extensionId)
                .setVersion(this.version)
                .setDistributionUrl(this.distributionUrl)
                .setDocumentationUrl(this.documentationUrl)
                .setIssueReportingUrl(this.issueReportingUrl);

            this.authors.forEach((name) -> b.addAuthor(name, null)); // TODO: Alias support
            this.contributors.forEach((name) -> b.addContributor(name, null));

            splitReference("service", this.services, b::addService);
            splitReference("extension dependency", this.extensionDependencies, b::addService);
            splitReference("service dependency", this.serviceDependencies, b::addService);
          })
          .build();
      System.out
          .println(String.format("Estimated Size: %,d byte(s)", manifest.getSerializedLength()));
      System.out.println();

      System.out.println("==> Writing Data");
      try (var outputChannel = FileChannel
          .open(this.containerPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
              StandardOpenOption.TRUNCATE_EXISTING)) {
        if (manifest.getSerializedLength() > Integer.MAX_VALUE) {
          throw new IllegalStateException(
              "Manifest exceeds maximum permitted size of " + manifest.getSerializedLength()
                  + " bytes");
        }

        var buffer = ByteBuffer.allocate((int) manifest.getSerializedLength());
        manifest.serialize(buffer);
        buffer.flip();
        outputChannel.write(buffer);

        inputChannel.transferTo(0, Long.MAX_VALUE, outputChannel);
        System.out.println(String.format("Written %,d byte(s)", outputChannel.size()));
      }
    } catch (FileNotFoundException ex) {
      System.err.println("No such file or directory: " + this.containerPath);
      System.exit(1);
    }
  }

  private static void splitReference(@NonNull String type, @NonNull Collection<String> elements,
      @NonNull BiConsumer<String, String> registrationFunc) {
    var it = elements.iterator();
    for (var i = 0; it.hasNext(); ++i) {
      var spec = it.next();

      var separatorIndex = spec.indexOf(':');
      if (separatorIndex == -1) {
        throw new IllegalStateException(
            "Illegal " + type + " spec #" + i + ": Missing version identifier");
      }

      registrationFunc
          .accept(spec.substring(0, separatorIndex), spec.substring(separatorIndex + 1));
    }
  }
}
