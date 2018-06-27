package qed.bigdata.infosupplyer.pojo.marktool;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package qed.bigdata.infosupplyer.pojo.marktool
 * @Description: ${todo}
 * @date 2018/6/21 15:07
 */
public class Roi3dEntity {

    String roi3d_series_uid; //唯一性的索引ID，由orthanc生成
    Integer roi3d_signs;    //CT征像
    Integer roi3d_nodule_analysis;  //结节分析
    Integer roi3d_follow_up;    //随访时间
    Integer roi3d_risk_assessment;     //恶性风险评估
    String roi3d_color;     //颜色
    Integer roi3d_width;        //宽度
    Integer roi3d_type;         //图形类型(0椭圆、1多边形)
    Integer roi3d_nodal_position;   //节结位置
    Integer roi3d_num;      //自定义数字，病灶结节序号

    public Roi3dEntity() {
    }

    public Roi3dEntity(String roi3d_series_uid, Integer roi3d_signs, Integer roi3d_nodule_analysis,
                       Integer roi3d_follow_up, Integer roi3d_risk_assessment, String roi3d_color,
                       Integer roi3d_width, Integer roi3d_type, Integer roi3d_nodal_position, Integer roi3d_num) {
        this.roi3d_series_uid = roi3d_series_uid;
        this.roi3d_signs = roi3d_signs;
        this.roi3d_nodule_analysis = roi3d_nodule_analysis;
        this.roi3d_follow_up = roi3d_follow_up;
        this.roi3d_risk_assessment = roi3d_risk_assessment;
        this.roi3d_color = roi3d_color;
        this.roi3d_width = roi3d_width;
        this.roi3d_type = roi3d_type;
        this.roi3d_nodal_position = roi3d_nodal_position;
        this.roi3d_num = roi3d_num;
    }

    public void setRoi3d_series_uid(String roi3d_series_uid) {
        this.roi3d_series_uid = roi3d_series_uid;
    }

    public void setRoi3d_signs(Integer roi3d_signs) {
        this.roi3d_signs = roi3d_signs;
    }

    public void setRoi3d_nodule_analysis(Integer roi3d_nodule_analysis) {
        this.roi3d_nodule_analysis = roi3d_nodule_analysis;
    }

    public void setRoi3d_follow_up(Integer roi3d_follow_up) {
        this.roi3d_follow_up = roi3d_follow_up;
    }

    public void setRoi3d_risk_assessment(Integer roi3d_risk_assessment) {
        this.roi3d_risk_assessment = roi3d_risk_assessment;
    }

    public void setRoi3d_color(String roi3d_color) {
        this.roi3d_color = roi3d_color;
    }

    public void setRoi3d_width(Integer roi3d_width) {
        this.roi3d_width = roi3d_width;
    }

    public void setRoi3d_type(Integer roi3d_type) {
        this.roi3d_type = roi3d_type;
    }

    public void setRoi3d_nodal_position(Integer roi3d_nodal_position) {
        this.roi3d_nodal_position = roi3d_nodal_position;
    }

    public void setRoi3d_num(Integer roi3d_num) {
        this.roi3d_num = roi3d_num;
    }

    public String getRoi3d_series_uid() {
        return roi3d_series_uid;
    }

    public Integer getRoi3d_signs() {
        return roi3d_signs;
    }

    public Integer getRoi3d_nodule_analysis() {
        return roi3d_nodule_analysis;
    }

    public Integer getRoi3d_follow_up() {
        return roi3d_follow_up;
    }

    public Integer getRoi3d_risk_assessment() {
        return roi3d_risk_assessment;
    }

    public String getRoi3d_color() {
        return roi3d_color;
    }

    public Integer getRoi3d_width() {
        return roi3d_width;
    }

    public Integer getRoi3d_type() {
        return roi3d_type;
    }

    public Integer getRoi3d_nodal_position() {
        return roi3d_nodal_position;
    }

    public Integer getRoi3d_num() {
        return roi3d_num;
    }
}
