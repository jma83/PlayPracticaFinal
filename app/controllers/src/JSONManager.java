package controllers.src;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import models.BaseModel;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import utils.MessageUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JSONManager {

    public static String formatJsonError(JsonNode node){
        StringBuilder str = new StringBuilder();
        Iterator<Map.Entry <String ,JsonNode>> it = node.fields();
        while (it.hasNext()){
            Map.Entry <String ,JsonNode> map = it.next();
            str.append(map.getKey()).append(": ").append(map.getValue());
            str = new StringBuilder(str.toString().replace("\"", ""));
            if (it.hasNext())
                str.append(", ");
        }
        return str.toString();

    }
    public static Result genericJsonResponse(Boolean success, String errorMsg, Integer status){
        ObjectNode objectNode = Json.newObject();
        objectNode.put(MessageUtils.success, success);
        objectNode.put(MessageUtils.message, errorMsg);
        return Results.status(status,objectNode);
    }


    public static String getResultJson(FilterProvider filters, List<BaseModel> modelList) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writer(filters).writeValueAsString(modelList);
    }
}
