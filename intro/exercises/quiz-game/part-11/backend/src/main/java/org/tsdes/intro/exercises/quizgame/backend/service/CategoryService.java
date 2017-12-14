package org.tsdes.intro.exercises.quizgame.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tsdes.intro.exercises.quizgame.backend.entity.Category;
import org.tsdes.intro.exercises.quizgame.backend.entity.SubCategory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by arcuri82 on 14-Dec-17.
 */
@Service
@Transactional
public class CategoryService {

    @Autowired
    private EntityManager em;

    public Long createCategory(String name){

        Category category = new Category();
        category.setName(name);

        em.persist(category);

        return category.getId();
    }


    public Long createSubCategory(long parentId, String name){

        Category category = em.find(Category.class, parentId);
        if(category == null){
            throw new IllegalArgumentException("Category with id "+parentId+" does not exist");
        }

        SubCategory subCategory = new SubCategory();
        subCategory.setName(name);
        subCategory.setParent(category);
        em.persist(subCategory);

        category.getSubCategories().add(subCategory);

        return subCategory.getId();
    }


    public List<Category> getAllCategories(boolean withSub){

        TypedQuery<Category> query = em.createQuery("select c from Category c", Category.class);
        List<Category> categories = query.getResultList();

        if(withSub){
            //force loading
            categories.forEach(c -> c.getSubCategories().size());
        }

        return categories;
    }

    public boolean doesCategoryExist(long id){
        Category category = em.find(Category.class, id);
        return category != null;
    }

    public Void changeCategoryName(long id, String name){
        Category category = em.find(Category.class, id);
        if(category != null){
            category.setName(name);
        }
        return null;
    }

    public Category getCategory(long id, boolean withSub){

        Category category = em.find(Category.class, id);
        if(withSub && category != null){
            category.getSubCategories().size();
        }

        return category;
    }

    public boolean deleteCategory(long id){
        Category category = em.find(Category.class, id);
        if(category != null){
            em.remove(category);
            return true;
        }
        return false;
    }

    public SubCategory getSubCategory(long id){

        return em.find(SubCategory.class, id);
    }

    public List<SubCategory> getSubCategories(Long parentId){

        TypedQuery<SubCategory> query;
        if(parentId == null){
            query = em.createQuery("select s from SubCategory s", SubCategory.class);
        } else {
            query = em.createQuery("select s from SubCategory s where s.parent.id=?1", SubCategory.class);
            query.setParameter(1, parentId);
        }

        return query.getResultList();
    }

}
