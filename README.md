# MO-IT101-Group40
## **MotorPH Basic Payroll System**

**Team Details: Group 40**

Aquino, Micaleigh Gwyett
- Designed menu structures and output formatting
- Implemented PhilHealth and Pag-IBIG contribution calculations
- Conducted testing
- Wrote the README documentation

Dioso, Mary Joyce
- Implemented the login functionality
- Performed testing

King, Melanie
- Developed the main program flow, classes, variables, and resource handling
- Implemented hours validation and cutoff calculations
- Developed SSS contribution and tax computation
- Conducted testing
- Contributed to README documentation


 <br/>
---------------


# **Program Details**

## Overview
The **MS2MotorPH Basic Payroll System** is a Java-based payroll application designed to manage employee profiles, attendance records, and salary computations for a small to medium-sized company. It supports both **payroll staff** and **employee** login, providing tools for processing payroll and viewing employee profiles. The system automatically calculates statutory deductions (SSS, PhilHealth, Pag-IBIG) and withholding tax based on the employee's gross salary.

This project uses CSV files as data sources and demonstrates robust handling of employee data, attendance records, and payroll computations in a user-friendly console interface.

---

## Features

### For Payroll Staff
- Login as **payroll_staff** (default password: `12345`).
- Process payroll for:
  - **One employee** – enter employee number to compute salary.
  - **All employees** – compute payroll for the entire company.
- Automatic calculation of deductions:
  - SSS contribution based on a provided SSS table.
  - PhilHealth (3% of monthly gross, capped at PHP 1,800, shared equally by employer and employee).
  - Pag-IBIG contribution based on monthly gross (deducted only for the second cutoff).
  - Withholding tax based on the total taxable income.
- Supports bi-monthly cutoff payroll (1st cutoff: 1–15, 2nd cutoff: 16–end of month).

### For Employees
- Login as **employee** (default password: `12345`).
- View personal profile:
  - Employee number
  - Full name
  - Birthday

---

## Data Files

The system relies on three CSV files located in `src/main/resources`:

1. **Employee Data** – `MotorPH_EmployeeData-EmployeeDetails.csv`  
   Contains employee details, including name, birthdate, and hourly rate.

2. **Attendance Records** – `MotorPH_EmployeeData-AttendanceRecord.csv`  
   Contains employee attendance logs (date, time in, time out).

3. **SSS Contribution Table** – `Computation_SSSContributionN.csv`  
   Contains ranges of monthly gross salaries and corresponding SSS contributions.

**Note:** The CSV parser handles quoted fields and commas correctly.

---

## Payroll Calculation Flow

1. Attendance hours are validated:
   - Workday starts at **8:00 AM** (earlier clock-ins are ignored).
   - Workday ends at **5:00 PM** (later clock-outs are ignored).
2. Hours worked are accumulated per **cutoff period** (1–15 or 16–end of month).
3. **Gross salary** is calculated: hours worked × hourly rate.
4. Deductions for the second cutoff:
   - SSS
   - PhilHealth
   - Pag-IBIG
   - Withholding Tax
5. **Net salary** = Gross salary – Total deductions.
6. Payroll staff can view each cutoff and all deduction details for transparency.

---


 <br/>
---------------

## **Project Plan Link**
-https://docs.google.com/document/d/1EHxauxH1mZHPrnynFezEyRQUGUngM7wHlD564d7DZJQ/edit?usp=sharing
