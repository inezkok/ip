import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class Storage {
    // Fields
    private String filePath;

    // Constructor
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    // Methods
    private void validateDate(String date) throws DukeException {
        try {
            LocalDate d = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new DukeException("Boop Beep OOPS! Please check that the date is in YYYY-MM-DD format.");
        }
    }

    private String formatDate(String date) {
        String dateString = date;
        String month = dateString.substring(0, 3);
        String day = dateString.substring(4, 6);
        String year = dateString.substring(7, 11);

        if (month.equals("Jan")) {
            month = "01";
        } else if (month.equals("Feb")) {
            month = "02";
        } else if (month.equals("Mar")) {
            month = "03";
        } else if (month.equals("Apr")) {
            month = "04";
        } else if (month.equals("May")) {
            month = "05";
        } else if (month.equals("Jun")) {
            month = "06";
        } else if (month.equals("Jul")) {
            month = "07";
        } else if (month.equals("Aug")) {
            month = "08";
        } else if (month.equals("Sep")) {
            month = "09";
        } else if (month.equals("Oct")) {
            month = "10";
        } else if (month.equals("Nov")) {
            month = "11";
        } else if (month.equals("Dec")) {
            month = "12";
        }

        return String.format("%s-%s-%s", year, month, day);
    }

    public ArrayList<Task> load() throws DukeException {
        ArrayList<Task> tasks = new ArrayList<>();

        try {
            File f = new File(filePath);
            if (!f.exists()) {
                return tasks;
            }

            Scanner s = new Scanner(f);
            int i = 0;
            while (s.hasNext()) {
                String line = s.nextLine();
                String taskType = line.substring(0, 3);
                String done = line.substring(4, 7);

                if (taskType.equals("[T]")) {
                    String description = line.substring(8);

                    ToDo toDo = new ToDo(description);
                    tasks.add(toDo);
                } else if (taskType.equals("[D]")) {
                    String[] fullLine = line.substring(8).split(":", 2);
                    String description = fullLine[0];
                    String date = fullLine[1];

                    String taskDescription = description.substring(0, description.length() - 3).trim();
                    String taskDate = formatDate(date.substring(0, date.length()).trim());
                    validateDate(taskDate);
                    LocalDate d = LocalDate.parse(taskDate);

                    Deadline deadline = new Deadline(taskDescription, d);
                    tasks.add(deadline);
                } else if (taskType.equals("[E]")) {
                    String[] fullLine = line.substring(8).split(":", 3);
                    String description = fullLine[0];
                    String start = fullLine[1];
                    String end = fullLine[2];

                    String taskDescription = description.substring(0, description.length() - 5).trim();
                    String taskStart = formatDate(start.substring(0, start.length() - 2).trim());
                    String taskEnd = formatDate(end.substring(0, end.length() - 1).trim());
                    validateDate(taskStart);
                    validateDate(taskEnd);
                    LocalDate d1 = LocalDate.parse(taskStart);
                    LocalDate d2 = LocalDate.parse(taskEnd);

                    Event event = new Event(taskDescription, d1, d2);
                    tasks.add(event);
                } else {
                    throw new DukeException("Boop Beep OOPS! File has unexpected task type :(");
                }

                if (done.equals("[X]")) {
                    tasks.get(i).markDone();
                } else if (done.equals("[ ]")) {

                } else {
                    throw new DukeException("Boop Beep OOPS! File has unexpected done value :(");
                }

                i++;
            }

        } catch (FileNotFoundException e) {
            throw new DukeException("Boop Beep OOPS! " + e.getMessage());
        }

        return tasks;
    }

    public void save(ArrayList<Task> list) throws DukeException {
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                try {
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                } catch (IOException e) {
                    System.out.println("Boop Beep OOPS! " + e.getMessage());
                }
            }

            FileWriter fw = new FileWriter(filePath);
            String stringList = "";
            for (int i = 0; i < list.size(); i++) {
                stringList = stringList + list.get(i) + System.lineSeparator();
            }
            fw.write(stringList);
            fw.close();
        } catch (IOException e) {
            throw new DukeException("Boop Beep OOPS! "+ e.getMessage());
        }
    }
}
