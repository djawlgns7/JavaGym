package controller.admin;

import domain.*;
import domain.member.EntryLog;
import domain.member.Member;
import domain.trainer.Trainer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import repository.EntryLogRepository;
import repository.MemberRepository;
import repository.TrainerRepository;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static domain.member.SelectedMember.*;
import static util.AlertUtil.*;
import static util.ControllerUtil.*;
import static util.PageUtil.movePageCenter;
import static util.MemberUtil.*;

public class MemberDetailController implements Initializable {

    private final EntryLogRepository entryLogRepository = new EntryLogRepository();
    private final MemberRepository memberRepository = new MemberRepository();
    private final TrainerRepository trainerRepository = new TrainerRepository();

    @FXML
    private TextField nameField, phoneField, emailField, trainerField, lockerNumField;

    @FXML
    private Spinner gymTicketSpinner, PTTicketSpinner, clothesSpinner, lockerSpinner;

    @FXML
    private DatePicker birthPicker;

    @FXML
    private RadioButton maleButton, femaleButton;

    @FXML
    private TableView<EntryLog> entryTable;

    @FXML
    private TableColumn<EntryLog, Number> countColumn;

    @FXML
    private TableColumn<EntryLog, String> entryLogColumn;

    @FXML
    private final ToggleGroup genderRadio = new ToggleGroup();

    // 관리자가 선택한 회원의 정보를 불러온다.
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (currentMember != null) {
            Member member = currentMember;
            Integer memberNum = member.getNum();

            // 회원의 일반 정보와 부가 정보 설정
            setBasicInfo(member, birthPicker);
            setAdditionalInfo(memberNum);

            // 입장 로그 설정
            columnBinding();
            loadEntryLog(memberNum);

        }
    }

    private void setBasicInfo(Member member, DatePicker birthPicker) {

        // DatePicker를 사용하기 위해 SQL Date를 Local Date로 변환한다.
        LocalDate birthDate = member.getBirthDate().toLocalDate();

        nameField.setText(member.getName());
        phoneField.setText(member.getPhone());
        emailField.setText(member.getEmail());
        birthPicker.setValue(birthDate);

        maleButton.setToggleGroup(genderRadio);
        femaleButton.setToggleGroup(genderRadio);

        if (member.getGender().equals(Gender.M)) {
            genderRadio.selectToggle(maleButton);
        } else {
            genderRadio.selectToggle(femaleButton);
        }
    }

    private void setAdditionalInfo(Integer memberNum) {
        List<Integer> remains = getRemainAll(memberNum);
        Spinner[] spinners = {gymTicketSpinner, PTTicketSpinner, clothesSpinner, lockerSpinner};

        for (int i = 0; i < 4; i++) {
            Integer remain = remains.get(i);
            if (remain != 0) {
                spinners[i].getValueFactory().setValue(remain);
            }
        }

        // promptText를 사용하기 위해 값이 유효할 때만 값을 설정한다.
        String lockerNum = getLockerNum(memberNum).toString();
        if (!lockerNum.equals("0")) {
            lockerNumField.setText(lockerNum);
        }

        Trainer findTrainer = trainerRepository.findByNum(getTrainerNum(memberNum));
        if (findTrainer != null) {
            trainerField.setText(findTrainer.getName());
        }
    }

    @FXML
    private void updateMember(ActionEvent event) throws IOException {

        // 검증 로직 추후 구현

        // 정상 로직
        currentMember.setName(nameField.getText().trim());
        currentMember.setGender(Gender.valueOf(getSelectedGender(maleButton, femaleButton)));
        currentMember.setEmail(emailField.getText().trim());
        currentMember.setPhone(phoneField.getText().trim());
        currentMember.setBirthDate(Date.valueOf(birthPicker.getValue()));
        memberRepository.updateMember(currentMember);

        Optional<ButtonType> result = showAlertChoose("회원 정보를 수정하시겠습니까?");

        if (result.get() == ButtonType.OK){
            memberRepository.updateMember(currentMember);
            showAlertAndMove("알림", "회원이 수정되었습니다.", Alert.AlertType.INFORMATION, "/view/admin/memberInfo", event);
        }
    }

    @FXML
    private void deleteMember(ActionEvent event) throws IOException {

        Optional<ButtonType> result = showAlertChoose("정말로 " + currentMember.getName() + " 회원을 삭제하시겠습니까?");

        if (result.get() == ButtonType.OK){
            memberRepository.deleteMember(currentMember.getNum());
            showAlertAndMove("알림", "회원이 삭제되었습니다.", Alert.AlertType.INFORMATION, "/view/admin/memberInfo", event);
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        movePageCenter(event, "/view/admin/memberInfo");
    }

    private void columnBinding() {
        countColumn.setCellValueFactory(new PropertyValueFactory<>("entryNum"));
        entryLogColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cellData.getValue().getEntryTime())
        ));
    }

    private void loadEntryLog(Integer memberNum) {
        List<Timestamp> timestamps = entryLogRepository.findAllEntryLogs(memberNum);
        ObservableList<EntryLog> entryLogs = FXCollections.observableArrayList();

        int count = 1;
        for (Timestamp timestamp : timestamps) {
            EntryLog entryLog = new EntryLog();
            entryLog.setEntryTime(timestamp);
            entryLog.setEntryNum(count++);
            entryLogs.add(entryLog);
        }

        entryTable.setItems(entryLogs);
    }
}