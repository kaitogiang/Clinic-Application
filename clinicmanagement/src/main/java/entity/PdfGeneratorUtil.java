package entity;

import java.io.FileOutputStream;
import java.time.LocalDate;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.collections.ObservableList;

public class PdfGeneratorUtil {

	public static void exportPrescriptionPDF(String path, Patient patient, String prescriptionId, 
		String doctorId, String docName, String diagnosis, ObservableList<PrescriptionDetail> prescription) {
		//Đặt margin cho giấy A4, left, right, top, bottom
        Document document = new Document(PageSize.A4, -20, -20, 10, 50);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            
            float[] columnWidths = {5.5f, 1f, 2.5f};
            PdfPTable table = new PdfPTable(columnWidths);
            table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

         // Create a BaseFont object, sử dụng times.ttf vì nó chạy ok
            BaseFont timesNewRoman = BaseFont.createFont("C:\\Users\\SuBur\\Downloads\\FontTimesNewRoman\\times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            
            // Tạo các Font chữ
            Font normalFont = new Font(timesNewRoman, 15);
            Font smallFont = new Font(timesNewRoman, 13);
            Font normalFontBold = new Font(timesNewRoman, 15, Font.BOLD);
            Font bigFont = new Font(timesNewRoman, 18, Font.BOLD);
            Font mediumFontBold = new Font(timesNewRoman, 16, Font.BOLD);
            Font smallFontItalic = new Font(timesNewRoman, 13, Font.ITALIC);
            
            Paragraph brandText = new Paragraph("Phòng khám Tai Mũi Họng", normalFontBold);
            Paragraph idText = new Paragraph("Mã BN: " +patient.getPatientIdValue() + "\nID: "+prescriptionId, smallFont);
            
            // Left-aligned text
            table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(brandText);

            table.addCell("");  // Empty cell

            // Right-aligned text
            table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            table.addCell(idText);

//            table.writeSelectedRows(0, -1, rect.getLeft(30), rect.getTop(20), writer.getDirectContent());
            document.add(table);
            
            // Add centered text below the table
            Paragraph centeredText = new Paragraph("ĐƠN THUỐC", bigFont);
            centeredText.setAlignment(Element.ALIGN_CENTER);
            centeredText.setSpacingAfter(10f); //Tạo một dòng trống bên dưới đoạn này
            document.add(centeredText);
            
            //Tạo một bảng hiển thị thông tin bệnh nhân
            PdfPTable informationTable = new PdfPTable(columnWidths);
            informationTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            //Tạo một ổ để lưu trữ họ tên bệnh nhân
            PdfPCell nameCell = new PdfPCell();
            nameCell.setBorder(Rectangle.NO_BORDER);
            //Thông tin của ô đầu tiên dòng 1
            Phrase nameTitle = new Phrase("Họ tên: ", normalFont);
            Phrase nameContent = new Phrase(patient.getPatientNameValue().toUpperCase(), mediumFontBold);
            Phrase combinedText = new Phrase();
            //Thông tin của ô thứ hai dòng 1
            Phrase ageContent = new Phrase("Tuổi: "+patient.getPatientAgeValue(), normalFont);
            //Thông tin của ô thứ ba dòng 1
            Phrase weightContent = new Phrase("Cân nặng: "+patient.getPatientWeightValue()+"kg", normalFont);
            
            combinedText.add(nameTitle);
            combinedText.add(nameContent);
            nameCell.addElement(combinedText);
            //Thêm ô đầu tiên
            informationTable.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            informationTable.addCell(nameCell);
            //Thêm ổ hiển thị tuổi
            informationTable.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);            
            informationTable.addCell(ageContent);
            informationTable.addCell(weightContent);
            //Thêm dòng thứ hai
            Paragraph addressContent = new Paragraph("Địa chỉ liên hệ: "+patient.getPatientAddressValue(), normalFont);
            PdfPCell expandCell = new PdfPCell();
            expandCell.setColspan(3);
            expandCell.addElement(addressContent);
            expandCell.setBorder(Rectangle.NO_BORDER);
            informationTable.addCell(expandCell);
            //Thêm dòng chuẩn đoán
            Paragraph diagnosisContent = new Paragraph("Chuẩn đoán: "+diagnosis,normalFont);
            PdfPCell diagnosisCell = new PdfPCell();
            diagnosisCell.setColspan(3);
            diagnosisCell.addElement(diagnosisContent);
            diagnosisCell.setBorder(Rectangle.NO_BORDER);
            informationTable.addCell(diagnosisCell);
            //Thêm dòng tiêu đề cho các đơn thuốc
            Paragraph prescriptionTitle = new Paragraph("Thuốc điều trị:", bigFont);
            PdfPCell prescriptionCell = new PdfPCell();
            prescriptionCell.setColspan(3);
            prescriptionCell.addElement(prescriptionTitle);
            prescriptionCell.setBorder(Rectangle.NO_BORDER);
            informationTable.addCell(prescriptionCell);
            document.add(informationTable);
            //Thêm dòng hiển thị danh sách thuốc được bác sĩ tạo
            PdfPTable medicineTable = new PdfPTable(columnWidths);
            //Dòng đầu tiên
            for(PrescriptionDetail e : prescription) {
            	Paragraph firstMedicineRow = new Paragraph(e.getOrderNumberValue()+". "+e.getMedicineNameValue(), normalFont);
            	Paragraph medicineUsage = new Paragraph(e.getUsageValue(),smallFontItalic);
                medicineUsage.setIndentationLeft(20);
            	PdfPCell firstCell1 = new PdfPCell();
                medicineTable.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                firstCell1.setColspan(2);
                firstCell1.setBorder(Rectangle.NO_BORDER);
                firstCell1.addElement(firstMedicineRow);
                firstCell1.addElement(medicineUsage);
                medicineTable.addCell(firstCell1);
                            
                Paragraph medicineQuantity = new Paragraph(String.valueOf(e.getMedicineQuantityValue()), normalFont);
                medicineQuantity.setAlignment(Element.ALIGN_RIGHT);
                PdfPCell firstCell2 = new PdfPCell();
                firstCell2.setBorder(Rectangle.NO_BORDER);
                firstCell2.addElement(medicineQuantity);
                medicineTable.addCell(firstCell2);
            }
            
            document.add(medicineTable);
            
            //Tạo ngày tháng năm ở góc dưới bên phải
            LocalDate currentDate = LocalDate.now();

            int day = currentDate.getDayOfMonth();
            int month = currentDate.getMonthValue();
            int year = currentDate.getYear();
            
            
            PdfContentByte canvas = writer.getDirectContent();
            Paragraph text = new Paragraph("Ngày " + day+ " tháng "+ month +" năm "+year,normalFont);
            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, text, document.right() - 65, document.bottom() + 90, 0);
            	//Title vai trò
            Paragraph roleText = new Paragraph("Bác sĩ/Y sĩ khám bệnh", normalFont);
            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, roleText, document.right() - 65, document.bottom() + 70, 0);

            	//Họ tên bác sĩ
            Phrase doctorName = new Phrase(docName,normalFontBold);
            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, doctorName, document.right() - 65, document.bottom(), 0);
            
            
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
