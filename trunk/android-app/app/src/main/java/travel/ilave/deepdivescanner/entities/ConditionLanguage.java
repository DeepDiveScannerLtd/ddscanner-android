package travel.ilave.deepdivescanner.entities;

/**
 * Created by Vitaly on 28.11.2015.
 */
public class ConditionLanguage {
    //    {
//        "id": "2",
//            "name": "Afar"
//    }
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
