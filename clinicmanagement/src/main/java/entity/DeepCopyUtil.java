package entity;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DeepCopyUtil {

	public static ObservableList<Patient> createDeepObservableListCopy(ObservableList<Patient> obj) {
		ObservableList<Patient> copyOb = FXCollections.observableArrayList();
		for(Patient e : obj) {
			Patient copiedPatient = new Patient(e);
			copyOb.add(copiedPatient);
		}
		return copyOb;
	}
	
	public static ObservableList<Medicine> createDeepMedicineListCopy(ObservableList<Medicine> obj) {
		ObservableList<Medicine> copyOb = FXCollections.observableArrayList();
		for(Medicine e : obj) {
			Medicine copiedMedicine = new Medicine(e); 
			copyOb.add(copiedMedicine);
		}
		return copyOb;
	}
	
}
