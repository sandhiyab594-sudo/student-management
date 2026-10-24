import java.io.*;
import java.util.*;
public class StudentManagementApp {

    private static Scanner sc = new Scanner(System.in);
    private static StudentManagementSystem sms = new StudentManagementSystem();

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ", 1, 6);
            switch (choice) {
                case 1: addStudent(); break;
                case 2: editStudent(); break;
                case 3: removeStudent(); break;
                case 4: searchStudent(); break;
                case 5: displayAllStudents(); break;
                case 6: running = false; System.out.println("Exiting... Goodbye!"); break;
            }
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("\n===== Student Management System =====");
        System.out.println("1. Add Student");
        System.out.println("2. Edit Student");
        System.out.println("3. Remove Student");
        System.out.println("4. Search Student");
        System.out.println("5. Display All Students");
        System.out.println("6. Exit");
    }

    
    private static void addStudent() {
        System.out.println("\n--- Add New Student ---");
        int rollNumber = readUniqueRollNumber();
        String name = readNonEmptyString("Enter Name: ");
        int age = readInt("Enter Age: ", 1, 120);
        String grade = readGrade("Enter Grade (A+/A/B/C/D/E/F): ");
        String email = readEmail("Enter Email: ");
        String phone = readPhone("Enter Phone Number (10 digits): ");

        Student student = new Student(rollNumber, name, age, grade, email, phone);
        boolean added = sms.addStudent(student);
        System.out.println(added ? "Student added successfully!" : "Failed to add student (roll number exists).");
    }

    
    private static void editStudent() {
        System.out.println("\n--- Edit Student ---");
        int rollNumber = readInt("Enter Roll Number to edit: ", 0, Integer.MAX_VALUE);
        Student existing = sms.findByRollNumber(rollNumber);
        if (existing == null) {
            System.out.println("No student found with roll number " + rollNumber);
            return;
        }
        System.out.println("Current details: " + existing);
        System.out.println("Press Enter on any field to keep its current value.");

        System.out.print("New Name [" + existing.getName() + "]: ");
        String name = sc.nextLine().trim();

        System.out.print("New Age [" + existing.getAge() + "]: ");
        String ageStr = sc.nextLine().trim();
        Integer age = ageStr.isEmpty() ? null : safeParseInt(ageStr, existing.getAge());

        System.out.print("New Grade [" + existing.getGrade() + "]: ");
        String grade = sc.nextLine().trim();

        System.out.print("New Email [" + existing.getEmail() + "]: ");
        String email = sc.nextLine().trim();

        System.out.print("New Phone [" + existing.getPhoneNumber() + "]: ");
        String phone = sc.nextLine().trim();

        boolean updated = sms.updateStudent(rollNumber,
                name.isEmpty() ? null : name,
                age,
                grade.isEmpty() ? null : grade,
                email.isEmpty() ? null : email,
                phone.isEmpty() ? null : phone);
        System.out.println(updated ? "Student updated successfully!" : "Update failed.");
    }

    private static void removeStudent() {
        System.out.println("\n--- Remove Student ---");
        int rollNumber = readInt("Enter Roll Number to remove: ", 0, Integer.MAX_VALUE);
        boolean removed = sms.removeStudent(rollNumber);
        System.out.println(removed ? "Student removed successfully!" : "No student found with that roll number.");
    }

    private static void searchStudent() {
        System.out.println("\n--- Search Student ---");
        System.out.println("1. Search by Roll Number");
        System.out.println("2. Search by Name");
        int choice = readInt("Enter choice: ", 1, 2);

        if (choice == 1) {
            int rollNumber = readInt("Enter Roll Number: ", 0, Integer.MAX_VALUE);
            Student s = sms.findByRollNumber(rollNumber);
            System.out.println(s != null ? s : "No student found with roll number " + rollNumber);
        } else {
            String name = readNonEmptyString("Enter Name (or part of name): ");
            List<Student> results = sms.findByName(name);
            if (results.isEmpty()) {
                System.out.println("No matching students found.");
            } else {
                results.forEach(System.out::println);
            }
        }
    }

    
    private static void displayAllStudents() {
        System.out.println("\n--- All Students ---");
        List<Student> all = sms.getAllStudents();
        if (all.isEmpty()) {
            System.out.println("No students in the system.");
        } else {
            all.forEach(System.out::println);
        }
    }



    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    private static int readUniqueRollNumber() {
        while (true) {
            int rollNumber = readInt("Enter Roll Number: ", 1, Integer.MAX_VALUE);
            if (sms.findByRollNumber(rollNumber) == null) {
                return rollNumber;
            }
            System.out.println("Roll number already exists. Please enter a different one.");
        }
    }

    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    private static String readGrade(String prompt) {
        List<String> validGrades = Arrays.asList("A+", "A", "B", "C", "D", "E", "F");
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim().toUpperCase();
            if (validGrades.contains(input)) {
                return input;
            }
            System.out.println("Invalid grade. Valid options: " + validGrades);
        }
    }

    private static String readEmail(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
                return input;
            }
            System.out.println("Invalid email format. Example: name@example.com");
        }
    }

    private static String readPhone(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.matches("\\d{10}")) {
                return input;
            }
            System.out.println("Invalid phone number. Please enter exactly 10 digits.");
        }
    }

    private static int safeParseInt(String s, int fallback) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}


class Student {
    private int rollNumber;
    private String name;
    private int age;
    private String grade;
    private String email;
    private String phoneNumber;

    public Student(int rollNumber, String name, int age, String grade, String email, String phoneNumber) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.age = age;
        this.grade = grade;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    
    public int getRollNumber() { return rollNumber; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGrade() { return grade; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }

    
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String toFileString() {
        return rollNumber + "|" + name + "|" + age + "|" + grade + "|" + email + "|" + phoneNumber;
    }

    /** Rebuilds a Student object from a stored line. */
    public static Student fromFileString(String line) {
        String[] parts = line.split("\\|");
        return new Student(
                Integer.parseInt(parts[0]),
                parts[1],
                Integer.parseInt(parts[2]),
                parts[3],
                parts[4],
                parts[5]
        );
    }

    @Override
    public String toString() {
        return String.format("Roll No: %-6d Name: %-20s Age: %-4d Grade: %-5s Email: %-25s Phone: %s",
                rollNumber, name, age, grade, email, phoneNumber);
    }
}


class StudentManagementSystem {
    private List<Student> students;
    private static final String FILE_NAME = "students.txt";

    public StudentManagementSystem() {
        students = new ArrayList<>();
        loadFromFile();
    }

    
    public boolean addStudent(Student student) {
        if (findByRollNumber(student.getRollNumber()) != null) {
            return false; // roll number must be unique
        }
        students.add(student);
        saveToFile();
        return true;
    }

    public boolean removeStudent(int rollNumber) {
        Student s = findByRollNumber(rollNumber);
        if (s != null) {
            students.remove(s);
            saveToFile();
            return true;
        }
        return false;
    }

   
    public Student findByRollNumber(int rollNumber) {
        for (Student s : students) {
            if (s.getRollNumber() == rollNumber) {
                return s;
            }
        }
        return null;
    }

    public List<Student> findByName(String name) {
        List<Student> results = new ArrayList<>();
        for (Student s : students) {
            if (s.getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(s);
            }
        }
        return results;
    }

   
    public boolean updateStudent(int rollNumber, String name, Integer age, String grade,
                                  String email, String phoneNumber) {
        Student s = findByRollNumber(rollNumber);
        if (s == null) return false;

        if (name != null && !name.trim().isEmpty()) s.setName(name);
        if (age != null) s.setAge(age);
        if (grade != null && !grade.trim().isEmpty()) s.setGrade(grade);
        if (email != null && !email.trim().isEmpty()) s.setEmail(email);
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) s.setPhoneNumber(phoneNumber);

        saveToFile();
        return true;
    }

    public List<Student> getAllStudents() {
        return students;
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Student s : students) {
                writer.write(s.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    students.add(Student.fromFileString(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
}
