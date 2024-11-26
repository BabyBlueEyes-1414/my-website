
public class studMain {

    public static void main(String[] args) {
        studView view = new studView();
        view.setVisible(true);
        new studController(view);
    }
}
