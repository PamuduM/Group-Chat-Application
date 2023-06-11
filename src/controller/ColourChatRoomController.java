package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;



public class ColourChatRoomController extends Thread {
    //    public TextArea txtTextArea;
    public TextField txtTextMsg;
    public ImageView imgSendMsg;
    public AnchorPane apnChatForm;
    public Rectangle rectangle;
    public Label txtUserName;
    public Pane pnePopUp;
    public TextField txtNicName;
    public VBox vboxChat;

    BufferedReader reader;
    PrintWriter writer;
    Socket socket;

    private FileChooser fileChooser;
    private File filePath;




    public void initialize() {


        try {
            socket = new Socket("localhost", 5000);

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {


                String msg = reader.readLine();
                String[] tokens = msg.split(" ");
                String cmd = tokens[0];

//                txtTextArea.appendText(cmd+"\n");
                StringBuilder fullMsg = new StringBuilder();
                for (int i = 1; i < tokens.length; i++) {
                    fullMsg.append(tokens[i]);
                }


                String[] msgToAr = msg.split(" ");
                String st = "";
                for (int i = 0; i < msgToAr.length - 1; i++) {
                    st += msgToAr[i + 1] + " ";
                }
//======================================================================



                Text text = new Text(st);
                String firstChars = "";
                if (st.length() > 3) {
                    firstChars = st.substring(0, 3);

                }


                if (firstChars.equalsIgnoreCase("img")) {
                    //for the Images

                    st = st.substring(3, st.length() - 1);



                    File file = new File(st);
                    Image image = new Image(file.toURI().toString());

                    ImageView imageView = new ImageView(image);

                    imageView.setFitHeight(200);
                    imageView.setFitWidth(300);


                    HBox hBox = new HBox(10);
                    hBox.setAlignment(Pos.BOTTOM_RIGHT);


                    if (!cmd.equalsIgnoreCase(txtUserName.getText())) {

                        vboxChat.setAlignment(Pos.TOP_LEFT);
                        hBox.setAlignment(Pos.CENTER_LEFT);


                        Text text1=new Text("  "+cmd+" :");
                        hBox.getChildren().add(text1);
                        hBox.getChildren().add(imageView);

                    } else {
                        hBox.setAlignment(Pos.BOTTOM_RIGHT);
                        hBox.getChildren().add(imageView);
                        Text text1=new Text(": Me ");
                        hBox.getChildren().add(text1);

                    }

                    Platform.runLater(() -> vboxChat.getChildren().addAll(hBox));


                } else {
                    //For the Text
                    text.setFill(Color.WHITE);
                    text.getStyleClass().add("message");
                    TextFlow tempFlow = new TextFlow();

                    if (!cmd.equalsIgnoreCase(txtUserName.getText() + ":")) {
                        Text txtName = new Text(cmd + "\n");
                        txtName.getStyleClass().add("txtName");
                        tempFlow.getChildren().add(txtName);
                    }

                    tempFlow.getChildren().add(text);
                    tempFlow.setMaxWidth(200); //200

                    TextFlow flow = new TextFlow(tempFlow);

                    HBox hBox = new HBox(12); //12

                    //=================================================


                    if (!cmd.equalsIgnoreCase(txtUserName.getText() + ":")) {

                        tempFlow.getStyleClass().add("tempFlowFlipped");
                        flow.getStyleClass().add("textFlowFlipped");
                        vboxChat.setAlignment(Pos.TOP_LEFT);
                        hBox.setAlignment(Pos.CENTER_LEFT);
                        hBox.getChildren().add(flow);

                    } else {
                        text.setFill(Color.WHITE);
                        tempFlow.getStyleClass().add("tempFlow");
                        flow.getStyleClass().add("textFlow");
                        hBox.setAlignment(Pos.BOTTOM_RIGHT);
                        hBox.getChildren().add(flow);
                    }
                    hBox.getStyleClass().add("hbox");
                    Platform.runLater(() -> vboxChat.getChildren().addAll(hBox));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void imgSendMsgOnAction(MouseEvent mouseEvent) throws IOException {
        send();
    }

    private void go(){
        txtUserName.setText(txtNicName.getText().trim());
        pnePopUp.setVisible(false);
        apnChatForm.setVisible(true);
    }
    private void send(){
        String msg = txtTextMsg.getText();
        writer.println(txtUserName.getText() + ": " + txtTextMsg.getText());

        txtTextMsg.clear();
        if (msg.equalsIgnoreCase("BYE") || (msg.equalsIgnoreCase("logout"))) {
            Stage stage = (Stage) txtTextMsg.getScene().getWindow();
            stage.close();
        }
    }

    public void btnGoOnAction(ActionEvent actionEvent) {
        go();
    }

    public void AddClientOnAction(MouseEvent mouseEvent) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/ColourChatForm.fxml"))));
        stage.setResizable(false);
        stage.getIcons().add(new Image("assets/messenger.gif"));
        stage.setTitle("Messenger");
        stage.centerOnScreen();
        stage.show();

    }

    public void chooseImageButton(MouseEvent mouseEvent) {

        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        this.filePath = fileChooser.showOpenDialog(stage);
        writer.println(txtUserName.getText() + " " + "img" + filePath.getPath());

    }

    public void msgEnterKeyOnAction(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            send();
        }
    }

    public void userNameEnterKeyOnAction(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            go();
        }



    }
}
