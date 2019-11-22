package com.moroz.logsearcher.view.fxview;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

import com.moroz.logsearcher.AppProperties;
import com.moroz.logsearcher.model.FileTree;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FXViewController extends FXViewImpl {

    private static final Logger log = LogManager.getLogger(FXViewController.class.getName());

    public FXViewController() {
        setFxViewController(this);
        folderIcon = new Image(getClass().getResourceAsStream("/img/folder_icon.png"));
        fileIcon = new Image(getClass().getResourceAsStream("/img/file_icon.png"));
        load = new Image(getClass().getResourceAsStream("/img/load.gif"));
        doneIcon = new Image(getClass().getResourceAsStream("/img/done_icon.png"));
    }

    private Image folderIcon;
    private Image fileIcon;
    private Image load;
    private Image doneIcon;

    private int searchTextLength;

    private int openFilePartCount;
    private int currentOpenFilePart;
    private Path currentOpenFile;
    private List<Pair<Integer, Integer>> foundTextIndexes;
    private int currentHighlightedText;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private TextField pathToDirField;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button openDirButton;

    @FXML
    private TextField filesTypeField;

    @FXML
    private TreeView<String> fileTreeView;

    @FXML
    private TextArea fileTextArea;

    @FXML
    private Button findButton;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button prevPageButton;

    @FXML
    private Button nextPageButton;

    @FXML
    private Label foundedCount;

    @FXML
    private ImageView processImageView;

    @FXML
    private void initialize() {

    }

    @FXML
    private void openDirButtonHandler(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
        File dir = directoryChooser.showDialog(stage);

        if(dir != null) {
            pathToDirField.setText(dir.getAbsolutePath());
            log.trace("openDirButtonHandler result dir: "+dir.getAbsolutePath());
        } else {
            log.trace("openDirButtonHandler result dir: null");
        }
    }

    @FXML
    private void findButtonHandler(ActionEvent event) {
        String pathToRootDir = pathToDirField.getText();
        log.info("finding files in "+pathToRootDir);

        if(pathToRootDir == null || pathToRootDir.trim().isEmpty()) {
            log.trace("findButtonHandler result: empty directory");
            showErrorMessage(AppProperties.getProperty("emptyDirectory"));
            return;
        }

        Path rootDir = Paths.get(pathToRootDir);
        if(!rootDir.toFile().exists()) {
            log.trace("findButtonHandler result: invalid directory");
            showErrorMessage(AppProperties.getProperty("invalidDirectory"));
            return;
        }

        String searchText = searchTextField.getText();
        if(searchText == null || searchText.trim().isEmpty()) {
            log.trace("findButtonHandler result: no search text");
            showErrorMessage(AppProperties.getProperty("noSearchText"));
            return;
        }

        String filesType = filesTypeField.getText();
        if(filesType == null || filesType.trim().isEmpty()) {
            log.trace("findButtonHandler set filesType to \".log\"");
            filesType = "log";
        }

        fileTextArea.setText("");
        openFilePartCount = -1;
        currentOpenFilePart = -1;
        currentOpenFile = null;
        foundTextIndexes = null;
        currentHighlightedText = -1;
        searchTextLength = searchTextField.getText().length();
        fileTreeView.getSelectionModel().clearSelection();

        fileTreeView.setRoot(new TreeItem<>());
        log.trace("call MvcController method \"find\" with rootDir: "+rootDir+" searchText: "+searchText+
                        " filesType: "+filesType);
        getMvcController().find(rootDir, searchText, filesType);
        processImageView.setImage(load);
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(AppProperties.getProperty("error"));
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    @FXML
    private void fileTreeViewClickHandler(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        // Accept clicks only on node cells, and not on empty spaces of the TreeView
        if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
            TreeItem<String> item = fileTreeView.getSelectionModel().getSelectedItem();
            if(item != null && item.isLeaf()) {
                log.trace("click on node: "+item.getValue());

                String path = item.getValue();
                TreeItem<String> itemParent = item.getParent();
                while (itemParent.getValue() != null) {
                    path = itemParent.getValue()+File.separator+path;
                    itemParent = itemParent.getParent();
                }
                Path openedFile = Paths.get(path);
                currentOpenFile = openedFile;

                log.trace("call MvcController method \"openFile\" with: "+openedFile);
                getMvcController().openFile(openedFile);
            }
        }
    }

    @FXML
    private void nextPageButtonHandler(ActionEvent event) {
        openFilePart(++currentOpenFilePart);
    }

    @FXML
    private void prevPageButtonHandler(ActionEvent event) {
        openFilePart(--currentOpenFilePart);
    }

    private void openFilePart(int filePart) {
        currentOpenFilePart = filePart;

        prevPageButton.setDisable(false);
        nextPageButton.setDisable(false);

        if(currentOpenFilePart == (openFilePartCount-1)) {
            nextPageButton.setDisable(true);
        }

        if(currentOpenFilePart == 0) {
            prevPageButton.setDisable(true);
        }

        log.trace("call MvcController method \"getOpenFilePart\" with currentOpenFile: "+currentOpenFile+
                    " currentOpenFilePart: "+currentOpenFilePart);
        fileTextArea.setText(getMvcController().getOpenFilePart(currentOpenFile, currentOpenFilePart));
    }

    @FXML
    private void nextButtonHandler(ActionEvent event) {
        ++currentHighlightedText;
        if (currentHighlightedText == foundTextIndexes.size()) {
            currentHighlightedText = 0;
        }

        setHighlight(currentHighlightedText);
    }

    @FXML
    private void previousButtonHandler(ActionEvent event) {
        --currentHighlightedText;
        if (currentHighlightedText == -1) {
            currentHighlightedText = foundTextIndexes.size()-1;
        }

        setHighlight(currentHighlightedText);
    }

    public void openFile(String text, int filePartCount, List<Pair<Integer, Integer> > foundTextIndexes) {
        log.trace("open file with filePartCount: "+filePartCount+
                " foundTextIndexesCount: "+foundTextIndexes.size());

        fileTextArea.setText(text);
        openFilePartCount = filePartCount;
        currentOpenFilePart = 0;
        currentHighlightedText = 0;
        this.foundTextIndexes = foundTextIndexes;
        foundedCount.setText(String.valueOf(foundTextIndexes.size()));

        setHighlight(currentHighlightedText);

        prevPageButton.setDisable(true);
        if(filePartCount == 1) {
            nextPageButton.setDisable(true);
        } else {
            nextPageButton.setDisable(false);
        }
    }

    private void setHighlight(int highlightIndex) {
        int highlightTextPart = foundTextIndexes.get(highlightIndex).getKey();
        int highlightStartPosition = foundTextIndexes.get(highlightIndex).getValue();

        log.trace("set highlight with highlightIndex: "+highlightIndex+" highlightTextPart: "+highlightTextPart
        +" highlightStartPosition: "+highlightStartPosition);

        if(highlightTextPart != currentOpenFilePart) {
            openFilePart(highlightTextPart);
        }

        fileTextArea.selectRange(highlightStartPosition,
                highlightStartPosition+searchTextLength);
    }

    public void addFileTree(FileTree fileTree) {
        TreeItem<String> root = fileTreeView.getRoot();
        addNodeToTreeView(root, fileTree.getRoot());
        processImageView.setImage(doneIcon);
    }

    private void addNodeToTreeView(TreeItem<String> root, FileTree.Node node) {
        log.trace("adding node \""+node.getName()+"\" to tree view");

        TreeItem<String> addedNode = new TreeItem<>(node.getName(), new ImageView(fileIcon));
        root.getChildren().add(addedNode);

        if(!node.isLeaf()) {
            addedNode.setGraphic(new ImageView(folderIcon));
            root = addedNode;
            List<FileTree.Node> childNodes = new CopyOnWriteArrayList<>(node.getChilds());
            for (FileTree.Node childNode : childNodes) {
                addNodeToTreeView(root, childNode);
            }
        }
    }
}
