package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import play.data.validation.Constraints.*;
import validators.Name;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Entity
public class Tag extends BaseModel {
    @Required
    @Name
    String tagName = "";

    @JsonIgnore
    @ManyToMany(mappedBy = "tagList")
    public List<Recipe> recipeList;


    public static final Finder<Long,Tag> find = new Finder<>(Tag.class);

    public static List<Tag> findAndMergeTagList(List<Tag> tagList){
        List<String> listNames = new ArrayList<>();
        for (Tag i:tagList) {
            listNames.add(i.getTagName());
        }
        List<Tag> tagList2 = find.query().where().in("tagName", listNames).findList();

        for (int i = 0; i< tagList.size();i++) {
            for (Tag tag : tagList2) {
                if (tagList.get(i) != null && tag != null)
                    if (tagList.get(i).getTagName().equals(tag.getTagName())) {
                        tagList.set(i, tag);
                        break;
                    }
            }
        }
        //https://www.baeldung.com/java-remove-duplicates-from-list
        tagList = new ArrayList<>(new HashSet<>(tagList));
        return tagList;

    }

    public Tag(){
        super();
        setTitleXML("tag");
    }

    public Tag (String tagName,List<Recipe> recipeList){
        this.tagName = tagName;
        this.recipeList = recipeList;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }
}
