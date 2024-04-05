package controller.admin;

import domain.Gender;
import domain.Item;
import domain.Member;
import domain.SelectedMember;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static repository.PurchaseRepository.*;
import static util.PageUtil.movePageCenter;

public class MemberDetailController implements Initializable {

    @FXML private TextField nameField, phoneField, emailField;
    @FXML private DatePicker birthPicker, enrollPicker;
    @FXML private RadioButton maleButton, femaleButton;
    @FXML private Label gymTicket, PT_ticket, trainer;
    private ToggleGroup genderRadio = new ToggleGroup();

    // 관리자가 선택한 회원 정보를 불러온다.
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        maleButton.setToggleGroup(genderRadio);
        femaleButton.setToggleGroup(genderRadio);

        if (SelectedMember.currentMember != null) {
            Member member = SelectedMember.currentMember;
            LocalDate birthDate = member.getBirthDate().toLocalDate();
            LocalDate enrollDate = member.getEnrolDate().toLocalDate();

            nameField.setText(member.getName());
            phoneField.setText(member.getPhone());
            emailField.setText(member.getEmail());
            birthPicker.setValue(birthDate);
            enrollPicker.setValue(enrollDate);

            gymTicket.setText(getRemain(member.getNum(), Item.GYM_TICKET).toString() + "일 남음");
            PT_ticket.setText(getRemain(member.getNum(), Item.PT_TICKET).toString() + "개");

            if (member.getGender().equals(Gender.M)) {
                genderRadio.selectToggle(maleButton);
            } else {
                genderRadio.selectToggle(femaleButton);
            }
        }
    }

    @FXML
    private void updateMember(ActionEvent event) throws IOException {

    }

    @FXML
    private void deleteMember(ActionEvent event) throws IOException {

    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/admin/memberInfo");
    }
}