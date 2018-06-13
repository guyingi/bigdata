package qed.bigdata.es.pojo;

/**
 * @Title: Dicom.java
 * @Package yasen.bigdata.es.pojo
 * @Description: 该类是一个dicom信息封装类，里面主要包含页面需要显示的一些元素字段
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

public class Dicom {
	String id;
	String InstitutionName;
	String organ;
	String SeriesDescription;
	String PatientName;
	String SeriesDate;
	Integer NumberOfSlices;
	String tag;
	String hdfspath;

	public Dicom() {
	}

    public Dicom(String id, String institutionName, String organ, String seriesDescription, String patientName,
				 String seriesDate, Integer numberOfSlices, String tag,String hdfspath) {
        this.id = id;
		this.InstitutionName = institutionName;
        this.organ = organ;
		this.SeriesDescription = seriesDescription;
		this.PatientName = patientName;
		this.SeriesDate = seriesDate;
		this.NumberOfSlices = numberOfSlices;
		this.tag = tag;
        this.hdfspath = hdfspath;
    }

	public void setId(String id) {
		this.id = id;
	}

    public void setInstitutionName(String institutionName) {
		InstitutionName = institutionName;
	}

	public void setOrgan(String organ) {
		this.organ = organ;
	}

	public void setSeriesDescription(String seriesDescription) {
		SeriesDescription = seriesDescription;
	}

	public void setPatientName(String patientName) {
		PatientName = patientName;
	}

	public void setSeriesDate(String seriesDate) {
		SeriesDate = seriesDate;
	}

	public void setNumberOfSlices(Integer numberOfSlices) {
		NumberOfSlices = numberOfSlices;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setHdfspath(String hdfspath) {
		this.hdfspath = hdfspath;
	}


	public String getId() {
		return id;
	}

	public String getInstitutionName() {
		return InstitutionName;
	}

	public String getOrgan() {
		return organ;
	}

	public String getSeriesDescription() {
		return SeriesDescription;
	}

	public String getPatientName() {
		return PatientName;
	}

	public String getSeriesDate() {
		return SeriesDate;
	}

	public Integer getNumberOfSlices() {
		return NumberOfSlices;
	}

	public String getTag() {
		return tag;
	}

	public String getHdfspath() {
        return hdfspath;
    }
}
