module FoundationsF25 {
	requires javafx.controls;
	requires java.sql;
	requires jdk.incubator.vector;
	requires org.junit.jupiter.api;
	requires junit;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
}
