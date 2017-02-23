package com.ddscanner.entities;

public class SpotPhotoEditScreen {

    private int authorId;
    private String photoPath;
    private boolean isCover;

    public SpotPhotoEditScreen(int authorId, String photoPath, boolean isCover) {
        this.authorId = authorId;
        this.photoPath = photoPath;
        this.isCover = isCover;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public boolean isCover() {
        return isCover;
    }

    public void setCover(boolean cover) {
        isCover = cover;
    }
}
