package com.sky.medialib.ui.kit.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class GalleryFolder {
    private int count;
    private String dir;
    private ArrayList<GalleryModel> filteredImages = new ArrayList();
    private String firstImagePath;
    private String name;

    public String getDir() {
        return this.dir;
    }

    public void setDir(String str) {
        this.dir = str;
        this.name = str.substring(str.lastIndexOf("/") + 1);
    }

    public String getFilteredFirstImageUrl() {
        if (this.filteredImages.size() <= 0 || this.filteredImages.get(0) == null) {
            return null;
        }
        if (TextUtils.isEmpty(((GalleryModel) this.filteredImages.get(0)).getCoverPath())) {
            return ((GalleryModel) this.filteredImages.get(0)).getFilePath();
        }
        return ((GalleryModel) this.filteredImages.get(0)).getCoverPath();
    }

    public String getFirstImagePath() {
        return this.firstImagePath;
    }

    public void setFirstImagePath(String str) {
        this.firstImagePath = str;
    }

    public String getName() {
        return this.name;
    }

    public int getCount() {
        return this.count;
    }

    public int getFilterdCount() {
        return this.filteredImages.size();
    }

    public void setCount(int i) {
        this.count = i;
    }

    public void addImage(GalleryModel galleryModel) {
        this.filteredImages.add(galleryModel);
    }

    public void addImages(List<GalleryModel> list) {
        if (list != null && !list.isEmpty()) {
            this.filteredImages.addAll(list);
        }
    }

    public ArrayList<GalleryModel> getImages() {
        return this.filteredImages;
    }

    public GalleryModel getImage(int i) {
        if (i < 0 || i >= this.filteredImages.size()) {
            return null;
        }
        return (GalleryModel) this.filteredImages.get(i);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.dir.equals(((GalleryFolder) obj).dir);
    }

    public int hashCode() {
        return this.dir.hashCode();
    }
}
