package controller.admin;

import domain.Member;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import repository.MemberRepository;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import static util.ControllerUtil.sqlDateToLocalDate;
import static util.PageUtil.*;

public class MemberInfoController implements Initializable {

    @FXML
    TextField nameField, birthField, phoneField;

    @FXML
    private RadioButton male, female;

    @FXML
    private void addMember(ActionEvent event) throws IOException {

    }

    @FXML
    private void updateMember(ActionEvent event) throws IOException {

    }

    @FXML
    private void deleteMember(ActionEvent event) throws IOException {

    }

    @FXML
    private void memberDetail(MouseEvent event) throws IOException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnBinding();
        loadMemberData();
    }

    private void loadMemberData() {
        MemberRepository memberRepository = new MemberRepository();
        List<Member> members = memberRepository.findAllMembers();

        // 조회한 회원 정보를 TableView에 설정
        membersTable.setItems(FXCollections.observableArrayList(members));
    }

    @FXML
    private TableView<Member> membersTable;

    @FXML
    private TableColumn<Member, String> numCol, nameCol, genderCol, emailCol, birthCol, phoneCol, enrollCol;

    private void columnBinding() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        numCol.setCellValueFactory(new PropertyValueFactory<>("num"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        birthCol.setCellValueFactory(cellData -> {
            Date sqlDate = cellData.getValue().getBirthDate();
            return sqlDateToLocalDate(sqlDate, formatter);
        });

        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        enrollCol.setCellValueFactory(cellData -> {
            Date sqlDate = cellData.getValue().getEnrolDate();
            return sqlDateToLocalDate(sqlDate, formatter);
        });
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePageCenter(event, "Admin", "/view/admin/helloAdmin");
    }
}