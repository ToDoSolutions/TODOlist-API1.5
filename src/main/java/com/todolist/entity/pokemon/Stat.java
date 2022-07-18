package com.todolist.entity.pokemon;

import com.fasterxml.jackson.annotation.*;
import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "base_stat",
        "effort",
        "stat"
})
@Generated("jsonschema2pojo")
public class Stat {

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    @JsonProperty("base_stat")
    private Integer baseStat;
    @JsonProperty("effort")
    private Integer effort;
    @JsonProperty("stat")
    private InfoStat stat;

    @JsonProperty("base_stat")
    public Integer getBaseStat() {
        return baseStat;
    }

    @JsonProperty("base_stat")
    public void setBaseStat(Integer baseStat) {
        this.baseStat = baseStat;
    }

    @JsonProperty("effort")
    public Integer getEffort() {
        return effort;
    }

    @JsonProperty("effort")
    public void setEffort(Integer effort) {
        this.effort = effort;
    }

    @JsonProperty("stat")
    public InfoStat getStat() {
        return stat;
    }

    @JsonProperty("stat")
    public void setStat(InfoStat stat) {
        this.stat = stat;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
