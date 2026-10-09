package com.example.inventory.repository;

import com.example.inventory.dto.ProductSearchCriteria;
import com.example.inventory.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<Product> searchProducts(ProductSearchCriteria criteria) {
        StringBuilder sql = new StringBuilder("SELECT p FROM Product p WHERE 1=1 ");

        if (criteria.query() != null && !criteria.query().isBlank()) {
            sql.append("AND LOWER(p.name) LIKE '%").append(criteria.query().toLowerCase()).append("%' ");
        }

        if (criteria.category() != null && !criteria.category().isBlank()) {
            sql.append("AND p.category = '").append(criteria.category()).append("' ");
        }

        if (criteria.minPrice() != null) {
            sql.append("AND p.price >= ").append(criteria.minPrice()).append(" ");
        }

        if (criteria.maxPrice() != null) {
            sql.append("AND p.price <= ").append(criteria.maxPrice()).append(" ");
        }

        Query query = entityManager.createQuery(sql.toString(), Product.class);

        int offset = criteria.page() * criteria.size();
        query.setFirstResult(offset);
        query.setMaxResults(criteria.size());

        return query.getResultList();
    }
}
