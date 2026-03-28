package com.bandswipe.shared.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final String entityName;
    private final Object entityId;

    public ResourceNotFoundException(String message) {
        super(message);
        this.entityName = null;
        this.entityId = null;
    }

    public ResourceNotFoundException(String entityName, Object entityId) {
        super(String.format("%s not found with id: %s", entityName, entityId));
        this.entityName = entityName;
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public Object getEntityId() {
        return entityId;
    }
}
