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
package org.basinmc.chloramine.manifest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import org.basinmc.chloramine.manifest.error.ManifestEncoderException;
import org.basinmc.chloramine.manifest.error.ManifestException;
import org.basinmc.chloramine.manifest.util.DataUtil;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ManifestTest {

  /**
   * Evaluates whether the library is capable of reading a known good extension container.
   */
  @Test
  public void testRead() throws IOException, ManifestException {
    // TODO: Add a full test file as well
    try (var inputStream = ManifestTest.class.getResourceAsStream("/test.bec");
        var inputChannel = Channels.newChannel(inputStream)) {
      var manifest = Manifest.read(inputChannel);

      assertEquals(0x0000, manifest.getFlags());
      assertEquals(23, manifest.getContentLength());
      assertEquals(100, manifest.getContentOffset());

      var metadata = manifest.getMetadata();
      assertNotNull(metadata);
      assertEquals(0, metadata.getFormatVersion());
      assertEquals("org.basinmc.faucet", metadata.getProductIdentifier());
      assertEquals("java", metadata.getEnvironmentType());

      assertEquals("org.basinmc.test", metadata.getIdentifier());
      assertEquals("1.2.3", metadata.getVersion());
      assertFalse(metadata.getDistributionUrl().isPresent());
      assertFalse(metadata.getDocumentationUrl().isPresent());
      assertFalse(metadata.getIssueReportingUrl().isPresent());
      assertTrue(metadata.getAuthors().isEmpty());
      assertTrue(metadata.getContributors().isEmpty());

      assertTrue(metadata.getProvidedServices().isEmpty());
      assertTrue(metadata.getExtensionDependencies().isEmpty());
      assertTrue(metadata.getServiceDependencies().isEmpty());
    }
  }

  @Test
  public void testBuildAndSerialize() throws ManifestEncoderException {
    var manifest = Manifest.builder()
        .setContentLength(42)
        .setFlags(0x0001)
        .createMetadata((short) 0, (b) -> b.setProductIdentifier("org.basinmc.faucet")
            .setEnvironmentType("java")
            .setIdentifier("org.basinmc.test")
            .setVersion("1.2.3")
            .setDistributionUrl(URI.create("http://example.org/distribution"))
            .setDocumentationUrl(URI.create("http://example.org/documentation"))
            .setIssueReportingUrl(URI.create("http://example.org/issues"))
            .addAuthor("John Doe", "Mr Pineapple")
            .addAuthor("Jane Doe", "Mrs Pineapple")
            .addContributor("John Doe", "Mr Pineapple")
            .addContributor("Jane Doe", "Mrs Pineapple")
            .addService("org.basinmc.test.pineapple", "1.0.0")
            .addService("org.basinmc.test.potato", "2.1.3")
            .addExtensionDependency("org.basinmc.stuff", "(1.0.0,2.0.0]", false)
            .addExtensionDependency("org.basinmc.things", "[1.0.0,2.1.0]", true)
            .addServiceDependency("org.basinmc.beans", "(1.0.0,2.0.0]", false)
            .addServiceDependency("org.basinmc.croquettes", "[1.0.0,2.1.0]", true))
        .build();

    assertEquals(42, manifest.getContentLength());
    assertEquals(499, manifest.getContentOffset());
    assertEquals(499, manifest.getSerializedLength());

    var metadata = manifest.getMetadata();
    assertNotNull(metadata);
    assertEquals(0, metadata.getFormatVersion());
    assertEquals("org.basinmc.faucet", metadata.getProductIdentifier());
    assertEquals("java", metadata.getEnvironmentType());
    assertEquals("org.basinmc.test", metadata.getIdentifier());
    assertEquals("1.2.3", metadata.getVersion());
    assertEquals("http://example.org/distribution", metadata.getDistributionUrl().get().toString());
    assertEquals("http://example.org/documentation",
        metadata.getDocumentationUrl().get().toString());
    assertEquals("http://example.org/issues", metadata.getIssueReportingUrl().get().toString());
    assertEquals(2, metadata.getAuthors().size());
    assertTrue(metadata.getAuthors().stream().anyMatch(
        (author) -> "John Doe".equals(author.getName()) && "Mr Pineapple"
            .equals(author.getAlias().get())));
    assertTrue(metadata.getAuthors().stream().anyMatch(
        (author) -> "Jane Doe".equals(author.getName()) && "Mrs Pineapple"
            .equals(author.getAlias().get())));
    assertTrue(metadata.getContributors().stream().anyMatch(
        (author) -> "John Doe".equals(author.getName()) && "Mr Pineapple"
            .equals(author.getAlias().get())));
    assertTrue(metadata.getContributors().stream().anyMatch(
        (author) -> "Jane Doe".equals(author.getName()) && "Mrs Pineapple"
            .equals(author.getAlias().get())));

    assertTrue(metadata.getProvidedServices().stream().anyMatch(
        (service) -> "org.basinmc.test.pineapple".equals(service.getIdentifier()) && "1.0.0"
            .equals(service.getVersion())));
    assertTrue(metadata.getProvidedServices().stream().anyMatch(
        (service) -> "org.basinmc.test.potato".equals(service.getIdentifier()) && "2.1.3"
            .equals(service.getVersion())));
    assertTrue(metadata.getExtensionDependencies().stream().anyMatch(
        (ext) -> "org.basinmc.stuff".equals(ext.getIdentifier()) && "(1.0.0,2.0.0]"
            .equals(ext.getVersionRange()) && !ext.isOptional()));
    assertTrue(metadata.getExtensionDependencies().stream().anyMatch(
        (ext) -> "org.basinmc.things".equals(ext.getIdentifier()) && "[1.0.0,2.1.0]"
            .equals(ext.getVersionRange()) && ext.isOptional()));
    assertTrue(metadata.getServiceDependencies().stream().anyMatch(
        (service) -> "org.basinmc.beans".equals(service.getIdentifier()) && "(1.0.0,2.0.0]"
            .equals(service.getVersionRange()) && !service.isOptional()));
    assertTrue(metadata.getServiceDependencies().stream().anyMatch(
        (service) -> "org.basinmc.croquettes".equals(service.getIdentifier()) && "[1.0.0,2.1.0]"
            .equals(service.getVersionRange()) && service.isOptional()));

    assertTrue(manifest.getSerializedLength() < Integer.MAX_VALUE);
    var buffer = ByteBuffer.allocate((int) manifest.getSerializedLength());
    manifest.serialize(buffer);
    buffer.flip();

    assertEquals(Manifest.MAGIC_NUMBER, buffer.getInt());
    assertEquals(0x0001, DataUtil.readUnsignedShort(buffer));
    assertEquals(0, buffer.getLong());
    assertEquals(469, buffer.getLong());
    assertEquals(42, buffer.getLong());

    assertEquals(0, DataUtil.readUnsignedByte(buffer));
    assertEquals("org.basinmc.faucet", DataUtil.readString(buffer).get());
    assertEquals("java", DataUtil.readString(buffer).get());
    assertEquals(0x0000, DataUtil.readUnsignedShort(buffer));
    assertEquals("org.basinmc.test", DataUtil.readString(buffer).get());
    assertEquals("1.2.3", DataUtil.readString(buffer).get());
    assertEquals("http://example.org/distribution", DataUtil.readString(buffer).get());
    assertEquals("http://example.org/documentation", DataUtil.readString(buffer).get());
    assertEquals("http://example.org/issues", DataUtil.readString(buffer).get());
    assertEquals(2, DataUtil.readUnsignedShort(buffer)); // TODO: We should not test this here
    assertEquals("John Doe", DataUtil.readString(buffer).get());
    assertEquals("Mr Pineapple", DataUtil.readString(buffer).get());
    assertEquals("Jane Doe", DataUtil.readString(buffer).get());
    assertEquals("Mrs Pineapple", DataUtil.readString(buffer).get());
    assertEquals(2, DataUtil.readUnsignedShort(buffer)); // TODO: We should not test this here
    assertEquals("John Doe", DataUtil.readString(buffer).get());
    assertEquals("Mr Pineapple", DataUtil.readString(buffer).get());
    assertEquals("Jane Doe", DataUtil.readString(buffer).get());
    assertEquals("Mrs Pineapple", DataUtil.readString(buffer).get());

    assertEquals(2, DataUtil.readUnsignedShort(buffer)); // TODO: We should not test this here
    assertEquals("org.basinmc.test.pineapple", DataUtil.readString(buffer).get());
    assertEquals("1.0.0", DataUtil.readString(buffer).get());
    assertEquals("org.basinmc.test.potato", DataUtil.readString(buffer).get());
    assertEquals("2.1.3", DataUtil.readString(buffer).get());
    assertEquals(2, DataUtil.readUnsignedShort(buffer)); // TODO: We should not test this here
    assertEquals("org.basinmc.stuff", DataUtil.readString(buffer).get());
    assertEquals("(1.0.0,2.0.0]", DataUtil.readString(buffer).get());
    assertEquals(0x00, buffer.get());
    assertEquals("org.basinmc.things", DataUtil.readString(buffer).get());
    assertEquals("[1.0.0,2.1.0]", DataUtil.readString(buffer).get());
    assertEquals(0x01, buffer.get());
    assertEquals(2, DataUtil.readUnsignedShort(buffer)); // TODO: We should not test this here
    assertEquals("org.basinmc.beans", DataUtil.readString(buffer).get());
    assertEquals("(1.0.0,2.0.0]", DataUtil.readString(buffer).get());
    assertEquals(0x00, buffer.get());
    assertEquals("org.basinmc.croquettes", DataUtil.readString(buffer).get());
    assertEquals("[1.0.0,2.1.0]", DataUtil.readString(buffer).get());
    assertEquals(0x01, buffer.get());

    assertFalse(buffer.hasRemaining());
  }
}
