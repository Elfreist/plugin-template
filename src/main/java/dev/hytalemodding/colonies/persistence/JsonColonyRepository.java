package dev.hytalemodding.colonies.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.hytalemodding.colonies.model.Bucket;
import dev.hytalemodding.colonies.model.BucketKey;
import dev.hytalemodding.colonies.model.Colony;
import dev.hytalemodding.colonies.model.Group;
import dev.hytalemodding.colonies.model.Location;
import dev.hytalemodding.colonies.model.Zone;
import dev.hytalemodding.colonies.model.enums.XpType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * V1 JSON repository storing each colony in data/colonies/<uuid>.json.
 */
public class JsonColonyRepository implements ColonyRepository {
    private static final int DOCUMENT_VERSION = 1;

    private final Path baseDirectory;
    private final ObjectMapper mapper;

    public JsonColonyRepository(Path dataDirectory) {
        this.baseDirectory = dataDirectory.resolve("colonies");
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void save(Colony colony) {
        ensureDirectory();
        Path file = colonyFile(colony.getId());
        ColonyDocument document = toDocument(colony);
        try {
            mapper.writeValue(file.toFile(), document);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save colony " + colony.getId(), e);
        }
    }

    @Override
    public Optional<Colony> load(UUID id) {
        Path file = colonyFile(id);
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        try {
            ColonyDocument document = mapper.readValue(file.toFile(), ColonyDocument.class);
            return Optional.of(fromDocument(document));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load colony " + id, e);
        }
    }

    @Override
    public List<Colony> loadAll() {
        ensureDirectory();
        try (Stream<Path> files = Files.list(baseDirectory)) {
            List<Colony> colonies = new ArrayList<>();
            files.filter(path -> path.getFileName().toString().endsWith(".json"))
                    .forEach(path -> {
                        try {
                            ColonyDocument document = mapper.readValue(path.toFile(), ColonyDocument.class);
                            colonies.add(fromDocument(document));
                        } catch (IOException e) {
                            throw new IllegalStateException("Failed to load colony file: " + path, e);
                        }
                    });
            return colonies;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to list colony files", e);
        }
    }

    @Override
    public void delete(UUID id) {
        Path file = colonyFile(id);
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to delete colony " + id, e);
        }
    }

    private void ensureDirectory() {
        try {
            Files.createDirectories(baseDirectory);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create colony directory", e);
        }
    }

    private Path colonyFile(UUID id) {
        return baseDirectory.resolve(id + ".json");
    }

    private ColonyDocument toDocument(Colony colony) {
        ColonyDocument doc = new ColonyDocument();
        doc.version = DOCUMENT_VERSION;
        doc.id = colony.getId();
        doc.zone = toDocument(colony.getZone());
        doc.familyId = colony.getFamilyId();
        doc.populationTotal = colony.getPopulationTotal();
        doc.createdAtEpochMs = colony.getCreatedAtEpochMs();
        doc.groups = colony.getGroups().stream().map(this::toDocument).toList();
        return doc;
    }

    private GroupDocument toDocument(Group group) {
        GroupDocument doc = new GroupDocument();
        doc.generationId = group.getGenerationId();
        doc.ageCycles = group.getAgeCycles();
        doc.buckets = group.getBuckets().entrySet().stream().map(entry -> {
            BucketDocument bucketDocument = new BucketDocument();
            bucketDocument.roleId = entry.getKey().getRoleId();
            bucketDocument.stageId = entry.getKey().getStageId();
            bucketDocument.evolutionId = entry.getKey().getEvolutionId();
            bucketDocument.count = entry.getValue().getCount();
            Map<String, Integer> xpPools = new HashMap<>();
            entry.getValue().getXpPoolsView().forEach((type, value) -> xpPools.put(type.name(), value));
            bucketDocument.xpPools = xpPools;
            return bucketDocument;
        }).toList();
        return doc;
    }

    private ZoneDocument toDocument(Zone zone) {
        ZoneDocument doc = new ZoneDocument();
        doc.pos1 = toDocument(zone.getPos1());
        doc.pos2 = toDocument(zone.getPos2());
        doc.center = toDocument(zone.getCenter());
        return doc;
    }

    private LocationDocument toDocument(Location location) {
        LocationDocument doc = new LocationDocument();
        doc.worldId = location.getWorldId();
        doc.x = location.getX();
        doc.y = location.getY();
        doc.z = location.getZ();
        return doc;
    }

    private Colony fromDocument(ColonyDocument document) {
        List<Group> groups = document.groups.stream().map(this::fromDocument).toList();
        return new Colony(
                document.id,
                fromDocument(document.zone),
                document.familyId,
                document.populationTotal,
                groups,
                document.createdAtEpochMs
        );
    }

    private Group fromDocument(GroupDocument document) {
        Map<BucketKey, Bucket> buckets = new HashMap<>();
        for (BucketDocument bucketDocument : document.buckets) {
            BucketKey key = new BucketKey(bucketDocument.roleId, bucketDocument.stageId, bucketDocument.evolutionId);
            Bucket bucket = new Bucket(bucketDocument.count);
            Map<String, Integer> xpPools = bucketDocument.xpPools == null ? Map.of() : bucketDocument.xpPools;
            for (Map.Entry<String, Integer> entry : xpPools.entrySet()) {
                try {
                    bucket.addXp(XpType.valueOf(entry.getKey()), entry.getValue());
                } catch (IllegalArgumentException ignored) {
                    // Unknown XP type from a newer version: ignored in V1 for forward compatibility.
                }
            }
            buckets.put(key, bucket);
        }
        return new Group(document.generationId, document.ageCycles, buckets);
    }

    private Zone fromDocument(ZoneDocument document) {
        return new Zone(
                fromDocument(document.pos1),
                fromDocument(document.pos2),
                fromDocument(document.center)
        );
    }

    private Location fromDocument(LocationDocument document) {
        return new Location(document.worldId, document.x, document.y, document.z);
    }

    public static final class ColonyDocument {
        public int version;
        public UUID id;
        public ZoneDocument zone;
        public String familyId;
        public int populationTotal;
        public List<GroupDocument> groups = new ArrayList<>();
        public long createdAtEpochMs;
    }

    public static final class ZoneDocument {
        public LocationDocument pos1;
        public LocationDocument pos2;
        public LocationDocument center;
    }

    public static final class LocationDocument {
        public String worldId;
        public double x;
        public double y;
        public double z;
    }

    public static final class GroupDocument {
        public int generationId;
        public int ageCycles;
        public List<BucketDocument> buckets = new ArrayList<>();
    }

    public static final class BucketDocument {
        public String roleId;
        public int stageId;
        public String evolutionId;
        public int count;
        public Map<String, Integer> xpPools = new HashMap<>();
    }
}
