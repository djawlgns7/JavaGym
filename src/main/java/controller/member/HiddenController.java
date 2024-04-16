package controller.member;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import static util.DialogUtil.showDialog;

public class HiddenController implements Initializable {
    @FXML
    private VBox tetrisArea;

    Label[][] tetrisBlocks = new Label[24][10];
    Label[] currentBlock = null;
    List<Integer> nextBlockOrder = new ArrayList<>();
    int currentBlockNum = -1, nextBlockNum = -1, currentBlockDir = 0;
    int timer = 0;
    boolean isplaying = false;

    Thread play;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        makeTetrisArea();
        setScene();
        setNextBlock();
        currentBlockNum = nextBlockNum;
        setNextBlock();
        isplaying = true;

        run();
    }

    @FXML
    public void makeTetrisArea(){
        for (int i = 20; i < 24; i++) {
            for (int j = 0; j < 10; j++) {
                tetrisBlocks[i][j] = new Label(i + "," + j);
                tetrisBlocks[i][j].getStyleClass().add("empty");
            }
        }

        for(int i = 19; i >= 0; i--){
            HBox newLine = new HBox();
            newLine.setSpacing(0);
            newLine.setAlignment(Pos.CENTER);
            tetrisArea.getChildren().add(newLine);
            for(int j = 0; j < 10; j++){
                tetrisBlocks[i][j] = new Label(i + "," + j);
                tetrisBlocks[i][j].getStyleClass().add("empty");
                newLine.getChildren().add(tetrisBlocks[i][j]);
                int finalI = i;
                int finalJ = j;
                tetrisBlocks[i][j].setOnMouseClicked(mouseEvent -> {System.out.println(tetrisBlocks[finalI][finalJ].getText());});
            }
        }
    }

    public void setScene() {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Scene scene = tetrisArea.getScene();
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.UP) {

                } else if (event.getCode() == KeyCode.DOWN) {

                } else if (event.getCode() == KeyCode.LEFT) {

                } else if (event.getCode() == KeyCode.RIGHT) {

                }else if (event.getCode() == KeyCode.SPACE) {
                    while(!moveDown()){}
                    currentBlock = null;
                    timer = 0;
                }else if (event.getCode() == KeyCode.Z) {

                }else if (event.getCode() == KeyCode.X) {

                }else if (event.getCode() == KeyCode.SHIFT) {

                }
            });

        });
        thread.start();
    }

    public void run(){
        play = new Thread(() -> {
            while(isplaying) {
                if (timer == 0) {
                    if (currentBlock == null) {

                        currentBlockNum = nextBlockNum;
                        setNextBlock();
                        setCurrentBlock(currentBlockNum);
                    }

                    if (moveDown()) {
                        currentBlock = null;
                    }
                }

                timer = (timer + 1) % 100;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        play.start();
    }

    public void check(){
        for(int i = 0; i < 4; i++){
            String[] index = currentBlock[i].getText().split(",");
            int yIndex = Integer.parseInt(index[0]);
            if(yIndex >= 20){
                Platform.runLater(() -> {
                    if(isplaying) {
                        showDialog("패배하였습니다!");
                    }
                    isplaying = false;
                });
            }
        }
    }

    public boolean moveDown(){
        if(currentBlock == null){
            return false;
        }

        Label[] nextPosition = new Label[4];

        setCurrentColor(-1);
        for(int i = 0; i < 4; i++){
            String[] index = currentBlock[i].getText().split(",");
            int yIndex = Integer.parseInt(index[0]);
            int xIndex = Integer.parseInt(index[1]);
            if(--yIndex < 0 || !tetrisBlocks[yIndex][xIndex].getStyleClass().contains("empty")){
                setCurrentColor(currentBlockNum);
                check();
                return true;
            }
            nextPosition[i] = tetrisBlocks[yIndex][xIndex];
        }

        for(int i = 0; i < 4; i++){
            currentBlock[i] = nextPosition[i];
        }

        setCurrentColor(currentBlockNum);

        return false;
    }

    public void setNextBlock(){
        if(nextBlockOrder.isEmpty()){
            int i = 7;
            while(i-- > 0){
                nextBlockOrder.add(i);
            }
        }
        Random random = new Random();
        int next = random.nextInt(nextBlockOrder.size());
        nextBlockNum = nextBlockOrder.get(next);
        nextBlockOrder.remove(next);
    }

    public void setCurrentColor(int color){
        String className = getClassName(color);

        for(int i = 0; i < 4; i++){
            currentBlock[i].getStyleClass().clear();
            currentBlock[i].getStyleClass().add(className);
        }
    }

    public String getClassName(int classNum){
        switch (classNum) {
            case 0:
                return "iMino";
            case 1:
                return "oMino";
            case 2:
                return "zMino";
            case 3:
                return "sMino";
            case 4:
                return "jMino";
            case 5:
                return "lMino";
            case 6:
                return "tMino";
            default:
                return "empty";
        }
    }

    public void setCurrentBlock(int index){
        currentBlock = new Label[4];
        switch(index){
            case 0:
                // I-Mino
                currentBlock[0] = tetrisBlocks[20][3];
                currentBlock[1] = tetrisBlocks[20][4];
                currentBlock[2] = tetrisBlocks[20][5];
                currentBlock[3] = tetrisBlocks[20][6];
                for (int i = 0; i < 4; i++) {
                    currentBlock[i].getStyleClass().remove("empty");
                    currentBlock[i].getStyleClass().add("iMino");
                }
                break;
            case 1:
                // O-Mino
                currentBlock[0] = tetrisBlocks[21][4];
                currentBlock[1] = tetrisBlocks[21][5];
                currentBlock[2] = tetrisBlocks[20][4];
                currentBlock[3] = tetrisBlocks[20][5];
                for (int i = 0; i < 4; i++) {
                    currentBlock[i].getStyleClass().remove("empty");
                    currentBlock[i].getStyleClass().add("oMino");
                }
                break;
            case 2:
                // Z-Mino
                currentBlock[0] = tetrisBlocks[21][3];
                currentBlock[1] = tetrisBlocks[21][4];
                currentBlock[2] = tetrisBlocks[20][4];
                currentBlock[3] = tetrisBlocks[20][5];
                for (int i = 0; i < 4; i++) {
                    currentBlock[i].getStyleClass().remove("empty");
                    currentBlock[i].getStyleClass().add("zMino");
                }
                break;
            case 3:
                // S-Mino
                currentBlock[0] = tetrisBlocks[20][3];
                currentBlock[1] = tetrisBlocks[20][4];
                currentBlock[2] = tetrisBlocks[21][4];
                currentBlock[3] = tetrisBlocks[21][5];
                for (int i = 0; i < 4; i++) {
                    currentBlock[i].getStyleClass().remove("empty");
                    currentBlock[i].getStyleClass().add("sMino");
                }
                break;
            case 4:
                // J-Mino
                currentBlock[0] = tetrisBlocks[21][5];
                currentBlock[1] = tetrisBlocks[20][3];
                currentBlock[2] = tetrisBlocks[20][4];
                currentBlock[3] = tetrisBlocks[20][5];
                for (int i = 0; i < 4; i++) {
                    currentBlock[i].getStyleClass().remove("empty");
                    currentBlock[i].getStyleClass().add("jMino");
                }
                break;
            case 5:
                // L-Mino
                currentBlock[0] = tetrisBlocks[21][3];
                currentBlock[1] = tetrisBlocks[20][3];
                currentBlock[2] = tetrisBlocks[20][4];
                currentBlock[3] = tetrisBlocks[20][5];
                for (int i = 0; i < 4; i++) {
                    currentBlock[i].getStyleClass().remove("empty");
                    currentBlock[i].getStyleClass().add("lMino");
                }
                break;
            case 6:
                // T-Mino
                currentBlock[0] = tetrisBlocks[20][4];
                currentBlock[1] = tetrisBlocks[21][3];
                currentBlock[2] = tetrisBlocks[21][4];
                currentBlock[3] = tetrisBlocks[21][5];
                for (int i = 0; i < 4; i++) {
                    currentBlock[i].getStyleClass().remove("empty");
                    currentBlock[i].getStyleClass().add("tMino");
                }
                break;
        }
    }
}
