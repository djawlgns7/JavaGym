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
    HBox[] lines = new HBox[20];
    List<Integer> nextBlockOrder = new ArrayList<>();
    int currentBlockNum = -1, nextBlockNum = -1, storedBlockNum = -1, centerXIndex, centerYIndex;
    int timer = 0;
    boolean isplaying = false, canStore = true;

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
            lines[i] = new HBox();
            lines[i].setSpacing(0);
            lines[i].setAlignment(Pos.CENTER);
            tetrisArea.getChildren().add(lines[i]);
            for(int j = 0; j < 10; j++){
                tetrisBlocks[i][j] = new Label(i + "," + j);
                tetrisBlocks[i][j].getStyleClass().add("empty");
                lines[i].getChildren().add(tetrisBlocks[i][j]);
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
                if (event.getCode() == KeyCode.DOWN) {
                    timer = 1000;
                    moveBlock(0, 1);
                    timer = 1000;
                } else if (event.getCode() == KeyCode.LEFT) {
                    moveBlock(-1, 0);
                } else if (event.getCode() == KeyCode.RIGHT) {
                    moveBlock(1, 0);
                }else if (event.getCode() == KeyCode.SPACE) {
                    timer = 1000;
                    while(!moveBlock(0, 1)){}
                    currentBlock = null;
                    timer = 0;
                }else if (event.getCode() == KeyCode.Z) {
                    timer += 100;
                    //rotateBlock(0);
                }else if (event.getCode() == KeyCode.X) {
                    timer += 100;
                    //rotateBlock(1);
                }else if (event.getCode() == KeyCode.SHIFT) {
                    if(canStore) {
                        timer = 1000;
                        storeBlock();
                    }
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
                        clearFullLines();
                        currentBlockNum = nextBlockNum;
                        setNextBlock();
                        setCurrentBlock(currentBlockNum);
                    }

                    if (moveBlock(0, 1)) {
                        currentBlock = null;
                        canStore = true;
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
                return;
            }
        }
    }

    public void clearFullLines(){
        List<Integer> fullLines = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            boolean isFull = true;
            for(int j = 0; j < 10; j++){
                if(tetrisBlocks[i][j].getStyleClass().contains("empty")){
                    isFull = false;
                }
            }
            if(isFull){
                fullLines.add(i);
            }
        }

        for(int i : fullLines){
            for(int j = 0; j < 10; j++){
                tetrisBlocks[i][j].getStyleClass().clear();
                tetrisBlocks[i][j].getStyleClass().add("empty");
            }

//            Platform.runLater(() -> {
                floatLine(i);
//            });
        }

    }

    public void floatLine(int height){
        HBox selectedLine = lines[height];
        Platform.runLater(() -> {
            tetrisArea.getChildren().clear();
        });

        for(int i = height; i < 19; i++){
            lines[i] = lines[i + 1];
        }

        lines[19] = selectedLine;

        for(int i = 19; i >= 0; i--){
            int finalI = i;
            Platform.runLater(() -> {
                tetrisArea.getChildren().add(lines[finalI]);
            });
        }

        setBlocksText();
    }

    public void setBlocksText(){
        for(int i = 0; i < 24; i++){
            for(int j = 0; j < 10; j++){
                tetrisBlocks[i][j].setText(i + "," + j);
            }
        }
    }

    public boolean moveBlock(int rightMove, int downMove){
        if(currentBlock == null){
            return false;
        }

        Label[] nextPosition = new Label[4];

        setCurrentColor(-1);
        for(int i = 0; i < 4; i++){
            String[] index = currentBlock[i].getText().split(",");
            int yIndex = Integer.parseInt(index[0]) - downMove;
            int xIndex = Integer.parseInt(index[1]) + rightMove;
            if(yIndex < 0 || xIndex < 0 || xIndex > 9 || !tetrisBlocks[yIndex][xIndex].getStyleClass().contains("empty")){
                setCurrentColor(currentBlockNum);
                if(downMove >= 1) {
                    check();
                }
                return true;
            }
            nextPosition[i] = tetrisBlocks[yIndex][xIndex];
        }

        for(int i = 0; i < 4; i++){
            currentBlock[i] = nextPosition[i];
        }

        centerXIndex += rightMove;
        centerYIndex -= downMove;
        setCurrentColor(currentBlockNum);

        return false;
    }

    // 0: 좌회전 1: 우회전
    public boolean rotateBlock(int direction){
        if(currentBlock == null){
            return false;
        }

        // oMino
        if(currentBlockNum == 1){
            return false;
        }

        Label[] nextPositions = new Label[4];

        // iMino
        if(currentBlockNum == 0){
            if(tetrisBlocks[centerYIndex][centerXIndex + 1].getStyleClass().contains("empty")){
                nextPositions[0] = tetrisBlocks[centerYIndex][centerXIndex - 1];
                nextPositions[1] = tetrisBlocks[centerYIndex][centerXIndex];
                nextPositions[2] = tetrisBlocks[centerYIndex][centerXIndex + 1];
                nextPositions[3] = tetrisBlocks[centerYIndex][centerXIndex + 2];
            }else{
                nextPositions[0] = tetrisBlocks[centerYIndex + 2][centerXIndex];
                nextPositions[1] = tetrisBlocks[centerYIndex + 1][centerXIndex];
                nextPositions[2] = tetrisBlocks[centerYIndex][centerXIndex];
                nextPositions[3] = tetrisBlocks[centerYIndex - 1][centerXIndex];
            }
        }

        // zMino
        if(currentBlockNum == 2){

        }

        // sMino
        if(currentBlockNum == 3){

        }

        // 좌회전
        if(direction == 0) {
            switch(currentBlockNum){
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
            }
        // 우회전
        }else{
            switch(currentBlockNum){
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
            }
        }

        return false;
    }

    public void storeBlock(){
        if(currentBlock == null){
            return;
        }

        if(storedBlockNum == -1){
            storedBlockNum = currentBlockNum;
            currentBlockNum = nextBlockNum;
            setNextBlock();
        }else{
            int tempNum = storedBlockNum;
            storedBlockNum = currentBlockNum;
            currentBlockNum = tempNum;
        }
        setCurrentColor(-1);
        setCurrentBlock(currentBlockNum);
        canStore = false;
        timer = 0;
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
                centerYIndex = 20;
                centerXIndex = 4;
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
                centerYIndex = 20;
                centerXIndex = 4;
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
                centerYIndex = 20;
                centerXIndex = 4;
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
                centerYIndex = 20;
                centerXIndex = 4;
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
                centerYIndex = 20;
                centerXIndex = 4;
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
                centerYIndex = 20;
                centerXIndex = 4;
                for (int i = 0; i < 4; i++) {
                    currentBlock[i].getStyleClass().remove("empty");
                    currentBlock[i].getStyleClass().add("lMino");
                }
                break;
            case 6:
                // T-Mino
                currentBlock[0] = tetrisBlocks[21][4];
                currentBlock[1] = tetrisBlocks[20][3];
                currentBlock[2] = tetrisBlocks[20][4];
                currentBlock[3] = tetrisBlocks[20][5];
                centerYIndex = 20;
                centerXIndex = 4;
                for (int i = 0; i < 4; i++) {
                    currentBlock[i].getStyleClass().remove("empty");
                    currentBlock[i].getStyleClass().add("tMino");
                }
                break;
        }
    }
}
