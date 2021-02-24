package filters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import models.User;

//https://www.baeldung.com/jackson-serialize-field-custom-criteria
public class UserTokenFilter extends SimpleBeanPropertyFilter {
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
            if (!writer.getName().equals("userToken")) {
                writer.serializeAsField(pojo, jgen, provider);
                return;
            }
            Boolean visible = ((User) pojo).getUserToken().getVisible();
            if (visible) {
                writer.serializeAsField(pojo, jgen, provider);
            }else{
                writer.serializeAsOmittedField(pojo, jgen, provider);
            }
        } else if (!jgen.canOmitFields()) {
            writer.serializeAsOmittedField(pojo, jgen, provider);
        }
    }
}
