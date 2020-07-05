package ch.micheljung.fxwindow;

import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/** Not intended to be used by the library user. */
public class WindowController implements FxStage {

  private static final PseudoClass MAXIMIZED_PSEUDO_CLASS = PseudoClass.getPseudoClass("maximized");

  public Region controlBox;
  public Pane windowRoot;
  public Button minimizeButton;
  public Button maximizeButton;
  public Button restoreButton;
  public Button closeButton;
  public AnchorPane windowContent;

  // Those are not part of the FX window, but set by the user
  private Region titleBar;
  private Node leftMenu;
  private Node rightMenu;
  private Node icon;

  public void initialize() {
    minimizeButton.managedProperty().bind(minimizeButton.visibleProperty());
    maximizeButton.managedProperty().bind(maximizeButton.visibleProperty());
    restoreButton.managedProperty().bind(restoreButton.visibleProperty());
    closeButton.managedProperty().bind(closeButton.visibleProperty());

    restoreButton.visibleProperty().bind(maximizeButton.visibleProperty().not());

    windowRoot.sceneProperty().addListener(sceneProperty ->
      windowRoot.getScene().windowProperty().addListener(windowProperty -> {
        maximizeButton.visibleProperty().bind(getStage().maximizedProperty().not());
        getStage().maximizedProperty().addListener(maximizedProperty -> getStage().getScene().getRoot().pseudoClassStateChanged(MAXIMIZED_PSEUDO_CLASS, getStage().isMaximized()));
        getStage().iconifiedProperty().addListener((observable, oldValue, newValue) -> {
          if (!newValue) {
            // Workaround for minimize button not losing its "pressed" status
            maximizeButton.arm();
            maximizeButton.disarm();
          }
        });
      }));
  }

  public void onMinimizeButtonClicked() {
    getStage().setIconified(true);
  }

  public void onMaximiseButtonClicked() {
    getStage().setMaximized(true);
  }

  public void onRestoreButtonClicked() {
    getStage().setMaximized(false);
  }

  public void onCloseButtonClicked() {
    getStage().close();
  }

  public Stage getStage() {
    return (Stage) windowRoot.getScene().getWindow();
  }

  int getResizeBorderThickness() {
    return 8;
  }

  @Override
  public FxStage setContent(Region node) {
    windowContent.getChildren().setAll(node);

    getStage().minWidthProperty().bind(Bindings.createDoubleBinding(() -> Math.max(node.getMinWidth(), 10), node.minWidthProperty()));
    getStage().minHeightProperty().bind(Bindings.createDoubleBinding(() -> Math.max(node.getMinHeight(), 10), node.minHeightProperty()));

    AnchorPane.setBottomAnchor(node, 0d);
    AnchorPane.setLeftAnchor(node, 0d);
    AnchorPane.setRightAnchor(node, 0d);
    AnchorPane.setTopAnchor(node, 0d);
    return this;
  }

  @Override
  public FxStage setTitleBar(Region titleBar) {
    this.titleBar = titleBar;
    return this;
  }

  @Override
  public FxStage setLeftMenu(Node leftMenu) {
    this.leftMenu = leftMenu;
    return this;
  }

  @Override
  public FxStage setRightMenu(Node rightMenu) {
    this.rightMenu = rightMenu;
    return this;
  }

  @Override
  public FxStage setIcon(Node icon) {
    this.icon = leftMenu;
    return this;
  }

  @Override
  public Node getLeftMenu() {
    return leftMenu;
  }

  @Override
  public Node getRightMenu() {
    return rightMenu;
  }

  @Override
  public Region getTitleBar() {
    return titleBar;
  }

  @Override
  public Node getIcon() {
    return icon;
  }

  public void setAllowMinimize(boolean allowMinimize) {
    if (!allowMinimize) {
      minimizeButton.setDisable(false);
    }
  }
}