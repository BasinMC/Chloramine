/**
 * Representations and factories for extension container manifests.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
module org.basinmc.chloramine.manifest {
  requires static com.github.spotbugs.annotations;

  exports org.basinmc.chloramine.manifest;
  exports org.basinmc.chloramine.manifest.error;
  exports org.basinmc.chloramine.manifest.metadata;
  exports org.basinmc.chloramine.manifest.metadata.v0;
}
