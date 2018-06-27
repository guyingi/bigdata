package qed.bigdata.infosupplyer.pojo.marktool;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package qed.bigdata.infosupplyer.pojo.marktool
 * @Description: ${todo}
 * @date 2018/6/21 15:37
 */
public class BreastRoiEntity {
    String series_uid;
    String instances_uid;
    String location;
    Integer classification;
    Integer shape;
    String boundary;
    Integer boundary1;
    Integer boundary2;
    Integer density;
    Integer quadrant;
    Integer risk;
    String points;
    String type;
    Integer uid;
    String series_description;
    String tool_state_manager;
    String restore_data;

    public BreastRoiEntity() {
    }

    public void setSeries_uid(String series_uid) {
        this.series_uid = series_uid;
    }

    public void setInstances_uid(String instances_uid) {
        this.instances_uid = instances_uid;
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

    public void setBoundary(String boundary) {
        this.boundary = boundary;
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

    public void setPoints(String points) {
        this.points = points;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public void setSeries_description(String series_description) {
        this.series_description = series_description;
    }

    public void setTool_state_manager(String tool_state_manager) {
        this.tool_state_manager = tool_state_manager;
    }

    public void setRestore_data(String restore_data) {
        this.restore_data = restore_data;
    }

    public String getSeries_uid() {
        return series_uid;
    }

    public String getInstances_uid() {
        return instances_uid;
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

    public String getBoundary() {
        return boundary;
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

    public String getPoints() {
        return points;
    }

    public String getType() {
        return type;
    }

    public Integer getUid() {
        return uid;
    }

    public String getSeries_description() {
        return series_description;
    }

    public String getTool_state_manager() {
        return tool_state_manager;
    }

    public String getRestore_data() {
        return restore_data;
    }
}
