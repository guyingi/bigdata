package qed.bigdata.infosupplyer.pojo.marktool;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package qed.bigdata.infosupplyer.pojo.marktool
 * @Description: ${todo}
 * @date 2018/6/21 15:24
 */
public class Roi2dEntity {
    Integer roi3d_num;
    String roi2d_series_uid;
    String roi2d_instances_uid;
    Integer roi2d_dim;
    Integer roi2d_slice;
    String roi2d_points;

    public Roi2dEntity() {
    }

    public Roi2dEntity(Integer roi3d_num, String roi2d_series_uid, String roi2d_instances_uid, Integer roi2d_dim, Integer roi2d_slice, String roi2d_points) {
        this.roi3d_num = roi3d_num;
        this.roi2d_series_uid = roi2d_series_uid;
        this.roi2d_instances_uid = roi2d_instances_uid;
        this.roi2d_dim = roi2d_dim;
        this.roi2d_slice = roi2d_slice;
        this.roi2d_points = roi2d_points;
    }

    public void setRoi3d_num(Integer roi3d_num) {
        this.roi3d_num = roi3d_num;
    }

    public void setRoi2d_series_uid(String roi2d_series_uid) {
        this.roi2d_series_uid = roi2d_series_uid;
    }

    public void setRoi2d_instances_uid(String roi2d_instances_uid) {
        this.roi2d_instances_uid = roi2d_instances_uid;
    }

    public void setRoi2d_dim(Integer roi2d_dim) {
        this.roi2d_dim = roi2d_dim;
    }

    public void setRoi2d_slice(Integer roi2d_slice) {
        this.roi2d_slice = roi2d_slice;
    }

    public void setRoi2d_points(String roi2d_points) {
        this.roi2d_points = roi2d_points;
    }

    public Integer getRoi3d_num() {
        return roi3d_num;
    }

    public String getRoi2d_series_uid() {
        return roi2d_series_uid;
    }

    public String getRoi2d_instances_uid() {
        return roi2d_instances_uid;
    }

    public Integer getRoi2d_dim() {
        return roi2d_dim;
    }

    public Integer getRoi2d_slice() {
        return roi2d_slice;
    }

    public String getRoi2d_points() {
        return roi2d_points;
    }
}
