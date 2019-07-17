package rpg.pojo;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author ljq
 *
 */
public class GhstoreExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public GhstoreExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andGzidIsNull() {
            addCriterion("gzid is null");
            return (Criteria) this;
        }

        public Criteria andGzidIsNotNull() {
            addCriterion("gzid is not null");
            return (Criteria) this;
        }

        public Criteria andGzidEqualTo(String string) {
            addCriterion("gzid =", string, "gzid");
            return (Criteria) this;
        }

        public Criteria andGzidNotEqualTo(Integer value) {
            addCriterion("gzid <>", value, "gzid");
            return (Criteria) this;
        }

        public Criteria andGzidGreaterThan(Integer value) {
            addCriterion("gzid >", value, "gzid");
            return (Criteria) this;
        }

        public Criteria andGzidGreaterThanOrEqualTo(Integer value) {
            addCriterion("gzid >=", value, "gzid");
            return (Criteria) this;
        }

        public Criteria andGzidLessThan(Integer value) {
            addCriterion("gzid <", value, "gzid");
            return (Criteria) this;
        }

        public Criteria andGzidLessThanOrEqualTo(Integer value) {
            addCriterion("gzid <=", value, "gzid");
            return (Criteria) this;
        }

        public Criteria andGzidIn(List<Integer> values) {
            addCriterion("gzid in", values, "gzid");
            return (Criteria) this;
        }

        public Criteria andGzidNotIn(List<Integer> values) {
            addCriterion("gzid not in", values, "gzid");
            return (Criteria) this;
        }

        public Criteria andGzidBetween(Integer value1, Integer value2) {
            addCriterion("gzid between", value1, value2, "gzid");
            return (Criteria) this;
        }

        public Criteria andGzidNotBetween(Integer value1, Integer value2) {
            addCriterion("gzid not between", value1, value2, "gzid");
            return (Criteria) this;
        }

        public Criteria andWpidIsNull() {
            addCriterion("wpid is null");
            return (Criteria) this;
        }

        public Criteria andWpidIsNotNull() {
            addCriterion("wpid is not null");
            return (Criteria) this;
        }

        public Criteria andWpidEqualTo(Integer value) {
            addCriterion("wpid =", value, "wpid");
            return (Criteria) this;
        }

        public Criteria andWpidNotEqualTo(Integer value) {
            addCriterion("wpid <>", value, "wpid");
            return (Criteria) this;
        }

        public Criteria andWpidGreaterThan(Integer value) {
            addCriterion("wpid >", value, "wpid");
            return (Criteria) this;
        }

        public Criteria andWpidGreaterThanOrEqualTo(Integer value) {
            addCriterion("wpid >=", value, "wpid");
            return (Criteria) this;
        }

        public Criteria andWpidLessThan(Integer value) {
            addCriterion("wpid <", value, "wpid");
            return (Criteria) this;
        }

        public Criteria andWpidLessThanOrEqualTo(Integer value) {
            addCriterion("wpid <=", value, "wpid");
            return (Criteria) this;
        }

        public Criteria andWpidIn(List<Integer> values) {
            addCriterion("wpid in", values, "wpid");
            return (Criteria) this;
        }

        public Criteria andWpidNotIn(List<Integer> values) {
            addCriterion("wpid not in", values, "wpid");
            return (Criteria) this;
        }

        public Criteria andWpidBetween(Integer value1, Integer value2) {
            addCriterion("wpid between", value1, value2, "wpid");
            return (Criteria) this;
        }

        public Criteria andWpidNotBetween(Integer value1, Integer value2) {
            addCriterion("wpid not between", value1, value2, "wpid");
            return (Criteria) this;
        }

        public Criteria andNumberIsNull() {
            addCriterion("number is null");
            return (Criteria) this;
        }

        public Criteria andNumberIsNotNull() {
            addCriterion("number is not null");
            return (Criteria) this;
        }

        public Criteria andNumberEqualTo(Integer value) {
            addCriterion("number =", value, "number");
            return (Criteria) this;
        }

        public Criteria andNumberNotEqualTo(Integer value) {
            addCriterion("number <>", value, "number");
            return (Criteria) this;
        }

        public Criteria andNumberGreaterThan(Integer value) {
            addCriterion("number >", value, "number");
            return (Criteria) this;
        }

        public Criteria andNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("number >=", value, "number");
            return (Criteria) this;
        }

        public Criteria andNumberLessThan(Integer value) {
            addCriterion("number <", value, "number");
            return (Criteria) this;
        }

        public Criteria andNumberLessThanOrEqualTo(Integer value) {
            addCriterion("number <=", value, "number");
            return (Criteria) this;
        }

        public Criteria andNumberIn(List<Integer> values) {
            addCriterion("number in", values, "number");
            return (Criteria) this;
        }

        public Criteria andNumberNotIn(List<Integer> values) {
            addCriterion("number not in", values, "number");
            return (Criteria) this;
        }

        public Criteria andNumberBetween(Integer value1, Integer value2) {
            addCriterion("number between", value1, value2, "number");
            return (Criteria) this;
        }

        public Criteria andNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("number not between", value1, value2, "number");
            return (Criteria) this;
        }

        public Criteria andNjdIsNull() {
            addCriterion("njd is null");
            return (Criteria) this;
        }

        public Criteria andNjdIsNotNull() {
            addCriterion("njd is not null");
            return (Criteria) this;
        }

        public Criteria andNjdEqualTo(Integer value) {
            addCriterion("njd =", value, "njd");
            return (Criteria) this;
        }

        public Criteria andNjdNotEqualTo(Integer value) {
            addCriterion("njd <>", value, "njd");
            return (Criteria) this;
        }

        public Criteria andNjdGreaterThan(Integer value) {
            addCriterion("njd >", value, "njd");
            return (Criteria) this;
        }

        public Criteria andNjdGreaterThanOrEqualTo(Integer value) {
            addCriterion("njd >=", value, "njd");
            return (Criteria) this;
        }

        public Criteria andNjdLessThan(Integer value) {
            addCriterion("njd <", value, "njd");
            return (Criteria) this;
        }

        public Criteria andNjdLessThanOrEqualTo(Integer value) {
            addCriterion("njd <=", value, "njd");
            return (Criteria) this;
        }

        public Criteria andNjdIn(List<Integer> values) {
            addCriterion("njd in", values, "njd");
            return (Criteria) this;
        }

        public Criteria andNjdNotIn(List<Integer> values) {
            addCriterion("njd not in", values, "njd");
            return (Criteria) this;
        }

        public Criteria andNjdBetween(Integer value1, Integer value2) {
            addCriterion("njd between", value1, value2, "njd");
            return (Criteria) this;
        }

        public Criteria andNjdNotBetween(Integer value1, Integer value2) {
            addCriterion("njd not between", value1, value2, "njd");
            return (Criteria) this;
        }

        public Criteria andIsaddIsNull() {
            addCriterion("isadd is null");
            return (Criteria) this;
        }

        public Criteria andIsaddIsNotNull() {
            addCriterion("isadd is not null");
            return (Criteria) this;
        }

        public Criteria andIsaddEqualTo(Integer value) {
            addCriterion("isadd =", value, "isadd");
            return (Criteria) this;
        }

        public Criteria andIsaddNotEqualTo(Integer value) {
            addCriterion("isadd <>", value, "isadd");
            return (Criteria) this;
        }

        public Criteria andIsaddGreaterThan(Integer value) {
            addCriterion("isadd >", value, "isadd");
            return (Criteria) this;
        }

        public Criteria andIsaddGreaterThanOrEqualTo(Integer value) {
            addCriterion("isadd >=", value, "isadd");
            return (Criteria) this;
        }

        public Criteria andIsaddLessThan(Integer value) {
            addCriterion("isadd <", value, "isadd");
            return (Criteria) this;
        }

        public Criteria andIsaddLessThanOrEqualTo(Integer value) {
            addCriterion("isadd <=", value, "isadd");
            return (Criteria) this;
        }

        public Criteria andIsaddIn(List<Integer> values) {
            addCriterion("isadd in", values, "isadd");
            return (Criteria) this;
        }

        public Criteria andIsaddNotIn(List<Integer> values) {
            addCriterion("isadd not in", values, "isadd");
            return (Criteria) this;
        }

        public Criteria andIsaddBetween(Integer value1, Integer value2) {
            addCriterion("isadd between", value1, value2, "isadd");
            return (Criteria) this;
        }

        public Criteria andIsaddNotBetween(Integer value1, Integer value2) {
            addCriterion("isadd not between", value1, value2, "isadd");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}