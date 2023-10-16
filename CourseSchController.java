import java.util.ArrayList;
import java.util.Arrays;

import javafx.event.ActionEvent;

public class CourseSchController {

    CourseSchModel model;
    CourseSchView view;

    public CourseSchController(CourseSchModel model,CourseSchView view){
        this.model = model;
        this.view = view;
    }

    void submit(ActionEvent e){
        try {
            for (int i = 0; i < CourseSchView.lg; i++) {
            model.SchCourses[i] = new Course();
            model.SchCourses[i].course_no = view.course_text[i].getText();

            if (view.enrol_text[i].getText().isEmpty())
                model.SchCourses[i].enrol = 0;
            else
                model.SchCourses[i].enrol = Integer.parseInt(view.enrol_text[i].getText());
            
            if(view.pref_text[i].getText().isEmpty()){
                model.SchCourses[i].lst = new ArrayList<String>();
                continue;
            }

            String[] pref = view.pref_text[i].getText().split(",");
            model.SchCourses[i].lst = new ArrayList<String>(Arrays.asList(pref));
            }

            model.NoOfCourses = CourseSchView.lg;

            for (int i = 0; i < model.NoOfCourses; i++) {
                System.out.println(model.SchCourses[i].course_no + " " + model.SchCourses[i].enrol);
                System.out.println(model.SchCourses[i].lst);
            }
        } 
        catch (Exception e1) {
            System.out.println(e1.getMessage());
        }
    }

    public void schedule(ActionEvent e){
        model.Schedule();
        view.output(model);
    }
}

