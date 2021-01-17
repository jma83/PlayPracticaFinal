package models;

import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.UpdatedTimestamp;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.sql.Timestamp;

@MappedSuperclass
public class BaseModel extends Model {
    @Id
    public Long id;
    @Version
    Long version;
    @CreatedTimestamp
    Timestamp whenCreated;
    @UpdatedTimestamp
    Timestamp whenUpdated;
}