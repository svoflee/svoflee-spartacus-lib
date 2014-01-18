/*
 * Copyright (c) http://www.svoflee.com All rights reserved.
 **************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **************************************************************************      
 */

package com.svoflee.spartacus.lib.dal.sql.jpa;

import static org.springframework.data.jpa.repository.query.QueryUtils.COUNT_QUERY_STRING;
import static org.springframework.data.jpa.repository.query.QueryUtils.DELETE_ALL_QUERY_STRING;
import static org.springframework.data.jpa.repository.query.QueryUtils.EXISTS_QUERY_STRING;
import static org.springframework.data.jpa.repository.query.QueryUtils.applyAndBind;
import static org.springframework.data.jpa.repository.query.QueryUtils.getQueryString;
import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.PersistenceProvider;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.svoflee.spartacus.core.constants.GlobalConstants.Version;
import com.svoflee.spartacus.core.domain.DataPersistable;
import com.svoflee.spartacus.core.domain.query.CriteriaContainer;
import com.svoflee.spartacus.core.domain.query.Pageable;
import com.svoflee.spartacus.core.domain.query.Sort;
import com.svoflee.spartacus.core.log.Logger;
import com.svoflee.spartacus.core.utils.IdUtils;
import com.svoflee.spartacus.core.utils.exception.EUtils;
import com.svoflee.spartacus.core.utils.reflection.ReflectionUtils;
import com.svoflee.spartacus.lib.dal.IBaseRepository;

/**
 * BaseDefaultJpaRepository 是
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee</a>
 * @since 1.0.0
 * @version 1.0.0
 */
@Transactional(readOnly = true)
public abstract class BaseDefaultJpaRepository<E extends DataPersistable<PK>, PK extends Serializable> implements
        IBaseRepository<E, PK>, InitializingBean {

    private static final Logger log = Logger.getLogger(BaseDefaultJpaRepository.class.getName());

    /**
     * 默认的entity的id属性名称
     */
    public static final String ID_NAME = "id";

    /**
     * XXX：svoflee：getCountQueryPlaceholder方法是protected,why?
     * 
     * @see PersistenceProvider#getCountQueryPlaceholder()
     */
    private static final String CountQueryPlaceholder = "x";

    private JpaEntityInformation<E, ?> entityInformation;

    private EntityManager entityManager;

    private PersistenceProvider provider;

    protected Class<E> domainClass;

    // /**
    // * @param entityInformation
    // * @param entityManager
    // */
    // public BaseDefaultJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
    //
    // Assert.notNull(entityInformation);
    // Assert.notNull(entityManager);
    // this.entityInformation = entityInformation;
    // this.setEntityManager(entityManager);
    // this.provider = PersistenceProvider.fromEntityManager(entityManager);
    // }
    //
    // /**
    // * @param domainClass
    // * @param entityManager
    // */
    // public BaseDefaultJpaRepository(Class<T> domainClass, EntityManager em) {
    // this(JpaEntityInformationSupport.getMetadata(domainClass, em), em);
    // }

    public BaseDefaultJpaRepository() {
        // this.entityManager = this.getEntityManager();
        // Assert.notNull(this.getEntityManager());

        // this.entityInformation = JpaEntityInformationSupport.getMetadata(this.domainClass, this.entityManager);
        // this.provider = PersistenceProvider.fromEntityManager(this.entityManager);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.domainClass = ReflectionUtils.getSuperClassGenricType(this.getClass());
        Assert.notNull(this.domainClass);
        Assert.notNull(this.getEntityManager());
        this.entityInformation = JpaEntityInformationSupport.getMetadata(this.domainClass, this.getEntityManager());
    }

    private Class<E> getDomainClass() {
        return this.getEntityInformation().getJavaType();
    }

    private String getDeleteAllQueryString() {
        return getQueryString(DELETE_ALL_QUERY_STRING, this.getEntityInformation().getEntityName());
    }

    private String getCountQueryString() {
        String countQuery = String.format(COUNT_QUERY_STRING, CountQueryPlaceholder, "%s");
        return getQueryString(countQuery, this.getEntityInformation().getEntityName());
    }

    @Override
    @Transactional
    public void deleteEntity(PK id) {
        this.deleteEntity(this.selectByPrimaryKey(id));
    }

    @Override
    @Transactional
    public void deleteEntity(E entity) {
        this.getEntityManager().remove(
                this.getEntityManager().contains(entity) ? entity : this.getEntityManager().merge(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteEntities(Iterable<? extends E> entities) {
        delete(entities);
    }

    // @Override

    private void delete(Iterable<? extends E> entities) {
        if (entities == null) {
            return;
        }

        for (E entity : entities) {
            this.deleteEntity(entity);
        }
    }

    @Override
    @Transactional
    public void deleteInBatch(Iterable<E> entities) {
        if ((null == entities) || !entities.iterator().hasNext()) {
            return;
        }

        applyAndBind(getQueryString(DELETE_ALL_QUERY_STRING, this.getEntityInformation().getEntityName()), entities,
                this.getEntityManager()).executeUpdate();
    }

    @Override
    @Transactional
    public void deleteAll() {
        this.getEntityManager().createQuery(this.getDeleteAllQueryString()).executeUpdate();
    }

    @Override
    public E selectByPrimaryKey(PK id) {
        Assert.notNull(id, "The given id must not be null!");
        return this.getEntityManager().find(this.getDomainClass(), id);
    }

    @Override
    public boolean exists(PK id) {

        // String placeholder = this.provider.getCountQueryPlaceholder();
        String placeholder = CountQueryPlaceholder;

        String entityName = this.getEntityInformation().getEntityName();
        String idAttributeName = this.getEntityInformation().getIdAttribute().getName();

        String existsQuery = String.format(EXISTS_QUERY_STRING, placeholder, entityName, idAttributeName);

        TypedQuery<Long> query = this.getEntityManager().createQuery(existsQuery, Long.class);
        query.setParameter(ID_NAME, id);// XXX：svoflee:parameter="id"是否有问题?

        return query.getSingleResult() == 1;
    }

    public E findOne(Specification<E> spec) {
        try {
            return this.getQuery(spec, (Sort) null).getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    public List<E> findAll(Specification<E> spec) {
        return this.getQuery(spec, (Sort) null).getResultList();
    }

    // public Page<E> findAll(Specification<E> spec, Pageable pageable) {
    // TypedQuery<E> query = this.getQuery(spec, pageable);
    //
    // return pageable == null ? new PageImpl<E>(query.getResultList()) : this.readPage(query, pageable, spec);
    // }

    public List<E> findAll(Specification<E> spec, Sort sort) {
        return this.getQuery(spec, sort).getResultList();
    }

    @Override
    public long count() {
        return this.getEntityManager().createQuery(this.getCountQueryString(), Long.class).getSingleResult();
    }

    public long count(Specification<E> spec) {
        return this.getCountQuery(spec).getSingleResult();
    }

    @Override
    @Transactional
    public E saveEntity(E entity) {
        if (this.getEntityInformation().isNew(entity)) {
            entity.setId((PK) IdUtils.getGlobleUniqueId());
            this.getEntityManager().persist(entity);
            return entity;
        }
        else {
            return this.getEntityManager().merge(entity);
        }
    }

    @Override
    @Transactional
    public E saveAndFlush(E entity) {
        E result = this.saveEntity(entity);
        this.flush();

        return result;
    }

    @Override
    @Transactional
    public List<E> saveEntities(Iterable<? extends E> entities) {

        List<E> result = new ArrayList<E>();

        if (entities == null) {
            return result;
        }

        for (E entity : entities) {
            result.add(this.saveEntity(entity));
        }

        return result;
    }

    @Override
    @Transactional
    public void flush() {
        this.getEntityManager().flush();
    }

    // /**
    // * Reads the given {@link TypedQuery} into a {@link Page} applying the given {@link Pageable} and
    // * {@link Specification}.
    // *
    // * @param query
    // * @param spec
    // * @param pageable
    // * @return
    // */
    // private Page<E> readPage(TypedQuery<E> query, Pageable pageable, Specification<E> spec) {
    // query.setFirstResult(pageable.getOffset());
    // query.setMaxResults(pageable.getPageSize());
    //
    // Long total = this.getCountQuery(spec).getSingleResult();
    //
    // return new PageImpl<E>(query.getResultList(), pageable, total);
    // }
    // /**
    // * Creates a new {@link TypedQuery} from the given {@link Specification}.
    // *
    // * @param spec can be {@literal null}
    // * @param pageable can be {@literal null}
    // * @return
    // */
    // private TypedQuery<E> getQuery(Specification<E> spec, Pageable pageable) {
    // CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
    // CriteriaQuery<E> query = builder.createQuery(this.getDomainClass());
    //
    // Root<E> root = this.applySpecificationToCriteria(spec, query);
    // query.select(root);
    //
    // if (pageable != null) {
    // query.orderBy(toOrders(pageable.getSort(), root, builder));
    // }
    //
    // return this.getEntityManager().createQuery(query);
    // }

    /**
     * Creates a {@link TypedQuery} for the given {@link Specification} and {@link Sort}.
     * 
     * @param spec
     * @param sort
     * @return
     */
    private TypedQuery<E> getQuery(Specification<E> spec, Sort sort) {

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(this.getDomainClass());

        Root<E> root = this.applySpecificationToCriteria(spec, query);
        query.select(root);

        if (sort != null) {
            query.orderBy(toOrders(sort, root, builder));
        }

        return this.getEntityManager().createQuery(query);
    }

    /**
     * Creates a new count query for the given {@link Specification}.
     * 
     * @param spec can be {@literal null}.
     * @return
     */
    private TypedQuery<Long> getCountQuery(Specification<E> spec) {

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<E> root = this.applySpecificationToCriteria(spec, query);
        query.select(builder.count(root));

        return this.getEntityManager().createQuery(query);
    }

    /**
     * Applies the given {@link Specification} to the given {@link CriteriaQuery}.
     * 
     * @param spec can be {@literal null}
     * @param query
     * @return
     */
    private <S> Root<E> applySpecificationToCriteria(Specification<E> spec, CriteriaQuery<S> query) {

        Assert.notNull(query);
        Root<E> root = query.from(this.getDomainClass());

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setProvider(PersistenceProvider provider) {
        this.provider = provider;
    }

    public PersistenceProvider getProvider() {
        return this.provider;
    }

    public void setEntityInformation(JpaEntityInformation<E, ?> entityInformation) {
        this.entityInformation = entityInformation;
    }

    public JpaEntityInformation<E, ?> getEntityInformation() {
        return this.entityInformation;
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return this.getEntityManager().getCriteriaBuilder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E findUniqueByEq(String propertyName, Object propertyValue) {
        E result = null;
        try {
            CriteriaQuery<E> aCriteriaQuery = this.createCriteriaQueryByEq(propertyName, propertyValue);
            result = this.getEntityManager().createQuery(aCriteriaQuery).getSingleResult();
        }
        catch (Exception e) {
            // Logs.LogEx(log, e);
            // throw new RepositoryException(e);
        }
        return result;
    }

    /**
     * @param propertyName
     * @param value
     * @return
     */
    private CriteriaQuery<E> createCriteriaQueryByEq(String propertyName, Object propertyValue) {
        CriteriaBuilder aCriteriaBuilder = this.getCriteriaBuilder();
        CriteriaQuery<E> aCriteriaQuery = aCriteriaBuilder.createQuery(this.getDomainClass());
        Root<E> root = aCriteriaQuery.from(this.getDomainClass());
        Predicate propertyNameEq = aCriteriaBuilder.equal(root.get(propertyName), propertyValue);
        aCriteriaQuery = aCriteriaQuery.where(aCriteriaBuilder.and(propertyNameEq));
        return aCriteriaQuery;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> findByEq(String propertyName, Object propertyValue) {
        // query.where(builder.and(hasBirthday, isLongTermCustomer));
        CriteriaQuery<E> aCriteriaQuery = this.createCriteriaQueryByEq(propertyName, propertyValue);
        List<E> result = this.getEntityManager().createQuery(aCriteriaQuery).getResultList();

        return result;
    }

    @Override
    public List<E> findAll() {
        return this.getQuery(null, (Sort) null).getResultList();
    }

    @Override
    public Pageable findAll(CriteriaContainer aCriteriaContainer) {
        // CriteriaBuilder aCriteriaBuilder = this.getCriteriaBuilder();
        // CriteriaQuery<E> aCriteriaQuery = aCriteriaBuilder.createQuery(this.getDomainClass());
        // Root<E> root = aCriteriaQuery.from(this.getDomainClass());
        //
        //
        // Predicate propertyNameEq = aCriteriaBuilder.equal(root.get(propertyName), propertyValue);
        // aCriteriaQuery = aCriteriaQuery.where(aCriteriaBuilder.and(propertyNameEq));
        //
        //
        //
        // Map<String, Criteria> aaCriteriaContainer = aCriteriaContainer.getCriterias();
        // for (String columnName : aaCriteriaContainer.keySet()) {
        // Criteria aRootCriteria = aaCriteriaContainer.get(columnName);
        // if (aRootCriteria instanceof FieldCriteria) {
        //
        // FieldCriteria aRootFieldCriteria = (FieldCriteria) aRootCriteria;
        // FieldCriteria rootC = null;
        // if (aRootFieldCriteria.isRootCriteria()) {
        // rootC = aRootFieldCriteria;
        // }
        // else {
        // rootC = (FieldCriteria) aRootFieldCriteria.getRootCriteria();
        // }
        //
        // CriteriaJoin rootCriteriaJoin = rootC.getCriteriaJoin();
        //
        // Set<Criteria> aCriteriaCollection = rootC.getCriteriaCollection();
        // Iterator<Criteria> it = aCriteriaCollection.iterator();
        // QueryBuilder curQueryBuilder = null;
        // if (rootCriteriaJoin == CriteriaJoin.AND) {
        // curQueryBuilder = query.and(columnName);
        // }
        // else {
        // curQueryBuilder = QueryBuilder.start(columnName);
        // }
        //
        // for (int i = 0; it.hasNext(); i++) {
        // FieldCriteria curFieldCriteria = (FieldCriteria) it.next();
        // String fn = curFieldCriteria.getColumnName();
        // CriteriaJoin aCriteriaJoin = curFieldCriteria.getCriteriaJoin();
        // CriteriaOperator aCriteriaOperator = curFieldCriteria.getCriteriaOperator();
        // Object filterValue = curFieldCriteria.getValue();
        // if (aCriteriaJoin == CriteriaJoin.AND) {
        // putQueryFilters(curQueryBuilder, aCriteriaOperator, filterValue);
        // }
        // else {
        // // TODO:1.0.0版本不支持Or操作，后续修正mongodb driver的一致性后在进行修改
        // // throw new RuntimeException("Not supportted in Version 1.0.0");
        // EUtils.throwNotSupportedException(Version.V1_0_0);
        // }
        // }
        // // mongodb java driver的api不太友好，提供了and，没有提供or，
        // // 抽时间修正一下这个api,对Or方法进行研究并提供支持
        // // if (rootCriteriaJoin == CriteriaJoin.OR) {
        // // EUtils.throwNotSupportedException(Version.V1_0_0);
        // // query.or(curQueryBuilder.get());
        // // }
        // // else {
        // // // TODO:@@@这里如何处理？
        // // }
        //
        // }
        // else {
        // // throw new RuntimeException("Not supportted in Version 1.0.0");
        // // VVV:V1.0.0 目前版本支持FieldCriteria方式,其他类型暂时不考虑
        // EUtils.throwNotSupportedException(Version.V1_0_0);
        // }
        // }

        // Map<String, Criteria> aCriterias = aCriteriaContainer.getCriterias();
        // for (String key : aCriterias.keySet()) {
        // Criteria aCriteria = aCriterias.get(key);
        // String aColumnName = aCriteria.getColumnName();
        // aCriteria.
        //
        // }
        // Pageable page = aCriteriaContainer.getPage();
        // CriteriaBuilder aCriteriaBuilder = this.getCriteriaBuilder();
        // CriteriaQuery<E> aCriteriaQuery = aCriteriaBuilder.createQuery(this.getDomainClass());
        // TypedQuery<E> aTypedQuery = this.createTypedQueryBy(aCriteriaContainer);
        // aCriteriaQuery.page.setResult(result);
        EUtils.throwNotSupportedException(Version.V1_0_0);
        return null;
    }

    /**
     * @param aCriteriaContainer
     * @return
     */
    private TypedQuery<E> createTypedQueryBy(CriteriaContainer aCriteriaContainer) {
        // Map<String, Criteria> aCriterias = aCriteriaContainer.getCriterias();
        // for (String key : aCriterias.keySet()) {
        // Criteria aCriteria=aCriterias.get(key);
        // aCriteria.get
        // }
        EUtils.throwNotSupportedException(Version.V1_0_0);

        return null;
    }

    // public Page<E> findAll(Pageable pageable) {
    // if (null == pageable) {
    // return new PageImpl<E>(this.findAll());
    // }
    //
    // return this.findAll(null, pageable);
    // }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> findAll(Sort sort) {
        return this.getQuery(null, sort).getResultList();
    }

    // @Override
    // public List<E> findAll(Sort sort) {
    // return this.getQuery(null, sort).getResultList();
    // }

}
