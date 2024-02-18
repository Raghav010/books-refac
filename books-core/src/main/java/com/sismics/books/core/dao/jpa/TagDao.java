package com.sismics.books.core.dao.jpa;


import java.util.Date;
import java.util.List;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;


import com.sismics.books.core.interfaces.Dao;

import com.sismics.books.core.model.jpa.Tag;
import com.sismics.util.context.ThreadLocalContext;

/**
 * Tag DAO.
 * 
 * @author bgamard
 */
public class TagDao implements Dao<Tag> {
    /**
     * Gets a tag by its ID.
     * 
     * @param id Tag ID
     * @return Tag
     */
    public Tag getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            return em.find(Tag.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Returns the list of all tags.
     * 
     * @return List of tags
     */
    @SuppressWarnings("unchecked")
    public List<Tag> getByUserId(String userId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select t from Tag t where t.userId = :userId and t.deleteDate is null order by t.name");
        q.setParameter("userId", userId);
        return q.getResultList();
    }

    
    /**
     * Creates a new tag.
     * 
     * @param tag Tag
     * @return New ID
     * @throws Exception
     */
    public String create(Tag tag) {
        // Create the UUID
        tag.setId(UUID.randomUUID().toString());
        
        // Create the tag
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        tag.setCreateDate(new Date());
        em.persist(tag);
        
        return tag.getId();
    }

    /**
     * Returns a tag by name.
     * @param userId User ID
     * @param name Name
     * @return Tag
     */
    public Tag getByName(String userId, String name) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select t from Tag t where t.name = :name and t.userId = :userId and t.deleteDate is null");
        q.setParameter("userId", userId);
        q.setParameter("name", name);
        try {
            return (Tag) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Returns a tag by ID.
     * @param userId User ID
     * @param tagId Tag ID
     * @return Tag
     */
    public Tag getByTagId(String userId, String tagId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select t from Tag t where t.id = :tagId and t.userId = :userId and t.deleteDate is null");
        q.setParameter("userId", userId);
        q.setParameter("tagId", tagId);
        try {
            return (Tag) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Deletes a tag.
     * 
     * @param tagId Tag ID
     */
    public void delete(String tagId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
            
        // Get the tag
        Query q = em.createQuery("select t from Tag t where t.id = :id and t.deleteDate is null");
        q.setParameter("id", tagId);
        Tag tagDb = (Tag) q.getSingleResult();
        
        // Delete the tag
        Date dateNow = new Date();
        tagDb.setDeleteDate(dateNow);

        UserBookTagDao UBTagDao = new UserBookTagDao();
        UBTagDao.delete(tagId);
    }

    /**
     * Search tags by name.
     * 
     * @param name Tag name
     * @return List of found tags
     */
    @SuppressWarnings("unchecked")
    public List<Tag> findByName(String userId, String name) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select t from Tag t where t.name like :name and t.userId = :userId and t.deleteDate is null");
        q.setParameter("userId", userId);
        q.setParameter("name", "%" + name + "%");
        return q.getResultList();
    }
}
