package controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import services.Database;

public class AdminHomeController implements Initializable{

    @FXML
    private BarChart<String, Number> annualRevenue;

    @FXML
    private BarChart<String, Number> averagePatientChar;

    @FXML
    private Label averagePatientQuantity;

    @FXML
    private Label dailyRevenue;

    @FXML
    private Label doctorQuantity;

    @FXML
    private Label medicalSuppliesStatus;

    @FXML
    private BarChart<String, Number> monthlyRevenue;

    @FXML
    private Label pharmacistQuantity;

    @FXML
    private Label receptionistQuantity;

    @FXML
    private PieChart suppliesStatusChar;

    @FXML
    private Label totalRevenue;

    @FXML
    private BarChart<String, Number> weeklyRevenue;

    //user-defined variable
    enum TimeType {
    	LAST_WEEK,
    	LAST_MONTH,
    	LAST_YEAR
    }
    
    private Map<String, String> memberQuantity; 
    
    private Map<String, Number> lastWeekRevenueData; //Chứa danh sách doanh thu trong tuần qua
    
    private Map<String, Number> lastMonthRevenueData; //Chứa danh sách doanh thu trong tháng
    
    private Map<String, Number> lastYearRevenueData; //Chứa danh sách doanh thu trong năm
    
    private Map<String, Number> quantityOfPatient; //Số lượng bệnh nhân khám trong tuàn7
    
    private ObservableList<PieChart.Data> medicalSuppliesList;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		memberQuantity = new HashMap<>();
		getQuantityMember(memberQuantity);
		setStatisticCard();
		//Gán dữ liệu cho các map
		lastWeekRevenueData = getRevenueByTime(TimeType.LAST_WEEK);
		lastMonthRevenueData = getRevenueByTime(TimeType.LAST_MONTH);
		lastYearRevenueData = getRevenueByTime(TimeType.LAST_YEAR);
		quantityOfPatient = getPatientQuantityInWeek();
		medicalSuppliesList = getMedicalSuppliesList();
		//Hiển thị các chart
		showRevenueChart(TimeType.LAST_WEEK);
		showRevenueChart(TimeType.LAST_MONTH);
		showRevenueChart(TimeType.LAST_YEAR);
		showPatientChart();
		suppliesStatusChar.getData().addAll(medicalSuppliesList);
		
	}
	//Hàm lấy số lượng phần tử
	public void getQuantityMember(Map<String, String> map) {
		int spoiledQuantity = 0;
		
		String docSql = "SELECT COUNT(*) doctorQuantity FROM doctors";
		String pharSql = "SELECT COUNT(*) pharmacistQuantity FROM pharmacists";
		String repSql = "SELECT COUNT(*) receptionistQuantity FROM receptionists";
		String patientSql = "SELECT IFNULL(ROUND(AVG(quantity)),0) "
				+ "average_patient FROM "
				+ "(SELECT COUNT(*) quantity FROM `diagnosis` "
				+ "WHERE diagnosis_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 1 WEEK) "
				+ "AND CURDATE() GROUP BY DATE(diagnosis_date)) subqury;";
		String suppliesSql = "SELECT * FROM medical_supplies";
		String totalRevenue = "SELECT SUM(total_amount) total_revenue FROM invoice WHERE 1";
		String dailyRevenue = "SELECT IFNULL(AVG(total), 0) average "
				+ "FROM ( SELECT SUM(total_amount) total FROM `invoice` "
				+ "WHERE creation_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 1 WEEK) "
				+ "AND CURDATE() GROUP BY DATE(creation_date)) subquery;";
		
		
		try(Connection con = Database.connectDB()) {
			PreparedStatement ps1 = con.prepareStatement(docSql);
			PreparedStatement ps2 = con.prepareStatement(pharSql);
			PreparedStatement ps3 = con.prepareStatement(repSql);
			PreparedStatement ps4 = con.prepareStatement(patientSql);
			PreparedStatement ps5 = con.prepareStatement(suppliesSql);
			PreparedStatement ps6 = con.prepareStatement(totalRevenue);
			PreparedStatement ps7 = con.prepareStatement(dailyRevenue);
			
			ResultSet rs1 = ps1.executeQuery();
			ResultSet rs2 = ps2.executeQuery();
			ResultSet rs3 = ps3.executeQuery();
			ResultSet rs4 = ps4.executeQuery();
			ResultSet rs5 = ps5.executeQuery();
			ResultSet rs6 = ps6.executeQuery();
			ResultSet rs7 = ps7.executeQuery();
			
			if (rs1.next()) {
				map.put("doctorQuantity", rs1.getInt("doctorQuantity")+" người");
			}
			if (rs2.next()) {
				map.put("pharmacistQuantity", rs2.getInt("pharmacistQuantity") + " người");
			}
			if (rs3.next()) {
				map.put("receptionistQuantity", rs3.getInt("receptionistQuantity")+" người");
			}
			if (rs4.next()) {
				map.put("averagePatient", rs4.getInt("average_patient")+ " BN");
			}
			
			while(rs5.next()) {
				spoiledQuantity += rs5.getInt("spoiled_quantity");
			}
			if (spoiledQuantity == 0) {
				map.put("spoiledQuantity", "Tất cả tốt");
			} else {
				map.put("spoiledQuantity", spoiledQuantity+" vật tư hỏng");
			}
			
			if (rs6.next()) {
				map.put("totalRevenue", rs6.getFloat("total_revenue")+"VND");
			}
			
			if(rs7.next()) {
				map.put("daylyRevenue", rs7.getFloat("average")+"VND");
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
    
    public void setStatisticCard() {
    	doctorQuantity.setText(memberQuantity.get("doctorQuantity"));
    	pharmacistQuantity.setText(memberQuantity.get("pharmacistQuantity"));
    	receptionistQuantity.setText(memberQuantity.get("receptionistQuantity"));
    	totalRevenue.setText(memberQuantity.get("totalRevenue"));
    	dailyRevenue.setText(memberQuantity.get("daylyRevenue"));
    	averagePatientQuantity.setText(memberQuantity.get("averagePatient"));
    	medicalSuppliesStatus.setText(memberQuantity.get("spoiledQuantity"));
    }
    
    public Map<String, Number> getRevenueByTime(TimeType condition) {
    	Map<String, Number> map = new HashMap<String, Number>();
    	String sql = "SELECT timeType(creation_date) date, "
    			+ "SUM(total_amount) total FROM invoice "
    			+ "WHERE DATE(creation_date) BETWEEN DATE_SUB(CURDATE(), INTERVAL time) "
    			+ "AND CURDATE() GROUP BY timeType(creation_date)";
    	if (condition == TimeType.LAST_WEEK) {
    		sql = sql.replaceAll("timeType", "DATE");
    		sql = sql.replaceAll("time", "1 WEEK");
    		try(Connection con = Database.connectDB()) {
    			PreparedStatement ps = con.prepareStatement(sql);
    			ResultSet rs = ps.executeQuery();
    			while(rs.next()) {
    				map.put(rs.getString("date"), rs.getFloat("total"));
    			}
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    		
    	} else if (condition == TimeType.LAST_MONTH) {
    		sql = sql.replaceAll("timeType", "WEEK");
    		sql = sql.replaceAll("time", "1 MONTH");
    		try(Connection con = Database.connectDB()) {
    			PreparedStatement ps = con.prepareStatement(sql);
    			ResultSet rs = ps.executeQuery();
    			while(rs.next()) {
    				map.put("Tuần "+rs.getInt("date"), rs.getFloat("total"));
    			}
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    	} else if (condition == TimeType.LAST_YEAR) {
    		sql = sql.replaceAll("timeType", "MONTH");
    		sql = sql.replaceAll("time", "1 YEAR");
    		try(Connection con = Database.connectDB()) {
    			PreparedStatement ps = con.prepareStatement(sql);
    			ResultSet rs = ps.executeQuery();
    			while(rs.next()) {
    				map.put("Tháng "+rs.getInt("date"), rs.getFloat("total"));
    			}
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	return map;
    }
    
    public void showRevenueChart(TimeType condition) {
    	if (condition == TimeType.LAST_WEEK) {
    		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
    		series.setName("Ngày");
    		lastWeekRevenueData.forEach((key, value)->{
    			series.getData().add(new XYChart.Data<String, Number>(key, value));
    		});
    		weeklyRevenue.getData().add(series);
    	} else if (condition == TimeType.LAST_MONTH) {
    		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
    		series.setName("Số tuần");
    		lastMonthRevenueData.forEach((key, value)->{
    			series.getData().add(new XYChart.Data<String, Number>(key, value));
    		});
    		monthlyRevenue.getData().add(series);
    	} else {
    		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
    		series.setName("Tháng");
    		lastYearRevenueData.forEach((key, value)->{
    			series.getData().add(new XYChart.Data<String, Number>(key, value));
    		});
    		annualRevenue.getData().add(series);
    	}
    	
    }
    
    public Map<String, Number> getPatientQuantityInWeek() {
    	String sql = "SELECT DATE(date) date, COUNT(*) quantity FROM history "
    			+ "WHERE DATE(date) BETWEEN DATE_SUB(CURDATE(), INTERVAL 1 WEEK) "
    			+ "AND CURDATE() GROUP BY DATE(date)";
    	Map<String, Number> map = new HashMap<String, Number>();
    	try(Connection con = Database.connectDB()) {
    		PreparedStatement ps = con.prepareStatement(sql);
    		ResultSet rs = ps.executeQuery();
    		while(rs.next()) {
    			map.put(rs.getString("date"), rs.getInt("quantity"));
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	return map;
    }
    
    public void showPatientChart() {
    	XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
    	series.setName("Ngày");
    	quantityOfPatient.forEach((key, value)->{
    		series.getData().add(new XYChart.Data<String, Number>(key, value));
    	});
    	averagePatientChar.getData().add(series);
    }
    
    public ObservableList<PieChart.Data> getMedicalSuppliesList() {
    	ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
    	String sql = "SELECT SUM(quantity - spoiled_quantity) good FROM medical_supplies";
    	String secondSql = "SELECT SUM(spoiled_quantity) spoiled FROM medical_supplies";
    	try(Connection con = Database.connectDB()) {
    		PreparedStatement ps = con.prepareStatement(sql);
    		PreparedStatement ps2 = con.prepareStatement(secondSql);
    		
    		ResultSet rs1 = ps.executeQuery();
    		ResultSet rs2 = ps2.executeQuery();
    		
    		if (rs1.next()) {
    			list.add(new PieChart.Data("Còn tốt", rs1.getInt("good")));
    		}
    		if (rs2.next()) {
    			list.add(new PieChart.Data("Đã hỏng", rs2.getInt("spoiled")));
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return list;
    }
}
