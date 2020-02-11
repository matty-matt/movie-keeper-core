package com.kociszewski.moviekeepercore.infrastructure.movierelease;

public enum ReleaseType {
    DIGITAL(4), PHYSICAL(5);

    private final int typeId;

    ReleaseType(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return this.typeId;
    }
}
