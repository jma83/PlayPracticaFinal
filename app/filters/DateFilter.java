package filters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import models.BaseModel;
import utils.DateUtils;

import java.util.Date;

public class DateFilter extends SimpleBeanPropertyFilter {
    @Override
    protected boolean include(BeanPropertyWriter writer) {
        return super.include(writer);
    }

    @Override
    protected boolean include(PropertyWriter writer) {
        return super.include(writer);
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        if (include(writer)) {
            if (writer.getName().equals("whenCreated")) {
                Date dateC = ((BaseModel) pojo).getWhenCreated();
                ((BaseModel) pojo).setWhenCreated(DateUtils.convertTimestamp(dateC));
                return;
            }
            if (writer.getName().equals("whenUpdated")) {
                Date dateU = ((BaseModel) pojo).getWhenUpdated();
                ((BaseModel) pojo).setWhenUpdated(DateUtils.convertTimestamp(dateU));
                writer.serializeAsField(pojo, jgen, provider);
                return;
            }
            writer.serializeAsField(pojo, jgen, provider);
            return;

        } else if (!jgen.canOmitFields()) {
            writer.serializeAsOmittedField(pojo, jgen, provider);
        }
    }
}
