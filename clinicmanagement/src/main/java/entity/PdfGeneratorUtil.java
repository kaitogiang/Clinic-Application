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
	
	//Hàm xuất PDF hóa đơn
	public static void exportInvoicePDF(String invoiceId, Patient patient, ObservableList<PrescriptionDetail> medicineList, String path) {
        System.out.println("id trong pdf la: "+invoiceId);
		
		Document document = new Document(PageSize.A4, -20, -20, 10, 50);
		LocalDate date = LocalDate.now();
        try {
        	PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
         // Create a BaseFont object, sử dụng times.ttf vì nó chạy ok
            BaseFont timesNewRoman = BaseFont.createFont("C:\\Users\\SuBur\\Downloads\\FontTimesNewRoman\\times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            
            // Tạo các Font chữ
            Font normalFont = new Font(timesNewRoman, 15);
            Font smallFont = new Font(timesNewRoman, 13);
            Font normalFontBold = new Font(timesNewRoman, 15, Font.BOLD);
            Font bigFont = new Font(timesNewRoman, 18, Font.BOLD);
            Font mediumFontBold = new Font(timesNewRoman, 16, Font.BOLD);
            Font smallFontItalic = new Font(timesNewRoman, 13, Font.ITALIC);
            
            //Tạo phần tiêu đề và id hóa đơn
            Paragraph brand = new Paragraph("HÓA ĐƠN BÁN HÀNG\n", bigFont);
            Paragraph dateTime = new Paragraph("Ngày " + date.getDayOfMonth() + " tháng " +date.getMonthValue()+ " năm "+date.getYear(), normalFont);
            dateTime.setAlignment(Element.ALIGN_CENTER);
            
            Paragraph combine = new Paragraph();
            combine.add(brand);
            combine.add(dateTime);
            combine.setAlignment(Element.ALIGN_CENTER);
            
            Paragraph invoiceIdTitle = new Paragraph("Id hóa đơn: "+invoiceId, smallFont);
            
            //Thiết lập căn giữa tiêu đề và căn phải id hóa đơn
            brand.setAlignment(Element.ALIGN_CENTER); //Tiêu đề căn giữa
            invoiceIdTitle.setAlignment(Element.ALIGN_RIGHT); //Id hóa đơn căn phải
            invoiceIdTitle.setPaddingTop(40);
            
            //Tạo một bảng hiển thị tiêu đề và id hóa đơn
            float[] columnWidths = {2.5f, 5f, 2.5f}; //Bảng gồm 3 cột
            PdfPTable invoiceTitle = new PdfPTable(columnWidths);
            invoiceTitle.getDefaultCell().setBorder(Rectangle.NO_BORDER); //Loại bỏ viền của bảng
            //I. Tạo dòng đầu tiên
            //1. Tạo cell dành cho tiêu đề
            PdfPCell invoiceTitleCell = new PdfPCell();
            invoiceTitleCell.setBorder(Rectangle.NO_BORDER);
            invoiceTitleCell.addElement(combine);
            invoiceTitleCell.setPadding(0);
            
            //2. Tạo cell dành cho id hóa đơn
            PdfPCell invoiceIdCell = new PdfPCell();
            invoiceIdCell.setBorder(Rectangle.NO_BORDER);
            invoiceIdCell.addElement(invoiceIdTitle);
            invoiceIdCell.setPadding(10);
            
            //Thêm 3 cell trên vào bảng, phải đủ 3 cell ứng với 3 cột thì bảng mới show
            invoiceTitle.addCell(""); //Cột đầu rỗng
            invoiceTitle.addCell(invoiceTitleCell); //Cột giữa là tiêu đề "HÓA DƠN BÁN HÀNG"
            invoiceTitle.addCell(invoiceIdCell); //Cột 3 là id hóa đơn
            document.add(invoiceTitle);
            
            //Tạo bảng thứ hai để lưu trữ thông tin mã số thuế, địa chỉ phòng khám
            float[] clinicSize = {2.4f, 5.5f};
            PdfPTable clinicInfoTable = new PdfPTable(columnWidths);
            //Dòng đầu tiên mã số thuế
            Paragraph title1 = new Paragraph("Mã số thuế:", normalFontBold);
            Paragraph title1Value = new Paragraph("1800271748", normalFont);
            //Ô đầu tiên dòng 1
            PdfPCell title1Cell = new PdfPCell();
            title1Cell.setBorder(Rectangle.NO_BORDER);
            title1Cell.addElement(title1);
            //Ô thứ 2 dòng 1
            PdfPCell title1ValueCell = new PdfPCell();
            title1ValueCell.setBorder(Rectangle.NO_BORDER);
            title1ValueCell.setColspan(2);
            title1ValueCell.addElement(title1Value);
            //Thêm dòng đầu tiên vào bảng clinic
            clinicInfoTable.addCell(title1Cell);
            clinicInfoTable.addCell(title1ValueCell);
            
            //Dòng thứ hai địa chỉ
            Paragraph title2 = new Paragraph("Địa chỉ:",normalFontBold);
            Paragraph title2Value = new Paragraph("208 Đường 3 tháng 4, Phường Xuân Khánh, Quận Ninh Kiều, Thành phố Cần Thơ", normalFont);
            //Ô đầu tiên dòng hai
            PdfPCell title2Cell = new PdfPCell();
            title2Cell.setBorder(Rectangle.NO_BORDER);
            title2Cell.addElement(title2);
            //Ô thứ hai dòng hai
            PdfPCell title2ValueCell = new PdfPCell();
            title2ValueCell.setBorder(Rectangle.NO_BORDER);
            title2ValueCell.setColspan(2);
            title2ValueCell.addElement(title2Value);
            //Thêm dòng hai vào bảng clinic
            clinicInfoTable.addCell(title2Cell);
            clinicInfoTable.addCell(title2ValueCell);
            
            document.add(clinicInfoTable);
            float[] columnWidths2 = {3.5f, 5f, 1.5f}; //Bảng gồm 3 cột
            //Tiêu đề "Thông tin người mua"
            PdfPTable customerTable = new PdfPTable(columnWidths2);
            Paragraph customerInfoTitle = new Paragraph("Thông tin người mua",bigFont);
            //Dòng tiêu đề
            PdfPCell customerInfoTitleCell = new PdfPCell();
            customerInfoTitleCell.setBorder(Rectangle.NO_BORDER);
            customerInfoTitleCell.setColspan(3);
            customerInfoTitleCell.addElement(customerInfoTitle);
            //Thêm dòng đầu vào bảng customer
            customerTable.addCell(customerInfoTitleCell);
            
            //Dòng thứ hai thông tin họ tên người mua
            Paragraph customerNameTitle = new Paragraph("Họ tên người mua:",normalFontBold);
            Paragraph customerNameValue = new Paragraph(patient.getPatientNameValue().toUpperCase(), normalFont); //Đổi chỗ này thành patient.getName
            //Ô thứ nhất dòng hai
            PdfPCell customerNameTitleCell = new PdfPCell();
            customerNameTitleCell.setBorder(Rectangle.NO_BORDER);
            customerNameTitleCell.addElement(customerNameTitle);
            //Ô thứ hai dòng hai
            PdfPCell customerNameValueCell = new PdfPCell();
            customerNameValueCell.setBorder(Rectangle.NO_BORDER);
            customerNameValueCell.setColspan(2);
            customerNameValueCell.addElement(customerNameValue);
            
            customerTable.addCell(customerNameTitleCell);
            customerTable.addCell(customerNameValueCell);
            
            //Dòng thứ ba địa chỉ customer
            Paragraph addressTitle = new Paragraph("Địa chỉ: ",normalFontBold);
            Paragraph addressValue = new Paragraph(patient.getPatientAddressValue(), normalFont); //Đổi chỗ này thành patient.getAdress
            //Ô thứ nhất dòng 3
            PdfPCell addressTitleCell = new PdfPCell();
            addressTitleCell.setBorder(Rectangle.NO_BORDER);
            addressTitleCell.addElement(addressTitle);
            //Ô thứ hai dòng 3
            PdfPCell addressValueCell = new PdfPCell();
            addressValueCell.setBorder(Rectangle.NO_BORDER);
            addressValueCell.setColspan(2);
            addressValueCell.addElement(addressValue);
            //Thêm dòng 3 vào bảng customerTable
            customerTable.addCell(addressTitleCell);
            customerTable.addCell(addressValueCell);

            //Dòng thứ tư hình thức chuyển khoản
            Paragraph transferTitle = new Paragraph("Hình thức chuyển khoản: ",normalFontBold);
            Paragraph transferValue = new Paragraph("Tiền mặt/Chuyển khoản", normalFont);
            //Ô đầu tiên dòng tư
            PdfPCell transferTitleCell = new PdfPCell();
            transferTitleCell.setBorder(Rectangle.NO_BORDER);
            transferTitleCell.addElement(transferTitle);
            //Ô thứ hai dòng tư
            PdfPCell transferValueCell = new PdfPCell();
            transferValueCell.setBorder(Rectangle.NO_BORDER);
            transferValueCell.setColspan(2);
            transferValueCell.addElement(transferValue);
            //Thêm dòng thứ tư vào bảng customerTable
            customerTable.addCell(transferTitleCell);
            customerTable.addCell(transferValueCell);
            
            //Dòng thứ năm tiêu đề "Thông tin đơn thuốc"
            Paragraph prescriptionTitle = new Paragraph("Thông tin đơn thuốc",bigFont);
            PdfPCell prescriptionTitleCell = new PdfPCell();
            prescriptionTitleCell.setBorder(Rectangle.NO_BORDER);
            prescriptionTitleCell.addElement(prescriptionTitle);
            prescriptionTitleCell.setColspan(3);
            
            customerTable.addCell(prescriptionTitleCell);

            
            document.add(customerTable);
            
            //Tạo bảng hiển thị danh sách thuốc
            float[] columnWidths3 = {1f, 3f, 2f, 2f, 2f};
            PdfPTable medicineTable = new PdfPTable(columnWidths3);
            medicineTable.setSpacingBefore(12);
            //Các tiêu đề bảng
            Paragraph orderTitle = new Paragraph("STT", normalFontBold);
            Paragraph medicineTitle = new Paragraph("Tên hàng hóa", normalFontBold);
            Paragraph quantity = new Paragraph("Số lượng", normalFontBold);
            Paragraph unitPrice = new Paragraph("Đơn giá", normalFontBold);
            Paragraph totalAmount = new Paragraph("Thành tiền", normalFontBold);
            
            //Căn giữa các tiêu đề
            orderTitle.setAlignment(Element.ALIGN_CENTER);
            medicineTitle.setAlignment(Element.ALIGN_CENTER);
            quantity.setAlignment(Element.ALIGN_CENTER);
            unitPrice.setAlignment(Element.ALIGN_CENTER);
            totalAmount.setAlignment(Element.ALIGN_CENTER);
            
            //Tạo cell cho các tiêu đề
            //Cell cho số thứ tự
            PdfPCell orderCell = new PdfPCell();
            orderCell.addElement(orderTitle);
            orderCell.setPaddingBottom(10);
            
            //Cell cho số tên hàng hóa
            PdfPCell medicineCell = new PdfPCell();
            medicineCell.addElement(medicineTitle);
            medicineCell.setPaddingBottom(10);
            
            //Cell cho số lượng
            PdfPCell quantityCell = new PdfPCell();
            quantityCell.addElement(quantity);
            quantityCell.setPaddingBottom(10);
            
            //Cell cho đơn giá
            PdfPCell unitPriceCell = new PdfPCell();
            unitPriceCell.addElement(unitPrice);
            unitPriceCell.setPaddingBottom(10);
            
            //Cell cho đơn giá
            PdfPCell totalAmountCell = new PdfPCell();
            totalAmountCell.addElement(totalAmount);
            totalAmountCell.setPaddingBottom(10);
            
            //Thêm các cell tiêu đề vào bảng medicineTable
            medicineTable.addCell(orderCell);
            medicineTable.addCell(medicineCell);
            medicineTable.addCell(quantityCell);
            medicineTable.addCell(unitPriceCell);
            medicineTable.addCell(totalAmountCell);
            
            int i;
            float totalSum = 0;
            //Thay đổi số phần tử bằng với kích thước list
            for (i=0; i<medicineList.size(); i++) {
            	//Gắn kết nội dung
            	Paragraph order = new Paragraph(String.valueOf(i+1), normalFont);
            	Paragraph medicineName = new Paragraph(medicineList.get(i).getMedicineNameValue(), normalFont);
            	Paragraph medicineQuantity = new Paragraph(String.valueOf(medicineList.get(i).getMedicineQuantityValue()), normalFont);
            	Paragraph price = new Paragraph(medicineList.get(i).getMedicineValue().getUnitPriceValue()+" VND", normalFont);
            	float medicineSum = medicineList.get(i).getMedicineValue().getUnitPriceValue()*medicineList.get(i).getMedicineQuantityValue();
            	totalSum += medicineSum;
            	Paragraph total = new Paragraph(medicineSum+" VND", normalFont);
            	//Căn giữa các text
            	order.setAlignment(Element.ALIGN_CENTER);
            	medicineName.setAlignment(Element.ALIGN_CENTER);
            	medicineQuantity.setAlignment(Element.ALIGN_CENTER);
            	price.setAlignment(Element.ALIGN_CENTER);
            	total.setAlignment(Element.ALIGN_CENTER);
            	//Tạo cell cho các ô
            	PdfPCell orderContentCell = new PdfPCell();
            	orderContentCell.addElement(order);
            	
            	PdfPCell medicineContentCell = new PdfPCell();
            	medicineContentCell.addElement(medicineName);
            	
            	PdfPCell quantityContentCell = new PdfPCell();
            	quantityContentCell.addElement(medicineQuantity);
            	
            	PdfPCell priceContentCell = new PdfPCell();
            	priceContentCell.addElement(price);
            	
            	PdfPCell totalContentCell = new PdfPCell();
            	totalContentCell.addElement(total);
            	
            	medicineTable.addCell(orderContentCell);
            	medicineTable.addCell(medicineContentCell);
            	medicineTable.addCell(quantityContentCell);
            	medicineTable.addCell(priceContentCell);
            	medicineTable.addCell(totalContentCell);
            	
            }

            document.add(medicineTable);
            
            Paragraph sum = new Paragraph("Tổng tiền: "+totalSum+" VND", normalFont); //Thay đổi chỗ này
            sum.setAlignment(Element.ALIGN_RIGHT);
            sum.setIndentationRight(65);
            document.add(sum);
            
            
            document.close();
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	
}
