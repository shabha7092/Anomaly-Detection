package com.shabha.anomalydetection;

import com.enterprisemath.utils.DomainUtils;
import com.enterprisemath.utils.ValidationUtils;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Histogram.
 *
 * @param <T> type of the observations
 */
public class Histogram<T> {

    public static class Builder<T> {

     
        private Map<T, Integer> observations = new HashMap<>();
        
        public Builder<T> addObservation(T observation) {
            if (!observations.containsKey(observation)) {
                observations.put(observation, 0);
            }
            observations.put(observation, observations.get(observation) + 1);
            return this;
        }
    
        public Builder<T> addObservations(T observation, int num) {
            ValidationUtils.guardNotNegativeInt(num, "num cannot be negative");
            if (!observations.containsKey(observation)) {
                observations.put(observation, 0);
            }
            observations.put(observation, observations.get(observation) + num);
            return this;
        }

        public Histogram<T> build() {
            return new Histogram<>(this);
        }

    }

    private Map<T, Integer> observations;

    public Histogram(Builder<T> builder) {
        observations = Collections.unmodifiableMap(DomainUtils.softCopyMap(builder.observations));
        guardInvariants();
    }

    
    private void guardInvariants() {
        ValidationUtils.guardNotNullMap(observations, "observations cannot have null key or value");
    }

  
    public int getTotalNumObservations() {
        int res = 0;
        for (T key : observations.keySet()) {
            res = res + observations.get(key);
        }
        return res;
    }

    public Set<T> getObservations() {
        return Collections.unmodifiableSet(observations.keySet());
    }

    public int getNum(T observation) {
        if (observations.containsKey(observation)) {
            return observations.get(observation);
        }
        else {
            return 0;
        }
    }

    public List<T> getTopN(int n) {
        List<T> res = DomainUtils.softCopyList(observations.keySet());
        Collections.sort(res, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                if (observations.get(o1) > observations.get(o2)) {
                    return -1;
                }
                else if (observations.get(o1) < observations.get(o2)) {
                    return 1;
                }
                else if (o1 instanceof Comparable) {
                    return ((Comparable) o1).compareTo(o2);
                }
                else {
                    return 0;
                }
            }
        });
        if (res.size() < n) {
            return res;
        }
        return res.subList(0, n);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


    public static <T> Histogram<T> create(T obs1, int num, Object... others) {
        Histogram.Builder<T> res = new Histogram.Builder<>();
        res.addObservations(obs1, num);
        for (int i = 0; i < others.length; i = i + 2) {
            res.addObservations((T) others[i], (Integer) others[i + 1]);
        }
        return res.build();
    }

    public static <T> int getDistance(Histogram<T> h1, Histogram<T> h2) {
        Set<T> obss = new HashSet<>();
        obss.addAll(h1.getObservations());
        obss.addAll(h2.getObservations());
        int res = 0;
        for (T obs : obss) {
            res = res + Math.abs(h1.getNum(obs) - h2.getNum(obs));
        }
        return res;
    }
}
