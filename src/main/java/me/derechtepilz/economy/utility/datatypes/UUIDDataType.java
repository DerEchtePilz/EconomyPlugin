package me.derechtepilz.economy.utility.datatypes;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDDataType {
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    public Class<UUID> getComplexType() {
        return UUID.class;
    }

    public byte[] toPrimitive(UUID complex) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(complex.getMostSignificantBits());
        bb.putLong(complex.getLeastSignificantBits());
        return bb.array();
    }

    public UUID fromPrimitive(byte[] primitive) {
        ByteBuffer bb = ByteBuffer.wrap(primitive);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }
}
