package controller.admin;

import domain.Member;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import repository.MemberRepository;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class HelloAdminController implements Initializable {

    @FXML
    private TableView<Member> membersTable;

    @FXML
    private TableColumn<Member, String> id, name, gender, email, birth, phone, enroll;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnBinding();
        loadMemberData();
    }

    private void columnBinding() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        gender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        birth.setCellValueFactory(cellData -> {
            Date sqlDate = cellData.getValue().getBirthDate();
            return sqlDateToLocalDate(sqlDate, formatter);
        });
        
        phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        
        enroll.setCellValueFactory(cellData -> {
            Date sqlDate = cellData.getValue().getEnrolDate();
            return sqlDateToLocalDate(sqlDate, formatter);
        });
    }

    private static SimpleStringProperty sqlDateToLocalDate(Date sqlDate, DateTimeFormatter formatter) {
        LocalDate localDate = sqlDate.toLocalDate();
        String formattedDate = localDate.format(formatter);
        return new SimpleStringProperty(formattedDate);
    }

    private void loadMemberData() {
        MemberRepository memberRepository = new MemberRepository();
        List<Member> members = memberRepository.findAllMembers();

        // 조회한 회원 정보를 TableView에 설정
        membersTable.setItems(FXCollections.observableArrayList(members));
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/memberLogin.fxml"));
        Scene scene = new Scene(loginRoot);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Login");

        stage.setScene(scene);
        stage.show();
    }
}