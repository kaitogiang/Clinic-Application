package entity;

import java.text.Normalizer;

public class AccentRemover {
	
	//Hàm loại bỏ dấu của tiếng Việt
	public static String removeDiacritics(String text) {
		//Normalize the texxt to Unicode normalization form NFD
		String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD);
		return normalizedText.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
}
