public class App{
    public static void main(String[] args){
        CourseSchModel model = new CourseSchModel();
        CourseSchView view = new CourseSchView();
        CourseSchController controller = new CourseSchController(model, view);

        CourseSchView.controller=controller;
        view.startView();
    }
}