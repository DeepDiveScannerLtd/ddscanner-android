package com.ddscanner.entities.request;

/**
 * Created by lashket on 29.4.16.
 */
public class CreateSealifeRequest {

    private String name;
    private String habitat;
    private String distribution;
    private String length;
    private String weight;
    private String depth;
    private String scName;
    private String order;

    public String getScClass() {
        return scClass;
    }

    public void setScClass(String scClass) {
        this.scClass = scClass;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    private String scClass;
//    private TypedFile image;
    private String token;
    private String social;
    private String secret;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSocial() {
        return social;
    }

    public void setSocial(String social) {
        this.social = social;
    }

//    public TypedFile getImage() {
//        return image;
//    }
//
//    public void setImage(TypedFile image) {
//        this.image = image;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHabitat() {
        return habitat;
    }

    public void setHabitat(String habitat) {
        this.habitat = habitat;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getScName() {
        return scName;
    }

    public void setScName(String scName) {
        this.scName = scName;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

  /*  public String getScClass() {
        return scClass;
    }

    public void setScClass(String scClass) {
        this.scClass = scClass;
    }*/
}
