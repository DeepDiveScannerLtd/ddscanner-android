package travel.ilave.deepdivescanner.entities;

import java.util.List;

/**
 * Created by Vitaly on 28.11.2015.
 */
public class ProductDetails {
//    id": "72",
//            "name": "Praesentium iusto quia",
//            "images": [
//            "trizerilocal.travel/images/products/small/phuket_family_trips_4.jpg",
//            "trizerilocal.travel/images/products/small/phuket_water_activites_xxxl.jpg",
//            ],
//            "rating": 1,
//            "dept": 30,
//            "visiblity": "excent",
//            "access": "by boat",
//            "sealife": [
//            "trizerilocal.travel/images/ddscanner/icons/sealife/fish18.svg",
//            "trizerilocal.travel/images/ddscanner/icons/sealife/fish8.svg",
//            ]

    private String id;
    private String name;
    private List<String> images;
    private int rating;
    private int dept;
    private String visiblity;
    private String access;
    private List<String> sealife;

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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getDept() {
        return dept;
    }

    public void setDept(int dept) {
        this.dept = dept;
    }

    public String getVisiblity() {
        return visiblity;
    }

    public void setVisiblity(String visibility) {
        this.visiblity = visiblity;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public List<String> getSealife() {
        return sealife;
    }

    public void setSealife(List<String> sealife) {
        this.sealife = sealife;
    }
}
