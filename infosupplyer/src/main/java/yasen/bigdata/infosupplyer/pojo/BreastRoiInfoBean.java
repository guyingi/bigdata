package yasen.bigdata.infosupplyer.pojo;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.infosupplyer.pojo
 * @Description: ${todo}
 * @date 2018/5/23 14:30
 */
public class BreastRoiInfoBean {
    String series_uid;
    String location;
    Integer classification;
    Integer shape;
    Integer boundary1;
    Integer boundary2;
    Integer density;
    Integer quadrant;
    Integer risk;

    public void setSeries_uid(String series_uid) {
        this.series_uid = series_uid;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setClassification(Integer classification) {
        this.classification = classification;
    }

    public void setShape(Integer shape) {
        this.shape = shape;
    }

    public void setBoundary1(Integer boundary1) {
        this.boundary1 = boundary1;
    }

    public void setBoundary2(Integer boundary2) {
        this.boundary2 = boundary2;
    }

    public void setDensity(Integer density) {
        this.density = density;
    }

    public void setQuadrant(Integer quadrant) {
        this.quadrant = quadrant;
    }

    public void setRisk(Integer risk) {
        this.risk = risk;
    }

    public String getSeries_uid() {
        return series_uid;
    }

    public String getLocation() {
        return location;
    }

    public Integer getClassification() {
        return classification;
    }

    public Integer getShape() {
        return shape;
    }

    public Integer getBoundary1() {
        return boundary1;
    }

    public Integer getBoundary2() {
        return boundary2;
    }

    public Integer getDensity() {
        return density;
    }

    public Integer getQuadrant() {
        return quadrant;
    }

    public Integer getRisk() {
        return risk;
    }

    @Override
    public String toString() {
        return "BreastRoiInfoBean{" +
                "location='" + location + '\'' +
                ", classification=" + classification +
                ", shape=" + shape +
                ", boundary1=" + boundary1 +
                ", boundary2=" + boundary2 +
                ", density=" + density +
                ", quadrant=" + quadrant +
                ", risk=" + risk +
                '}';
    }
}
