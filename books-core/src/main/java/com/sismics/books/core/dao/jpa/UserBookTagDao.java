package com.sismics.books.core.dao.jpa;

import com.sismics.books.core.dao.jpa.dto.TagDto;
import com.sismics.books.core.model.jpa.UserBookTag;
import com.sismics.util.context.ThreadLocalContext;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;


public class UserBookTagDao {

    /**
     * Update tags on a user book. or creates a userbooktag
     *
     * @param userBookId
     * @param tagIdSet
     */
    public void updateTagList(String userBookId, Set<String> tagIdSet) {
        // Delete old tag links
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("delete UserBookTag bt where bt.userBookId = :userBookId");
        q.setParameter("userBookId", userBookId);
        q.executeUpdate();

        // Create new tag links
        for (String tagId : tagIdSet) {
            UserBookTag userBookTag = new UserBookTag();
            userBookTag.setId(UUID.randomUUID().toString());
            userBookTag.setUserBookId(userBookId);
            userBookTag.setTagId(tagId);
            em.persist(userBookTag);
        }

    }

    /**
     * Returns tag list on a user book.
     * @param userBookId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<TagDto> getByUserBookId(String userBookId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        StringBuilder sb = new StringBuilder("select t.TAG_ID_C, t.TAG_NAME_C, t.TAG_COLOR_C from T_USER_BOOK_TAG bt ");
        sb.append(" join T_TAG t on t.TAG_ID_C = bt.BOT_IDTAG_C ");
        sb.append(" where bt.BOT_IDUSERBOOK_C = :userBookId and t.TAG_DELETEDATE_D is null ");
        sb.append(" order by t.TAG_NAME_C ");

        // Perform the query
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("userBookId", userBookId);
        List<Object[]> l = q.getResultList();

        // Assemble results
        List<TagDto> tagDtoList = new ArrayList<TagDto>();
        for (Object[] o : l) {
            int i = 0;
            TagDto tagDto = new TagDto();
            tagDto.setId((String) o[i++]);
            tagDto.setName((String) o[i++]);
            tagDto.setColor((String) o[i++]);
            tagDtoList.add(tagDto);
        }
        return tagDtoList;
    }


    /**
     * Deletes a user book tag.
     *
     * @param tagId Tag ID
     */
    public void delete(String tagId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();

        // Delete linked data
        Query q = em.createQuery("delete UserBookTag bt where bt.tagId = :tagId");
        q.setParameter("tagId", tagId);
        q.executeUpdate();
    }


}
