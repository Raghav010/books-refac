package com.sismics.books.core.interfaces;

/**
 * Basic functionalities related to DAO - Data Access Object
 * @param <T>
 */
public interface Dao<T> {
    /**
     * creates an object (specified by T)
     * @param obj
     * @throws Exception
     * @return String
     */
    String create(T obj) throws Exception;

    /**
     * deletes an object instance
     * @param id
     * @throws Exception
     */
    void delete(String id) throws Exception;
}
