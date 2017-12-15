package com.ddscanner.entities;

import com.ddscanner.utils.Helpers;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class DiveCenterProfile implements Serializable{

    public enum DiveCenterServiceType {
        COMPANY, RESELLER
    }

    private Integer id;
    private String name;
    private String photo;
    private int type;
    @SerializedName("country_name")
    private String countryName;
    @SerializedName("country_code")
    private String countryCode;
    private CountryEntity country;
    private ArrayList<Address> addresses;
    private ArrayList<String> emails;
    private ArrayList<String> phones;
    private ArrayList<String> languages;
    @SerializedName("instructors_count")
    private String instructorsCount;
    private ArrayList<DiveSpotPhoto> photos;
    @SerializedName("photos_count")
    private int photosCount;
    @SerializedName("created_spots_count")
    private int createdSpotsCount;
    @SerializedName("edited_spots_count")
    private int editedSpotsCount;
    private String token;
    @SerializedName("where_working_count")
    private Integer workingCount;
    @SerializedName("where_working_dive_spots")
    private ArrayList<DiveSpotShort> workingSpots;
    @SerializedName("new_instructors_exist")
    private boolean isNewInstructors;
    @SerializedName("service_type")
    private Integer serviceType;
    @SerializedName("provider_type")
    private Integer providerType;
    @SerializedName("bio")
    private String about;
    private ArrayList<Brand> brands;
    private ArrayList<Integer> associations;
    @SerializedName("is_dive_shop")
    private int isDiveShop;
    @SerializedName("brands_count")
    private String brandsCount;
    @SerializedName("products_count")
    private String productsCount;
    private boolean isForBooking = false;
    private String diveSpotBookingId;
    private ArrayList<DailyTour> products;
    private ArrayList<FunDive> funDives;
    @SerializedName("fundives_count")
    private int funDivesCount;

    public int getFunDivesCount() {
        return funDivesCount;
    }

    public void setFunDivesCount(int funDivesCount) {
        this.funDivesCount = funDivesCount;
    }

    public ArrayList<FunDive> getFunDives() {
        return funDives;
    }

    public void setFunDives(ArrayList<FunDive> funDives) {
        this.funDives = funDives;
    }

    public String getProductsCount() {
        return "5";
//        return productsCount;
    }

    public void setProductsCount(String productsCount) {
        this.productsCount = productsCount;
    }

    public ArrayList<DailyTour> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<DailyTour> products) {
        this.products = products;
    }

    public boolean isForBooking() {
        return isForBooking;
    }

    public void setForBooking(boolean forBooking) {
        isForBooking = forBooking;
    }

    public String getDiveSpotBookingId() {
        return diveSpotBookingId;
    }

    public void setDiveSpotBookingId(String diveSpotBookingId) {
        this.diveSpotBookingId = diveSpotBookingId;
    }

    public String getBrandsCount() {
        return brandsCount;
    }

    public void setBrandsCount(String brandsCount) {
        this.brandsCount = brandsCount;
    }

    public boolean isDiveShop() {
        if (isDiveShop == 1) {
            return true;
        }
        return false;
    }

    public void setDiveShop(int diveShop) {
        isDiveShop = diveShop;
    }

    public ArrayList<Brand> getBrands() {
        return brands;
    }

    public ArrayList<BaseIdNamePhotoEntity> getBrandsForEditProfile() {
        ArrayList<BaseIdNamePhotoEntity> list = new ArrayList<>();
        for (Brand brand : brands) {
            list.add(new BaseIdNamePhotoEntity(brand.getId(), brand.getName()));
        }
        return list;
    }

    public ArrayList<BaseIdNamePhotoEntity> getAssociatinsForEditProfile() {
        ArrayList<BaseIdNamePhotoEntity> list = new ArrayList<>();
        for (Integer integer : associations) {
            list.add(new BaseIdNamePhotoEntity(Helpers.getAssociationByCode(integer).getId(), Helpers.getAssociationByCode(integer).getName()));
        }
        return list;
    }

    public void setBrands(ArrayList<Brand> brands) {
        this.brands = brands;
    }

    public ArrayList<Integer> getAssociations() {
        return associations;
    }

    public void setAssociations(ArrayList<Integer> associations) {
        this.associations = associations;
    }

    public Integer getProviderType() {
        return providerType;
    }

    public void setProviderType(Integer providerType) {
        this.providerType = providerType;
    }

    public DiveCenterServiceType getServiceType() {
        if (serviceType == null) {
            return DiveCenterServiceType.COMPANY;
        }
        switch (serviceType) {
            case 1:
                return DiveCenterServiceType.COMPANY;
            case 2:
                return DiveCenterServiceType.RESELLER;
            default:
                return null;
        }
    }

    public CountryEntity getCountry() {
        return country;
    }

    public void setCountry(CountryEntity country) {
        this.country = country;
    }

    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isNewInstructors() {
        return isNewInstructors;
    }

    public void setNewInstructors(boolean newInstructors) {
        isNewInstructors = newInstructors;
    }

    public ArrayList<DiveSpotShort> getWorkingSpots() {
        return workingSpots;
    }

    public void setWorkingSpots(ArrayList<DiveSpotShort> workingSpots) {
        this.workingSpots = workingSpots;
    }

    public Integer getWorkingCount() {
        return workingCount;
    }

    public void setWorkingCount(Integer workingCount) {
        this.workingCount = workingCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<Address> addresses) {
        this.addresses = addresses;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<String> emails) {
        this.emails = emails;
    }

    public ArrayList<String> getPhones() {
        return phones;
    }

    public void setPhones(ArrayList<String> phones) {
        this.phones = phones;
    }

    public ArrayList<String> getLanguages() {
        return languages;
    }

    public void setLanguages(ArrayList<String> languages) {
        this.languages = languages;
    }

    public String getInstructorsCount() {
        return instructorsCount;
    }

    public void setInstructorsCount(String instructorsCount) {
        this.instructorsCount = instructorsCount;
    }

    public ArrayList<DiveSpotPhoto> getPhotos() {
        if (photos != null) {
            for (DiveSpotPhoto diveSpotPhoto : photos) {
                PhotoAuthor photoAuthor = new PhotoAuthor(String.valueOf(id), name, photo, type);
                photos.get(photos.indexOf(diveSpotPhoto)).setAuthor(photoAuthor);
            }
            return photos;
        }
        return photos;
    }

    public void setPhotos(ArrayList<DiveSpotPhoto> photos) {
        this.photos = photos;
    }

    public int getPhotosCount() {
        return photosCount;
    }

    public void setPhotosCount(int photosCount) {
        this.photosCount = photosCount;
    }

    public int getCreatedSpotsCount() {
        return createdSpotsCount;
    }

    public void setCreatedSpotsCount(int createdSpotsCount) {
        this.createdSpotsCount = createdSpotsCount;
    }

    public int getEditedSpotsCount() {
        return editedSpotsCount;
    }

    public void setEditedSpotsCount(int editedSpotsCount) {
        this.editedSpotsCount = editedSpotsCount;
    }
}
