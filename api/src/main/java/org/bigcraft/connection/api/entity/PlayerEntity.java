package org.bigcraft.connection.api.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "players")
public class PlayerEntity {
    @DatabaseField(id = true)
    private UUID uuid;
    @DatabaseField(unique = true, canBeNull = false)
    private String name;

    public PlayerEntity() {
    }

    public PlayerEntity(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
