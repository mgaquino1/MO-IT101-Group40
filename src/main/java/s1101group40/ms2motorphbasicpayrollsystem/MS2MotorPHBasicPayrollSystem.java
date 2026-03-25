package s1101group40.ms2motorphbasicpayrollsystem;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;

public class MS2MotorPHBasicPayrollSystem {

    static Scanner sc = new Scanner(System.in);
    static Map<String, String[]> employeeData = new TreeMap<>();
    static Map<String, Double> employeeHourlyRate = new TreeMap<>();
    static Map<String, List<String[]>> attendanceData = new HashMap<>();

    static List<double[]> sssTable = new ArrayList<>();

    public static void main(String[] args) {
        String employeeCSV = "src/main/resources/MotorPH_EmployeeData-EmployeeDetails.csv";
        String attendanceCSV = "src/main/resources/MotorPH_EmployeeData-AttendanceRecord.csv";
        String sssCSV = "src/main/resources/Computation_SSSContributionN.csv";

        loadSSS(sssCSV);
        loadEmployees(employeeCSV);
        loadAttendance(attendanceCSV);

        while (true) {
            System.out.println("\n=== Payroll System Login ===");
            System.out.print("Enter username: ");
            String username = sc.nextLine();

            System.out.print("Enter password: ");
            String password = sc.nextLine();

            if (username.equals("payroll_staff") && password.equals("12345"))
                handlePayrollStaffMenu();
            else if (username.equals("employee") && password.equals("12345"))
                handleEmployeeMenu();
            else
                System.out.println("Invalid login. Try again.");
        }
    }

    static void loadSSS(String csvPath) {
        try (Scanner file = new Scanner(new File(csvPath))) {
            file.nextLine();
            while (file.hasNextLine()) {
                String line = file.nextLine();
                String[] parts = line.split(",");
                // parts[0] = lower salary column in the CSV
                // parts[1] = upper salary column in the CSV
                // parts[2] = SSS contribution amount column in the CSV
                double lower = parseCSVNumber(parts[0]);
                double upper = parseCSVNumber(parts[1]);
                double contribution = parseCSVNumber(parts[2]);
                sssTable.add(new double[]{lower, upper, contribution});
            }
        } catch (Exception e) {
            System.out.println("Error loading SSS table. Make sure the CSV exists at: " + csvPath);
            e.printStackTrace();
            System.exit(1);
        }
    }

    static double parseCSVNumber(String s) {
        s = s.replace("\"", "").replace(",", "");
        return Double.parseDouble(s.trim());
    }

    static void loadEmployees(String csvPath) {
        try (Scanner file = new Scanner(new File(csvPath))) {
            file.nextLine(); 
            while (file.hasNextLine()) {
                String line = file.nextLine();
                String[] fields = parseCSVLine(line);
                // fields[0] = Employee Number column in the CSV
                String empNum = fields[0];
                employeeData.put(empNum, fields);

                // fields[18] = Hourly Rate column in the CSV
                employeeHourlyRate.put(empNum, Double.parseDouble(fields[18].replace(",", "")));
            }
        } catch (Exception e) {
            System.out.println("Error loading employee data. Make sure the CSV exists at: " + csvPath);
            e.printStackTrace();
            System.exit(1);
        }
    }

    static void loadAttendance(String csvPath) {
        try (Scanner file = new Scanner(new File(csvPath))) {
            file.nextLine();
            while (file.hasNextLine()) {
                String line = file.nextLine();
                String[] fields = parseCSVLine(line);
                attendanceData
                        // fields[0] = Employee Number column in the CSV
                        .computeIfAbsent(fields[0], k -> new ArrayList<>())
                        // fields[3] = Date column in the CSV
                        // fields[4] = Time In column in the CSV
                        // fields[5] = Time Out column in the CSV
                        .add(new String[]{fields[3], fields[4], fields[5]});
            }
        } catch (Exception e) {
            System.out.println("Error loading attendance data. Make sure the CSV exists at: " + csvPath);
            e.printStackTrace();
            System.exit(1);
        }
    }

    static String[] parseCSVLine(String line) {
            String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            for (int i = 0; i < tokens.length; i++) {
                tokens[i] = tokens[i].replace("\"", "").trim();
            }
            return tokens;
     }

    static double getSSSContribution(double monthlyGross) {
        for (double[] r : sssTable) {
            // r[0] = lower, r[1] = upper, r[2] = contribution
            if (monthlyGross >= r[0] && monthlyGross <= r[1])
                return r[2];
        }
        return 0;
    }

    static double getPhilHealthContribution(double monthlyGross) {
        double premium = monthlyGross * 0.03;
        if (premium > 1800) premium = 1800;
        return premium / 2;
    }

    static double getPagIBIGContribution(double monthlyGross) {
        if (monthlyGross < 1000) {
            return 0;
        } else if (monthlyGross <= 1500) {
            return Math.min(monthlyGross * 0.01, 100);
        } else {
            return Math.min(monthlyGross * 0.02, 100);
        }
    }

    static double getWithholdingTax(double monthlyGross, double totalDeductions) {
        double taxableIncome = monthlyGross - totalDeductions;

        if (taxableIncome <= 20832) {
            return 0;
        } else if (taxableIncome <= 33333) {
            return (taxableIncome - 20833) * 0.20;
        } else if (taxableIncome <= 66667) {
            return 2500 + (taxableIncome - 33333) * 0.25;
        } else if (taxableIncome <= 166667) {
            return 10833 + (taxableIncome - 66667) * 0.30;
        } else if (taxableIncome <= 666667) {
            return 40833.33 + (taxableIncome - 166667) * 0.32;
        } else {
            return 200833.33 + (taxableIncome - 666667) * 0.35;
        }
    }

    static void handlePayrollStaffMenu() {
        while (true) {
            System.out.println("\nPayroll Processing Menu");
            System.out.println("1. One employee");
            System.out.println("2. All employees");
            System.out.println("3. Logout");

            System.out.print("Enter option: ");
            String opt = sc.nextLine();

            if (opt.equals("1")) {
                System.out.print("Enter employee #: ");
                processEmployeePayroll(sc.nextLine());
            } else if (opt.equals("2")) {
                employeeData.keySet().forEach(MS2MotorPHBasicPayrollSystem::processEmployeePayroll);
            } else if (opt.equals("3")) break;
            else System.out.println("Invalid option.");
        }
    }

    static void handleEmployeeMenu() {
        while (true) {
            System.out.println("\nEmployee Menu");
            System.out.println("1. Check My Profile");
            System.out.println("2. Logout");

            System.out.print("Enter option: ");
            String opt = sc.nextLine();

            if (opt.equals("1")) {
                System.out.print("Enter employee #: ");
                displayEmployeeProfile(sc.nextLine());
                break;
            } else if (opt.equals("2")) break;
            else System.out.println("Invalid option.");
        }
    }

    static void displayEmployeeProfile(String empNum) {
        if (!employeeData.containsKey(empNum)) {
            System.out.println("Employee not found.");
            return;
        }
        // emp[1] = Last Name column in the CSV
        // emp[2] = First Name column in the CSV
        // emp[3] = Birthday column in the CSV
        String[] emp = employeeData.get(empNum);
        System.out.println("\nEmployee #: " + empNum);
        System.out.println("Name: " + emp[2] + " " + emp[1]);
        System.out.println("Birthday: " + emp[3]);
    }

    static void processEmployeePayroll(String empNum) {
        if (!employeeData.containsKey(empNum)) {
            System.out.println("Employee not found.");
            return;
        }

        String[] emp = employeeData.get(empNum);
        double hourlyRate = employeeHourlyRate.get(empNum);

        List<String[]> records = attendanceData.getOrDefault(empNum, new ArrayList<>());
        Map<String, Double> cutoffHours = new LinkedHashMap<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        for (String[] rec : records) {
            // rec[0] = Date column from attendance CSV
            LocalDate date = LocalDate.parse(rec[0], df);
            String cutoff = date.getDayOfMonth() <= 15
                    // Determines payroll cutoff period: 1–15 or 16–end of month
                    ? date.getMonthValue() + "/1-" + date.getMonthValue() + "/15"
                    : date.getMonthValue() + "/16-" + date.getMonthValue() + "/" + date.lengthOfMonth();

            // rec[1] = Time In column from attendance CSV
            // rec[2] = Time Out column from attendance CSV
            LocalTime in = parseTime(rec[1]); 
            LocalTime out = parseTime(rec[2]);

            LocalTime standardIn = LocalTime.of(8, 0); // Standard start time
            LocalTime graceIn = LocalTime.of(8, 10); // 10-minute grace period for late arrivals

            if (in.isBefore(graceIn)) {
                in = standardIn;
            }

            if (out.isAfter(LocalTime.of(17, 0))) out = LocalTime.of(17, 0);

            double hours = Duration.between(in, out).toMinutes() / 60.0;
            // Deduct 1 hour for lunch break if full workday
            if (hours >= 8.0) {
                hours = Math.max(0, hours - 1.0);
            }
            cutoffHours.put(cutoff, cutoffHours.getOrDefault(cutoff, 0.0) + hours);
        }
        printPayrollReport(empNum, emp, cutoffHours, hourlyRate);
    }

    static void printPayrollReport(String empNum, String[] emp, Map<String, Double> cutoffHours, double hourlyRate) {
        // emp[1] = Last Name column in the CSV
        // emp[2] = First Name column in the CSV
        // emp[3] = Birthday column in the CSV
        System.out.println("\nEmployee #: " + empNum);
        System.out.println("Employee Name: " + emp[2] + " " + emp[1]);
        System.out.println("Birthday: " + emp[3]);

        double firstCutoffGross = 0;

        for (String cutoff : cutoffHours.keySet()) {
            double hours = cutoffHours.get(cutoff);
            double gross = hours * hourlyRate;
            boolean secondCutoff = cutoff.contains("/16-");

            System.out.println("\nCutoff Date: " + cutoff);
            System.out.println("Total Hours Worked: " + hours);
            System.out.println("Gross Salary: " + gross);

            if (!secondCutoff) {
                firstCutoffGross = gross;
                System.out.println("Net Salary: " + gross);
            } else {
                double monthlyGross = firstCutoffGross + gross;
                double sss = getSSSContribution(monthlyGross);
                double philHealth = getPhilHealthContribution(monthlyGross);
                double pagIbig = getPagIBIGContribution(monthlyGross);
                double totalDeductions = sss + philHealth + pagIbig;
                double withholdingTax = getWithholdingTax(monthlyGross, totalDeductions);
                totalDeductions += withholdingTax;

                System.out.println("Each Deduction:");
                System.out.println("SSS: " + sss);
                System.out.println("PhilHealth: " + philHealth);
                System.out.println("Pag-IBIG: " + pagIbig);
                System.out.println("Tax: " + withholdingTax);
                System.out.println("Total Deductions: " + totalDeductions);
                System.out.println("Net Salary: " + (gross - totalDeductions));
            }
        }
    }

    static LocalTime parseTime(String t) {
        String[] p = t.split(":");
        // p[0] = hour part of the time string
        // p[1] = minute part of the time string
        return LocalTime.of(Integer.parseInt(p[0].trim()), Integer.parseInt(p[1].trim()));
    }
}