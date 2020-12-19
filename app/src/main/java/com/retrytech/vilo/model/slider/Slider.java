
package com.retrytech.vilo.model.slider;

import java.util.List;
import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class Slider {

    @Expose
    private String id;
    @Expose
    private String image;
    @Expose
    private List<Slider> slider;
    @Expose
    private String url;

    private String btntext;

    public String getBtntext() {
        return btntext;
    }

    public void setBtntext(String btntext) {
        this.btntext = btntext;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Slider> getSlider() {
        return slider;
    }

    public void setSlider(List<Slider> slider) {
        this.slider = slider;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Slider{" +
                "id='" + id + '\'' +
                ", image='" + image + '\'' +
                ", slider=" + slider +
                ", url='" + url + '\'' +
                '}';
    }
}
