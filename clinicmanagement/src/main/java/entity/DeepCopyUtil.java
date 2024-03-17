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
}
