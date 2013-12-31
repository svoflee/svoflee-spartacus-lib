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

package com.svoflee.spartacus.lib.dal.nosql.mongodb;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import com.mongodb.WriteResult;
import com.svoflee.spartacus.core.constants.GlobalConstants.Version;
import com.svoflee.spartacus.core.domain.query.Criteria;
import com.svoflee.spartacus.core.domain.query.CriteriaContainer;
import com.svoflee.spartacus.core.domain.query.CriteriaJoin;
import com.svoflee.spartacus.core.domain.query.CriteriaOperator;
import com.svoflee.spartacus.core.domain.query.FieldCriteria;
import com.svoflee.spartacus.core.domain.query.Pageable;
import com.svoflee.spartacus.core.log.Logger;
import com.svoflee.spartacus.core.utils.exception.EUtils;

/**
 * CommonMongoDao 是通用的MongoDbDao
 * 
 * @author <a href="mailto:svoflee@gmail.com">svoflee@gmail.com</a>
 * @since 1.0.0
 * @version 1.0.0
 */
public class CommonMongoDao extends BaseRawMongoDao {

    private static final Logger log = Logger.getLogger(CommonMongoDao.class);

    private MongoFactory mongoFactory;

    public void setMongoFactory(MongoFactory mongoFactory) {
        this.mongoFactory = mongoFactory;
    }

    public MongoFactory getMongoFactory() {
        return mongoFactory;
    }

    public DB getDefaultDB() {
        return mongoFactory.getDefaultDB();
    }

    @Override
    public void insertRecords(String tableName, List records) {
        // List<DBObject> list = records;
        WriteResult result = getMongoFactory().getDefaultDB().getCollection(tableName).insert(records);
        // log.debug("{}", result.getN());
    }

    public void insertRecords(String tableName, DBObject doc) {
        WriteResult result = getMongoFactory().getDefaultDB().getCollection(tableName).insert(doc);
    }

    public void updateRecord(String tableName, DBObject query, DBObject updatedRecord) {
        getDefaultDB().getCollection(tableName).findAndModify(query, updatedRecord);
    }

    /**
     * @param tableName
     * @param aCriteriaContainer
     * @param fields 需要查询的字段
     * @return
     */
    @SuppressWarnings("deprecation")
    @Override
    public Iterator findByCriteriaContainer(String tableName, CriteriaContainer aCriteriaContainer, List<String> fields) {
        DBCollection aDBCollection = getMongoFactory().getDefaultDB().getCollection(tableName);

        DBObject query = getQueryByCriteriaContainer(aCriteriaContainer);

        DBObject keys = getQueryFields(fields);

        DBCursor aDBCursor = null;

        // int numToSkip = 0;
        // int batchSize = 0;
        Pageable page = aCriteriaContainer.getPage();

        if (page != null) {
            int numToSkip = page.getFirst() - 1;
            int batchSize = page.getPageSize();
            aDBCursor = aDBCollection.find(query, keys, numToSkip, batchSize);

        }
        else {
            aDBCursor = aDBCollection.find(query, keys);

        }
        // aDBCursor.skip(20);
        // aDBCursor.next();
        long aCursorId = aDBCursor.getCursorId();
        DBObject acurr = aDBCursor.curr();
        // List<DBObject> tempTest = aDBCursor.toArray(10);
        return aDBCursor;
    }

    @Override
    public void deleteByCriteria(CriteriaContainer aCriteriaContainer) {
        String tableName = aCriteriaContainer.getTableName();
        DBCollection aDBCollection = getMongoFactory().getDefaultDB().getCollection(tableName);
        DBObject query = getQueryByCriteriaContainer(aCriteriaContainer);
        aDBCollection.remove(query);
    }

    private DBObject getQueryFields(List<String> fields) {
        DBObject result = null;
        if (CollectionUtils.isNotEmpty(fields)) {
            result = new BasicDBObject();
            Iterator<String> it = fields.iterator();
            for (int i = 1; it.hasNext(); i++) {
                result.put(it.next(), i);
            }
        }

        return result;
    }

    /**
     * 获取复合查询条件
     * 
     * @param aCriteriaContainer
     * @return
     */
    private DBObject getQueryByCriteriaContainer(CriteriaContainer aCriteriaContainer) {
        QueryBuilder query = QueryBuilder.start();
        Map<String, Criteria> aaCriteriaContainer = aCriteriaContainer.getCriterias();
        for (String columnName : aaCriteriaContainer.keySet()) {
            Criteria aRootCriteria = aaCriteriaContainer.get(columnName);
            if (aRootCriteria instanceof FieldCriteria) {

                FieldCriteria aRootFieldCriteria = (FieldCriteria) aRootCriteria;
                FieldCriteria rootC = null;
                if (aRootFieldCriteria.isRootCriteria()) {
                    rootC = aRootFieldCriteria;
                }
                else {
                    rootC = (FieldCriteria) aRootFieldCriteria.getRootCriteria();
                }

                CriteriaJoin rootCriteriaJoin = rootC.getCriteriaJoin();

                Set<Criteria> aCriteriaCollection = rootC.getCriteriaCollection();
                Iterator<Criteria> it = aCriteriaCollection.iterator();
                QueryBuilder curQueryBuilder = null;
                if (rootCriteriaJoin == CriteriaJoin.AND) {
                    curQueryBuilder = query.and(columnName);
                }
                else {
                    curQueryBuilder = QueryBuilder.start(columnName);
                }

                for (int i = 0; it.hasNext(); i++) {
                    FieldCriteria curFieldCriteria = (FieldCriteria) it.next();
                    String fn = curFieldCriteria.getColumnName();
                    CriteriaJoin aCriteriaJoin = curFieldCriteria.getCriteriaJoin();
                    CriteriaOperator aCriteriaOperator = curFieldCriteria.getCriteriaOperator();
                    Object filterValue = curFieldCriteria.getValue();
                    if (aCriteriaJoin == CriteriaJoin.AND) {
                        putQueryFilters(curQueryBuilder, aCriteriaOperator, filterValue);
                    }
                    else {
                        // TODO:1.0.0版本不支持Or操作，后续修正mongodb driver的一致性后在进行修改
                        // throw new RuntimeException("Not supportted in Version 1.0.0");
                        EUtils.throwNotSupportedException(Version.V1_0_0);
                    }
                }
                // mongodb java driver的api不太友好，提供了and，没有提供or，
                // 抽时间修正一下这个api,对Or方法进行研究并提供支持
                // if (rootCriteriaJoin == CriteriaJoin.OR) {
                // EUtils.throwNotSupportedException(Version.V1_0_0);
                // query.or(curQueryBuilder.get());
                // }
                // else {
                // // TODO:@@@这里如何处理？
                // }

            }
            else {
                // throw new RuntimeException("Not supportted in Version 1.0.0");
                // VVV:V1.0.0 目前版本支持FieldCriteria方式,其他类型暂时不考虑
                EUtils.throwNotSupportedException(Version.V1_0_0);
            }
        }
        DBObject result = query.get();
        // log.debug("query:{}", result);
        return result;
    }

    /**
     * 取出所有字段
     * 
     * @param tableName
     * @param aCriteriaContainer
     * @return
     */
    @Override
    public Iterator findByCriteriaContainer(String tableName, CriteriaContainer aCriteriaContainer) {
        return this.findByCriteriaContainer(tableName, aCriteriaContainer, null);
    }

    private void putQueryFilters(QueryBuilder aQueryBuilder, CriteriaOperator aCriteriaOperator, Object filterValue) {
        if (aCriteriaOperator == CriteriaOperator.EQUAL) {
            aQueryBuilder.is(filterValue);
        }
        else if (aCriteriaOperator == CriteriaOperator.NOT_EQUAL) {
            aQueryBuilder.notEquals(filterValue);
        }
        else if (aCriteriaOperator == CriteriaOperator.GREATER_THAN) {
            aQueryBuilder.greaterThan(filterValue);
        }
        else if (aCriteriaOperator == CriteriaOperator.GREATER_THAN_OR_EQUAL) {
            aQueryBuilder.greaterThanEquals(filterValue);
        }
        else if (aCriteriaOperator == CriteriaOperator.LESS_THAN) {
            aQueryBuilder.lessThan(filterValue);
        }
        else if (aCriteriaOperator == CriteriaOperator.LESS_THAN_OR_EQUAL) {
            aQueryBuilder.lessThanEquals(filterValue);
        }
        else if (aCriteriaOperator == CriteriaOperator.IN) {
            aQueryBuilder.in(filterValue);
        }

    }
}
