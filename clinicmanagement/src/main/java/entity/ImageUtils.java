package entity;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.scene.image.Image;
import services.Database;

public class ImageUtils {
	//Hàm lưu trữ file ảnh lên cơ sở dữ liệu
	public static int storeImage(String imagePath) {
		String sql = "INSERT INTO images(image_data) VALUES(?)";
		try(Connection con = Database.connectDB()) {
			PreparedStatement ps = con.prepareStatement(sql);
			FileInputStream fis = new FileInputStream(imagePath);
			ps.setBinaryStream(1, fis, fis.available());
			//fis.available() là chiều dài của stream
			ps.executeUpdate();
			//Hàm trả về id khi upload lên
			String idSql = "SELECT image_id FROM images ORDER BY image_id DESC LIMIT 1";
			PreparedStatement idPs = con.prepareStatement(idSql);
			ResultSet idRs = idPs.executeQuery();
			if (idRs.next()) {
				return idRs.getInt(1);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	//Hàm lấy hình ảnh từ cơ sở dữ liệu dựa trên id của hình ảnh
	public static Image retrieveImage(int imageId) {
		String sql = "SELECT image_data FROM images WHERE image_id = ?";
		try(Connection con = Database.connectDB()) {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, imageId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				byte[] imageData = rs.getBytes("image_data");
				InputStream inputStream = new ByteArrayInputStream(imageData);
				return new Image(inputStream);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	//Hàm cập nhật hình ảnh trên cơ sở dữ liệu
	public static void updateImage(String imagePath, int imageId) {
		String sql = "UPDATE images SET image_data = ? WHERE image_id = ?";
		try (Connection con = Database.connectDB()) {
			PreparedStatement ps = con.prepareStatement(sql);
			FileInputStream fis = new FileInputStream(imagePath);
			ps.setBinaryStream(1, fis, fis.available());
			ps.setInt(2, imageId);
			int update = ps.executeUpdate();
			if (update==1) {
				System.out.println("Avatar updated successfully!!");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
