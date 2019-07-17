package rpg.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author ljq
 *
 */
public class UserzbExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public UserzbExample() {
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

        public Criteria andIdEqualTo(String value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(String value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(String value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(String value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(String value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(String value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLike(String value) {
            addCriterion("id like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotLike(String value) {
            addCriterion("id not like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<String> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<String> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(String value1, String value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(String value1, String value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andUsernameIsNull() {
            addCriterion("username is null");
            return (Criteria) this;
        }

        public Criteria andUsernameIsNotNull() {
            addCriterion("username is not null");
            return (Criteria) this;
        }

        public Criteria andUsernameEqualTo(String value) {
            addCriterion("username =", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotEqualTo(String value) {
            addCriterion("username <>", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameGreaterThan(String value) {
            addCriterion("username >", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameGreaterThanOrEqualTo(String value) {
            addCriterion("username >=", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLessThan(String value) {
            addCriterion("username <", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLessThanOrEqualTo(String value) {
            addCriterion("username <=", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLike(String value) {
            addCriterion("username like", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotLike(String value) {
            addCriterion("username not like", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameIn(List<String> values) {
            addCriterion("username in", values, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotIn(List<String> values) {
            addCriterion("username not in", values, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameBetween(String value1, String value2) {
            addCriterion("username between", value1, value2, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotBetween(String value1, String value2) {
            addCriterion("username not between", value1, value2, "username");
            return (Criteria) this;
        }

        public Criteria andZbidIsNull() {
            addCriterion("zbid is null");
            return (Criteria) this;
        }

        public Criteria andZbidIsNotNull() {
            addCriterion("zbid is not null");
            return (Criteria) this;
        }

        public Criteria andZbidEqualTo(Integer value) {
            addCriterion("zbid =", value, "zbid");
            return (Criteria) this;
        }

        public Criteria andZbidNotEqualTo(Integer value) {
            addCriterion("zbid <>", value, "zbid");
            return (Criteria) this;
        }

        public Criteria andZbidGreaterThan(Integer value) {
            addCriterion("zbid >", value, "zbid");
            return (Criteria) this;
        }

        public Criteria andZbidGreaterThanOrEqualTo(Integer value) {
            addCriterion("zbid >=", value, "zbid");
            return (Criteria) this;
        }

        public Criteria andZbidLessThan(Integer value) {
            addCriterion("zbid <", value, "zbid");
            return (Criteria) this;
        }

        public Criteria andZbidLessThanOrEqualTo(Integer value) {
            addCriterion("zbid <=", value, "zbid");
            return (Criteria) this;
        }

        public Criteria andZbidIn(List<Integer> values) {
            addCriterion("zbid in", values, "zbid");
            return (Criteria) this;
        }

        public Criteria andZbidNotIn(List<Integer> values) {
            addCriterion("zbid not in", values, "zbid");
            return (Criteria) this;
        }

        public Criteria andZbidBetween(Integer value1, Integer value2) {
            addCriterion("zbid between", value1, value2, "zbid");
            return (Criteria) this;
        }

        public Criteria andZbidNotBetween(Integer value1, Integer value2) {
            addCriterion("zbid not between", value1, value2, "zbid");
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

        public Criteria andIsuseIsNull() {
            addCriterion("isuse is null");
            return (Criteria) this;
        }

        public Criteria andIsuseIsNotNull() {
            addCriterion("isuse is not null");
            return (Criteria) this;
        }

        public Criteria andIsuseEqualTo(Integer value) {
            addCriterion("isuse =", value, "isuse");
            return (Criteria) this;
        }

        public Criteria andIsuseNotEqualTo(Integer value) {
            addCriterion("isuse <>", value, "isuse");
            return (Criteria) this;
        }

        public Criteria andIsuseGreaterThan(Integer value) {
            addCriterion("isuse >", value, "isuse");
            return (Criteria) this;
        }

        public Criteria andIsuseGreaterThanOrEqualTo(Integer value) {
            addCriterion("isuse >=", value, "isuse");
            return (Criteria) this;
        }

        public Criteria andIsuseLessThan(Integer value) {
            addCriterion("isuse <", value, "isuse");
            return (Criteria) this;
        }

        public Criteria andIsuseLessThanOrEqualTo(Integer value) {
            addCriterion("isuse <=", value, "isuse");
            return (Criteria) this;
        }

        public Criteria andIsuseIn(List<Integer> values) {
            addCriterion("isuse in", values, "isuse");
            return (Criteria) this;
        }

        public Criteria andIsuseNotIn(List<Integer> values) {
            addCriterion("isuse not in", values, "isuse");
            return (Criteria) this;
        }

        public Criteria andIsuseBetween(Integer value1, Integer value2) {
            addCriterion("isuse between", value1, value2, "isuse");
            return (Criteria) this;
        }

        public Criteria andIsuseNotBetween(Integer value1, Integer value2) {
            addCriterion("isuse not between", value1, value2, "isuse");
            return (Criteria) this;
        }

        public Criteria andEnhanceIsNull() {
            addCriterion("enhance is null");
            return (Criteria) this;
        }

        public Criteria andEnhanceIsNotNull() {
            addCriterion("enhance is not null");
            return (Criteria) this;
        }

        public Criteria andEnhanceEqualTo(Integer value) {
            addCriterion("enhance =", value, "enhance");
            return (Criteria) this;
        }

        public Criteria andEnhanceNotEqualTo(Integer value) {
            addCriterion("enhance <>", value, "enhance");
            return (Criteria) this;
        }

        public Criteria andEnhanceGreaterThan(Integer value) {
            addCriterion("enhance >", value, "enhance");
            return (Criteria) this;
        }

        public Criteria andEnhanceGreaterThanOrEqualTo(Integer value) {
            addCriterion("enhance >=", value, "enhance");
            return (Criteria) this;
        }

        public Criteria andEnhanceLessThan(Integer value) {
            addCriterion("enhance <", value, "enhance");
            return (Criteria) this;
        }

        public Criteria andEnhanceLessThanOrEqualTo(Integer value) {
            addCriterion("enhance <=", value, "enhance");
            return (Criteria) this;
        }

        public Criteria andEnhanceIn(List<Integer> values) {
            addCriterion("enhance in", values, "enhance");
            return (Criteria) this;
        }

        public Criteria andEnhanceNotIn(List<Integer> values) {
            addCriterion("enhance not in", values, "enhance");
            return (Criteria) this;
        }

        public Criteria andEnhanceBetween(Integer value1, Integer value2) {
            addCriterion("enhance between", value1, value2, "enhance");
            return (Criteria) this;
        }

        public Criteria andEnhanceNotBetween(Integer value1, Integer value2) {
            addCriterion("enhance not between", value1, value2, "enhance");
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