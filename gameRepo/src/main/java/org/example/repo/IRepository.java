package org.example.repo;

import org.example.Entity;

import java.io.IOException;
import java.util.List;

public interface IRepository<ID, E extends Entity<ID>> {
    /**
     * Returns the list of all Entities
     * @return List of Entities
     */
    List<E> getAll();

    /**
     * Deletes an entity
     * @param entity E
     * @return true if the deletion was successful, false otherwise
     */
    boolean delete(E entity) throws IOException;

    /**
     * Updates an entity
     * @param entity E
     * @return the entity if it doesn't exist
     */
    E update (E entity) throws IOException;

    /**
     * Saves and validates an entity if it isn't already saved, throws Illegal Argument Exception otherwise
     * @param entity E
     * @return entity E if it is already logged in
     */
    E save(E entity) throws IOException;

    /**
     * Returns the number of elements in the container
     * @return int
     */
    int size();
}