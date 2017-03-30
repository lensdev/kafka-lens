package com.lensdev.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Kafkaevent.
 */
@Entity
@Table(name = "kafkaevent")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "kafkaevent")
public class Kafkaevent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kafkakey")
    private String kafkakey;

    @Column(name = "kpartition")
    private Integer kpartition;

    @Column(name = "koffset")
    private Long koffset;

    @Column(name = "body")
    private String body;

    @Column(name = "eventtime")
    private ZonedDateTime eventtime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKafkakey() {
        return kafkakey;
    }

    public Kafkaevent kafkakey(String kafkakey) {
        this.kafkakey = kafkakey;
        return this;
    }

    public void setKafkakey(String kafkakey) {
        this.kafkakey = kafkakey;
    }

    public Integer getKpartition() {
        return kpartition;
    }

    public Kafkaevent kpartition(Integer kpartition) {
        this.kpartition = kpartition;
        return this;
    }

    public void setKpartition(Integer kpartition) {
        this.kpartition = kpartition;
    }

    public Long getKoffset() {
        return koffset;
    }

    public Kafkaevent koffset(Long koffset) {
        this.koffset = koffset;
        return this;
    }

    public void setKoffset(Long koffset) {
        this.koffset = koffset;
    }

    public String getBody() {
        return body;
    }

    public Kafkaevent body(String body) {
        this.body = body;
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ZonedDateTime getEventtime() {
        return eventtime;
    }

    public Kafkaevent eventtime(ZonedDateTime eventtime) {
        this.eventtime = eventtime;
        return this;
    }

    public void setEventtime(ZonedDateTime eventtime) {
        this.eventtime = eventtime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Kafkaevent kafkaevent = (Kafkaevent) o;
        if (kafkaevent.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, kafkaevent.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Kafkaevent{" +
            "id=" + id +
            ", kafkakey='" + kafkakey + "'" +
            ", kpartition='" + kpartition + "'" +
            ", koffset='" + koffset + "'" +
            ", body='" + body + "'" +
            ", eventtime='" + eventtime + "'" +
            '}';
    }
}
