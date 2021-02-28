package models;

import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.annotation.UpdatedTimestamp;
import play.data.format.Formats.DateTime;
import utils.DateUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.util.Date;

@MappedSuperclass
public class BaseModel extends Model {
    @Id
    public Long id;
    @JsonIgnore
    @Version
    @Column(updatable=false)
    Long version;
    @DateTime(pattern=DateUtils.DATETIME_FORMAT)
    @CreatedTimestamp
    @Column(updatable=false)
    Date whenCreated;
    @DateTime(pattern=DateUtils.DATETIME_FORMAT)
    @UpdatedTimestamp
    @Column(updatable=false)
    Date whenUpdated;
    @JsonIgnore
    @Column(updatable=false)
    String titleXML = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Date getWhenCreated() {
        return whenCreated;
    }

    public void setWhenCreated(Date whenCreated) {
        this.whenCreated = whenCreated;
    }

    public Date getWhenUpdated() {
        return whenUpdated;
    }

    public void setWhenUpdated(Date whenUpdated) {
        this.whenUpdated = whenUpdated;
    }

    public String getTitleXML() {
        return titleXML;
    }

    public void setTitleXML(String title) {
        this.titleXML = title;
    }

}