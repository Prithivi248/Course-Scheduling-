import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

class Room implements Comparable<Room>{
    String room_no;
    int capacity;
    Room(String room_no,int capacity){
        this.room_no=room_no;
        this.capacity=capacity;
    }
    public int compareTo(Room other){
        return Integer.compare(this.capacity,other.capacity);
    }
}

class Course{
    String course_no;
    int enrol;
    ArrayList<String> lst;
    Boolean canSchedule=true;
    Boolean isScheduled;
}

class ErrorHan{
    Course c;
    String err;
}

public class CourseSchModel {

    int TotRooms=4,TotTimes=6,TotCourses=30;
    static int MAX_COURSES=30;
    int NoOfCourses;

    int PgNP,UgP,UgNP;

    Course SchCourses[]=new Course[MAX_COURSES];
    Room ClassroomDB[]=new Room[TotRooms];
    String CourseDB[]=new String[TotCourses];
    String TimesslotDB[]=new String[TotTimes];

    int TimeTable[][]=new int[TotRooms][TotTimes];
    ArrayList<ErrorHan> Error = new ArrayList<ErrorHan>();

    public CourseSchModel(){
        try {
            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/coursesch","root","root");
            Statement stmt = c.createStatement();
            ResultSet r = stmt.executeQuery("select * from roomsdb");
            int i=0;
            while(r.next()){
                ClassroomDB[i] = new Room(r.getString(1), r.getInt(2));
                i++;
            }
            i=0;
            r = stmt.executeQuery("select * from coursesdb");
            while (r.next()) {
                CourseDB[i] = r.getString(1);
                i++;
            }
            r = stmt.executeQuery("select * from timesdb");
            i = 0;
            while (r.next()) {
                TimesslotDB[i] = r.getString(1);
                i++;
            }
            //check();
        } 
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /*private void check(){
        if(CourseDB.length > 30){
            ErrorHan temp = new ErrorHan();
            temp.err = "More than 30 courses not allowed!";
            Error.add(temp);
        }
        
        for(String i:CourseDB){
            if(i.charAt(0) != 'c' || i.charAt(1)!= 's'){
                ErrorHan temp = new ErrorHan();
                temp.c = i;
                temp.err = "Invalid course format";
                Error.add(temp);
            }
            else{
                continue;
            }
        }

        for()
    }*/

    private void sortCourses(){
        try {
            Course pg_pref[] = new Course[MAX_COURSES];
            Course ug_pref[] = new Course[MAX_COURSES];
            Course pg_nopref[] = new Course[MAX_COURSES];
            Course ug_nopref[] = new Course[MAX_COURSES];

            int pg_pref_i = 0;
            int ug_pref_i = 0;
            int pg_no_pref_i = 0;
            int ug_no_pref_i = 0;
            int j = 0, i = 0;

            for (i = 0; i < NoOfCourses; i++) {
                char temp = SchCourses[i].course_no.charAt(2);
                if (temp > '5') {
                    if (SchCourses[i].lst.isEmpty()) {
                        pg_nopref[pg_no_pref_i] = new Course();
                        pg_nopref[pg_no_pref_i++] = SchCourses[i];
                    } 
                    else {
                        pg_pref[pg_pref_i] = new Course();
                        pg_pref[pg_pref_i++] = SchCourses[i];
                    }
                }
                else {
                    if (SchCourses[i].lst.isEmpty()) {
                        ug_nopref[ug_no_pref_i] = new Course();
                        ug_nopref[ug_no_pref_i++] = SchCourses[i];
                    } 
                    else {
                        ug_pref[ug_pref_i] = new Course();
                        ug_pref[ug_pref_i++] = SchCourses[i];
                    }
                }
            }
            for (i = 0, j = 0; i < pg_pref_i; i++, j++)
                SchCourses[j] = pg_pref[i];

            UgP = j;
            for (i = 0; i < ug_pref_i; i++, j++)
                SchCourses[j] = ug_pref[i];

            PgNP = j;
            for (i = 0; i < pg_no_pref_i; i++, j++)
                SchCourses[j] = pg_nopref[i];

            UgNP = j;
            for (i = 0; i < ug_no_pref_i; i++, j++)
                SchCourses[j] = ug_nopref[i];
        } 
        catch (Exception e1) {
            System.out.println(e1.getMessage());
        }
    }

    private int getRoom(int capacity){
        for(int i=0;i<TotRooms;i++){
            if(ClassroomDB[i].capacity>=capacity){
                return i;
            }
        }
        return -1;
    }

    void Schedule(){
        Arrays.sort(ClassroomDB);
        sortCourses();

        /*for(int i=0;i<NoOfCourses;i++){
            System.out.println(SchCourses[i].course_no+" "+SchCourses[i].enrol);
            System.out.println(SchCourses[i].lst+" "+SchCourses[i].lst.isEmpty()+" "+SchCourses[i].lst.size());
        }*/

        ArrayList<String> CourseDBA=new ArrayList<>(Arrays.asList(CourseDB));
        ArrayList<String> TimesslotDBA=new ArrayList<>(Arrays.asList(TimesslotDB));

        for(int i=0;i<NoOfCourses;i++){
            if(SchCourses[i].enrol<=2 || SchCourses[i].enrol>250){
                SchCourses[i].canSchedule=false;
                ErrorHan temp = new ErrorHan();
                temp.c = SchCourses[i];
                temp.err = "Invalid Enrollment";
                Error.add(temp);
            }

            if(SchCourses[i].lst.size()>5){
                ErrorHan temp = new ErrorHan();
                temp.c = SchCourses[i];
                temp.err = "More number of preferences";
                Error.add(temp);
            }
            if(!CourseDBA.contains(SchCourses[i].course_no)){
                SchCourses[i].canSchedule = false;
                ErrorHan temp = new ErrorHan();
                temp.c = SchCourses[i];
                temp.err = "Invalid Course Number";
                Error.add(temp);
            }
        }

        for(int i=0;i<TotRooms;i++){
            for(int j=0;j<TotTimes;j++){
                TimeTable[i][j]=-1;
            }
        }

        for (int i = 0; i < UgP; i++) {
            if(!SchCourses[i].canSchedule)
                continue;

            int room = getRoom(SchCourses[i].enrol);
            if (room == -1){
                ErrorHan temp = new ErrorHan();
                temp.c = SchCourses[i];
                temp.err = "No room available";
                Error.add(temp);
                continue;
            }
            for (String s : SchCourses[i].lst) {
                int index = TimesslotDBA.indexOf(s);
                if (index == -1){
                    ErrorHan temp = new ErrorHan();
                    temp.c = SchCourses[i];
                    temp.err = "Invalid Time Preference "+s;
                    Error.add(temp);
                    continue;
                }
                if (TimeTable[room][index] == -1) {
                    TimeTable[room][index] = CourseDBA.indexOf(SchCourses[i].course_no);
                    break;
                }
                else{
                    ErrorHan temp = new ErrorHan();
                    temp.c = SchCourses[i];
                    temp.err = "Conflict with "+CourseDB[TimeTable[room][index]];
                    Error.add(temp);
                }
            }
        }

        for (int i = UgP; i < PgNP; i++) {
            if(!SchCourses[i].canSchedule)
                continue;

            int room = getRoom(SchCourses[i].enrol);
            if (room == -1){
                ErrorHan temp = new ErrorHan();
                temp.c = SchCourses[i];
                temp.err = "No room available";
                Error.add(temp);
                continue;
            }
            for (String s : SchCourses[i].lst) {
                int index = TimesslotDBA.indexOf(s);
                if (index == -1){
                    ErrorHan temp = new ErrorHan();
                    temp.c = SchCourses[i];
                    temp.err = "Invalid Time Preference "+s;
                    Error.add(temp);
                    continue;
                }
                if (TimeTable[room][index] == -1) {
                    TimeTable[room][index] = CourseDBA.indexOf(SchCourses[i].course_no);
                    break;
                }
                else{
                    ErrorHan temp = new ErrorHan();
                    temp.c = SchCourses[i];
                    temp.err = "Conflict with "+CourseDB[TimeTable[room][index]];
                    Error.add(temp);
                }
            }
        }

        for (int i = PgNP; i < UgNP; i++) {
            if(!SchCourses[i].canSchedule)
                continue;

            int room = getRoom(SchCourses[i].enrol);
            if (room == -1){
                ErrorHan temp = new ErrorHan();
                temp.c = SchCourses[i];
                temp.err = "No room available";
                Error.add(temp);
                continue;
            }
            for(int j=0;j<TotTimes;j++){
                if(TimeTable[room][j]==-1){
                    TimeTable[room][j] = CourseDBA.indexOf(SchCourses[i].course_no);
                    break;
                }
                else{
                    ErrorHan temp = new ErrorHan();
                    temp.c = SchCourses[i];
                    temp.err = "Confict with "+CourseDB[TimeTable[room][j]];
                    Error.add(temp);
                }
            }
        }

        for (int i = UgNP; i < NoOfCourses; i++) {
            if(!SchCourses[i].canSchedule)
                continue;

            int room = getRoom(SchCourses[i].enrol);
            if (room == -1){
                ErrorHan temp = new ErrorHan();
                temp.c = SchCourses[i];
                temp.err = "No room available";
                Error.add(temp);
                continue;
            }
            for(int j=0;j<TotTimes;j++){
                if(TimeTable[room][j]==-1){
                    TimeTable[room][j] = CourseDBA.indexOf(SchCourses[i].course_no);
                    break;
                }
                else{
                    ErrorHan temp = new ErrorHan();
                    temp.c = SchCourses[i];
                    temp.err = "Confict with "+CourseDB[TimeTable[room][j]];
                    Error.add(temp);
                }
            }
        }
    } 
}