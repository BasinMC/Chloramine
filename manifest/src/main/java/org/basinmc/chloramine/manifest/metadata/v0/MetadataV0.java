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
package org.basinmc.chloramine.manifest.metadata.v0;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.basinmc.chloramine.manifest.error.ManifestEncoderException;
import org.basinmc.chloramine.manifest.error.MetadataDecoderException;
import org.basinmc.chloramine.manifest.metadata.AbstractMetadata;
import org.basinmc.chloramine.manifest.metadata.Author;
import org.basinmc.chloramine.manifest.metadata.Dependency;
import org.basinmc.chloramine.manifest.metadata.Metadata;
import org.basinmc.chloramine.manifest.metadata.Service;
import org.basinmc.chloramine.manifest.util.DataUtil;
import org.basinmc.chloramine.manifest.util.MappingUtil;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class MetadataV0 extends AbstractMetadata {

  private final String productIdentifier;
  private final String environmentType;
  private final int flags;

  private final String identifier;
  private final String version;

  private final URI distributionUrl;
  private final URI documentationUrl;
  private final URI issueReportingUrl;
  private final List<AuthorV0> authors;
  private final List<AuthorV0> contributors;

  private final List<ServiceV0> providedServices;
  private final List<DependencyV0> extensionDependencies;
  private final List<DependencyV0> serviceDependencies;

  public MetadataV0(short formatVersion,
      @NonNull String productIdentifier,
      @NonNull String environmentType,
      int flags,
      @NonNull String identifier,
      @NonNull String version,
      @NonNull URI distributionUrl,
      @NonNull URI documentationUrl,
      @NonNull URI issueReportingUrl,
      @NonNull List<AuthorV0> authors,
      @NonNull List<AuthorV0> contributors,
      @NonNull List<ServiceV0> providedServices,
      @NonNull List<DependencyV0> extensionDependencies,
      @NonNull List<DependencyV0> serviceDependencies) {
    super(formatVersion);
    this.productIdentifier = productIdentifier;
    this.environmentType = environmentType;
    this.flags = flags;
    this.identifier = identifier;
    this.version = version;
    this.distributionUrl = distributionUrl;
    this.documentationUrl = documentationUrl;
    this.issueReportingUrl = issueReportingUrl;
    this.authors = new ArrayList<>(authors);
    this.contributors = new ArrayList<>(contributors);
    this.providedServices = new ArrayList<>(providedServices);
    this.extensionDependencies = new ArrayList<>(extensionDependencies);
    this.serviceDependencies = new ArrayList<>(serviceDependencies);
  }

  public MetadataV0(short formatVersion, @NonNull ByteBuffer buffer)
      throws MetadataDecoderException {
    super(formatVersion);

    this.productIdentifier = DataUtil.readString(buffer)
        .orElseThrow(() -> new MetadataDecoderException(
            "Missing value for required field: productIdentifier"));
    this.environmentType = DataUtil.readString(buffer)
        .orElseThrow(() -> new MetadataDecoderException(
            "Missing value for required field: environmentType"));
    this.flags = DataUtil.readUnsignedShort(buffer);

    this.identifier = DataUtil.readString(buffer)
        .orElseThrow(
            () -> new MetadataDecoderException("Missing value for required field: identifier"));
    this.version = DataUtil.readString(buffer)
        .orElseThrow(
            () -> new MetadataDecoderException("Missing value for required field: version"));

    this.distributionUrl = MappingUtil.map(DataUtil.readString(buffer), MetadataV0::decodeUri)
        .orElse(null);
    this.documentationUrl = MappingUtil.map(DataUtil.readString(buffer), MetadataV0::decodeUri)
        .orElse(null);
    this.issueReportingUrl = MappingUtil.map(DataUtil.readString(buffer), MetadataV0::decodeUri)
        .orElse(null);
    this.authors = DataUtil.readCollection(buffer, new ArrayList<>(), AuthorV0::new);
    this.contributors = DataUtil.readCollection(buffer, new ArrayList<>(), AuthorV0::new);

    this.providedServices = DataUtil.readCollection(buffer, new ArrayList<>(), ServiceV0::new);
    this.extensionDependencies = DataUtil
        .readCollection(buffer, new ArrayList<>(), DependencyV0::new);
    this.serviceDependencies = DataUtil
        .readCollection(buffer, new ArrayList<>(), DependencyV0::new);
  }

  private static URI decodeUri(@NonNull String uri) throws MetadataDecoderException {
    try {
      return URI.create(uri);
    } catch (IllegalArgumentException ex) {
      throw new MetadataDecoderException("Illegal field value", ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String getProductIdentifier() {
    return this.productIdentifier;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String getEnvironmentType() {
    return this.environmentType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getFlags() {
    return this.flags;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String getIdentifier() {
    return this.identifier;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public Optional<URI> getDistributionUrl() {
    return Optional.ofNullable(this.distributionUrl);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public Optional<URI> getDocumentationUrl() {
    return Optional.ofNullable(this.documentationUrl);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public Optional<URI> getIssueReportingUrl() {
    return Optional.ofNullable(this.issueReportingUrl);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public List<Author> getAuthors() {
    return Collections.unmodifiableList(this.authors);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public List<Author> getContributors() {
    return Collections.unmodifiableList(this.contributors);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public List<Service> getProvidedServices() {
    return Collections.unmodifiableList(this.providedServices);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public List<Dependency> getExtensionDependencies() {
    return Collections.unmodifiableList(this.extensionDependencies);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public List<Dependency> getServiceDependencies() {
    return Collections.unmodifiableList(this.serviceDependencies);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getSerializedLength() {
    return super.getSerializedLength() + DataUtil.estimateString(this.productIdentifier) +
        DataUtil.estimateString(this.environmentType) +
        2 +
        DataUtil.estimateString(this.identifier) +
        DataUtil.estimateString(this.version) +
        DataUtil.estimateString(Objects.toString(this.distributionUrl, null)) +
        DataUtil.estimateString(Objects.toString(this.documentationUrl, null)) +
        DataUtil.estimateString(Objects.toString(this.issueReportingUrl, null)) +
        DataUtil.estimateCollection(this.authors, AuthorV0::getSerializedLength) +
        DataUtil.estimateCollection(this.contributors, AuthorV0::getSerializedLength) +
        DataUtil.estimateCollection(this.providedServices, ServiceV0::getSerializedLength) +
        DataUtil.estimateCollection(this.extensionDependencies, DependencyV0::getSerializedLength) +
        DataUtil.estimateCollection(this.serviceDependencies, DependencyV0::getSerializedLength);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void serialize(@NonNull ByteBuffer buffer) throws ManifestEncoderException {
    super.serialize(buffer);
    DataUtil.writeString(buffer, this.productIdentifier);
    DataUtil.writeString(buffer, this.environmentType);
    DataUtil.writeUnsignedShort(buffer, this.flags);
    DataUtil.writeString(buffer, this.identifier);
    DataUtil.writeString(buffer, this.version);
    DataUtil.writeString(buffer, Objects.toString(this.distributionUrl, null));
    DataUtil.writeString(buffer, Objects.toString(this.documentationUrl, null));
    DataUtil.writeString(buffer, Objects.toString(this.issueReportingUrl, null));
    DataUtil.writeCollection(buffer, this.authors, (b, author) -> author.serialize(b));
    DataUtil.writeCollection(buffer, this.contributors, (b, author) -> author.serialize(b));
    DataUtil.writeCollection(buffer, this.providedServices, (b, service) -> service.serialize(b));
    DataUtil.writeCollection(buffer, this.extensionDependencies, (b, dep) -> dep.serialize(b));
    DataUtil.writeCollection(buffer, this.serviceDependencies, (b, dep) -> dep.serialize(b));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MetadataV0)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    MetadataV0 that = (MetadataV0) o;
    return this.flags == that.flags &&
        Objects.equals(this.productIdentifier, that.productIdentifier) &&
        Objects.equals(this.environmentType, that.environmentType) &&
        Objects.equals(this.identifier, that.identifier) &&
        Objects.equals(this.version, that.version) &&
        Objects.equals(this.distributionUrl, that.distributionUrl) &&
        Objects.equals(this.documentationUrl, that.documentationUrl) &&
        Objects.equals(this.issueReportingUrl, that.issueReportingUrl) &&
        Objects.equals(this.authors, that.authors) &&
        Objects.equals(this.contributors, that.contributors) &&
        Objects.equals(this.providedServices, that.providedServices) &&
        Objects.equals(this.extensionDependencies, that.extensionDependencies) &&
        Objects.equals(this.serviceDependencies, that.serviceDependencies);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects
        .hash(super.hashCode(), this.productIdentifier, this.environmentType, this.flags,
            this.identifier, this.version,
            this.distributionUrl, this.documentationUrl, this.issueReportingUrl, this.authors,
            this.contributors,
            this.providedServices, this.extensionDependencies, this.serviceDependencies);
  }

  public static class Builder implements Metadata.Builder {

    private String productIdentifier;
    private String environmentType;
    private int flags;

    private String identifier;
    private String version;

    private URI distributionUrl;
    private URI documentationUrl;
    private URI issueReportingUrl;
    private final List<AuthorV0> authors = new ArrayList<>();
    private final List<AuthorV0> contributors = new ArrayList<>();

    private final List<ServiceV0> services = new ArrayList<>();
    private final List<DependencyV0> extensionDependencies = new ArrayList<>();
    private final List<DependencyV0> serviceDependencies = new ArrayList<>();

    @NonNull
    @Override
    public MetadataV0 build() {
      return new MetadataV0(
          (short) 0,
          this.productIdentifier, this.environmentType, this.flags,
          this.identifier, this.version,
          this.distributionUrl, this.documentationUrl, this.issueReportingUrl,
          this.authors, this.contributors,
          this.services, this.extensionDependencies, this.serviceDependencies
      );
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Builder setProductIdentifier(@NonNull String identifier) {
      this.productIdentifier = identifier;
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Builder setEnvironmentType(@NonNull String environmentType) {
      this.environmentType = environmentType;
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Builder setFlags(int flags) {
      this.flags = flags;
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Builder setIdentifier(@NonNull String identifier) {
      this.identifier = identifier;
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Metadata.Builder setVersion(@NonNull String version) {
      this.version = version;
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Builder setDistributionUrl(@NonNull URI distributionUrl) {
      this.distributionUrl = distributionUrl;
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Builder setDocumentationUrl(@NonNull URI documentationUrl) {
      this.documentationUrl = documentationUrl;
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Builder setIssueReportingUrl(@NonNull URI issueReportingUrl) {
      this.issueReportingUrl = issueReportingUrl;
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Builder addAuthor(@NonNull String name, @Nullable String alias) {
      // TODO: Disallow duplicates?
      this.authors.add(new AuthorV0(name, alias));
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Builder addContributor(@NonNull String name, @Nullable String alias) {
      // TODO: Disallow duplicates?
      this.contributors.add(new AuthorV0(name, alias));
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Builder addService(@NonNull String identifier, @NonNull String version) {
      this.services.add(new ServiceV0(identifier, version));
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Builder addExtensionDependency(@NonNull String identifier,
        @NonNull String versionRange, boolean optional) {
      this.extensionDependencies.add(new DependencyV0(identifier, versionRange, optional));
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Builder addServiceDependency(@NonNull String identifier,
        @NonNull String versionRange, boolean optional) {
      this.serviceDependencies.add(new DependencyV0(identifier, versionRange, optional));
      return this;
    }
  }
}
